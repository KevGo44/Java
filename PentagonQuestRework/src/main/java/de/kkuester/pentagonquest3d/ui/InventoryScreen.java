package de.kkuester.pentagonquest3d.ui;

import de.kkuester.pentagonquest3d.entities.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class InventoryScreen {

    public static VBox build(Player player, Runnable onClose, Consumer<String> onMessage, Runnable onChanged) {
        Label title = new Label("Inventar & Ausrüstung");
        title.getStyleClass().add("menu-title");

        Label stats = new Label(statsText(player));
        stats.getStyleClass().add("menu-label");

        VBox itemList = new VBox(8);
        itemList.setPadding(new Insets(6));
        rebuild(itemList, player, onMessage, onChanged, stats);

        ScrollPane scroll = new ScrollPane(itemList);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(320);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button close = new Button("Schließen [E]");
        close.getStyleClass().add("menu-button");
        close.setOnAction(e -> onClose.run());

        VBox panel = new VBox(16, title, stats, scroll, close);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("menu-panel");
        panel.setMaxSize(520, 520);
        return panel;
    }

    private static void rebuild(VBox itemList, Player player, Consumer<String> onMessage, Runnable onChanged,
                                 Label stats) {
        itemList.getChildren().clear();
        if (player.inventory.isEmpty()) {
            Label empty = new Label("Inventar leer.");
            empty.getStyleClass().add("menu-label-dim");
            itemList.getChildren().add(empty);
        }
        for (Item item : java.util.List.copyOf(player.inventory)) {
            itemList.getChildren().add(itemRow(item, player, onMessage, () -> {
                rebuild(itemList, player, onMessage, onChanged, stats);
                stats.setText(statsText(player));
                onChanged.run();
            }));
        }
    }

    private static HBox itemRow(Item item, Player player, Consumer<String> onMessage, Runnable onChanged) {
        String detail = switch (item) {
            case Sword sword -> "Waffe  +" + sword.attackDamage + " ATK";
            case Armor armor -> "Rüstung  +" + armor.defense + " DEF";
            case HPPlus potion -> "Heiltrank  +" + potion.hpPlus + " HP";
            default -> "";
        };
        Label name = new Label((item.equipped ? "★ " : "") + item.name + "  —  " + detail);
        name.getStyleClass().add("menu-label");
        name.setMaxWidth(260);

        HBox row = new HBox(10, name);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("menu-panel");
        row.setStyle("-fx-padding: 8 12 8 12; -fx-background-radius: 6;");
        HBox.setHgrow(name, Priority.ALWAYS);

        if (item instanceof Sword || item instanceof Armor) {
            Button equip = new Button("Ausrüsten");
            equip.getStyleClass().add("menu-button");
            equip.setOnAction(e -> {
                onMessage.accept(player.equip(item));
                onChanged.run();
            });
            row.getChildren().add(equip);
        }
        if (item instanceof HPPlus) {
            Button use = new Button("Benutzen");
            use.getStyleClass().add("menu-button");
            use.setOnAction(e -> {
                onMessage.accept(player.useItem(item));
                onChanged.run();
            });
            row.getChildren().add(use);
        }
        Button drop = new Button("Ablegen");
        drop.getStyleClass().addAll("menu-button", "menu-button-danger");
        drop.setOnAction(e -> {
            player.dropItem(item);
            onMessage.accept("'" + item.name + "' abgelegt.");
            onChanged.run();
        });
        row.getChildren().add(drop);
        return row;
    }

    private static String statsText(Player p) {
        return String.format("Lv %d   HP %d/%d   ATK %d   DEF %d   XP %d/%d",
                p.level, p.hp, p.maxHp, p.attackDamage, p.defense, p.xp, p.xpNeeded());
    }
}
