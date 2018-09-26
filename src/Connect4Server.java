/**
 * This class contains code for a server implementation of a server that runs the Connect4 game
 * @author Garrett Pierce
 * @version 1.0 9/19/2018
 */

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.Date;

public class Connect4Server extends JFrame implements Connect4Constants {
    public static void main(String[] args) { Connect4Server frame = new Connect4Server(); }

    /**
     * Constructor that sets up a window for server logging. It also initializes the various streams and sockets needed.
     */
    public Connect4Server() {
        JTextArea jtaLog = new JTextArea();

        //Create a scroll pane to hold text area
        JScrollPane scrollPane = new JScrollPane(jtaLog);

        //Add scroll pane to frame
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("Connect4Server");
        setVisible(true);

        try {
            //Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            jtaLog.append(new Date() + ": Server started at socket 8000\n");

            //Number a session
            int sessionNo = 1;

            //Ready to create a session for every two players
            while (true) {
                jtaLog.append(new Date() + ": Wait for players to join session " + sessionNo + '\n');

                //Connect to player 1
                Socket player1 = serverSocket.accept();

                jtaLog.append(new Date() + ": Player 1 joined session " + sessionNo + '\n');
                jtaLog.append("Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');

                //Notify that the player is Player 1
                new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                //Connect to player 2
                Socket player2 = serverSocket.accept();

                jtaLog.append(new Date() + ": Player 2 joined session " + sessionNo + '\n');
                jtaLog.append("Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');

                //Notify that the player is player 2
                new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                //Display this session and increment session number
                jtaLog.append(new Date() + ": Start a thread for session " + sessionNo++ + '\n');

                //Create a new thread for this session of two players
                HandleASession task = new HandleASession(player1, player2);

                //Start the new thread
                new Thread(task).start();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}

/**
 * Contains the game logic
 */
class HandleASession implements Runnable, Connect4Constants {
    private Socket player1;
    private Socket player2;

    //Create and initialize board
    private char[][] level = makeBoardInit();

    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;

    //Continue to play
    private boolean continueToPlay = true;

    /** Construct a thread */
    public HandleASession(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Method to continually run the game
     * @throws ArrayIndexOutOfBoundsException
     */
    public void run() throws ArrayIndexOutOfBoundsException {
        try {
            //Create data input and output streams
            fromPlayer1 = new DataInputStream(player1.getInputStream());
            toPlayer1 = new DataOutputStream(player1.getOutputStream());
            fromPlayer2 = new DataInputStream(player2.getInputStream());
            toPlayer2 = new DataOutputStream(player2.getOutputStream());

            //Let player 1 know to start
            toPlayer1.writeInt(1);

            //Continuously serve players, determine state and report
            while (true) {
                //Receive move from player 1, add to board
                int col1 = fromPlayer1.readInt();
                //int col1 = makeMove(move1);
                int row1 = fromPlayer1.readInt();

                //toPlayer1.writeInt(nextOpen1);

                try {
                    level[row1][col1] = ICON_PX;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid entry.");
                }

                //Check if Player 1 wins
                if (winCheck(col1, ICON_PX)) {
                    toPlayer1.writeInt(PLAYER1_WON);
                    toPlayer2.writeInt(PLAYER1_WON);
                    sendMove(toPlayer2, row1, col1);
                    break;
                } else {
                    //Notify player 2 to take turn
                    toPlayer2.writeInt(CONTINUE);

                    //Send player 1's selected row and column to player 2
                    sendMove(toPlayer2, row1, col1);
                }

                //Receive a move from Player 2, add to board
                int col2 = fromPlayer2.readInt();
                //int col2 = makeMove(move2);
                int row2 = fromPlayer2.readInt();

                try {
                    level[row2][col2] = ICON_PO;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid entry.");
                }

                //Check if player 2 wins
                if (winCheck(col2, ICON_PO)) {
                    toPlayer1.writeInt(PLAYER2_WON);
                    toPlayer2.writeInt(PLAYER2_WON);
                    sendMove(toPlayer1, row2, col2);
                    break;
                } else {
                    //Notify player 1 to take turn
                    toPlayer1.writeInt(CONTINUE);

                    //Send player2's move to player 1
                    sendMove(toPlayer1, row2, col2);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Initializes the two-dimensional char array that represents the board
     * @return char[][] representing the level
     */
    public char[][] makeBoardInit() {
        char level[][] = {
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'},
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'},
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'},
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'},
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'},
                {'|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|', ' ', '|'}
        };
        return level;
    }

    /**
     * Method to compute the player's selected column
     * @param move column selected by the user
     * @return int representing the column selected by the user usable by the board array
     */
    public int makeMove(int move) {
        int moveUse = 0;
        switch (move) {
            case 1:
                moveUse = 1;
                break;
            case 2:
                moveUse = 3;
                break;
            case 3:
                moveUse = 5;
                break;
            case 4:
                moveUse = 7;
                break;
            case 5:
                moveUse = 9;
                break;
            case 6:
                moveUse = 11;
                break;
            case 7:
                moveUse = 13;
                break;
            default:
                moveUse = 0;
                break;
        }
        return moveUse;
    }

    /**
     * Finds the highest spot not occupied by a player icon
     * @param col Column from user selection
     * @return int representing highest spot not occupied by a player icon
     */
    public int nextOpen(int col) throws ArrayIndexOutOfBoundsException {
        int y = 5;
        boolean found = false;
        while (!found) {
            Character c = level[y][col];
            if (c.equals(ICON_BLANK)) {
                found = true;
            } else {
                y--;
            }

            if (y < 0) {
                throw new ArrayIndexOutOfBoundsException("Row is full. Try again.");
            }
        }

        return y;
    }

    /**
     * Checks to see if game has reached win state
     * @param col column from user selection
     * @return boolean indicating win state
     */
    public boolean winCheck(int col, char check) {
        char APC = check;
        int count = 0;

        //Horizontal Check
        int hcount = 0;
        for (int j = 0; j < BOARD_HEIGHT; j++) {
            for (int i = 1; i < BOARD_WIDTH; i += 2) {
                if (level[j][i] == APC) {
                    hcount++;
                } else {
                    hcount = 0;
                }

                if (hcount >= 4) {
                    return true;
                }
            }
        }

        //Vertical Check
        int vcount = 0;
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            if (level[i][col] == APC) {
                vcount++;
            } else {
                vcount = 0;
            }

            if (vcount >= 4) {
                return true;
            }
        }

        //Descending Diagonal Check
        int dcount1 = 0;
        for (int i = 0; i < BOARD_HEIGHT - 2; i++) {
            dcount1 = 0;
            int rows, cols;
            for (rows = i, cols = 1; rows < BOARD_HEIGHT && cols < BOARD_WIDTH; rows++, cols+=2) {
                if (level[rows][cols] == APC) {
                    dcount1++;
                    if (dcount1 >= 4) {
                        return true;
                    }
                } else {
                    dcount1 = 0;
                }
            }
        }

        for (int i = 1; i < BOARD_WIDTH - 4; i++) {
            dcount1 = 0;
            int rows, cols;
            for (rows = 0, cols = 1; rows < BOARD_HEIGHT && cols < BOARD_WIDTH; rows++, cols+=2) {
                if (level[rows][cols] == APC) {
                    dcount1++;
                    if (dcount1 >= 4) {
                        return true;
                    }
                } else {
                    dcount1 = 0;
                }
            }
        }

        return false;
    }

    /**
     * Method for sending move to a specified user
     * @param out DataOutputStream for desired user
     * @param row int representing the highest available position on the board
     * @param column int representing the player's column choice
     * @throws IOException if there's a problem with IO
     */
    public void sendMove(DataOutputStream out, int row, int column) throws IOException {
        out.writeInt(row);
        out.writeInt(column);
    }

}
