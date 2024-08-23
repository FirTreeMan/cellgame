package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class ChitinCell extends LivingCell {
    public ChitinCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.CHITIN, 5, 5, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "H";
    }

    @Override
    public String getName() {
        return "Chitin";
    }
}
