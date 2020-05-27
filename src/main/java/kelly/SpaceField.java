package kelly;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class SpaceField implements KeyListener {
    private static final int LOOP_DELAY = 20;
    private static final double minForce = 0.8;
    private static final double maxForce = 2;
    private int timeIndex;
    private int level;
    private SpaceShip ship;
    private ArrayList<DrawableThing> movingObjects;
    private Set<TimeEventListener> timeEventListeners;
    private int width;
    private int height;

    private int timeForAsteroid;

    private Random random = new Random();

    public SpaceField(int width, int height) {
        timeIndex = 0;
        level = 1;
        ship = new SpaceShip();
        movingObjects = new ArrayList<>();
        timeEventListeners = new HashSet<>();
        this.width = width;
        this.height = height;

        ship.setPosition(new double[] {300, 240});
    }

    public void addTimeEventListener(TimeEventListener l) {
        timeEventListeners.add(l);
    }

    public void publishTimeEvent() {
        for(TimeEventListener l : timeEventListeners) {
            l.onTimeEvent();
        }
    }

    public void startClock() {
        while(true) {
            timeIndex++;
            updateKeyStatus();
            generateAsteroid();

            moveObjects();
            // updateShip();

            // checkShipCollision();
            // checkAsteroidCollision();
            // checkBulletsHitObjects();

            // checkLevelUp();
            // checkGameOver();

            publishTimeEvent();
            safeSleep(LOOP_DELAY);
        }
    }

    private int rotationDirection = 0;
    private void updateKeyStatus() {
        ship.rotate(rotationDirection * 5);
    }

    private void generateBullet() {
        double theta = ship.getAngle() * Math.PI / 180;
        double scale = ship.getScale();
        double speedFactor = 0.4;
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        Bullet bullet = new Bullet(timeIndex);
        double[] p = new double[] {x * scale, y * scale};
        MatrixUtil.addToArray(p, ship.getPosition());
        bullet.setPosition(p);
        bullet.setVelocity(new double[] {x * scale * speedFactor, y * scale * speedFactor});
        movingObjects.add(bullet);
    }

    private void generateAsteroid() {
        if(timeIndex >= timeForAsteroid) {
            double[] position = generateEdgePosition();
            Asteroid asteroid = new Asteroid(3);
            asteroid.setPosition(position);
            asteroid.setVelocity(generateRandomVelocity());
            movingObjects.add(asteroid);
            calculateNextAsteroidDelay();
        }
    }

    private double[] generateRandomVelocity() {
        double force = minForce + (random.nextDouble() * (maxForce - minForce));
        double angle = random.nextInt(360) * Math.PI /180;
        return new double[] {force * Math.cos(angle), force * Math.sin(angle)};
    }

    private void calculateNextAsteroidDelay() {
        timeForAsteroid = timeIndex + levelAsteroidDelay();
    }

    private int levelAsteroidDelay() {
        return 50 * 8;
    }

    private void moveObjects() {
        HashSet<DrawableThing> toBeRemoved = new HashSet<>();
        for(DrawableThing m : movingObjects) {
            if(m.toBeRemoved(timeIndex)) {
                toBeRemoved.add(m);
            } else {
                double[] bounds = new double[] {width, height};
                double[] force = new double[] {0, 0};
                m.update(timeIndex, force, bounds);
            }
        }
        for(DrawableThing m : toBeRemoved) {
            movingObjects.remove(m);
        }
    }

    private double[] generateEdgePosition() {
        double[] position = new double[2];
        double odd = random.nextDouble();
        if(odd < 0.5) {
            position[0] = odd < 0.25 ? 0 : width;
            position[1] = random.nextDouble() * height;
        } else {
            position[0] = random.nextDouble() * width;
            position[1] = odd < 0.75 ? 0 : height;
        }
        return position;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawField(Graphics2D g2d) {
        ship.draw(g2d, timeIndex);
        for(DrawableThing m : movingObjects) {
            m.draw(g2d, timeIndex);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP :
                System.out.println("up held");
                break;
            case KeyEvent.VK_DOWN :
                System.out.println("down held");
                break;
            case KeyEvent.VK_LEFT :
                System.out.println("left held");
                rotationDirection = - 1;
                break;
            case KeyEvent.VK_RIGHT :
                System.out.println("right held");
                rotationDirection = 1;
                break;
            case KeyEvent.VK_SPACE :
                System.out.println("space held");
                generateBullet();
                SoundPlayer.SoundFX.LAZER.playSound();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
//            case KeyEvent.VK_UP :
//                System.out.println("up released");
//                break;
//            case KeyEvent.VK_DOWN :
//                System.out.println("down released");
//                break;
            case KeyEvent.VK_LEFT :
                System.out.println("left released");
                rotationDirection = 0;
                break;
            case KeyEvent.VK_RIGHT :
                System.out.println("right released");
                rotationDirection = 0;
                break;
            case KeyEvent.VK_SPACE :
//                System.out.println("space released");
//                generateBullet();
//                SoundPlayer.SoundFX.LAZER.playSound();
//                break;
        }
    }

    private static void safeSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
