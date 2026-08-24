package de.kkuester.pentagonquest3d.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Randomisierter Recursive-Backtracker (perfektes Labyrinth) + optionales
 * Wand-Entfernen für Schleifen/Abkürzungen. Direkter Port des Algorithmus aus
 * der Python-Version (pentagonquest/dungeon.py: _carve_maze / _add_loops),
 * hier ohne Spieler-Blickrichtung - nur die reine Wand-Topologie wird gebraucht,
 * da die Bewegung in der 3D-Version frei/kontinuierlich statt zellenbasiert ist.
 */
public final class MazeGenerator {

    public static final int WALL_N = 1;
    public static final int WALL_E = 2;
    public static final int WALL_S = 4;
    public static final int WALL_W = 8;
    public static final int ALL_WALLS = WALL_N | WALL_E | WALL_S | WALL_W;

    private record Dir(int dCol, int dRow, int bit, int oppositeBit) {
    }

    private static final Dir[] DIRECTIONS = {
            new Dir(0, -1, WALL_N, WALL_S),
            new Dir(1, 0, WALL_E, WALL_W),
            new Dir(0, 1, WALL_S, WALL_N),
            new Dir(-1, 0, WALL_W, WALL_E),
    };

    private MazeGenerator() {
    }

    /** Erzeugt ein perfektes Labyrinth (Spanning-Tree ab (0,0)) als size x size Wand-Bitmasken. */
    public static int[][] carve(int size, Random random) {
        int[][] walls = new int[size][size];
        for (int[] row : walls) {
            Arrays.fill(row, ALL_WALLS);
        }
        boolean[][] visited = new boolean[size][size];
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, 0});
        visited[0][0] = true;

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            List<Dir> candidateDirs = new ArrayList<>();
            List<int[]> candidateCells = new ArrayList<>();
            for (Dir dir : DIRECTIONS) {
                int nc = current[0] + dir.dCol();
                int nr = current[1] + dir.dRow();
                if (nc >= 0 && nc < size && nr >= 0 && nr < size && !visited[nc][nr]) {
                    candidateDirs.add(dir);
                    candidateCells.add(new int[]{nc, nr});
                }
            }
            if (candidateDirs.isEmpty()) {
                stack.pop();
                continue;
            }
            int pick = random.nextInt(candidateDirs.size());
            Dir dir = candidateDirs.get(pick);
            int[] next = candidateCells.get(pick);

            walls[current[0]][current[1]] &= ~dir.bit();
            walls[next[0]][next[1]] &= ~dir.oppositeBit();
            visited[next[0]][next[1]] = true;
            stack.push(next);
        }
        return walls;
    }

    /** Entfernt zufällig ~extraChance der verbleibenden Innenwände für Schleifen/Abkürzungen. */
    public static void addLoops(int[][] walls, int size, double extraChance, Random random) {
        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                for (Dir dir : DIRECTIONS) {
                    int nc = c + dir.dCol();
                    int nr = r + dir.dRow();
                    if (nc < 0 || nc >= size || nr < 0 || nr >= size) {
                        continue;
                    }
                    if ((walls[c][r] & dir.bit()) != 0 && random.nextDouble() < extraChance) {
                        walls[c][r] &= ~dir.bit();
                        walls[nc][nr] &= ~dir.oppositeBit();
                    }
                }
            }
        }
    }

    public static boolean hasWall(int[][] walls, int col, int row, int wallBit) {
        return (walls[col][row] & wallBit) != 0;
    }
}
