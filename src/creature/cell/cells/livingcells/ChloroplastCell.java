package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.GlucoseCell;

import java.awt.*;

public class ChloroplastCell extends LivingCell {
    public static int PRODUCTION_TICKS = 5;
    private int ticksToProduce;

    public ChloroplastCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(43, 194, 156), 15, 10, relativeRow, relativeCol);
        ticksToProduce = 0;
    }

    @Override
    public void tick(Cell[][] mat) {
        ticksToProduce++;
        if (ticksToProduce >= PRODUCTION_TICKS) {
            ticksToProduce = 0;
            getOwner().eat(new GlucoseCell());
        }
    }
}
