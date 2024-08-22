package util;

public enum MoveDirection {
    NEUTRAL,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    CLOCKWISE,
    COUNTERCLOCKWISE,
    REPRODUCE;

    public boolean isMotion() {
        return this.isOrthogonal() || this.isRotation();
    }

    public boolean isOrthogonal() {
        return this == UP || this == DOWN || this == LEFT || this == RIGHT;
    }

    public boolean isRotation() {
        return this == CLOCKWISE || this == COUNTERCLOCKWISE;
    }

    public MoveDirection rotate(MoveDirection rotateDirection) {
        if (rotateDirection == CLOCKWISE) {
            return switch (this) {
                case UP -> RIGHT;
                case DOWN -> LEFT;
                case LEFT -> UP;
                case RIGHT -> DOWN;
                default -> this;
            };
        }
        else if (rotateDirection == COUNTERCLOCKWISE) {
            return switch (this) {
                case UP -> LEFT;
                case DOWN -> RIGHT;
                case LEFT -> DOWN;
                case RIGHT -> UP;
                default -> this;
            };
        }

        throw new IllegalArgumentException("Supplied MoveDirection must be a rotation");
    }
}
