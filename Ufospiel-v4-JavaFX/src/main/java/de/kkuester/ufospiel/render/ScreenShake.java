package de.kkuester.ufospiel.render;

/** Classic "trauma" based screen shake: trauma decays over time, offset scales with trauma^2. */
public class ScreenShake {

    private double trauma = 0;
    private final double decayPerSecond = 1.6;
    private double offsetX;
    private double offsetY;
    private double seedTime;

    public void addTrauma(double amount) {
        trauma = Math.min(1.0, trauma + amount);
    }

    public void update(double dt) {
        seedTime += dt * 30;
        if (trauma > 0) {
            trauma = Math.max(0, trauma - decayPerSecond * dt);
        }
        double shake = trauma * trauma;
        double maxOffset = 18 * shake;
        double maxAngleJitter = 0.06 * shake;
        offsetX = maxOffset * noise(seedTime);
        offsetY = maxOffset * noise(seedTime + 91.7);
        this.angle = maxAngleJitter * noise(seedTime + 173.3);
    }

    private double angle;

    private double noise(double t) {
        // cheap deterministic pseudo-noise via layered sine waves
        return Math.sin(t) * 0.6 + Math.sin(t * 2.13 + 1.7) * 0.3 + Math.sin(t * 4.7 + 4.1) * 0.1;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getAngle() {
        return angle;
    }
}
