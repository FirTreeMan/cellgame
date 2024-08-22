package creature.cell;

import creature.Creature;
import util.Cells;
import util.MoveDirection;

import java.awt.*;

public abstract class LivingCell extends Cell {
    private final int tickCost;
    private final int moveCost;

    private final int relativeRow;
    private final int relativeCol;

    private boolean alive;

    public LivingCell(Creature owner, Cells cellEnum, int tickCost, int moveCost, int relativeRow, int relativeCol) {
        super(owner, cellEnum);
        this.tickCost = tickCost;
        this.moveCost = moveCost;
        this.relativeRow = relativeRow;
        this.relativeCol = relativeCol;
        this.alive = true;
    }

    public int getRelativeX() {
        return relativeRow;
    }

    public int getRelativeY() {
        return relativeCol;
    }

    public int getCost(boolean isMoving) {
        if (isMoving)
            return tickCost + moveCost;
        return tickCost;
    }

    public Cell[] getBorderingCells(Cell[][] mat) {
        Cell[] nearby = new Cell[4];
        if (getRow() > 0)
            nearby[0] = mat[getRow() - 1][getCol()];
        if (getRow() < mat.length - 1)
            nearby[1] = mat[getRow() + 1][getCol()];
        if (getCol() > 0)
            nearby[2] = mat[getRow()][getCol() - 1];
        if (getCol() < mat[0].length - 1)
            nearby[3] = mat[getRow()][getCol() + 1];

        return nearby;
    }

    public void onMove(MoveDirection moveDirection) {}

    public void tick(Cell[][] mat) {}

    public abstract String toSpeciesString();
}
