package gameLogic;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

/**
 * Created by Victoria
 */
public class game extends Applet {
    private static board b = new board();

    /**
     * Draws the board and sets up the pieces
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(600, 620);
        b.placePieces();

        frame.getContentPane().add(b);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
