package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.GlucoseCell;
import creature.cell.cells.foodcells.RotCell;
import grid.Grid;
import util.CellAttrs;
import util.Cells;

public class ChloroplastCell extends LivingCell {
    public static int PRODUCTION_TICKS = 2;
    private int ticksToProduce;

    public ChloroplastCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.CHLOROPLAST, 15, 10, relativeRow, relativeCol);
        this.ticksToProduce = 0;
    }

    @Override
    public void tick(Grid grid) {
        ticksToProduce++;
        if (ticksToProduce >= PRODUCTION_TICKS) {
            ticksToProduce = 0;
            getOwner().eat(new GlucoseCell());
        }

        for (Cell cell: getBorderingCells(grid))
            if (cell instanceof RotCell rotCell) {
                rotCell.kill();
                getOwner().eat(new GlucoseCell());
            }
    }

    @Override
    public String toSpeciesString() {
        return "C";
    }

    @Override
    public String getAttr(CellAttrs attr) {
        if (attr == CellAttrs.TIMER) return String.valueOf(ticksToProduce);
        return super.getAttr(attr);
    }

    @Override
    public String getName() {
        return "Chloroplast";
    }
}
