package com.kothead.ld31.data;

/**
 * Created by st on 12/7/14.
 */
public enum Direction {
    TOP, RIGHT, BOTTOM, LEFT;

    private Direction opposite;

    static {
        TOP.opposite = BOTTOM;
        RIGHT.opposite = LEFT;
        BOTTOM.opposite = TOP;
        LEFT.opposite = RIGHT;
    }

    public static Direction getByOffset(int dx, int dy) {
        if (dx > 0) return RIGHT;
        if (dx < 0) return LEFT;
        if (dy > 0) return TOP;
        if (dy < 0) return BOTTOM;
        return null;
    }

    public static int getDx(Direction dir) {
        if (dir == RIGHT) return 1;
        if (dir == LEFT) return -1;
        return 0;
    }

    public static int getDy(Direction dir) {
        if (dir == BOTTOM) return -1;
        if (dir == TOP) return 1;
        return 0;
    }

    public Direction getOpposite() {
        return opposite;
    }
};
