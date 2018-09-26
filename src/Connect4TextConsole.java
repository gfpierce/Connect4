/**
 * Defines methods to print the game board to the console at various points in the game
 * @author Garrett Pierce
 * @version 1.0 8/28/2018
 */

public class Connect4TextConsole {
    private final char ICON_WALL = '|';
    private final char ICON_BLANK = ' ';
    private final char ICON_PX = 'X';
    private final char ICON_PO = 'O';
    private final char BOARD_WIDTH = 15;
    private final char BOARD_HEIGHT = 6;

    /**
     * Generates a board in its initial state
     * @return char[][] representing the game board
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
     * Displays board as passed in by parameter
     * @param level two-dimensional array of chars representing the game board in its current state
     */
    public void display(char[][] level) {
        int y, x;
        for (y = 0; y < BOARD_HEIGHT; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                System.out.print(level[y][x]);
            }
            System.out.println();
        }
    }
}