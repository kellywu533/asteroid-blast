package kelly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * The SpaceField in which the Asteroid Blast game is played.
 */
public class SpaceField implements KeyListener {
    private static final Logger logger = LogManager.getLogger(SpaceField.class);

    public static final int LOOP_DELAY = 20;
    private static final int TICKS_PER_SECOND = 1000 / LOOP_DELAY;
    private static final int LEVEL_UP_DELAY = 1000;
    private static final int BANNER_TICKS = 2 * TICKS_PER_SECOND;
    private static final double minForce = 0.8;
    private static final double maxForce = 2;
    private static final double[] ZEROS = {0, 0};
    private static final double FRICTION = 1;
    private static final double SHIP_ENGINE_FORCE = 0.2;
    private static final double SHIP_FRICTION_FACTOR = 0.98;
    private static final int BANNER_TEXT_SIZE = 18;
    private static final int BANNER_TIME = BANNER_TICKS - 4;
    private int timeIndex, gameEndTime;
    private int level;
    private int score;
    private SpaceShip ship;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private ArrayList<Banner> banners;
    private Set<TimeEventListener> timeEventListeners;
    private Set<GameEventListener> gameEventListeners;
    private double width;
    private double height;
    private double[] fieldBounds;
    private double[] fieldCenter;
    private int timeForAsteroid;

    private Random random = new Random();

    /**
     * Constructs the SpaceField with a given width and height.
     * @param width The width of the field.
     * @param height The height of the field.
     */
    public SpaceField(int width, int height) {
        timeEventListeners = new HashSet<>();
        gameEventListeners = new HashSet<>();
        this.width = width;
        this.height = height;

        init();
    }

    /**
     * Registers the TimeEventListener to receive time events.
     * @param l The TimeEventListener to be added to the set of TimeEventListeners.
     */
    public void addTimeEventListener(TimeEventListener l) {
        timeEventListeners.add(l);
    }

    /**
     * Registers the GameEventListener to receive GameEvents.
     * @param l The GameEventListener to be added to the set of GameEventListeners.
     */
    public void addGameEventListener(GameEventListener l) {
        gameEventListeners.add(l);
    }

    /**
     * Publishes an event to the set of TimeEventListeners.
     */
    private void publishTimeEvent() {
        for(TimeEventListener l : timeEventListeners) {
            l.onTimeEvent();
        }
    }

    /**
     * Publishes an event to the set of GameEventListeners.
     */
    private void publishGameEvent() {
        GameEvent e = new GameEvent(ship.getLives(), score, level, ship.getShields());
        for(GameEventListener l : gameEventListeners) {
            l.onGameEvent(e);
        }
    }

    /**
     * Initializes the values of the member variables of the SpaceField.
     */
    private void init() {
        timeIndex = 0;
        gameEndTime = 0;
        level = 1;
        score = 0;
        timeForAsteroid = 0;
        ship = new SpaceShip();
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        banners = new ArrayList<>();

        updateBounds();
        resetShip();
        ship.setLives(3);
        ship.setShields(3);
    }

    private void updateBounds() {
        fieldBounds = new double[] {width, height};
        fieldCenter = new double[] {width / 2, height / 2};
    }

    private void resetShip() {
        ship.setPosition(MatrixUtil.cloneArray(fieldCenter));
        ship.setAngle(SpaceShip.NORTH_ANGLE);
        ship.setVelocity(MatrixUtil.cloneArray(ZEROS));

        autofireOn = false;
        fireOn = false;
        turnLeft = false;
        turnRight = false;
        forward = false;
    }

    private Clip startUpClip = null;
    private boolean autofireOn = false;
    private boolean fireOn = false;
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private boolean forward = false;

    /**
     * Runs the runGameLoop and runGameOver methods in an endless loop.
     */
    public void runGame() {
        while(true) {
            if(ship.getLives() > 0) {
                startUpClip = SoundPlayer.SoundFX.STARTUP.playSound();
                runGameLoop();
            } else {
                runGameOver();
            }
        }
    }

