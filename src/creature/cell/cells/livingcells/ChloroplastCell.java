package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.GlucoseCell;
import util.CellAttrs;
import util.Cells;

public class ChloroplastCell extends LivingCell {
    public static int PRODUCTION_TICKS = 2;
    private int ticksToProduce;

    public ChloroplastCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.CHLOROPLAST.get(), 15, 10, relativeRow, relativeCol);
        ticksToProduce = 0;
    }

    @Override
    public void tick(Cell[][] mat) {
        ticksToProduce++;
        if (ticksToProduce >= PRODUCTION_TICKS) {
            ticksToProduce = 0;
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

    @Override
    public String getDescription() {
        return Cells.CHLOROPLAST.getDescription();
    }
}
