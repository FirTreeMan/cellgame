package util;

import creature.cell.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public enum EyeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static HashMap<EyeDirection, ArrayList<Cell>> getCellHashMap() {
        HashMap<EyeDirection, ArrayList<Cell>> hashMap = new HashMap<>(4);
        for (EyeDirection eyeDirection: values())
            hashMap.put(eyeDirection, new ArrayList<>());
        return hashMap;
    }

    public static HashMap<EyeDirection, Float> getFloatHashMap() {
        HashMap<EyeDirection, Float> hashMap = new HashMap<>(4);
        for (EyeDirection eyeDirection: values())
            hashMap.put(eyeDirection, 0.0F);
        return hashMap;
    }

    public static EyeDirection getRandom(Random random) {
        int index = random.nextInt(EyeDirection.values().length);
        return EyeDirection.values()[index];
    }

    public EyeDirection getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public MoveDirection asMoveDirection() {
        return switch (this) {
            case UP -> MoveDirection.UP;
            case DOWN -> MoveDirection.DOWN;
            case LEFT -> MoveDirection.LEFT;
            case RIGHT -> MoveDirection.RIGHT;
        };
    }

    public EyeDirection rotate(MoveDirection rotateDirection) {
        if (rotateDirection == MoveDirection.CLOCKWISE) {
            return switch (this) {
                case UP -> RIGHT;
                case DOWN -> LEFT;
                case LEFT -> UP;
                case RIGHT -> DOWN;
            };
        }
        if (rotateDirection == MoveDirection.COUNTERCLOCKWISE) {
            return switch (this) {
                case UP -> LEFT;
                case DOWN -> RIGHT;
                case LEFT -> DOWN;
                case RIGHT -> UP;
            };
        }

        throw new IllegalArgumentException("Supplied MoveDirection must be a rotation");
    }

    // default EyeDirection is UP
    public EyeDirection relativeTo(EyeDirection facing) {
        return switch (facing) {
            case UP -> this;
            case DOWN -> this.getOpposite();
            case LEFT -> this.rotate(MoveDirection.CLOCKWISE);
            case RIGHT -> this.rotate(MoveDirection.COUNTERCLOCKWISE);
        };
    }

    public int relativeX(int x, int y) {
        return switch (this) {
            case UP -> x;
            case DOWN -> -x;
            case LEFT -> y;
            case RIGHT -> -y;
        };
    }

    public int relativeY(int x, int y) {
        return switch (this) {
            case UP -> y;
            case DOWN -> -y;
            case LEFT -> x;
            case RIGHT -> -x;
        };
    }
}
