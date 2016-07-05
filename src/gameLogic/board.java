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

    public board() {
        addMouseListener(this);
        ai = new aiMove(color.BLACK);
    }

    public board(color[][] boardColor) {
        addMouseListener(this);
        this.boardColor = boardColor;
    }

    public board(color[][] boardColor, int numRed, int numBlack) {
        addMouseListener(this);
        this.boardColor = boardColor;
        this.redCount = numRed;
        this.blackCount = numBlack;
    }

    public board(color[][] boardColor, int numRed, int numBlack, int numRedKing, int numBlackKing) {
        addMouseListener(this);
        this.boardColor = boardColor;
        this.redCount = numRed;
        this.blackCount = numBlack;
        this.redKing = numRedKing;
        this.blackKing = numBlackKing;
    }

    public int getNumRed() {
        return redCount;
    }

    public int getNumRedKing() {
        return redKing;
    }

    public int getNumBlack() {
        return blackCount;
    }

    public int getNumBlackKing() {
        return blackKing;
    }
    public int getRedWeightedScore() {
        return redCount - redKing + (3 * redKing);
    }

    public int getBlackWeightedScore() {
        return blackCount - blackKing + (3 * blackKing);
    }


    public void setPlayer(color color) {
        player = color;
    }

    public void mouseReleased(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

        boolean crowned;

        if (player != ai.getColor()) {
            for (Move move : moves) {
                if (move.movRow == row && move.movCol == col) {
                    // Move piece
                    crowned = movePiece(move);

                    // Delete piece that was eaten
                    handleJump(move);

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

                    // Handle multi jump
                    if (!crowned && jumped) {
                        if (getJumps(move.movRow, move.movCol).isEmpty()) {
                            jumped = false;
                            if (player == color.RED)
                                player = color.BLACK;
                            else
                                player = color.RED;
                        }
                    } else {
                        // Change player
                        jumped = false;
                        if (player == color.RED) {
                            player = color.BLACK;
                        } else
                            player = color.RED;
                    }

                    break;
                }
            }
            repaint();
        } else if (player == ai.getColor()) {
            Move aiMove = ai.getAIMove(this);
            crowned = movePiece(aiMove);

            // Delete piece that was eaten
            handleJump(aiMove);

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

            // Handle multi jump
            if (!crowned && jumped) {
                if (getJumps(aiMove.movRow, aiMove.movCol).isEmpty()) {
                    jumped = false;
                    if (player == color.RED)
                        player = color.BLACK;
                    else
                        player = color.RED;
                }
            } else {

                // Change player
                jumped = false;
                if (player == color.RED) {
                    player = color.BLACK;
                } else
                    player = color.RED;
            }
            repaint();
        }

    }
    public void mouseClicked(MouseEvent evt) {
        if (initial) {
            initial = false;
            repaint();
        }

    }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

    public void mousePressed(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

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
        if (col >= 0 && col < 8 && row >=0 && row < 8) {
            if (jumped) {
                moves = getJumps(row, col);
            } else {
                moves = getLegalMoves(row, col);
            }
        }
    }

    public void handleJump(Move move) {
        Pair<Integer, Integer> spaceSkipped = move.getSpaceInbetween();

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
                blackCount-=1;

            } else {
                redCount-=1;
            }
        } else {
            jumped = false;
        }
    }

    public ArrayList<Move> getJumps(int row, int col) {
        ArrayList<Move> jumps = new ArrayList<>();
        color chosenPiece = getInfoAtPosition(row, col);
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
        } else if (player == color.BLACK) {
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

    public ArrayList<Move> getLegalMoves(int row, int col) {
        color chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();
        if (player == color.RED) {
            if (chosenPiece == color.RED || chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));

            }
            if (chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));

            }
        } else if (player == color.BLACK){
            if (chosenPiece == color.BLACK || chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));
            }
            if (chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));
            }
        }
        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    public ArrayList<Move> getLegalMovesForColor(color color, int row, int col) {
        board.color chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();
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
        } else if (color == board.color.BLACK){
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
        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    public ArrayList<Move> getAllLegalMovesForColor(color color) {
        ArrayList<Move> moves = new ArrayList<>();
        int count = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.color currPosition = getInfoAtPosition(row, col);
                if (currPosition == color) {
                    moves.addAll(getLegalMovesForColor(color, row, col));
                    count++;
                }
                if (color == board.color.RED && currPosition == board.color.RED_KING){
                    moves.addAll(getLegalMovesForColor(color, row, col));
                    count++;
                } else if (color == board.color.BLACK && currPosition == board.color.BLACK_KING) {
                    moves.addAll(getLegalMovesForColor(color, row, col));
                    count++;
                }
                if (count == 12) {
                    return moves;
                }
            }
        }
        return moves;
    }

    public boolean movePiece(Move move) {
        color temp = boardColor[move.currRow][move.currCol];
        boardColor[move.currRow][move.currCol] = color.EMPTY;

        // handle king
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

    public void paintComponent(Graphics graphic) {
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

        if (initial == true) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 20));
            graphic.drawString("WELCOME TO CHECKERS! CLICK TO BEGIN", 85, 300);
        }
        if (winner != color.NULL) {
            graphic.setColor(Color.RED);
            if (winner == color.RED) {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
                graphic.drawString("RED WINS", 50, 300);
            } else {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 90));
                graphic.drawString("BLACK WINS", 30, 300);
            }
        } else if (tie) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
            graphic.drawString("TIE", 225, 300);

        } else {
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

    public void placePieces() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 != col % 2) {
                    if (row < 3)
                        boardColor[row][col] = color.RED;
                    else if (row > 4)
                        boardColor[row][col] = color.BLACK;

                    else {
                        boardColor[row][col] = color.EMPTY;

                    }
                } else {
                    boardColor[row][col] = color.EMPTY;

                }
            }
        }
    }

    public color getInfoAtPosition(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7)
            return color.NULL;
        return boardColor[row][col];
    }

    @Override
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

    public boolean equals(board other) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.getInfoAtPosition(row, col) != other.getInfoAtPosition(row, col))
                    return false;
            }
        }
        return true;
    }

    public enum color {
        BLACK,
        BLACK_KING,
        RED,
        RED_KING,
        EMPTY,
        NULL
    }
}