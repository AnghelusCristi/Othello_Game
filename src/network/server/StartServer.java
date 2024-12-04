package network.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A main class used to start a GameServer.
 */
public class StartServer {

    /**
     * Checks if the port is available in the localhost.
     * @param port the port to be checked.
     * @return true if the port is free, false otherwise.
     */
    private static boolean freePort(int port) {
        try (Socket s = new Socket("localhost", port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean validPort = false;
        int port = -1;
        //ask for a port until a valid free port is provided by the user
        while (!validPort) {
            System.out.print("Please provide a port number (leave blank for 44444):");
            String number = sc.nextLine();
            if (number.isEmpty()) {
                port = 44444; //default port number
                validPort = true;
            } else {
                try {
                    port = Integer.parseInt(number);
                    validPort = true;
                    if (port <= 0 || port > 65536) {
                        System.out.println("That port is not valid. Try another port.");
                        validPort = false;
                    } else if (!freePort(port)) {
                        System.out.println("That port is already used. Try another port.");
                        validPort = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("That port is not an integer.");
                }
            }
        }

        GameServer server = new GameServer(port);
        server.start();
        System.out.println("Server started at port " + port + "\n");
        System.out.println("Type 'quit' to close the server.\n");

        while (true) {
            //if the user types quit, it closes the server
            if (sc.nextLine().equals("quit")) {
                server.stop();
                System.out.println("The server is closed!!!");
                System.exit(-1);
                break;
            }
        }
    }
}
