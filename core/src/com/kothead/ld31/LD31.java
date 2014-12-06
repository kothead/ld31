package com.kothead.ld31;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.screen.GameScreen;

public class LD31 extends Game {

	@Override
	public void create () {
		ImageCache.load();
		setGameScreen();
	}

	@Override
	public void setScreen(Screen screen) {
		Screen old = getScreen();
		super.setScreen(screen);
		if (old != null) old.dispose();
	}

	@Override
	public void render () {
		super.render();
	}

	public void setGameScreen() {
		setScreen(new GameScreen(this));
	}
}
