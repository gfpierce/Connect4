/**
 * Draws GUI for Connect 4 game, and implements game logic involving the GUI.
 * @author Garrett Pierce
 * @version 1.0 9/9/2018
 */

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Group;
import javafx.scene.text.*;
import java.util.Scanner;

public class Connect4GUI extends Application {
    private char[][] level;
    Scanner scan = new Scanner(System.in);
    Text tBoard = new Text();
    TextField moveField = new TextField();
    Label moveLabel = new Label();
    Label activePlayerLabel = new Label();
    TextField opponentSel = new TextField();
    Label opponentLabel = new Label("Enter \'P\' if you want to play against another player. Enter \'C\' to play against computer.");
    Group root = new Group();
    ObservableList list = root.getChildren();


    private int activePlayer = 0;
    private final char ICON_PO = 'O';
    private final char ICON_PX = 'X';
    private final char ICON_WALL = '|';
    private final char ICON_BLANK = ' ';
    private final char BOARD_WIDTH = 15;
    private final char BOARD_HEIGHT = 6;

    /**
     * Function that launches and initializes the application
     * @param primaryStage stage to be worked upon
     */
    @Override
    public void start(Stage primaryStage) {
        level = makeBoardInit();
        String sBoard = convertBoard(level);
        FlowPane pane = new FlowPane();

        // Place initial board on pane
        tBoard.setText(sBoard);
        tBoard.setX(277);
        tBoard.setY(100);

        opponentSel.setLayoutX(250);
        opponentSel.setLayoutY(225);
        OpponentHandler opponentHandler = new OpponentHandler();
        opponentSel.setOnAction(opponentHandler);

        opponentLabel.setLayoutY(200);
        opponentLabel.setLayoutX(75);
        list.add(opponentLabel);
        list.add(opponentSel);

        // Create a scene and place it in the stage
        Scene scene = new Scene(root,600,300);
        primaryStage.setTitle("Connect4GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handler for Opponent Selection text field
     */
    class OpponentHandler implements EventHandler<ActionEvent>  {
        @Override
        public void handle(ActionEvent event) throws IllegalArgumentException {
            if ((opponentSel.getText() != null && !opponentSel.getText().isEmpty())) {
                String s = opponentSel.getText();
                int opponent = opponentSelect(s);
                if (opponent == 0) {
                    initPlayer();
                } else if (opponent == 1) {
                    initComp();
                } else {
                    throw new IllegalArgumentException("Entry must be \'P\' or \'C\'.");
                }
            } else {
                Label emptyLabel = new Label("You have not entered a move.");
                emptyLabel.setLayoutX(250);
                emptyLabel.setLayoutY(275);
            }
        }
    }

    /**
     * Handler for playing against another player
     */
    class MoveHandlerPlayer implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if ((moveField.getText() != null && !moveField.getText().isEmpty())) {
                int input = Integer.parseInt(moveField.getText());
                moveField.clear();
                int col = makeMove(input);
                tBoard.setText(convertBoard(level));
                boolean done = winCheck(col);

                if (done) {
                    char APC = charSet();
                    list.remove(moveField);
                    list.remove(moveLabel);
                    activePlayerLabel.setText("Player" + APC + " is the winner.");
                    activePlayerLabel.setLayoutX(250);
                    activePlayerLabel.setLayoutY(225);
                    list.add(activePlayerLabel);
                }
                if (col != -1){
                    switchPlayer();
                    char APC = charSet();
                    moveLabel.setText("Enter Move, Player" + APC);
                }
            } else {
                Label emptyLabel = new Label("You have not entered a move.");
                emptyLabel.setLayoutX(250);
                emptyLabel.setLayoutY(275);
            }
        }
    }

    /**
     * Handler for playing against computer
     */
    class MoveHandlerComp implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if ((moveField.getText() != null && !moveField.getText().isEmpty())) {
                Connect4ComputerPlayer comp = new Connect4ComputerPlayer();
                int input = Integer.parseInt(moveField.getText());
                int col = 0;
                int lastCol = 0;
                moveField.clear();
                boolean done = false;


