package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss extends Entity {

    private enum Phase { ENTERING, COOLDOWN, TELEGRAPH, FIRING }

    public static final int SCORE_VALUE = 5000;

    public final int maxHitPoints;
    public int hitPoints;
    private Phase phase = Phase.ENTERING;
    private double phaseTimer = 0;
    private int patternIndex = 0;
    private double time;
    private final Random random = new Random();
    public double entranceTargetY;

    public Boss(double x, double y, int wave) {
        this.x = x;
        this.y = -120;
        this.entranceTargetY = y;
        this.radius = 60;
        this.maxHitPoints = 30 + wave * 6;
        this.hitPoints = maxHitPoints;
    }

    public boolean isTelegraphing() {
        return phase == Phase.TELEGRAPH;
    }

    /** Returns freshly spawned hostile projectiles for this frame (usually empty). */
    public List<Projectile> update(double dt, double playerX, double playerY, double arenaW) {
        time += dt;
        List<Projectile> spawned = new ArrayList<>();

        if (phase == Phase.ENTERING) {
            y += (entranceTargetY - y) * Math.min(1, dt * 1.2);
            if (Math.abs(y - entranceTargetY) < 2) {
                phase = Phase.COOLDOWN;
                phaseTimer = 1.5;
            }
            x = arenaW / 2.0 + Math.sin(time * 0.4) * 80;
            return spawned;
        }

        x = arenaW / 2.0 + Math.sin(time * 0.4) * 120;

        phaseTimer -= dt;
        if (phaseTimer <= 0) {
            switch (phase) {
                case COOLDOWN -> {
                    phase = Phase.TELEGRAPH;
                    phaseTimer = 0.6;
                }
                case TELEGRAPH -> {
                    phase = Phase.FIRING;
                    spawned.addAll(fireCurrentPattern(playerX, playerY));
                    phaseTimer = 0.05;
                }
                case FIRING -> {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 2.2;
                    patternIndex = (patternIndex + 1) % 3;
                }
                default -> {
                }
            }
        }
        return spawned;
    }

    private List<Projectile> fireCurrentPattern(double playerX, double playerY) {
        List<Projectile> result = new ArrayList<>();
        double speed = 210;
        switch (patternIndex) {
            case 0 -> {
                int count = 12;
                for (int i = 0; i < count; i++) {
                    double angle = (Math.PI * 2 / count) * i;
                    result.add(new Projectile(Projectile.Kind.BOSS_BOLT, x, y, angle, speed));
                }
            }
            case 1 -> {
                double base = Math.atan2(playerY - y, playerX - x);
                for (double offset : new double[]{-0.28, 0, 0.28}) {
                    result.add(new Projectile(Projectile.Kind.BOSS_BOLT, x, y, base + offset, speed * 1.15));
                }
            }
            default -> {
                double base = Math.atan2(playerY - y, playerX - x);
                int count = 7;
                double span = Math.PI * 0.55;
                for (int i = 0; i < count; i++) {
                    double angle = base - span / 2 + span * i / (count - 1) + (random.nextDouble() - 0.5) * 0.05;
                    result.add(new Projectile(Projectile.Kind.BOSS_BOLT, x, y, angle, speed));
                }
            }
        }
        return result;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);

        boolean telegraph = phase == Phase.TELEGRAPH;
        double pulse = telegraph ? (0.5 + 0.5 * Math.sin(time * 40)) : 0.4 + 0.2 * Math.sin(time * 2);

        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setGlobalAlpha(0.35 + pulse * 0.4);
        gc.setFill(telegraph ? Color.rgb(255, 210, 90) : Color.rgb(255, 60, 160));
        gc.fillOval(-radius * 1.3, -radius * 1.3, radius * 2.6, radius * 2.6);
        gc.restore();

        gc.setFill(Color.rgb(60, 15, 45));
        gc.fillOval(-radius, -radius * 0.7, radius * 2, radius * 1.4);
        gc.setStroke(Color.rgb(255, 90, 180));
        gc.setLineWidth(3);
        gc.strokeOval(-radius, -radius * 0.7, radius * 2, radius * 1.4);

        gc.setFill(telegraph ? Color.rgb(255, 230, 150) : Color.rgb(255, 120, 200));
        gc.fillOval(-radius * 0.35, -radius * 0.35, radius * 0.7, radius * 0.7);

        gc.restore();

        double barWidth = 220;
        double barX = x - barWidth / 2;
        double barY = y - radius - 26;
        gc.setFill(Color.rgb(20, 20, 30, 0.8));
        gc.fillRect(barX, barY, barWidth, 10);
        double pct = Math.max(0, hitPoints / (double) maxHitPoints);
        gc.setFill(Color.rgb(255, 70, 130));
        gc.fillRect(barX, barY, barWidth * pct, 10);
        gc.setStroke(Color.rgb(255, 180, 210));
        gc.setLineWidth(1.5);
        gc.strokeRect(barX, barY, barWidth, 10);
    }
}
