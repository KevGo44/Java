package de.kkuester.pentagonquest3d.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainMenuScreen {

    public static VBox build(Runnable onStart, Runnable onHelp, Runnable onHighscores, Runnable onExit,
                              java.util.function.Consumer<String> onNameChange, String initialName) {
        Label title = new Label("DIE PENTAGON QUEST");
        title.getStyleClass().add("menu-title");
        Label subtitle = new Label("Ein Ego-Dungeon-Action-RPG");
        subtitle.getStyleClass().add("menu-subtitle");

        TextField nameField = new TextField(initialName);
        nameField.setPromptText("Dein Name, Fremder...");
        nameField.setMaxWidth(260);
        nameField.textProperty().addListener((obs, old, val) -> onNameChange.accept(val));

        Button start = new Button("Ins Ödland aufbrechen");
        start.getStyleClass().add("menu-button");
        start.setOnAction(e -> onStart.run());

        Button help = new Button("Hilfe & Steuerung");
        help.getStyleClass().add("menu-button");
        help.setOnAction(e -> onHelp.run());

        Button highscores = new Button("Ruhmeshalle");
        highscores.getStyleClass().add("menu-button");
        highscores.setOnAction(e -> onHighscores.run());

        Button exit = new Button("Beenden");
        exit.getStyleClass().addAll("menu-button", "menu-button-danger");
        exit.setOnAction(e -> onExit.run());

        Label credit = new Label("Original: Kevin Küster  |  3D-Rework: Claude");
        credit.getStyleClass().add("menu-label-dim");

        VBox buttons = new VBox(14, start, help, highscores, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox panel = new VBox(20, title, subtitle, nameField, buttons, credit);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(440, -1);
        return panel;
    }
}
