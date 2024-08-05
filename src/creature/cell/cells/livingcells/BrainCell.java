package creature.cell.cells.livingcells;

import creature.Creature;
import creature.cell.Cell;
import creature.cell.LivingCell;
import util.MoveDirection;
import util.EyeDirection;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrainCell extends LivingCell {
    private final Map<Class<? extends Cell>, Float> cellWeights;
    private final HashMap<EyeDirection, Float> eyeDecisionMap;
    private final float actionThreshold;
    private final float clockwiseTurnThreshold;
    private final float counterClockwiseTurnThreshold;

    private MoveDirection decision;
    private EyeDirection eyeDecision;

    public BrainCell(Creature owner, int relativeRow, int relativeCol, Map<Class<? extends Cell>, Float> cellWeights, float actionThreshold, float clockwiseTurnThreshold, float counterClockwiseTurnThreshold) {
        super(owner, new Color(252, 205, 35), 10, 5, relativeRow, relativeCol);
        this.cellWeights = mutateMap(cellWeights);

        this.actionThreshold = getOwner().mutateCell(actionThreshold);
        this.clockwiseTurnThreshold = getOwner().mutateCell(clockwiseTurnThreshold);
        this.counterClockwiseTurnThreshold = getOwner().mutateCell(counterClockwiseTurnThreshold);

        this.decision = MoveDirection.NEUTRAL;
        this.eyeDecision = EyeDirection.UP;
        this.eyeDecisionMap = new HashMap<>();
        for (EyeDirection eyeDirection: EyeDirection.values())
            this.eyeDecisionMap.put(eyeDirection, 0.0F);
    }

    public MoveDirection getDecision() {
        return decision;
    }

    public void makeDecision(HashMap<EyeDirection, Cell[]> visibleCells, EyeDirection facing) {
        makeEyeDecision(visibleCells);
        decision = calcDecisionFromEyeDecision(facing);
    }

    public MoveDirection calcDecisionFromEyeDecision(EyeDirection facing) {
        float eyeDecisionWeight = eyeDecisionMap.get(eyeDecision);
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

    public void makeEyeDecision(HashMap<EyeDirection, Cell[]> visibleCells) {
        for (EyeDirection eyeDirection: EyeDirection.values()) {
            float eyeWeight = calcEyeWeight(visibleCells.get(eyeDirection));
            if (eyeWeight > 0)
                eyeDecisionMap.put(eyeDirection, eyeWeight);
            else eyeDecisionMap.put(eyeDirection.getOpposite(), -eyeWeight);
        }

        EyeDirection heaviest = EyeDirection.UP;
        for (EyeDirection eyeDirection: eyeDecisionMap.keySet()) {
            if (eyeDecisionMap.get(eyeDirection) > eyeDecisionMap.get(heaviest))
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
    
    private Map<Class<? extends Cell>, Float> mutateMap(Map<Class<? extends Cell>, Float> map) {
        HashMap<Class<? extends Cell>, Float> mutatedMap = new HashMap<>(map);

        for (Class<? extends Cell> key: map.keySet()) {
            float value = map.get(key);
            float mutatedValue = getOwner().mutateCell(value);
            mutatedMap.put(key, mutatedValue);
        }
        
        return Collections.unmodifiableMap(mutatedMap);
    }
}
