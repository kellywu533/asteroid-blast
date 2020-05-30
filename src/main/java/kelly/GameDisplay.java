package kelly;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the Asteroid Blast game.
 */
public class GameDisplay extends JComponent implements TimeEventListener {
    private SpaceField field;

    /**
     * Constructs the display of the field for the game.
     * @param field The game field.
     */
    public GameDisplay(SpaceField field) {
        this.field = field;
        addKeyListener(field);
        field.addTimeEventListener(this);
    }

    /**
     * Overrides the Paint method to draw the game field.
     * @param g Graphics.
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g;
        field.drawField(g2d);
    }

    /**
     * Implements the TimeEventListener method to repaint the field on each time event recieved.
     */
    @Override
    public void onTimeEvent() {
        repaint();
    }
}
