package kelly;

import javax.swing.*;

public class GameStatusPanel extends JPanel {
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

        field.addTimeEventListener(() -> {
            timeValue.setText(Integer.toString(field.getGameEndTime()));
        });

        field.addGameEventListener(e -> {
            levelValue.setText(e.levelAsString());
            scoreValue.setText(e.scoreAsString());
            livesValue.setText(e.livesAsString());
        });

    }
}
