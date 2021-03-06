package com.kothead.ld31.model;

import com.badlogic.gdx.Gdx;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.data.SoundCache;
import com.kothead.ld31.util.Util;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import static com.kothead.ld31.data.Configuration.LABYRINTH_HEIGHT;
import static com.kothead.ld31.data.Configuration.LABYRINTH_WIDTH;

/**
 * Created by st on 12/7/14.
 */
public class BacktrackController implements LabyrinthController {

    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;

    private static final int PROJECTION_DEEPNESS = 10;
    private static final int MAX_PORTALS = 10;

    private static final int HOME = 0;
    private static final int VIEW = 1;
    private static final int PORTAL = 2;
    private static final int XEN = 3;

    private long seed;
    private Random random;
    private Stack<Integer> visited;
    private Labyrinth previous, current, next;
    private int prevX, prevY, nextX, nextY, playerX, playerY, deepness;
    private int[][] projectionPrev;
    private int[][] projectionNext;
    private int[][] removedWalls;
    private boolean updated;

    public BacktrackController(long seed) {
        this.seed = seed;
        random = Util.getRandom();
        projectionNext = new int[LABYRINTH_HEIGHT][LABYRINTH_WIDTH];
        projectionPrev = new int[LABYRINTH_HEIGHT][LABYRINTH_WIDTH];
        removedWalls = new int[LABYRINTH_HEIGHT + 2][LABYRINTH_WIDTH + 2];

        current = new LabyrinthBacktrack.Builder()
                .setCanMoveBack(false)
                .setWidth(LABYRINTH_WIDTH)
                .setHeight(LABYRINTH_HEIGHT)
                .setSeed(seed)
                .setStartX(0)
                .setStartY(0)
                .create();

        prevX = 0;
        prevY = 0;
        deepness = 0;
        generateNext();
    }

    public boolean isUpdated() {
        return updated;
    }

    public int getLevel() {
        return deepness;
    }

    public boolean isPortal(int x, int y) {
        return projectionNext[y][x] == PORTAL;
    }

    public void destroyWallRight(int x, int y) {
        removedWalls[y + 1][x + 1] |= RIGHT;
    }

    public void destroyWallBottom(int x, int y) {
        removedWalls[y + 1][x + 1] |= BOTTOM;
    }

    @Override
    public void moveTo(int x, int y) {
        updated = false;

        int oldX = playerX;
        int oldY = playerY;
        playerX = x;
        playerY = y;
        int was = projectionNext[oldY][oldX];
        int now = projectionNext[y][x];
        if (was == PORTAL && now == XEN) {
            moveIntoNext();
            updated = true;
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
        if ((removedWalls[y + 1][x + 1] & RIGHT) == RIGHT) return false;
        if (x <= -1 || y <= -1 || x >= getWidth() || y >= getHeight()) return true;

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
        if ((removedWalls[y + 1][x + 1] & BOTTOM) == BOTTOM) return false;
        if (x <= -1 || y <= -1 || x >= getWidth() || y >= getHeight()) return true;

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
        clearRemovedWalls();
        previous = current;
        current = next;
        deepness++;
        generateNext();
        SoundCache.play(SoundCache.SOUND_GLITCH);
    }

    private void moveIntoPrev() {

    }

    private void generateNext() {
        int[] deadends = findSomeDeadends();
        random.setSeed(seed * deepness);
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
                .setSeed(seed * deepness)
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
        if (x == nextX && y == nextY && x != 0) return false;
        if (x == 0 || y == 0) return false;
        if (x == labyrinth.getWidth() - 1
                || y == labyrinth.getHeight() - 1) return false;
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
        //Util.logArray(projection);
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

    private void clearRemovedWalls() {
        for (int i = 0; i < removedWalls.length; i++) {
            for (int j = 0; j < removedWalls[0].length; j++) {
                removedWalls[i][j] = 0;
            }
        }
    }
}
