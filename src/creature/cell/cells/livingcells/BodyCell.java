package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class BodyCell extends LivingCell {
    public BodyCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.BODY, 2, 0, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "B";
    }

    @Override
    public String getName() {
        return "Body";
    }
}
