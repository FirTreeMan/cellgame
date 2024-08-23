package creature.cell;

import creature.Creature;
import grid.Grid;
import util.CellAttrs;
import util.Cells;

public abstract class FoodCell extends Cell implements EdibleCell {
    private final int foodValue;
    private final int rotTime;
    private final boolean isMeat;

    private int timeLived;
    private boolean alive;
    private boolean shouldMakeRot;

    public FoodCell(Cells cellEnum, int foodValue, int rotTime, boolean isMeat) {
        super(null, cellEnum);
        this.foodValue = foodValue;
        this.rotTime = rotTime;
        this.isMeat = isMeat;

        this.timeLived = 0;
        this.alive = true;
        this.shouldMakeRot = false;
    }

    public boolean shouldMakeRot() {
        return shouldMakeRot;
    }

    public void rot(boolean shouldMakeRot) {
        this.shouldMakeRot = shouldMakeRot;
        alive = false;
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
        rot(false);
    }

    @Override
    public void onEaten(Creature creature) {

    }

    @Override
    public void tick() {
        if (rotTime < 0) return;

        if (timeLived >= rotTime)
            rot(true);
        timeLived++;
    }
}