    /**
     * Runs the game as the SpaceShip still has lives left.
     */
    public void runGameLoop() {
        publishGameEvent();
        while(ship.getLives() > 0) {
            timeIndex++;
            checkLevelUp();
            generateAsteroid();

            if(!ship.checkIncubation(timeIndex) && (fireOn || autofireOn)) {
                generateBullet();
                fireOn = false;
                SoundPlayer.SoundFX.LAZER.playSound();
            }

            moveObjects(asteroids);
            moveObjects(bullets);
            moveObjects(banners);
            moveShip();


//            checkAsteroidCollision();
            checkBulletCollision();

            publishTimeEvent();
            safeSleep(LOOP_DELAY);
        }
    }

    /**
     * Runs the game as the SpaceShip has no lives left.
     */
    private void runGameOver() {
        gameEndTime = timeIndex;
        publishGameEvent();
        while(ship.getLives() == 0) {
            moveObjects(asteroids);
            moveObjects(bullets);
            moveObjects(banners);

            publishTimeEvent();
            timeIndex++;
            safeSleep(LOOP_DELAY);
        }
    }

    /**
     * Moves and rotates the SpaceShip according to the keyboard inputs.
     */
    private void moveShip() {
        if(!ship.checkIncubation(timeIndex)) {
            int rotateDirection = turnLeft && turnRight ? 0
                : turnLeft ? - 1
                : turnRight ? 1
                : 0
                ;
            ship.rotate(rotateDirection * 5);
            if(forward) {
                double angle = ship.getAngle();
                double[] force = calculateVector(angle, SHIP_ENGINE_FORCE);
                ship.update(fieldBounds, force, SHIP_FRICTION_FACTOR);
            } else {
                ship.update(fieldBounds, ZEROS, SHIP_FRICTION_FACTOR);
            }

            if(!ship.checkInvincible(timeIndex)) {
                checkShipCollision();
            }
        } else if(ship.timeForSound(timeIndex)) {
            if (startUpClip == null || !startUpClip.isRunning()) {
                startUpClip = SoundPlayer.SoundFX.STARTUP.playSound();
            }
        }
    }

    /**
     * Creates a Banner with the given message.
     * @param text The message to be displayed on the screen.
     * @return The Banner that is to be displayed on the screen.
     */
    private Banner createBanner(String text) {
        Banner b = new Banner(text, BANNER_TEXT_SIZE, timeIndex + BANNER_TIME);
        b.setPosition(new double[]{0, 20});
        b.setVelocity(new double[]{ width / BANNER_TICKS, 0});
        b.setAngle(0);
        banners.add(b);
        return b;
    }

    /**
     * Checks whether enough time has passed for the player to level up.
     */
    private void checkLevelUp() {
        if (timeIndex % LEVEL_UP_DELAY == 0) {
            if (level < 50) {
                level++;
            }
            publishGameEvent();
            createBanner("Level " + level + "!");
        }
    }

    /**
     * Checks whether any Asteroids have collided with the SpaceShip.
     */
    private void checkShipCollision() {
        if(timeIndex > ship.getInvincibleTime()) {
            double[] shipPos = ship.getPosition();
            int sr = ship.getScale();
            for(DrawableThing o : asteroids) {
                double d2 = MatrixUtil.distancedSquare(shipPos, o.getPosition());
                double r = sr + o.getRadius();
                if(d2 <= r * r) {
                    ship.changeIncubate(timeIndex);
                    ship.changeInvincible(timeIndex);
                    ship.setLives(ship.getLives() - 1);

                    generateExplosion();
                    publishGameEvent();
                    resetShip();
                    SoundPlayer.SoundFX.EXPLOSION1.playSound();
                    return;
                }
            }
        }
    }

//    private void checkAsteroidCollision() {
//        for(int i = 0; i < asteroids.size() - 1; i++) {
//            Asteroid a1 = asteroids.get(i);
//            for(int j = i + 1; j < asteroids.size(); j++) {
//                Asteroid a2 = asteroids.get(j);
//                double d2 = MatrixUtil.distancedSquare(a1.getPosition(), a2.getPosition());
//                double r = a1.getRadius() + a2.getRadius();
//                if(d2 < r * r) {
//
//                }
//            }
//        }
//    }

