package creature;

import creature.cell.*;
import creature.cell.cells.foodcells.EggCell;
import creature.cell.cells.foodcells.TrapCell;
import creature.cell.cells.livingcells.BrainCell;
import creature.cell.cells.livingcells.EyeCell;
import creature.cell.cells.livingcells.LegCell;
import creature.cell.cells.livingcells.SpinnerCell;
import grid.Grid;
import util.BodyMutation;
import util.EyeDirection;
import util.MoveDirection;

import java.util.*;

public class Creature {
    public static int EGG_THROW_RANGE = 8;
    public static String[] BODY_PARAMS = new String[]{
            "name:",
            "maxAge:",
            "pubertyAge:",
            "reproductionEnergy:",
            "mutationChance:",
            "mutationSeverity:",
            "moveCooldown:",
            "facing:",
            "alive:",
            "age:",
            "energy:",
            "births:",
    };

    protected final LivingCell[] cells;

    private final BrainCell brain;
    private final EyeCell[] eyes;
    private final Random random;
    private final int maxMoveCooldown;
    private final String name;

    private final int maxAge;
    private final int pubertyAge;
    private final int reproductionEnergy;

    private final float mutationChance;
    private final int mutationSeverity;

    private int moveCooldown;
    private EyeDirection facing;
    private boolean alive;
    private int age;
    private int energy;
    private int births;

    public Creature(LivingCell[] cells, Random random, int maxAge, int pubertyAge, int reproductionEnergy, float mutationChance, int mutationSeverity) {
        this(cells, random, maxAge, pubertyAge, reproductionEnergy, mutationChance, mutationSeverity, true);
    }

    public Creature(LivingCell[] cells, Random random, int maxAge, int pubertyAge, int reproductionEnergy, float mutationChance, int mutationSeverity, boolean doMutate) {
        this.random = random;

        LivingCell[] myCells = new LivingCell[cells.length];
        BrainCell brain = null;

        for (int i = 0; i < cells.length; i++) {
            LivingCell cell = cells[i];

            myCells[i] = CellFactory.copyToOwner(cell, this);

            if (myCells[i] instanceof BrainCell brainCell && brain == null)
                brain = brainCell;
            }
        if (brain == null)
            throw new NoSuchElementException("No Brain found");

        this.brain = brain;

        if (doMutate) {
            this.cells = mutateBody(myCells, mutationChance, mutationSeverity);

            this.maxAge = mutate(maxAge, mutationChance, mutationSeverity);
            this.pubertyAge = mutate(pubertyAge, mutationChance, mutationSeverity, maxAge);
            this.reproductionEnergy = mutate(reproductionEnergy, mutationChance, mutationSeverity * 10);

            this.mutationChance = mutate(mutationChance, mutationChance, mutationSeverity);
            // don't want mutationSeverity to change as much to prevent extreme scaling
            this.mutationSeverity = mutate(mutationSeverity, mutationChance / 2, mutationSeverity / 2 + 1);
        } else {
            this.cells = myCells;

            this.maxAge = maxAge;
            this.pubertyAge = pubertyAge;
            this.reproductionEnergy = reproductionEnergy;

            this.mutationChance = mutationChance;
            this.mutationSeverity = mutationSeverity;
        }

        this.eyes = Arrays.stream(this.cells).filter(s -> s instanceof EyeCell).map(s -> (EyeCell) s).toArray(EyeCell[]::new);

        int legs = (int) Arrays.stream(this.cells).filter(s -> s instanceof LegCell).count();
        this.maxMoveCooldown = legs > 0 ? Math.max(this.cells.length - (legs * LegCell.MAX_CARRY), 0) : -1;
        this.moveCooldown = 0;

        this.name = brain.toSpeciesString();

        this.facing = EyeDirection.UP;

        this.alive = true;
        this.age = 0;

        this.energy = reproductionEnergy - 10;
    }

    public static Creature defaultCreature(LivingCell[] cells, Random random) {
        return new Creature(cells, random, 100, 20, 400, 0.1F, 1, false);
    }

    public Creature copyTo(LivingCell[] cells) {
        return new Creature(cells, random, maxAge, pubertyAge, reproductionEnergy, mutationChance, mutationSeverity, false);
    }

    public Creature makeChild() {
        return new Creature(cells, random, maxAge, pubertyAge, reproductionEnergy, mutationChance, mutationSeverity);
    }

