package de.kkuester.ufospiel.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public void update(double dt) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(dt);
            if (p.isDead()) {
                particles.remove(i);
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        for (Particle p : particles) {
            double t = p.progress();
            double size = lerp(p.size, p.endSize, t) * (1 - t * 0.15);
            Color c = interpolate(p.startColor, p.endColor, t);
            double alpha = Math.max(0, 1 - t);
            gc.setGlobalAlpha(alpha);
            gc.setFill(c);
            gc.fillOval(p.x - size / 2, p.y - size / 2, size, size);
        }
        gc.restore();
    }

    public void spawnExplosion(double x, double y, Color color, int count, double speed) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double spd = speed * (0.4 + random.nextDouble() * 0.9);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = Math.cos(angle) * spd;
            p.vy = Math.sin(angle) * spd;
            p.maxLife = p.life = 0.4 + random.nextDouble() * 0.5;
            p.size = 4 + random.nextDouble() * 6;
            p.endSize = 0.5;
            p.drag = 1.4;
            p.startColor = color;
            p.endColor = color.deriveColor(0, 1, 1, 0);
            particles.add(p);
        }
    }

    public void spawnThruster(double x, double y, double dirAngleRad, Color color) {
        double spread = 0.35;
        double angle = dirAngleRad + Math.PI + (random.nextDouble() - 0.5) * spread;
        double spd = 60 + random.nextDouble() * 90;
        Particle p = new Particle();
        p.x = x;
        p.y = y;
        p.vx = Math.cos(angle) * spd;
        p.vy = Math.sin(angle) * spd;
        p.maxLife = p.life = 0.18 + random.nextDouble() * 0.15;
        p.size = 5 + random.nextDouble() * 4;
        p.endSize = 0.5;
        p.drag = 0.5;
        p.startColor = color;
        p.endColor = Color.TRANSPARENT;
        particles.add(p);
    }

    public void spawnSparkle(double x, double y, Color color) {
        for (int i = 0; i < 10; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double spd = 30 + random.nextDouble() * 60;
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = Math.cos(angle) * spd;
            p.vy = Math.sin(angle) * spd;
            p.maxLife = p.life = 0.3 + random.nextDouble() * 0.3;
            p.size = 3 + random.nextDouble() * 3;
            p.endSize = 0.5;
            p.drag = 1.0;
            p.startColor = color;
            p.endColor = color.deriveColor(0, 1, 1, 0);
            particles.add(p);
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static Color interpolate(Color a, Color b, double t) {
        return a.interpolate(b, Math.max(0, Math.min(1, t)));
    }

    public int count() {
        return particles.size();
    }
}
