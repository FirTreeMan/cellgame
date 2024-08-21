package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class LegCell extends LivingCell {
    public static int MAX_CARRY = 5;

    public LegCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.LEG.get(), 5, 0, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "L";
    }

    @Override
    public String getName() {
        return "Leg";
    }

    @Override
    public String getDescription() {
        return Cells.LEG.getDescription();
    }
}
