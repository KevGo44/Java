package de.kkuester.pentagonquest3d.world;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CollisionTest {

    private static DungeonLayout freshLayout() {
        return DungeonLayout.generate(new Random(11));
    }

    @Test
    void occupancy_cornerBlocksAreAlwaysSolid() {
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        // Zelle (0,0), lokaler Block (0,0) ist die Raum-Ecke -> global Block (0,0).
        assertTrue(occ.solid[0][0]);
    }

    @Test
    void occupancy_interiorBlocksAreNeverSolid() {
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        int last = DungeonBuilder.ROOM_BLOCKS - 1;
        for (int bx = 1; bx < last; bx++) {
            for (int bz = 1; bz < last; bz++) {
                assertFalse(occ.solid[bx][bz], "innerer Block (" + bx + "," + bz + ") sollte begehbar sein");
            }
        }
    }

    @Test
    void occupancy_westEdgeCenterMatchesWallBit() {
        // Startzelle (0,0) hat garantiert WALL_W (Kartenrand).
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        assertTrue(layout.hasWall(0, 0, MazeGenerator.WALL_W));

        int center = DungeonBuilder.ROOM_BLOCKS / 2;
        assertTrue(occ.solid[0][center], "West-Kantenmitte muss solide sein, da WALL_W gesetzt ist");
    }

    @Test
    void occupancy_nonGatewayPerimeterBlocksAreAlwaysSolid() {
        // Randblöcke, die weder Ecke noch exakte Kantenmitte sind, müssen unabhängig
        // von den Maze-Wanddaten immer feste Wand sein (kein Durchgang dort vorgesehen).
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        int last = DungeonBuilder.ROOM_BLOCKS - 1;
        int center = DungeonBuilder.ROOM_BLOCKS / 2;
        if (center - 1 > 0) {
            assertTrue(occ.solid[0][center - 1], "Randblock neben der Kantenmitte muss fest sein");
        }
    }

    @Test
    void moveWithSliding_isBlockedBySolidWestWall() {
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        Collision collision = new Collision(occ);

        // Frei im Rauminneren stehen und versuchen, durch die (garantiert solide) Westwand zu laufen.
        double startX = DungeonLayout.cellCenterX(0);
        double startZ = DungeonLayout.cellCenterZ(0);
        Collision.Position result = collision.moveWithSliding(startX, startZ, -10.0, 0, 0.2);

        assertTrue(result.x() > 0, "darf nicht durch die Wand ins Negative laufen");
        assertTrue(result.x() >= DungeonBuilder.BLOCK_SIZE, "sollte spätestens am Wandblock stoppen");
    }

    @Test
    void moveWithSliding_allowsMovementInOpenSpace() {
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        Collision collision = new Collision(occ);

        double startX = DungeonLayout.cellCenterX(0);
        double startZ = DungeonLayout.cellCenterZ(0);
        Collision.Position result = collision.moveWithSliding(startX, startZ, 0.1, 0.1, 0.2);

        assertEquals(startX + 0.1, result.x(), 0.0001);
        assertEquals(startZ + 0.1, result.z(), 0.0001);
    }

    @Test
    void moveWithSliding_slidesAlongWallWhenOneAxisBlocked() {
        DungeonLayout layout = freshLayout();
        DungeonOccupancy occ = DungeonOccupancy.build(layout);
        Collision collision = new Collision(occ);

        double startX = DungeonLayout.cellCenterX(0);
        double startZ = DungeonLayout.cellCenterZ(0);
        // Versuch: gleichzeitig weit nach Westen (blockiert von der Kartenrand-Wand) und ein
        // kleines Stück nach Süden (frei) zu laufen.
        Collision.Position result = collision.moveWithSliding(startX, startZ, -10.0, 0.1, 0.2);

        assertTrue(result.x() > 0, "X-Bewegung soll an der Wand gestoppt werden");
        assertEquals(startZ + 0.1, result.z(), 0.0001, "Z-Bewegung soll trotzdem stattfinden (Entlanggleiten)");
    }
}
