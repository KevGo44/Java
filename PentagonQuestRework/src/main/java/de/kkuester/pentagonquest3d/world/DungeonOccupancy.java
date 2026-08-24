package de.kkuester.pentagonquest3d.world;

/**
 * Block-genaues Begehbarkeits-Raster, abgeleitet aus der gleichen Perimeter/Gateway-Logik,
 * die {@link DungeonBuilder} zum Platzieren der 3D-Geometrie verwendet - eine einzige
 * Quelle der Wahrheit für "ist dieser Block solide?", genutzt sowohl fürs Rendern als
 * auch für die Kollisionserkennung ({@link Collision}).
 */
public class DungeonOccupancy {

    public final boolean[][] solid; // [globalBlockX][globalBlockZ]
    public final int blocksPerAxis;

    private DungeonOccupancy(boolean[][] solid, int blocksPerAxis) {
        this.solid = solid;
        this.blocksPerAxis = blocksPerAxis;
    }

    public static DungeonOccupancy build(DungeonLayout layout) {
        int n = DungeonLayout.SIZE * DungeonBuilder.ROOM_BLOCKS;
        boolean[][] solid = new boolean[n][n];
        for (int col = 0; col < DungeonLayout.SIZE; col++) {
            for (int row = 0; row < DungeonLayout.SIZE; row++) {
                int baseX = col * DungeonBuilder.ROOM_BLOCKS;
                int baseZ = row * DungeonBuilder.ROOM_BLOCKS;
                for (int bx = 0; bx < DungeonBuilder.ROOM_BLOCKS; bx++) {
                    for (int bz = 0; bz < DungeonBuilder.ROOM_BLOCKS; bz++) {
                        solid[baseX + bx][baseZ + bz] = isBlockSolid(layout, col, row, bx, bz);
                    }
                }
            }
        }
        return new DungeonOccupancy(solid, n);
    }

    static boolean isBlockSolid(DungeonLayout layout, int col, int row, int bx, int bz) {
        int last = DungeonBuilder.ROOM_BLOCKS - 1;
        int center = DungeonBuilder.ROOM_BLOCKS / 2;
        boolean isPerimeter = bx == 0 || bx == last || bz == 0 || bz == last;
        if (!isPerimeter) {
            return false;
        }
        boolean isCorner = (bx == 0 || bx == last) && (bz == 0 || bz == last);
        if (isCorner) {
            return true;
        }
        Integer wallBit = edgeCenterToWallBit(bx, bz, last, center);
        // Randblock ohne Torfunktion (nicht exakt der Kanten-Mittelpunkt) -> immer feste Wand.
        return wallBit == null || layout.hasWall(col, row, wallBit);
    }

    private static Integer edgeCenterToWallBit(int bx, int bz, int last, int center) {
        if (bz == 0 && bx == center) return MazeGenerator.WALL_N;
        if (bz == last && bx == center) return MazeGenerator.WALL_S;
        if (bx == 0 && bz == center) return MazeGenerator.WALL_W;
        if (bx == last && bz == center) return MazeGenerator.WALL_E;
        return null;
    }

    public boolean isSolidWorld(double worldX, double worldZ) {
        int bx = (int) Math.floor(worldX / DungeonBuilder.BLOCK_SIZE);
        int bz = (int) Math.floor(worldZ / DungeonBuilder.BLOCK_SIZE);
        if (bx < 0 || bz < 0 || bx >= blocksPerAxis || bz >= blocksPerAxis) {
            return true; // außerhalb der Karte = sicherheitshalber solide
        }
        return solid[bx][bz];
    }
}
