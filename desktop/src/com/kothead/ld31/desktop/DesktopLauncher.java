package com.kothead.ld31.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.Configuration;

import java.io.File;
import java.io.FileFilter;

public class DesktopLauncher {

	public static void main (String[] arg) {
		packAssets();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Configuration.WORLD_WIDTH;
		config.height = Configuration.WORLD_HEIGHT;
		new LwjglApplication(new LD31(), config);
	}

	private static void packAssets() {
		File dir = new File("../../images");

		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.edgePadding = true;
		settings.duplicatePadding = true;
		settings.paddingX = 4;
		settings.paddingY = 4;

		for (File childDir: dir.listFiles(filter)) {
			TexturePacker.process(settings, childDir.getPath(), "data", childDir.getName());
		}

	}
}
