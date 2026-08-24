package de.kkuester.ufospiel.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Slow-drifting soft-glow nebula clouds rendered additively behind the action. */
public class Nebula {

    private record Cloud(double baseX, double baseY, double radius, Color color, double driftSeed) {
    }

    private final List<Cloud> clouds = new ArrayList<>();
    private final double width;
    private final double height;
    private double time;

    public Nebula(double width, double height) {
        this.width = width;
        this.height = height;
        Random random = new Random(7);
        Color[] palette = {
                Color.rgb(90, 40, 180),
                Color.rgb(20, 120, 200),
                Color.rgb(180, 30, 140)
        };
        for (int i = 0; i < 4; i++) {
            clouds.add(new Cloud(
                    random.nextDouble() * width,
                    random.nextDouble() * height,
                    220 + random.nextDouble() * 260,
                    palette[i % palette.length],
                    random.nextDouble() * 1000));
        }
    }

    public void update(double dt) {
        time += dt;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        for (Cloud c : clouds) {
            double x = c.baseX() + Math.sin(time * 0.05 + c.driftSeed()) * 60;
            double y = c.baseY() + Math.cos(time * 0.04 + c.driftSeed()) * 40;
            RadialGradient gradient = new RadialGradient(0, 0, x, y, c.radius(), false,
                    javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new Stop(0, c.color().deriveColor(0, 1, 1, 0.10)),
                    new Stop(1, c.color().deriveColor(0, 1, 1, 0)));
            gc.setFill(gradient);
            gc.fillOval(x - c.radius(), y - c.radius(), c.radius() * 2, c.radius() * 2);
        }
        gc.restore();
    }
}
