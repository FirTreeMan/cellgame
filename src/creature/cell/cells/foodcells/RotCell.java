package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import util.Cells;

public class RotCell extends FoodCell {
    public RotCell() {
        super(Cells.ROT, 10, 50, false);
    }

    @Override
    public void rot(boolean shouldMakeRot) {
        super.rot(false);
    }

    @Override
    public String getName() {
        return "Rot";
    }
}
