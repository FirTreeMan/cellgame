package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class NullCell extends LivingCell {
    public NullCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.NULL.get(), 0, 0, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "N";
    }

    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public String getDescription() {
        return "Hell.";
    }
}