                col = makeMove(input);
                lastCol = col;
                done = winCheck(col);
                tBoard.setText(convertBoard(level));
                if (done) {
                    char APC = charSet();
                    list.remove(moveField);
                    list.remove(moveLabel);
                    activePlayerLabel.setText("Player" + ICON_PO + " is the winner.");
                    activePlayerLabel.setLayoutX(250);
                    activePlayerLabel.setLayoutY(225);
                    list.add(activePlayerLabel);
                } else {
                    switchPlayer();
                    col = makeMoveComp(lastCol, comp);

                    tBoard.setText(convertBoard(level));
                    done = winCheck(col);

                    if (done) {
                        char APC = charSet();
                        list.remove(moveField);
                        list.remove(moveLabel);
                        activePlayerLabel.setText("Player" + ICON_PX + " is the winner.");
                        activePlayerLabel.setLayoutX(250);
                        activePlayerLabel.setLayoutY(225);
                        list.add(activePlayerLabel);
                    }
                    switchPlayer();
                }



            } else {
                Label emptyLabel = new Label("You have not entered a move.");
                emptyLabel.setLayoutX(250);
                emptyLabel.setLayoutY(275);
            }
        }

    }

    /**
     * Initializes the GUI for playing against another player
     */
    public void initPlayer() {
        list.add(tBoard);
        list.remove(opponentSel);
        list.remove(opponentLabel);
        char activePlayerChar = charSet();

        moveLabel.setText("Enter Move, Player" + activePlayerChar);
        moveLabel.setLayoutY(225);
        moveLabel.setLayoutX(250);
        list.add(moveLabel);

        moveField.setLayoutY(250);
        moveField.setLayoutX(250);
        MoveHandlerPlayer moveHandlerPlayer = new MoveHandlerPlayer();
        moveField.setOnAction(moveHandlerPlayer);
        list.add(moveField);

    }

    /**
     * Initializes the GUI for playing against the computer
     */
    public void initComp() {
        list.add(tBoard);
        list.remove(opponentSel);
        list.remove(opponentLabel);
        char APC = charSet();

        moveLabel.setText("You are playerO.");
        moveLabel.setLayoutY(225);
        moveLabel.setLayoutX(250);
        list.add(moveLabel);

        moveField.setLayoutY(250);
        moveField.setLayoutX(250);
        MoveHandlerComp moveHandlerComp = new MoveHandlerComp();
        moveField.setOnAction(moveHandlerComp);
        list.add(moveField);
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
     * Converts the two-dimensional char array into a string for use with text elements in JavaFX
     * @param board two-dimensional char array representing the board
     * @return String representing the text-friendly board
     */
    public String convertBoard(char[][] board) {
        String s = "";
        for (int i=0; i<board.length; i++) {
            for (int j=0; j<board[i].length; j++) {
                s += board[i][j];
            }

            s += "\n";
        }
        return s.toString();
    }

    /**
     * Takes in the player's column selection, converts it into a number that can be used with the board, then attempts to place that choice on the board.
     * @param input the player's input
     * @return int representing the column the player moved into
     * @throws ArrayIndexOutOfBoundsException
     */
    public int makeMove(int input) throws ArrayIndexOutOfBoundsException {
        int column = 0;
        char activePlayerChar = charSet();

        // Convert input into number usable by board
        switch (input) {
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
            default:
                System.out.println("Invalid input.");
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
     * Takes in the computer's column selection, converts it into a number that can be used with the board, then attempts to place that choice on the board.
     * @param lastCol the move made by the player prior to this call
     * @param comp computer player AI object
     * @return int representing the column the computer moved into
     */
    public int makeMoveComp(int lastCol, Connect4ComputerPlayer comp) {
        int move = comp.compMakeMove(lastCol);
        int y = nextOpen(move);
        if (y == -1) {
            System.out.println("Computer made invalid move.");
            return move;
        }
        level[y][move] = ICON_PX;
        return move;
    }

    /**
     * Helper method to provide char for filling the board with active player's icon when needed
     * @return char representing active player's icon
     */
    public char charSet() {
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
     * Method for parsing player's response to whether they want to play against the computer or another human player
     * @param s input from opponent selection text field
     * @return int representing selected opponent
     * @throws IllegalArgumentException
     */
    public int opponentSelect(String s) throws IllegalArgumentException {
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
     * Finds the highest spot in a given column not occupied by a player icon
     * @param col Column from user selection
     * @return int representing highest spot not occupied by a player icon
     */
    public int nextOpen(int col) {
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
    public boolean winCheck(int col) {
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
    public void switchPlayer() {
        if (activePlayer == 0) {
            activePlayer = 1;
        } else if (activePlayer == 1) {
            activePlayer = 0;
        } else {
            System.out.println("Player error."); //Should never reach this line.
        }
    }
}




