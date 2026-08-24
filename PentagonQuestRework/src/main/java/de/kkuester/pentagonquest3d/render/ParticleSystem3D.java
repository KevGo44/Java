package de.kkuester.pentagonquest3d.render;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/** Einfache 3D-Partikel (kleine Kugeln) für Trefferfunken, Tod-Explosionen und Staub. */
public class ParticleSystem3D {

    private record Particle(Sphere shape, double maxLife, double[] life, double[] velocity) {
    }

    private final Group root = new Group();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public Group getRoot() {
        return root;
    }

    public void update(double dt) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.life()[0] -= dt;
            if (p.life()[0] <= 0) {
                root.getChildren().remove(p.shape());
                it.remove();
                continue;
            }
            p.velocity()[1] -= 2.4 * dt; // leichte Schwerkraft
            p.shape().setTranslateX(p.shape().getTranslateX() + p.velocity()[0] * dt);
            p.shape().setTranslateY(p.shape().getTranslateY() + p.velocity()[1] * dt);
            p.shape().setTranslateZ(p.shape().getTranslateZ() + p.velocity()[2] * dt);
            double t = Math.max(0, p.life()[0] / p.maxLife());
            p.shape().setScaleX(t);
            p.shape().setScaleY(t);
            p.shape().setScaleZ(t);
            p.shape().setOpacity(t);
        }
    }

    public void spawnHitSpark(double x, double y, double z, Color color) {
        spawnBurst(x, y, z, color, 10, 0.28, 1.4);
    }

    public void spawnDeathBurst(double x, double y, double z, Color color) {
        spawnBurst(x, y, z, color, 26, 0.55, 2.1);
    }

    public void spawnDust(double x, double y, double z, Color color) {
        spawnBurst(x, y, z, color, 3, 0.5, 0.35);
    }

    private void spawnBurst(double x, double y, double z, Color color, int count, double life, double speed) {
        for (int i = 0; i < count; i++) {
            Sphere sphere = new Sphere(0.035);
            sphere.setMaterial(new PhongMaterial(color));
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);

            double theta = random.nextDouble() * Math.PI * 2;
            double phi = random.nextDouble() * Math.PI - Math.PI / 2;
            double spd = speed * (0.4 + random.nextDouble() * 0.9);
            double[] velocity = {
                    Math.cos(theta) * Math.cos(phi) * spd,
                    Math.sin(phi) * spd,
                    Math.sin(theta) * Math.cos(phi) * spd,
            };
            double maxLife = life * (0.6 + random.nextDouble() * 0.8);
            particles.add(new Particle(sphere, maxLife, new double[]{maxLife}, velocity));
            root.getChildren().add(sphere);
        }
    }
}
