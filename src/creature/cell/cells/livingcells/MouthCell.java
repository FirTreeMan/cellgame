package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;
import util.Cells;

public class MouthCell extends LivingCell {
    public MouthCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.MOUTH.get(), 5, 5, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[][] mat) {
        for (Cell cell: getBorderingCells(mat))
            if (cell instanceof FoodCell foodCell && foodCell.isAlive() && cell.getOwner() != getOwner())
                getOwner().eat(foodCell);
    }

    @Override
    public String toSpeciesString() {
        return "M";
    }

    @Override
    public String getName() {
        return "Mouth";
    }

    @Override
    public String getDescription() {
        return "Allows a creature to eat food.\nQuite important.";
    }
}
