package com.kothead.ld31.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.view.TiledSprite;

/**
 * Created by st on 12/7/14.
 */
public class GameScreen extends BaseScreen {

    private static final String TEXTURE_FLOOR = "floor";

    private SpriteBatch batch;
    private TiledSprite background;

    public GameScreen(LD31 game) {
        super(game);
        batch = new SpriteBatch();
        background = new TiledSprite(ImageCache.getTexture(TEXTURE_FLOOR),
                getWorldWidth(), getWorldHeight());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        batch.setProjectionMatrix(getCamera().combined);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        background.draw(batch, 0, 0);
        batch.end();
    }
}
