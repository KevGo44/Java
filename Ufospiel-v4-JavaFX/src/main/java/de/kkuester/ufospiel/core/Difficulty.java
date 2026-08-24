package de.kkuester.ufospiel.core;

public enum Difficulty {
    EASY(0.75, 1.4, "Leicht"),
    NORMAL(1.0, 1.0, "Normal"),
    HARD(1.35, 0.7, "Schwer");

    public final double speedFactor;
    public final double spawnIntervalFactor;
    public final String label;

    Difficulty(double speedFactor, double spawnIntervalFactor, String label) {
        this.speedFactor = speedFactor;
        this.spawnIntervalFactor = spawnIntervalFactor;
        this.label = label;
    }

    public Difficulty next() {
        Difficulty[] v = values();
        return v[(ordinal() + 1) % v.length];
    }
}
