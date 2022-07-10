package jon.com.ua.view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 11/16/13
 */
public class PlayBackground extends Thread {
    private static File soundFile = new File("smb-overworld.wav");
    private static AudioInputStream ais;
    public static Clip clip;

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                ais = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();
                clip.open(ais);

                clip.setFramePosition(0);
                clip.start();

                Thread.sleep(clip.getMicrosecondLength() / 1000);
                clip.stop();
                clip.close();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
