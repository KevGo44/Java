package de.kkuester.pentagonquest3d.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.Set;

/**
 * WASD-Halte-Status + Maus-Look. Für endloses Umschauen wird der Cursor nach jedem
 * verarbeiteten Frame per {@link Robot} auf die Fenstermitte zurückgesetzt (Standard-
 * Pattern für eine "gefangene" Maus in JavaFX, das es ohne echtes OS-Pointer-Lock nicht gibt).
 */
public class InputManager {

    private final Set<KeyCode> pressed = EnumSet.noneOf(KeyCode.class);
    private final Robot robot = new Robot();
    private final Stage stage;
    private final Scene scene;

    private double lastMouseScreenX;
    private double lastMouseScreenY;
    private double accumDeltaX;
    private double accumDeltaY;
    private boolean ignoreNextDelta;
    private boolean mouseLookActive;

    public InputManager(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;

        scene.setOnKeyPressed(e -> pressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressed.remove(e.getCode()));

        scene.setOnMouseMoved(this::onMouseMoved);
        scene.setOnMouseDragged(this::onMouseMoved);
        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) attackPressed = true;
            if (e.getButton() == MouseButton.SECONDARY) blockHeld = true;
        });
        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) blockHeld = false;
        });
    }

    private volatile boolean attackPressed;
    private volatile boolean blockHeld;

    private void onMouseMoved(javafx.scene.input.MouseEvent e) {
        if (!mouseLookActive) {
            lastMouseScreenX = e.getScreenX();
            lastMouseScreenY = e.getScreenY();
            return;
        }
        if (ignoreNextDelta) {
            ignoreNextDelta = false;
            lastMouseScreenX = e.getScreenX();
            lastMouseScreenY = e.getScreenY();
            return;
        }
        accumDeltaX += e.getScreenX() - lastMouseScreenX;
        accumDeltaY += e.getScreenY() - lastMouseScreenY;
        lastMouseScreenX = e.getScreenX();
        lastMouseScreenY = e.getScreenY();
    }

    public void setMouseLookActive(boolean active) {
        this.mouseLookActive = active;
        if (active) {
            recenterCursor();
        }
    }

    /** Am Ende jedes Frames aufrufen: liefert die aufgelaufene Maus-Bewegung und setzt den Cursor zurück. */
    public double[] consumeMouseDelta() {
        double dx = accumDeltaX;
        double dy = accumDeltaY;
        accumDeltaX = 0;
        accumDeltaY = 0;
        if (mouseLookActive) {
            recenterCursor();
        }
        return new double[]{dx, dy};
    }

    private void recenterCursor() {
        double centerX = stage.getX() + scene.getX() + scene.getWidth() / 2.0;
        double centerY = stage.getY() + scene.getY() + scene.getHeight() / 2.0;
        ignoreNextDelta = true;
        robot.mouseMove(centerX, centerY);
        lastMouseScreenX = centerX;
        lastMouseScreenY = centerY;
    }

    public boolean isDown(KeyCode code) {
        return pressed.contains(code);
    }

    public boolean consumeAttackClick() {
        if (attackPressed) {
            attackPressed = false;
            return true;
        }
        return false;
    }

    public boolean isBlockHeld() {
        return blockHeld;
    }

    public void clear() {
        pressed.clear();
        attackPressed = false;
        blockHeld = false;
    }
}
