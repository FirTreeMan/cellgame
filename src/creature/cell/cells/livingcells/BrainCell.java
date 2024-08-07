package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.MoveDirection;
import util.EyeDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrainCell extends LivingCell {
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
    private final int reproductionEnergyRemainderRange;

    private MoveDirection decision;
    private EyeDirection eyeDecision;

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Class<? extends Cell>, Float> cellWeights, Map<EyeDirection, Float> facingWeights, float eyeDirectionLeak, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold, float reproductionPriority, int reproductionEnergyRemainder, int reproductionEnergyRemainderRange) {
        super(owner, new Color(252, 205, 35), 10, 5, relativeRow, relativeCol);
        this.cellWeights = mutateMap(cellWeights);
        this.facingWeights = mutateMap(facingWeights);

        this.eyeDirectionLeak = getOwner().mutateCell(eyeDirectionLeak);
        this.actionThreshold = getOwner().mutateCell(actionThreshold);
        this.clockwiseTurnThreshold = getOwner().mutateCell(clockwiseTurnThreshold);
        this.counterClockwiseTurnThreshold = getOwner().mutateCell(counterClockwiseTurnThreshold);
        this.reproductionPriority = reproductionPriority;
        this.reproductionEnergyRemainder = reproductionEnergyRemainder;
        this.reproductionEnergyRemainderRange = reproductionEnergyRemainderRange;

        this.decision = MoveDirection.NEUTRAL;
        this.eyeDecision = EyeDirection.UP;
        this.eyeDecisionMap = new HashMap<>(4);
        this.leakMap = new HashMap<>(4);
    }

    public MoveDirection getDecision() {
        return decision;
    }

    public void makeDecision(HashMap<EyeDirection, ArrayList<Cell>> visibleCells, EyeDirection facing, boolean canReproduce, int energyAfterReproduction) {
        makeEyeDecision(visibleCells);
        decision = calcDecisionFromEyeDecision(facing, canReproduce, energyAfterReproduction);
    }

    public MoveDirection calcDecisionFromEyeDecision(EyeDirection facing, boolean canReproduce, int energyAfterReproduction) {
        float eyeDecisionWeight = eyeDecisionMap.get(eyeDecision) + facingWeights.get(eyeDecision.relativeTo(facing));

        if (eyeDecisionWeight < reproductionPriority) {
            if (canReproduce &&
                    // reproductionEnergyRemainder - reproductionEnergyRemainderRange < energyAfterReproduction < reproductionEnergyRemainder + reproductionEnergyRemainderRange
                    Math.abs(energyAfterReproduction - reproductionEnergyRemainder) < reproductionEnergyRemainderRange)
                return MoveDirection.REPRODUCE;
        }
        if (eyeDecisionWeight < actionThreshold) {
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
    
    private <T> Map<T, Float> mutateMap(Map<T, Float> map) {
        HashMap<T, Float> mutatedMap = new HashMap<>(map);

        for (T key: map.keySet()) {
            float value = map.get(key);
            float mutatedValue = getOwner().mutateCell(value, 1000);
            mutatedMap.put(key, mutatedValue);
        }
        
        return Collections.unmodifiableMap(mutatedMap);
    }
}
