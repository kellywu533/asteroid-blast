package kelly;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

public abstract class DrawableThing {
    private double[] position = new double[2];
    private double[] velocity = new double[2];
    protected double radius = 1;
    protected double mass = 1;
    protected double angle = 0;

    public void update(int timeIndex, double[] force, double[] bounds) {
        for(int i = 0; i < force.length; i++) {
            double a = force[i] / mass;
            double v = velocity[i] + a;
            double p = position[i] + v;

            if (p >= bounds[i]) {
                p = 0;
            } else if (p <= 0) {
                p = bounds[i];
            } else {
                p = position[i] + v;
            }

            velocity[i] = v;
            position[i] = p;
        }
    }

    public final void draw(Graphics2D g2d, int timeIndex) {
        AffineTransform savePoint = g2d.getTransform();
        g2d.translate(position[0], position[1]);
        drawThing(g2d, timeIndex);
        g2d.setTransform(savePoint);
    }

    protected abstract void drawThing(Graphics2D g2d, int timeIndex);

    public abstract boolean toBeRemoved(int timeIndex);

    public synchronized double[] getPosition() {
        return position;
    }

    public synchronized void setPosition(double[] position) {
        this.position = position;
    }

    public synchronized double[] getVelocity() {
        return velocity;
    }

    public synchronized void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getAngle() {
        return angle;
    }

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
