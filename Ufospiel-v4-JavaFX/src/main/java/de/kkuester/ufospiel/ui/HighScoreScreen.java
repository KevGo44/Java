package de.kkuester.ufospiel.ui;

import de.kkuester.ufospiel.core.HighScoreStore;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class HighScoreScreen {

    public static VBox build(HighScoreStore store, Runnable onBack) {
        Label title = new Label("HIGHSCORES");
        title.getStyleClass().add("menu-title");

        VBox rows = new VBox(6);
        List<HighScoreStore.Entry> entries = store.getEntries();
        if (entries.isEmpty()) {
            Label empty = new Label("Noch keine Einträge");
            empty.getStyleClass().add("menu-label-dim");
            rows.getChildren().add(empty);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                HighScoreStore.Entry entry = entries.get(i);
                Label rank = new Label(String.format("%2d.", i + 1));
                Label name = new Label(entry.name());
                name.setPrefWidth(160);
                Label score = new Label(String.valueOf(entry.score()));
                HBox row = new HBox(16, rank, name, score);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add(i == 0 ? "highscore-row-top" : "highscore-row");
                rows.getChildren().add(row);
            }
        }

        Button back = new Button("Zurück");
        back.getStyleClass().add("menu-button");
        back.setOnAction(e -> onBack.run());

        VBox panel = new VBox(20, title, rows, back);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(400, -1);
        return panel;
    }
}
