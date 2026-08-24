package de.kkuester.pentagonquest3d.entities;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTypeTest {

    @Test
    void goblin_alwaysDropsExactlyOneItem() {
        Random random = new Random(1);
        for (int i = 0; i < 50; i++) {
            List<Item> drops = MonsterType.GOBLIN.rollDrops(random);
            assertEquals(1, drops.size());
        }
    }

    @Test
    void ork_alwaysDropsAtLeastAHealthPotion() {
        Random random = new Random(2);
        for (int i = 0; i < 50; i++) {
            List<Item> drops = MonsterType.ORK.rollDrops(random);
            assertTrue(drops.size() >= 1);
            assertInstanceOf(HPPlus.class, drops.get(0));
        }
    }

    @Test
    void waechter_alwaysDropsALongsword() {
        Random random = new Random(3);
        for (int i = 0; i < 50; i++) {
            List<Item> drops = MonsterType.WAECHTER.rollDrops(random);
            assertFalse(drops.isEmpty());
            assertInstanceOf(Sword.class, drops.get(0));
            assertEquals("Langschwert", drops.get(0).name);
        }
    }

    @Test
    void orkKoenig_neverDropsAnything() {
        Random random = new Random(4);
        List<Item> drops = MonsterType.ORK_KOENIG.rollDrops(random);
        assertTrue(drops.isEmpty());
    }

    @Test
    void monster_takesDamageAndDies() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        assertFalse(m.isDead());
        m.applyDamage(m.maxHp);
        assertTrue(m.isDead());
    }
}
