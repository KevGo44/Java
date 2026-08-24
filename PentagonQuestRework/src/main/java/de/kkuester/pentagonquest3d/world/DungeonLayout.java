package de.kkuester.pentagonquest3d.world;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.MonsterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kombiniert die reine Wand-Topologie (MazeGenerator) mit Rauminhalten -
 * Port von Field.random()/GameMap aus der Python-Version (pentagonquest/dungeon.py),
 * angepasst: Monster werden hier direkt als lebende, im Raum positionierte
 * Instanzen erzeugt statt als raumgebundene rundenbasierte Listen.
 */
public class DungeonLayout {

    public static final int SIZE = 5;
    /**
     * Kantenlänge einer Maze-Zelle in Weltkoordinaten. Muss mit {@link DungeonBuilder#ROOM_SIZE}
     * übereinstimmen, da jede Maze-Zelle dort als {@code ROOM_BLOCKS}x{@code ROOM_BLOCKS}-Block-Raum
     * aus dem Kenney-Kit gebaut wird (das Kit ist block- statt kantenwand-basiert).
     */
    public static final double CELL_SIZE = DungeonBuilder.ROOM_SIZE;

    public enum RoomType {
        EMPTY_TREASURE, GOBLIN_PAIR, GOBLIN_TRIO, ORK, GOBLIN_ORK, WAECHTER, GOBLIN_WAECHTER, WAECHTER_PAIR,
        START, BOSS
    }

    public final int[][] walls;
    public final RoomType[][] roomTypes;

    private DungeonLayout(int[][] walls, RoomType[][] roomTypes) {
        this.walls = walls;
        this.roomTypes = roomTypes;
    }

    public static DungeonLayout generate(Random random) {
        int[][] walls = MazeGenerator.carve(SIZE, random);
        MazeGenerator.addLoops(walls, SIZE, 0.13, random);

        RoomType[][] roomTypes = new RoomType[SIZE][SIZE];
        for (int c = 0; c < SIZE; c++) {
            for (int r = 0; r < SIZE; r++) {
                if (c == 0 && r == 0) {
                    roomTypes[c][r] = RoomType.START;
                } else if (c == SIZE - 1 && r == SIZE - 1) {
                    roomTypes[c][r] = RoomType.BOSS;
                } else {
                    roomTypes[c][r] = randomRoomType(random);
                }
            }
        }
        return new DungeonLayout(walls, roomTypes);
    }

    private static RoomType randomRoomType(Random random) {
        return switch (random.nextInt(8)) {
            case 0 -> RoomType.EMPTY_TREASURE;
            case 1 -> RoomType.GOBLIN_PAIR;
            case 2 -> RoomType.GOBLIN_TRIO;
            case 3 -> RoomType.ORK;
            case 4 -> RoomType.GOBLIN_ORK;
            case 5 -> RoomType.WAECHTER;
            case 6 -> RoomType.GOBLIN_WAECHTER;
            default -> RoomType.WAECHTER_PAIR;
        };
    }

    public boolean hasWall(int col, int row, int wallBit) {
        return MazeGenerator.hasWall(walls, col, row, wallBit);
    }

    public static double cellCenterX(int col) {
        return col * CELL_SIZE + CELL_SIZE / 2.0;
    }

    public static double cellCenterZ(int row) {
        return row * CELL_SIZE + CELL_SIZE / 2.0;
    }

    /** Erzeugt die Monster-Population für alle Zellen (an ihrer Zellmitte mit leichtem Jitter positioniert). */
    public List<Monster> spawnAllMonsters(Random random) {
        List<Monster> monsters = new ArrayList<>();
        for (int c = 0; c < SIZE; c++) {
            for (int r = 0; r < SIZE; r++) {
                for (MonsterType type : monsterTypesFor(roomTypes[c][r])) {
                    Monster m = new Monster(type, c, r);
                    // Bewusst deutlich kleiner als der begehbare 3x3-Block-Innenbereich einer Zelle,
                    // damit auch das größte Monster (Ork-König) nie in einem Wandblock spawnt.
                    double jitterX = (random.nextDouble() - 0.5) * CELL_SIZE * 0.3;
                    double jitterZ = (random.nextDouble() - 0.5) * CELL_SIZE * 0.3;
                    m.x = cellCenterX(c) + jitterX;
                    m.z = cellCenterZ(r) + jitterZ;
                    m.facingYawDeg = random.nextDouble() * 360;
                    monsters.add(m);
                }
            }
        }
        return monsters;
    }

    private static List<MonsterType> monsterTypesFor(RoomType roomType) {
        return switch (roomType) {
            case START -> List.of(MonsterType.GOBLIN, MonsterType.GOBLIN);
            case BOSS -> List.of(MonsterType.ORK_KOENIG);
            case EMPTY_TREASURE -> List.of();
            case GOBLIN_PAIR -> List.of(MonsterType.GOBLIN, MonsterType.GOBLIN);
            case GOBLIN_TRIO -> List.of(MonsterType.GOBLIN, MonsterType.GOBLIN, MonsterType.GOBLIN);
            case ORK -> List.of(MonsterType.ORK);
            case GOBLIN_ORK -> List.of(MonsterType.GOBLIN, MonsterType.ORK);
            case WAECHTER -> List.of(MonsterType.WAECHTER);
            case GOBLIN_WAECHTER -> List.of(MonsterType.GOBLIN, MonsterType.WAECHTER);
            case WAECHTER_PAIR -> List.of(MonsterType.WAECHTER, MonsterType.WAECHTER);
        };
    }

    public boolean isTreasureRoom(int col, int row) {
        return roomTypes[col][row] == RoomType.EMPTY_TREASURE;
    }
}
