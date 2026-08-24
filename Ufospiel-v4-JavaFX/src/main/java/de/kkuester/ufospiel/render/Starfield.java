package de.kkuester.ufospiel.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Multi-layer parallax starfield with independent drift and twinkle, purely procedural. */
public class Starfield {

    private record Star(double x, double y, double size, double speed, double twinkleSeed, double brightness) {
    }

    private final List<List<Star>> layers = new ArrayList<>();
    private final double width;
    private final double height;
    private double time;

    public Starfield(double width, double height) {
        this.width = width;
        this.height = height;
        Random random = new Random(42);
        int[] counts = {70, 45, 25};
        double[] speeds = {6, 14, 26};
        double[] sizes = {1.2, 1.8, 2.6};
        for (int layer = 0; layer < counts.length; layer++) {
            List<Star> stars = new ArrayList<>();
            for (int i = 0; i < counts[layer]; i++) {
                stars.add(new Star(
                        random.nextDouble() * width,
                        random.nextDouble() * height,
                        sizes[layer] * (0.6 + random.nextDouble() * 0.8),
                        speeds[layer],
                        random.nextDouble() * Math.PI * 2,
                        0.4 + random.nextDouble() * 0.6));
            }
            layers.add(stars);
        }
    }

    public void update(double dt) {
        time += dt;
        for (int layerIdx = 0; layerIdx < layers.size(); layerIdx++) {
            List<Star> stars = layers.get(layerIdx);
            for (int i = 0; i < stars.size(); i++) {
                Star s = stars.get(i);
                double ny = s.y() + s.speed() * dt;
                if (ny > height) {
                    ny -= height;
                }
                stars.set(i, new Star(s.x(), ny, s.size(), s.speed(), s.twinkleSeed(), s.brightness()));
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (List<Star> stars : layers) {
            for (Star s : stars) {
                double twinkle = 0.65 + 0.35 * Math.sin(time * 2.2 + s.twinkleSeed());
                double alpha = Math.max(0.1, Math.min(1, s.brightness() * twinkle));
                gc.setFill(Color.color(0.75, 0.85, 1.0, alpha));
                gc.fillOval(s.x(), s.y(), s.size(), s.size());
            }
        }
    }
}
