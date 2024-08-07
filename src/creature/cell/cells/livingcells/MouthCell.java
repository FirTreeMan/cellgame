package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;

import java.awt.*;

public class MouthCell extends LivingCell {
    public MouthCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(69, 130, 209), 5, 5, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[][] mat) {
        for (Cell cell: getBorderingCells(mat))
            if (cell instanceof FoodCell foodCell && cell.getOwner() != getOwner())
                getOwner().eat(foodCell);
    }
}
