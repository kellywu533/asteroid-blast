package kelly;

import javax.swing.*;
import java.awt.*;

public class GameDisplay extends JComponent implements TimeEventListener {
    private SpaceField field;

    public GameDisplay(SpaceField field) {
        this.field = field;
        addKeyListener(field);
        field.addTimeEventListener(this);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        field.drawField(g2d);
    }

    @Override
    public void onTimeEvent() {
        repaint();
    }
}
