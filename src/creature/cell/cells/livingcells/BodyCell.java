package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;

import java.awt.*;

public class BodyCell extends LivingCell {
    public BodyCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(252, 172, 35), 5, 5, relativeRow, relativeCol);
    }
}
