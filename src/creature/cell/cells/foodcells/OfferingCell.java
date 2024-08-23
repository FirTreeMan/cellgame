package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

public class OfferingCell extends FoodCell {
    public OfferingCell() {
        this(10);
    }

    public OfferingCell(int foodValue) {
        super(Cells.OFFERING, foodValue, -1, false);
    }

    @Override
    public String getName() {
        return "Glucose";
    }
}
