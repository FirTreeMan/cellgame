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
    public static Map<Cells, Float> DEFAULT_CELL_WEIGHTS = Map.ofEntries(
            Map.entry(Cells.BODY, 0.0F),
            Map.entry(Cells.MOUTH, 0.0F),
            Map.entry(Cells.LEG, 0.0F),
            Map.entry(Cells.DAMAGER, -0.1F),
            Map.entry(Cells.CHLOROPLAST, 0.0F),
            Map.entry(Cells.EYE, 0.0F),
            Map.entry(Cells.BRAIN, 0.0F),
            Map.entry(Cells.NULL, 0.0F),

            Map.entry(Cells.PLANT, 0.05F),
            Map.entry(Cells.MEAT, 0.1F),
            Map.entry(Cells.GLUCOSE, 0.05F),
            Map.entry(Cells.EGG, 0.1F)
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

    private final Map<Cells, Float> cellWeights;
    private final Map<EyeDirection, Float> facingWeights;
    private final HashMap<EyeDirection, Float> eyeDecisionMap;
    private final HashMap<EyeDirection, Float> leakMap;
    private final float eyeDirectionLeak;
    private final float actionThreshold;
    private final float clockwiseTurnThreshold;
    private final float counterClockwiseTurnThreshold;
    private final float reproductionPriority;
    private final int reproductionEnergyRemainder;

    private final int charGenNumber;

    private MoveDirection decision;
    private EyeDirection eyeDecision;

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Cells, Float> cellWeights, Map<EyeDirection, Float> facingWeights, float eyeDirectionLeak, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold, float reproductionPriority, int reproductionEnergyRemainder) {
        this(owner, relativeRow, relativeCol, cellWeights, facingWeights, eyeDirectionLeak, actionThreshold, clockwiseTurnThreshold, counterClockwiseTurnThreshold, reproductionPriority, reproductionEnergyRemainder, true);
    }

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Cells, Float> cellWeights, Map<EyeDirection, Float> facingWeights, float eyeDirectionLeak, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold, float reproductionPriority, int reproductionEnergyRemainder, boolean doMutate) {
        super(owner, Cells.BRAIN, 5, 0, relativeRow, relativeCol);

        if (doMutate) {
            this.cellWeights = mutateMap(cellWeights);
            this.facingWeights = mutateMap(facingWeights);

            this.eyeDirectionLeak = getOwner().mutateCell(eyeDirectionLeak);
            this.actionThreshold = getOwner().mutateCell(actionThreshold);
            this.clockwiseTurnThreshold = getOwner().mutateCell(clockwiseTurnThreshold);
            this.counterClockwiseTurnThreshold = getOwner().mutateCell(counterClockwiseTurnThreshold);
            this.reproductionPriority = getOwner().mutateCell(reproductionPriority);
            this.reproductionEnergyRemainder = getOwner().mutateCell(reproductionEnergyRemainder);
        } else {
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
        this.charGenNumber = 6;
    }

    public static BrainCell defaultBrain(Creature owner) {
        return new BrainCell(owner, 0, 0, DEFAULT_CELL_WEIGHTS, DEFAULT_FACING_WEIGHTS, 0.02F, 0.01F, 0.04F, 0.04F, 0.2F, 300, false);
    }

    public MoveDirection getDecision() {
        return decision;
    }

    public void makeDecision(HashMap<EyeDirection, ArrayList<Cell>> visibleCells, EyeDirection facing, boolean canReproduce, int energyAfterReproduction, boolean hasLegs) {
        makeEyeDecision(visibleCells);
        decision = calcDecisionFromEyeDecision(facing, canReproduce, energyAfterReproduction, hasLegs);
    }

    public MoveDirection calcDecisionFromEyeDecision(EyeDirection facing, boolean canReproduce, int energyAfterReproduction, boolean hasLegs) {
//        System.out.println(eyeDecisionMap.get(eyeDecision) + " " + facingWeights.get(eyeDecision.relativeTo(facing)));
        float eyeDecisionWeight = eyeDecisionMap.get(eyeDecision) + facingWeights.get(eyeDecision.relativeTo(facing));

        if (eyeDecisionWeight < reproductionPriority) {
            if (canReproduce &&
                    energyAfterReproduction - reproductionEnergyRemainder > 0)
                return MoveDirection.REPRODUCE;
        }
        if (!hasLegs || eyeDecisionWeight < actionThreshold) {
//            System.out.println(eyeDecisionWeight + " " + actionThreshold);
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
//        System.out.println(visibleCells);
        eyeDecisionMap.replaceAll((d, v) -> 0.0F);
        leakMap.replaceAll((d, v) -> 0.0F);

        for (EyeDirection eyeDirection: EyeDirection.values()) {
            float eyeWeight = calcEyeWeight(visibleCells.get(eyeDirection).toArray(Cell[]::new));
//            System.out.println(eyeWeight + " " + eyeDirection);
            if (eyeWeight > 0) {
                eyeDecisionMap.put(eyeDirection, eyeWeight);
                float leak = eyeWeight * eyeDirectionLeak;
                float oldLeakClockWise = leakMap.get(eyeDirection.rotate(MoveDirection.CLOCKWISE));
                float oldLeakCounterClockWise = leakMap.get(eyeDirection.rotate(MoveDirection.COUNTERCLOCKWISE));
                leakMap.put(eyeDirection.rotate(MoveDirection.CLOCKWISE), oldLeakClockWise + leak);
                leakMap.put(eyeDirection.rotate(MoveDirection.COUNTERCLOCKWISE), oldLeakCounterClockWise + leak);
            }
            else eyeDecisionMap.put(eyeDirection.getOpposite(), eyeDecisionMap.get(eyeDirection.getOpposite()) - eyeWeight);
        }
//        System.out.println(eyeDecisionMap);

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
            eyeWeight += cellWeights.get(cell.getCellEnum());

        return eyeWeight;
    }

    public BrainCell copyToChild(Creature newOwner) {
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
        int out = Math.abs(val * 100);
        out *= (int) (Math.pow(charGenNumber, 2) % 17) + 1;
        out %= 52;
        if (out >= 26)
            out += 32 - 26;
        return (char) (out + 65);
    }

    @Override
    public String toSpeciesString() {
        StringBuilder builder = new StringBuilder();

        float out1, out2, out3, out4;

        out1 = 0;
        for (float val: cellWeights.values())
            out1 += (float) Math.pow(val, 2);
        for (float val: facingWeights.values())
            out1 += (float) Math.pow(val, 2);
        out2 = eyeDirectionLeak * 8.32F / actionThreshold + 5.0F;
        out3 = clockwiseTurnThreshold % 0.035F * counterClockwiseTurnThreshold * 9.0F;
        out4 = Math.fma(reproductionPriority, 2.22F, reproductionEnergyRemainder);

        builder.append(getCharFromValue(out1));
        builder.append(getCharFromValue(out2));
        builder.append(getCharFromValue(out3));
        builder.append(getCharFromValue(out4));

        return builder.toString();
    }

    @Override
    public String getName() {
        return "Brain";
    }
}
