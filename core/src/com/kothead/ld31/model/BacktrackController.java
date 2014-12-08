package com.kothead.ld31.model;

import static com.kothead.ld31.data.Configuration.LABYRINTH_WIDTH;
import static com.kothead.ld31.data.Configuration.LABYRINTH_HEIGHT;

import com.badlogic.gdx.Gdx;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.util.Util;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * Created by st on 12/7/14.
 */
public class BacktrackController implements LabyrinthController {

    private static final int PROJECTION_DEEPNESS = 6;
    private static final int MAX_PORTALS = 10;
    private static final int SEED = 100;

    private static final int HOME = 0;
    private static final int VIEW = 1;
    private static final int PORTAL = 2;
    private static final int XEN = 3;

    private Random random;
    private Stack<Integer> visited;
    private Labyrinth previous, current, next;
    private int prevX, prevY, nextX, nextY, playerX, playerY, deepness;
    private int[][] projectionPrev;
    private int[][] projectionNext;

    public BacktrackController() {
        random = new Random();
        projectionNext = new int[LABYRINTH_HEIGHT][LABYRINTH_WIDTH];
        projectionPrev = new int[LABYRINTH_HEIGHT][LABYRINTH_WIDTH];

        current = new LabyrinthBacktrack.Builder()
                .setCanMoveBack(false)
                .setWidth(LABYRINTH_WIDTH)
                .setHeight(LABYRINTH_HEIGHT)
                .setSeed(SEED)
                .setStartX(0)
                .setStartY(0)
                .create();

        prevX = 0;
        prevY = 0;
        deepness = 0;
        generateNext();
    }

    @Override
    public void moveTo(int x, int y) {
        int oldX = playerX;
        int oldY = playerY;
        playerX = x;
        playerY = y;
        int was = projectionNext[oldY][oldX];
        int now = projectionNext[y][x];
        if (was == PORTAL && now == XEN) {
            moveIntoNext();
        }
    }

    @Override
    public int getWidth() {
        return current.getWidth();
    }

    @Override
    public int getHeight() {
        return current.getHeight();
    }

    @Override
    public boolean hasWallRight(int x, int y) {
        int from = projectionNext[playerY][playerX];
        int where = projectionNext[y][x];

        if (from == VIEW || from == PORTAL) {
            if (where == XEN) {
                return next.hasWallRight(x, y);
            } else if (where == PORTAL) {
                return hasWallRightPortal(current, x, y);
            }
        }
        return current.hasWallRight(x, y);
    }

    @Override
    public boolean hasWallBottom(int x, int y) {
        int from = projectionNext[playerY][playerX];
        int where = projectionNext[y][x];

        if (from == VIEW || from == PORTAL) {
            if (where == XEN) {
                return next.hasWallBottom(x, y);
            } else if (where == PORTAL) {
                return hasWallBottomPortal(current, x, y);
            }
        }

        return current.hasWallBottom(x, y);
    }

    private void moveIntoNext() {
        previous = current;
        current = next;
        generateNext();
    }

    private void moveIntoPrev() {

    }

    private void generateNext() {
        int[] deadends = findSomeDeadends();
        random.setSeed(SEED * deepness);
        random.nextBoolean();

        int index = random.nextInt(deadends.length / 2) * 2;
        recreateLabyrinth(deadends[index], deadends[index + 1]);
        nextX = deadends[index];
        nextY = deadends[index + 1];
        Gdx.app.log("next", nextX + "; " + nextY);
    }

    private void recreateLabyrinth(int startX, int startY) {
        Direction entrance = getDeadendEntrance(current, startX, startY);
        next = new LabyrinthBacktrack.Builder()
                .setWidth(LABYRINTH_WIDTH)
                .setHeight(LABYRINTH_HEIGHT)
                .setStartX(startX)
                .setStartY(startY)
                .setDirection(entrance.getOpposite())
                .setSeed(SEED * deepness)
                .create();
        generateProjection(current, next, startX, startY, projectionNext);
//        if (previous != null)
//            generateProjection(current, previous, startX, startY, projectionPrev);
    }

