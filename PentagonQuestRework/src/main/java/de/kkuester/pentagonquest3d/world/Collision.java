package de.kkuester.pentagonquest3d.world;

/**
 * Kreis-vs-Block-Kollision für die freie Bewegung. Achsen-getrennt aufgelöst
 * (erst X, dann Z), damit an Wänden sauber entlanggeglitten statt hart
 * gestoppt wird.
 */
public class Collision {

    private final DungeonOccupancy occupancy;

    public Collision(DungeonOccupancy occupancy) {
        this.occupancy = occupancy;
    }

    public record Position(double x, double z) {
    }

    public Position moveWithSliding(double x, double z, double dx, double dz, double radius) {
        double newX = resolveAxis(x, z, dx, radius, true);
        double newZ = resolveAxis(newX, z, dz, radius, false);
        return new Position(newX, newZ);
    }

    private double resolveAxis(double x, double z, double delta, double radius, boolean movingX) {
        double current = movingX ? x : z;
        if (delta == 0) {
            return current;
        }
        double candidate = current + delta;
        double testX = movingX ? candidate : x;
        double testZ = movingX ? z : candidate;
        return collidesAt(testX, testZ, radius) ? current : candidate;
    }

    private boolean collidesAt(double x, double z, double radius) {
        return occupancy.isSolidWorld(x + radius, z)
                || occupancy.isSolidWorld(x - radius, z)
                || occupancy.isSolidWorld(x, z + radius)
                || occupancy.isSolidWorld(x, z - radius)
                || occupancy.isSolidWorld(x, z);
    }
}
