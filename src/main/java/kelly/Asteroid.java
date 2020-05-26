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
        setRadius(asteroidLevelRadius[asteroidLevel]);
        setMass(asteroidLevelMass[asteroidLevel]);
    }

    public int getAsteroidLevel() {
        return asteroidLevel;
    }

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(Color.RED);

        int r = (int) getRadius();

        g2d.rotate(timeIndex * Math.PI / 360);
        g2d.drawRect( -r,  -r, r * 2, r * 2);
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return asteroidLevel < 0;
    }
}
