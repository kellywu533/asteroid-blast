package kelly;

import java.awt.*;

/**
 * Encapsulates a DrawableThing Banner that notifies the player of a given message.
 */
public class Banner extends DrawableThing {
    private String text;
    private int size;
    private int expiration;

    /**
     * Constructs a Banner with the given text, size, and expiration time.
     * @param text Message to be displayed.
     * @param size The size of the text.
     * @param expiration The scheduled time to remove the Banner.
     */
    public Banner(String text, int size, int expiration) {
        this.text = text;
        this.size = size;
        this.expiration = expiration;
    }

    /**
     * Implements how to draw the Banner.
     * @param g2d Graphics 2D.
     * @param timeIndex The time index of the game.
     */
    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(Color.WHITE);
        Font f = g2d.getFont();
        Font bigger = new Font(f.getName(), Font.BOLD, size);
        g2d.setFont(bigger);
        g2d.drawString(text, 0, 0);
        g2d.setFont(f);
    }

    /**
     * Implements when the Banner should be removed from the field.
     * @param timeIndex The time index of the game.
     * @return Boolean whether the Banner should be removed at a given time.
     */
    @Override
    public boolean toBeRemoved(int timeIndex) {
        return expiration < timeIndex;
    }
}