    /**
     * Checks whether any Bullets have hit Asteroids and removes the Bullets and Asteroids that have collided.
     */
    private synchronized void checkBulletCollision() {
        Set<Bullet> bulletsToBeRemoved = new HashSet<>();
        Set<Asteroid> asteroidsToBeRemoved = new HashSet<>();
        Set<Asteroid> asteroidsToBeAdded = new HashSet<>();
        for(Bullet b : bullets) {
            for(Asteroid a : asteroids) {
                if(asteroidsToBeRemoved.contains(a)) {
                    continue;
                }
                double d2 = MatrixUtil.distancedSquare(b.getPosition(), a.getPosition());
                double r = b.getRadius() + a.getRadius();
                if(d2 < r * r) {
                    score += a.getAsteroidLevel() * 10;
                    publishGameEvent();

                    asteroidsToBeRemoved.add(a);
                    bulletsToBeRemoved.add(b);

                    int level = a.getAsteroidLevel() - 1;
                    if(level >= 0) {
                        for(int i = 0; i < 3; i++) {
                            Asteroid asteroid = generateAsteroid(
                                MatrixUtil.cloneArray(a.getPosition())
                                , generateRandomVector()
                                , level
                            );
                            asteroidsToBeAdded.add(asteroid);
                        }
                    }
                }
            }
        }
        if(!bulletsToBeRemoved.isEmpty()) {
            SoundPlayer.SoundFX.EXPLOSION2.playSound();
            asteroids.removeAll(asteroidsToBeRemoved);
            bullets.removeAll(bulletsToBeRemoved);
            asteroids.addAll(asteroidsToBeAdded);
        }
    }

    /**
     * Generates and shoots a Bullet from the head of the SpaceShip.
     */
    private synchronized void generateBullet() {
        int bulletDuration = 140;
        double speedFactor = 0.4 * ship.getScale();
        createBullet(ship.getPosition(), ship.getAngle(), speedFactor, bulletDuration);
    }

    private Bullet createBullet(double[] position, double angle, double speedFactor, int duration) {
        double theta = angle * Math.PI / 180;
        double scale = ship.getScale();
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        Bullet bullet = new Bullet(timeIndex, duration);
        double[] p = new double[] {x * scale, y * scale};
        MatrixUtil.addTo(p, position);
        bullet.setPosition(p);
        bullet.setVelocity(new double[] {x * speedFactor, y * speedFactor});
        bullets.add(bullet);
        return bullet;
    }

    private void generateExplosion() {
        int debris = random.nextInt(30) + 30;
        for(; debris > 0; debris--) {
            double angle = random.nextDouble() * 360;
            double speedFactor = (random.nextDouble() * 0.4 + 0.1) * ship.getScale();
            int duration = random.nextInt(30) + 30;
            createBullet(ship.getPosition(), angle, speedFactor, duration);
        }
    }

    /**
     * Generates Asteroids at the appropriate time index.
     */
    private synchronized void generateAsteroid() {
        if(timeIndex >= timeForAsteroid) {
            double[] position = generateEdgePosition();
            Asteroid asteroid = generateAsteroid(position, generateRandomVector(), 3);
            asteroids.add(asteroid);
            calculateNextAsteroidDelay();
        }
    }

    /**
     * Generates Asteroids at a given position, velocity, and level.
     * @param position The position of the Asteroid.
     * @param velocity The velocity of the generated Asteroid.
     * @param level The level of the generated Asteroid.
     * @return The Asteroid that is to be drawn on the field.
     */
    private Asteroid generateAsteroid(double[] position, double[] velocity, int level) {
        Asteroid asteroid = new Asteroid(level);
        asteroid.setPosition(position);
        asteroid.setVelocity(velocity);
        return asteroid;
    }

    /**
     * Calculates a vector according to the angle and length that is applied to move the SpaceShip.
     * @param angle The angle of the SpaceShip.
     * @param length The length of the vector.
     * @return The vector that is applied to move the SpaceShip.
     */
    private double[] calculateVector(double angle, double length) {
        double radians =  Math.PI * angle / 180;
        return new double[] {length * Math.cos(radians), length * Math.sin(radians)};
    }

    private double[] generateRandomVector() {
        double length = minForce + (random.nextDouble() * (maxForce - minForce));
        double angle = random.nextDouble() * 360;
        return calculateVector(angle, length);
    }

    /**
     * Calculates the time index at which the next Asteroid should be deployed.
     */
    private void calculateNextAsteroidDelay() {
        timeForAsteroid = timeIndex + levelAsteroidDelay() + TICKS_PER_SECOND / 3;
    }

