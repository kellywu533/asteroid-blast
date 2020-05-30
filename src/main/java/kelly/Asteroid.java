package kelly;

import java.awt.*;

/**
 * Encapsulates a DrawableThing Asteroid with different levels and corresponding sizes.
 */
public class Asteroid extends DrawableThing {
    public int asteroidLevel;
    public static final int[] asteroidLevelRadius = {
            5
            , 10
            , 20
            , 40
    };
    public static final double[] asteroidLevelMass = {
            1
            , 2
            , 3
            , 4
    };
    private static Color[] colors = new Color[] {
        Color.CYAN, Color.MAGENTA, Color.PINK, Color.BLUE
    };

    /**
     * Constructs an Asteroid at the given level.
     * @param asteroidLevel The Asteroid's level that represents the Asteroid's size and mass.
     */
    public Asteroid(int asteroidLevel) {
        this.asteroidLevel = asteroidLevel;
        setRadius(asteroidLevelRadius[asteroidLevel]);
        setMass(asteroidLevelMass[asteroidLevel]);
    }

    /**
     * Gets the level of the Asteroid.
     * @return The Asteroid level.
     */
    public int getAsteroidLevel() {
        return asteroidLevel;
    }

    /**
     * Implements how to draw the Asteroid.
     * @param g2d Graphics 2D.
     * @param timeIndex The time index of the game.
     */
    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(colors[asteroidLevel]);

        int r = (int) getRadius();

        g2d.fillOval( -r,  -r, r * 2, r * 2);
    }

    /**
     * Implements when the Asteroid should be removed from the field.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the Asteroid should be removed at a given time.
     */
    @Override
    public boolean toBeRemoved(int timeIndex) {
        return asteroidLevel < 0;
    }
}
