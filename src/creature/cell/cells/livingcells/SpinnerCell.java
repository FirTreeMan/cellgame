package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.TrapCell;
import grid.Grid;
import util.Cells;

public class SpinnerCell extends LivingCell {
    public static int PRODUCTION_TICKS;
    private int ticksToProduce;

    public SpinnerCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.SPINNER, 6, 5, relativeRow, relativeCol);
        ticksToProduce = 0;
    }

    @Override
    public void tick(Grid grid) {
        ticksToProduce++;
        if (ticksToProduce >= PRODUCTION_TICKS) {
            TrapCell trapCell = new TrapCell();
            int row = getRow();
            int col = getCol();

            switch (getOwner().getFacing()) {
                case UP -> col++;
                case DOWN -> col--;
                case LEFT -> row++;
                case RIGHT -> row--;
            }

            if (grid.isEmptyAtCell(row, col) && getOwner().useEnergy(100)) {
                grid.addFood(trapCell, row, col);
                ticksToProduce = 0;
            }
        }
    }

    @Override
    public String toSpeciesString() {
        return "W";
    }

    @Override
    public String getName() {
        return "Spinner";
    }
}
