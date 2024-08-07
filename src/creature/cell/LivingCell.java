package creature.cell;

import creature.Creature;
import util.MoveDirection;

import java.awt.*;

public abstract class LivingCell extends Cell {
    private final int tickCost;
    private final int moveCost;

    private final int relativeRow;
    private final int relativeCol;

    private int row;
    private int col;
    private boolean alive;

    public LivingCell(Creature owner, Color color, int tickCost, int moveCost, int relativeRow, int relativeCol) {
        super(owner, color);
        this.tickCost = tickCost;
        this.moveCost = moveCost;
        this.relativeRow = relativeRow;
        this.relativeCol = relativeCol;
        this.alive = true;
    }

    public int getTickCost() {
        return tickCost;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public int getRelativeX() {
        return relativeRow;
    }

    public int getRelativeY() {
        return relativeCol;
    }

    public int getX() {
        return row;
    }

    public int getY() {
        return col;
    }

    public int getCost(boolean isMoving) {
        if (isMoving)
            return tickCost + moveCost;
        return tickCost;
    }

    public void setX(int row) {
        this.row = row;
    }

    public void setY(int col) {
        this.col = col;
    }

    public Cell[] getBorderingCells(Cell[][] mat) {
        Cell[] nearby = new Cell[4];
        if (getX() > 0)
            nearby[0] = mat[getX() - 1][getY()];
        if (getX() < mat.length - 1)
            nearby[1] = mat[getX() + 1][getY()];
        if (getY() > 0)
            nearby[2] = mat[getX()][getY() - 1];
        if (getY() < mat[0].length)
            nearby[3] = mat[getX()][getY() + 1];

        return nearby;
    }

    public void onMove(MoveDirection moveDirection) {}

    public void tick(Cell[][] mat) {}
}
