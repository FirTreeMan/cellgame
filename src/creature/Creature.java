package creature;

import creature.cell.Cell;
import creature.cell.FoodCell;
import creature.cell.LivingCell;
import creature.cell.LivingCellFactory;
import creature.cell.cells.livingcells.BrainCell;
import creature.cell.cells.livingcells.LegCell;
import util.EyeDirection;
import util.MoveDirection;

import java.util.*;

public class Creature {
    public static int EGG_THROW_RANGE = 6;

    private final LivingCell[] cells;
    private final BrainCell brain;
    private final int moveCooldown;

    private final int maxAge;
    private final int pubertyTime;
    private final int reproductionEnergy;

    private final float mutationChance;
    private final int mutationSeverity;

    private EyeDirection facing;
    private boolean alive;
    private int age;
    private int energy;
    private int births;

    public Creature(LivingCell[] cells, int maxAge, int pubertyTime, int reproductionEnergy, float mutationChance, int mutationSeverity) {
        this.cells = new LivingCell[cells.length];

        BrainCell brain = null;
        int legs = 0;

        for (int i = 0; i < cells.length; i++) {
            LivingCell cell = cells[i];

            this.cells[i] = LivingCellFactory.copyToOwner(cell, this);

            if (cell instanceof BrainCell brainCell && brain == null)
                brain = brainCell;
            else if (cell instanceof LegCell)
                legs++;
            }
        if (brain == null)
            throw new NoSuchElementException("No Brain found");

        this.brain = brain;
        this.moveCooldown = Math.max(cells.length - (legs * LegCell.MAX_CARRY), 0);

        this.facing = EyeDirection.UP;

        this.alive = true;
        this.age = 0;
        this.maxAge = mutate(maxAge, mutationChance, mutationSeverity);
        this.pubertyTime = mutate(pubertyTime, mutationChance, mutationSeverity, maxAge);

        this.energy = reproductionEnergy;
        this.reproductionEnergy = mutate(reproductionEnergy, mutationChance, mutationSeverity * 10);

        this.mutationChance = mutate(mutationChance, mutationChance, mutationSeverity);
        this.mutationSeverity = mutate(mutationSeverity, mutationChance / 2, mutationSeverity);
    }

    public LivingCell[] getCells() {
        return cells;
    }

    public EyeDirection getFacing() {
        return facing;
    }

    public boolean isAlive() {
        return alive;
    }

    // call mutateCell in Cell constructor
    public int mutateCell(int value) {
        return mutate(value, mutationChance, mutationSeverity);
    }

    public int mutateCell(int value, int max) {
        return mutate(value, mutationChance, mutationSeverity, max);
    }

    public int mutateCell(int value, int min, int max) {
        return mutate(value, mutationChance, mutationSeverity, min, max);
    }

    public float mutateCell(float value) {
        return mutate(value, mutationChance, mutationSeverity);
    }

    // call mutate in this constructor
    private int mutate(int value, float mutationChance, int mutationSeverity) {
        return mutate(value, mutationChance, mutationSeverity, Integer.MAX_VALUE);
    }

    private int mutate(int value, float mutationChance, int mutationSeverity, int max) {
        return mutate(value, mutationChance, mutationSeverity, 1, max);
    }

    private int mutate(int value, float mutationChance, int mutationSeverity, int min, int max) {
        Random random = new Random();
        if (random.nextDouble() >= mutationChance) return value;

        int variation = random.nextInt(-mutationSeverity, mutationSeverity + 1);

        return Math.clamp(value + variation, min, max);
    }

    private float mutate(float value, float mutationChance, int mutationSeverity) {
        return mutate((int) (value * 100) + 1, mutationChance, mutationSeverity) / 100.0F;
    }

    public MoveDirection move(HashMap<EyeDirection, Cell[]> visibleCells) {
        brain.makeDecision(visibleCells, facing);

        return brain.getDecision();
    }

    public void moveSuccess(MoveDirection moveDirection) {
        for (LivingCell cell: cells)
            cell.onMove(moveDirection);
    }

    public void tick(Cell[] nearby) {
        if (age > maxAge) {
            die();
            return;
        }
        age++;

        for (LivingCell cell: cells) {
            cell.tick(nearby);
        }
    }

    public void eat(FoodCell cell) {
        energy += cell.getFoodValue();
        cell.kill();
    }

    public void hurt(Creature creature) {
        creature.die();
    }

    private void die() {
        alive = false;
    }
}
