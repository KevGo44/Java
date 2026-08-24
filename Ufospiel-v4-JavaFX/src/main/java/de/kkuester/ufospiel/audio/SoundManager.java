package de.kkuester.ufospiel.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Loads short SFX fully into memory so multiple instances can overlap (unlike
 * the original Sound.java, which reused a single Clip and cut itself off).
 */
public class SoundManager {

    public enum Sfx {
        FIRE("/de/kkuester/ufospiel/material/fire.wav"),
        BANG_SMALL("/de/kkuester/ufospiel/material/bangSmall.wav"),
        BANG_LARGE("/de/kkuester/ufospiel/material/bangLarge.wav");

        final String path;

        Sfx(String path) {
            this.path = path;
        }
    }

    private static final String MUSIC_PATH = "/de/kkuester/ufospiel/material/turrican.wav";

    private final Map<Sfx, CachedClip> cache = new EnumMap<>(Sfx.class);
    private Clip musicClip;
    private double musicVolume = 0.6;
    private double sfxVolume = 0.8;

    private record CachedClip(byte[] data, AudioFormat format) {
    }

    public void init() {
        for (Sfx sfx : Sfx.values()) {
            try {
                cache.put(sfx, loadIntoMemory(sfx.path));
            } catch (Exception e) {
                System.err.println("Konnte Sound nicht laden: " + sfx.path + " (" + e.getMessage() + ")");
            }
        }
        try {
            URL url = getClass().getResource(MUSIC_PATH);
            if (url != null) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                musicClip = AudioSystem.getClip();
                musicClip.open(stream);
                applyGain(musicClip, musicVolume);
            }
        } catch (Exception e) {
            System.err.println("Konnte Musik nicht laden: " + e.getMessage());
        }
    }

    private CachedClip loadIntoMemory(String resourcePath) throws IOException, UnsupportedAudioFileException {
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            throw new IOException("Ressource nicht gefunden: " + resourcePath);
        }
        try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
            byte[] bytes = in.readAllBytes();
            return new CachedClip(bytes, in.getFormat());
        }
    }

    public void play(Sfx sfx) {
        CachedClip cached = cache.get(sfx);
        if (cached == null) {
            return;
        }
        try {
            Clip clip = AudioSystem.getClip();
            InputStream byteStream = new ByteArrayInputStream(cached.data());
            AudioInputStream audioStream = new AudioInputStream(byteStream, cached.format(),
                    cached.data().length / cached.format().getFrameSize());
            clip.open(audioStream);
            applyGain(clip, sfxVolume);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception ignored) {
            // dropped sound effect is not fatal
        }
    }

    public void playMusicLoop() {
        if (musicClip == null) {
            return;
        }
        musicClip.setFramePosition(0);
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
        }
    }

    public void setMusicVolume(double linear) {
        musicVolume = linear;
        if (musicClip != null) {
            applyGain(musicClip, linear);
        }
    }

    public void setSfxVolume(double linear) {
        sfxVolume = linear;
    }

    private static void applyGain(Clip clip, double linear) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        double clamped = Math.max(0.0001, Math.min(1.0, linear));
        float dB = (float) (20.0 * Math.log10(clamped));
        dB = Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB));
        control.setValue(dB);
    }
}
