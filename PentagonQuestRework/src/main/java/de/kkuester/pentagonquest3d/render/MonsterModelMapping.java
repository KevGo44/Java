package de.kkuester.pentagonquest3d.render;

import de.kkuester.pentagonquest3d.assets.ModelLibrary;
import de.kkuester.pentagonquest3d.entities.MonsterType;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

import java.util.EnumMap;
import java.util.Map;

/**
 * Da das CC0-Monster-Set (Quaternius) nicht ohne itch.io-„Pay what you want"-Claim-Flow
 * automatisiert herunterladbar war (siehe Session-Notiz), werden die vier Gegnertypen
 * stattdessen durch das echte Kenney-Charaktermodell ({@code character-orc.obj}) in
 * unterschiedlichen Größen/Farbtönen dargestellt statt durch reine Code-Primitive -
 * bleibt reale heruntergeladene Geometrie, nur eingefärbt/skaliert statt separater Meshes.
 */
public final class MonsterModelMapping {

    private static final Map<MonsterType, Color> TINTS = new EnumMap<>(MonsterType.class);

    static {
        TINTS.put(MonsterType.GOBLIN, Color.rgb(120, 200, 110));
        TINTS.put(MonsterType.ORK, Color.rgb(90, 150, 90));
        TINTS.put(MonsterType.WAECHTER, Color.rgb(160, 165, 175));
        TINTS.put(MonsterType.ORK_KOENIG, Color.rgb(150, 40, 40));
    }

    private MonsterModelMapping() {
    }

    public static final String NORMAL_MATERIAL_KEY = "normalMaterial";
    public static final String FLASH_MATERIAL_KEY = "flashMaterial";

    public static MeshView createView(ModelLibrary models, MonsterType type) {
        MeshView view = models.createView(ModelLibrary.Model.CHARACTER_ORC);
        PhongMaterial tinted = new PhongMaterial(TINTS.get(type));
        PhongMaterial flash = new PhongMaterial(Color.rgb(255, 245, 220));
        if (view.getMaterial() instanceof PhongMaterial base && base.getDiffuseMap() != null) {
            tinted.setDiffuseMap(base.getDiffuseMap());
            flash.setDiffuseMap(base.getDiffuseMap());
        }
        view.setMaterial(tinted);
        view.getProperties().put(NORMAL_MATERIAL_KEY, tinted);
        view.getProperties().put(FLASH_MATERIAL_KEY, flash);
        double s = type.meshScale;
        view.setScaleX(s);
        view.setScaleY(s);
        view.setScaleZ(s);
        return view;
    }

    public static void applyFlash(MeshView view, boolean flashing) {
        Object key = flashing ? FLASH_MATERIAL_KEY : NORMAL_MATERIAL_KEY;
        Object material = view.getProperties().get(key);
        if (material instanceof PhongMaterial pm) {
            view.setMaterial(pm);
        }
    }
}
