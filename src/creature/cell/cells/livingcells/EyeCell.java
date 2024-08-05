package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.EyeDirection;
import util.MoveDirection;

import java.awt.*;

public class EyeCell extends LivingCell {
    private final EyeDirection originalFacing;
    private EyeDirection facing;

    public EyeCell(Creature owner, int relativeRow, int relativeCol, EyeDirection facing) {
        super(owner, new Color(207, 31, 154), 10, 5, relativeRow, relativeCol);
        this.originalFacing = facing;
        this.facing = facing;
    }

    public EyeDirection getOriginalFacing() {
        return originalFacing;
    }

    public EyeDirection getFacing() {
        return facing;
    }

    @Override
    public void onMove(MoveDirection moveDirection) {
        if (moveDirection.isRotation())
            facing = facing.rotate(moveDirection);
    }

}
