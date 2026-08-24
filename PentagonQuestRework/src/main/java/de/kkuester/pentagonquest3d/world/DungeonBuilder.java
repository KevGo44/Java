package de.kkuester.pentagonquest3d.world;

import de.kkuester.pentagonquest3d.assets.ModelLibrary;
import de.kkuester.pentagonquest3d.assets.ModelLibrary.Model;
import javafx.scene.Group;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.util.Random;

/**
 * Übersetzt die abstrakte Maze-Topologie (DungeonLayout) in echte 3D-Geometrie aus dem
 * Kenney-„Mini Dungeon"-Kit. Das Kit ist block-basiert (jedes Modell füllt eine volle
 * 1x1x1-Zelle statt nur eine dünne Kante), daher wird jede Maze-Zelle als kleiner
 * {@value #ROOM_BLOCKS}x{@value #ROOM_BLOCKS}-Raum aus Kenney-Blöcken gebaut: ein 3x3-Block
 * großes Bodenfeld in der Mitte (genug Platz für Echtzeit-Kampf/Ausweichen), umgeben von
 * einem Wand-Ring. Am exakten Mitten-Block jeder der 4 Kanten sitzt je nach Maze-Wanddaten
 * entweder eine massive Wand oder ein Durchgang ({@code wall-opening.obj}) zum Nachbarraum;
 * alle anderen Randblöcke sind feste Wand, die vier Ecken sind Säulen.
 */
public class DungeonBuilder {

    public static final double BLOCK_SIZE = 1.0;
    public static final int ROOM_BLOCKS = 5;
    public static final double ROOM_SIZE = ROOM_BLOCKS * BLOCK_SIZE;
    public static final double WALL_HEIGHT = 1.0;
    private static final int LAST = ROOM_BLOCKS - 1;
    private static final int CENTER = ROOM_BLOCKS / 2;

    private final ModelLibrary models;

    public DungeonBuilder(ModelLibrary models) {
        this.models = models;
    }

    public Group build(DungeonLayout layout, Random random) {
        Group root = new Group();
        for (int col = 0; col < DungeonLayout.SIZE; col++) {
            for (int row = 0; row < DungeonLayout.SIZE; row++) {
                buildRoom(root, layout, col, row, random);
            }
        }
        return root;
    }

    private void buildRoom(Group root, DungeonLayout layout, int col, int row, Random random) {
        double originX = col * ROOM_SIZE;
        double originZ = row * ROOM_SIZE;

        for (int bx = 0; bx < ROOM_BLOCKS; bx++) {
            for (int bz = 0; bz < ROOM_BLOCKS; bz++) {
                double wx = originX + bx * BLOCK_SIZE + BLOCK_SIZE / 2.0;
                double wz = originZ + bz * BLOCK_SIZE + BLOCK_SIZE / 2.0;
                boolean isPerimeter = bx == 0 || bx == LAST || bz == 0 || bz == LAST;
                if (!isPerimeter) {
                    addFloor(root, wx, wz);
                    addCeiling(root, wx, wz);
                    continue;
                }
                placePerimeterBlock(root, layout, col, row, bx, bz, wx, wz);
            }
        }

        double centerX = originX + CENTER + 0.5;
        double centerZ = originZ + CENTER + 0.5;
        if (layout.isTreasureRoom(col, row)) {
            addChest(root, centerX, centerZ, random);
        } else if (random.nextDouble() < 0.35) {
            addAmbientProp(root, centerX, centerZ, random);
        }
    }

    /**
     * Entscheidet für einen Randblock, ob dort eine Säule (Ecke), eine feste Wand, oder -
     * exakt am Mitten-Block einer Kante - ein Wand/Durchgang-Block je nach Maze-Wanddaten
     * sitzt. Muss dieselbe Solid/Open-Logik wie {@link DungeonOccupancy#isBlockSolid} treffen.
     *
     * ANNAHME zur wall-opening.obj-Ausrichtung (nicht visuell verifiziert, siehe Nutzer-Vorgabe
     * "kein eigenes Starten der App"): Standardmäßig entlang der Z-Achse orientiert (passend für
     * Nord/Süd ohne Drehung), für Ost/West zusätzlich um 90° gedreht. Falls die Öffnung beim
     * ersten Testlauf falsch herum steht, hier die {@code extraYRotation}-Werte um 90° verschieben.
     */
    private void placePerimeterBlock(Group root, DungeonLayout layout, int col, int row,
                                      int bx, int bz, double worldX, double worldZ) {
        boolean isCorner = (bx == 0 || bx == LAST) && (bz == 0 || bz == LAST);
        if (isCorner) {
            addColumn(root, worldX, worldZ);
            return;
        }
        Integer wallBit = null;
        double extraYRotation = 0;
        if (bz == 0 && bx == CENTER) {
            wallBit = MazeGenerator.WALL_N;
        } else if (bz == LAST && bx == CENTER) {
            wallBit = MazeGenerator.WALL_S;
        } else if (bx == 0 && bz == CENTER) {
            wallBit = MazeGenerator.WALL_W;
            extraYRotation = 90;
        } else if (bx == LAST && bz == CENTER) {
            wallBit = MazeGenerator.WALL_E;
            extraYRotation = 90;
        }

        Model model;
        if (wallBit == null) {
            model = Model.WALL; // Randblock ohne Torfunktion: immer feste Wand
        } else {
            model = layout.hasWall(col, row, wallBit) ? Model.WALL : Model.WALL_OPENING;
        }
        MeshView view = models.createView(model);
        if (extraYRotation != 0) {
            view.getTransforms().add(new Rotate(extraYRotation, Rotate.Y_AXIS));
        }
        view.setTranslateX(worldX);
        view.setTranslateZ(worldZ);
        root.getChildren().add(view);
    }

    private void addFloor(Group root, double worldX, double worldZ) {
        MeshView floor = models.createView(Model.FLOOR);
        floor.setTranslateX(worldX);
        floor.setTranslateZ(worldZ);
        root.getChildren().add(floor);
    }

    private void addCeiling(Group root, double worldX, double worldZ) {
        MeshView ceiling = models.createView(Model.FLOOR);
        ceiling.getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        ceiling.setTranslateX(worldX);
        ceiling.setTranslateY(WALL_HEIGHT);
        ceiling.setTranslateZ(worldZ);
        root.getChildren().add(ceiling);
    }

    private void addColumn(Group root, double worldX, double worldZ) {
        MeshView column = models.createView(Model.COLUMN);
        column.setTranslateX(worldX);
        column.setTranslateZ(worldZ);
        root.getChildren().add(column);
    }

    private void addChest(Group root, double worldX, double worldZ, Random random) {
        MeshView chest = models.createView(Model.CHEST);
        chest.getTransforms().add(new Rotate(random.nextInt(4) * 90, Rotate.Y_AXIS));
        chest.setTranslateX(worldX);
        chest.setTranslateZ(worldZ);
        root.getChildren().add(chest);
    }

    private void addAmbientProp(Group root, double worldX, double worldZ, Random random) {
        Model[] props = {Model.BARREL, Model.TABLE, Model.BANNER, Model.ROCKS, Model.POT};
        Model chosen = props[random.nextInt(props.length)];
        MeshView prop = models.createView(chosen);
        prop.getTransforms().add(new Rotate(random.nextInt(4) * 90, Rotate.Y_AXIS));
        prop.setTranslateX(worldX + (random.nextDouble() - 0.5) * 1.2);
        prop.setTranslateZ(worldZ + (random.nextDouble() - 0.5) * 1.2);
        root.getChildren().add(prop);
    }
}
