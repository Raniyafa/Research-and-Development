package com.mygdx.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.czyzby.websocket.GwtWebSockets;
import com.mygdx.game.MultipleScenes;


public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
               // return new GwtApplicationConfiguration(true);
                // Fixed size application:
                GwtApplicationConfiguration config = new GwtApplicationConfiguration(360, 640);
                config.padHorizontal = 0;
                config.padVertical = 0;
                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                GwtWebSockets.initiate();
                return new MultipleScenes();
        }
}