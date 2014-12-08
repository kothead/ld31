package com.kothead.ld31.view;

import static com.kothead.ld31.data.Configuration.*;
import static com.kothead.ld31.view.Wall.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.model.LabyrinthController;
import com.kothead.ld31.util.Util;

import java.util.Random;

/**
 * Created by st on 12/8/14.
 */
public class Enemy extends Walker {

    public static final int SIZE = 10;

    private static final float START_SPEED = 25f;
    private static final float SPEED_FACTOR = 2.5f;
    private static final int START_LIFE = 50;
    private static final int LIFE_PER_LEVEL = 50;
    private static final float EXP_PER_LIFE = 1f;
    private static final float EXP_FLOATING = 0.1f;
    private static final int MIN_DISTANCE_TO_PLAYER = 2;

    private static Random random = new Random();

    private LabyrinthController labyrinth;
    private Player player;
    private Direction goal;
    private int life, exp;
    private boolean seePlayer;
    private Rectangle rectEnemy, rectBullet, rectResult;
    private long seed;

    public Enemy(LabyrinthController labyrinth, Player player, long seed) {
        setSize(SIZE, SIZE);
        setSpeedValue(START_SPEED);
        this.labyrinth = labyrinth;
        this.player = player;
        this.seed = seed;
        rectEnemy = new Rectangle();
        rectBullet = new Rectangle();
        rectResult = new Rectangle();
        generatePosition();
    }

    public void setLevel(int level) {
        life = START_LIFE + LIFE_PER_LEVEL * level;
        exp = generateExpCount();
    }

    @Override
    public void draw(float delta, ShapeRenderer renderer) {
        super.draw(delta, renderer);
        renderer.setColor(Color.BLACK);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    public void process() {
        goal = decideWhereToGo(goal);

        stop();
        switch (goal) {
            case RIGHT:
                goRight();
                break;

            case BOTTOM:
                goDown();
                break;

            case LEFT:
                goLeft();
                break;

            case TOP:
                goUp();
        }

        if (seePlayer) {
            setVx(getVx() * SPEED_FACTOR);
            setVy(getVy() * SPEED_FACTOR);
        }
    }

    public boolean isDead() {
        return life <= 0;
    }

    public boolean hit(Bullet bullet) {
        rectEnemy.set(getX(), getY(), getWidth(), getHeight());
        rectBullet.set(bullet.getX(), bullet.getY(),
                bullet.getWidth(), bullet.getHeight());
        if (Intersector.intersectRectangles(rectEnemy, rectBullet, rectResult)) {
            life -= bullet.getDamage();
            return true;
        }
        return false;
    }

    public boolean grab() {
        rectEnemy.set(getX(), getY(), getWidth(), getHeight());
        //TODO: change bullet to player's rect
        rectBullet.set(player.getX(), player.getY(),
                player.getWidth(), player.getHeight());
        return Intersector.intersectRectangles(rectEnemy, rectBullet, rectResult);
    }

    private Direction decideWhereToGo(Direction goal) {
        Direction sight = hasPlayerInSight();
        if (sight != null) {
            seePlayer = true;
            return sight;
        } else {
            seePlayer = false;
        }

        int gridX = getGridX(getX() + SIZE / 2f);
        int gridY = getGridY(getY() + SIZE / 2f);
        int goalX = getGridX(getX() + SIZE / 2f + LABYRINTH_CELL_SIZE / 2f * Math.signum(getVx()));
        int goalY = getGridY(getY() + SIZE / 2f + LABYRINTH_CELL_SIZE / 2f * Math.signum(getVy()));

        Direction pass = Direction.getByOffset(goalX - gridX, goalY - gridY);
        Direction opposite = pass == null ? null : pass.getOpposite();

        if (goal != null && pass == null) return goal;
        if (pass != null && hasPath(gridX, gridY, pass)) return pass;

        for (Direction direction: Direction.getDirections()) {
            if (direction != pass && direction != opposite
                    && hasPath(gridX, gridY, direction)) return direction;
        }
        return opposite;
    }

    private Direction hasPlayerInSight() {
        int gridX = getGridX();
        int gridY = getGridY();
        int playerX = player.getGridX();
        int playerY = player.getGridY();
        int diffX = playerX - gridX;
        int diffY = playerY - gridY;
        if (diffX != 0 && diffY != 0) return null;

        Direction direction = Direction.getByOffset(diffX, diffY);

        // not to run in corner
        int cornerX = ((int) getX() + SIZE / 2) % LABYRINTH_CELL_SIZE;
        int cornerY = ((int) getY() + SIZE / 2) % LABYRINTH_CELL_SIZE;
        int test1 = SIZE / 2 + WALL_HEIGHT / 2;
        int test2 = LABYRINTH_CELL_SIZE - SIZE / 2 - WALL_HEIGHT / 2;
        if ((direction == Direction.TOP || direction == Direction.BOTTOM)
                && (cornerX <= test1 || cornerX >= test2)) return null;
        if ((direction == Direction.LEFT || direction == Direction.RIGHT)
                && (cornerY <= test1 || cornerY >= test2)) return null;

        int x = gridX;
        int y = gridY;
        while ((x != playerX || y != playerY)
                && hasPath(x, y, direction)) {
            x += Direction.getDx(direction);
            y += Direction.getDy(direction);
        }

        if (playerX == x && playerY == y) return direction;
        return null;
    }

    private boolean hasPath(int x, int y, Direction direction) {
        if (!isPosValid(x, y, direction)) return false;

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

    private boolean isPosValid(int x, int y, Direction direction) {
        return isPosValid(x + Direction.getDx(direction), y + Direction.getDy(direction));
    }

    private boolean isPosValid(int x, int y) {
        return x >= 0 && x < labyrinth.getWidth()
                && y >= 0 && y < labyrinth.getHeight();
    }

    private void generatePosition() {
        Random random = Util.getRandom();
        random.setSeed(seed);
        random.nextBoolean();
        float x, y;
        do {
            x = (int) (random.nextFloat() * LABYRINTH_WIDTH) * LABYRINTH_CELL_SIZE
                    + LABYRINTH_CELL_SIZE / 2 - getWidth() / 2;
            y = (int) (random.nextFloat() * LABYRINTH_HEIGHT) * LABYRINTH_CELL_SIZE
                    + LABYRINTH_CELL_SIZE / 2 - getHeight() / 2;
        } while (Math.abs(getGridX(x) - player.getGridX()) < MIN_DISTANCE_TO_PLAYER
                && Math.abs(getGridY(y) - player.getGridY()) < MIN_DISTANCE_TO_PLAYER);
        setPosition(x, y);
    }

    private int generateExpCount() {
        Random random = Util.getRandom();
        random.setSeed(seed * getGridX() * getGridY());
        random.nextBoolean();
        return (int) (random.nextInt((int) (EXP_PER_LIFE * life)) * EXP_FLOATING);
    }
}
