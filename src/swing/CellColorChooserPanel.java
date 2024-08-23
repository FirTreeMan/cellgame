package swing;

import creature.cell.Cell;
import creature.cell.CellFactory;
import util.Cells;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import javax.swing.colorchooser.ColorSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.Arrays;

public class CellColorChooserPanel extends AbstractColorChooserPanel {
    SwatchPanel swatchPanel;
    MouseListener swatchListener;
    private KeyListener swatchKeyListener;

    public CellColorChooserPanel() {
        super();
        setInheritsPopupMenu(true);
    }

    public Cell getSelectedCell() {
        return swatchPanel.getSelectedCell();
    }

    public String getDisplayName() {
        return "Cell Swatch Chooser";
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {
        JPanel superHolder = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        swatchPanel = new SwatchPanel();
        swatchPanel.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY,
                getDisplayName());
        swatchPanel.setInheritsPopupMenu(true);

        swatchKeyListener = new SwatchKeyListener();
        swatchListener = new SwatchListener();
        swatchPanel.addMouseListener(swatchListener);
        swatchPanel.addKeyListener(swatchKeyListener);

        JPanel mainHolder = new JPanel(new BorderLayout());
        Border border = new CompoundBorder(new LineBorder(Color.black),
                new LineBorder(Color.white));
        mainHolder.setBorder(border);
        mainHolder.add(swatchPanel, BorderLayout.CENTER);

        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        superHolder.add(mainHolder, gbc);

        add(superHolder);
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
        swatchPanel.removeMouseListener(swatchListener);
        swatchPanel.removeKeyListener(swatchKeyListener);

        swatchPanel = null;
        swatchListener = null;
        swatchKeyListener = null;

        removeAll();
    }

    public void updateChooser() {}

    void setSelectedColor(Color color) {
        ColorSelectionModel model = getColorSelectionModel();
        if (model != null) {
            model.setSelectedColor(color);
        }
    }

    private class SwatchKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_SPACE == e.getKeyCode()) {
                Color color = swatchPanel.getSelectedColor();
                setSelectedColor(color);
            }
        }
    }

    class SwatchListener extends MouseAdapter implements Serializable {
        public void mousePressed(MouseEvent e) {
            if (isEnabled()) {
                Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
                setSelectedColor(color);
                swatchPanel.setSelectedColorFromLocation(e.getX(), e.getY());
                swatchPanel.requestFocusInWindow();
            }
        }
    }

}

class SwatchPanel extends JPanel {
    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;

    private int selRow;
    private int selCol;

    public SwatchPanel() {
        initValues();
        initColors();
        setToolTipText("");
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        setInheritsPopupMenu(true);

        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int typed = e.getKeyCode();
                switch (typed) {
                    case KeyEvent.VK_UP:
                        if (selRow > 0) {
                            selRow--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (selRow < numSwatches.height - 1) {
                            selRow++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        } else if (selCol < numSwatches.width - 1
                                && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (selCol < numSwatches.width - 1
                                && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        } else if (selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_HOME:
                        selCol = 0;
                        selRow = 0;
                        repaint();
                        break;
                    case KeyEvent.VK_END:
                        selCol = numSwatches.width - 1;
                        selRow = numSwatches.height - 1;
                        repaint();
                        break;
                }
            }
        });
    }

    public Color getSelectedColor() {
        return getColorForCell(selCol, selRow);
    }

    public Cell getSelectedCell() {
        return CellFactory.generateTempCell(Cells.REVERSE.get(getSelectedColor()));
    }

    protected void initValues() {
        swatchSize = new Dimension(30, 30);
        numSwatches = new Dimension(4, 3);
        gap = new Dimension(1, 1);
    }

    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0 , getWidth(), getHeight());
        for (int row = 0; row < numSwatches.height; row++) {
            int y = row * (swatchSize.height + gap.height);
            for (int column = 0; column < numSwatches.width; column++) {
                Color c = getColorForCell(column, row);
                g.setColor(c);
                int x;
                if (!this.getComponentOrientation().isLeftToRight()) {
                    x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
                } else {
                    x = column * (swatchSize.width + gap.width);
                }
                g.fillRect(x, y, swatchSize.width, swatchSize.height);
                g.setColor(Color.BLACK);
                g.drawLine(x + swatchSize.width, y + 1, x + swatchSize.width, y + swatchSize.height);
                g.drawLine(x + 1, y + swatchSize.height, x + swatchSize.width, y + swatchSize.height);

                if (selRow == row && selCol == column) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x + swatchSize.width / 4 + 1, y + swatchSize.height / 4 + 1, swatchSize.width / 2, swatchSize.height / 2);
                    g.setColor(Color.BLACK);
                    g.drawLine(x + swatchSize.width * 3 / 4 + 1, y + swatchSize.height / 4 + 2, x + swatchSize.width * 3 / 4 + 1, y + swatchSize.height * 3 / 4 + 1);
                    g.drawLine(x + swatchSize.width / 4 + 2, y + swatchSize.height * 3 / 4 + 1, x + swatchSize.width * 3 / 4 + 1, y + swatchSize.height * 3 / 4 + 1);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        int x = numSwatches.width * (swatchSize.width + gap.width);
        int y = numSwatches.height * (swatchSize.height + gap.height);
        return new Dimension(x, y);
    }

    protected void initColors() {
        colors = Arrays.stream(Cells.values()).map(Cells::get).toArray(Color[]::new);
    }

    public String getToolTipText(MouseEvent e) {
        Color color = getColorForLocation(e.getX(), e.getY());
        Cells cellEnum = Cells.REVERSE.get(color);
        return Cells.wrapHtml(cellEnum.toString() + "<br>" + cellEnum.getDescription());
    }

    public void setSelectedColorFromLocation(int x, int y) {
        if (!this.getComponentOrientation().isLeftToRight()) {
            selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            selCol = x / (swatchSize.width + gap.width);
        }
        selRow = y / (swatchSize.height + gap.height);
        repaint();
    }

    public Color getColorForLocation(int x, int y) {
        int column;
        if (!this.getComponentOrientation().isLeftToRight()) {
            column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            column = x / (swatchSize.width + gap.width);
        }
        int row = y / (swatchSize.height + gap.height);
        return getColorForCell(column, row);
    }

    private Color getColorForCell(int column, int row) {
        return colors[(row * numSwatches.width) + column];
    }
}