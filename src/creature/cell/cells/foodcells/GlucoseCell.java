package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class GlucoseCell extends FoodCell {
    public GlucoseCell() {
        super(Cells.GLUCOSE, 40, false);
    }

    @Override
    public String getName() {
        return "Glucose";
    }
}
