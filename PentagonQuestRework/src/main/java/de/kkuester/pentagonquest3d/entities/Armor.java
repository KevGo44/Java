package de.kkuester.pentagonquest3d.entities;

public class Armor extends Item {
    public final int defense;

    public Armor(int weight, int worth, int defense, String name) {
        super(weight, worth, name);
        this.defense = defense;
    }
}
