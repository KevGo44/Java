package de.kkuester.ufospiel.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsteroidTest {

    @Test
    void largeAsteroidSplitsIntoTwoMediumOnes() {
        Asteroid large = new Asteroid(Asteroid.Size.LARGE, 100, 100, 0, 1.0);
        List<Asteroid> children = large.split();
        assertEquals(2, children.size());
        assertTrue(children.stream().allMatch(a -> a.size == Asteroid.Size.MEDIUM));
    }

    @Test
    void mediumAsteroidSplitsIntoTwoSmallOnes() {
        Asteroid medium = new Asteroid(Asteroid.Size.MEDIUM, 100, 100, 0, 1.0);
        List<Asteroid> children = medium.split();
        assertEquals(2, children.size());
        assertTrue(children.stream().allMatch(a -> a.size == Asteroid.Size.SMALL));
    }

    @Test
    void smallAsteroidDoesNotSplitFurther() {
        Asteroid small = new Asteroid(Asteroid.Size.SMALL, 100, 100, 0, 1.0);
        assertTrue(small.split().isEmpty());
    }

    @Test
    void childrenSpawnAtParentPosition() {
        Asteroid large = new Asteroid(Asteroid.Size.LARGE, 42, 77, 0, 1.0);
        List<Asteroid> children = large.split();
        for (Asteroid child : children) {
            assertEquals(42, child.x);
            assertEquals(77, child.y);
        }
    }

    @Test
    void hitPointsMatchSizeTier() {
        assertEquals(3, new Asteroid(Asteroid.Size.LARGE, 0, 0, 0, 1.0).hitPoints);
        assertEquals(2, new Asteroid(Asteroid.Size.MEDIUM, 0, 0, 0, 1.0).hitPoints);
        assertEquals(1, new Asteroid(Asteroid.Size.SMALL, 0, 0, 0, 1.0).hitPoints);
    }

    @Test
    void update_movesPositionAccordingToVelocity() {
        Asteroid a = new Asteroid(Asteroid.Size.SMALL, 100, 100, 0, 0);
        a.vx = 50;
        a.vy = -25;
        a.update(1.0, 4000, 4000);
        assertEquals(150, a.x, 0.0001);
        assertEquals(75, a.y, 0.0001);
    }
}
