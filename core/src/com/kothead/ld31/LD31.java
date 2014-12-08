package com.kothead.ld31;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.data.MusicCache;
import com.kothead.ld31.data.SkinCache;
import com.kothead.ld31.data.SoundCache;
import com.kothead.ld31.screen.GameOverScreen;
import com.kothead.ld31.screen.GameScreen;
import com.kothead.ld31.screen.StartScreen;

public class LD31 extends Game {

	@Override
	public void create () {
		ImageCache.load();
		SoundCache.load();
		SkinCache.load();
		setStartScreen();
		MusicCache.play(MusicCache.GLOOM);
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

	public void setStartScreen() {
		setScreen(new StartScreen(this));
	}

	public void setGameScreen(long seed) {
		setScreen(new GameScreen(this, seed));
	}

	public void setGameOverScreen(boolean success) {
		setScreen(new GameOverScreen(this, success));
	}
}
