package com.kothead.ld31.screen;

import static com.kothead.ld31.data.Configuration.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.Direction;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.model.BacktrackController;
import com.kothead.ld31.model.LabyrinthBacktrack;
import com.kothead.ld31.model.LabyrinthController;
import com.kothead.ld31.view.*;

import java.util.Iterator;

/**
 * Created by st on 12/7/14.
 */
public class GameScreen extends BaseScreen {

    private static final String TEXTURE_FLOOR = "floor";
    private static final int MAX_ENEMIES = 20;

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private TiledSprite background;
    private Player player;
    private Wall wall;
    private BacktrackController controller;
    private Lightmap lightmap;
    private Array<Bullet> bullets;
    private Array<Enemy> enemies;

    public GameScreen(LD31 game) {
        super(game);
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        background = new TiledSprite(ImageCache.getTexture(TEXTURE_FLOOR),
                getWorldWidth(), getWorldHeight());

        player = new Player();
        float position = (Configuration.LABYRINTH_CELL_SIZE - Player.SIZE) / 2f;
        player.setPosition(position, position);
        wall = new Wall();

        controller = new BacktrackController();
        bullets = new Array<Bullet>();
        enemies = new Array<Enemy>();
        lightmap = new Lightmap(controller);

        recreateThingsOnMap();

        Gdx.input.setInputProcessor(new Processor());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        batch.setProjectionMatrix(getCamera().combined);
        shapes.setProjectionMatrix(getCamera().combined);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        background.draw(batch, 0, 0);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        player.setNotBlocked();
        for (int i = 0; i < Configuration.LABYRINTH_HEIGHT; i++) {
            for (int j = 0; j < Configuration.LABYRINTH_WIDTH; j++) {
                if (controller.hasWallBottom(j, i)) {
                    wall.setPosition(true, j, i);
                    player.processWall(delta, wall);
                    wall.draw(delta, shapes);
                    processBullets(wall, delta);
                }

                if (controller.hasWallRight(j, i)) {
                    wall.setPosition(false, j, i);
                    player.processWall(delta, wall);
                    wall.draw(delta, shapes);
                    processBullets(wall, delta);
                }
            }
        }

        for (Bullet bullet: bullets) {
            bullet.draw(shapes, delta);
        }

        for (Enemy enemy: enemies) {
            enemy.process();
            enemy.draw(delta, shapes);
        }

        player.updateLabyrinth(delta, controller);
        if (controller.isUpdated()) {
            recreateThingsOnMap();
        }
        player.draw(delta, shapes);

        lightmap.setLightPosition(player.getGridX(), player.getGridY());
        lightmap.draw(shapes);

        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void processBullets(Wall wall, float delta) {
        Iterator<Bullet> iterBullet = bullets.iterator();
        while (iterBullet.hasNext()) {
            Bullet bullet = iterBullet.next();
            if (bullet.collided(wall, delta)) {
                iterBullet.remove();
                continue;
            }

            for (int i = 0; i < enemies.size; i++) {
                Enemy enemy = enemies.get(i);
                if (enemy.hit(bullet)) {
                    iterBullet.remove();
                    break;
                }
            }
        }

        Iterator<Enemy> iterEnemy = enemies.iterator();
        while (iterEnemy.hasNext()) {
            Enemy enemy = iterEnemy.next();
            if (enemy.isDead()) {
                iterEnemy.remove();
            }
        }
    }

    private void recreateThingsOnMap() {
        enemies.clear();
        int count = (int) (Math.random() * MAX_ENEMIES);
        for (int i = 0; i < count; i++) {
            Enemy enemy = new Enemy(controller, player);
            float x = (int) (Math.random() * LABYRINTH_WIDTH) * LABYRINTH_CELL_SIZE
                    + LABYRINTH_CELL_SIZE / 2 - enemy.getWidth() / 2;
            float y = (int) (Math.random() * LABYRINTH_HEIGHT) * LABYRINTH_CELL_SIZE
                    + LABYRINTH_CELL_SIZE / 2 - enemy.getHeight() / 2;
            enemy.setPosition(x, y);
            enemy.setLevel(controller.getLevel());
            enemies.add(enemy);
        }
    }

    private class Processor extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.stopUp();
                    return true;

                case Input.Keys.A:
                    player.stopLeft();
                    return true;

                case Input.Keys.S:
                    player.stopDown();
                    return true;

                case Input.Keys.D:
                    player.stopRight();
                    return true;

                case Input.Keys.UP:
                    bullets.add(player.shoot(Direction.TOP));
                    return true;

                case Input.Keys.RIGHT:
                    bullets.add(player.shoot(Direction.RIGHT));
                    return true;

                case Input.Keys.DOWN:
                    bullets.add(player.shoot(Direction.BOTTOM));
                    return true;

                case Input.Keys.LEFT:
                    bullets.add(player.shoot(Direction.LEFT));
                    return true;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.goUp();
                    return true;

                case Input.Keys.A:
                    player.goLeft();
                    return true;

                case Input.Keys.S:
                    player.goDown();
                    return true;

                case Input.Keys.D:
                    player.goRight();
                    return true;
            }

            return super.keyDown(keycode);
        }
    }
}
