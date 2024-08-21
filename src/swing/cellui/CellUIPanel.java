package swing.cellui;

import creature.cell.Cell;
import grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CellUIPanel extends JPanel {
    private final Grid grid;
    private final CellUI[] cellUIs;
    private boolean examining;

    public CellUIPanel(Grid grid, int cellSize) {
        this(null, grid, cellSize);
    }

    public CellUIPanel(ActionListener listener, Grid grid, int cellSize) {
        this.grid = grid;
        this.cellUIs = new CellUI[grid.getCellMatrix().length * grid.getCellMatrix()[0].length];

        Cell[][] matrix = grid.getCellMatrix();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        for (int r = 0; r < matrix.length; r++)
            for (int c = 0; c < matrix[0].length; c++) {
                CellUI cellUI = new CellUI(grid.getColorAtCell(r, c), cellSize, r, c);
                if (listener != null)
                    cellUI.addActionListener(listener);
                this.cellUIs[r * matrix.length + c] = cellUI;

                gbc.gridx = r;
                gbc.gridy = c;
                gbc.insets.set(1, 1, 1, 1);
                add(cellUI, gbc);
            }
    }

    public void setExamining(boolean examining) {
        this.examining = examining;
    }

    public Grid getGrid() {
        return grid;
    }

    public CellUI[] getCellUIs() {
        return cellUIs;
    }

    public CellUI getCellAt(int row, int col) {
        return cellUIs[row * grid.getCellMatrix().length + col];
    }

    public boolean isExamining() {
        return examining;
    }

    public void refresh() {
        Cell[][] cellMatrix = grid.getCellMatrix();
        for (List<Integer> coord: grid.getToUpdate())
            cellUIs[coord.get(0) * cellMatrix.length + coord.get(1)].setCell(cellMatrix[coord.get(0)][coord.get(1)], cellMatrix);
        grid.getToUpdate().clear();
    }
}
