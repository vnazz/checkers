package gameLogic;


import java.util.ArrayList;

/**
 * Created by Victoria
 */
public class aiMove {

    private board.color color;
    private board.color oppColor;

    Tree descisionTree;

    public aiMove(board.color color) {
        this.color = color;
        if (color == board.color.RED) {
            oppColor = board.color.BLACK;
        } else {
            oppColor = board.color.RED;
        }
    }

    public Move getAIMove(board board) {
        descisionTree = makeDescisionTree(board);
        return pickMove();
    }

    public board.color getColor() {
        return color;
    }

    private Move pickMove() {
        int max = -13;
        int index = 0;
        for (int i = 0; i < descisionTree.getNumChildren(); i++) {
            Tree child = descisionTree.getChild(i);
            int smin = 13;
            for (Tree sChild : child.getChildren()) {
                int tMax = -13;
                for (Tree tchild : sChild.getChildren()) {
                    if (tchild.getScore() >= tMax) {
                        tMax = tchild.getScore();
                    }
                }
                sChild.setScore(tMax);

                if (sChild.getScore() <= smin) {
                    smin = sChild.getScore();
                }
            }
            child.setScore(smin);
            if (child.getScore() >= max) {
                max = child.getScore();
                index = i;
            }
        }
        return descisionTree.getChild(index).getMove();
    }

    private Tree makeDescisionTree(board board) {
        Tree mainTree = new Tree(board, null, score(board));
        ArrayList<Move> moves = board.getAllLegalMovesForColor(color);
        for (Move move : moves) {
            board temp = copyBoard(board);
            temp.movePiece(move);
            temp.handleJump(move);
            Tree firstLayer = new Tree(temp, move, score(temp));
            ArrayList<Move> secondMoves = temp.getAllLegalMovesForColor(oppColor);

            for (Move sMove : secondMoves) {
                board temp2 = copyBoard(temp);
                temp2.movePiece(sMove);
                temp2.handleJump(sMove);

                Tree secondLayer = new Tree(temp2, sMove, score(temp2));

                ArrayList<Move> thirdMoves = temp2.getAllLegalMovesForColor(color);
                for (Move tMove : thirdMoves) {
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

    private int score(board board) {
        if (color == gameLogic.board.color.RED) {
            return board.getRedWeightedScore() - board.getBlackWeightedScore();
        } else {
            return board.getRedWeightedScore() - board.getRedWeightedScore();
        }
    }

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
