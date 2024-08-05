package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;

import java.awt.*;

public class LegCell extends LivingCell {
    public static int MAX_CARRY = 5;

    public LegCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(49, 238, 245), 5, 0, relativeRow, relativeCol);
    }
}
