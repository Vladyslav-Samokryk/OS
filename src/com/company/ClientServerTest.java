package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class ClientServerTest {

    private LinkedBlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private HashMap<String, Integer> uuidToSize = new HashMap<String, Integer>();

    private class StreamWriteTask implements Runnable {
        private ByteBuffer buffer;
        private SelectionKey key;
        private Selector selector;

        private StreamWriteTask(ByteBuffer buffer, SelectionKey key, Selector selector) {
            this.buffer = buffer;
            this.key = key;
            this.selector = selector;
        }

        @Override
        public void run() {
            SocketChannel sc = (SocketChannel) key.channel();
            byte[] data = (byte[]) key.attachment();
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            int results = 0;
            while (buffer.hasRemaining()) {
                try {
                    results = sc.write(buffer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (results == 0) {
                    buffer.compact();
                    buffer.flip();
                    data = new byte[buffer.remaining()];
                    buffer.get(data);
                    key.interestOps(SelectionKey.OP_WRITE);
                    key.attach(data);
                    selector.wakeup();
                    return;
                }
            }

            key.interestOps(SelectionKey.OP_READ);
            key.attach(null);
            selector.wakeup();
        }

    }

    private class StreamReadTask implements Runnable {
        private ByteBuffer buffer;
        private SelectionKey key;
        private Selector selector;

        private StreamReadTask(ByteBuffer buffer, SelectionKey key, Selector selector) {
            this.buffer = buffer;
            this.key = key;
            this.selector = selector;
        }

        private boolean checkUUID(byte[] data) {
            return uuidToSize.containsKey(new String(data));
        }

        @Override
        public void run() {
            SocketChannel sc = (SocketChannel) key.channel();
            buffer.clear();
            byte[] data = (byte[]) key.attachment();
            if (data != null) {
                buffer.put(data);
            }
            int count = 0;
            int readAttempts = 0;
            try {
                while ((count = sc.read(buffer)) > 0) {
                    readAttempts++;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (count == 0) {
                buffer.flip();
                data = new byte[buffer.limit()];
                buffer.get(data);
                if (checkUUID(data)) {
                    key.interestOps(SelectionKey.OP_READ);
                    key.attach(data);
                } else {
                    System.out.println("Clinet Read - uuid ~~~~ " + new String(data));
                    key.interestOps(SelectionKey.OP_WRITE);
                    key.attach(null);
                }
            }

            if (count == -1) {
                try {
                    sc.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            selector.wakeup();
        }

    }

    private class ClientWorker implements Runnable {

        @Override
        public void run() {
            try {
                Selector selector = Selector.open();
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(new InetSocketAddress("127.0.0.1", 9001));
                sc.register(selector, SelectionKey.OP_CONNECT);
                ByteBuffer buffer = ByteBuffer.allocateDirect(65535);

                while (selector.isOpen()) {
                    int count = selector.select(10);

                    if (count == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                    while (it.hasNext()) {
                        final SelectionKey key = it.next();
                        it.remove();
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isConnectable()) {
                            sc = (SocketChannel) key.channel();
                            if (!sc.finishConnect()) {
                                continue;
                            }
                            sc.register(selector, SelectionKey.OP_WRITE);
                        }

                        if (key.isReadable()) {
                            key.interestOps(0);
                            executor.execute(new StreamReadTask(buffer, key, selector));
                        }
                        if (key.isWritable()) {
                            key.interestOps(0);
                            if(key.attachment() == null){
                                key.attach(dataQueue.take());
                            }
                            executor.execute(new StreamWriteTask(buffer, key, selector));
                        }
                    }
                }
            } catch (IOException ex) {
                // Handle Exception
            }catch(InterruptedException ex){

            }

        }
    }




    /**
     * @param args
     */
    public static void main(String[] args) {
        ClientServerTest test = new ClientServerTest();
        for(;;){

        }
    }

}