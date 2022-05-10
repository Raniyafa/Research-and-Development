package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.mygdx.game.MultipleScenes;

public class DesktopLauncher {
	public static void main (String[] arg) {
		CommonWebSockets.initiate();

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(360, 640);
		config.useVsync(true);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);

		new Lwjgl3Application(new MultipleScenes(), config);
	}
}