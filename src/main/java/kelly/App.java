package kelly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class App
{
    private static final boolean TEST_SOUND_FX = false;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 480;
    private SpaceField field;
    private GameDisplay c;

    private static void addButtonTo(JPanel jp, String label, ActionListener al) {
        JButton jb = new JButton(label);
        jp.add(jb);
        jb.addActionListener(al);
    }

    public App() {
        field = new SpaceField(WIDTH, HEIGHT);
        c = new GameDisplay(field);
        Dimension d = new Dimension(WIDTH, HEIGHT);
        c.setPreferredSize(d);
//        c.addKeyListener(field);

        JFrame jf = new JFrame("Asteroid Blast");
        jf.add(c, BorderLayout.CENTER);
        jf.addKeyListener(field);

        jf.add(new GameStatusPanel(field), BorderLayout.NORTH);

        JPanel jp = new JPanel();
        jf.add(jp, BorderLayout.SOUTH);
        jp.add(new JLabel("Press 'r' to restart"));
        if(TEST_SOUND_FX) {
            jp.addKeyListener(field);
            for(SoundPlayer.SoundFX sfx : SoundPlayer.SoundFX.values()) {
                addButtonTo(jp, sfx.toString(), e -> sfx.playSound());
            }
        }

        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.pack();
        jf.setVisible(true);

        field.runGame();
    }

    public static void main( String[] args ) {
        App app = new App();
    }
}
