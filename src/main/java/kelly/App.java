package kelly;

import javax.swing.*;
import java.awt.*;

/**
 * Lays out the user interface elements.
 */
public class App
{
    private static final int WIDTH = 600;
    private static final int HEIGHT = 480;

    /**
     * Constructs the user interface elements.
     */
    public App() {
        SpaceField field = new SpaceField(WIDTH, HEIGHT);
        GameDisplay c = new GameDisplay(field);
        Dimension d = new Dimension(WIDTH, HEIGHT);
        c.setPreferredSize(d);

        JFrame jf = new JFrame("Asteroid Blast");
        jf.add(c, BorderLayout.CENTER);
        jf.addKeyListener(field);

        jf.add(new GameStatusPanel(field), BorderLayout.NORTH);

        JPanel jp = new JPanel();
        jf.add(jp, BorderLayout.SOUTH);
        jp.add(new JLabel("Press 'r' restart; 'Space' fire; 'Left/Right' rotate; 'Down' shield"));

        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.pack();
        jf.setVisible(true);

        field.runGame();
    }

    public static void main( String[] args ) {
        new App();
    }
}
