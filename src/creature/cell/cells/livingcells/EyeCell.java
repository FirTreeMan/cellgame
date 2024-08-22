package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.CellAttrs;
import util.Cells;
import util.EyeDirection;
import util.MoveDirection;

import java.util.ArrayList;
import java.util.HashMap;

public class EyeCell extends LivingCell {
    public EyeCell(Creature owner, int relativeRow, int relativeCol) {
        super(owner, Cells.EYE, 5, 5, relativeRow, relativeCol);
    }

    public HashMap<EyeDirection, ArrayList<Cell>> getVisible(Cell[][] mat) {
        HashMap<EyeDirection, ArrayList<Cell>> visibleCells = EyeDirection.getCellHashMap();

        cellRay(mat, EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow() - 1, getCol() - 1);
        cellRay(mat, EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow(), getCol() - 1);
        cellRay(mat, EyeDirection.UP, visibleCells.get(EyeDirection.UP), getRow() + 1, getCol() - 1);

        cellRay(mat, EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow() - 1, getCol() + 1);
        cellRay(mat, EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow(), getCol() + 1);
        cellRay(mat, EyeDirection.DOWN, visibleCells.get(EyeDirection.DOWN), getRow() + 1, getCol() + 1);

        cellRay(mat, EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol() - 1);
        cellRay(mat, EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol());
        cellRay(mat, EyeDirection.LEFT, visibleCells.get(EyeDirection.LEFT), getRow() - 1, getCol() + 1);

        cellRay(mat, EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol() - 1);
        cellRay(mat, EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol());
        cellRay(mat, EyeDirection.RIGHT, visibleCells.get(EyeDirection.RIGHT), getRow() + 1, getCol() + 1);

        return visibleCells;
    }

    public boolean cellRay(Cell[][] mat, EyeDirection facing, ArrayList<Cell> visible, int x, int y) {
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
    public String toSpeciesString() {
        return "E";
    }

    @Override
    public String getName() {
        return "Eye";
    }
}
