package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by st on 8/14/14.
 */
public class TiledSprite extends Sprite {

    public TiledSprite(TextureRegion region) {
        super(region);
    }

    public TiledSprite(TextureRegion region, float width, float height) {
        super(region);
        setSize(width, height);
    }

    public void draw(Batch batch, int offsetX, int offsetY) {
        offsetX = offsetX % getRegionWidth() - getRegionWidth();
        offsetY = offsetY % getRegionHeight() - getRegionHeight();
        for (int i = offsetY; i < getHeight(); i += getRegionHeight()) {
            for (int j = offsetX; j < getWidth(); j += getRegionWidth()) {
                batch.draw(getTexture(), j, i, getRegionWidth(), getRegionHeight(),
                        getU(), getV(), getU2(), getV2());
            }
        }
    }
}
