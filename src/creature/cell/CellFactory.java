package creature.cell;

import creature.Creature;
import creature.cell.cells.foodcells.*;
import creature.cell.cells.livingcells.*;
import util.Cells;

import java.util.Random;

public class CellFactory {
    public static int CELL_MUTATION_CANDIDATE_COUNT = 10;

    public static Cell generateTempCell(Cells cellEnum) {
        return switch (cellEnum) {
            case BODY -> new BodyCell(null, 0, 0);
            case MOUTH -> new MouthCell(null, 0, 0);
            case LEG -> new LegCell(null, 0, 0);
            case DAMAGER -> new DamagerCell(null, 0, 0);
            case CHLOROPLAST -> new ChloroplastCell(null, 0, 0);
            case EYE -> new EyeCell(null, 0, 0);
            case CHITIN -> new ChitinCell(null, 0, 0);
            case SHARER -> new SharerCell(null, 0, 0);
            case SPINNER -> new SpinnerCell(null, 0, 0);
            case BOMBER -> new BomberCell(null, 0, 0);
            case BRAIN -> BrainCell.defaultBrain(null, BrainCell.BrainTypes.IMMOBILE);
            case NULL -> null;

            case PLANT -> new PlantCell();
            case MEAT -> new MeatCell();
            case GLUCOSE -> new GlucoseCell();
            case EGG -> null;
            case ROT -> new RotCell();
            case OFFERING -> new OfferingCell();
            case TRAP -> new TrapCell();
            case BOMB -> new BombCell();
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
            case EyeCell ignored -> new EyeCell(owner, relativeX, relativeY);
            case ChitinCell ignored -> new ChitinCell(owner, relativeX, relativeY);
            case SharerCell ignored -> new SharerCell(owner, relativeX, relativeY);
            case SpinnerCell ignored -> new SpinnerCell(owner, relativeX, relativeY);
            case BomberCell ignored -> new BomberCell(owner, relativeX, relativeY);
            case BrainCell brainCell -> brainCell.copyToChild(owner);

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
            case 5 -> new EyeCell(owner, relativeX, relativeY);
            case 6 -> new ChitinCell(owner, relativeX, relativeY);
            case 7 -> new SharerCell(owner, relativeX, relativeY);
            case 8 -> new SpinnerCell(owner, relativeX, relativeY);
            case 9 -> new BomberCell(owner, relativeX, relativeY);

            default -> new NullCell(owner, relativeX, relativeY);
        };
    }
}
