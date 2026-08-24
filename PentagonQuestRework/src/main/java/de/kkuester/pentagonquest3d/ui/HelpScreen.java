package de.kkuester.pentagonquest3d.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HelpScreen {

    public static VBox build(Runnable onClose) {
        Label title = new Label("HILFE & STEUERUNG");
        title.getStyleClass().add("menu-title");

        String[] lines = {
                "WASD - Bewegen (frei, keine Zellensprünge)",
                "Maus - Umschauen",
                "Linksklick - Angreifen",
                "Rechtsklick (halten) - Blocken",
                "Leertaste - Ausweichrolle (kostet Ausdauer)",
                "E - Inventar öffnen/schließen",
                "Q / Esc - Pause",
                "",
                "Kampf: Nahkampf mit Reichweite + Blickkegel vor der Kamera.",
                "Ausdauer regeneriert automatisch, außer beim Blocken.",
                "Level-Up: mehr Max-HP und Angriffskraft, HP wird aufgefüllt.",
                "Ziel: kämpfe dich zum Ork-König im hintersten Raum vor!",
        };

        VBox content = new VBox(6);
        content.setAlignment(Pos.CENTER_LEFT);
        for (String line : lines) {
            Label l = new Label(line);
            l.getStyleClass().add(line.isEmpty() ? "menu-label-dim" : "menu-label");
            content.getChildren().add(l);
        }

        Button close = new Button("Schließen");
        close.getStyleClass().add("menu-button");
        close.setOnAction(e -> onClose.run());

        VBox panel = new VBox(20, title, content, close);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(480, -1);
        return panel;
    }
}
