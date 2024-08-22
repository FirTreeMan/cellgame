package creature.cell;

import grid.Grid;
import util.CellAttrs;
import util.Cells;

import java.awt.*;

public abstract class FoodCell extends Cell implements EdibleCell {
    private final int foodValue;
    private final boolean isMeat;
    private boolean alive;

    public FoodCell(Cells cellEnum, int foodValue, boolean isMeat) {
        super(null, cellEnum);
        this.foodValue = foodValue;
        this.isMeat = isMeat;
        this.alive = true;
    }

    @Override
    public String getAttr(CellAttrs attr) {
        if (attr == CellAttrs.NUTRITION) return String.valueOf(foodValue);
        return super.getAttr(attr);
    }

    @Override
    public int getFoodValue() {
        return foodValue;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void kill() {
        alive = false;
    }
}
