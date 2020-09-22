package com.company;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class Runnable {
    int bufferSize = 8192;

    int port;

    String host;

    static class Attachment {

        ByteBuffer in;

        ByteBuffer out;

        SelectionKey peer;

    }

    static final byte[] OK = new byte[]{0x00, 0x5a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public void run() {
        try {

            Selector selector = SelectorProvider.provider().openSelector();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();

            serverChannel.configureBlocking(false);

            serverChannel.socket().bind(new InetSocketAddress(host, port));

            serverChannel.register(selector, serverChannel.validOps());

            while (selector.select() > -1) {

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid()) {

                        try {
                            if (key.isAcceptable()) {

                                accept(key);
                            } else if (key.isConnectable()) {

                                connect(key);
                            } else if (key.isReadable()) {

                                read(key);
                            } else if (key.isWritable()) {

                                write(key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            close(key);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }


    private void accept(SelectionKey key) throws IOException, ClosedChannelException {

        SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();

        newChannel.configureBlocking(false);

        newChannel.register(key.selector(), SelectionKey.OP_READ);
    }


    private void read(SelectionKey key) throws IOException, UnknownHostException, ClosedChannelException {
        SocketChannel channel = ((SocketChannel) key.channel());
        Attachment attachment = ((Attachment) key.attachment());
        if (attachment == null) {

            key.attach(attachment = new Attachment());
            attachment.in = ByteBuffer.allocate(bufferSize);
        }
        if (channel.read(attachment.in) < 1) {

            close(key);
        } else if (attachment.peer == null) {

            readHeader(key, attachment);
        } else {

            attachment.peer.interestOps(attachment.peer.interestOps() | SelectionKey.OP_WRITE);

            key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);

            attachment.in.flip();
        }
    }

    private void readHeader(SelectionKey key, Attachment attachment) throws IllegalStateException, IOException,
            UnknownHostException, ClosedChannelException {
        byte[] ar = attachment.in.array();
        if (ar[attachment.in.position() - 1] == 0) {

            if (ar[0] != 4 && ar[1] != 1 || attachment.in.position() < 8) {

                throw new IllegalStateException("Bad Request");
            } else {

                SocketChannel peer = SocketChannel.open();
                peer.configureBlocking(false);

                byte[] addr = new byte[]{ar[4], ar[5], ar[6], ar[7]};
                int p = (((0xFF & ar[2]) << 8) + (0xFF & ar[3]));

                peer.connect(new InetSocketAddress(InetAddress.getByAddress(addr), p));

                SelectionKey peerKey = peer.register(key.selector(), SelectionKey.OP_CONNECT);

                key.interestOps(0);

                attachment.peer = peerKey;
                Attachment peerAttachemtn = new Attachment();
                peerAttachemtn.peer = key;
                peerKey.attach(peerAttachemtn);

                attachment.in.clear();
            }
        }
    }


    private void write(SelectionKey key) throws IOException {

        SocketChannel channel = ((SocketChannel) key.channel());
        Attachment attachment = ((Attachment) key.attachment());
        if (channel.write(attachment.out) == -1) {
            close(key);
        } else if (attachment.out.remaining() == 0) {
            if (attachment.peer == null) {

                close(key);
            } else {

                attachment.out.clear();

                attachment.peer.interestOps(attachment.peer.interestOps() | SelectionKey.OP_READ);

                key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            }
        }
    }


    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = ((SocketChannel) key.channel());
        Attachment attachment = ((Attachment) key.attachment());

        channel.finishConnect();

        attachment.in = ByteBuffer.allocate(bufferSize);
        attachment.in.put(OK).flip();
        attachment.out = ((Attachment) attachment.peer.attachment()).in;
        ((Attachment) attachment.peer.attachment()).out = attachment.in;

        attachment.peer.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        key.interestOps(0);
    }

    private void close(SelectionKey key) throws IOException {
        key.cancel();
        key.channel().close();
        SelectionKey peerKey = ((Attachment) key.attachment()).peer;
        if (peerKey != null) {
            ((Attachment) peerKey.attachment()).peer = null;
            if ((peerKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
                ((Attachment) peerKey.attachment()).out.flip();
            }
            peerKey.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public static void main(String[] args) {
        Runnable server = new Runnable();
        server.host = "127.0.0.1";
        server.port = 1080;
        server.run();
    }

}
