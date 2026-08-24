package de.kkuester.pentagonquest3d.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PauseOverlay {

    public static VBox build(Runnable onResume, Runnable onInventory, Runnable onHelp, Runnable onMainMenu) {
        Label title = new Label("PAUSE");
        title.getStyleClass().add("menu-title");

        Button resume = new Button("Fortsetzen");
        resume.getStyleClass().add("menu-button");
        resume.setOnAction(e -> onResume.run());

        Button inventory = new Button("Inventar");
        inventory.getStyleClass().add("menu-button");
        inventory.setOnAction(e -> onInventory.run());

        Button help = new Button("Hilfe");
        help.getStyleClass().add("menu-button");
        help.setOnAction(e -> onHelp.run());

        Button mainMenu = new Button("Hauptmenü");
        mainMenu.getStyleClass().addAll("menu-button", "menu-button-danger");
        mainMenu.setOnAction(e -> onMainMenu.run());

        VBox buttons = new VBox(14, resume, inventory, help, mainMenu);
        buttons.setAlignment(Pos.CENTER);

        VBox panel = new VBox(22, title, buttons);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(360, -1);
        return panel;
    }
}
