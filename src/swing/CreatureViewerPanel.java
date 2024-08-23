package swing;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.CellFactory;
import creature.cell.LivingCell;
import creature.cell.cells.livingcells.BrainCell;
import grid.Grid;
import swing.cellui.CellUI;
import swing.cellui.CellUIPanel;
import util.CellAttrs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class CreatureViewerPanel extends JPanel implements ActionListener {
    public static String VALID = "VALID";
    public static String INVALID = "INVALID";

    public final JLabel[] currentBrainParams;
    public final JLabel[] currentBodyParams;
    public final JLabel[] currentCellParams;

    private Grid mainGrid;
    private CellUIPanel cellUIPanel;
    private CellColorChooserPanel cellColorChooserPanel;
    private JLabel validationLabel;

    private Creature creature;
    private boolean creatureValid;

    public CreatureViewerPanel(LayoutManager layout) {
        super(layout);
        this.currentBrainParams = new JLabel[BrainCell.BRAIN_PARAMS.length];
        this.currentBodyParams = new JLabel[Creature.BODY_PARAMS.length];
        this.currentCellParams = new JLabel[CellAttrs.values().length];
    }

    public void setMainGrid(Grid mainGrid) {
        this.mainGrid = mainGrid;
    }

    public void setCellUIPanel(CellUIPanel cellUIPanel) {
        this.cellUIPanel = cellUIPanel;
    }

    public void setCellColorChooserPanel(CellColorChooserPanel cellColorChooserPanel) {
        this.cellColorChooserPanel = cellColorChooserPanel;
    }

    public void setValidationLabel(JLabel validationLabel) {
        this.validationLabel = validationLabel;
    }

    public void setExamining(boolean examining) {
        cellUIPanel.setExamining(examining);
    }

    public Creature getCreature() {
        return creature;
    }

    public boolean isCreatureValid() {
        return creatureValid;
    }

    private boolean creatureIsValid() {
        boolean hasBrain = false;
        int brainRow = 0;
        int brainCol = 0;

        Cell[][] mat = cellUIPanel.getGrid().getCellMatrix();
        for (int r = 0; r < mat.length; r++)
            for (int c = 0; c < mat[0].length; c++) {
                if (mat[r][c] == null) continue;

                if (mat[r][c] instanceof BrainCell) {
                    if (hasBrain)
                        return false;
                    hasBrain = true;
                    brainRow = r;
                    brainCol = c;
                }
            }

        if (!hasBrain) return false;

        boolean[][] trackerMat = new boolean[mat.length][mat[0].length];
        validateCell(mat, trackerMat, brainRow, brainCol);

        for (int r = 0; r < mat.length; r++)
            for (int c = 0; c < mat[0].length; c++)
                if (mat[r][c] != null && !trackerMat[r][c])
                    return false;
        return true;
    }

    private void validateCell(Cell[][] mat, boolean[][] trackerMat, int row, int col) {
        trackerMat[row][col] = true;

        if (row > 0 && mat[row-1][col] != null && !trackerMat[row-1][col])
            validateCell(mat, trackerMat, row - 1, col);
        if (row < trackerMat.length - 1 && mat[row+1][col] != null && !trackerMat[row+1][col])
            validateCell(mat, trackerMat, row + 1, col);
        if (col > 0 && mat[row][col-1] != null && !trackerMat[row][col-1])
            validateCell(mat, trackerMat, row, col - 1);
        if (col < trackerMat[0].length - 1 && mat[row][col+1] != null && !trackerMat[row][col+1])
            validateCell(mat, trackerMat, row, col + 1);
    }

    public void validate() {
        creatureValid = creatureIsValid();

        validationLabel.setText(creatureValid ? VALID : INVALID);
        validationLabel.setForeground(creatureValid ? Color.GREEN : Color.RED);
    }

    public void confirmChanges() {
        if (creature == null) return;

        Creature oldCreature = creature;
        creature = makeCreature();
        mainGrid.modifyCreature(oldCreature, creature);
    }

    public void detachCreature() {
        creature = null;
        updateBodyParams();
    }

    public void checkCreature() {
        if (creature == null || !creature.isAlive())
            detachCreature();
        else updateBodyParams();
    }

    public void killCreature() {
        mainGrid.removeCreature(creature);
        detachCreature();
    }

    public void clear() {
        detachCreature();

        for (CellUI cellUI: cellUIPanel.getCellUIs())
            cellUI.setCell(null, cellUIPanel.getGrid().getCellMatrix());

        for (JLabel label: currentBrainParams)
            label.setText("N/A");
        for (JLabel label: currentBodyParams)
            label.setText("N/A");
        for (JLabel label: currentCellParams)
            label.setText("N/A");

        validate();
    }

    public void loadCreature(Creature creature) {
        clear();

        this.creature = creature;
        updateBodyParams();

        Cell[][] cellMatrix = cellUIPanel.getGrid().getCellMatrix();

        int centerX = cellMatrix.length / 2;
        int centerY = cellMatrix[0].length / 2;

        for (LivingCell cell: creature.getCells()) {
            CellUI cellUI = cellUIPanel.getCellAt(centerX + cell.getRelativeX(), centerY + cell.getRelativeY());
            cellUI.setCell(cell, cellMatrix);
            if (cell instanceof BrainCell brainCell)
                updateBrainParams(brainCell);
        }

        validate();
    }

    public Creature makeCreature() {
        if (!creatureValid) return null;

        ArrayList<LivingCell> cells = new ArrayList<>();
        Random random = cellUIPanel.getGrid().getRandom();

        int brainRow = 0;
        int brainCol = 0;
        for (CellUI cellUI: cellUIPanel.getCellUIs())
            if (cellUI.getCell() instanceof BrainCell brainCell) {
                brainRow = cellUI.getRow();
                brainCol = cellUI.getCol();
                cells.add(brainCell);
                break;
            }

        for (CellUI cellUI: cellUIPanel.getCellUIs()) {
            if (cellUI.getCell() == null) continue;

            if (cellUI.getCell() instanceof LivingCell livingCell && !(livingCell instanceof BrainCell)) {
                LivingCell newCell = CellFactory.copyToOwner(livingCell, null, cellUI.getRow() - brainRow, cellUI.getCol() - brainCol);
                cells.add(newCell);
            }
        }

        LivingCell[] cellArr = cells.toArray(LivingCell[]::new);
        return creature != null ? creature.copyTo(cellArr) : Creature.defaultCreature(cellArr, random);
    }

    private void updateBrainParams(BrainCell brainCell) {
        String[] params = brainCell.getParamValues();
        for (int i = 0; i < BrainCell.BRAIN_PARAMS.length; i++) {
            currentBrainParams[i].setText(params[i]);
        }
    }

    private void updateCellParams(CellUI cellUI) {
        for (int i = 0; i < CellAttrs.values().length; i++) {
            Cell cell = cellUI.getCell();
            if (cell == null) {
                currentCellParams[i].setText("N/A");
                continue;
            }

            currentCellParams[i].setText(cell.getAttr(CellAttrs.values()[i]));
        }
    }

    public void updateBodyParams() {
        if (creature == null) {
            for (int i = 0; i < Creature.BODY_PARAMS.length; i++)
                currentBodyParams[i].setText("N/A");
            return;
        }

        String[] params = creature.getParamValues();
        for (int i = 0; i < Creature.BODY_PARAMS.length; i++)
            currentBodyParams[i].setText(params[i]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CellUI cellUI) {
            if (cellUIPanel.isExamining()) {

            } else {
                Cell cell = cellColorChooserPanel.getSelectedCell();
                cellUI.setCell(cell, cellUIPanel.getGrid().getCellMatrix());

                validate();
            }

            if (cellUI.getCell() instanceof BrainCell brainCell)
                updateBrainParams(brainCell);

            updateCellParams(cellUI);
        }
    }
}
