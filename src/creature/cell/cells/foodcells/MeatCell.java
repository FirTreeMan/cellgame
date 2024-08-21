package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class MeatCell extends FoodCell {
    public MeatCell() {
        super(Cells.MEAT.get(), 1000, true);
    }

    @Override
    public String getName() {
        return "Meat";
    }

    @Override
    public String getDescription() {
        return Cells.MEAT.getDescription();
    }
}
