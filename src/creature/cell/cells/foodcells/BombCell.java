package creature.cell.cells.foodcells;

import creature.cell.FoodCell;
import grid.Grid;
import util.Cells;

public class BombCell extends FoodCell {
    public static int EXPLOSION_RADIUS = 4;

    public BombCell() {
        super(Cells.BOMB, 100, 100, true);
    }

    @Override
    public void removeSelfFromGrid(Grid grid) {
        if (!shouldMakeRot())
            grid.queueExplosion(getRow(), getCol(), EXPLOSION_RADIUS);

        super.removeSelfFromGrid(grid);
    }

    @Override
    public String getName() {
        return "Bomb";
    }
}
