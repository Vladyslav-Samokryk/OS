import com.constants.Constants;
import com.sockets.SocketServer;
import java.io.IOException;
import java.util.HashMap;

//import sun.misc.Signal;


public class MainManager {
    private final SocketServer socketServer = new SocketServer("localhost", 7777);
    private final Demonstration demo = new Demonstration();
    private Integer variant = null;


    public void interactiveMenu() {
        while (true) {
            this.demo.showVariants();
            this.variant = this.demo.inputVariant();

            if (this.variant != null) {
                this.socketServer.setVariant(this.variant);
                break;
            }

            System.out.println("Provided incorrect number of variant, try again!\n");
        }
    }

    public void onExit() {
        System.out.println("\nExited by user ...");

        HashMap<String, String> results = this.socketServer.getResults();

        if (results.get(Constants.FUNC_F) == null) {
            System.out.println(Constants.FUNC_F + " has not been computed ...");
        }

        if (results.get(Constants.FUNC_G) == null) {
            System.out.println(Constants.FUNC_G + " has not been computed ...");
        }

        System.exit(Constants.EXIT_CODE);
    }

    public void start() throws IOException {
//        Signal.handle(new Signal("INT"), signal -> {
//            this.onExit();
//        } );
        this.socketServer.startServer();
        System.out.println("Result of computations is: " + this.socketServer.getMultiplication());
    }
}
