package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.EdibleCell;
import creature.cell.LivingCell;
import util.Cells;

public class DamagerCell extends LivingCell {
    public DamagerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.DAMAGER, 10, 10, relativeRow, relativeCol);
    }

    @Override
    public void tick(Cell[][] mat) {
        for (Cell cell: getBorderingCells(mat)) {
            if (cell instanceof LivingCell livingCell && cell.getOwner() != getOwner() && !(livingCell instanceof DamagerCell))
                getOwner().hurt(livingCell.getOwner());
            if (cell instanceof EdibleCell edibleCell && edibleCell.isAlive() && cell.getOwner() != getOwner())
                getOwner().eat(edibleCell);
        }
    }

    @Override
    public String toSpeciesString() {
        return "D";
    }

    @Override
    public String getName() {
        return "Damager";
    }
}
