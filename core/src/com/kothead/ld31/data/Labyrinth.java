package com.kothead.ld31.data;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by st on 12/7/14.
 */
public class Labyrinth {

    public static final int WALL_RIGHT = 1;
    public static final int WALL_BOTTOM = 2;

    private static final int PATH_COUNT = Integer.MAX_VALUE;
    private static final int CLEAR_VALUE = -1;
    private static final int REVISITED_CHECK_COUNT = 4;
    private static final int DEEPNESS = 3;

    private enum Direction {
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
            if (dy > 0) return BOTTOM;
            if (dy < 0) return TOP;
            return null;
        }

        public static int getDx(Direction dir) {
            if (dir == RIGHT) return 1;
            if (dir == LEFT) return -1;
            return 0;
        }

        public static int getDy(Direction dir) {
            if (dir == BOTTOM) return 1;
            if (dir == TOP) return -1;
            return 0;
        }

        public Direction getOpposite() {
            return opposite;
        }
    };

    private int seed;
    private Random random;
    private int width, height;
    private int oldX, oldY, curX, curY;
    private int[][] steps;
    private int[][] walls;
    private List<Direction> path;

    public Labyrinth(int width, int height) {
        this.width = width;
        this.height = height;

        steps = new int[height][width];
        walls = new int[height][width];
        clearArray(steps);
        clearArray(walls, 0);

        path = new ArrayList<Direction>();

        seed = (int) (Math.random() * PATH_COUNT);
        random = new Random();

        moveTo(0, 0);
    }

    public boolean hasVerticalWall(int x, int y) {
        return (walls[y][x] & WALL_RIGHT) > 0;
    }

    public boolean hasHorizontalWall(int x, int y) {
        return (walls[y][x] & WALL_BOTTOM) > 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void moveTo(int x, int y) {
        oldX = curX;
        oldY = curY;
        curX = x;
        curY = y;

        processDirection();
        calcSteps();
        generateWalls();
        logArray(steps);
    }

    private void processDirection() {
        int dx = curX - oldX;
        int dy = curY - oldY;

        Direction dir = Direction.getByOffset(dx, dy);
        if (dir == null) {
            path.clear();
            Gdx.app.log("path", "clear()");
        } else {
            int revisitedId = findWhenVisited(dir, path, REVISITED_CHECK_COUNT);
            if (revisitedId > -1) {
                int last = path.size() - 1;
                for (int i = last; i >= revisitedId; i--) path.remove(i);
                Gdx.app.log("path", "revisited()");
            } else {
                path.add(dir);
                Gdx.app.log("path", "add()");
            }
        }
    }

    /**
     * finds out was cell in given direction visited during last moves
     * @param dir   in which we want move
     * @param list  of old move directions
     * @param count moves count
     * @return revisited item id or -1 if never
     */
    private int findWhenVisited(Direction dir, List<Direction> list, int count) {
        int last = list.size() - 1;
        int dx = Direction.getDx(dir);
        int dy = Direction.getDy(dir);
        for (int i = last; i >= 0 && i > last - count; i--) {
            Direction prev = list.get(i);
            dx += Direction.getDx(prev);
            dy += Direction.getDy(prev);
            if (dx == 0 && dy == 0) return i;
        }
        return -1;
    }

    private void calcSteps() {
        clearArray(steps, CLEAR_VALUE);
        int x = curX;
        int y = curY;
        int last = path.size() - 1;
        for (int i = last; i >= 0 && i > last - DEEPNESS; i--) {
            Direction prev = path.get(i).getOpposite();
            x += Direction.getDx(prev);
            y += Direction.getDy(prev);
            steps[y][x] = i;
        }
        calcStep(curX, curY, last + 1, DEEPNESS);
    }

    private void calcStep(int x, int y, int value, int iteration) {
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;

        if (steps[y][x] == CLEAR_VALUE || steps[y][x] > value) {
            steps[y][x] = value;
        } else {
            value = steps[y][x];
        }
        value++;

        iteration--;
        if (iteration == 0) return;

        calcStep(x + 1, y, value, iteration);
        calcStep(x - 1, y, value, iteration);
        calcStep(x, y + 1, value, iteration);
        calcStep(x, y - 1, value, iteration);
    }

    private void generateWalls() {
        clearArray(walls, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                generateWall(j, i);
            }
        }
    }

    private void generateWall(int x, int y) {
        if (steps[y][x] == CLEAR_VALUE) return;
        random.setSeed(steps[y][x] * (y - x));
        random.nextBoolean();
        if (random.nextBoolean()) {
            walls[y][x] |= WALL_RIGHT;
        } else {
            walls[y][x] |= WALL_BOTTOM;
        }
    }

    private void clearArray(int[][] array) {
        clearArray(array, CLEAR_VALUE);
    }

    private void clearArray(int[][] array, int value) {
        for (int i = 0; i < array.length; i++) {
            int[] inner = array[i];
            for (int j = 0; j < inner.length; j++) {
                inner[j] = value;
            }
        }
    }

    private void logArray(int[][] array) {
        for (int i = 0; i < height; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < width; j++) {
                builder.append(String.format("%2d ", array[i][j]));
            }
            Gdx.app.log("wall", builder.toString());
        }
        Gdx.app.log("wall", "--------------------------------------------");
    }
}
