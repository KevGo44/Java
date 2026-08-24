package de.kkuester.ufospiel.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectileTest {

    @Test
    void update_movesStraightAlongItsAngle() {
        Projectile p = new Projectile(Projectile.Kind.PLAYER_LASER, 0, 0, 0, 100);
        p.update(1.0, null);
        assertEquals(100, p.x, 0.001);
        assertEquals(0, p.y, 0.001);
    }

    @Test
    void update_movingUpwardDecreasesY() {
        Projectile p = new Projectile(Projectile.Kind.PLAYER_LASER, 0, 0, -Math.PI / 2, 100);
        p.update(1.0, null);
        assertEquals(0, p.x, 0.001);
        assertEquals(-100, p.y, 0.001);
    }

    @Test
    void update_marksDeadAfterLifespanExpires() {
        Projectile p = new Projectile(Projectile.Kind.PLAYER_LASER, 0, 0, 0, 100);
        assertFalse(p.dead);
        p.update(10.0, null);
        assertTrue(p.dead);
    }

    @Test
    void isPlayerOwned_trueForPlayerKinds_falseForHostileKinds() {
        assertTrue(new Projectile(Projectile.Kind.PLAYER_LASER, 0, 0, 0, 1).isPlayerOwned());
        assertTrue(new Projectile(Projectile.Kind.PLAYER_HOMING, 0, 0, 0, 1).isPlayerOwned());
        assertFalse(new Projectile(Projectile.Kind.ENEMY_BOLT, 0, 0, 0, 1).isPlayerOwned());
        assertFalse(new Projectile(Projectile.Kind.BOSS_BOLT, 0, 0, 0, 1).isPlayerOwned());
    }

    @Test
    void homingMissile_turnsTowardTarget() {
        // fired straight right (angle 0), target sits above it -> should steer upward (negative angle) over time
        Projectile missile = new Projectile(Projectile.Kind.PLAYER_HOMING, 0, 0, 0, 100);
        Entity target = new Entity() {
        };
        target.x = 0;
        target.y = -500;

        double initialAngle = missile.rotation;
        for (int i = 0; i < 20; i++) {
            missile.update(0.05, target);
        }
        assertTrue(missile.rotation < initialAngle, "missile should have turned toward the target above it");
    }

    @Test
    void nonHomingProjectile_ignoresTargetAndStaysStraight() {
        Projectile laser = new Projectile(Projectile.Kind.PLAYER_LASER, 0, 0, 0, 100);
        Entity target = new Entity() {
        };
        target.x = 0;
        target.y = -500;
        laser.update(0.5, target);
        assertEquals(0, laser.rotation, 0.0001, "non-homing projectiles must not steer");
    }
}
