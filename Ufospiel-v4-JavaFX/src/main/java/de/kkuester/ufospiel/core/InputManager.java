package de.kkuester.ufospiel.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public class InputManager {

    private final Set<KeyCode> pressed = EnumSet.noneOf(KeyCode.class);
    private final Settings settings;
    private Consumer<KeyCode> oneShotListener;

    public InputManager(Scene scene, Settings settings) {
        this.settings = settings;
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (oneShotListener != null) {
                Consumer<KeyCode> l = oneShotListener;
                oneShotListener = null;
                l.accept(code);
                return;
            }
            pressed.add(code);
        });
        scene.setOnKeyReleased(e -> pressed.remove(e.getCode()));
    }

    /** Captures the next raw key press (used for rebinding UI) instead of feeding it into gameplay. */
    public void awaitNextKey(Consumer<KeyCode> listener) {
        this.oneShotListener = listener;
    }

    public boolean isActionDown(GameAction action) {
        KeyCode code = settings.getBinding(action);
        return code != null && pressed.contains(code);
    }

    public boolean isKeyDown(KeyCode code) {
        return pressed.contains(code);
    }

    public void clear() {
        pressed.clear();
    }
}
