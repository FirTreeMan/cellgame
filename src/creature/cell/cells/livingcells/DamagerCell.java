package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.EdibleCell;
import creature.cell.LivingCell;
import grid.Grid;
import util.Cells;

import java.util.Set;

public class DamagerCell extends LivingCell {
    public static Set<Class<? extends LivingCell>> CELLS_IMMUNE = Set.of(
            DamagerCell.class,
            ChitinCell.class
    );

    public DamagerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.DAMAGER, 10, 5, relativeRow, relativeCol);
    }

    @Override
    public void tick(Grid grid) {
        for (Cell cell: getBorderingCells(grid)) {
            if (cell instanceof LivingCell livingCell && cell.getOwner() != getOwner() && !CELLS_IMMUNE.contains(cell.getClass()))
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
