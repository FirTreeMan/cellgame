package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.Cells;
import util.MoveDirection;
import util.EyeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrainCell extends LivingCell {
    public static Map<Class<? extends Cell>, Float> DEFAULT_CELL_WEIGHTS = Map.ofEntries(

    );
    public static Map<EyeDirection, Float> DEFAULT_FACING_WEIGHTS = Map.ofEntries(
            Map.entry(EyeDirection.UP, 0.0F),
            Map.entry(EyeDirection.DOWN, 0.0F),
            Map.entry(EyeDirection.LEFT, 0.0F),
            Map.entry(EyeDirection.RIGHT, 0.0F)
    );
    public static String[] BRAIN_PARAMS = new String[]{
            "facingUpWeight:",
            "facingDownWeight:",
            "facingLeftWeight:",
            "facingRightWeight:",
            "eyeDirectionLeak:",
            "actionThreshold:",
            "clockwiseTurnThreshold:",
            "counterClockwiseTurnThreshold:",
            "reproductionPriority:",
            "reproductionEnergyRemainder:",
    };

    private final Map<Class<? extends Cell>, Float> cellWeights;
    private final Map<EyeDirection, Float> facingWeights;
    private final HashMap<EyeDirection, Float> eyeDecisionMap;
    private final HashMap<EyeDirection, Float> leakMap;
    private final float eyeDirectionLeak;
    private final float actionThreshold;
    private final float clockwiseTurnThreshold;
    private final float counterClockwiseTurnThreshold;
    private final float reproductionPriority;
    private final int reproductionEnergyRemainder;

    private MoveDirection decision;
    private EyeDirection eyeDecision;

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Class<? extends Cell>, Float> cellWeights, Map<EyeDirection, Float> facingWeights, float eyeDirectionLeak, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold, float reproductionPriority, int reproductionEnergyRemainder) {
        this(owner, relativeRow, relativeCol, cellWeights, facingWeights, eyeDirectionLeak, actionThreshold, clockwiseTurnThreshold, counterClockwiseTurnThreshold, reproductionPriority, reproductionEnergyRemainder, true);
    }

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Class<? extends Cell>, Float> cellWeights, Map<EyeDirection, Float> facingWeights, float eyeDirectionLeak, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold, float reproductionPriority, int reproductionEnergyRemainder, boolean doMutate) {
        super(owner, Cells.BRAIN.get(), 10, 5, relativeRow, relativeCol);

        if (doMutate) {
            this.cellWeights = mutateMap(cellWeights);
            this.facingWeights = mutateMap(facingWeights);

            this.eyeDirectionLeak = getOwner().mutateCell(eyeDirectionLeak);
            this.actionThreshold = getOwner().mutateCell(actionThreshold);
            this.clockwiseTurnThreshold = getOwner().mutateCell(clockwiseTurnThreshold);
            this.counterClockwiseTurnThreshold = getOwner().mutateCell(counterClockwiseTurnThreshold);
            this.reproductionPriority = getOwner().mutateCell(reproductionPriority);
            this.reproductionEnergyRemainder = getOwner().mutateCell(reproductionEnergyRemainder);
        }
        else {
            this.cellWeights = cellWeights;
            this.facingWeights = facingWeights;

            this.eyeDirectionLeak = eyeDirectionLeak;
            this.actionThreshold = actionThreshold;
            this.clockwiseTurnThreshold = clockwiseTurnThreshold;
            this.counterClockwiseTurnThreshold = counterClockwiseTurnThreshold;
            this.reproductionPriority = reproductionPriority;
            this.reproductionEnergyRemainder = reproductionEnergyRemainder;
        }

        this.decision = MoveDirection.NEUTRAL;
        this.eyeDecision = EyeDirection.UP;
        this.eyeDecisionMap = EyeDirection.getFloatHashMap();
        this.leakMap = EyeDirection.getFloatHashMap();
    }

    public static BrainCell defaultBrain(Creature owner) {
        return new BrainCell(owner, 0, 0, DEFAULT_CELL_WEIGHTS, DEFAULT_FACING_WEIGHTS, 0.02F, 0.01F, 0.04F, 0.04F, 0.1F, 200, false);
    }

    public MoveDirection getDecision() {
        return decision;
    }

    public void makeDecision(HashMap<EyeDirection, ArrayList<Cell>> visibleCells, EyeDirection facing, boolean canReproduce, int energyAfterReproduction, boolean hasLegs) {
        makeEyeDecision(visibleCells);
        decision = calcDecisionFromEyeDecision(facing, canReproduce, energyAfterReproduction, hasLegs);
    }

    public MoveDirection calcDecisionFromEyeDecision(EyeDirection facing, boolean canReproduce, int energyAfterReproduction, boolean hasLegs) {
        float eyeDecisionWeight = eyeDecisionMap.get(eyeDecision) + facingWeights.get(eyeDecision.relativeTo(facing));

        if (eyeDecisionWeight < reproductionPriority) {
            if (canReproduce &&
                    energyAfterReproduction - reproductionEnergyRemainder > 0)
                return MoveDirection.REPRODUCE;
        }
        if (!hasLegs || eyeDecisionWeight < actionThreshold) {
            return MoveDirection.NEUTRAL;
        }

        if (eyeDecision != facing) {
            // choose to turn clockwise or move sideways
            if (eyeDecision == facing.rotate(MoveDirection.CLOCKWISE))
                return eyeDecisionWeight > clockwiseTurnThreshold ? MoveDirection.CLOCKWISE : eyeDecision.asMoveDirection();
            // choose to turn counterclockwise or move sideways
            if (eyeDecision == facing.rotate(MoveDirection.COUNTERCLOCKWISE))
                return eyeDecisionWeight > counterClockwiseTurnThreshold ? MoveDirection.COUNTERCLOCKWISE : eyeDecision.asMoveDirection();
            // need to turn around, choose preferred rotation
            return clockwiseTurnThreshold < counterClockwiseTurnThreshold ? MoveDirection.CLOCKWISE : MoveDirection.COUNTERCLOCKWISE;
        }

        return eyeDecision.asMoveDirection();
    }

    public void makeEyeDecision(HashMap<EyeDirection, ArrayList<Cell>> visibleCells) {
        eyeDecisionMap.replaceAll((d, v) -> 0.0F);
        leakMap.replaceAll((d, v) -> 0.0F);

        for (EyeDirection eyeDirection: EyeDirection.values()) {
            float eyeWeight = calcEyeWeight(visibleCells.get(eyeDirection).toArray(Cell[]::new));
            if (eyeWeight > 0) {
                eyeDecisionMap.put(eyeDirection, eyeWeight);
                float leak = eyeWeight * eyeDirectionLeak;
                float oldLeakClockWise = leakMap.getOrDefault(eyeDirection.rotate(MoveDirection.CLOCKWISE), 0.0F);
                float oldLeakCounterClockWise = leakMap.getOrDefault(eyeDirection.rotate(MoveDirection.COUNTERCLOCKWISE), 0.0F);
                leakMap.put(eyeDirection.rotate(MoveDirection.CLOCKWISE), oldLeakClockWise + leak);
                leakMap.put(eyeDirection.rotate(MoveDirection.COUNTERCLOCKWISE), oldLeakCounterClockWise + leak);
            }
            else eyeDecisionMap.put(eyeDirection.getOpposite(), -eyeWeight);
        }

        EyeDirection heaviest = EyeDirection.UP;
        for (EyeDirection eyeDirection: eyeDecisionMap.keySet()) {
            if (eyeDecisionMap.get(eyeDirection) + leakMap.get(eyeDirection) > eyeDecisionMap.get(heaviest) + leakMap.get(heaviest))
                heaviest = eyeDirection;
        }
        eyeDecision = heaviest;
    }

    public float calcEyeWeight(Cell[] visibleCells) {
        float eyeWeight = 0.0F;

        for (Cell cell: visibleCells)
            eyeWeight += cellWeights.get(cell.getClass());

        return eyeWeight;
    }

    public BrainCell copyToOwner(Creature newOwner) {
        return new BrainCell(newOwner, getRelativeX(), getRelativeY(), cellWeights, facingWeights, eyeDirectionLeak, actionThreshold, clockwiseTurnThreshold, counterClockwiseTurnThreshold, reproductionPriority, reproductionEnergyRemainder);
    }

    public String[] getParamValues() {
        return new String[]{
                format(facingWeights.get(EyeDirection.UP)),
                format(facingWeights.get(EyeDirection.DOWN)),
                format(facingWeights.get(EyeDirection.LEFT)),
                format(facingWeights.get(EyeDirection.RIGHT)),
                format(eyeDirectionLeak),
                format(actionThreshold),
                format(clockwiseTurnThreshold),
                format(counterClockwiseTurnThreshold),
                format(reproductionPriority),
                format(reproductionEnergyRemainder),
        };
    }
    
    private <T> Map<T, Float> mutateMap(Map<T, Float> map) {
        HashMap<T, Float> mutatedMap = new HashMap<>(map);

        for (T key: map.keySet()) {
            float value = map.get(key);
            float mutatedValue = getOwner().mutateCell(value, 1000);
            mutatedMap.put(key, mutatedValue);
        }
        
        return Collections.unmodifiableMap(mutatedMap);
    }

    public char getCharFromValue(float val) {
        return getCharFromValue((int) (val * 1000));
    }

    // char in [A-Za-z]
    public char getCharFromValue(int val) {
        int out = val * 100;
        out %= 52;
        if (out >= 26)
            out += 32 - 26;
        return (char) (out + 65);
    }

    @Override
    public String toSpeciesString() {
        StringBuilder builder = new StringBuilder();

        DEFAULT_CELL_WEIGHTS.keySet().forEach(s ->
                builder.append(getCharFromValue(cellWeights.get(s))));
        DEFAULT_FACING_WEIGHTS.keySet().forEach(s ->
                builder.append(getCharFromValue(facingWeights.get(s))));
        builder.append(getCharFromValue(eyeDirectionLeak));
        builder.append(getCharFromValue(actionThreshold));
        builder.append(getCharFromValue(clockwiseTurnThreshold));
        builder.append(getCharFromValue(counterClockwiseTurnThreshold));
        builder.append(getCharFromValue(reproductionPriority));
        builder.append(getCharFromValue(reproductionEnergyRemainder));

        return builder.toString();
    }

    @Override
    public String getName() {
        return "Brain";
    }

    @Override
    public String getDescription() {
        return Cells.BRAIN.getDescription();
    }
}
