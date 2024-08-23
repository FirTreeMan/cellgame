package creature.cell.cells.foodcells;

import creature.Creature;
import creature.cell.FoodCell;
import util.Cells;

public class TrapCell extends FoodCell {
    public static int SLOWDOWN_TICKS = 2;

    public TrapCell() {
        super(Cells.TRAP, 50, 100, false);
    }

    @Override
    public void onEaten(Creature creature) {
        creature.slowDown(SLOWDOWN_TICKS);

        super.onEaten(creature);
    }

    @Override
    public String getName() {
        return "Trap";
    }
}
