package creature.cell.cells.foodcells;

import creature.Creature;
import creature.cell.FoodCell;
import creature.cell.HatchableCell;

import java.awt.*;

public class EggCell extends FoodCell implements HatchableCell {
    private final Creature creature;
    private final int incubationTicks;

    private int ticksPassed;

    public EggCell(Creature creature) {
        super(new Color(237, 198, 26), 1000, true);

        this.creature = creature;
        this.incubationTicks = creature.getCellCount();
        this.ticksPassed = 0;
    }

    @Override
    public void tick() {
        ticksPassed++;
    }

    @Override
    public Creature getCreature() {
        return creature;
    }

    @Override
    public boolean canHatch() {
        return ticksPassed >= incubationTicks;
    }
}
