package de.kkuester.pentagonquest3d.assets;

import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;

import java.util.EnumMap;
import java.util.Map;

/** Lädt alle benötigten Kenney-„Mini Dungeon"-Modelle einmalig beim Start und cached sie. */
public class ModelLibrary {

    public enum Model {
        WALL("wall.obj"),
        WALL_HALF("wall-half.obj"),
        WALL_NARROW("wall-narrow.obj"),
        WALL_OPENING("wall-opening.obj"),
        FLOOR("floor.obj"),
        FLOOR_DETAIL("floor-detail.obj"),
        COLUMN("column.obj"),
        STAIRS("stairs.obj"),
        CHEST("chest.obj"),
        BARREL("barrel.obj"),
        TABLE("table.obj"),
        CHAIR("chair.obj"),
        BANNER("banner.obj"),
        ROCKS("rocks.obj"),
        POT("pot.obj"),
        GATE("gate.obj"),
        SHIELD_ROUND("shield-round.obj"),
        SHIELD_RECTANGLE("shield-rectangle.obj"),
        WEAPON_SWORD("weapon-sword.obj"),
        WEAPON_SPEAR("weapon-spear.obj"),
        CHARACTER_ORC("character-orc.obj"),
        CHARACTER_HUMAN("character-human.obj");

        final String fileName;

        Model(String fileName) {
            this.fileName = fileName;
        }
    }

    private static final String BASE_PATH = "/de/kkuester/pentagonquest3d/models/mini_dungeon";

    private final Map<Model, ObjModelLoader.LoadedModel> cache = new EnumMap<>(Model.class);

    public void loadAll() {
        for (Model model : Model.values()) {
            cache.put(model, ObjModelLoader.load(ModelLibrary.class, BASE_PATH, model.fileName));
        }
    }

    public MeshView createView(Model model) {
        ObjModelLoader.LoadedModel loaded = cache.get(model);
        if (loaded == null) {
            throw new IllegalStateException("Modell nicht geladen: " + model + " (loadAll() vergessen?)");
        }
        MeshView view = new MeshView(loaded.mesh());
        view.setMaterial(loaded.material());
        view.setCullFace(CullFace.BACK);
        return view;
    }
}
