

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.sound.sampled.SourceDataLine;


public class SocketClient {
    private static String functionName;
    private static Function<Integer, Object> function;
    private static int testCase = 0;

    public static void main(String[] args) {
        System.out.println("Client is running ...");
        if (!parseArgs(Arrays.asList(args))) {
            System.out.println("Invalid arguments.");
            System.exit(Constants.EXIT_CODE);
        }

        start();
    }

    private static boolean parseArgs(List<String> args) {
        if (args.size() != 4) {
            return false;
        } else {
            functionName = args.get(2);

            if (!parseFunction(functionName)) {
                return false;
            }

            testCase = Integer.parseInt(args.get(3));
            return testCase >= 0 && testCase <= 5;
        }
    }

    private static void start() {
        try {
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 7777);
            SocketChannel client = SocketChannel.open();
            client.connect(hostAddress);

            if (!client.isConnected()) {
                System.out.println("Can't connect to server.");
                System.exit(Constants.EXIT_CODE);
            }

            byte[] bytes;

            String message = Arrays.toString(new String[]{functionName, function.apply(testCase).toString()});
            bytes = message.getBytes();

            /* Send message to server */
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            client.write(buffer);
            buffer.clear();

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Boolean parseFunction(String arg) {
        switch (arg) {
            case Constants.FUNC_F:
                function = (Integer i) -> {
                    try {
                        return customFuncF(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            case Constants.FUNC_G:
                function = (Integer i) -> {
                    try {
                        return customFuncG(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            default:
                return false;
        }

        return true;
    }

    private static Integer customFuncF(int variant) throws InterruptedException {
        /* Returned value = (variant + 1) * 10 */
        switch (variant) {
            case 0:
                Thread.sleep(3000);
                return 10;
            case 1:
                Thread.sleep(6000);
                return 20;
            case 2:
                Thread.sleep(3000);
                return 0;
            case 3:
            case 5:
                Thread.sleep(6000);
                while (true){}
            case 4:
                Thread.sleep(3000);
                return 50;
        }

        return null;
    }

    private static Integer customFuncG(int variant) throws InterruptedException {
        /* Returned value = (variant + 1) * 100 */
        switch (variant) {
            case 0:
                Thread.sleep(6000);
                return 100;
            case 1:
                Thread.sleep(3000);
                return 200;
            case 2:
            case 4:
                while (true){}
            case 3:
                Thread.sleep(3000);
                return 0;
            case 5:
                Thread.sleep(3000);
                return 600;
        }
        return null;
    }
}
