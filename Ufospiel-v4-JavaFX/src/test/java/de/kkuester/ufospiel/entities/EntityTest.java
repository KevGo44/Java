package de.kkuester.ufospiel.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private static Entity entityAt(double x, double y, double radius) {
        Entity e = new Entity() {
        };
        e.x = x;
        e.y = y;
        e.radius = radius;
        return e;
    }

    @Test
    void collidesWith_returnsTrue_whenCirclesOverlap() {
        Entity a = entityAt(0, 0, 10);
        Entity b = entityAt(15, 0, 10);
        assertTrue(a.collidesWith(b), "circles with radius sum 20 and distance 15 should overlap");
    }

    @Test
    void collidesWith_returnsFalse_whenCirclesApart() {
        Entity a = entityAt(0, 0, 10);
        Entity b = entityAt(100, 0, 10);
        assertFalse(a.collidesWith(b));
    }

    @Test
    void collidesWith_boundaryCase_touchingCirclesCount() {
        Entity a = entityAt(0, 0, 10);
        Entity b = entityAt(20, 0, 10);
        assertTrue(a.collidesWith(b), "circles exactly touching (distance == radius sum) should count as colliding");
    }

    @Test
    void wrapAround_wrapsPastRightEdgeToLeft() {
        Entity e = entityAt(500, 100, 5);
        e.wrapAround(400, 600);
        assertTrue(e.x < 0, "entity past the right margin should wrap to the left side");
    }

    @Test
    void wrapAround_leavesInBoundsEntityUntouched() {
        Entity e = entityAt(200, 300, 5);
        e.wrapAround(400, 600);
        assertEquals(200, e.x);
        assertEquals(300, e.y);
    }
}
