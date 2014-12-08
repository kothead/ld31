package com.kothead.ld31.view;

import static com.kothead.ld31.data.Configuration.LABYRINTH_CELL_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kothead.ld31.data.ImageCache;

import java.awt.*;

/**
 * Created by st on 12/9/14.
 */
public class Portal extends Sprite {

    private static final float DURATION = 0.05f;

    private static final int FRAMES_COUNT = 4;
    private static final String TEXTURE_NAME = "portal";

    private float time;
    private Animation animation;

    public Portal() {
        super(ImageCache.getTexture(TEXTURE_NAME));
        animation = new Animation(DURATION,
                ImageCache.getFrame(TEXTURE_NAME, 1),
                ImageCache.getFrame(TEXTURE_NAME, 2),
                ImageCache.getFrame(TEXTURE_NAME, 3),
                ImageCache.getFrame(TEXTURE_NAME, 4));
        setRegion(animation.getKeyFrame(0, true));
    }

    @Override
    public void draw(Batch batch) {
        time += Gdx.graphics.getDeltaTime();
        setRegion(animation.getKeyFrame(time, true));
        super.draw(batch);
    }

    public void setPositionGrid(int x, int y) {
        setPosition(x * LABYRINTH_CELL_SIZE, y * LABYRINTH_CELL_SIZE);
    }
}
