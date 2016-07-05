package gameLogic;

import javafx.util.Pair;

/**
 * Created by Victoria
 */
public class Move {
    public int currRow, currCol, movRow, movCol;

    public Move(int currRow, int currCol, int movRow, int movCol) {
        this.currRow = currRow;
        this.currCol = currCol;
        this.movRow = movRow;
        this.movCol = movCol;
    }

    public Pair<Integer, Integer> getSpaceInbetween() {
        return new Pair<>((currRow + movRow) / 2, (currCol + movCol) / 2);

    }

    public String toString() {
        return "current: (" + currRow + ", " + currCol + ") + next: (" + movRow + "," + movCol + ")";
    }
}
