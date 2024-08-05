package creature.cell;

import creature.Creature;

import java.awt.*;

public abstract class Cell {
    private final Creature owner;
    private final Color color;

    public Cell(Creature owner, Color color) {
        this.owner = owner;
        this.color = color;
    }

    public Creature getOwner() {
        return owner;
    }

    public Color getColor() {
        return color;
    }
}
