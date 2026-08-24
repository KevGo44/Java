package de.kkuester.ufospiel.render;

import javafx.scene.paint.Color;

public class Particle {
    public double x, y, vx, vy;
    public double life, maxLife;
    public double size, endSize;
    public Color startColor, endColor;
    public double drag;

    public boolean isDead() {
        return life <= 0;
    }

    public void update(double dt) {
        life -= dt;
        vx *= (1 - drag * dt);
        vy *= (1 - drag * dt);
        x += vx * dt;
        y += vy * dt;
    }

    public double progress() {
        return 1.0 - Math.max(0, life / maxLife);
    }
}
