package de.kkuester.ufospiel.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuScreen {

    public static VBox build(Runnable onStart, Runnable onSettings, Runnable onHighscores, Runnable onExit) {
        Label title = new Label("UFOSPIEL");
        title.getStyleClass().add("menu-title");
        Label subtitle = new Label("Neon Arena Shooter");
        subtitle.getStyleClass().add("menu-subtitle");

        Button start = new Button("Start");
        start.getStyleClass().add("menu-button");
        start.setOnAction(e -> onStart.run());

        Button settings = new Button("Einstellungen");
        settings.getStyleClass().add("menu-button");
        settings.setOnAction(e -> onSettings.run());

        Button highscores = new Button("Highscores");
        highscores.getStyleClass().add("menu-button");
        highscores.setOnAction(e -> onHighscores.run());

        Button exit = new Button("Beenden");
        exit.getStyleClass().addAll("menu-button", "menu-button-danger");
        exit.setOnAction(e -> onExit.run());

        Label credit = new Label("Programmed by Kevin Küster");
        credit.getStyleClass().add("menu-label-dim");

        VBox buttons = new VBox(14, start, settings, highscores, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox panel = new VBox(22, title, subtitle, buttons, credit);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(420, -1);
        return panel;
    }
}
