package swing.cellui;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.cells.livingcells.BrainCell;
import grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CellUIPanel extends JPanel {
    public static int CELL_SIZE_TO_CHANGE_INSETS = 10;

    private final Grid grid;
    private final CellUI[] cellUIs;
    private CellUI selected;
    private boolean examining;

    public CellUIPanel(ActionListener listener, Grid grid, int cellSize) {
        this.grid = grid;
        this.cellUIs = new CellUI[grid.getCellMatrix().length * grid.getCellMatrix()[0].length];

        Cell[][] matrix = grid.getCellMatrix();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(1, 1, 1, 1);

        for (int r = 0; r < matrix.length; r++)
            for (int c = 0; c < matrix[0].length; c++) {
                CellUI cellUI = new CellUI(grid.getColorAtCell(r, c), cellSize, r, c);
                if (listener != null)
                    cellUI.addActionListener(listener);
                this.cellUIs[r * matrix.length + c] = cellUI;

                gbc.gridx = r;
                gbc.gridy = c;
                add(cellUI, gbc);
            }
    }

    public void setCellSize(int cellSize) {
        GridBagConstraints gbc = new GridBagConstraints();
        for (CellUI cellUI: cellUIs) {
            if (cellSize > CELL_SIZE_TO_CHANGE_INSETS ^ cellUI.getCellSize() > CELL_SIZE_TO_CHANGE_INSETS) {
                gbc.gridx = cellUI.getRow();
                gbc.gridy = cellUI.getCol();

                gbc.insets = cellSize > CELL_SIZE_TO_CHANGE_INSETS ? new Insets(1, 1, 1, 1) : new Insets(0, 0, 0, 0);
                ((GridBagLayout) getLayout()).setConstraints(cellUI, gbc);
            }
            cellUI.setCellSize(cellSize);
        }
        revalidate();
        repaint();
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

    public void refreshCell(CellUI cellUI) {
        Cell[][] cellMatrix = grid.getCellMatrix();
        cellUI.setCell(cellMatrix[cellUI.getRow()][cellUI.getCol()], cellMatrix);
    }

    public void refresh(Creature selectedCreature) {
        Cell[][] cellMatrix = grid.getCellMatrix();

        for (List<Integer> coord: grid.getToUpdate())
            refreshCell(cellUIs[coord.get(0) * cellMatrix.length + coord.get(1)]);
        grid.getToUpdate().clear();

        if (selected != null) {
            refreshCell(selected);
            selected = null;
        }
        if (selectedCreature != null)
            setSelected(selectedCreature);
    }

    public void setSelected(Creature creature) {
        Cell[][] cellMatrix = grid.getCellMatrix();
        BrainCell brain = creature.getBrain();
        selected = cellUIs[brain.getRow() * cellMatrix.length + brain.getCol()];
        selected.setBackground(Grid.SELECTED_COLOR);
    }
}
