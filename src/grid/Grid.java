package grid;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.HatchableCell;
import creature.cell.LivingCell;
import creature.cell.cells.foodcells.MeatCell;
import creature.cell.cells.foodcells.PlantCell;
import creature.cell.cells.foodcells.RotCell;
import swing.IterableListModel;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Grid {
    public static Color SELECTED_COLOR = new Color(252, 255, 125);
    public static Color EMPTY_COLOR = new Color(40, 40, 40);
    public static String EMPTY_TOOLTIP = "Nothing at all.";

    private final Cell[][] cellMatrix;
    private final IterableListModel<Creature> creatures;
    private final IterableListModel<FoodCell> food;
    private final Random random;
    private final HashSet<List<Integer>> toExplode;
    private final HashSet<List<Integer>> toUpdate;

    private int plantSpawnAttemptsPerTick;

    public Grid(int size, long seed) {
        this(size);
        random.setSeed(seed);
    }

    public Grid(int size) {
        cellMatrix = new Cell[size][size];
        creatures = new IterableListModel<>();
        food = new IterableListModel<>();
        random = new Random();
        toExplode = new HashSet<>();
        toUpdate = new HashSet<>();

        plantSpawnAttemptsPerTick = 5;
    }

    public static boolean isEmptyAtCell(Cell[][] mat, int row, int col) {
        return row > 0 && row < mat.length &&
                col > 0 && col < mat[0].length &&
                mat[row][col] == null;
    }

    public static int taxicabDistance(Cell start, Cell end) {
        return taxicabDistance(start.getRow(), start.getCol(), end.getRow(), end.getCol());
    }

    public static int taxicabDistance(int startRow, int startCol, int endRow, int endCol) {
        return Math.abs(endRow - startRow + endCol - startCol);
    }

    public void setPlantSpawnAttemptsPerTick(int attemptsPerTick) {
        plantSpawnAttemptsPerTick = attemptsPerTick;
    }

    public Cell[][] getCellMatrix() {
        return cellMatrix;
    }

    public IterableListModel<Creature> getCreatures() {
        return creatures;
    }

    public IterableListModel<FoodCell> getFood() {
        return food;
    }

    public Random getRandom() {
        return random;
    }

    public HashSet<List<Integer>> getToUpdate() {
        return toUpdate;
    }

    public Color getColorAtCell(int row, int col) {
        return cellMatrix[row][col] == null ? Grid.EMPTY_COLOR : cellMatrix[row][col].getColor();
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < cellMatrix.length && col >= 0 && col < cellMatrix[0].length;
    }

    public void addCreature(Creature creature, int row, int col) {
        creature.setSpatial(row, col);
        for (LivingCell cell: creature.getCells()) {
            Cell oldCell = cellMatrix[cell.getRow()][cell.getCol()];
            if (oldCell instanceof LivingCell otherCreatureCell)
                removeCreature(otherCreatureCell.getOwner());
            else if (oldCell instanceof FoodCell foodCell) {
                removeFood(foodCell);
            }
            cell.addSelfToGrid(this);
        }

        creatures.addElement(creature);
    }

    public void removeCreature(Creature creature) {
        for (LivingCell cell: creature.getCells()) {
            cell.removeSelfFromGrid(this);
        }
        creatures.removeElement(creature);
    }

    public void addFood(FoodCell foodCell, int row, int col) {
        addFood(food.size(), foodCell, row, col);
    }

    public void addFood(int index, FoodCell foodCell, int row, int col) {
        foodCell.setCoords(row, col);
        foodCell.addSelfToGrid(this);
        food.add(index, foodCell);
    }

    public void removeFood(FoodCell foodCell) {
        foodCell.removeSelfFromGrid(this);
        food.removeElement(foodCell);
    }

    public void modifyCreature(Creature originalCreature, Creature modifiedCreature) {
        removeCreature(originalCreature);
        addCreature(modifiedCreature, originalCreature.getBrain().getRow(), originalCreature.getBrain().getCol());
    }

    public void queueUpdate(int row, int col) {
        toUpdate.add(List.of(row, col));
    }

    public void creatureToMeat(Creature creature) {
        for (LivingCell cell: creature.getCells()) {
            cell.removeSelfFromGrid(this);

            MeatCell meatCell = new MeatCell(creature.getEnergy() / creature.getCellCount() / 2);
            meatCell.setCoords(cell.getRow(), cell.getCol());
            meatCell.addSelfToGrid(this);
            food.addElement(meatCell);
        }
    }

    public boolean creatureHasSpace(Creature creature, int row, int col) {
        for (LivingCell cell: creature.getCells()) {
            if (cellMatrix[row + cell.getRelativeX()][col + cell.getRelativeY()] instanceof LivingCell)
                return false;
        }
        return true;
    }

    private void explode(int startRow, int startCol, int radius) {
        for (int r = -radius; r <= radius; r++)
            for (int c = -(radius - Math.abs(r)); c <= radius - Math.abs(r); c++) {
                if (cellMatrix[startRow + r][startCol + c] instanceof LivingCell livingCell)
                    removeCreature(livingCell.getOwner());
                else if (cellMatrix[startRow + r][startCol + c] instanceof FoodCell foodCell)
                    removeFood(foodCell);
            }
    }

    public void queueExplosion(int startRow, int startCol, int radius) {
        toExplode.add(List.of(startRow, startCol, radius));
    }

    public void tickExplosions() {
        for (List<Integer> list: toExplode) {
            explode(list.get(0), list.get(1), list.get(2));
        }
        toExplode.clear();
    }

    public void tick() {
        for (int i = 0; i < plantSpawnAttemptsPerTick; i++) {
            int r = random.nextInt(cellMatrix.length);
            int c = random.nextInt(cellMatrix[0].length);
            if (cellMatrix[r][c] == null) {
                addFood(new PlantCell(), r, c);
            }
        }

        creatures.forEach(creature -> creature.moveTick(this));
        creatures.forEach(creature -> creature.tick(this));
        tickExplosions();

        // cleanup/hatching
        for (int i = 0; i < food.size(); i++) {
            FoodCell foodCell = food.get(i);
            foodCell.tick();

            if (!foodCell.isAlive()) {
                if (foodCell.shouldMakeRot()) {
                    removeFood(foodCell);
                    RotCell rotCell = new RotCell();
                    addFood(i, rotCell, foodCell.getRow(), foodCell.getCol());
                } else {
                    foodCell.removeSelfFromGrid(this);
                    food.remove(i);
                    i--;
                }
            }

            if (foodCell instanceof HatchableCell hatchableCell &&
                    hatchableCell.canHatch() &&
                    creatureHasSpace(hatchableCell.getCreature(), foodCell.getRow(), foodCell.getCol())) {
                foodCell.kill();
                addCreature(hatchableCell.getCreature(), foodCell.getRow(), foodCell.getCol());
            }
        }
        for (int i = 0; i < creatures.size(); i++) {
            Creature creature = creatures.get(i);
            if (!creature.isAlive()) {
                creatureToMeat(creature);
                creatures.remove(i);
                i--;
            }
        }
    }
}
