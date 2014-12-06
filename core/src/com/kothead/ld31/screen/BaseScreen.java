package com.kothead.ld31.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.Configuration;

/**
 * Created by st on 10/24/14.
 */
public abstract class BaseScreen extends ScreenAdapter {

    private LD31 game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private float worldWidth, worldHeight;

    public BaseScreen(LD31 game) {
        this.game = game;
        camera = new OrthographicCamera();
        calcWorldSize();
        viewport = new StretchViewport(worldWidth, worldHeight, camera);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.setWorldSize(getWorldWidth(), getWorldHeight());
        viewport.update(width, height, true);
    }

    public LD31 getGame() {
        return game;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    private void calcWorldSize() {
        worldWidth = Configuration.WORLD_WIDTH;
        worldHeight = Configuration.WORLD_HEIGHT;
    }
}
