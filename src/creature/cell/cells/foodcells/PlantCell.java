package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

import java.awt.*;

public class PlantCell extends FoodCell {
    public PlantCell() {
        super(Cells.PLANT.get(), 100, false);
    }

    @Override
    public String getName() {
        return "Plant";
    }

    @Override
    public String getDescription() {
        return Cells.PLANT.getDescription();
    }
}
