package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class MeatCell extends FoodCell {
    public MeatCell() {
        this(500);
    }

    public MeatCell(int foodValue) {
        super(Cells.MEAT, foodValue + 100, 40, true);
    }

    @Override
    public String getName() {
        return "Meat";
    }
}
