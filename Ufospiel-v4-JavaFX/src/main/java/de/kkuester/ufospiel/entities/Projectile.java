package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile extends Entity {

    public enum Kind {
        PLAYER_LASER(false), PLAYER_SPREAD(false), PLAYER_HOMING(false), ENEMY_BOLT(true), BOSS_BOLT(true);

        public final boolean hostileToPlayer;

        Kind(boolean hostileToPlayer) {
            this.hostileToPlayer = hostileToPlayer;
        }
    }

    public final Kind kind;
    public double life;
    public int damage;
    private final double turnRateRadPerSec = 3.2;

    public Projectile(Kind kind, double x, double y, double angleRad, double speed) {
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.rotation = angleRad;
        this.vx = Math.cos(angleRad) * speed;
        this.vy = Math.sin(angleRad) * speed;
        this.life = 2.2;
        switch (kind) {
            case PLAYER_LASER -> {
                radius = 5;
                damage = 1;
            }
            case PLAYER_SPREAD -> {
                radius = 4;
                damage = 1;
            }
            case PLAYER_HOMING -> {
                radius = 6;
                damage = 2;
                life = 3.5;
            }
            case ENEMY_BOLT -> {
                radius = 5;
                damage = 1;
            }
            case BOSS_BOLT -> {
                radius = 7;
                damage = 1;
            }
        }
    }

    public boolean isPlayerOwned() {
        return !kind.hostileToPlayer;
    }

    public void update(double dt, Entity homingTarget) {
        if (kind == Kind.PLAYER_HOMING && homingTarget != null && !homingTarget.dead) {
            double desired = Math.atan2(homingTarget.y - y, homingTarget.x - x);
            double diff = normalizeAngle(desired - rotation);
            double maxTurn = turnRateRadPerSec * dt;
            if (Math.abs(diff) < maxTurn) {
                rotation = desired;
            } else {
                rotation += Math.signum(diff) * maxTurn;
            }
            double speed = Math.hypot(vx, vy);
            vx = Math.cos(rotation) * speed;
            vy = Math.sin(rotation) * speed;
        }
        x += vx * dt;
        y += vy * dt;
        life -= dt;
        if (life <= 0) {
            dead = true;
        }
    }

    private static double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(rotation));
        Color core;
        double length;
        double width;
        switch (kind) {
            case PLAYER_LASER -> {
                core = Color.rgb(120, 220, 255);
                length = 22;
                width = 4;
            }
            case PLAYER_SPREAD -> {
                core = Color.rgb(150, 255, 210);
                length = 16;
                width = 3.5;
            }
            case PLAYER_HOMING -> {
                core = Color.rgb(255, 200, 90);
                length = 14;
                width = 6;
            }
            case BOSS_BOLT -> {
                core = Color.rgb(255, 90, 200);
                length = 16;
                width = 7;
            }
            default -> {
                core = Color.rgb(255, 90, 90);
                length = 14;
                width = 4;
            }
        }
        gc.setGlobalAlpha(0.35);
        gc.setFill(core);
        gc.fillOval(-length * 0.7, -width * 1.4, length * 1.4, width * 2.8);
        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.WHITE.interpolate(core, 0.35));
        gc.fillOval(-length / 2, -width / 2, length, width);
        gc.restore();
    }
}
