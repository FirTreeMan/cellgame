package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.BombCell;
import creature.cell.cells.foodcells.TrapCell;
import grid.Grid;
import util.Cells;

public class BomberCell extends LivingCell {
    public static int PRODUCTION_TICKS = 60;
    private int ticksToProduce;

    public BomberCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.BOMBER, 10, 10, relativeRow, relativeCol);
        ticksToProduce = 0;
    }

    @Override
    public void tick(Grid grid) {
        ticksToProduce++;
        if (ticksToProduce >= PRODUCTION_TICKS) {
            ticksToProduce = 0;

            BombCell bombCell = new BombCell();
            int row = getRow();
            int col = getCol();

            switch (getOwner().getFacing()) {
                case UP -> col--;
                case DOWN -> col++;
                case LEFT -> row--;
                case RIGHT -> row++;
            }

            getOwner().useEnergy(200);
            grid.addFood(bombCell, row, col);
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
