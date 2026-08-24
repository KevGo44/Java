package de.kkuester.ufospiel.entities;

public abstract class Entity {
    public double x, y;
    public double vx, vy;
    public double rotation;
    public double radius;
    public boolean dead;

    public boolean collidesWith(Entity other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double distSq = dx * dx + dy * dy;
        double r = radius + other.radius;
        return distSq <= r * r;
    }

    public void wrapAround(double width, double height) {
        double margin = radius + 20;
        if (x < -margin) x = width + margin;
        if (x > width + margin) x = -margin;
        if (y < -margin) y = height + margin;
        if (y > height + margin) y = -margin;
    }
}
