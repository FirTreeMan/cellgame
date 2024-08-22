package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class MeatCell extends FoodCell {
    public MeatCell() {
        super(Cells.MEAT, 500, true);
    }

    @Override
    public String getName() {
        return "Meat";
    }
}
