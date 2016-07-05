package gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Victoria
 */
public class Tree {
    board node;
    ArrayList<Tree> children;
    int score;
    Move move;

    public Tree(board node, Move move, int score, Tree ... children) {
        this.node = node;
        this.children = new ArrayList<>(Arrays.asList(children));
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }

    public board getBoard() {return node;}

    public void setScore(int newVal) {
        score = newVal;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public int getNumChildren() {
        return children.size();
    }

    public Tree getChild(int index) {
        return children.get(index);
    }

    public void addChild(Tree child) {
        children.add(child);
    }

    public void addChildren(Tree ... children) {
        for (Tree child : children) {
            addChild(child);
        }
    }
}
