package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class BodyCell extends LivingCell {
    public BodyCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.BODY.get(), 5, 5, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "B";
    }

    @Override
    public String getName() {
        return "Body";
    }

    @Override
    public String getDescription() {
        return Cells.BODY.getDescription();
    }
}
