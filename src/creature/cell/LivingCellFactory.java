package creature.cell;

import creature.Creature;
import creature.cell.cells.livingcells.*;
import util.EyeDirection;

import java.util.Random;

public class LivingCellFactory {
    public static int CELL_MUTATION_CANDIDATE_COUNT = 6;

    public static LivingCell copyToOwner(LivingCell cell, Creature owner) {
        return switch (cell) {
            case BodyCell ignored -> new BodyCell(owner, cell.getRelativeX(), cell.getRelativeY());
            case MouthCell ignored -> new MouthCell(owner, cell.getRelativeX(), cell.getRelativeY());
            case LegCell ignored -> new LegCell(owner, cell.getRelativeX(), cell.getRelativeY());
            case DamagerCell ignored -> new DamagerCell(owner, cell.getRelativeX(), cell.getRelativeY());
            case ChloroplastCell ignored -> new ChloroplastCell(owner, cell.getRelativeX(), cell.getRelativeY());
            case EyeCell eyeCell -> new EyeCell(owner, cell.getRelativeX(), cell.getRelativeY(), eyeCell.getOriginalFacing());

            default -> new NullCell(owner, cell.getRelativeX(), cell.getRelativeY());
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
