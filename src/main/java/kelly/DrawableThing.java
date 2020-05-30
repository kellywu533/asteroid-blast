package kelly;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

/**
 * Abstract class that is drawable on screen. It contains a set of physics attributes of the object.
 */
public abstract class DrawableThing {
    private double[] position = new double[2];
    private double[] velocity = new double[2];
    protected double radius = 1;
    protected double mass = 1;
    protected double angle = 0;

    /**
     * Updates the object so that it may move.
     * @param bounds Bounds of the field.
     * @param force Force to be applied to the object.
     * @param frictionFactor Friction factor to be applied to the velocity of the object.
     */
    public void update(double[] bounds, double[] force, double frictionFactor) {
        for(int i = 0; i < position.length; i++) {
            double a = force == null ? 0 : force[i] / mass;
            double v = velocity[i] + a;
            double p = position[i] + v;

            if (p >= bounds[i]) {
                p = 0;
            } else if (p <= 0) {
                p = bounds[i];
            } else {
                p = position[i] + v;
            }

            velocity[i] = v * frictionFactor;
            position[i] = p;
        }
    }

    /**
     * The main entry point that draws the DrawableThing.
     * This method will translate the canvas to the given position and angle
     * and restore the original translation after the object is drawn.
     * @param g2d Graphics 2D.
     * @param timeIndex The time index of the game.
     */
    public final void draw(Graphics2D g2d, int timeIndex) {
        AffineTransform savePoint = g2d.getTransform();
        g2d.translate(position[0], position[1]);
        drawThing(g2d, timeIndex);
        g2d.setTransform(savePoint);
    }

    /**
     * Implements how to draw the specific object.
     * @param g2d Graphics 2D
     * @param timeIndex The time index of the game.
     */
    protected abstract void drawThing(Graphics2D g2d, int timeIndex);

    /**
     * Implements when the object is to be removed from the game field.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the object should be removed from the field at the given time index.
     */
    public abstract boolean toBeRemoved(int timeIndex);

    /**
     * Gets the position of the object.
     * @return The position of the object.
     */
    public synchronized double[] getPosition() {
        return position;
    }

    /**
     * Sets the position of the object.
     * The position vector will be mutated by the update method.
     * @param position The position that is set to the object.
     */
    public synchronized void setPosition(double[] position) {
        this.position = position;
    }

    /**
     * Gets the velocity of object.
     * @return The velocity of the object.
     */
    public synchronized double[] getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of the object.
     * The velocity vector will be mutated by the update method.
     * @param velocity The velocity that is set to the object.
     */
    public synchronized void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    /**
     * Gets the radius of the object for collision detection and sizing.
     * @return The radius of the object.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the object used for collision detection.
     * @param radius The radius that is set to the object.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Sets the mass of the object for calculating acceleration.
     * @param mass The mass that is set to the object.
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Gets the angle heading of the object for rotating the object.
     * @return The heading of the object.
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the heading of the object.
     * @param angle The heading angle that is set to the object.
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "DrawableThing{" +
                "position=" + Arrays.toString(position) +
                ", velocity=" + Arrays.toString(velocity) +
                ", radius=" + radius +
                ", mass=" + mass +
                ", angle=" + angle +
                "} " +
                super.toString();
    }
}
