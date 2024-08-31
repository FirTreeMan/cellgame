package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class GlucoseCell extends FoodCell {
    public GlucoseCell() {
        super(Cells.GLUCOSE, 18, -1, false);
    }

    @Override
    public String getName() {
        return "Glucose";
    }
}
