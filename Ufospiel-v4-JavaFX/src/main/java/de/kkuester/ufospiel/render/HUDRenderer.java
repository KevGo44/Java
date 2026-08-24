package de.kkuester.ufospiel.render;

import de.kkuester.ufospiel.entities.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class HUDRenderer {

    private static class Popup {
        double x, y, life, maxLife;
        String text;
        Color color;
    }

    private final List<Popup> popups = new ArrayList<>();
    private String bannerText;
    private double bannerTimer;
    private final Font hudFont;
    private final Font titleFont;

    public HUDRenderer(Font hudFont, Font titleFont) {
        this.hudFont = hudFont;
        this.titleFont = titleFont;
    }

    public void addPopup(double x, double y, String text, Color color) {
        Popup p = new Popup();
        p.x = x;
        p.y = y;
        p.life = p.maxLife = 0.9;
        p.text = text;
        p.color = color;
        popups.add(p);
    }

    public void showBanner(String text) {
        bannerText = text;
        bannerTimer = 2.2;
    }

    public void update(double dt) {
        for (int i = popups.size() - 1; i >= 0; i--) {
            Popup p = popups.get(i);
            p.life -= dt;
            p.y -= 24 * dt;
            if (p.life <= 0) {
                popups.remove(i);
            }
        }
        if (bannerTimer > 0) {
            bannerTimer -= dt;
        }
    }

    public void render(GraphicsContext gc, double width, int score, int combo, int wave, Player player) {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(javafx.geometry.VPos.TOP);

        gc.setFont(hudFont != null ? Font.font(hudFont.getFamily(), FontWeight.BOLD, 20) : Font.font("Consolas", FontWeight.BOLD, 20));
        gc.setFill(Color.rgb(255, 220, 120));
        gc.fillText("SCORE " + score, width - 220, 20);
        if (combo > 1) {
            gc.setFill(Color.rgb(255, 140, 90));
            gc.fillText("COMBO x" + combo, width - 220, 46);
        }

        gc.setFill(Color.rgb(160, 220, 255));
        gc.fillText("WELLE " + wave, 20, 20);

        for (int i = 0; i < player.lives; i++) {
            drawLifeIcon(gc, 20 + i * 26, 50);
        }
        for (int i = 0; i < player.maxShieldSegments; i++) {
            Color c = i < player.shieldSegments ? Color.rgb(90, 200, 255) : Color.rgb(60, 70, 90, 0.5);
            gc.setStroke(c);
            gc.setLineWidth(3);
            gc.strokeOval(20 + i * 20, 78, 14, 14);
        }

        StringBuilder weapons = new StringBuilder();
        if (player.rapidFireTimer > 0) weapons.append("RAPID ");
        if (player.spreadShotTimer > 0) weapons.append("SPREAD ");
        if (player.homingMissiles > 0) weapons.append("MISSILES:").append(player.homingMissiles).append(' ');
        if (player.scoreMultiplierTimer > 0) weapons.append("x2-SCORE ");
        if (weapons.length() > 0) {
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
            gc.setFill(Color.rgb(200, 230, 255, 0.9));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(weapons.toString().trim(), width / 2.0, 20);
        }

        gc.setTextAlign(TextAlignment.CENTER);
        for (Popup p : popups) {
            double alpha = Math.max(0, p.life / p.maxLife);
            gc.setGlobalAlpha(alpha);
            gc.setFill(p.color);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
            gc.fillText(p.text, p.x, p.y);
            gc.setGlobalAlpha(1.0);
        }

        if (bannerTimer > 0) {
            double alpha = Math.min(1, bannerTimer);
            gc.setGlobalAlpha(alpha);
            gc.setFont(titleFont != null ? Font.font(titleFont.getFamily(), FontWeight.BOLD, 42) : Font.font("Consolas", FontWeight.BOLD, 42));
            gc.setFill(Color.rgb(150, 220, 255));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(bannerText, width / 2.0, 140);
            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawLifeIcon(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(120, 220, 255));
        double[] xs = {x, x + 16, x};
        double[] ys = {y - 8, y, y + 8};
        gc.fillPolygon(xs, ys, 3);
    }
}
