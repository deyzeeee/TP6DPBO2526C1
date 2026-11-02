import javax.swing.*;
import java.awt.*;

public class View extends JPanel {

    private final int width = 360;
    private final int height = 640;

    private final Logic logic;
    private final JLabel scoreLabel;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        setLayout(null);

        // JLabel untuk menampilkan skor
        scoreLabel = new JLabel("Skor: 0") {
            // Bayangan hitam supaya skor tetap terbaca di atas background terang
            @Override
            protected void paintComponent(Graphics g) {
                FontMetrics fm = getFontMetrics(getFont());
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.setFont(getFont());
                g.setColor(Color.BLACK);
                g.drawString(getText(), 2, y + 2);
                super.paintComponent(g);
            }
        };
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(12, 10, 200, 32);
        add(scoreLabel);

        // Fokus dan KeyListener untuk menerima input keyboard
        setFocusable(true);
        addKeyListener(logic);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Skor: " + score);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(logic.getBackgroundImage(), 0, 0, width, height, null);

        // Pipa
        for (Pipe pipe : logic.getPipes()) {
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(),
                    pipe.getWidth(), pipe.getHeight(), null);
        }

        // Player
        Player player = logic.getPlayer();
        if (player != null) {
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(),
                    player.getWidth(), player.getHeight(), null);
        }

        // Teks status
        if (logic.getState() == Logic.State.READY) {
            drawCenteredText(g, "Tekan SPASI untuk mulai", 340, new Font("Arial", Font.BOLD, 20), Color.WHITE);
        } else if (logic.getState() == Logic.State.GAME_OVER) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, width, height);
            drawCenteredText(g, "GAME OVER", 270, new Font("Arial", Font.BOLD, 40), Color.RED);
            drawCenteredText(g, "Skor: " + logic.getScore(), 320, new Font("Arial", Font.BOLD, 26), Color.WHITE);
            drawCenteredText(g, "Tekan R untuk restart", 370, new Font("Arial", Font.PLAIN, 20), Color.YELLOW);
        }
    }

    private void drawCenteredText(Graphics g, String text, int y, Font font, Color color) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.setColor(Color.BLACK); // bayangan
        g.drawString(text, x + 2, y + 2);
        g.setColor(color);
        g.drawString(text, x, y);
    }
}
