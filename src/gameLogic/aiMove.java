package gameLogic;


import java.util.ArrayList;

/**
 * Created by Victoria
 */
public class aiMove {

    private board.color color;
    private board.color oppColor;
    private Tree descisionTree;

    /**
     * Creates a new aiMove with the color of its pieces
     * @param color the color of the pieces the ai will move
     */
    public aiMove(board.color color) {
        this.color = color;
        if (color == board.color.RED) {
            oppColor = board.color.BLACK;
        } else {
            oppColor = board.color.RED;
        }
    }

    /**
     * Picks its move based on all possible moves
     * @param board the current state of the board
     * @return the move picked by the ai
     */
    public Move getAIMove(board board) {
        descisionTree = makeDescisionTree(board);
        return pickMove();
    }

    /**
     * @return the color of the ai
     */
    public board.color getColor() {
        return color;
    }

    /**
     * Creates a tree with a height of four that has all possible moves
     * for the next three moves of the game
     * @param board the board that the tree will be based on
     * @return a tree with all possible moves
     */
    private Tree makeDescisionTree(board board) {
        Tree mainTree = new Tree(board, null, score(board));
        ArrayList<Move> moves = board.getAllLegalMovesForColor(color);

        for (Move move : moves) {
            // Make second row
            board temp = copyBoard(board);
            temp.movePiece(move);
            temp.handleJump(move);
            Tree firstLayer = new Tree(temp, move, score(temp));
            ArrayList<Move> secondMoves = temp.getAllLegalMovesForColor(oppColor);

            for (Move sMove : secondMoves) {
                // Make third row
                board temp2 = copyBoard(temp);
                temp2.movePiece(sMove);
                temp2.handleJump(sMove);
                Tree secondLayer = new Tree(temp2, sMove, score(temp2));
                ArrayList<Move> thirdMoves = temp2.getAllLegalMovesForColor(color);

                for (Move tMove : thirdMoves) {
                    // Make fourth row
                    board temp3 = copyBoard(temp2);
                    temp3.movePiece(tMove);
                    temp3.handleJump(tMove);

                    secondLayer.addChild(new Tree(temp3, tMove, score(temp3)));
                }

                firstLayer.addChild(secondLayer);
            }
            mainTree.addChild(firstLayer);
        }

        return mainTree;
    }

    /**
     * Picks the move based on minimax
     * @return the move that was selected
     */
    private Move pickMove() {
        int max = -13;
        int index = 0;
        for (int i = 0; i < descisionTree.getNumChildren(); i++) {
            Tree child = descisionTree.getChild(i);
            int smin = 13;
            // Find the max leaf
            for (Tree sChild : child.getChildren()) {
                int tMax = -13;
                for (Tree tchild : sChild.getChildren()) {
                    if (tchild.getScore() >= tMax) {
                        tMax = tchild.getScore();
                    }
                }
                sChild.setScore(tMax);
                // Find the min on the third level
                if (sChild.getScore() <= smin) {
                    smin = sChild.getScore();
                }
            }
            child.setScore(smin);
            // Find the max on the second layer and save the index
            if (child.getScore() >= max) {
                max = child.getScore();
                index = i;
            }
        }
        return descisionTree.getChild(index).getMove();
    }

    /**
     * Scores the given board based on a weighted system
     * @param board the board that will be scored
     * @return the score of the given board
     */
    private int score(board board) {
        if (color == gameLogic.board.color.RED) {
            return board.getRedWeightedScore() - board.getBlackWeightedScore();
        } else {
            return board.getRedWeightedScore() - board.getRedWeightedScore();
        }
    }

    /**
     * Creates a new board with the same information as the given board
     * @param board the board that will be copied
     * @return a copy of the given board
     */
    private board copyBoard(board board) {
        gameLogic.board.color[][] color = new board.color[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                color[row][col] = board.getInfoAtPosition(row, col);
            }
        }
        return new board(color, board.getNumRed(), board.getNumBlack(), board.getNumRedKing(), board.getNumBlackKing());
    }


}
