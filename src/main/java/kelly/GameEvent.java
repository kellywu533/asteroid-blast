package kelly;

public class GameEvent {
    private int lives;
    private int score;
    private int level;

    public GameEvent(int lives, int score, int level) {
        this.lives = lives;
        this.score = score;
        this.level = level;
    }

    public String livesAsString() {
        return Integer.toString(lives);
    }

    public String scoreAsString() {
        return Integer.toString(score);
    }

    public String levelAsString() {
        return Integer.toString(level);
    }

    @Override
    public String toString() {
        return "GameEvent{" +
            "lives=" + lives +
            ", score=" + score +
            ", level=" + level +
            '}';
    }
}
