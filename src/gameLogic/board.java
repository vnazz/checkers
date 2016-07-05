package gameLogic;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by Victoria
 */

public class board extends JPanel implements MouseListener{
    private color[][] boardColor = new color[8][8];
    private color player = color.RED;
    private ArrayList<Move> moves;
    private boolean jumped = false;
    private int redCount = 12;
    private int blackCount = 12;
    private int blackKing = 0;
    private int redKing = 0;
    private color winner = color.NULL;
    private boolean initial = true;
    private aiMove ai;
    private boolean tie = false;

    /**
     * Creates a new board with an ai player and mouse listeners
     */
    public board() {
        addMouseListener(this);
        ai = new aiMove(color.BLACK);
    }

    /**
     * Creates a new board with pieces in the
     * same locations as boardColor
     * @param boardColor the location of the pieces
     */
    public board(color[][] boardColor) {
        this();
        this.boardColor = boardColor;
    }

    /**
     * Creates a new board with pieces in the
     * same locations as boardColor and with numRed
     * red pieces and numBlack black pieces
     * @param boardColor the location of the pieces
     * @param numRed the number of red pieces
     * @param numBlack the number of black pieces
     */
    public board(color[][] boardColor, int numRed, int numBlack) {
        this();
        this.boardColor = boardColor;
        this.redCount = numRed;
        this.blackCount = numBlack;
    }

    /**
     * Creates a new board with pieces in the
     * same locations as boardColor and with numRed
     * red pieces, numBlack black pieces, numRedKing
     * red kings, and numBlackKing black kings
     * @param boardColor the location of the pieces
     * @param numRed the number of red pieces
     * @param numBlack the number of black pieces
     * @param numRedKing the number of red kings
     * @param numBlackKing the number of black kings
     */
    public board(color[][] boardColor, int numRed, int numBlack, int numRedKing, int numBlackKing) {
        this(boardColor, numRed, numBlack);
        this.redKing = numRedKing;
        this.blackKing = numBlackKing;
    }

    /**
     * @return the number of red pieces on the board
     */
    public int getNumRed() {
        return redCount;
    }

    /**
     * @return the number of red kings on the board
     */
    public int getNumRedKing() {
        return redKing;
    }

    /**
     * @return the number of black pieces on the board
     */
    public int getNumBlack() {
        return blackCount;
    }

    /**
     * @return the number of black kings on the board
     */
    public int getNumBlackKing() {
        return blackKing;
    }

    /**
     * @return whether or not the last move was a jump
     */
    public boolean isJumped() {return jumped;}

    /**
     * @return the weighted score of the board
     */
    public int getRedWeightedScore() {
        return redCount - redKing + (3 * redKing);
    }

    /**
     * @return the weighted score of the board
     */
    public int getBlackWeightedScore() {
        return blackCount - blackKing + (3 * blackKing);
    }

    /**
     * Sets the player to color
     * @param color the color the player will be set to
     */
    public void setPlayer(color color) {
        player = color;
    }

    /**
     * Handles moving the piece that was selected and updating the
     * board
     * @param evt where the piece was dragged to
     */
    public void mouseReleased(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

        boolean crowned;

        if (player != ai.getColor()) {
            for (Move move : moves) {
                if (move.movRow == row && move.movCol == col) {

                    crowned = movePiece(move);
                    handleJump(move);
                    updateBoard(move, crowned);
                    break;
                }
            }
            repaint();
        } else if (player == ai.getColor()) {
            Move aiMove = ai.getAIMove(this);

            crowned = movePiece(aiMove);
            handleJump(aiMove);
            updateBoard(aiMove, crowned);
        }

    }

    /**
     * Handles repainting the board, multiple jumps, changing the player
     * and checking for a winner
     * @param move the move that was made
     * @param crowned whether or not the piece was crowned on the move
     */
    public void updateBoard(Move move, boolean crowned) {
        // Checks for winner
        if (blackCount == 0) {
            winner = color.RED;
            repaint();
            return;
        }
        if (redCount == 0) {
            winner = color.BLACK;
            repaint();
            return;
        }

        // Handle multiple jumps
        if (!crowned && jumped) {
            if (getJumps(move.movRow, move.movCol).isEmpty()) {
                jumped = false;
                if (player == color.RED)
                    player = color.BLACK;
                else
                    player = color.RED;
            }
        } else {
            // Changes player
            jumped = false;
            if (player == color.RED) {
                player = color.BLACK;
            } else
                player = color.RED;
        }
        repaint();
    }

