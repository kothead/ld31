package com.kothead.ld31.data;

import com.badlogic.gdx.Gdx;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * Created by st on 12/7/14.
 */
public class LabyrinthBacktrack {

    public static final int WALL_RIGHT = 1;
    public static final int WALL_BOTTOM = 2;

    private static final int RETAIN_SIZE = 3;

    private int seed;
    private Random random;
    private int width, height;
    private int[][] walls;

    private LabyrinthBacktrack(int width, int height) {
        this.width = width;
        this.height = height;

        walls = new int[height][width];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasWallRight(int x, int y) {
        return (walls[y][x] & WALL_RIGHT) == WALL_RIGHT;
    }

    public boolean hasWallBottom(int x, int y) {
        return (walls[y][x] & WALL_BOTTOM) == WALL_BOTTOM;
    }

    private void setWallRight(int x, int y) {
        walls[y][x] |= WALL_RIGHT;
    }

    private void setWallBottom(int x, int y) {
        walls[y][x] |= WALL_BOTTOM;
    }

    private void removeWallRight(int x, int y) {
        walls[y][x] &= ~WALL_RIGHT;
    }

    private void removeWallBottom(int x, int y) {
        walls[y][x] &= ~WALL_BOTTOM;
    }

    public static class Builder {

        private int width;
        private int height;
        private int startX;
        private int startY;
        private Direction direction;
        private boolean canMoveBack;
        private Labyrinth copyFrom;
        private int copyWidth;
        private int copyHeight;

        private LabyrinthBacktrack labyrinth;
        private boolean visited[][];
        private int totalVisited;
        private int total;
        private Random random;

        public Builder() {
            random = new Random();
        }

        public Builder setSeed(long seed) {
            random.setSeed(seed);
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setStartX(int startX) {
            this.startX = startX;
            return this;
        }

        public Builder setStartY(int startY) {
            this.startY = startY;
            return this;
        }

        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder setCanMoveBack(boolean canMoveBack) {
            this.canMoveBack = canMoveBack;
            return this;
        }

        public Builder setCopyFrom(Labyrinth copyFrom) {
            this.copyFrom = copyFrom;
            return this;
        }

        public Builder setCopyWidth(int copyWidth) {
            this.copyWidth = copyWidth;
            return this;
        }

        public Builder setCopyHeight(int copyHeight) {
            this.copyHeight = copyHeight;
            return this;
        }

        public LabyrinthBacktrack create() {
            labyrinth = new LabyrinthBacktrack(width, height);
            generateWalls();

            visited = new boolean[height][width];
            total = width * height;
            totalVisited = 0;
            if (direction != null) {
                int diffX = Direction.getDx(direction.getOpposite());
                int diffY = Direction.getDy(direction.getOpposite());
                int prevX = startX + diffX;
                int prevY = startY + diffY;
                markVisited(prevX, prevY);
            }

            Stack<Integer> stack = new Stack<Integer>();
            int curX = startX;
            int curY = startY;
            markVisited(startX, startY);

            do {
                int[] neighbours = getNeighbours(curX, curY);
                if (neighbours.length > 0) {
                    int[] neighbour = getRandomNeighbour(neighbours);
                    removeWall(curX, curY, neighbour[0], neighbour[1]);
                    markVisited(neighbour[0], neighbour[1]);
                    stack.push(curX);
                    stack.push(curY);
                    curX = neighbour[0];
                    curY = neighbour[1];
                } else if (!stack.empty()) {
                    curY = stack.pop();
                    curX = stack.pop();
                } else {
                    int[] unvisited = getRandomUnvisited();
                    curX = unvisited[0];
                    curY = unvisited[1];
                }
            } while (!stack.empty() || totalVisited < total);

            logArray(labyrinth.walls);
            return labyrinth;
        }

        private void generateWalls() {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    labyrinth.setWallBottom(j, i);
                    labyrinth.setWallRight(j, i);
                }
            }
        }

        private void removeWall(int x1, int y1, int x2, int y2) {
            int diffX = x2 - x1;
            int diffY = y2 - y1;
            Direction dir = Direction.getByOffset(diffX, diffY);
            switch (dir) {
                case TOP:
                    labyrinth.removeWallBottom(x2, y2);
                    break;

                case RIGHT:
                    labyrinth.removeWallRight(x1, y1);
                    break;

                case BOTTOM:
                    labyrinth.removeWallBottom(x1, y1);
                    break;

                case LEFT:
                    labyrinth.removeWallRight(x2, y2);
                    break;
            }
        }

        private void markVisited(int x, int y) {
            visited[y][x] = true;
            totalVisited++;
        }

        private int[] getNeighbours(int curX, int curY) {
            Direction[] directions = {Direction.TOP, Direction.RIGHT, Direction.BOTTOM, Direction.LEFT};
            int[] neighbours = new int[directions.length * 2];
            int count = 0;
            for (Direction direction: directions) {
                int x = curX + Direction.getDx(direction);
                int y = curY + Direction.getDy(direction);
                if (x < 0 || y < 0 || x >= width || y >= height || visited[y][x]) continue;
                neighbours[count++] = x;
                neighbours[count++] = y;
            }
            return Arrays.copyOf(neighbours, count);
        }

        private int[] getRandomNeighbour(int[] neighbours) {
            int index = random.nextInt(neighbours.length / 2) * 2;
            return Arrays.copyOfRange(neighbours, index, index + 2);
        }

        private int[] getRandomUnvisited() {
            // TODO: make pick random
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (!visited[i][j]) return new int[] {j, i};
                }
            }
            return  null;
        }

        private void logArray(int[][] array) {
            for (int i = height - 1; i >= 0; i--) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < width; j++) {
                    builder.append(String.format("%2d ", array[i][j]));
                }
            }
        }
    }
}
