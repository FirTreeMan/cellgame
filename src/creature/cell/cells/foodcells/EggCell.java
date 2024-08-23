package creature.cell.cells.foodcells;

import creature.Creature;
import creature.cell.FoodCell;
import creature.cell.HatchableCell;
import util.Cells;

import java.awt.*;

public class EggCell extends FoodCell implements HatchableCell {
    private final Creature creature;
    private final int incubationTicks;

    private int ticksPassed;

    public EggCell(Creature creature) {
        this(creature, 1000);
    }

    public EggCell(Creature creature, int foodValue) {
        super(Cells.EGG, foodValue, creature.getCellCount() * 2, true);

        this.creature = creature;
        this.incubationTicks = creature.getCellCount();
        this.ticksPassed = 0;
    }

    @Override
    public String getName() {
        return "Egg";
    }

    @Override
    public void tick() {
        ticksPassed++;
        super.tick();
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
