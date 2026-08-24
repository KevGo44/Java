package de.kkuester.pentagonquest3d.entities;

public class Item {
    public final int weight;
    public final int worth;
    public final String name;
    public boolean equipped;

    public Item(int weight, int worth, String name) {
        this.weight = weight;
        this.worth = worth;
        this.name = name;
    }
}
