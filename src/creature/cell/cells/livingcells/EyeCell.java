package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.Cells;
import util.EyeDirection;

import java.util.ArrayList;
import java.util.HashMap;

public class EyeCell extends LivingCell {
    public EyeCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.EYE, 5, 2, relativeRow, relativeCol);
    }

    public HashMap<EyeDirection, ArrayList<Cell>> getVisible(Cell[][] mat) {
        HashMap<EyeDirection, ArrayList<Cell>> visibleCells = EyeDirection.getCellHashMap();

        cellRay(mat, getOwner(), EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow() - 1, getCol() - 1);
        cellRay(mat, getOwner(), EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow(), getCol() - 1);
        cellRay(mat, getOwner(), EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow() + 1, getCol() - 1);

        cellRay(mat, getOwner(), EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow() - 1, getCol() + 1);
        cellRay(mat, getOwner(), EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow(), getCol() + 1);
        cellRay(mat, getOwner(), EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow() + 1, getCol() + 1);

        cellRay(mat, getOwner(), EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol() - 1);
        cellRay(mat, getOwner(), EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol());
        cellRay(mat, getOwner(), EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol() + 1);

        cellRay(mat, getOwner(), EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol() - 1);
        cellRay(mat, getOwner(), EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol());
        cellRay(mat, getOwner(), EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol() + 1);

        return visibleCells;
    }

    public boolean cellRay(Cell[][] mat, Creature owner, EyeDirection facing, ArrayList<Cell> visible, int x, int y) {
        if (x < 0 || x >= mat.length || y < 0 || y >= mat.length) return false;

        switch (facing) {
            case UP -> {
                for (; y >= 0; y--)
                    if (mat[x][y] != null)
                        return addVisibleCell(visible, mat[x][y], owner);
            }
            case DOWN -> {
                for (; y < mat[0].length; y++)
                    if (mat[x][y] != null)
                        return addVisibleCell(visible, mat[x][y], owner);
            }
            case LEFT -> {
                for (; x >= 0; x--)
                    if (mat[x][y] != null)
                        return addVisibleCell(visible, mat[x][y], owner);
            }
            case RIGHT -> {
                for (; x < mat.length; x++)
                    if (mat[x][y] != null)
                        return addVisibleCell(visible, mat[x][y], owner);
            }
        }

        return false;
    }

    public boolean addVisibleCell(ArrayList<Cell> visible, Cell cell, Creature owner) {
        if (cell.getOwner() == owner) return false;
        return visible.add(cell);
    }

    @Override
    public String toSpeciesString() {
        return "E";
    }

    @Override
    public String getName() {
        return "Eye";
    }
}
