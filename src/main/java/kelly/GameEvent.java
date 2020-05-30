package kelly;

/**
 * Object that holds the game event information to be sent out to the GameEventListeners.
 */
public class GameEvent {
    private int lives;
    private int score;
    private int level;
    private int shields;

    /**
     * Constructs the event that is to be sent to the GameEventListeners.
     * @param lives The number of lives of the SpaceShip.
     * @param score The score of the game.
     * @param level The level of the player of the game.
     * @param shields The number of shields available.
     */
    public GameEvent(int lives, int score, int level, int shields) {
        this.lives = lives;
        this.score = score;
        this.level = level;
        this.shields = shields;
    }

    /**
     * Returns the number of lives passed into the GameEvent as a String.
     * @return String of the number of lives.
     */
    public String livesAsString() {
        return Integer.toString(lives);
    }

    /**
     * Returns the score passed into the GameEvent as a String.
     * @return String of the score.
     */
    public String scoreAsString() {
        return Integer.toString(score);
    }

    /**
     * Returns the level passed into the GameEvent as a String.
     * @return String of the level.
     */
    public String levelAsString() {
        return Integer.toString(level);
    }

    /**
     * Returns the number of shields passed into the GameEvent as a String.
     * @return String of the number of shields.
     */
    public String shieldsAsString() {
        return Integer.toString(shields);
    }

    /**
     * Implements the toString method to return the game event as a String.
     * @return String of the game event details.
     */
    @Override
    public String toString() {
        return "GameEvent{" +
            "lives=" + lives +
            ", score=" + score +
            ", level=" + level +
            ", shields=" + shields +
            '}';
    }
}
