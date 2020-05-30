package kelly;

import javax.swing.*;

/**
 * Displays the status of the game.
 */
public class GameStatusPanel extends JPanel {

    /**
     * Constructs the GameStatusPanel that shows the time index, player level, player score, and lives of the SpaceShip
     * @param field The game field.
     */
    public GameStatusPanel(SpaceField field) {
        add(new JLabel("Time: "));
        JLabel timeValue = new JLabel();
        add(timeValue);
        add(new JLabel("Level: "));
        JLabel levelValue = new JLabel();
        add(levelValue);
        add(new JLabel("Score: "));
        JLabel scoreValue = new JLabel();
        add(scoreValue);
        add(new JLabel("Lives: "));
        JLabel livesValue = new JLabel();
        add(livesValue);
        add(new JLabel("Shields: "));
        JLabel shieldsValue = new JLabel();
        add(shieldsValue);

        field.addTimeEventListener(() -> {
            timeValue.setText(Integer.toString(field.getGameEndTime()));
        });

        field.addGameEventListener(e -> {
            levelValue.setText(e.levelAsString());
            scoreValue.setText(e.scoreAsString());
            livesValue.setText(e.livesAsString());
            shieldsValue.setText(e.shieldsAsString());
        });
    }
}
