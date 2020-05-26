package kelly;

import java.awt.*;

public class Bullet extends DrawableThing {
    private int expirationTime;

    public Bullet(int timeIndex) {
        expirationTime = timeIndex + 200;
        setMass(1);
        setRadius(2);
    }

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(Color.BLACK);
        double[] p = getPosition();
        double[] v = getVelocity();

        int r = (int) getRadius();
        g2d.fillOval(-r, -r, r * 2, r * 2);
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return expirationTime <= timeIndex;
    }
}
