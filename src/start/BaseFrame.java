package start;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import creature.cell.cells.livingcells.BrainCell;
import grid.Grid;
import swing.CellColorChooserPanel;
import swing.CreatureViewerPanel;
import swing.cellui.CellUI;
import swing.cellui.CellUIPanel;
import util.CellAttrs;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.function.Consumer;

public class BaseFrame extends JFrame {
    private final Grid grid;
    private CellUIPanel gridUI;
    private CreatureViewerPanel creatureViewerPanel;

    public BaseFrame(String name, Grid grid, Grid creatureView) {
        super(name);
        this.grid = grid;
        this.gridUI = new CellUIPanel(e -> {
            if (e.getSource() instanceof CellUI cellUI) {
                Cell cell = cellUI.getCell();

                if (gridUI.isExamining()) {
                    if (cell instanceof LivingCell livingCell) {
                        creatureViewerPanel.loadCreature(livingCell.getOwner());
                        refreshGridUI();
                    }
                } else {
                    Creature creature = creatureViewerPanel.makeCreature();
                    if (creature == null) return;

                    grid.addCreature(creature, cellUI.getRow(), cellUI.getCol());
                    creatureViewerPanel.loadCreature(creature);
                    refreshGridUI();
                }
            }
        }, grid, 15);
        this.gridUI.setExamining(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mapPanel = getMapPanel();
        add(mapPanel, BorderLayout.CENTER);

        this.creatureViewerPanel = getCreatureViewerPanel(creatureView);
        add(creatureViewerPanel, BorderLayout.EAST);

        JPanel entityListPanel = getEntityListPanel();
        add(entityListPanel, BorderLayout.WEST);

        JPanel controlPanel = getControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel getMapPanel() {
        JPanel mapPanel = new JPanel(new GridBagLayout());
        mapPanel.setBorder(new TitledBorder(new EtchedBorder(), "Map", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));

        JScrollPane gridScrollPane = new JScrollPane(gridUI) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(550, 550);
            }
        };

        JSlider gridScaleSlider = new JSlider(JSlider.HORIZONTAL, 1, 35, 15);
        gridScaleSlider.addChangeListener(e -> gridUI.setCellSize(gridScaleSlider.getValue()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        mapPanel.add(gridScrollPane, gbc);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;
        mapPanel.add(gridScaleSlider, gbc);

        return mapPanel;
    }

    private ActionListener getInsertButtonActionListener(JButton insertButton) {
        return e -> {
            if (creatureViewerPanel.isCreatureValid())
                gridUI.setExamining(!gridUI.isExamining());
            else gridUI.setExamining(true);

            if (gridUI.isExamining()) {
                insertButton.setText("Insert copy into Map");
            } else {
                insertButton.setText("Cancel insertion");
            }
        };
    }

    private JPanel getCreatureViewButtonsPanel(CreatureViewerPanel creatureViewerPanel) {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setForeground(Color.GREEN);
        confirmButton.addActionListener(e -> {
            creatureViewerPanel.confirmChanges();
            refreshGridUI();
        });
        JButton insertButton = new JButton("Insert copy into Map");
        insertButton.setForeground(Color.BLUE);
        insertButton.addActionListener(getInsertButtonActionListener(insertButton));
        JButton clearButton = new JButton("Clear");
        clearButton.setForeground(Color.MAGENTA);
        clearButton.addActionListener(e -> {
            creatureViewerPanel.clear();
            refreshGridUI();
        });
        JButton killButton = new JButton("Kill creature");
        killButton.setForeground(Color.RED);
        killButton.addActionListener(e -> {
            creatureViewerPanel.killCreature();
            refreshGridUI();
        });
        JButton saveButton = new JButton("Save creature");
        saveButton.setForeground(Color.GRAY);
        JCheckBox examiningCheckBox = new JCheckBox("Examining");
        examiningCheckBox.setForeground(Color.GRAY);
        examiningCheckBox.addActionListener(e -> creatureViewerPanel.setExamining(examiningCheckBox.isSelected()));

        buttonPanel.add(confirmButton);
        buttonPanel.add(insertButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(killButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(examiningCheckBox);

        return buttonPanel;
    }

    private JPanel getParamPanel(String title, String[] paramNames, JLabel[] mutableLabels) {
        JPanel paramMasterPanel = new JPanel(new BorderLayout());
        paramMasterPanel.setBorder(new TitledBorder(new EtchedBorder(), title, TitledBorder.RIGHT, TitledBorder.DEFAULT_POSITION));

        JPanel namePanel = new JPanel(new GridLayout(0, 1, 0, 0));
        JPanel valuePanel = new JPanel(new GridLayout(0, 1, 0, 0));
        Font font = new Font("Serif", Font.PLAIN, 9);
        for (int i = 0; i < mutableLabels.length; i++) {
            JLabel nameLabel = new JLabel(paramNames[i]);
            JLabel valueLabel = new JLabel("N/A");

            nameLabel.setLabelFor(valueLabel);
            nameLabel.setFont(font);
            valueLabel.setFont(font);

            mutableLabels[i] = valueLabel;

            namePanel.add(nameLabel);
            valuePanel.add(valueLabel);
        }

        paramMasterPanel.add(namePanel, BorderLayout.WEST);
        paramMasterPanel.add(valuePanel, BorderLayout.EAST);

        return paramMasterPanel;
    }

    private CreatureViewerPanel getCreatureViewerPanel(Grid creatureView) {
        CreatureViewerPanel creatureViewerPanel = new CreatureViewerPanel(new GridBagLayout());
        creatureViewerPanel.setBorder(new TitledBorder(new EtchedBorder(), "Creature View", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        creatureViewerPanel.setMainGrid(grid);

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel creatureViewToolsPanel = new JPanel();
        creatureViewToolsPanel.setLayout(new GridBagLayout());

        CellUIPanel creatureViewerGrid = new CellUIPanel(creatureViewerPanel, creatureView, 35);
        creatureViewerPanel.setCellUIPanel(creatureViewerGrid);

        JPanel creatureCellAndBrainChooser = new JPanel();

        JColorChooser colorChooser = new JColorChooser();
        CellColorChooserPanel swatchChooserPanel = new CellColorChooserPanel();
        colorChooser.setChooserPanels(new AbstractColorChooserPanel[]{swatchChooserPanel});
        colorChooser.setPreviewPanel(new JPanel());
        creatureViewerPanel.setCellColorChooserPanel(swatchChooserPanel);

        JComboBox<BrainCell.BrainTypes> brainTypeChooser = new JComboBox<>(BrainCell.BrainTypes.values());
        creatureViewerPanel.setBrainTypeChooser(brainTypeChooser);

        creatureCellAndBrainChooser.add(colorChooser);
        creatureCellAndBrainChooser.add(brainTypeChooser);

        JPanel buttonPanel = getCreatureViewButtonsPanel(creatureViewerPanel);

        JLabel validationLabel = new JLabel();
        validationLabel.setBorder(new TitledBorder(new EtchedBorder(), "Valid Creature?", TitledBorder.CENTER, TitledBorder.BOTTOM));
        validationLabel.setHorizontalAlignment(JLabel.CENTER);
        creatureViewerPanel.setValidationLabel(validationLabel);

        gbc.gridy = 0;
        creatureViewToolsPanel.add(creatureViewerGrid, gbc);
        gbc.gridy = 1;
        creatureViewToolsPanel.add(creatureCellAndBrainChooser, gbc);
        gbc.gridy = 2;
        creatureViewToolsPanel.add(buttonPanel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 3;
        creatureViewToolsPanel.add(validationLabel, gbc);

        JPanel paramPanel = new JPanel(new GridBagLayout());
        JPanel brainParamPanel = getParamPanel("Brain Params", BrainCell.BRAIN_PARAMS, creatureViewerPanel.currentBrainParams);
        JPanel bodyParamPanel = getParamPanel("Body Params", Creature.BODY_PARAMS, creatureViewerPanel.currentBodyParams);
        JPanel cellParamPanel = getParamPanel("Cell Params", Arrays.stream(CellAttrs.values()).map(CellAttrs::toString).toArray(String[]::new), creatureViewerPanel.currentCellParams);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        paramPanel.add(brainParamPanel, gbc);
        gbc.gridy = 1;
        paramPanel.add(bodyParamPanel, gbc);
        gbc.gridy = 2;
        paramPanel.add(cellParamPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1.0;
        creatureViewerPanel.add(creatureViewToolsPanel, gbc);
        creatureViewerPanel.add(paramPanel, gbc);

        creatureViewerPanel.validate();

        return creatureViewerPanel;
    }

    private <E> JScrollPane getScrollableListPanel(JList<E> list, String title, Consumer<E> consumer) {
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(e -> consumer.accept(list.getSelectedValue()));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(100, (int) scrollPane.getPreferredSize().getHeight()));
        scrollPane.setBorder(new TitledBorder(new EtchedBorder(), title));

        return scrollPane;
    }

    private JPanel getEntityListPanel() {
        JPanel entityListPanel = new JPanel(new GridLayout(0, 1));

        JScrollPane creatureScrollPane = getScrollableListPanel(new JList<>(grid.getCreatures()), "Creatures", s -> {
            if (s == null) return;
            creatureViewerPanel.loadCreature(s);
            refreshGridUI();
        });
        JScrollPane foodScrollPane = getScrollableListPanel(new JList<>(grid.getFood()), "Food", s -> {});

        entityListPanel.add(creatureScrollPane);
        entityListPanel.add(foodScrollPane);

        return entityListPanel;
    }

    private JPanel getControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 0, 10, 10));
        controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));

        ActionListener tickListener = e -> {
            grid.tick();
            creatureViewerPanel.checkCreature();
            refreshGridUI();
        };

        Timer tickTimer = new Timer(1000 / 30, tickListener);

        JButton tickButton = new JButton("Tick");
        tickButton.addActionListener(tickListener);
        JButton runButton = new JButton("Run");
        runButton.addActionListener(e -> {
            if (tickTimer.isRunning()) {
                tickTimer.stop();
                runButton.setText("Run");
            } else {
                tickTimer.start();
                runButton.setText("Pause");
            }
        });

        JPanel tickSpeedPanel = new JPanel();
        JLabel tickSpeedLabel = new JLabel("Ticks per Second:");
        JSpinner tickSpeedSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
        tickSpeedSpinner.addChangeListener(e -> tickTimer.setDelay(1000 / (int) tickSpeedSpinner.getValue()));
        tickSpeedLabel.setLabelFor(tickSpeedSpinner);
        tickSpeedPanel.add(tickSpeedLabel);
        tickSpeedPanel.add(tickSpeedSpinner);

        controlPanel.add(tickButton);
        controlPanel.add(tickSpeedPanel);
        controlPanel.add(runButton);

        return controlPanel;
    }

    private void refreshGridUI() {
        gridUI.refresh(creatureViewerPanel.getCreature());
    }

    public void createAndShowGUI() {
        pack();
        setVisible(true);
    }
}
