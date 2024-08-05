package creature.cell;

import creature.Creature;
import util.MoveDirection;

import java.awt.*;

public abstract class LivingCell extends Cell {
    private final int tickCost;
    private final int moveCost;

    private final int relativeRow;
    private final int relativeCol;

    public LivingCell(Creature owner, Color color, int tickCost, int moveCost, int relativeRow, int relativeCol) {
        super(owner, color);
        this.tickCost = tickCost;
        this.moveCost = moveCost;
        this.relativeRow = relativeRow;
        this.relativeCol = relativeCol;
    }

    public int getTickCost() {
        return tickCost;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public int getRelativeRow() {
        return relativeRow;
    }

    public int getRelativeCol() {
        return relativeCol;
    }

    public int getCost(boolean isMoving) {
        if (isMoving)
            return tickCost + moveCost;
        return tickCost;
    }

    public void onMove(MoveDirection moveDirection) {}

    public void tick(Cell[] nearby) {}
}
