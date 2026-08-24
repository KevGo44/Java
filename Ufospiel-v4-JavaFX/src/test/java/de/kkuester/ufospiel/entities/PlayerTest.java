package de.kkuester.ufospiel.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void takeHit_consumesShieldBeforeLives() {
        Player p = new Player(100, 100);
        int startingLives = p.lives;
        int startingShield = p.shieldSegments;

        boolean lostLife = p.takeHit();

        assertFalse(lostLife, "a hit with shield up should not cost a life");
        assertEquals(startingShield - 1, p.shieldSegments);
        assertEquals(startingLives, p.lives);
    }

    @Test
    void takeHit_costsLifeOnceShieldIsGone() {
        Player p = new Player(100, 100);
        p.shieldSegments = 0;
        int startingLives = p.lives;

        boolean lostLife = p.takeHit();

        assertTrue(lostLife);
        assertEquals(startingLives - 1, p.lives);
    }

    @Test
    void takeHit_duringInvulnerabilityWindowIsIgnored() {
        Player p = new Player(100, 100);
        p.invulnerableTimer = 1.0;
        int shieldBefore = p.shieldSegments;
        int livesBefore = p.lives;

        boolean lostLife = p.takeHit();

        assertFalse(lostLife);
        assertEquals(shieldBefore, p.shieldSegments);
        assertEquals(livesBefore, p.lives);
    }

    @Test
    void resetForNewLife_restoresShieldAndCentersShip() {
        Player p = new Player(100, 100);
        p.shieldSegments = 0;
        p.x = 999;
        p.y = 999;
        p.vx = 50;
        p.vy = 50;

        p.resetForNewLife(1280, 720);

        assertEquals(p.maxShieldSegments, p.shieldSegments);
        assertEquals(640, p.x, 0.0001);
        assertEquals(360, p.y, 0.0001);
        assertEquals(0, p.vx, 0.0001);
        assertEquals(0, p.vy, 0.0001);
        assertTrue(p.isInvulnerable());
    }

    @Test
    void update_neverExceedsTheConfiguredMaxSpeed() {
        Player p = new Player(640, 360);
        for (int i = 0; i < 50; i++) {
            p.thrust(1, 0, 0.1);
            p.update(0.1, 1280, 720);
        }
        double speed = Math.hypot(p.vx, p.vy);
        assertTrue(speed <= 420.01, "speed should be clamped to the max speed but was " + speed);
    }

    @Test
    void update_clampsPositionWithinArenaBounds() {
        Player p = new Player(10, 10);
        p.vx = -10000;
        p.vy = -10000;
        p.update(1.0, 1280, 720);
        assertTrue(p.x >= p.radius);
        assertTrue(p.y >= p.radius);
    }
}
