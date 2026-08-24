package de.kkuester.pentagonquest3d.world;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static de.kkuester.pentagonquest3d.world.MazeGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

class MazeGeneratorTest {

    private static final int SIZE = 5;

    private static Set<Long> reachableCells(int[][] walls, int size) {
        Set<Long> seen = new HashSet<>();
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        seen.add(key(0, 0));
        int[][] deltas = {{0, -1, WALL_N}, {1, 0, WALL_E}, {0, 1, WALL_S}, {-1, 0, WALL_W}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : deltas) {
                if ((walls[cur[0]][cur[1]] & d[2]) != 0) {
                    continue;
                }
                int nc = cur[0] + d[0], nr = cur[1] + d[1];
                if (nc < 0 || nc >= size || nr < 0 || nr >= size) {
                    continue;
                }
                long k = key(nc, nr);
                if (seen.add(k)) {
                    queue.add(new int[]{nc, nr});
                }
            }
        }
        return seen;
    }

    private static long key(int c, int r) {
        return (long) c * 1000 + r;
    }

    @Test
    void perfectMazeIsFullyConnected() {
        for (int run = 0; run < 8; run++) {
            int[][] walls = carve(SIZE, new Random(run));
            assertEquals(SIZE * SIZE, reachableCells(walls, SIZE).size(),
                    "Lauf " + run + ": nicht alle Zellen erreichbar");
        }
    }

    @Test
    void loopsKeepFullConnectivity() {
        for (int run = 0; run < 8; run++) {
            int[][] walls = carve(SIZE, new Random(run));
            addLoops(walls, SIZE, 0.13, new Random(run + 1000));
            assertEquals(SIZE * SIZE, reachableCells(walls, SIZE).size(),
                    "Lauf " + run + ": Schleifen haben Zellen isoliert");
        }
    }

    @Test
    void boundaryWallsAreNeverOpened() {
        int[][] walls = carve(SIZE, new Random(42));
        addLoops(walls, SIZE, 0.13, new Random(43));
        for (int r = 0; r < SIZE; r++) {
            assertTrue(hasWall(walls, 0, r, WALL_W), "linker Kartenrand muss Wand haben");
            assertTrue(hasWall(walls, SIZE - 1, r, WALL_E), "rechter Kartenrand muss Wand haben");
        }
        for (int c = 0; c < SIZE; c++) {
            assertTrue(hasWall(walls, c, 0, WALL_N), "oberer Kartenrand muss Wand haben");
            assertTrue(hasWall(walls, c, SIZE - 1, WALL_S), "unterer Kartenrand muss Wand haben");
        }
    }

    @Test
    void startCellHasAtLeastOneOpening() {
        for (int run = 0; run < 5; run++) {
            int[][] walls = carve(SIZE, new Random(run));
            assertNotEquals(ALL_WALLS, walls[0][0], "Startzelle darf nicht komplett eingeschlossen sein");
        }
    }
}
