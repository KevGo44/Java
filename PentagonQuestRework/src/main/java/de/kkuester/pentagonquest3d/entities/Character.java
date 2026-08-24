package de.kkuester.pentagonquest3d.entities;

/** Basis für Spieler und Monster: Position im Raum (XZ = Boden-Ebene, Y = Höhe) und Kern-Stats. */
public abstract class Character {
    public double x, y, z;
    public double radius = 0.35;

    public int hp;
    public int maxHp;
    public int attackDamage;
    public final String name;

    protected Character(int hp, int attackDamage, String name) {
        this.hp = hp;
        this.maxHp = hp;
        this.attackDamage = attackDamage;
        this.name = name;
    }

    public void applyDamage(int dmg) {
        hp = Math.max(0, hp - dmg);
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public double distanceTo(Character other) {
        double dx = x - other.x, dz = z - other.z;
        return Math.sqrt(dx * dx + dz * dz);
    }
}
