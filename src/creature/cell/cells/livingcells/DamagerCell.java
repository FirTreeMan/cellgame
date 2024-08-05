package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;

import java.awt.*;

public class DamagerCell extends LivingCell {
    public DamagerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(209, 69, 69), 10, 5, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[] nearby) {
        for (Cell cell: nearby)
            if (cell instanceof LivingCell livingCell && !(livingCell instanceof DamagerCell))
                getOwner().hurt(livingCell.getOwner());
    }
}
