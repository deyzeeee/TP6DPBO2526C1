import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Logic logika = new Logic();
        View tampilan = new View(logika); // supaya view bisa komunikasi dengan logic
        logika.setView(tampilan);

        // CardLayout: pindah antara Main Menu dan layar game
        CardLayout cards = new CardLayout();
        JPanel container = new JPanel(cards);

        MainMenu menu = new MainMenu(
                logika.getBackgroundImage(),
                () -> {
                    cards.show(container, "GAME");
                    logika.newGame();
                    SwingUtilities.invokeLater(tampilan::requestFocusInWindow);
                },
                () -> System.exit(0)
        );

        container.add(menu, "MENU");
        container.add(tampilan, "GAME");

        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
