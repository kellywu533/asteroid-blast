package kelly;

import java.awt.*;

public class Banner extends DrawableThing {
    private String text;
    private int size;
    private int expiration;

    public Banner(String text, int size, int expiration) {
        this.text = text;
        this.size = size;
        this.expiration = expiration;
    }

    @Override
    protected void drawThing(Graphics2D g2d, int timeIndex) {
        g2d.setColor(Color.WHITE);
        Font f = g2d.getFont();
        Font bigger = new Font(f.getName(), Font.BOLD, size);
        g2d.setFont(bigger);
        g2d.drawString(text, 0, 0);
        g2d.setFont(f);
    }

    @Override
    public boolean toBeRemoved(int timeIndex) {
        return expiration < timeIndex;
    }
}
