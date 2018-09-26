/**
 * Defines methods to perform game logic tasks, and main method to run the game
 * @author Garrett Pierce
 * @version 3.0 9/9/2018
 */

import javafx.application.Application;

import javax.swing.*;
import java.util.Scanner;

public class Connect4 {
    private static final char ICON_WALL = '|';
    private static final char ICON_BLANK = ' ';
    private static final char ICON_PX = 'X';
    private static final char ICON_PO = 'O';
    private static int activePlayer;
    private static char[][] level;
    private static final char BOARD_WIDTH = 15;
    private static final char BOARD_HEIGHT = 6;
    static Scanner scan = new Scanner(System.in);

    /**
     * Used to establish a Connect4TextConsole object and run the game.
     * @param args
     */
    public static void main(String[] args) {
        Connect4TextConsole cons = new Connect4TextConsole();
        level = cons.makeBoardInit();
        cons.display(level);
        System.out.println("Begin game.");
        activePlayer = 0;
        int online = selectOnline();

        if (online == 0) {
            Connect4Server c4s = new Connect4Server();
            Connect4Client p1 = new Connect4Client();
            Connect4Client p2 = new Connect4Client();
        } else if (online == 1) {
            int ui = UISelect();
            if (ui == 0) {
                int opponent = opponentSelect();
                if (opponent == 0) {
                    runPlayer(cons);
                } else if (opponent == 1) {
                    runComp(cons);
                }
            } else if (ui == 1) {
                Application.launch(Connect4GUI.class, args);
            }
        }


    }


    /**
     * Continuously loops until win state is reached
     * @param cons Connect4TextConsole object passed in to use display method
     */
    public static void runPlayer(Connect4TextConsole cons) {
        boolean done = false;

        while (!done) {
            int col = makeMove();
            cons.display(level);
            done = winCheck(col);
            if(done) {
                char APC = charSet();
                System.out.println("Player " + APC + " won the game.");
            }
            if (col != -1) {
                switchPlayer();
            }
        }
    }

    /**
     * Separate method for running game against computer
     * @param cons Connect4TextConsole object passed in to use display method
     */
    public static void runComp(Connect4TextConsole cons) {
        boolean done = false;
        Connect4ComputerPlayer comp = new Connect4ComputerPlayer();
        int lastCol = 0;
        int col = 0;
        System.out.println("Your player is represented by \'O\'.");

        while (!done) {
            if (activePlayer == 0) {
                col = makeMovePlayer();
                lastCol = col;
            } else if (activePlayer == 1) {
                col = makeMoveComp(lastCol, comp);
            }
            cons.display(level);
            done = winCheck(col);
            if (done) {
                char APC = charSet();
                System.out.println("Player " + APC + " won the game.");
            }
            if (col != -1) {
                switchPlayer();
            }
        }
    }

    /**
     * Scans for user input and, if valid, inserts the player's icon in selected column
     * @return int representing column selected by the user
     */
    public static int makeMove() throws ArrayIndexOutOfBoundsException {
        int column = 0;
        char activePlayerChar = charSet();
        System.out.println("Player" + activePlayerChar + " - your turn. Choose a column number from 1-7.");

        String s = scan.next();
        int stringIn = Integer.parseInt(s);

        switch (stringIn) {
            case 1:
                column = 1;
                break;
            case 2:
                column = 3;
                break;
            case 3:
                column = 5;
                break;
            case 4:
                column = 7;
                break;
            case 5:
                column = 9;
                break;
            case 6:
                column = 11;
                break;
            case 7:
                column = 13;
                break;
        }

        int y = nextOpen(column);

        try {
            level[y][column] = activePlayerChar;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid entry. Starting turn over.");
            column = -1;
        }

        return column;
    }

