package de.kkuester.pentagonquest3d;

import de.kkuester.pentagonquest3d.assets.ModelLibrary;
import de.kkuester.pentagonquest3d.audio.SoundManager;
import de.kkuester.pentagonquest3d.core.GameLoop;
import de.kkuester.pentagonquest3d.core.GameState;
import de.kkuester.pentagonquest3d.core.HighScoreStore;
import de.kkuester.pentagonquest3d.core.InputManager;
import de.kkuester.pentagonquest3d.core.Settings;
import de.kkuester.pentagonquest3d.render.HudOverlay;
import de.kkuester.pentagonquest3d.ui.GameOverScreen;
import de.kkuester.pentagonquest3d.ui.HelpScreen;
import de.kkuester.pentagonquest3d.ui.HighScoreScreen;
import de.kkuester.pentagonquest3d.ui.InventoryScreen;
import de.kkuester.pentagonquest3d.ui.MainMenuScreen;
import de.kkuester.pentagonquest3d.ui.PauseOverlay;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private static final double WIDTH = 1280;
    private static final double HEIGHT = 720;

    private Stage stage;
    private StackPane uiLayer;
    private SubScene subScene;
    private GameLoop gameLoop;
    private InputManager input;
    private Settings settings;
    private SoundManager sound;
    private HighScoreStore highScores;
    private HudOverlay hud;
    private String playerName = "";

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        settings = new Settings();
        sound = new SoundManager();
        sound.init();
        sound.setSfxVolume(settings.getSfxVolume());
        sound.setMusicVolume(settings.getMusicVolume());
        highScores = new HighScoreStore();

        ModelLibrary models = new ModelLibrary();
        models.loadAll();

        hud = new HudOverlay(WIDTH, HEIGHT);
        hud.getCanvas().setMouseTransparent(true);

        Group sceneRoot3D = new Group();
        sceneRoot3D.getChildren().add(new AmbientLight(Color.rgb(70, 65, 80)));

        subScene = new SubScene(sceneRoot3D, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(4, 3, 6));

        uiLayer = new StackPane();
        uiLayer.setPickOnBounds(false);

        StackPane rootStack = new StackPane(subScene, hud.getCanvas(), uiLayer);
        Scene scene = new Scene(rootStack, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource(
                "/de/kkuester/pentagonquest3d/styles/game.css").toExternalForm());

        input = new InputManager(stage, scene);

        gameLoop = new GameLoop(input, settings, sound, hud, models, this::onGameOver);
        sceneRoot3D.getChildren().add(gameLoop.getWorldRoot());
        sceneRoot3D.getChildren().add(gameLoop.getCameraGroup());
        subScene.setCamera(gameLoop.getCamera());
        gameLoop.start();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGlobalKeys);

        showMenu();

        stage.setTitle("Die PentagonQuest - 3D");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setFullScreen(settings.isFullscreen());
        stage.setOnCloseRequest(e -> {
            gameLoop.stop();
            sound.stopMusic();
        });
        stage.show();
        subScene.requestFocus();
    }

    private void handleGlobalKeys(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.ESCAPE || code == KeyCode.Q) {
            if (gameLoop.state == GameState.PLAYING) {
                showPause();
            } else if (gameLoop.state == GameState.PAUSED) {
                hideOverlayAndResume();
            } else if (gameLoop.state == GameState.INVENTORY || gameLoop.state == GameState.HELP) {
                showPause();
            }
        } else if (code == KeyCode.E) {
            if (gameLoop.state == GameState.PLAYING) {
                showInventory(this::hideOverlayAndResume);
            } else if (gameLoop.state == GameState.INVENTORY) {
                hideOverlayAndResume();
            }
        }
    }

    private void showMenu() {
        gameLoop.state = GameState.MENU;
        input.setMouseLookActive(false);
        input.clear();
        uiLayer.getChildren().setAll(MainMenuScreen.build(
                this::startGame, this::showHelpFromMenu, this::showHighscoresFromMenu, Platform::exit,
                name -> playerName = name, playerName));
    }

    private void startGame() {
        uiLayer.getChildren().clear();
        gameLoop.startNewGame(playerName);
        subScene.requestFocus();
    }

    private void showPause() {
        gameLoop.state = GameState.PAUSED;
        input.setMouseLookActive(false);
        uiLayer.getChildren().setAll(PauseOverlay.build(
                this::hideOverlayAndResume,
                () -> showInventory(this::showPause),
                () -> showHelp(this::showPause),
                this::quitToMenu));
    }

    private void hideOverlayAndResume() {
        uiLayer.getChildren().clear();
        gameLoop.state = GameState.PLAYING;
        input.setMouseLookActive(true);
        subScene.requestFocus();
    }

    private void showInventory(Runnable onClose) {
        gameLoop.state = GameState.INVENTORY;
        input.setMouseLookActive(false);
        uiLayer.getChildren().setAll(InventoryScreen.build(
                gameLoop.getPlayer(), onClose, msg -> { /* könnte künftig als Toast angezeigt werden */ },
                () -> { /* Statuszeile aktualisiert sich beim nächsten rebuild automatisch */ }));
    }

    private void showHelp(Runnable onClose) {
        gameLoop.state = GameState.HELP;
        input.setMouseLookActive(false);
        uiLayer.getChildren().setAll(HelpScreen.build(onClose));
    }

    private void showHelpFromMenu() {
        uiLayer.getChildren().setAll(HelpScreen.build(this::showMenu));
    }

    private void showHighscoresFromMenu() {
        uiLayer.getChildren().setAll(HighScoreScreen.build(highScores, this::showMenu));
    }

    private void quitToMenu() {
        input.clear();
        sound.stopMusic();
        showMenu();
    }

    private void onGameOver(boolean victory, int score, int level) {
        uiLayer.getChildren().setAll(GameOverScreen.build(victory, score, level, highScores, this::showMenu));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
