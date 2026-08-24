package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class Player extends Entity {

    private static final double ACCELERATION = 950;
    private static final double MAX_SPEED = 420;
    private static final double DRAG = 2.6;
    private static final double BASE_FIRE_COOLDOWN = 0.20;
    private static final double HOMING_FIRE_INTERVAL = 0.9;

    public int lives = 3;
    public int shieldSegments = 3;
    public final int maxShieldSegments = 3;

    public double invulnerableTimer = 0;
    public double hitFlashTimer = 0;

    public double bankAngle = 0;

    private double fireCooldown = 0;
    private double homingCooldown = 0;

    public double rapidFireTimer = 0;
    public double spreadShotTimer = 0;
    public double shieldPowerTimer = 0;
    public double scoreMultiplierTimer = 0;
    public int homingMissiles = 0;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 20;
    }

    public void thrust(double dx, double dy, double dt) {
        double len = Math.hypot(dx, dy);
        if (len > 0.001) {
            vx += (dx / len) * ACCELERATION * dt;
            vy += (dy / len) * ACCELERATION * dt;
        }
    }

    public void update(double dt, double arenaW, double arenaH) {
        double speed = Math.hypot(vx, vy);
        if (speed > MAX_SPEED) {
            vx = vx / speed * MAX_SPEED;
            vy = vy / speed * MAX_SPEED;
        }
        vx *= Math.max(0, 1 - DRAG * dt);
        vy *= Math.max(0, 1 - DRAG * dt);
        x += vx * dt;
        y += vy * dt;
        x = clamp(x, radius, arenaW - radius);
        y = clamp(y, radius, arenaH - radius);

        double targetBank = clamp(-vx / MAX_SPEED, -1, 1);
        bankAngle += (targetBank - bankAngle) * Math.min(1, dt * 8);

        if (fireCooldown > 0) fireCooldown -= dt;
        if (homingCooldown > 0) homingCooldown -= dt;
        if (invulnerableTimer > 0) invulnerableTimer -= dt;
        if (hitFlashTimer > 0) hitFlashTimer -= dt;
        if (rapidFireTimer > 0) rapidFireTimer -= dt;
        if (spreadShotTimer > 0) spreadShotTimer -= dt;
        if (shieldPowerTimer > 0) shieldPowerTimer -= dt;
        if (scoreMultiplierTimer > 0) scoreMultiplierTimer -= dt;
    }

    public boolean canFire() {
        return fireCooldown <= 0;
    }

    public void resetFireCooldown() {
        fireCooldown = rapidFireTimer > 0 ? BASE_FIRE_COOLDOWN * 0.4 : BASE_FIRE_COOLDOWN;
    }

    public boolean canFireHoming() {
        return homingMissiles > 0 && homingCooldown <= 0;
    }

    public void resetHomingCooldown() {
        homingCooldown = HOMING_FIRE_INTERVAL;
    }

    public boolean isInvulnerable() {
        return invulnerableTimer > 0 || shieldPowerTimer > 0;
    }

    public boolean takeHit() {
        if (isInvulnerable()) {
            return false;
        }
        hitFlashTimer = 0.35;
        if (shieldSegments > 0) {
            shieldSegments--;
            invulnerableTimer = 0.9;
            return false;
        }
        lives--;
        invulnerableTimer = 1.6;
        return true;
    }

    public void resetForNewLife(double arenaW, double arenaH) {
        x = arenaW / 2.0;
        y = arenaH / 2.0;
        vx = 0;
        vy = 0;
        shieldSegments = maxShieldSegments;
        invulnerableTimer = 2.0;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(90 + Math.toDegrees(bankAngle) * 0.5);

        boolean blinking = invulnerableTimer > 0 && (((int) (invulnerableTimer * 12)) % 2 == 0);
        double alpha = blinking ? 0.35 : 1.0;

        if (shieldPowerTimer > 0 || shieldSegments > 0) {
            gc.save();
            gc.setGlobalBlendMode(BlendMode.ADD);
            double pulse = 0.55 + 0.25 * Math.sin(System.nanoTime() / 2.0e8);
            gc.setGlobalAlpha(0.18 * pulse * (shieldPowerTimer > 0 ? 1.6 : 1.0));
            gc.setFill(Color.rgb(90, 200, 255));
            gc.fillOval(-radius * 1.6, -radius * 1.6, radius * 3.2, radius * 3.2);
            gc.restore();
        }

        gc.setGlobalAlpha(alpha);
        Color hull = hitFlashTimer > 0 ? Color.rgb(255, 120, 120) : Color.rgb(120, 220, 255);
        Color glow = hull.deriveColor(0, 1, 1, 0.5);

        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(glow);
        double[] gx = {-14, 20, -14, -4, -14};
        double[] gy = {-16, 0, 16, 0, -16};
        gc.fillPolygon(gx, gy, gx.length);
        gc.restore();

        gc.setFill(hull);
        double[] xs = {-10, 16, -10, -2};
        double[] ys = {-11, 0, 11, 0};
        gc.fillPolygon(xs, ys, xs.length);

        gc.setFill(Color.rgb(20, 30, 45));
        gc.fillOval(-2, -4, 8, 8);

        gc.restore();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
