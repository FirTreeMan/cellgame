package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.EdibleCell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;
import util.Cells;

public class MouthCell extends LivingCell {
    public MouthCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.MOUTH, 5, 0, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[][] mat) {
        for (Cell cell: getBorderingCells(mat))
            if (cell instanceof EdibleCell edibleCell && edibleCell.isAlive() && cell.getOwner() != getOwner())
                getOwner().eat(edibleCell);
    }

    @Override
    public String toSpeciesString() {
        return "M";
    }

    @Override
    public String getName() {
        return "Mouth";
    }
}
