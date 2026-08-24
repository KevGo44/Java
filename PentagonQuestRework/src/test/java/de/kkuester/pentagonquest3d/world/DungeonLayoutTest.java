package de.kkuester.pentagonquest3d.world;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.MonsterType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DungeonLayoutTest {

    @Test
    void startRoomAlwaysHasTwoGoblins() {
        DungeonLayout layout = DungeonLayout.generate(new Random(1));
        assertEquals(DungeonLayout.RoomType.START, layout.roomTypes[0][0]);
    }

    @Test
    void bossRoomAlwaysHasOrkKoenig() {
        DungeonLayout layout = DungeonLayout.generate(new Random(1));
        int last = DungeonLayout.SIZE - 1;
        assertEquals(DungeonLayout.RoomType.BOSS, layout.roomTypes[last][last]);
    }

    @Test
    void spawnAllMonsters_includesStartGoblinsAndBoss() {
        DungeonLayout layout = DungeonLayout.generate(new Random(7));
        List<Monster> monsters = layout.spawnAllMonsters(new Random(7));

        long goblinsAtStart = monsters.stream()
                .filter(m -> m.homeCol == 0 && m.homeRow == 0 && m.type == MonsterType.GOBLIN)
                .count();
        long bosses = monsters.stream().filter(m -> m.type == MonsterType.ORK_KOENIG).count();

        assertEquals(2, goblinsAtStart);
        assertEquals(1, bosses);
    }

    @Test
    void spawnedMonsters_stayWithinTheirHomeCellBounds() {
        DungeonLayout layout = DungeonLayout.generate(new Random(3));
        List<Monster> monsters = layout.spawnAllMonsters(new Random(3));

        for (Monster m : monsters) {
            double cx = DungeonLayout.cellCenterX(m.homeCol);
            double cz = DungeonLayout.cellCenterZ(m.homeRow);
            double dx = Math.abs(m.x - cx);
            double dz = Math.abs(m.z - cz);
            assertTrue(dx <= DungeonLayout.CELL_SIZE / 2.0,
                    () -> "col=" + m.homeCol + " row=" + m.homeRow + " x=" + m.x + " cx=" + cx + " dx=" + dx
                            + " CELL_SIZE=" + DungeonLayout.CELL_SIZE);
            assertTrue(dz <= DungeonLayout.CELL_SIZE / 2.0,
                    () -> "col=" + m.homeCol + " row=" + m.homeRow + " z=" + m.z + " cz=" + cz + " dz=" + dz
                            + " CELL_SIZE=" + DungeonLayout.CELL_SIZE);
        }
    }

    @Test
    void bossRoomReachableFromStart_usingWallData() {
        DungeonLayout layout = DungeonLayout.generate(new Random(9));
        boolean[][] visited = new boolean[DungeonLayout.SIZE][DungeonLayout.SIZE];
        java.util.Deque<int[]> queue = new java.util.ArrayDeque<>();
        queue.add(new int[]{0, 0});
        visited[0][0] = true;
        int[][] deltas = {{0, -1, MazeGenerator.WALL_N}, {1, 0, MazeGenerator.WALL_E},
                {0, 1, MazeGenerator.WALL_S}, {-1, 0, MazeGenerator.WALL_W}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : deltas) {
                if (layout.hasWall(cur[0], cur[1], d[2])) continue;
                int nc = cur[0] + d[0], nr = cur[1] + d[1];
                if (nc < 0 || nc >= DungeonLayout.SIZE || nr < 0 || nr >= DungeonLayout.SIZE || visited[nc][nr]) continue;
                visited[nc][nr] = true;
                queue.add(new int[]{nc, nr});
            }
        }
        assertTrue(visited[DungeonLayout.SIZE - 1][DungeonLayout.SIZE - 1]);
    }
}
