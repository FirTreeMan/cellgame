package swing.cellui;

import creature.cell.Cell;
import grid.Grid;
import util.Cells;

import javax.swing.*;
import java.awt.*;

public class CellUI extends JButton {
    private final int row;
    private final int col;
    private int cellSize;
    private Cell cell;

    public CellUI(Color background, int cellSize, int row, int col) {
        this.cellSize = cellSize;
        this.row = row;
        this.col = col;
        this.cell = null;

        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(background);
        setToolTipText(Grid.EMPTY_TOOLTIP);
        setOpaque(true);
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public void setCell(Cell cell, Cell[][] mat) {
        mat[row][col] = cell;

        this.cell = cell;
        setBackground(cell != null ? cell.getColor() : Grid.EMPTY_COLOR);
        setToolTipText(cell != null ? cell.getName() : Cells.NULL.getDescription());
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Cell getCell() {
        return cell;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cellSize, cellSize);
    }
}
