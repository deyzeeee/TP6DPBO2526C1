import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Logic implements ActionListener, KeyListener {

    // Tiga kondisi permainan
    public enum State { READY, PLAYING, GAME_OVER }

    // Ukuran frame
    private final int frameWidth = 360;
    private final int frameHeight = 640;

    // Posisi awal & ukuran Player
    private final int playerStartPosX = frameWidth / 8;
    private final int playerStartPosY = frameHeight / 2;
    private final int playerWidth = 42;
    private final int playerHeight = 36;

    // Posisi awal & ukuran Pipe
    private final int pipeStartPosX = frameWidth;
    private final int pipeStartPosY = 0;
    private final int pipeWidth = 64;
    private final int pipeHeight = 512;

    // Parameter fisika / gameplay
    private final int gravity = 1;
    private final int jumpVelocity = -10;
    private final int pipeVelocityX = -4;
    private final int openingSpace = frameHeight / 4; // celah antara pipa atas dan bawah

    // Timer
    private Timer gameLoop;
    private Timer pipesCooldown;

    // Komponen game
    private View view;
    private Image backgroundImage;
    private Image birdImage;
    private Image lowerPipeImage;
    private Image upperPipeImage;
    private Player player;
    private ArrayList<Pipe> pipes;

    // Audio
    private SoundPlayer jumpSound;
    private SoundPlayer pointSound;
    private SoundPlayer hitSound;
    private SoundPlayer bgm;

    private final Random random = new Random();
    private int score;
    private State state;

    public Logic() {
        backgroundImage = loadImage("assets/background.png");
        birdImage = loadImage("assets/bird.png");
        lowerPipeImage = loadImage("assets/lowerPipe.png");
        upperPipeImage = loadImage("assets/upperPipe.png");

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<>();

        jumpSound = new SoundPlayer("assets/jump.wav");
        pointSound = new SoundPlayer("assets/point.wav");
        hitSound = new SoundPlayer("assets/hit.wav");
        bgm = new SoundPlayer("assets/bgm.wav");

        score = 0;
        state = State.READY;

        // Game loop ~60 FPS
        gameLoop = new Timer(1000 / 60, this);

        // Memunculkan pasangan pipa tiap 1,5 detik
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipe();
            }
        });
    }

    private Image loadImage(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Gambar tidak ditemukan: " + path
                    + " (pastikan folder 'assets' ada di dalam src)");
        }
        return new ImageIcon(url).getImage();
    }

    public void setView(View view) {
        this.view = view;
    }

    // ===================== Getter untuk View =====================

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public int getScore() {
        return score;
    }

    public State getState() {
        return state;
    }

    // ===================== Alur permainan =====================

    // Dipanggil saat tombol Mulai ditekan & saat restart (R): kembali ke kondisi awal
    public void newGame() {
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        score = 0;
        state = State.READY;

        pipesCooldown.stop();
        bgm.stop();
        gameLoop.start();

        if (view != null) {
            view.updateScore(score);
            view.repaint();
        }
    }

    // Dipanggil saat SPASI pertama ditekan
    private void beginPlaying() {
        state = State.PLAYING;
        pipesCooldown.restart();
        bgm.loop();
    }

    private void gameOver() {
        state = State.GAME_OVER;
        gameLoop.stop();
        pipesCooldown.stop();
        bgm.stop();
        hitSound.play();
        if (view != null) {
            view.repaint();
        }
    }

    // Membuat sepasang pipa (atas & bawah) dengan tinggi celah acak
    public void placePipe() {
        int randomPosY = (int) (pipeStartPosY - pipeHeight / 4 - random.nextDouble() * (pipeHeight / 2));

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage, pipeVelocityX);
        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + pipeHeight + openingSpace,
                pipeWidth, pipeHeight, lowerPipeImage, pipeVelocityX);

        // Satu pasang pipa = +1 skor, jadi hanya pipa atas yang dihitung
        lowerPipe.setPassed(true);

        pipes.add(upperPipe);
        pipes.add(lowerPipe);
    }

    private boolean isColliding(Player p, Pipe pipe) {
        return p.getPosX() < pipe.getPosX() + pipe.getWidth()
                && p.getPosX() + p.getWidth() > pipe.getPosX()
                && p.getPosY() < pipe.getPosY() + pipe.getHeight()
                && p.getPosY() + p.getHeight() > pipe.getPosY();
    }

    public void move() {
        // Gravitasi + gerak Player
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0)); // tidak boleh keluar batas atas

        // Gerak pipa, cek tabrakan, hitung skor
        for (Pipe pipe : pipes) {
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            if (isColliding(player, pipe)) {
                gameOver();
                return;
            }

            if (!pipe.isPassed() && pipe.getPosX() + pipe.getWidth() < player.getPosX()) {
                pipe.setPassed(true);
                score++;
                pointSound.play();
                if (view != null) {
                    view.updateScore(score);
                }
            }
        }

        // Buang pipa yang sudah keluar layar supaya list tidak membengkak
        pipes.removeIf(pipe -> pipe.getPosX() + pipe.getWidth() < 0);

        // Jatuh ke batas bawah JFrame
        if (player.getPosY() + player.getHeight() >= frameHeight) {
            player.setPosY(frameHeight - player.getHeight());
            gameOver();
        }
    }

    // ===================== Event handler =====================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == State.PLAYING) {
            move();
        }
        if (view != null) {
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            if (state == State.READY) {
                beginPlaying();
            }
            if (state == State.PLAYING) {
                player.setVelocityY(jumpVelocity);
                jumpSound.play();
            }
        } else if (key == KeyEvent.VK_R && state == State.GAME_OVER) {
            newGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
