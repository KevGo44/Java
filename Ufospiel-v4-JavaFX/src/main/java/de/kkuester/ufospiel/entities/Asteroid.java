package de.kkuester.ufospiel.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asteroid extends Entity {

    public enum Size {
        LARGE(46, 90, 20),
        MEDIUM(28, 130, 50),
        SMALL(16, 190, 100);

        public final double radius;
        public final double baseSpeed;
        public final int score;

        Size(double radius, double baseSpeed, int score) {
            this.radius = radius;
            this.baseSpeed = baseSpeed;
            this.score = score;
        }
    }

    public final Size size;
    public double rotationSpeed;
    public int hitPoints;
    private final double[] jagged;

    public Asteroid(Size size, double x, double y, double angleRad, double speedFactor) {
        this.size = size;
        this.x = x;
        this.y = y;
        this.radius = size.radius;
        this.hitPoints = switch (size) {
            case LARGE -> 3;
            case MEDIUM -> 2;
            case SMALL -> 1;
        };
        Random random = new Random();
        double speed = size.baseSpeed * speedFactor * (0.6 + random.nextDouble() * 0.8);
        this.vx = Math.cos(angleRad) * speed;
        this.vy = Math.sin(angleRad) * speed;
        this.rotationSpeed = (random.nextBoolean() ? 1 : -1) * (0.3 + random.nextDouble() * 1.1);

        int points = 9 + random.nextInt(5);
        jagged = new double[points];
        for (int i = 0; i < points; i++) {
            jagged[i] = 0.72 + random.nextDouble() * 0.5;
        }
    }

    public void update(double dt, double arenaW, double arenaH) {
        x += vx * dt;
        y += vy * dt;
        rotation += rotationSpeed * dt;
        wrapAround(arenaW, arenaH);
    }

    /** Splits into two smaller asteroids, or empty list if already the smallest tier. */
    public List<Asteroid> split() {
        List<Asteroid> children = new ArrayList<>();
        Size next = switch (size) {
            case LARGE -> Size.MEDIUM;
            case MEDIUM -> Size.SMALL;
            case SMALL -> null;
        };
        if (next == null) {
            return children;
        }
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            Asteroid child = new Asteroid(next, x, y, angle, 1.0);
            child.vx += vx * 0.3;
            child.vy += vy * 0.3;
            children.add(child);
        }
        return children;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(rotation));

        double[] xs = new double[jagged.length];
        double[] ys = new double[jagged.length];
        for (int i = 0; i < jagged.length; i++) {
            double a = (Math.PI * 2 / jagged.length) * i;
            double r = radius * jagged[i];
            xs[i] = Math.cos(a) * r;
            ys[i] = Math.sin(a) * r;
        }

        gc.setGlobalAlpha(0.25);
        gc.setFill(Color.rgb(200, 150, 255));
        gc.fillPolygon(xs, ys, xs.length);

        gc.setGlobalAlpha(1.0);
        gc.setStroke(Color.rgb(210, 180, 255));
        gc.setLineWidth(2);
        gc.strokePolygon(xs, ys, xs.length);
        gc.setFill(Color.rgb(60, 40, 90, 0.85));
        gc.fillPolygon(xs, ys, xs.length);

        gc.restore();
    }
}
