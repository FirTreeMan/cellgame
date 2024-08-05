package creature.cell;

import creature.Creature;
import creature.cell.cells.livingcells.*;

public class LivingCellFactory {
    public static LivingCell copyToOwner(LivingCell cell, Creature owner) {
        return switch (cell) {
            case BodyCell ignored -> new BodyCell(owner, cell.getRelativeRow(), cell.getRelativeCol());
            case MouthCell ignored -> new MouthCell(owner, cell.getRelativeRow(), cell.getRelativeCol());
            case LegCell ignored -> new LegCell(owner, cell.getRelativeRow(), cell.getRelativeCol());
            case DamagerCell ignored -> new DamagerCell(owner, cell.getRelativeRow(), cell.getRelativeCol());
            case EyeCell eyeCell -> new EyeCell(owner, cell.getRelativeRow(), cell.getRelativeCol(), eyeCell.getOriginalFacing());

            default -> new NullCell(owner, cell.getRelativeRow(), cell.getRelativeCol());
        };
    }
}
