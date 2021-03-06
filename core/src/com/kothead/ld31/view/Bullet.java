package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.Direction;

/**
 * Created by st on 12/8/14.
 */
public class Bullet extends Sprite {

    public static final int START_SPEED = 100;
    public static final int START_SIZE = 5;
    public static final int START_DAMAGE = 10;

    public static final int UPGRADE_SPEED = 10;
    public static final int UPGRADE_DAMAGE = 10;

    private int damage;
    private int vx, vy;

    public Bullet(int level, Direction direction) {
        damage = START_DAMAGE + UPGRADE_DAMAGE * level;

        setSize(START_SIZE, START_SIZE);

        int speed = START_SPEED + UPGRADE_SPEED * level;
        switch (direction) {
            case TOP:
                vy = speed;
                break;

            case RIGHT:
                vx = speed;
                break;

            case LEFT:
                vx = -speed;
                break;

            case BOTTOM:
                vy = -speed;
                break;
        }
    }

    public int getDamage() {
        return damage;
    }

    public void draw(ShapeRenderer renderer, float delta) {
        renderer.setColor(1, 1, 0, 1);
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    public boolean collided(Wall wall, float delta) {
        // TODO: move this somewhere

        Rectangle rectBullet = new Rectangle(getX(), getY(), getWidth(), getHeight());
        Rectangle rectWall = new Rectangle(wall.getX(), wall.getY(),
                wall.getWidth(), wall.getHeight());
        Rectangle rectResult = new Rectangle();

        return Intersector.intersectRectangles(rectBullet, rectWall, rectResult);
    }

    public boolean farAway() {
        return getX() < 0 || getX() > Configuration.WORLD_WIDTH
                || getY() < 0 || getY() > Configuration.WORLD_HEIGHT;
    }
}
