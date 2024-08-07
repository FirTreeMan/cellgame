package grid;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.cells.foodcells.PlantCell;

import java.util.ArrayList;
import java.util.Random;

public class Grid {
    private final Cell[][] cellMatrix;
    private final ArrayList<Creature> creatures;
    private final Random random;

    private int plantSpawnAttemptsPerTick;

    public Grid(int size, long seed) {
        this(size);
        random.setSeed(seed);
    }

    public Grid(int size) {
        cellMatrix = new Cell[size][size];
        creatures = new ArrayList<>();
        random = new Random();
        plantSpawnAttemptsPerTick = 20;
    }

    public Cell[][] getCellMatrix() {
        return cellMatrix;
    }

    public Random getRandom() {
        return random;
    }

    public void setPlantSpawnAttemptsPerTick(int attemptsPerTick) {
        plantSpawnAttemptsPerTick = attemptsPerTick;
    }

    public static boolean isEmptyAtCell(Cell[][] mat, int row, int col) {
        return row > 0 && row < mat.length &&
                col > 0 && col < mat[0].length &&
                mat[row][col] == null;
    }

    public void addCreature(Creature creature, int x, int y) {
        creature.setSpatial(x, y);
        creatures.add(creature);
    }

    public void tick() {
        for (int i = 0; i < plantSpawnAttemptsPerTick; i++) {
            int r = random.nextInt(cellMatrix.length);
            int c = random.nextInt(cellMatrix[0].length);
            if (cellMatrix[r][c] == null) {
                cellMatrix[r][c] = new PlantCell();
            }
        }
        for (Creature creature: creatures)
            creature.moveTick(cellMatrix);
        for (Creature creature: creatures)
            creature.tick(cellMatrix);
    }
}
