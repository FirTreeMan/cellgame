package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.OfferingCell;
import grid.Grid;
import util.Cells;

public class SharerCell extends LivingCell {
    public static int MAX_ENERGY_SHARED = 100;

    public SharerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.SHARER, 3, 3, relativeRow, relativeCol);
    }

    @Override
    public void tick(Grid grid) {
        for (Cell cell: getBorderingCells(grid)) {
            if (cell instanceof SharerCell) {
                int energyToGive = Math.min((this.getOwner().getEnergy() - cell.getOwner().getEnergy()) / 2, MAX_ENERGY_SHARED);
                energyToGive = Math.min(energyToGive, getOwner().getEnergy() / 2);
                if (energyToGive <= 0) continue;

                this.getOwner().useEnergy(energyToGive);
                cell.getOwner().eat(new OfferingCell(energyToGive));
            }
        }
    }

    @Override
    public String toSpeciesString() {
        return "S";
    }

    @Override
    public String getName() {
        return "Sharer";
    }
}
