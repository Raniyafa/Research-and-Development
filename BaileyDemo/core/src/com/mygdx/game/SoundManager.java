package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    //SoundManager class

    public static Music background;
    public static Sound button;

    public static void create() {
        //Add backgroundmusic.mp3 as the Background Music
        background = Gdx.audio.newMusic(Gdx.files.internal("Music/backgroundmusic.mp3"));

        //Add buttonsound.mp3 as the Button Sound(Sound Effect)
        button = Gdx.audio.newSound(Gdx.files.internal("Music/buttonsound.mp3"));
    }

    public static void dispose(){
        background.dispose();
        button.dispose();
    }
}