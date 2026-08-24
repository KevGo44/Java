package de.kkuester.pentagonquest3d.render;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.Player;
import de.kkuester.pentagonquest3d.world.DungeonBuilder;
import de.kkuester.pentagonquest3d.world.DungeonLayout;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/** Canvas-basiertes 2D-HUD über der 3D-SubScene: Leben/Ausdauer, Fadenkreuz, Minimap, Schadenszahlen. */
public class HudOverlay {

    private record Popup(double x, double y, String text, Color color, double[] life, double maxLife) {
    }

    private final Canvas canvas;
    private final List<Popup> popups = new ArrayList<>();
    private String bannerText;
    private double bannerTimer;

    public HudOverlay(double width, double height) {
        this.canvas = new Canvas(width, height);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void addDamagePopup(String text, Color color) {
        double x = canvas.getWidth() / 2 + (Math.random() - 0.5) * 60;
        double y = canvas.getHeight() / 2 - 20;
        double life = 0.8;
        popups.add(new Popup(x, y, text, color, new double[]{life}, life));
    }

    public void showBanner(String text) {
        bannerText = text;
        bannerTimer = 2.2;
    }

    public void update(double dt) {
        popups.removeIf(p -> {
            p.life()[0] -= dt;
            return p.life()[0] <= 0;
        });
        if (bannerTimer > 0) {
            bannerTimer -= dt;
        }
    }

    public void render(Player player, List<Monster> monsters, DungeonLayout layout) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        drawCrosshair(gc, w, h);
        drawBars(gc, player, w, h);
        drawMinimap(gc, player, monsters, layout, w, h);
        drawPopups(gc);

        if (bannerTimer > 0) {
            gc.setGlobalAlpha(Math.min(1, bannerTimer));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.rgb(255, 210, 120));
            gc.setFont(Font.font("Serif", FontWeight.BOLD, 40));
            gc.fillText(bannerText, w / 2, 110);
            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawCrosshair(GraphicsContext gc, double w, double h) {
        double cx = w / 2, cy = h / 2;
        gc.setStroke(Color.rgb(255, 255, 255, 0.75));
        gc.setLineWidth(1.5);
        gc.strokeLine(cx - 8, cy, cx - 3, cy);
        gc.strokeLine(cx + 3, cy, cx + 8, cy);
        gc.strokeLine(cx, cy - 8, cx, cy - 3);
        gc.strokeLine(cx, cy + 3, cx, cy + 8);
    }

    private void drawBars(GraphicsContext gc, Player player, double w, double h) {
        double barW = 260, barH = 20;
        double x = 24, hpY = h - 74, stY = h - 46;

        drawBar(gc, x, hpY, barW, barH, player.hp / (double) player.maxHp,
                Color.rgb(60, 10, 10), Color.rgb(200, 40, 40),
                "HP " + player.hp + "/" + player.maxHp);
        drawBar(gc, x, stY, barW, barH, player.stamina / player.maxStamina,
                Color.rgb(10, 40, 10), Color.rgb(60, 180, 90),
                "AUSDAUER");

        gc.setFill(Color.rgb(255, 215, 130));
        gc.setFont(Font.font("Serif", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Lv " + player.level + "   XP " + player.xp + "/" + player.xpNeeded(), x, h - 92);
        if (player.equippedWeapon != null) {
            gc.setFont(Font.font("Serif", 13));
            gc.fillText("⚔ " + player.equippedWeapon.name, x + barW + 20, hpY + 14);
        }
    }

    private void drawBar(GraphicsContext gc, double x, double y, double w, double h, double ratio,
                          Color bg, Color fg, String label) {
        ratio = Math.max(0, Math.min(1, ratio));
        gc.setFill(bg);
        gc.fillRoundRect(x, y, w, h, 6, 6);
        gc.setFill(fg);
        gc.fillRoundRect(x, y, w * ratio, h, 6, 6);
        gc.setStroke(Color.rgb(255, 255, 255, 0.6));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, w, h, 6, 6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(label, x + w / 2, y + h / 2 + 1);
        gc.setTextBaseline(VPos.BASELINE);
    }

    private void drawMinimap(GraphicsContext gc, Player player, List<Monster> monsters, DungeonLayout layout,
                              double w, double h) {
        double size = 170, pad = 16;
        double mx = w - size - pad, my = pad;
        gc.setFill(Color.rgb(8, 8, 14, 0.75));
        gc.fillRoundRect(mx, my, size, size, 10, 10);
        gc.setStroke(Color.rgb(200, 170, 100));
        gc.strokeRoundRect(mx, my, size, size, 10, 10);

        double worldSize = DungeonLayout.SIZE * DungeonBuilder.ROOM_SIZE;
        double scale = (size - 16) / worldSize;
        double ox = mx + 8, oy = my + 8;

        gc.setFill(Color.rgb(150, 40, 40, 0.85));
        for (Monster m : monsters) {
            if (m.isDead()) continue;
            double dx = ox + m.x * scale;
            double dz = oy + m.z * scale;
            gc.fillOval(dx - 2, dz - 2, 4, 4);
        }

        double px = ox + player.x * scale;
        double pz = oy + player.z * scale;
        gc.setFill(Color.rgb(120, 220, 255));
        gc.fillOval(px - 3, pz - 3, 6, 6);
        double yawRad = Math.toRadians(player.yawDeg);
        gc.setStroke(Color.rgb(120, 220, 255));
        gc.setLineWidth(2);
        gc.strokeLine(px, pz, px + Math.sin(yawRad) * 9, pz + Math.cos(yawRad) * 9);
    }

    private void drawPopups(GraphicsContext gc) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Serif", FontWeight.BOLD, 20));
        for (Popup p : popups) {
            double alpha = Math.max(0, p.life()[0] / p.maxLife());
            gc.setGlobalAlpha(alpha);
            gc.setFill(p.color());
            gc.fillText(p.text(), p.x(), p.y() - (1 - alpha) * 30);
        }
        gc.setGlobalAlpha(1.0);
    }
}
