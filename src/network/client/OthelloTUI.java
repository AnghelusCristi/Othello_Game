package network.client;

import exceptions.FailedConnection;
import exceptions.InvalidUsername;

import java.util.Scanner;

/**
 * The main Othello game class. It implements the GameListener interface.
 * Acts mainly as a TUI for the user and offers
 * the possibility to connect to the server as a client.
 */
public class OthelloTUI implements GameListener {
    private static Client client;

    /**
     * Returns the help menu with all the available commands.
     * @return the menu with all the available commands.
     */
    private static String menu() {
        return  "List of available commands: \n"
                + "-list                           - List of connected users in the server separated by ~.\n"
                + "-queue                          - Adds you to the queue to play a game. If used again, removes you from the queue. \n"
                + "-move A/a                       - Make your move in the game: A/a - letter where you want to place your mark.\n"
                + "                                  You will see all available moves represented as letters on the board. Make a move with one of the letters. \n"
                + "                                  You can use both lowercase and uppercase letters to make your move. \n"
                + "-ai strategy                    - Enter before joining a game to enable an AI which will automatically play games for you. Enter simply 'ai' to disable it.\n"
                + "                                  The strategy parameter can be 0, 1, 2, 3, 4 :\n"
                + "                                  0: NaiveStrategy - This strategy chooses a random legal move.\n"
                + "                                  1: LimitingStrategy - This strategy is based on the premise of giving your opponent the least possible moves, since this may cause you to get more moves.\n"
                + "                                     Statistic: 66% win against Naive\n"
                + "                                  2: FieldValueStrategy - This strategy is based on the positional values from the publication https://repub.eur.nl/pub/7142.\n"
                + "                                     It chooses the field based on the highest value possible, if there are multiple of the same value, it chooses a random one.\n"
                + "                                     Statistic: 80% win against Naive\n"
                + "                                  3: StackStrategy - A combination of strategies 2 and 1.\n"
                + "                                     Stackable strategies are strategies that can be used one after the other. This is created by a StackStrategy.\n"
                + "                                     The premise of this stacking is that a strategy can have multiple \"best fields\" For example, a strategy limiting the \n"
                + "                                     other players turns can have 2 move which cause the next player to have 0 moves. These 2 moves are then passed on\n"
                + "                                     to the next Stackable strategy in the stack, which then filters based on its strategy.\n"
                + "                                  4: MiniMaxStrategy - The minimax strategy looks at n turns in the future to determine what is the best move, assuming the opponent has perfect play.\n"
                + "                                     A second parameter n is optional to determine the depth, else it will use depth 5. This has effect on the calculation time.\n"
                + "-hint                           - Gives you a legal move suggested by the FieldValueStrategy AI.\n"
                + "                                  Can be used only when it's your turn in a game.\n"
                + "-help                           - Print the help menu.\n"
                + "-exit                           - Exit the program.\n";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean connection = false;
        while (!connection) { //repeatedly ask for address and port until a connection can be established.
            System.out.print("Please provide an IP address or hostname (leave blank for localhost):");
            String address = sc.nextLine();
            if (address.equals("")) {
                address = "localhost"; //default address is localhost
            }
            boolean validPort = false;
            int port = -1;
            while (!validPort) {
                System.out.print("Please provide a port number (leave blank for 44444):");
                String number = sc.nextLine();
                if (number.equals("")) {
                    port = 44444; //default port is 44444
                    validPort = true;
                } else {
                    try {
                        port = Integer.parseInt(number);
                        validPort = true;
                        if (port <= 0 || port > 65536) {
                            System.out.println("That port is not valid.");
                            validPort = false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("That port is not an integer.");
                    }
                }
            }

            client = new Client(); // create a new client for the user

            try {
                client.connect(address, port);
                connection = true;
            } catch (FailedConnection e) {
                System.out.println(e.getMessage() + "\n");
            }
        }

        client.addGameListener(new OthelloTUI());

        while (!client.isLogged()) {
            System.out.print("Please provide a username: ");
            String username = sc.nextLine();
            try {
                client.sendUsername(username);
            } catch (InvalidUsername e) {
                System.out.println(e.getMessage());
            }
            // wait a little time to receive the message from server
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        //Print the welcome message and the help menu
        String welcome = "-".repeat(80) + "Welcome to the Othello Game Server!!!" + "-".repeat(80);
        System.out.println(welcome);
        System.out.println(menu());
        System.out.println("-".repeat(197));

        String line;
        System.out.print("Command: ");
        while (true) {
            line = sc.nextLine();
            if (line.equals("help")) {
                System.out.println(menu());
            } else {
                client.handleMessages(line);
            }
            // wait a little time to receive the message from server, then type Command: prompt
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            System.out.print("Command: ");
        }
    }

    @Override
    public void messageReceived(String message) {
        System.out.println(message);
        if (message.contains("GAMEOVER") && message.contains("disconnected")) {
            System.out.print("Command: ");
        }
    }
}
