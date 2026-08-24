package de.kkuester.pentagonquest3d.entities;

import java.util.ArrayList;
import java.util.List;

/** Port der Python-Player-Klasse (XP-Kurve, Ausrüstung, Inventar) + Echtzeit-Kampf-Ressourcen. */
public class Player extends Character {
    public static final int XP_PER_LEVEL = 120;
    public static final int HP_PER_LEVEL = 30;
    public static final int ATK_PER_LEVEL = 8;

    public final int adBase0 = 22;
    public int adBase = adBase0;
    public int defense = 0;
    public final List<Item> inventory = new ArrayList<>();
    public final int maxInventory = 8;
    public Sword equippedWeapon;
    public Armor equippedArmor;
    public int xp = 0;
    public int level = 1;

    // Kamera / Bewegung
    public double yawDeg;
    public double pitchDeg;
    public double velocityX, velocityZ;

    // Kampf-Ressourcen
    public double stamina = 100;
    public final double maxStamina = 100;
    public double attackCooldown;
    public double dodgeCooldown;
    public double invulnerableTimer;
    public boolean blocking;

    public Player(String name) {
        super(120, 22, name);
    }

    public String equip(Item item) {
        if (item instanceof Sword sword) {
            if (equippedWeapon != null) equippedWeapon.equipped = false;
            equippedWeapon = sword;
            item.equipped = true;
            attackDamage = adBase + sword.attackDamage;
            return "'" + item.name + "' ausgerüstet! ATK: " + attackDamage;
        } else if (item instanceof Armor armor) {
            if (equippedArmor != null) equippedArmor.equipped = false;
            equippedArmor = armor;
            item.equipped = true;
            defense = armor.defense;
            return "'" + item.name + "' ausgerüstet! DEF: " + defense;
        }
        return "Kann nicht ausgerüstet werden.";
    }

    public String useItem(Item item) {
        if (item instanceof HPPlus potion) {
            if (hp >= maxHp) {
                return "HP bereits voll! (" + hp + "/" + maxHp + ")";
            }
            int restored = Math.min(potion.hpPlus, maxHp - hp);
            hp += restored;
            inventory.remove(item);
            return "'" + item.name + "' benutzt! +" + restored + " HP (" + hp + "/" + maxHp + ")";
        }
        return "Kann nicht benutzt werden.";
    }

    public void dropItem(Item item) {
        if (item == equippedWeapon) {
            equippedWeapon = null;
            attackDamage = adBase;
        } else if (item == equippedArmor) {
            equippedArmor = null;
            defense = 0;
        }
        item.equipped = false;
        inventory.remove(item);
    }

    /** Schaden unter Berücksichtigung der Verteidigung; gibt den tatsächlich erlittenen Schaden zurück. */
    public int receiveHit(int rawDamage) {
        int actual = Math.max(1, rawDamage - defense);
        if (blocking) {
            actual = Math.max(0, actual / 3);
        }
        hp = Math.max(0, hp - actual);
        return actual;
    }

    public void rest() {
        hp = maxHp;
    }

    public int xpNeeded() {
        return level * XP_PER_LEVEL;
    }

    public List<String> gainXp(int amount) {
        xp += amount;
        List<String> messages = new ArrayList<>();
        while (xp >= xpNeeded()) {
            xp -= xpNeeded();
            level++;
            maxHp += HP_PER_LEVEL;
            hp = maxHp;
            adBase += ATK_PER_LEVEL;
            attackDamage = adBase + (equippedWeapon != null ? equippedWeapon.attackDamage : 0);
            messages.add("* Level Up! Du bist jetzt Level " + level + "! +" + HP_PER_LEVEL
                    + " Max-HP  +" + ATK_PER_LEVEL + " ATK  HP aufgefüllt!");
        }
        return messages;
    }
}
