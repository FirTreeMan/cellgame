package creature.cell;

import creature.Creature;
import creature.cell.cells.foodcells.GlucoseCell;
import creature.cell.cells.foodcells.MeatCell;
import creature.cell.cells.foodcells.PlantCell;
import creature.cell.cells.livingcells.*;
import util.Cells;
import util.EyeDirection;

import java.util.Random;

public class CellFactory {
    public static int CELL_MUTATION_CANDIDATE_COUNT = 6;

    public static Cell generateTempCell(Cells cellEnum) {
        return switch (cellEnum) {
            case BODY -> new BodyCell(null, 0, 0);
            case MOUTH -> new MouthCell(null, 0, 0);
            case LEG -> new LegCell(null, 0, 0);
            case DAMAGER -> new DamagerCell(null, 0, 0);
            case CHLOROPLAST -> new ChloroplastCell(null, 0, 0);
            case EYE -> new EyeCell(null, 0, 0, EyeDirection.UP);
            case BRAIN -> BrainCell.defaultBrain(null);
            case NULL -> null;

            case PLANT -> new PlantCell();
            case MEAT -> new MeatCell();
            case GLUCOSE -> new GlucoseCell();
            case EGG -> null;
        };
    }

    public static LivingCell copyToOwner(LivingCell cell, Creature owner) {
        return copyToOwner(cell, owner, cell.getRelativeX(), cell.getRelativeY());
    }

    public static LivingCell copyToOwner(LivingCell cell, Creature owner, int relativeX, int relativeY) {
        return switch (cell) {
            case BodyCell ignored -> new BodyCell(owner, relativeX, relativeY);
            case MouthCell ignored -> new MouthCell(owner, relativeX, relativeY);
            case LegCell ignored -> new LegCell(owner, relativeX, relativeY);
            case DamagerCell ignored -> new DamagerCell(owner, relativeX, relativeY);
            case ChloroplastCell ignored -> new ChloroplastCell(owner, relativeX, relativeY);
            case EyeCell eyeCell -> new EyeCell(owner, relativeX, relativeY, eyeCell.getOriginalFacing());
            case BrainCell brainCell -> brainCell.copyToOwner(owner);

            default -> new NullCell(owner, relativeX, relativeY);
        };
    }


    public static LivingCell mutateNewCell(Creature owner, int relativeX, int relativeY, Random random) {
        int selectedCellType = random.nextInt(CELL_MUTATION_CANDIDATE_COUNT);

        return switch (selectedCellType) {
            case 0 -> new BodyCell(owner, relativeX, relativeY);
            case 1 -> new MouthCell(owner, relativeX, relativeY);
            case 2 -> new LegCell(owner, relativeX, relativeY);
            case 3 -> new DamagerCell(owner, relativeX, relativeY);
            case 4 -> new ChloroplastCell(owner, relativeX, relativeY);
            case 5 -> new EyeCell(owner, relativeX, relativeY, EyeDirection.getRandom(random));

            default -> new NullCell(owner, relativeX, relativeY);
        };
    }
}
