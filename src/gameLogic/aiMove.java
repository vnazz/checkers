package gameLogic;


import java.util.ArrayList;

/**
 * Created by Victoria
 */
public class aiMove {

    private board.info color;
    private board.info oppColor;

    Tree descisionTree;

    public aiMove(board.info color) {
        this.color = color;
        if (color == board.info.RED) {
            oppColor = board.info.BLACK;
        } else {
            oppColor = board.info.RED;
        }
    }

    public Move getAIMove(board board) {
        descisionTree = makeDescisionTree(board);
        return pickMove();
    }

    public board.info getColor() {
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
        if (color == gameLogic.board.info.RED) {
            return board.getRedWeightedScore() - board.getBlackWeightedScore();
        } else {
            return board.getRedWeightedScore() - board.getRedWeightedScore();
        }
    }

    private board copyBoard(board board) {
        board.info[][] info = new board.info[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                info[row][col] = board.getInfoAtPosition(row, col);
            }
        }
        return new board(info, board.getNumRed(), board.getNumBlack(), board.getNumRedKing(), board.getNumBlackKing());
    }


}
