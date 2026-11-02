import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * Pembungkus sederhana untuk memutar file .wav dari folder assets.
 * Kalau audio gagal dimuat, game tetap jalan (tanpa suara).
 */
public class SoundPlayer {
    private Clip clip;

    public SoundPlayer(String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("Audio tidak ditemukan: " + resourcePath);
                return;
            }
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(stream);
        } catch (Exception e) {
            System.err.println("Audio gagal dimuat: " + resourcePath + " (" + e.getMessage() + ")");
            clip = null;
        }
    }

    // Putar sekali dari awal (untuk sound effect)
    public void play() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    // Putar berulang terus (untuk BGM)
    public void loop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
    }
}
