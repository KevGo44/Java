package de.kkuester.ufospiel.ui;

import de.kkuester.ufospiel.audio.SoundManager;
import de.kkuester.ufospiel.core.Difficulty;
import de.kkuester.ufospiel.core.GameAction;
import de.kkuester.ufospiel.core.InputManager;
import de.kkuester.ufospiel.core.Settings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsScreen {

    private static final GameAction[] ACTIONS = {
            GameAction.UP, GameAction.DOWN, GameAction.LEFT, GameAction.RIGHT, GameAction.FIRE, GameAction.PAUSE
    };
    private static final String[] LABELS = {"Hoch", "Runter", "Links", "Rechts", "Schuss", "Pause"};

    public static VBox build(Settings settings, InputManager input, SoundManager sound, Stage stage, Runnable onBack) {
        Label title = new Label("EINSTELLUNGEN");
        title.getStyleClass().add("menu-title");

        VBox keybindRows = new VBox(6);
        for (int i = 0; i < ACTIONS.length; i++) {
            GameAction action = ACTIONS[i];
            Label name = new Label(LABELS[i]);
            name.getStyleClass().add("menu-label");
            name.setPrefWidth(90);

            Button keyButton = new Button(settings.getBinding(action).getName());
            keyButton.getStyleClass().add("keybind-key");
            keyButton.setOnAction(e -> {
                keyButton.setText("...");
                input.awaitNextKey(code -> {
                    settings.setBinding(action, code);
                    javafx.application.Platform.runLater(() -> keyButton.setText(code.getName()));
                });
            });

            HBox row = new HBox(16, name, keyButton);
            row.getStyleClass().add("keybind-row");
            row.setAlignment(Pos.CENTER_LEFT);
            keybindRows.getChildren().add(row);
        }

        Label musicLabel = new Label("Musik-Lautstärke");
        musicLabel.getStyleClass().add("menu-label");
        Slider musicSlider = new Slider(0, 100, settings.getMusicVolume() * 100);
        musicSlider.getStyleClass().add("menu-slider");
        musicSlider.valueProperty().addListener((obs, old, val) -> {
            double v = val.doubleValue() / 100.0;
            settings.setMusicVolume(v);
            sound.setMusicVolume(v);
        });

        Label sfxLabel = new Label("Effekt-Lautstärke");
        sfxLabel.getStyleClass().add("menu-label");
        Slider sfxSlider = new Slider(0, 100, settings.getSfxVolume() * 100);
        sfxSlider.getStyleClass().add("menu-slider");
        sfxSlider.valueProperty().addListener((obs, old, val) -> {
            double v = val.doubleValue() / 100.0;
            settings.setSfxVolume(v);
            sound.setSfxVolume(v);
        });

        Label diffLabel = new Label("Schwierigkeit");
        diffLabel.getStyleClass().add("menu-label");
        Button diffButton = new Button(settings.getDifficulty().label);
        diffButton.getStyleClass().add("keybind-key");
        diffButton.setOnAction(e -> {
            Difficulty next = settings.getDifficulty().next();
            settings.setDifficulty(next);
            diffButton.setText(next.label);
        });
        HBox diffRow = new HBox(16, diffLabel, diffButton);
        diffRow.setAlignment(Pos.CENTER_LEFT);

        CheckBox fullscreenBox = new CheckBox("Vollbild");
        fullscreenBox.getStyleClass().add("menu-label");
        fullscreenBox.setSelected(settings.isFullscreen());
        fullscreenBox.selectedProperty().addListener((obs, old, val) -> {
            settings.setFullscreen(val);
            stage.setFullScreen(val);
        });

        Button back = new Button("Zurück");
        back.getStyleClass().add("menu-button");
        back.setOnAction(e -> onBack.run());

        VBox content = new VBox(14,
                keybindRows,
                sliderBlock(musicLabel, musicSlider),
                sliderBlock(sfxLabel, sfxSlider),
                diffRow,
                fullscreenBox,
                back);
        content.setAlignment(Pos.CENTER);

        VBox panel = new VBox(18, title, content);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(460, -1);
        return panel;
    }

    private static VBox sliderBlock(Label label, Slider slider) {
        slider.setPrefWidth(300);
        slider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(slider, Priority.ALWAYS);
        VBox box = new VBox(4, label, slider);
        return box;
    }
}
