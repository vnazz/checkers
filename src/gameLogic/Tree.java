package gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Victoria
 */
public class Tree {
    private board node;
    private Move move;
    private int score;
    private ArrayList<Tree> children;

    /**
     * Creates a new tree with node as the head, a move, a score, and children
     * @param node the head of the tree
     * @param move the move associated with the tree
     * @param score the score of the tree
     * @param children the children of the node
     */
    public Tree(board node, Move move, int score, Tree ... children) {
        this.node = node;
        this.children = new ArrayList<>(Arrays.asList(children));
        this.score = score;
        this.move = move;
    }

    /**
     * @return the board of the tree
     */
    public board getBoard() {return node;}

    /**
     * @return the move of the tree
     */
    public Move getMove() {
        return move;
    }

    /**
     * @return the score of the tree
     */
    public int getScore() {
        return score;
    }

    /**
     * @return the tree's children
     */
    public List<Tree> getChildren() {
        return children;
    }

    /**
     * @return the number of children the tree has
     */
    public int getNumChildren() {
        return children.size();
    }

    /**
     * Changes the tree's score
     * @param newVal the new score of the tree
     */
    public void setScore(int newVal) {
        score = newVal;
    }

    /**
     * The child at the given index
     * @param index the chosen index
     * @return the child at the index
     */
    public Tree getChild(int index) {
        return children.get(index);
    }

    /**
     * Adds a child to the tree
     * @param child the tree that will be added to the children
     */
    public void addChild(Tree child) {
        children.add(child);
    }

    /**
     * Adds multiple children to the tree
     * @param children the trees that will be added to the children
     */
    public void addChildren(Tree ... children) {
        for (Tree child : children) {
            addChild(child);
        }
    }
}
