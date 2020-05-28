package kelly;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class SpaceField implements KeyListener {
    public static final int LOOP_DELAY = 20;
    private static final double minForce = 0.8;
    private static final double maxForce = 2;
    private int timeIndex, gameEndTime;
    private int level;
    private int score;
    private SpaceShip ship;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private ArrayList<Banner> banners;
    private Set<TimeEventListener> timeEventListeners;
    private Set<GameEventListener> gameEventListeners;
    private int width;
    private int height;

    private int timeForAsteroid;

    private Random random = new Random();

    public SpaceField(int width, int height) {
        timeEventListeners = new HashSet<>();
        gameEventListeners = new HashSet<>();
        this.width = width;
        this.height = height;

        init();
    }

    public void addTimeEventListener(TimeEventListener l) {
        timeEventListeners.add(l);
    }

    public void addGameEventListener(GameEventListener l) {
        gameEventListeners.add(l);
    }

    private void publishTimeEvent() {
        for(TimeEventListener l : timeEventListeners) {
            l.onTimeEvent();
        }
    }

    private void publishGameEvent() {
        GameEvent e = new GameEvent(ship.getLives(), score, level);
        for(GameEventListener l : gameEventListeners) {
            l.onGameEvent(e);
        }
    }

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

        ship.setPosition(new double[] {300, 240});
        ship.setLives(3);
    }

    private Clip startUpClip = null;

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

    public void runGameLoop() {
        publishGameEvent();
        while(ship.getLives() > 0) {
            timeIndex++;
            checkLevelUp();
            updateKeyStatus();
            generateAsteroid();

            moveObjects(asteroids);
            moveObjects(bullets);

            if(!ship.checkIncubation(timeIndex) && !ship.checkInvincible(timeIndex)) {
                checkShipCollision();
            } else if(ship.timeForSound(timeIndex)) {
                if (startUpClip == null || !startUpClip.isRunning()) {
                    startUpClip = SoundPlayer.SoundFX.STARTUP.playSound();
                }
            }

//            checkAsteroidCollision();
            checkBulletsHitObjects();

            checkScore();

            publishTimeEvent();
            safeSleep(LOOP_DELAY);
        }
    }

    private void runGameOver() {
        gameEndTime = timeIndex;
        publishGameEvent();
        while(ship.getLives() == 0) {
            updateKeyStatus();

            moveObjects(asteroids);
            moveObjects(bullets);
            moveObjects(banners);

            publishTimeEvent();
            timeIndex++;
            safeSleep(LOOP_DELAY);
        }
    }

    private int rotationDirection = 0;
    private void updateKeyStatus() {
        ship.rotate(rotationDirection * 5);
    }

    private void checkLevelUp() {
        if (timeIndex % 500 == 0) {
            Banner b = new Banner("Level Up!", 18, timeIndex + 50);
            b.setPosition(new double[]{20, 20});
            b.setVelocity(new double[]{ 10, 1});
            b.setAngle(0);
            banners.add(b);
        }
    }

    private void checkScore() {
        int l = timeIndex / 250 + 1;
        level = l < 50 ? l : 50;
        publishGameEvent();
    }

    private void checkShipCollision() {
        if(timeIndex > ship.getInvincibleTime()) {
            double[] shipPos = ship.getPosition();
            int sr = ship.getScale();
            for(DrawableThing o : asteroids) {
                double d2 = MatrixUtil.distancedSquare(shipPos, o.getPosition());
                double r = sr + o.getRadius();
                if(d2 <= r * r) {
                    publishGameEvent();

                    ship.changeIncubate(timeIndex);
                    ship.changeInvincible(timeIndex);
                    ship.setLives(ship.getLives() - 1);
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

    private synchronized void checkBulletsHitObjects() {
        Set<Bullet> bulletsToBeRemoved = new HashSet<>();
        Set<Asteroid> asteroidsToBeRemoved = new HashSet<>();
        Set<Asteroid> asteroidsToBeAdded = new HashSet<>();
        for(Bullet b : bullets) {
            for(Asteroid a : asteroids) {
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

    private synchronized void generateBullet() {
        double theta = ship.getAngle() * Math.PI / 180;
        double scale = ship.getScale();
        double speedFactor = 0.4;
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        Bullet bullet = new Bullet(timeIndex);
        double[] p = new double[] {x * scale, y * scale};
        MatrixUtil.addTo(p, ship.getPosition());
        bullet.setPosition(p);
        bullet.setVelocity(new double[] {x * scale * speedFactor, y * scale * speedFactor});
        bullets.add(bullet);
    }

    private synchronized void generateAsteroid() {
        if(timeIndex >= timeForAsteroid) {
            double[] position = generateEdgePosition();
            Asteroid asteroid = generateAsteroid(position, generateRandomVector(), 3);
            asteroids.add(asteroid);
            calculateNextAsteroidDelay();
        }
    }

    private Asteroid generateAsteroid(double[] position, double[] velocity, int level) {
        Asteroid asteroid = new Asteroid(level);
        asteroid.setPosition(position);
        asteroid.setVelocity(velocity);
        return asteroid;
    }

    private double[] generateRandomVector() {
        double force = minForce + (random.nextDouble() * (maxForce - minForce));
        double angle = random.nextInt(360) * Math.PI /180;
        return new double[] {force * Math.cos(angle), force * Math.sin(angle)};
    }

    private void calculateNextAsteroidDelay() {
        timeForAsteroid = timeIndex + levelAsteroidDelay();
    }

    private int levelAsteroidDelay() {
        return 1000 / LOOP_DELAY * 10 / level;
    }

    private synchronized void moveObjects(Collection<? extends DrawableThing> collection) {
        HashSet<DrawableThing> toBeRemoved = new HashSet<>();
        for(DrawableThing m : collection) {
            if(m.toBeRemoved(timeIndex)) {
                toBeRemoved.add(m);
            } else {
                double[] bounds = new double[] {width, height};
                m.update(timeIndex, bounds);
            }
        }
        for(DrawableThing m : toBeRemoved) {
            collection.remove(m);
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

    public int getTimeIndex() {
        return timeIndex;
    }

    public int getGameEndTime() {
        if (gameEndTime > 0) {
            return gameEndTime;
        } else {
            return timeIndex;
        }
    }

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
                int x = (width - fm.stringWidth(gameOver)) / 2;
                int y = height / 2;
                g2d.drawString(gameOver, x, y);
                g2d.setFont(f);
            }
        } else {
            ship.draw(g2d, timeIndex);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP :
//                System.out.println("up held");
                break;
            case KeyEvent.VK_DOWN :
//                System.out.println("down held");
                break;
            case KeyEvent.VK_LEFT :
//                System.out.println("left held");
                rotationDirection = - 1;
                break;
            case KeyEvent.VK_RIGHT :
//                System.out.println("right held");
                rotationDirection = 1;
                break;
            case KeyEvent.VK_SPACE :
//                System.out.println("space held");
                if(!ship.checkIncubation(timeIndex)) {
                    generateBullet();
                    SoundPlayer.SoundFX.LAZER.playSound();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT :
                rotationDirection = 0;
                break;
            case KeyEvent.VK_RIGHT :
                rotationDirection = 0;
                break;
            case KeyEvent.VK_SPACE :
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
