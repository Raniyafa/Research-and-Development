package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    public static Music background;
    public static Sound button;

    public static void create() {
        background = Gdx.audio.newMusic(Gdx.files.internal("Music/background.mp3"));
        button = Gdx.audio.newSound(Gdx.files.internal("Music/buttonsound.mp3"));
    }

    public static void dispose(){
        background.dispose();
        button.dispose();
    }
}
