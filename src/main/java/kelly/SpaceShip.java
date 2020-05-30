package kelly;

import java.awt.*;

public class SpaceShip extends DrawableThing {
    private int scale;
    private int lives;

    private static final int TIME_SCALE = 1000 / SpaceField.LOOP_DELAY;
    public int incubationTime;
    private int invincibleTime;

    public SpaceShip() {
        incubationTime = -1;
        invincibleTime = (TIME_SCALE * 6);
        angle = 270;
        scale = 8;
    }

    public int getScale() {
        return scale;
    }

    public void rotate(int angle) {
        setAngle(getAngle() + angle);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getInvincibleTime() {
        return invincibleTime;
    }

    public boolean checkIncubation(int timeIndex) {
        if(timeIndex > incubationTime) {
            incubationTime = -1;
        }
        return incubationTime >= 0;
    }

    public void changeIncubate(int timeIndex) {
        incubationTime = timeIndex + (TIME_SCALE * 2);
    }

    public boolean checkInvincible(int timeIndex) {
        if(timeIndex > invincibleTime) {
            invincibleTime = -1;
        }
        return invincibleTime >= 0;
    }

    public void changeInvincible(int timeIndex) {
        invincibleTime = timeIndex + (TIME_SCALE * 8);
    }

    public boolean timeForSound(int timeIndex) {
        return timeIndex == incubationTime;
    }

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        if(incubationTime < 0) {
            if(invincibleTime > timeIndex) {
                if (timeIndex % 50 < 42) {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawOval(-scale * 2, -scale * 2, scale * 4, scale * 4);
                }
                g2d.setColor(Color.GRAY);
            } else {
                g2d.setColor(Color.WHITE);
            }
            int[] xVal = new int[] {-scale, scale, -scale};
            int[] yVal = new int[] {-scale, 0, scale};

            g2d.rotate(getAngle() * Math.PI / 180);

            g2d.drawPolygon(xVal, yVal, 3);
            g2d.drawLine(-scale, 0, scale, 0);
        }
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return lives <= 0;
    }
}
