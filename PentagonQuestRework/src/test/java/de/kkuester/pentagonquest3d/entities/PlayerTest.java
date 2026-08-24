package de.kkuester.pentagonquest3d.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void gainXp_levelsUpAndRefillsHpWhenThresholdReached() {
        Player p = new Player("Hero");
        int startingMaxHp = p.maxHp;

        List<String> messages = p.gainXp(120);

        assertEquals(2, p.level);
        assertEquals(startingMaxHp + Player.HP_PER_LEVEL, p.maxHp);
        assertEquals(p.maxHp, p.hp, "HP soll nach Level-Up voll aufgefuellt sein");
        assertEquals(1, messages.size());
    }

    @Test
    void gainXp_canTriggerMultipleLevelsAtOnce() {
        Player p = new Player("Hero");
        List<String> messages = p.gainXp(120 + 240); // genug fuer Level 2 und 3
        assertEquals(3, p.level);
        assertEquals(2, messages.size());
    }

    @Test
    void equip_sword_increasesAttackDamage() {
        Player p = new Player("Hero");
        int baseAttack = p.attackDamage;
        Sword sword = new Sword(1, 25, 18, "Rostiges Schwert");

        p.equip(sword);

        assertEquals(baseAttack + 18, p.attackDamage);
        assertTrue(sword.equipped);
    }

    @Test
    void equip_secondSword_unequipsFirst() {
        Player p = new Player("Hero");
        Sword rusty = new Sword(1, 25, 18, "Rostiges Schwert");
        Sword longsword = new Sword(3, 150, 88, "Langschwert");

        p.equip(rusty);
        p.equip(longsword);

        assertFalse(rusty.equipped);
        assertTrue(longsword.equipped);
        assertEquals(p.adBase + 88, p.attackDamage);
    }

    @Test
    void receiveHit_defenseReducesDamage_neverBelowOne() {
        Player p = new Player("Hero");
        p.defense = 1000; // absurd hoch, um die Untergrenze zu testen
        int actual = p.receiveHit(5);
        assertEquals(1, actual);
    }

    @Test
    void receiveHit_blockingReducesDamageFurther() {
        Player p1 = new Player("Hero");
        Player p2 = new Player("Hero");
        p1.blocking = false;
        p2.blocking = true;

        int dmgNoBlock = p1.receiveHit(30);
        int dmgBlocked = p2.receiveHit(30);

        assertTrue(dmgBlocked < dmgNoBlock, "Blocken soll den Schaden reduzieren");
    }

    @Test
    void useItem_healingPotion_neverExceedsMaxHp() {
        Player p = new Player("Hero");
        p.hp = p.maxHp - 5;
        HPPlus potion = new HPPlus(9999, "Riesentrank");
        p.inventory.add(potion);

        p.useItem(potion);

        assertEquals(p.maxHp, p.hp);
        assertFalse(p.inventory.contains(potion));
    }

    @Test
    void dropItem_equippedWeapon_resetsAttackToBase() {
        Player p = new Player("Hero");
        Sword sword = new Sword(1, 25, 18, "Rostiges Schwert");
        p.inventory.add(sword);
        p.equip(sword);

        p.dropItem(sword);

        assertEquals(p.adBase, p.attackDamage);
        assertNull(p.equippedWeapon);
    }
}