     private int[] findSomeDeadends() {
        int[] deadends = new int[MAX_PORTALS * 2];
        int count = 0;
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (isDeadend(current, j, i) && isSuitableDeadend(current, j, i)) {
                    deadends[count * 2] = j;
                    deadends[count * 2 + 1] = i;
                    count++;
                    if (count * 2 >= deadends.length) return deadends;
                }
            }
        }
        return Arrays.copyOf(deadends, count * 2);
    }

    private boolean isDeadend(Labyrinth labyrinth, int x, int y) {
        int count = 0;
        if (isPosValid(labyrinth, x - 1, y)
                && labyrinth.hasWallRight(x - 1, y)) count++;
        if (isPosValid(labyrinth, x, y + 1)
                && labyrinth.hasWallBottom(x, y + 1)) count++;
        if (labyrinth.hasWallBottom(x, y)) count++;
        if (labyrinth.hasWallRight(x, y)) count++;
        return count == 3;
    }

    private boolean isSuitableDeadend(Labyrinth labyrinth, int x, int y) {
        if (x == nextX && y == nextY) return false;
        Direction entrance = getDeadendEntrance(labyrinth, x, y);
        return isPosValid(labyrinth, x, y, entrance);
    }

    private boolean hasWallRightPortal(Labyrinth labyrinth, int x, int y) {
        Direction entrance = getDeadendEntrance(labyrinth, x, y);
        if (entrance == Direction.LEFT) return false;
        return labyrinth.hasWallRight(x, y);
    }

    private boolean hasWallBottomPortal(Labyrinth labyrinth, int x, int y) {
        Direction entrance = getDeadendEntrance(labyrinth, x, y);
        if (entrance == Direction.TOP) return false;
        return labyrinth.hasWallBottom(x, y);
    }

    private void generateProjection(Labyrinth home, Labyrinth away,
                                    int startX, int startY, int[][] projection) {
        for (int i = 0; i < home.getHeight(); i++) {
            for (int j = 0; j < home.getWidth(); j++) {
                projection[i][j] = HOME;
            }
        }
        projection[startY][startX] = PORTAL;
        Direction dir = getDeadendEntrance(home, startX, startY);

        castOnProjection(away, projection, startX, startY,
                dir.getOpposite(), dir, XEN, 0);
        castOnProjection(home, projection, startX, startY,
                dir, dir.getOpposite(), VIEW, 0);
//        Util.logArray(projection);
//        Util.logArray(((LabyrinthBacktrack) away).getWalls());
    }

    private void castOnProjection(Labyrinth labyrinth, int[][] projection, int x, int y,
                                  Direction there, Direction notThere,
                                  int value, int step) {
        if (!hasPath(labyrinth, x, y, there)) return;
        x += Direction.getDx(there);
        y += Direction.getDy(there);

        if (projection[y][x] == PORTAL) return;
        projection[y][x] = value;
        step++;
        if (step > PROJECTION_DEEPNESS) return;

        for (Direction direction: Direction.getDirections()) {
            if (direction != notThere) {
                castOnProjection(labyrinth, projection, x, y,
                        direction, notThere,value, step);
            }
        }
    }

    private boolean hasPath(Labyrinth labyrinth, int x, int y, Direction direction) {
        if (!isPosValid(labyrinth, x, y, direction)) return false;

        switch (direction) {
            case RIGHT:
                return !labyrinth.hasWallRight(x, y);

            case LEFT:
                return !labyrinth.hasWallRight(x - 1, y);

            case BOTTOM:
                return !labyrinth.hasWallBottom(x, y);

            case TOP:
                return !labyrinth.hasWallBottom(x, y + 1);

            default:
                return true;
        }
    }

    private Direction getDeadendEntrance(Labyrinth labyrinth, int x, int y) {
        if (!labyrinth.hasWallRight(x, y)) return Direction.RIGHT;
        if (!labyrinth.hasWallBottom(x, y)) return Direction.BOTTOM;
        if (isPosValid(labyrinth, x - 1, y)
                && !labyrinth.hasWallRight(x - 1, y)) return Direction.LEFT;
        return Direction.TOP;
    }

    private boolean isPosValid(Labyrinth labyrinth, int x, int y, Direction direction) {
        return isPosValid(labyrinth, x + Direction.getDx(direction),
                y + Direction.getDy(direction));
    }

    private boolean isPosValid(Labyrinth labyrinth, int x, int y) {
        return x >= 0 && x < labyrinth.getWidth()
                && y >= 0 && y < labyrinth.getHeight();
    }

}
