package util;

public enum EyeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

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
        else if (rotateDirection == MoveDirection.COUNTERCLOCKWISE) {
            return switch (this) {
                case UP -> LEFT;
                case DOWN -> RIGHT;
                case LEFT -> DOWN;
                case RIGHT -> UP;
            };
        }

        throw new IllegalArgumentException("Supplied MoveDirection must be a rotation");
    }
}
