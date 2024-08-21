package start;

import grid.Grid;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");}
        catch (Exception ignored) {}

        Grid grid = new Grid(75);
        Grid creatureView = new Grid(9);
        BaseFrame baseFrame = new BaseFrame("cell game", grid, creatureView);
        javax.swing.SwingUtilities.invokeLater(baseFrame::createAndShowGUI);
    }
}
