package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.data.SoundCache;
import com.kothead.ld31.model.LabyrinthController;

import static com.kothead.ld31.data.Configuration.*;

/**
 * Created by st on 12/7/14.
 */
public class Player extends Walker {

    public static final int SIZE = 10;

    private static final float SPEED = 50f;
    private static final int START_LIFE = 1;
    private static final int MAX_LIFE = 3;

    private int exp;
    private int nextLevelExp = 200;
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
        int curX = getGridX(getX() + getWidth() / 2f + delta * getVx());
        int curY = getGridY(getY() + getHeight() / 2f + delta * getVy());

        if ((curX != oldX || curY != oldY)
                && curX >= 0 && curX < Configuration.LABYRINTH_WIDTH
                && curY >= 0 && curY < Configuration.LABYRINTH_HEIGHT) {
            controller.moveTo(curX, curY);
        }
    }

    public Bullet shoot(Direction direction) {
        SoundCache.play(SoundCache.SOUND_LASER);
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

    public boolean levelUp() {
        if (exp >= nextLevelExp) {
            level++;
            int inc = (int) (nextLevelExp * 0.50);
            nextLevelExp += inc < 300 ? 300 : inc;
            return true;
        }
        return false;
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

    public boolean checkLife(Life life) {
        if (life == null) return false;
        if (life.getGridX() != getGridX()
                || life.getGridY() != getGridY()) return false;
        int offsetX = (int) getX() % LABYRINTH_CELL_SIZE + (int) getWidth() / 2;
        int offsetY = (int) getY() % LABYRINTH_CELL_SIZE + (int) getHeight() / 2;

        // TODO: heart size 20x20
        if (Math.abs(offsetX - LABYRINTH_CELL_SIZE / 2) < 10
                && Math.abs(offsetY - LABYRINTH_CELL_SIZE / 2) < 10) {
            incLife();
            return true;
        }
        return false;
    }

    public boolean isOutThere() {
        return getGridX() < 0 || getGridX() >= LABYRINTH_WIDTH
                || getGridY() < 0 || getGridY() >= LABYRINTH_HEIGHT;
    }

    public int getLifes() {
        return life;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void draw(float delta, ShapeRenderer renderer) {
        super.draw(delta, renderer);
        renderer.setColor(Color.WHITE);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

}
