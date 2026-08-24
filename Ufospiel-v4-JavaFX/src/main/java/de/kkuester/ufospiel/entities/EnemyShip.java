package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.Random;

public class EnemyShip extends Entity {

    public int hitPoints = 2;
    public static final int SCORE_VALUE = 150;
    private double fireTimer;
    private double driftChangeTimer;
    private final Random random = new Random();

    public EnemyShip(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 22;
        double angle = random.nextDouble() * Math.PI * 2;
        double speed = 70 + random.nextDouble() * 40;
        vx = Math.cos(angle) * speed;
        vy = Math.sin(angle) * speed;
        fireTimer = 1.0 + random.nextDouble();
    }

    /** Returns true if it wants to fire this frame (caller spawns the projectile then calls resetFireTimer). */
    public boolean update(double dt, double arenaW, double arenaH, double speedFactor) {
        x += vx * dt * speedFactor;
        y += vy * dt * speedFactor;
        wrapAround(arenaW, arenaH);
        rotation = Math.atan2(vy, vx);

        driftChangeTimer -= dt;
        if (driftChangeTimer <= 0) {
            driftChangeTimer = 2 + random.nextDouble() * 2;
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 70 + random.nextDouble() * 40;
            vx = Math.cos(angle) * speed;
            vy = Math.sin(angle) * speed;
        }

        fireTimer -= dt;
        return fireTimer <= 0;
    }

    public void resetFireTimer() {
        fireTimer = 1.4 + random.nextDouble() * 1.2;
    }

    public double angleToward(double tx, double ty) {
        return Math.atan2(ty - y, tx - x);
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(rotation));

        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setGlobalAlpha(0.45);
        gc.setFill(Color.rgb(255, 90, 60));
        double[] gx = {-16, 18, -16};
        double[] gy = {-18, 0, 18};
        gc.fillPolygon(gx, gy, 3);
        gc.restore();

        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.rgb(255, 60, 60));
        double[] xs = {-12, 14, -12};
        double[] ys = {-13, 0, 13};
        gc.fillPolygon(xs, ys, 3);
        gc.setFill(Color.rgb(30, 10, 10));
        gc.fillOval(-4, -3, 6, 6);

        gc.restore();
    }
}
