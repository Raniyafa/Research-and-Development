package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    public static Music backgroundmusic;
    public static Sound buttonsound;

    public static void create() {
        backgroundmusic = Gdx.audio.newMusic(Gdx.files.internal("Music/backgroundmusic.mp3"));
        buttonsound = Gdx.audio.newSound(Gdx.files.internal("Music/buttonsound.mp3"));
    }

    public static void dispose(){
        backgroundmusic.dispose();
        buttonsound.dispose();
    }}