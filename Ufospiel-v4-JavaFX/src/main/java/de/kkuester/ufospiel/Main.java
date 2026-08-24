package de.kkuester.ufospiel;

import de.kkuester.ufospiel.audio.SoundManager;
import de.kkuester.ufospiel.core.GameAction;
import de.kkuester.ufospiel.core.GameLoop;
import de.kkuester.ufospiel.core.GameState;
import de.kkuester.ufospiel.core.HighScoreStore;
import de.kkuester.ufospiel.core.InputManager;
import de.kkuester.ufospiel.core.Settings;
import de.kkuester.ufospiel.ui.GameOverScreen;
import de.kkuester.ufospiel.ui.HighScoreScreen;
import de.kkuester.ufospiel.ui.MainMenuScreen;
import de.kkuester.ufospiel.ui.PauseOverlay;
import de.kkuester.ufospiel.ui.SettingsScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    private static final double WIDTH = 1280;
    private static final double HEIGHT = 720;

    private GameLoop gameLoop;
    private Settings settings;
    private InputManager input;
    private SoundManager sound;
    private HighScoreStore highScores;
    private StackPane uiLayer;
    private Canvas canvas;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        Font titleFont = loadFont("/de/kkuester/ufospiel/fonts/Orbitron-Bold.ttf", 32);
        Font hudFont = titleFont != null ? titleFont : Font.font("Consolas");

        settings = new Settings();
        highScores = new HighScoreStore();
        sound = new SoundManager();
        sound.init();
        sound.setMusicVolume(settings.getMusicVolume());
        sound.setSfxVolume(settings.getSfxVolume());

        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        uiLayer = new StackPane();
        uiLayer.setPickOnBounds(false);

        StackPane gameRoot = new StackPane(canvas, uiLayer);
        gameRoot.setPrefSize(WIDTH, HEIGHT);
        gameRoot.setMinSize(WIDTH, HEIGHT);
        gameRoot.setMaxSize(WIDTH, HEIGHT);

        StackPane outer = new StackPane(gameRoot);
        outer.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(outer, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/de/kkuester/ufospiel/styles/game.css").toExternalForm());

        input = new InputManager(scene, settings);

        gameLoop = new GameLoop(gc, WIDTH, HEIGHT, input, settings, sound, hudFont, titleFont, this::onGameOver);
        gameLoop.start();

        scene.widthProperty().addListener((obs, ov, nv) -> rescale(scene, gameRoot));
        scene.heightProperty().addListener((obs, ov, nv) -> rescale(scene, gameRoot));

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == settings.getBinding(GameAction.PAUSE)) {
                if (gameLoop.state == GameState.PLAYING) {
                    showPause();
                } else if (gameLoop.state == GameState.PAUSED) {
                    hidePauseAndResume();
                }
            }
        });

        showMenu();

        stage.setTitle("Ufospiel");
        stage.setScene(scene);
        stage.setFullScreen(settings.isFullscreen());
        stage.setOnCloseRequest(e -> {
            gameLoop.stop();
            sound.stopMusic();
        });
        stage.show();
        canvas.requestFocus();

        sound.playMusicLoop();
    }

    private void rescale(Scene scene, StackPane gameRoot) {
        double scaleX = scene.getWidth() / WIDTH;
        double scaleY = scene.getHeight() / HEIGHT;
        double scale = Math.max(0.1, Math.min(scaleX, scaleY));
        gameRoot.setScaleX(scale);
        gameRoot.setScaleY(scale);
    }

    private Font loadFont(String resourcePath, double size) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            return Font.loadFont(in, size);
        } catch (Exception e) {
            return null;
        }
    }

    private void showMenu() {
        gameLoop.state = GameState.MENU;
        uiLayer.getChildren().setAll(MainMenuScreen.build(
                this::startGame, this::showSettingsFromMenu, this::showHighscoresFromMenu, Platform::exit));
    }

    private void startGame() {
        uiLayer.getChildren().clear();
        gameLoop.startNewGame();
        canvas.requestFocus();
    }

    private void showSettingsFromMenu() {
        uiLayer.getChildren().setAll(SettingsScreen.build(settings, input, sound, stage, this::showMenu));
    }

    private void showHighscoresFromMenu() {
        uiLayer.getChildren().setAll(HighScoreScreen.build(highScores, this::showMenu));
    }

    private void showPause() {
        gameLoop.state = GameState.PAUSED;
        uiLayer.getChildren().setAll(PauseOverlay.build(
                this::hidePauseAndResume, this::showSettingsFromPause, this::quitToMenuFromPause));
    }

    private void hidePauseAndResume() {
        uiLayer.getChildren().clear();
        gameLoop.state = GameState.PLAYING;
        canvas.requestFocus();
    }

    private void showSettingsFromPause() {
        uiLayer.getChildren().setAll(SettingsScreen.build(settings, input, sound, stage, this::showPause));
    }

    private void quitToMenuFromPause() {
        input.clear();
        showMenu();
    }

    private void onGameOver(int score, int wave) {
        uiLayer.getChildren().setAll(GameOverScreen.build(score, wave, highScores, this::showMenu));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
