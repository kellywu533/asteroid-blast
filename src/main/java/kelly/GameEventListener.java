package kelly;

/**
 * Interface that should be implemented by classes that wish to receive game events.
 */
public interface GameEventListener {
    void onGameEvent(GameEvent e);
}
