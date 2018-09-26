/**
 * Defines methods for computer player
 * @author Garrett Pierce
 * @version 1.0 9/2/2018
 */
public class Connect4ComputerPlayer {
    /**
     * Performs calculations necessary to determine whether or not to defend or attack, and which column to move into
     * @param lastCol incoming from Connect4.java, passed in to use when computer decides to go on the defensive
     * @return int representing the column computer chose to move into
     */
    public int compMakeMove(int lastCol) {
        int column = 1;

        // Calculate range for random number
        int max = 7;
        int min = 1;
        int range = max - min + 1;

        int move = (int)(Math.random() * range) + min;

        // Generate random number between 1 and 2 for deciding if computer wants to try to block player
        int decision = (int)(Math.random() * 2) + 1;

        switch (move) {
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

        if (decision == 1) {
            return lastCol;
        } else if (decision == 2) {
            return column;
        }

        return column;
    }
}
