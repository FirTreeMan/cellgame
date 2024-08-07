package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.EyeDirection;
import util.MoveDirection;

import java.awt.*;
import java.util.ArrayList;

public class EyeCell extends LivingCell {
    private final EyeDirection originalFacing;
    private EyeDirection facing;

    public EyeCell(Creature owner, int relativeRow, int relativeCol, EyeDirection facing) {
        super(owner, new Color(207, 31, 154), 10, 5, relativeRow, relativeCol);
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
                cellRay(mat, visible, getX() - 1, getY() - 1);
                cellRay(mat, visible, getX(), getY() - 1);
                cellRay(mat, visible, getX() + 1, getY() - 1);
            }
            case DOWN -> {
                cellRay(mat, visible, getX() - 1, getY() + 1);
                cellRay(mat, visible, getX(), getY() + 1);
                cellRay(mat, visible, getX() + 1, getY() + 1);
            }
            case LEFT -> {
                cellRay(mat, visible, getX() - 1, getY() - 1);
                cellRay(mat, visible, getX() - 1, getY());
                cellRay(mat, visible, getX() - 1, getY() + 1);
            }
            case RIGHT -> {
                cellRay(mat, visible, getX() + 1, getY() - 1);
                cellRay(mat, visible, getX() + 1, getY());
                cellRay(mat, visible, getX() + 1, getY() + 1);
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

}
