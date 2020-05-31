package kelly;

import java.awt.*;

/**
 * Encapsulates a DrawableThing Bullet that destroys the Asteroids.
 */
public class Bullet extends DrawableThing {
    private int expirationTime;

    /**
     * Constructs a Bullet.
     * @param timeIndex The time index of the game.
     */
    public Bullet(int timeIndex, int duration) {
        expirationTime = timeIndex + duration;
        setMass(1);
        setRadius(2);
    }

    /**
     * Implements how to draw the Bullet.
     * @param g2d Graphics 2D.
     * @param timeIndex The time index of the game.
     */
    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(Color.YELLOW);
        double[] p = getPosition();
        double[] v = getVelocity();

        int r = (int) getRadius();
        g2d.fillOval(-r, -r, r * 2, r * 2);
    }

    /**
     * Implements when the Bullet should be removed from the field.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the Bullet should be removed at a given time.
     */
    @Override
    public boolean toBeRemoved(int timeIndex) {
        return expirationTime <= timeIndex;
    }
}
