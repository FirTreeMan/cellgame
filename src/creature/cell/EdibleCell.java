package creature.cell;

import creature.Creature;
import grid.Grid;

public interface EdibleCell {
    int getFoodValue();

    boolean isAlive();

    void kill();

    void onEaten(Creature creature);
}