    public LivingCell[] getCells() {
        return cells;
    }

    public Random getRandom() {
        return random;
    }

    public int getCellCount() {
        return cells.length;
    }

    public EyeDirection getFacing() {
        return facing;
    }

    public BrainCell getBrain() {
        return brain;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean canReproduce() {
        return age > pubertyAge && energy > reproductionEnergy;
    }

    public int getEnergyAfterReproduction() {
        return energy - reproductionEnergy;
    }

    public void setSpatial(int row, int col) {
        for (LivingCell cell: cells) {
            cell.setCoords(row + cell.getRelativeX(), col + cell.getRelativeY());
        }
    }

    public void setSpatialAndFacing(EyeDirection facing, int x, int y) {
        this.facing = facing;
        if (facing == EyeDirection.UP) {
            setSpatial(x, y);
            return;
        }

        for (LivingCell cell: cells) {
            cell.setCoords(x + facing.relativeX(cell.getRelativeX(), cell.getRelativeY()), y + facing.relativeY(cell.getRelativeX(), cell.getRelativeY()));
        }
    }

    // call mutateCell() in Cell constructor
    public int mutateCell(int value) {
        return mutate(value, mutationChance, mutationSeverity);
    }

    public float mutateCell(float value) {
        return mutate(value, mutationChance, mutationSeverity);
    }

    public float mutateCell(float value, int scalar) {
        return mutate(value, mutationChance, mutationSeverity, scalar);
    }

    public int mutateCell(int value, int max) {
        return mutate(value, mutationChance, mutationSeverity, max);
    }

    public int mutateCell(int value, int min, int max) {
        return mutate(value, mutationChance, mutationSeverity, min, max);
    }

    // call mutate() in this constructor
    private int mutate(int value, float mutationChance, int mutationSeverity) {
        return mutate(value, mutationChance, mutationSeverity, Integer.MAX_VALUE);
    }

    private float mutate(float value, float mutationChance, int mutationSeverity) {
        return mutate(value, mutationChance, mutationSeverity, 100);
    }

    private float mutate(float value, float mutationChance, int mutationSeverity, int scalar) {
        return mutate((int) (value * scalar) + 1, mutationChance, mutationSeverity, scalar) / (float) scalar;
    }

    private int mutate(int value, float mutationChance, int mutationSeverity, int max) {
        return mutate(value, mutationChance, mutationSeverity, 1, max);
    }

    private int mutate(int value, float mutationChance, int mutationSeverity, int min, int max) {
        if (random.nextDouble() >= mutationChance) return value;

        // variation is a random int in the set [-mutationSeverity, 0) U (0, mutationSeverity]
        int variation = random.nextInt(-mutationSeverity, mutationSeverity);
        if (variation >= 0)
            variation++;

        return Math.clamp(value + variation, min, max);
    }

    private LivingCell[] mutateBody(LivingCell[] oldCells, float mutationChance, int mutationSeverity) {
        if (random.nextDouble() >= mutationChance / 2) return oldCells;

        int bodyMutationIndex = random.nextInt(BodyMutation.values().length);
        BodyMutation bodyMutation = BodyMutation.values()[bodyMutationIndex];

        // continue trying in case of failure depending on mutationSeverity
        for (int n = 0; n < mutationSeverity; n++)
            switch (bodyMutation) {
                case ADD -> {
                    ArrayList<EyeDirection> validDirections = new ArrayList<>(Arrays.asList(EyeDirection.values()));
                    int selectedIndex = random.nextInt(oldCells.length);
                    LivingCell selectedCell = oldCells[selectedIndex];

                    for (LivingCell cell: oldCells) {
                        if (selectedCell.getRelativeY() - 1 == cell.getRelativeY())
                            validDirections.remove(EyeDirection.UP);
                        else if (selectedCell.getRelativeY() + 1 == cell.getRelativeY())
                            validDirections.remove(EyeDirection.DOWN);
                        else if (selectedCell.getRelativeX() - 1 == cell.getRelativeX())
                            validDirections.remove(EyeDirection.LEFT);
                        else if (selectedCell.getRelativeX() + 1 == cell.getRelativeX())
                            validDirections.remove(EyeDirection.RIGHT);

                        if (validDirections.isEmpty())
                            break;
                    }

                    if (!validDirections.isEmpty()) {
                        EyeDirection direction = validDirections.get(random.nextInt(validDirections.size()));
                        int targetX = selectedCell.getRelativeX();
                        int targetY = selectedCell.getRelativeY();
                        switch (direction) {
                            case UP -> targetY--;
                            case DOWN -> targetY++;
                            case LEFT -> targetX--;
                            case RIGHT -> targetX++;
                        }

                        LivingCell[] output = new LivingCell[oldCells.length + 1];
                        System.arraycopy(oldCells, 0, output, 0, oldCells.length);
                        output[oldCells.length] = CellFactory.mutateNewCell(this, targetX, targetY, random);
                        return output;
                    }
                }
                case REPLACE -> {
                    int replacedIndex = random.nextInt(oldCells.length);
                    LivingCell cellToReplace = oldCells[replacedIndex];
                    if (!(cellToReplace instanceof BrainCell)) {
                        oldCells[replacedIndex] = CellFactory.mutateNewCell(this, cellToReplace.getRelativeX(), cellToReplace.getRelativeY(), random);
                        return oldCells;
                    }
                }
                case REMOVE -> {
                    if (random.nextDouble() >= 0.5) {
                        int removeIndex = random.nextInt(oldCells.length);
                        LivingCell cellToRemove = oldCells[removeIndex];

                        if (!(cellToRemove instanceof BrainCell)) {
                            LivingCell[] output = new LivingCell[oldCells.length - 1];
                            for (int i = 0; i < oldCells.length; i++) {
                                if (i == removeIndex) continue;
                                if (i > removeIndex)
                                    output[i - 1] = oldCells[i];
                                else output[i] = oldCells[i];
                            }
                            return output;
                        }
                    }
                }
            }

        return oldCells;
    }

    public void moveTick(Grid grid) {
        if (maxMoveCooldown < 0) {
            if (energy > reproductionEnergy) {
                brain.makeDecision(EyeDirection.getCellHashMap(), facing, canReproduce(), getEnergyAfterReproduction(), false);
                makeMove(grid, brain.getDecision());
            }
            return;
        }

        // can only move when moveCooldown == 0
        if (moveCooldown > 0) {
            moveCooldown -= 1;
            return;
        }
        moveCooldown = maxMoveCooldown;

        Cell[][] mat = grid.getCellMatrix();
        HashMap<EyeDirection, ArrayList<Cell>> visibleCells = EyeDirection.getCellHashMap();
        for (EyeCell eye: eyes) {
            HashMap<EyeDirection, ArrayList<Cell>> eyeVisible = eye.getVisible(mat);
            for (EyeDirection eyeDirection: visibleCells.keySet())
                visibleCells.get(eyeDirection).addAll(eyeVisible.get(eyeDirection));
        }

        brain.makeDecision(visibleCells, facing, canReproduce(), getEnergyAfterReproduction(), true);
        MoveDirection decision = brain.getDecision();
        if (!canMakeMove(grid, decision))
            decision = MoveDirection.NEUTRAL;
        makeMove(grid, decision);
    }

    public boolean canMakeMove(Grid grid, MoveDirection moveDirection) {
        if (moveDirection == MoveDirection.NEUTRAL || moveDirection == MoveDirection.REPRODUCE)
            return true;

        Cell[][] mat = grid.getCellMatrix();
        for (LivingCell cell: cells) {
            int newCellX;
            int newCellY;

            if (moveDirection.isRotation()) {
                EyeDirection newFacing = facing.rotate(moveDirection);

                int newRelX = newFacing.relativeX(cell.getRelativeX(), cell.getRelativeY());
                int newRelY = newFacing.relativeY(cell.getRelativeX(), cell.getRelativeY());

                newCellX = brain.getRow() + newRelX;
                newCellY = brain.getCol() + newRelY;
            } else {
                newCellX = cell.getRow();
                newCellY = cell.getCol();

                switch (moveDirection) {
                    case UP -> newCellY--;
                    case DOWN -> newCellY++;
                    case LEFT -> newCellX--;
                    case RIGHT -> newCellX++;
                }
            }

            if (grid.inBounds(newCellX, newCellY) &&
                    (mat[newCellX][newCellY] == null ||
                    mat[newCellX][newCellY] instanceof FoodCell ||
                    mat[newCellX][newCellY] instanceof LivingCell otherCell && otherCell.getOwner() == this))
                continue;
            return false;
        }

        return true;
    }

    public void makeMove(Grid grid, MoveDirection moveDirection) {
        Cell[][] mat = grid.getCellMatrix();

        if (moveDirection == MoveDirection.NEUTRAL)
            return;
        if (moveDirection == MoveDirection.REPRODUCE) {
            int freeSpaces = 0;
            for (int r = brain.getRow() - EGG_THROW_RANGE; r < brain.getRow() + EGG_THROW_RANGE + 1; r++)
                for (int c = brain.getCol() - EGG_THROW_RANGE; c < brain.getCol() + EGG_THROW_RANGE + 1; c++)
                    if (Grid.isEmptyAtCell(mat, r, c)) {
                        freeSpaces++;
                    }

            int targetSpace = random.nextInt(0, freeSpaces + 1);
            int passedSpaces = 0;
            for (int r = brain.getRow() - EGG_THROW_RANGE; r < brain.getRow() + EGG_THROW_RANGE + 1; r++)
                for (int c = brain.getCol() - EGG_THROW_RANGE; c < brain.getCol() + EGG_THROW_RANGE + 1; c++)
                    if (Grid.isEmptyAtCell(mat, r, c)) {
                        if (targetSpace == passedSpaces) {
                            reproduce(grid, r, c);
                            return;
                        }
                        passedSpaces++;
                    }

            return;
        }
        if (moveDirection.isRotation()) {
            facing = facing.rotate(moveDirection);
        }

        for (LivingCell cell: cells) {
            cell.removeSelfFromGrid(grid);
        }

        for (LivingCell cell: cells) {
            int newCellX, newCellY;

            if (moveDirection.isRotation()) {
                newCellX = brain.getRow() + facing.relativeX(cell.getRelativeX(), cell.getRelativeY());
                newCellY = brain.getCol() + facing.relativeY(cell.getRelativeX(), cell.getRelativeY());
            }
            else {
                newCellX = cell.getRow();
                newCellY = cell.getCol();

                switch (moveDirection) {
                    case UP -> newCellY--;
                    case DOWN -> newCellY++;
                    case LEFT -> newCellX--;
                    case RIGHT -> newCellX++;
                }
            }

            if (mat[newCellX][newCellY] instanceof FoodCell foodCell)
                grid.removeFood(foodCell);
            cell.setCoords(newCellX, newCellY);
            cell.addSelfToGrid(grid);

            cell.onMove(moveDirection);
        }
    }

    public void tick(Grid grid) {
        for (LivingCell cell: cells) {
            energy -= cell.getCost(moveCooldown == maxMoveCooldown && brain.getDecision().isMotion());
            cell.tick(grid);
        }

        age++;
        if (age > maxAge || energy <= 0)
            die();
    }

    public void slowDown(int ticksToSlow) {
        moveCooldown += ticksToSlow;
    }

    public void eat(EdibleCell cell) {
        if (!cell.isAlive()) return;

        energy += cell.getFoodValue();
        cell.kill();
        cell.onEaten(this);
    }

    public void reproduce(Grid grid, int targetRow, int targetCol) {
        Creature child = makeChild();
        child.setSpatial(targetRow, targetCol);
        for (LivingCell cell: child.getCells())
            if (!grid.inBounds(cell.getRow(), cell.getCol()))
                return;

        grid.addFood(new EggCell(child, reproductionEnergy - 5), targetRow, targetCol);
        energy -= reproductionEnergy;
        births++;
    }

    public void useEnergy(int energyToUse) {
        energy -= energyToUse;
    }

    public void hurt(Creature creature) {
        creature.die();
    }

    private void die() {
        alive = false;
    }

    public String[] getParamValues() {
        return new String[]{
                name,
                Cell.format(maxAge),
                Cell.format(pubertyAge),
                Cell.format(reproductionEnergy),
                Cell.format(mutationChance),
                Cell.format(mutationSeverity),
                Cell.format(moveCooldown),
                Cell.format(facing),
                Cell.format(alive),
                Cell.format(age),
                Cell.format(energy),
                Cell.format(births),
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (LivingCell cell: cells)
            if (!(cell instanceof BrainCell))
                builder.append(brain.getCharFromValue(cell.toSpeciesString().charAt(0) * cell.getRelativeX() * 12 | cell.getRelativeY()));

        builder.append('-');
        builder.append(brain.toSpeciesString());

        return builder.toString();
    }
}
