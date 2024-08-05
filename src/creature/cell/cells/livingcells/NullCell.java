package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;

import java.awt.*;

public class NullCell extends LivingCell {
    public NullCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, new Color(255, 255, 255), 0, 0, relativeRow, relativeCol);
    }
}