    /**
     * Removes the intial message
     * @param evt the mouse click
     */
    public void mouseClicked(MouseEvent evt) {
        if (initial) {
            initial = false;
            repaint();
        }
    }


    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

    /**
     * Checks for a tie and gets all the possible moves for the chosen piece
     * @param evt the location of the piece that was chosen
     */
    public void mousePressed(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

        // Check for tie
        if (getAllLegalMovesForColor(player).size() == 0) {
            if (player == color.RED) {
                player = color.BLACK;
            } else {
                player = color.RED;
            }
            if (getAllLegalMovesForColor(player).size() == 0) {
                tie = true;
                repaint();
            }
        }

        // Get possible moves based on whether the last move was a jump
        if (col >= 0 && col < 8 && row >=0 && row < 8) {
            if (jumped) {
                moves = getJumps(row, col);
            } else {
                moves = getLegalMovesForPlayer(row, col);
            }
        }
    }

    /**
     * Deletes the piece that was jumped over
     * @param move the move that was made
     */
    public void handleJump(Move move) {
        Pair<Integer, Integer> spaceSkipped = move.getSpaceInbetween();

        // Verifies that jump was made
        if (spaceSkipped.getKey() != move.currRow && spaceSkipped.getKey() != move.movRow &&
                spaceSkipped.getValue() != move.movCol && spaceSkipped.getValue() != move.currCol) {
            if (boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] == color.RED_KING) {
                redKing -= 1;
            }
            if (boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] == color.BLACK_KING) {
                blackKing -= 1;
            }
            boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] = color.EMPTY;
            jumped = true;
            if (player == color.RED) {
                blackCount -= 1;

            } else {
                redCount -= 1;
            }
        } else {
            jumped = false;
        }
    }

    /**
     * Get all the legal jumps from the given location
     * @param row the row of the piece
     * @param col the column of the piece
     * @return an array of all legal jumps
     */
    public ArrayList<Move> getJumps(int row, int col) {
        ArrayList<Move> jumps = new ArrayList<>();
        color chosenPiece = getInfoAtPosition(row, col);

        // Get red jumps
        if (player == color.RED) {
            if (chosenPiece == color.RED || chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.BLACK ||
                        getInfoAtPosition(row + 1, col + 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == color.BLACK ||
                        getInfoAtPosition(row + 1, col - 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }

            // Get backward jumps
            if (chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.BLACK ||
                        getInfoAtPosition(row - 1, col + 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                } if (getInfoAtPosition(row - 1, col - 1) == color.BLACK ||
                        getInfoAtPosition(row - 1, col - 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }
        } else if (player == color.BLACK) { // Get black jumps
            if (chosenPiece == color.BLACK || chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.RED ||
                        getInfoAtPosition(row - 1, col + 1) == color.RED_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row - 1, col - 1) == color.RED ||
                        getInfoAtPosition(row - 1, col - 1) == color.RED_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }

            // Get backwards jumps
            if (chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.RED ||
                        getInfoAtPosition(row + 1, col + 1) == color.RED_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == color.RED ||
                        getInfoAtPosition(row + 1, col - 1) == color.RED_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }
        }
        return jumps;
    }

    /**
     * Gets all the legal moves for the current player at the given location
     * @param row a row
     * @param col a column
     * @return all legal moves at position row, col for the current player
     */
    public ArrayList<Move> getLegalMovesForPlayer(int row, int col) {
        return getLegalMovesForColorAtPosition(player, row, col);
    }

    /**
     * Get all the legal moves for the given color at the given location
     * @param color the color whose moves will be found
     * @param row a row
     * @param col a column
     * @return all legal moves at position row, col for color
     */
    public ArrayList<Move> getLegalMovesForColorAtPosition(color color, int row, int col) {
        board.color chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();

        // Get red moves
        if (color == board.color.RED) {
            if (chosenPiece == board.color.RED || chosenPiece == board.color.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));

            }
            if (chosenPiece == board.color.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));

            }
        } else if (color == board.color.BLACK){ // Get black moves
            if (chosenPiece == board.color.BLACK || chosenPiece == board.color.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));
            }
            if (chosenPiece == board.color.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));
            }
        }

        // Add jumps
        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    /**
     * Get all possible moves for the given color
     * @param color the color whose moves will be found
     * @return all legal moves for color
     */
    public ArrayList<Move> getAllLegalMovesForColor(color color) {
        ArrayList<Move> moves = new ArrayList<>();
        int count = 0;

        // Loop through board and get moves at each location
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.color currPosition = getInfoAtPosition(row, col);
                if (currPosition == color) {
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                }

                // Get king moves
                if (color == board.color.RED && currPosition == board.color.RED_KING){
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                } else if (color == board.color.BLACK && currPosition == board.color.BLACK_KING) {
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                }

                // Stop if all the pieces of the color have been found
                if (count == 12) {
                    return moves;
                }
            }
        }
        return moves;
    }

    /**
     * Moves a piece
     * @param move the move that will be made
     * @return whether or not a king was made
     */
    public boolean movePiece(Move move) {
        // Changes location of piece
        color temp = boardColor[move.currRow][move.currCol];
        boardColor[move.currRow][move.currCol] = color.EMPTY;

        // handles king
        if (player == color.RED && move.movRow == 7) {
            boardColor[move.movRow][move.movCol] = color.RED_KING;
            redKing += 1;
            return true;
        } else if (player == color.BLACK && move.movRow == 0){
            boardColor[move.movRow][move.movCol] = color.BLACK_KING;
            blackKing += 1;
            return true;
        } else {
            boardColor[move.movRow][move.movCol] = temp;
            return false;
        }
    }

    /**
     * Repaints the board based on the status of the game
     * @param graphic what will be painted
     */
    public void paintComponent(Graphics graphic) {
        // Creates checker board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (col % 2 == row % 2) {
                    graphic.setColor(Color.white);
                } else {
                    graphic.setColor(Color.black);
                }
                graphic.fillRect(row * 75, col * 75, 75, 75);

            }
        }
        // Initial message
        if (initial == true) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 20));
            graphic.drawString("WELCOME TO CHECKERS! CLICK TO BEGIN", 85, 300);
        }

        // Winner message
        if (winner != color.NULL) {
            graphic.setColor(Color.RED);
            if (winner == color.RED) {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
                graphic.drawString("RED WINS", 50, 300);
            } else {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 90));
                graphic.drawString("BLACK WINS", 30, 300);
            }
        } else if (tie) { // Tie message
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
            graphic.drawString("TIE", 225, 300);

        } else { // Adds pieces if game still in progress
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {

                    switch (getInfoAtPosition(row, col)) {

                        case RED:
                            graphic.setColor(Color.RED);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            break;
                        case RED_KING:
                            graphic.setColor(Color.RED);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            graphic.setColor(Color.WHITE);
                            graphic.setFont(new Font("Helvetica", Font.PLAIN, 50));
                            graphic.drawString("K", col * 75 + 23, row * 75 + 55);
                            break;
                        case BLACK:
                            graphic.setColor(Color.BLUE);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            break;
                        case BLACK_KING:
                            graphic.setColor(Color.BLUE);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            graphic.setColor(Color.WHITE);
                            graphic.setFont(new Font("Helvetica", Font.PLAIN, 50));
                            graphic.drawString("K", col * 75 + 23, row * 75 + 55);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Places twelve pieces of each color in their
     * initial positions
     */
    public void placePieces() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 != col % 2) {
                    if (row < 3) {
                        boardColor[row][col] = color.RED;
                    } else if (row > 4) {
                        boardColor[row][col] = color.BLACK;
                    } else {
                        boardColor[row][col] = color.EMPTY;
                    }
                } else {
                    boardColor[row][col] = color.EMPTY;
                }
            }
        }
    }

    /**
     * Returns the type of piece at the given position
     * @param row a row
     * @param col a column
     * @return a color
     */
    public color getInfoAtPosition(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return color.NULL;
        }
        return boardColor[row][col];
    }

    /**
     * Creates a string representation of the board
     * @return a String version of the board
     */
    public String toString() {
        String string = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                switch (getInfoAtPosition(row, col)) {
                    case EMPTY:
                        string = string.concat("|  ");
                        break;
                    case RED:
                        string = string.concat("| R");
                        break;
                    case RED_KING:
                        string = string.concat("|RK");

                        break;
                    case BLACK:
                        string = string.concat("| B");

                        break;
                    case BLACK_KING:
                        string = string.concat("|BK");
                        break;
                }
                if (col == 7) {
                    string = string.concat("|\n");
                }
            }
        }
        return string;
    }

    /**
     * Determines if both boards have the same pieces at the
     * same locations
     * @param other a board
     * @return if the boards are equal
     */
    public boolean equals(board other) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.getInfoAtPosition(row, col) != other.getInfoAtPosition(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * All possible pieces that can be on the board
     */
    public enum color {
        BLACK,
        BLACK_KING,
        RED,
        RED_KING,
        EMPTY,
        NULL
    }
}