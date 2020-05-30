package kelly;

import java.awt.*;

/**
 * Encapsulates a DrawableThing SpaceShip that the player controls
 */
public class SpaceShip extends DrawableThing {
    public static final int NORTH_ANGLE = 270;
    private int scale;
    private int lives;
    private int shields;

    private static final int TIME_SCALE = 1000 / SpaceField.LOOP_DELAY;
    public int incubationTime;
    private int invincibleTime;

    /**
     * Constructs a SpaceShip with an incubation and invincible time.
     */
    public SpaceShip() {
        incubationTime = -1;
        invincibleTime = (TIME_SCALE * 6);
        angle = NORTH_ANGLE;
        scale = 8;
    }

    /**
     * Gets the scale of the size of the SpaceShip.
     * @return Integer size of the SpaceShip.
     */
    public int getScale() {
        return scale;
    }

    /**
     * Rotates the SpaceShip a specified number of degrees.
     * @param angle The angle that the SpaceShip rotates.
     */
    public void rotate(int angle) {
        setAngle(getAngle() + angle);
    }

    /**
     * Gets the number of lives of the SpaceShip.
     * @return Integer number of lives of the SpaceShip.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Sets the number of lives of the SpaceShip.
     * @param lives The number of lives set to the SpaceShip.
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }

    /**
     * Gets the time index at which the SpaceShip is invincible until.
     * @return Integer time index at which the SpaceShip loses invincibility.
     */
    public int getInvincibleTime() {
        return invincibleTime;
    }

    /**
     * Checks if the time index of the game is greater than the time at which the incubation is to end.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the game is at the time index at which the SpaceShip is out of incubation.
     */
    public boolean checkIncubation(int timeIndex) {
        if(timeIndex > incubationTime) {
            incubationTime = -1;
        }
        return incubationTime >= 0;
    }

    /**
     * Changes the end of the new incubation time after the SpaceShip has been destroyed.
     * @param timeIndex The time index of the game.
     */
    public void changeIncubate(int timeIndex) {
        incubationTime = timeIndex + (TIME_SCALE * 2);
    }

    /**
     * Checks if the time index of the game is greater than the time at which the invincibility is to end.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the game is at the time index at which the SpaceShip is out of invincibility mode.
     */
    public boolean checkInvincible(int timeIndex) {
        if(timeIndex > invincibleTime) {
            invincibleTime = -1;
        }
        return invincibleTime >= 0;
    }

    /**
     * Changes the end of the new invincibility time after the SpaceShip has been destroyed.
     * @param timeIndex The time index of the game.
     */
    public void changeInvincible(int timeIndex) {
        invincibleTime = timeIndex + (TIME_SCALE * 8);
    }

    /**
     * Checks if it is the appropriate time index to queue the new SpaceShip sound.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the game is at the time index to play the sound.
     */
    public boolean timeForSound(int timeIndex) {
        return timeIndex == incubationTime;
    }

    /**
     * Implements how to draw the SpaceShip.
     * @param g2d Graphics 2D.
     * @param timeIndex The time index of the game.
     */
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

    /**
     * Implements when the SpaceShip is to be removed from the field.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the SpaceShip should be removed at a given time.
     */
    @Override
    public boolean toBeRemoved(int timeIndex) {
        return lives <= 0;
    }
}
