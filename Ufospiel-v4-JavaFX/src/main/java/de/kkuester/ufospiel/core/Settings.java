package de.kkuester.ufospiel.core;

import javafx.scene.input.KeyCode;

import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class Settings {

    private static final Map<GameAction, KeyCode> DEFAULT_BINDINGS = new EnumMap<>(GameAction.class);
    static {
        DEFAULT_BINDINGS.put(GameAction.UP, KeyCode.W);
        DEFAULT_BINDINGS.put(GameAction.DOWN, KeyCode.S);
        DEFAULT_BINDINGS.put(GameAction.LEFT, KeyCode.A);
        DEFAULT_BINDINGS.put(GameAction.RIGHT, KeyCode.D);
        DEFAULT_BINDINGS.put(GameAction.FIRE, KeyCode.SPACE);
        DEFAULT_BINDINGS.put(GameAction.PAUSE, KeyCode.Q);
    }

    private final Preferences prefs = Preferences.userNodeForPackage(Settings.class);
    private final Map<GameAction, KeyCode> bindings = new EnumMap<>(GameAction.class);

    private double musicVolume;
    private double sfxVolume;
    private Difficulty difficulty;
    private boolean fullscreen;

    public Settings() {
        load();
    }

    public void load() {
        for (GameAction action : GameAction.values()) {
            String stored = prefs.get("key_" + action.name(), DEFAULT_BINDINGS.get(action).name());
            KeyCode code;
            try {
                code = KeyCode.valueOf(stored);
            } catch (IllegalArgumentException e) {
                code = DEFAULT_BINDINGS.get(action);
            }
            bindings.put(action, code);
        }
        musicVolume = prefs.getDouble("musicVolume", 0.6);
        sfxVolume = prefs.getDouble("sfxVolume", 0.8);
        fullscreen = prefs.getBoolean("fullscreen", false);
        String diffName = prefs.get("difficulty", Difficulty.NORMAL.name());
        try {
            difficulty = Difficulty.valueOf(diffName);
        } catch (IllegalArgumentException e) {
            difficulty = Difficulty.NORMAL;
        }
    }

    public KeyCode getBinding(GameAction action) {
        return bindings.get(action);
    }

    public void setBinding(GameAction action, KeyCode code) {
        bindings.put(action, code);
        prefs.put("key_" + action.name(), code.name());
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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty d) {
        difficulty = d;
        prefs.put("difficulty", d.name());
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
