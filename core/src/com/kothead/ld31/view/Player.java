package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.model.LabyrinthBacktrack;
import com.kothead.ld31.model.LabyrinthController;
import com.kothead.ld31.screen.GameScreen;

/**
 * Created by st on 12/7/14.
 */
public class Player extends Walker {

    public static final int SIZE = 10;

    private static final float SPEED = 50f;
    private static final int START_LIFE = 1;
    private static final int MAX_LIFE = 3;

    private int exp;
    private int kill;
    private int level;
    private int life = START_LIFE;

    public Player() {
        setSize(SIZE, SIZE);
        setSpeedValue(SPEED);
    }

    public void updateLabyrinth(float delta, LabyrinthController controller) {
        int oldX = getGridX();
        int oldY = getGridY();
        int curX = getGridX(getX() + delta * getVx());
        int curY = getGridY(getY() + delta * getVy());

        if ((curX != oldX || curY != oldY)
                && curX >= 0 && curX < Configuration.LABYRINTH_WIDTH
                && curY >= 0 && curY < Configuration.LABYRINTH_HEIGHT) {
            controller.moveTo(curX, curY);
        }
    }

    public Bullet shoot(Direction direction) {
        Bullet bullet = new Bullet(level, direction);
        bullet.setX(getX() + getWidth() / 2 - bullet.getWidth() / 2);
        bullet.setY(getY() + getHeight() / 2 - bullet.getHeight() / 2);
        return bullet;
    }

    public int getKillCount() {
        return kill;
    }

    public void incKillCount() {
        kill++;
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void decLife() {
        life--;
    }

    public void incLife() {
        life++;
    }

    @Override
    public void draw(float delta, ShapeRenderer renderer) {
        super.draw(delta, renderer);
        renderer.setColor(Color.WHITE);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

}
