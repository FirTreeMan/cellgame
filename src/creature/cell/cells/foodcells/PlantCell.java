package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class PlantCell extends FoodCell {
    public PlantCell() {
        super(Cells.PLANT, 200, -1, false);
    }

    @Override
    public String getName() {
        return "Plant";
    }
}
