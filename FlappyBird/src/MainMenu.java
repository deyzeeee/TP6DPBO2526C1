import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {

    private final int width = 360;
    private final int height = 640;
    private final Image backgroundImage;

    public MainMenu(Image backgroundImage, Runnable onStart, Runnable onExit) {
        this.backgroundImage = backgroundImage;
        setPreferredSize(new Dimension(width, height));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("FLAPPY BIRD");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = createButton("MULAI");
        startButton.addActionListener(e -> onStart.run());

        JButton exitButton = createButton("KELUAR");
        exitButton.addActionListener(e -> onExit.run());

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(exitButton);
        add(Box.createVerticalGlue());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, width, height, null);
    }
}
