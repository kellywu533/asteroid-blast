package kelly;

import javax.swing.*;
import java.awt.*;

public class App
{
    private static final int WIDTH = 600;
    private static final int HEIGHT = 480;
    private SpaceField field;
    private GameDisplay c;

    public App() {
        field = new SpaceField(WIDTH, HEIGHT);
        c = new GameDisplay(field);
        Dimension d = new Dimension(WIDTH, HEIGHT);
        c.setPreferredSize(d);

        JFrame jf = new JFrame("Asteroid Blast");
        jf.add(c);
        jf.addKeyListener(field);

        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);

        field.startClock();
    }

    public static void main( String[] args ) {
        App app = new App();
    }
}
