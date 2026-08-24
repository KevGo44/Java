package de.kkuester.pentagonquest3d.entities;

public class HPPlus extends Item {
    public final int hpPlus;

    public HPPlus(int hpPlus, String name) {
        super(0, 50, name);
        this.hpPlus = hpPlus;
    }
}
