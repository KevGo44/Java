package de.kkuester.pentagonquest3d.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Gegnertypen mit Stats/Drop-Tabellen - 1:1 aus der Python-Version (pentagonquest/models.py) übernommen. */
public enum MonsterType {
    GOBLIN(40, 9, 30, "Goblin", 0.62, 0.55),
    ORK(200, 26, 85, "Ork", 1.0, 0.65),
    WAECHTER(480, 20, 160, "Wächter", 1.05, 0.68),
    ORK_KOENIG(1800, 50, 800, "Ork-König", 1.35, 0.95);

    public final int hp;
    public final int attackDamage;
    public final int xp;
    public final String displayName;
    /** Skalierungsfaktoren für das (wiederverwendete) Kenney-Orc-Mesh, um Gegnertypen optisch zu unterscheiden. */
    public final double meshScale;
    public final double meleeRange;

    MonsterType(int hp, int attackDamage, int xp, String displayName, double meshScale, double meleeRange) {
        this.hp = hp;
        this.attackDamage = attackDamage;
        this.xp = xp;
        this.displayName = displayName;
        this.meshScale = meshScale;
        this.meleeRange = meleeRange;
    }

    public List<Item> rollDrops(Random random) {
        List<Item> drops = new ArrayList<>();
        switch (this) {
            case GOBLIN -> {
                if (random.nextDouble() < 0.55) {
                    drops.add(new HPPlus(40, "Kleiner Heiltrank"));
                } else {
                    drops.add(new Sword(1, 25, 18, "Rostiges Schwert"));
                }
            }
            case ORK -> {
                drops.add(new HPPlus(75, "Heiltrank"));
                if (random.nextDouble() < 0.50) {
                    drops.add(new Sword(2, 80, 52, "Kurzschwert"));
                }
            }
            case WAECHTER -> {
                drops.add(new Sword(3, 150, 88, "Langschwert"));
                double r = random.nextDouble();
                if (r < 0.30) {
                    drops.add(new Armor(4, 220, 18, "Lederrüstung"));
                } else if (r < 0.55) {
                    drops.add(new HPPlus(120, "Großer Heiltrank"));
                }
            }
            case ORK_KOENIG -> {
                // kein Drop - der Sieg selbst ist die Belohnung
            }
        }
        return drops;
    }
}
