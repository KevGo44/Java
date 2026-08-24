package de.kkuester.pentagonquest3d.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Musik-Loop (pentagonradio.wav, echte Audiodatei) + prozedural synthetisierte SFX
 * (gleiches abhängigkeitsfreie PCM-Verfahren wie in der Python-Version
 * pentagonquest/audio.py, hier über javax.sound.sampled statt pygame.mixer).
 */
public class SoundManager {

    public enum Sfx {
        HIT, HURT, STEP_A, STEP_B, SWING, BLOCK, DODGE, PICKUP, LEVEL_UP, CLICK, VICTORY, DEATH
    }

    private static final float SAMPLE_RATE = 22050f;
    private static final String MUSIC_PATH = "/de/kkuester/pentagonquest3d/material/pentagonradio.wav";

    private final AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
    private final Map<Sfx, byte[]> sfxData = new EnumMap<>(Sfx.class);
    private final Random random = new Random();
    private Clip musicClip;
    private boolean ready;
    private double sfxVolume = 0.8;

    public void init() {
        try {
            buildAllSfx();
            ready = true;
        } catch (Exception e) {
            System.err.println("SFX-Synthese fehlgeschlagen: " + e.getMessage());
        }
        try {
            URL url = getClass().getResource(MUSIC_PATH);
            if (url != null) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                musicClip = AudioSystem.getClip();
                musicClip.open(stream);
            }
        } catch (Exception e) {
            System.err.println("Musik konnte nicht geladen werden: " + e.getMessage());
        }
    }

    public void playMusicLoop() {
        if (musicClip != null) {
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        if (musicClip != null) musicClip.stop();
    }

    public void setMusicVolume(double linear) {
        applyGain(musicClip, linear);
    }

    public void setSfxVolume(double linear) {
        sfxVolume = linear;
    }

    public void play(Sfx sfx) {
        if (!ready) return;
        byte[] data = sfxData.get(sfx);
        if (data == null) return;
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(data), format,
                    data.length / format.getFrameSize());
            clip.open(stream);
            applyGain(clip, sfxVolume);
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.start();
        } catch (Exception ignored) {
            // ein verlorener Sound-Effekt ist nicht kritisch
        }
    }

    private static void applyGain(Clip clip, double linear) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        double clamped = Math.max(0.0001, Math.min(1.0, linear));
        float dB = (float) (20.0 * Math.log10(clamped));
        control.setValue(Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB)));
    }

    // ── Prozedurale PCM-Synthese ─────────────────────────────

    private void buildAllSfx() {
        sfxData.put(Sfx.HIT, toPcm(fade(mix(
                sine(420, 0.09, 0.35, 140.0), noise(0.06, 0.18, 1)), 0.005, 0.03)));
        sfxData.put(Sfx.HURT, toPcm(fade(mix(
                sine(140, 0.16, 0.4, 70.0), noise(0.10, 0.15, 1)), 0.005, 0.08)));
        sfxData.put(Sfx.STEP_A, toPcm(fade(noise(0.05, 0.22, 2), 0.005, 0.03)));
        sfxData.put(Sfx.STEP_B, toPcm(fade(noise(0.05, 0.20, 3), 0.005, 0.03)));
        sfxData.put(Sfx.SWING, toPcm(fade(noise(0.10, 0.16, 2), 0.005, 0.05)));
        sfxData.put(Sfx.BLOCK, toPcm(fade(mix(
                sine(260, 0.08, 0.3, 180.0), noise(0.05, 0.2, 1)), 0.005, 0.04)));
        sfxData.put(Sfx.DODGE, toPcm(fade(noise(0.14, 0.12, 3), 0.02, 0.08)));
        sfxData.put(Sfx.PICKUP, toPcm(fade(concat(
                sine(523, 0.07, 0.3, null), silence(0.02), sine(784, 0.09, 0.32, null)), 0.005, 0.03)));
        sfxData.put(Sfx.LEVEL_UP, toPcm(fade(concat(
                sine(523, 0.09, 0.28, null), sine(659, 0.09, 0.28, null),
                sine(784, 0.09, 0.28, null), sine(1046, 0.16, 0.32, null)), 0.005, 0.05)));
        sfxData.put(Sfx.CLICK, toPcm(fade(sine(900, 0.03, 0.2, null), 0.002, 0.015)));
        sfxData.put(Sfx.VICTORY, toPcm(fade(mix(
                sine(523, 1.3, 0.20, null), sine(659, 1.3, 0.16, null), sine(784, 1.3, 0.16, null)),
                0.02, 0.5)));
        sfxData.put(Sfx.DEATH, toPcm(fade(sine(200, 1.5, 0.28, 55.0), 0.02, 0.6)));
    }

    private float[] sine(double freq, double duration, double volume, Double sweepTo) {
        int n = (int) (SAMPLE_RATE * duration);
        float[] out = new float[n];
        double phase = 0;
        for (int i = 0; i < n; i++) {
            double f = sweepTo == null ? freq : freq + (sweepTo - freq) * (i / (double) Math.max(1, n - 1));
            phase += 2 * Math.PI * f / SAMPLE_RATE;
            out[i] = (float) (Math.sin(phase) * volume);
        }
        return out;
    }

    private float[] noise(double duration, double volume, int smoothPasses) {
        int n = (int) (SAMPLE_RATE * duration);
        float[] out = new float[n];
        for (int i = 0; i < n; i++) out[i] = (float) ((random.nextDouble() * 2 - 1) * volume);
        for (int p = 0; p < smoothPasses; p++) {
            float[] smoothed = new float[n];
            for (int i = 0; i < n; i++) smoothed[i] = i > 0 ? (out[i] + out[i - 1]) / 2f : out[i];
            out = smoothed;
        }
        return out;
    }

    private float[] silence(double duration) {
        return new float[(int) (SAMPLE_RATE * duration)];
    }

    private float[] fade(float[] samples, double fadeIn, double fadeOut) {
        int n = samples.length;
        int fi = Math.min(n, (int) (fadeIn * SAMPLE_RATE));
        int fo = Math.min(n, (int) (fadeOut * SAMPLE_RATE));
        float[] out = samples.clone();
        for (int i = 0; i < fi; i++) out[i] *= i / (float) Math.max(1, fi);
        for (int i = 0; i < fo; i++) out[n - 1 - i] *= i / (float) Math.max(1, fo);
        return out;
    }

    private float[] mix(float[]... tracks) {
        int len = 0;
        for (float[] t : tracks) len = Math.max(len, t.length);
        float[] out = new float[len];
        for (float[] t : tracks) {
            for (int i = 0; i < t.length; i++) out[i] += t[i];
        }
        return out;
    }

    private float[] concat(float[]... tracks) {
        int len = 0;
        for (float[] t : tracks) len += t.length;
        float[] out = new float[len];
        int pos = 0;
        for (float[] t : tracks) {
            System.arraycopy(t, 0, out, pos, t.length);
            pos += t.length;
        }
        return out;
    }

    private byte[] toPcm(float[] samples) {
        byte[] bytes = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            int clamped = (int) Math.max(-32768, Math.min(32767, samples[i] * 32767));
            bytes[i * 2] = (byte) (clamped & 0xFF);
            bytes[i * 2 + 1] = (byte) ((clamped >> 8) & 0xFF);
        }
        return bytes;
    }
}
