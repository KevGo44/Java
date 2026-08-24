package de.kkuester.ufospiel.ui;

import de.kkuester.ufospiel.core.HighScoreStore;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameOverScreen {

    public static VBox build(int score, int wave, HighScoreStore store, Runnable onBackToMenu) {
        Label title = new Label("GAME OVER");
        title.getStyleClass().add("menu-title");

        Label scoreLabel = new Label("Punkte: " + score);
        scoreLabel.getStyleClass().add("menu-subtitle");
        Label waveLabel = new Label("Erreichte Welle: " + wave);
        waveLabel.getStyleClass().add("menu-label");

        VBox content = new VBox(14, scoreLabel, waveLabel);
        content.setAlignment(Pos.CENTER);

        if (store.qualifies(score)) {
            Label prompt = new Label("Neuer Highscore! Name eingeben:");
            prompt.getStyleClass().add("menu-label");
            TextField nameField = new TextField();
            nameField.getStyleClass().add("menu-textfield");
            nameField.setPromptText("Pilot");
            nameField.setMaxWidth(220);

            Button save = new Button("Speichern");
            save.getStyleClass().add("menu-button");
            Button back = new Button("Zurück zum Menü");
            back.getStyleClass().add("menu-button");
            back.setDisable(false);

            save.setOnAction(e -> {
                store.addScore(nameField.getText().trim(), score);
                save.setDisable(true);
                nameField.setDisable(true);
                prompt.setText("Gespeichert!");
            });
            back.setOnAction(e -> onBackToMenu.run());

            HBox saveRow = new HBox(10, nameField, save);
            saveRow.setAlignment(Pos.CENTER);
            content.getChildren().addAll(prompt, saveRow, back);
        } else {
            Button back = new Button("Zurück zum Menü");
            back.getStyleClass().add("menu-button");
            back.setOnAction(e -> onBackToMenu.run());
            content.getChildren().add(back);
        }

        VBox panel = new VBox(20, title, content);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(440, -1);
        return panel;
    }
}