    /**
     * Calcluates the interval between each Asteroid deployment based on the player's level
     * @return The time index of when the next Asteroid should be deployed.
     */
    private int levelAsteroidDelay() {
        return TICKS_PER_SECOND * 10 / level;
    }

    /**
     * Moves collections of objects within the field.
     * @param collection The collection of objects that is to be moved.
     */
    private synchronized void moveObjects(Collection<? extends DrawableThing> collection) {
        HashSet<DrawableThing> toBeRemoved = new HashSet<>();
        for(DrawableThing m : collection) {
            if(m.toBeRemoved(timeIndex)) {
                toBeRemoved.add(m);
            } else {
                double[] bounds = new double[] {width, height};
                m.update(bounds, ZEROS, FRICTION);
            }
        }
        collection.removeAll(toBeRemoved);
    }

    /**
     * Generates a random position at the edge of the field for the generated Asteroids.
     * @return The generated edge position.
     */
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

    /**
     * Ends the displayed time on the GameStatusPanel once the game is over.
     * @return The time index when the game ended.
     */
    public int getGameEndTime() {
        if (gameEndTime > 0) {
            return gameEndTime;
        } else {
            return timeIndex;
        }
    }

    /**
     * Draws all the components component of the field.
     * @param g2d Graphics 2D.
     */
    public synchronized void drawField(Graphics2D g2d) {
        for(DrawableThing a : asteroids) {
            a.draw(g2d, timeIndex);
        }
        for(DrawableThing b : bullets) {
            b.draw(g2d, timeIndex);
        }

        HashSet<Banner> toBeRemoved = new HashSet<>();
        for(Banner b : banners) {
            b.draw(g2d, timeIndex);
            if (b.toBeRemoved(timeIndex)) {
                toBeRemoved.add(b);
            }
        }
        banners.removeAll(toBeRemoved);

        if(ship.getLives() <= 0) {
            if((timeIndex / 50) % 2 == 0) {
                Font f = g2d.getFont();
                Font bigger = new Font(f.getName(), Font.BOLD, 32);
                g2d.setFont(bigger);
                g2d.setColor(Color.RED);
                String gameOver = "GAME OVER";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (int) (width - fm.stringWidth(gameOver)) / 2;
                int y = (int) height / 2;
                g2d.drawString(gameOver, x, y);
                g2d.setFont(f);
            }
        } else {
            ship.draw(g2d, timeIndex);
        }
    }

    /**
     * Overrides the KeyListener method when a key is typed.
     * @param e The KeyEvent.
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Overrides the KeyListener method when a key is pressed.
     * @param e The KeyEvent.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP :
            case KeyEvent.VK_X :
                logger.debug("up held [{}]  [{}]", e.getKeyCode(), e);
                forward = true;
                break;
            case KeyEvent.VK_DOWN :
                logger.debug("down held [{}]  [{}]", e.getKeyCode(), e);
                break;
            case KeyEvent.VK_LEFT :
                logger.debug("left held [{}]  [{}]", e.getKeyCode(), e);
                turnLeft = true;
                break;
            case KeyEvent.VK_RIGHT :
                logger.debug("right held [{}]  [{}]", e.getKeyCode(), e);
                turnRight = true;
                break;
            case KeyEvent.VK_SPACE :
                logger.debug("space held [{}]  [{}]", e.getKeyCode(), e);
                fireOn = true;
                break;
        }
    }

    /**
     * Overrides the KeyListener method when a key is released.
     * @param e The KeyEvent.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT :
                turnLeft = false;
                break;
            case KeyEvent.VK_RIGHT :
                turnRight = false;
                break;
            case KeyEvent.VK_UP :
            case KeyEvent.VK_X :
                forward = false;
                break;
            case KeyEvent.VK_DOWN :
            case KeyEvent.VK_C :
                int shields = ship.getShields();
                if(shields > 0 && !ship.checkInvincible(timeIndex)) {
                    ship.setShields(shields - 1);
                    ship.changeInvincible(timeIndex);
                    createBanner("Shield Up!");
                    publishGameEvent();
                    SoundPlayer.SoundFX.SHIELD.playSound();
                }
                break;
            case KeyEvent.VK_SPACE :
                fireOn = false;
                break;
            case KeyEvent.VK_A :
            case KeyEvent.VK_Z :
                autofireOn = !autofireOn;
                break;
            case KeyEvent.VK_R :
                init();
                break;
        }
    }

    private static void safeSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
