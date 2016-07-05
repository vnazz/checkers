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
    private info[][] boardInfo = new info[8][8];
    private info player = info.RED;
    private ArrayList<Move> moves;
    private boolean jumped = false;
    private int redCount = 12;
    private int blackCount = 12;
    private int blackKing = 0;
    private int redKing = 0;
    private info winner = info.NULL;
    private boolean initial = true;
    private aiMove ai;
    private boolean tie = false;

    public board() {
        addMouseListener(this);
        ai = new aiMove(board.info.BLACK);
    }

    public board(info[][] boardInfo) {
        addMouseListener(this);
        this.boardInfo = boardInfo;
    }

    public board(info[][] boardInfo, int numRed, int numBlack) {
        addMouseListener(this);
        this.boardInfo = boardInfo;
        this.redCount = numRed;
        this.blackCount = numBlack;
    }

    public board(info[][] boardInfo, int numRed, int numBlack, int numRedKing, int numBlackKing) {
        addMouseListener(this);
        this.boardInfo = boardInfo;
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


    public void setPlayer(info color) {
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
                        winner = info.RED;
                        repaint();
                        return;
                    }
                    if (redCount == 0) {
                        winner = info.BLACK;
                        repaint();
                        return;
                    }

                    // Handle multi jump
                    if (!crowned && jumped) {
                        if (getJumps(move.movRow, move.movCol).isEmpty()) {
                            jumped = false;
                            if (player == info.RED)
                                player = info.BLACK;
                            else
                                player = info.RED;
                        }
                    } else {
                        // Change player
                        jumped = false;
                        if (player == info.RED) {
                            player = info.BLACK;
                        } else
                            player = info.RED;
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
                winner = info.RED;
                repaint();
                return;
            }
            if (redCount == 0) {
                winner = info.BLACK;
                repaint();
                return;
            }

            // Handle multi jump
            if (!crowned && jumped) {
                if (getJumps(aiMove.movRow, aiMove.movCol).isEmpty()) {
                    jumped = false;
                    if (player == info.RED)
                        player = info.BLACK;
                    else
                        player = info.RED;
                }
            } else {

                // Change player
                jumped = false;
                if (player == info.RED) {
                    player = info.BLACK;
                } else
                    player = info.RED;
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
            if (player == info.RED) {
                player = info.BLACK;
            } else {
                player = info.RED;
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
            if (boardInfo[spaceSkipped.getKey()][spaceSkipped.getValue()] == info.RED_KING) {
                redKing -= 1;
            }
            if (boardInfo[spaceSkipped.getKey()][spaceSkipped.getValue()] == info.BLACK_KING) {
                blackKing -= 1;
            }
            boardInfo[spaceSkipped.getKey()][spaceSkipped.getValue()] = info.EMPTY;
            jumped = true;
            if (player == info.RED) {
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
        info chosenPiece = getInfoAtPosition(row, col);
        if (player == info.RED) {
            if (chosenPiece == info.RED || chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.BLACK ||
                        getInfoAtPosition(row + 1, col + 1) == info.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == info.BLACK ||
                        getInfoAtPosition(row + 1, col - 1) == info.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }
            if (chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.BLACK ||
                        getInfoAtPosition(row - 1, col + 1) == info.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                } if (getInfoAtPosition(row - 1, col - 1) == info.BLACK ||
                        getInfoAtPosition(row - 1, col - 1) == info.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }
        } else if (player == info.BLACK) {
            if (chosenPiece == info.BLACK || chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.RED ||
                        getInfoAtPosition(row - 1, col + 1) == info.RED_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row - 1, col - 1) == info.RED ||
                        getInfoAtPosition(row - 1, col - 1) == info.RED_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }
            if (chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.RED ||
                        getInfoAtPosition(row + 1, col + 1) == info.RED_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == info.RED ||
                        getInfoAtPosition(row + 1, col - 1) == info.RED_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == info.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }
        }
        return jumps;
    }

    public ArrayList<Move> getLegalMoves(int row, int col) {
        info chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();
        if (player == info.RED) {
            if (chosenPiece == info.RED || chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));

            }
            if (chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));

            }
        } else if (player == info.BLACK){
            if (chosenPiece == info.BLACK || chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));
            }
            if (chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));
            }
        }
        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    public ArrayList<Move> getLegalMovesForColor(info color, int row, int col) {
        info chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();
        if (color == info.RED) {
            if (chosenPiece == info.RED || chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));

            }
            if (chosenPiece == info.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));

            }
        } else if (color == info.BLACK){
            if (chosenPiece == info.BLACK || chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));
            }
            if (chosenPiece == info.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == info.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));
            }
        }
        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    public ArrayList<Move> getAllLegalMovesForColor(info color) {
        ArrayList<Move> moves = new ArrayList<>();
        int count = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                info currPosition = getInfoAtPosition(row, col);
                if (currPosition == color) {
                    moves.addAll(getLegalMovesForColor(color, row, col));
                    count++;
                }
                if (color == info.RED && currPosition == info.RED_KING){
                    moves.addAll(getLegalMovesForColor(color, row, col));
                    count++;
                } else if (color == info.BLACK && currPosition == info.BLACK_KING) {
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
        info temp = boardInfo[move.currRow][move.currCol];
        boardInfo[move.currRow][move.currCol] = info.EMPTY;

        // handle king
        if (player == info.RED && move.movRow == 7) {
            boardInfo[move.movRow][move.movCol] = info.RED_KING;
            redKing += 1;
            return true;
        } else if (player == info.BLACK && move.movRow == 0){
            boardInfo[move.movRow][move.movCol] = info.BLACK_KING;
            blackKing += 1;
            return true;
        } else {
            boardInfo[move.movRow][move.movCol] = temp;
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
            graphic.drawString("WELCOME TO CHECKERS! CLICK TO BEGIN", 0, 300);
        }
        if (winner != info.NULL) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
            if (winner == info.RED)
                graphic.drawString("RED WINS", 0, 300);
            else {
                graphic.drawString("BLACK WINS", 0, 300);
            }
        } else if (tie) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
            graphic.drawString("TIE", 0, 300);

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
                        boardInfo[row][col] = info.RED;
                    else if (row > 4)
                        boardInfo[row][col] = info.BLACK;

                    else {
                        boardInfo[row][col] = info.EMPTY;

                    }
                } else {
                    boardInfo[row][col] = info.EMPTY;

                }
            }
        }
    }

    public info getInfoAtPosition(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7)
            return info.NULL;
        return boardInfo[row][col];
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

    public enum info {
        BLACK,
        BLACK_KING,
        RED,
        RED_KING,
        EMPTY,
        NULL
    }
}