/**
 * This interface defines a group of constants that both the server and client may need
 * @author Garrett Pierce
 * @version 1.0 9/19/2018
 */
public interface Connect4Constants {
    public static int PLAYER1 = 1;
    public static int PLAYER2 = 2;
    public static int PLAYER1_WON = 1;
    public static int PLAYER2_WON = 2;
    public static int DRAW = 3;
    public static int CONTINUE = 4;
    public static final char ICON_WALL = '|';
    public static final char ICON_BLANK = ' ';
    public static final char ICON_PX = 'X';
    public static final char ICON_PO = 'O';
    public static final char BOARD_WIDTH = 15;
    public static final char BOARD_HEIGHT = 6;
}
