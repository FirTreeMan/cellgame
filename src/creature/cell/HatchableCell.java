package creature.cell;

import creature.Creature;

public interface HatchableCell {
    Creature getCreature();

    boolean canHatch();
}
