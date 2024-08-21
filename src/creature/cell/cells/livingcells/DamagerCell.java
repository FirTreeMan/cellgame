package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.Cells;

public class DamagerCell extends LivingCell {
    public DamagerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.DAMAGER.get(), 10, 5, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[][] mat) {
        for (Cell cell: getBorderingCells(mat))
            if (cell instanceof LivingCell livingCell && cell.getOwner() != getOwner() && !(livingCell instanceof DamagerCell))
                getOwner().hurt(livingCell.getOwner());
    }

    @Override
    public String toSpeciesString() {
        return "D";
    }

    @Override
    public String getName() {
        return "Damager";
    }

    @Override
    public String getDescription() {
        return Cells.DAMAGER.getDescription();
    }
}
