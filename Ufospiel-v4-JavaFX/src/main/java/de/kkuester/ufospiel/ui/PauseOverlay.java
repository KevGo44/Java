package de.kkuester.ufospiel.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PauseOverlay {

    public static VBox build(Runnable onResume, Runnable onSettings, Runnable onMainMenu) {
        Label title = new Label("PAUSE");
        title.getStyleClass().add("menu-title");

        Button resume = new Button("Fortsetzen");
        resume.getStyleClass().add("menu-button");
        resume.setOnAction(e -> onResume.run());

        Button settings = new Button("Einstellungen");
        settings.getStyleClass().add("menu-button");
        settings.setOnAction(e -> onSettings.run());

        Button mainMenu = new Button("Hauptmenü");
        mainMenu.getStyleClass().addAll("menu-button", "menu-button-danger");
        mainMenu.setOnAction(e -> onMainMenu.run());

        VBox buttons = new VBox(14, resume, settings, mainMenu);
        buttons.setAlignment(Pos.CENTER);

        VBox panel = new VBox(24, title, buttons);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(380, -1);
        return panel;
    }
}
