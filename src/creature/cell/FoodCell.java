package creature.cell;

import java.awt.*;

public abstract class FoodCell extends Cell implements EdibleCell {
    private final int foodValue;
    private final boolean isMeat;
    private boolean alive;

    public FoodCell(Color color, int foodValue, boolean isMeat) {
        super(null, color);
        this.foodValue = foodValue;
        this.isMeat = isMeat;
    }

    @Override
    public int getFoodValue() {
        return foodValue;
    }

    @Override
    public void kill() {
        alive = false;
    }
}
