package kelly;

import java.awt.*;

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


    public Asteroid(int asteroidLevel) {
        this.asteroidLevel = asteroidLevel;
        setRadius(asteroidLevelRadius[asteroidLevel]);
        setMass(asteroidLevelMass[asteroidLevel]);
    }

    public int getAsteroidLevel() {
        return asteroidLevel;
    }

    private static Color[] colors = new Color[] {
        Color.CYAN, Color.MAGENTA, Color.PINK, Color.BLUE
    };

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(colors[asteroidLevel]);

        int r = (int) getRadius();

        g2d.rotate(timeIndex * Math.PI / 360);
        g2d.fillOval( -r,  -r, r * 2, r * 2);
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return asteroidLevel < 0;
    }
}
