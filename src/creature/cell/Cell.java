package creature.cell;

import creature.Creature;
import grid.Grid;
import util.CellAttrs;
import util.Cells;

import java.awt.*;

public abstract class Cell {
    private final Creature owner;
    private final Cells cellEnum;

    private int row;
    private int col;
    private boolean setCoords;

    public Cell(Creature owner, Cells cellEnum) {
        this.owner = owner;
        this.cellEnum = cellEnum;

        this.setCoords = false;
    }

    public static String format(Object obj) {
        return switch (obj) {
            case Float f -> String.format("%.2f", f);
            default -> obj.toString();
        };
    }

    public void addSelfToGrid(Grid grid) {
        Cell[][] mat = grid.getCellMatrix();
        checkCoords(mat);
        mat[row][col] = this;
        grid.queueUpdate(row, col);
    }

    public void removeSelfFromGrid(Grid grid) {
        Cell[][] mat = grid.getCellMatrix();
        checkCoords(mat, this);
        mat[row][col] = null;
        grid.queueUpdate(row, col);
    }

    private void checkCoords(Cell[][] mat, Cell expected) {
        checkCoords(mat);
        if (mat[row][col] != expected)
            throw new NullPointerException(String.format("Unexpected cell at coords (%d,%d): %s instead of %s", row, col, mat[row][col], expected));
    }

    private void checkCoords(Cell[][] mat) {
        if (!setCoords) throw new NullPointerException("Cell coords not set");
    }

    public void setCoords(int row, int col) {
        this.row = row;
        this.col = col;
        this.setCoords = true;
    }

    public Creature getOwner() {
        return owner;
    }

    public Cells getCellEnum() {
        return cellEnum;
    }

    public Color getColor() {
        return cellEnum.get();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getAttr(CellAttrs attr) {
        if (attr == CellAttrs.NAME)
            return getName();
        return "N/A";
    }

    public String getDescription() {
        return cellEnum.getDescription();
    }

    @Override
    public String toString() {
        return getName() + String.format("(%d,%d)", getRow(), getCol());
    }

    public abstract String getName();

    public void tick() {}
}
