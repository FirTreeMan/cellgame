package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.CellAttrs;
import util.Cells;
import util.EyeDirection;
import util.MoveDirection;

import java.util.ArrayList;

public class EyeCell extends LivingCell {
    private final EyeDirection originalFacing;
    private EyeDirection facing;

    public EyeCell(Creature owner, int relativeRow, int relativeCol, EyeDirection facing) {
        super(owner, Cells.EYE.get(), 10, 5, relativeRow, relativeCol);
        this.originalFacing = facing;
        this.facing = facing;
    }

    public EyeDirection getOriginalFacing() {
        return originalFacing;
    }

    public EyeDirection getFacing() {
        return facing;
    }

    public ArrayList<Cell> getVisible(Cell[][] mat) {
        ArrayList<Cell> visible = new ArrayList<>();

        switch (facing) {
            case UP -> {
                cellRay(mat, visible, getRow() - 1, getCol() - 1);
                cellRay(mat, visible, getRow(), getCol() - 1);
                cellRay(mat, visible, getRow() + 1, getCol() - 1);
            }
            case DOWN -> {
                cellRay(mat, visible, getRow() - 1, getCol() + 1);
                cellRay(mat, visible, getRow(), getCol() + 1);
                cellRay(mat, visible, getRow() + 1, getCol() + 1);
            }
            case LEFT -> {
                cellRay(mat, visible, getRow() - 1, getCol() - 1);
                cellRay(mat, visible, getRow() - 1, getCol());
                cellRay(mat, visible, getRow() - 1, getCol() + 1);
            }
            case RIGHT -> {
                cellRay(mat, visible, getRow() + 1, getCol() - 1);
                cellRay(mat, visible, getRow() + 1, getCol());
                cellRay(mat, visible, getRow() + 1, getCol() + 1);
            }
        }

        return visible;
    }

    public boolean cellRay(Cell[][] mat, ArrayList<Cell> visible, int x, int y) {
        if (x < 0 || x >= mat.length || y < 0 || y >= mat.length) return false;

        switch (facing) {
            case UP -> {
                for (; y >= 0; y--)
                    if (mat[x][y] != null)
                        return visible.add(mat[x][y]);
            }
            case DOWN -> {
                for (; y < mat[0].length; y++)
                    if (mat[x][y] != null)
                        return visible.add(mat[x][y]);
            }
            case LEFT -> {
                for (; x >= 0; x--)
                    if (mat[x][y] != null)
                        return visible.add(mat[x][y]);
            }
            case RIGHT -> {
                for (; x < mat.length; x++)
                    if (mat[x][y] != null)
                        return visible.add(mat[x][y]);
            }
        }

        return false;
    }

    @Override
    public void onMove(MoveDirection moveDirection) {
        if (moveDirection.isRotation())
            facing = facing.rotate(moveDirection);
    }

    @Override
    public String toSpeciesString() {
        return "E";
    }

    @Override
    public String getAttr(CellAttrs attr) {
        if (attr == CellAttrs.FACING) return String.valueOf(facing);
        return super.getAttr(attr);
    }

    @Override
    public String getName() {
        return "Eye";
    }

    @Override
    public String getDescription() {
        return Cells.EYE.getDescription();
    }
}
