package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.model.LabyrinthBacktrack;
import com.kothead.ld31.model.LabyrinthController;
import com.kothead.ld31.screen.GameScreen;

/**
 * Created by st on 12/7/14.
 */
public class Player extends Sprite {

    public static final int SIZE = 10;

    private static final float SPEED = 100f;

    private float vx, vy;
    private Rectangle rectPlayer, rectObstacle, overlap;
    private boolean horizontalBlocked, verticalBlocked;

    public Player() {
        setSize(SIZE, SIZE);
        rectPlayer = new Rectangle();
        rectPlayer.setSize(SIZE, SIZE);
        rectObstacle = new Rectangle();
        overlap = new Rectangle();
    }

    public void setNotBlocked() {
        horizontalBlocked = false;
        verticalBlocked = false;
    }

    public void processWall(float delta, Wall wall) {
        rectObstacle.set(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        if (!horizontalBlocked) {
            rectPlayer.setPosition(getX() + vx * delta, getY());
            if (Intersector.intersectRectangles(rectPlayer, rectObstacle, overlap)) {
                horizontalBlocked = true;
            }
        }
        if (!verticalBlocked) {
            rectPlayer.setPosition(getX(), getY() + vy * delta);
            if (Intersector.intersectRectangles(rectPlayer, rectObstacle, overlap)) {
                verticalBlocked = true;
            }
        }
    }

    public void updateLabyrinth(float delta, LabyrinthController controller) {
        int oldX = getGridX();
        int oldY = getGridY();
        int curX = getGridX(getX() + delta * vx);
        int curY = getGridY(getY() + delta * vy);

        if ((curX != oldX || curY != oldY)
                && curX >= 0 && curX < Configuration.LABYRINTH_WIDTH
                && curY >= 0 && curY < Configuration.LABYRINTH_HEIGHT) {
            controller.moveTo(curX, curY);
        }
    }

    public int getGridX() {
        return getGridX(getX());
    }

    public int getGridY() {
        return getGridY(getY());
    }

    public static int getGridX(float x) {
        return (int) (x / Configuration.LABYRINTH_CELL_SIZE);
    }

    public static int getGridY(float y) {
        return (int) (y / Configuration.LABYRINTH_CELL_SIZE);
    }

    public void draw(float delta, ShapeRenderer renderer) {
        if (!horizontalBlocked) setX(getX() + vx * delta);
        if (!verticalBlocked) setY(getY() + vy * delta);
        renderer.setColor(Color.WHITE);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    public void goUp() {
        vy += SPEED;
    }

    public void goDown() {
        vy -= SPEED;
    }

    public void goLeft() {
        vx -= SPEED;
    }

    public void goRight() {
        vx += SPEED;
    }

    public void stopUp() {
        vy -= SPEED;
    }

    public void stopDown() {
        vy += SPEED;
    }

    public void stopLeft() {
        vx += SPEED;
    }

    public void stopRight() {
        vx -= SPEED;
    }

}
