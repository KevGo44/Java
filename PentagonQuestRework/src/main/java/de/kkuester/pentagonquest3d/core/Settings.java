package de.kkuester.pentagonquest3d.core;

import java.util.prefs.Preferences;

public class Settings {

    private final Preferences prefs = Preferences.userNodeForPackage(Settings.class);
    private double musicVolume;
    private double sfxVolume;
    private boolean fullscreen;

    public Settings() {
        load();
    }

    public void load() {
        musicVolume = prefs.getDouble("musicVolume", 0.55);
        sfxVolume = prefs.getDouble("sfxVolume", 0.8);
        fullscreen = prefs.getBoolean("fullscreen", false);
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double v) {
        musicVolume = clamp01(v);
        prefs.putDouble("musicVolume", musicVolume);
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(double v) {
        sfxVolume = clamp01(v);
        prefs.putDouble("sfxVolume", sfxVolume);
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean f) {
        fullscreen = f;
        prefs.putBoolean("fullscreen", f);
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
