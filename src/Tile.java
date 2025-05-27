import javax.swing.*;
import java.awt.*;

public class Tile extends JButton {
    private int value;
    private final int xPos, yPos;
    private int angle = 0;

    public Tile(int x, int y) {
        super();

        this.setSize(new Dimension(Board.TILE_SIZE, Board.TILE_SIZE));
        this.setLocation(Board.BOARD_PADDING + (Board.TILE_SIZE + Board.TILE_MARGIN) * x,
                Board.BOARD_PADDING + (Board.TILE_SIZE + Board.TILE_MARGIN) * y);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);

        this.setFont(new Font("", Font.BOLD, 12));

        this.setVisible(true);

        this.xPos = x;
        this.yPos = y;
    }

    public int getXPosition() {
        return xPos;
    }

    public int getYPosition() {
        return yPos;
    }

    public void set(int value) {
        this.value = value;
        this.setText("");

        //this.setBorder(BorderFactory.createLineBorder(new Color((int)(Math.random()*255),
        //        (int)(Math.random()*255), (int)(Math.random()*255)), 5));

        this.angle = (int) (Math.random() * 4) * 90;
        repaint();
    }

    public int get() {
        return this.value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (value != 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            g2d.translate(w / 2, h / 2);
            g2d.rotate(Math.toRadians(angle));

            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            String text = value + "";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            g2d.setColor(getForeground());
            g2d.drawString(text, -textWidth / 2, textHeight / 2 - 2);

            g2d.dispose();
        }
    }
}
