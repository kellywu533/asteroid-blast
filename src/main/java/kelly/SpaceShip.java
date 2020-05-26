package kelly;

import java.awt.*;

public class SpaceShip extends DrawableThing {
    private int scale;
    private int lives;

    public SpaceShip() {
        angle = 270;
        scale = 20;
        lives = 3;
    }

    public int getScale() {
        return scale;
    }

    public void rotate(int angle) {
        setAngle(getAngle() + angle);
    }

    @Override
    public void update(int timeIndex, double[] force, double[] bounds) {

    }

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        int[] xVal = new int[] {-scale, scale, -scale};
        int[] yVal = new int[] {-scale, 0, scale};

        g2d.rotate(getAngle() * Math.PI / 180);

        g2d.setColor(Color.BLUE);
        g2d.drawPolygon(xVal, yVal, 3);
        g2d.drawLine(-scale, 0, scale, 0);
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return lives <= 0;
    }
}
