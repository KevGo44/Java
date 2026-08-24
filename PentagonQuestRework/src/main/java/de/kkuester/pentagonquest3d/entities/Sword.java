package de.kkuester.pentagonquest3d.entities;

public class Sword extends Item {
    public final int attackDamage;

    public Sword(int weight, int worth, int attackDamage, String name) {
        super(weight, worth, name);
        this.attackDamage = attackDamage;
    }
}
