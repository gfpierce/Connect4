/**
 * This class contains code for the client of a Connect4 game
 * @author Garrett Pierce
 * @version 1.0 9/19/2018
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Connect4Client extends JApplet implements Runnable, Connect4Constants {
    static Scanner scan = new Scanner(System.in);

    //Indicate whether play has the turn
    private boolean myTurn = false;

    //Indicate the token for player
    private char myToken = ' ';

    //Indicate the token for the other player
    private char otherToken = ' ';

    //Create and initialize cells
    //private Cell<Character>[][] level = makeBoardInit();
    private Cell[][] level = new Cell[BOARD_HEIGHT][BOARD_WIDTH];

    //Create and initialize a title label
    private JLabel jlblTitle = new JLabel();

    //Create and initialize a status label
    private JLabel jlblStatus = new JLabel();

    //Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    //Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    //Continue to play?
    private boolean continueToPlay = true;

    //Wait for the player to mark a cell
    private boolean waiting = true;

    //Indicate if it runs as application
    private boolean isStandAlone = false;

    //Host name or IP
    private String host = "localhost";

    /**
     * Initializes the UI and connects to the server
     */
    public void init() {
        //Panel p to hold board
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(BOARD_HEIGHT, BOARD_WIDTH, 0, 0));

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                p.add(level[i][j] = new Cell(i, j));
            }
        }

        //Set properties for labels and borders for labels and panel
        p.setBorder(new LineBorder(Color.black, 1));
        jlblTitle.setHorizontalAlignment(JLabel.CENTER);
        jlblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        jlblTitle.setBorder(new LineBorder(Color.black, 1));
        jlblStatus.setBorder(new LineBorder(Color.black, 1));

        //Place the panel and the labels to the applet
        add(jlblTitle, BorderLayout.NORTH);
        add(p, BorderLayout.CENTER);
        add(jlblStatus, BorderLayout.SOUTH);

        //Connect to the server
        connectToServer();
    }

    /**
     * Connects to the server
     */
    private void connectToServer() {
        try {
            //Create a socket to connect to the server
            Socket socket;
            if (isStandAlone) {
                socket = new Socket(host, 8000);
                //System.out.println("Success in if.");
            } else {
                socket = new Socket(getCodeBase().getHost(), 8000);
                //System.out.println("Success in else.");
            }

            //Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            //Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            System.err.println(ex);
        }

        //Control the game on a separate thread
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Processes server responses and sets up the game.
     */
    public void run() {
        try {
            //Get notification from the server
            int player = fromServer.readInt();

            //Am I player 1 or 2?
            if (player == PLAYER1) {
                myToken = ICON_PX;
                otherToken = ICON_PO;
                jlblTitle.setText("Player 1 with token 'X'");
                jlblStatus.setText("Waiting for player 2 to join");

                //Receive startup notification from the server
                fromServer.readInt();

                //The other player has joined
                jlblStatus.setText("Player 2 has joined. I start first");

                myTurn = true;
            } else if (player == PLAYER2) {
                myToken = ICON_PO;
                otherToken = ICON_PX;
                jlblTitle.setText("Player 2 with token 'O'");
                jlblStatus.setText("Waiting for player 1 to move");
            }

            //Continue to play
            while (continueToPlay) {
                if (player == PLAYER1) {
                    waitForPlayerAction();
                    sendMove();
                    receiveInfoFromServer();
                } else if (player == PLAYER2) {
                    receiveInfoFromServer();
                    waitForPlayerAction();
                    sendMove();
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Sleeps until player action fires
     * @throws InterruptedException
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * Sends move to the server
     * @throws IOException
     */
    private void sendMove() throws IOException {
        toServer.writeInt(columnSelected);
        toServer.writeInt(rowSelected);
    }

    /**
     * Receives information from the server, processes it, displays what the user needs.
     * @throws IOException
     */
    private void receiveInfoFromServer() throws IOException {
        //Receive game status
        int status = fromServer.readInt();

        if (status == PLAYER1_WON) {
            //Player 1 won, stop playing
            continueToPlay = false;
            if (myToken == ICON_PX) {
                jlblStatus.setText("I won! (X)");
            } else if (myToken == ICON_PO) {
                jlblStatus.setText("Player 1 (X) has won!");
                receiveMove();
            }
        } else if (status == PLAYER2_WON) {
            //Player 2 won, stop playing
            continueToPlay = false;
            if (myToken == ICON_PO) {
                jlblStatus.setText("I won! (O)");
            } else if (myToken == ICON_PX) {
                jlblStatus.setText("Player 2 (O) has won!");
                receiveMove();
            }
        } else if (status == DRAW) {
            // No winner, game is over
            // I don't think the game ever reaches this state as programmed currently
            continueToPlay = false;
            jlblStatus.setText("Game is over, no winner!");

            if (myToken == ICON_PO) {
                receiveMove();
            }
        } else {
            receiveMove();
            jlblStatus.setText("My turn");
            myTurn = true;
        }
    }

    /**
     * Receive other player's move from the server and add it to the board
     * @throws IOException
     */
    private void receiveMove() throws  IOException {
        int row = fromServer.readInt();
        int column = fromServer.readInt();
        level[row][column].setToken(otherToken);
    }

    /**
     * Class that aids in drawing the board for the UI
     */
    public class Cell extends JPanel {
        //Indicate the row and column of this cell in board
        private int row;
        private int column;

        //Token used for this cell
        private char token = ' ';

        /**
         * Constructor that takes sets up the board. Best used in a for loop.
         * @param row int representing the row on the board where this cell is
         * @param column int representing the column on the board where this cell is
         */
        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
            if (column % 2 == 0) {
                setBackground(Color.black);
            }

            if (column % 2 == 1) {
                addMouseListener(new ClickListener());
            }
            setBorder(new LineBorder(Color.black, 1));

        }

        /**
         * Method that returns the current token of the cell
         * @return char representing the current token in the cell
         */
        public char getToken() { return token; }

        /**
         * Method that changes the token in the cell
         * @param c
         */
        public void setToken(char c) {
            token = c;
            repaint();
        }

        /**
         * Method that draws the necessary pieces for the token
         * @param g Graphics object
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (token == ICON_PX) {
                g.drawLine(10, 10, getWidth() - 10, getHeight() - 10);
                g.drawLine(getWidth() - 10, 10, 10, getHeight() - 10);
            } else if (token == ICON_PO) {
                g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
            }
        }

        /**
         * ClickListener to handle mouse click events
         */
        private class ClickListener extends MouseAdapter {
            public void mouseClicked(MouseEvent e) {
                //If cell is not occupied and the player has the turn
                if (myTurn) {
                    myTurn = false;
                    columnSelected = column;
                    rowSelected = nextOpen(column);
                    level[rowSelected][columnSelected].setToken(myToken);
                    System.out.println(token);
                    jlblStatus.setText("Waiting for the other player to move");
                    waiting = false;
                }
            }
        }

        /**
         * Method that determines the next open row to draw on the board
         * @param col int representing player's choice of column
         * @return int representing the lowest open row for the specified column
         * @throws ArrayIndexOutOfBoundsException
         */
        private int nextOpen(int col) throws ArrayIndexOutOfBoundsException {
            int y = row;

            boolean found = false;
            while (!found) {
                char c = level[y][col].token;
                if (c == ICON_BLANK) {
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
    }

    public static void main(String[] args) {
        Connect4Client frame = new Connect4Client();
    }

    /**
     * Select whether the player wants to play online or locally
     * @return int representing the player's choice
     * @throws IllegalArgumentException
     */
    public static int selectOnline() throws IllegalArgumentException {
        System.out.println("Enter \'O\' to play online. Enter \'F\' to play offline");

        String s = scan.next();
        int decision = 0;
        boolean decided = false;

        while (!decided) {
            if (s.equals("O")) {
                decision = 0;
                decided = true;
            } else if (s.equals("F")) {
                decision = 1;
                decided = true;
            } else {
                decision = -1;
                throw new IllegalArgumentException("Entry must be \'C\' or \'G\'.");
            }
        }
        return decision;
    }
}

