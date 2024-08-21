package creature.cell;

public interface EdibleCell {
    int getFoodValue();

    boolean isAlive();

    void kill();
}
