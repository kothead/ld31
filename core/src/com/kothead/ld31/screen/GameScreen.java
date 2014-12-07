package com.kothead.ld31.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.data.Labyrinth;
import com.kothead.ld31.view.Lightmap;
import com.kothead.ld31.view.Player;
import com.kothead.ld31.view.TiledSprite;
import com.kothead.ld31.view.Wall;

/**
 * Created by st on 12/7/14.
 */
public class GameScreen extends BaseScreen {

    public static final int GRID_SIZE = 36;
    public static final int GRID_WIDTH = 16;
    public static final int GRID_HEIGHT = 10;

    private static final String TEXTURE_FLOOR = "floor";

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private TiledSprite background;
    private Player player;
    private Wall wall;
    private Labyrinth labyrinth;
    private Lightmap lightmap;

    public GameScreen(LD31 game) {
        super(game);
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        background = new TiledSprite(ImageCache.getTexture(TEXTURE_FLOOR),
                getWorldWidth(), getWorldHeight());

        player = new Player();
        player.setPosition(0, 0);
        wall = new Wall();

        labyrinth = new Labyrinth(GRID_WIDTH, GRID_HEIGHT);
        labyrinth.moveTo(0, 0);
        lightmap = new Lightmap(labyrinth);

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

        int oldX = (int) (player.getX() / GRID_SIZE);
        int oldY = (int) (player.getY() / GRID_SIZE);
        player.draw(delta, shapes);
        int curX = (int) (player.getX() / GRID_SIZE);
        int curY = (int) (player.getY() / GRID_SIZE);
        if ((curX != oldX || curY != oldY)
                && curX >= 0 && curX < GRID_WIDTH
                && curY >= 0 && curY < GRID_HEIGHT) labyrinth.moveTo(curX, curY);

        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                wall.setPosition(j, i);
                if (labyrinth.hasVerticalWall(j, i)) {
                    wall.setVertical();
                    wall.draw(delta, shapes);
                }

                if (labyrinth.hasHorizontalWall(j, i)) {
                    wall.setHorizontal();
                    wall.draw(delta, shapes);
                }
            }
        }

        lightmap.setLightPosition(curX, curY);
        lightmap.draw(shapes);

        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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
