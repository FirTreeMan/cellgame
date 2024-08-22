package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import util.Cells;

public class LegCell extends LivingCell {
    public static int MAX_CARRY = 5;

    public LegCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.LEG, 5, 0, relativeRow, relativeCol);
    }

    @Override
    public String toSpeciesString() {
        return "L";
    }

    @Override
    public String getName() {
        return "Leg";
    }
}
