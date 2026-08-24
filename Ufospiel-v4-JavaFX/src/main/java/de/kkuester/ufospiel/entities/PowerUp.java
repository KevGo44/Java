package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PowerUp extends Entity {

    public enum Type {
        SHIELD("S", Color.rgb(90, 200, 255)),
        EXTRA_LIFE("+", Color.rgb(255, 90, 130)),
        SCORE_MULTIPLIER("x2", Color.rgb(255, 210, 90)),
        SPREAD_SHOT("W", Color.rgb(150, 255, 210)),
        HOMING_MISSILES("H", Color.rgb(255, 160, 90)),
        RAPID_FIRE("R", Color.rgb(210, 150, 255));

        public final String icon;
        public final Color color;

        Type(String icon, Color color) {
            this.icon = icon;
            this.color = color;
        }
    }

    public final Type type;
    public double life = 9.0;
    private double bobTime;

    public PowerUp(Type type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.radius = 16;
        this.vy = 40;
        this.bobTime = Math.random() * Math.PI * 2;
    }

    public void update(double dt) {
        bobTime += dt * 3;
        y += vy * dt;
        x += Math.sin(bobTime) * 12 * dt;
        life -= dt;
        if (life <= 0) {
            dead = true;
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y + Math.sin(bobTime) * 4);

        double fade = life < 2 ? Math.max(0.2, life / 2.0) : 1.0;
        gc.setGlobalAlpha(fade);

        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(type.color.deriveColor(0, 1, 1, 0.4));
        gc.fillOval(-radius * 1.5, -radius * 1.5, radius * 3, radius * 3);
        gc.restore();

        gc.setFill(Color.rgb(15, 15, 25, 0.9));
        gc.fillOval(-radius, -radius, radius * 2, radius * 2);
        gc.setStroke(type.color);
        gc.setLineWidth(2.2);
        gc.strokeOval(-radius, -radius, radius * 2, radius * 2);

        gc.setFill(type.color);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText(type.icon, 0, 1);

        gc.restore();
    }
}