    /**
     * Method for player to make move in computer opponent case
     * @return int representing column selected by the user
     */
    public static int makeMovePlayer() throws ArrayIndexOutOfBoundsException {
        int column = 0;
        char activePlayerChar = ICON_PO;
        System.out.println("It is your turn. Choose a column number from 1-7.");

        String s = scan.next();
        int stringIn = Integer.parseInt(s);

        switch (stringIn) {
            case 1:
                column = 1;
                break;
            case 2:
                column = 3;
                break;
            case 3:
                column = 5;
                break;
            case 4:
                column = 7;
                break;
            case 5:
                column = 9;
                break;
            case 6:
                column = 11;
                break;
            case 7:
                column = 13;
                break;
        }

        int y = nextOpen(column);
        if (y == -1) {
            System.out.println("Invalid move.");
            return column;
        }

        try {
            level[y][column] = activePlayerChar;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid entry. Starting turn over.");
            column = -1;
        }

        return column;
    }

    /**
     * Method to add computer's move to the level
     * @param comp Connect4TextConsole object passed in to use display method
     * @param lastCol player's previous column choice, used to play defense if decided
     * @return int representing column selected by the computer
     */
    public static int makeMoveComp(int lastCol, Connect4ComputerPlayer comp) {
        System.out.println();
        System.out.println("Computer's turn, please wait.");
        System.out.println();
        int move = comp.compMakeMove(lastCol);
        int y = nextOpen(move);
        if (y == -1) {
            System.out.println("Invalid move.");
            return move;
        }
        level[y][move] = ICON_PX;

        return move;
    }

    /**
     * Finds the highest spot not occupied by a player icon
     * @param col Column from user selection
     * @return int representing highest spot not occupied by a player icon
     */
    public static int nextOpen(int col) {
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
                System.out.println("Row is full. Try again.");
                return -1;
            }
        }

        return y;
    }

    /**
     * Checks to see if game has reached win state
     * @param col column from user selection
     * @return boolean indicating win state
     */
    public static boolean winCheck(int col) {
        char APC = charSet();
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
     * Helper method to switch active player
     */
    public static void switchPlayer() {
        if (activePlayer == 0) {
            activePlayer = 1;
        } else if (activePlayer == 1) {
            activePlayer = 0;
        } else {
            System.out.println("Player error."); //Should never reach this line.
        }
    }

    /**
     * Helper method to provide char for filling board with active player's icon when needed
     * @return char representing active player's icon
     */
    public static char charSet() {
        char activePlayerChar = ' ';
        if (activePlayer == 0) {
            activePlayerChar = ICON_PO;
        } else if (activePlayer == 1) {
            activePlayerChar = ICON_PX;
        } else {
            System.out.println("Player error.");
        }
        return activePlayerChar;
    }

    /**
     * Method for deciding if the player wants to play against the computer or against another player
     * @return int representing selected opponent
     */
    public static int opponentSelect() throws IllegalArgumentException {
        System.out.println("Enter \'P\' if you want to play against another player. Enter \'C\' to play against computer.");
        String s = scan.next();
        int decision = 0;
        boolean decided = false;

        while (!decided) {
            if (s.equals("P")) {
                decision = 0;
                decided = true;
            } else if (s.equals("C")) {
                decision = 1;
                decided = true;
            } else {
                decision = -1;
                throw new IllegalArgumentException("Entry must be \'P\' or \'C\'.");
            }
        }

        return decision;
    }

    /**
     * Method for parsing player's response to UI choice
     * @return int representing player choice in UI
     * @throws IllegalArgumentException
     */
    public static int UISelect() throws IllegalArgumentException {
        System.out.println("Enter \'C\' if you want to play in the console. Enter \'G\' to play in the GUI.");
        String s = scan.next();
        int decision = 0;
        boolean decided = false;

        while (!decided) {
            if (s.equals("C")) {
                decision = 0;
                decided = true;
            } else if (s.equals("G")) {
                decision = 1;
                decided = true;
            } else {
                decision = -1;
                throw new IllegalArgumentException("Entry must be \'C\' or \'G\'.");
            }
        }
        return decision;
    }

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
