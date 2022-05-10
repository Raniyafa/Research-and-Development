package Upskilling;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class SoundTest extends ApplicationAdapter {

    ShapeRenderer shapeRenderer;
    Music music;
    Sound sound;
    float circleX;
    float circleY;
    float circleRadius;

    public void create () {
        shapeRenderer = new ShapeRenderer();
       // music = Gdx.audio.newMusic(Gdx.files.internal("sound.mp4"));
        sound = Gdx.audio.newSound(Gdx.files.internal("shipshoot.wav"));
        circleX = Gdx.graphics.getWidth() / 2;
        circleY = Gdx.graphics.getHeight() / 2;
        circleRadius = Gdx.graphics.getHeight() / 3;

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (Vector2.dst(x, y, circleX, circleY) < circleRadius) {
                    sound.play();
                }
                return true;
            }
        });

    //    music.setLooping(true);
      //  music.play();
    }

    public void render () {
        Gdx.gl.glClearColor(.125f, .125f, .125f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.circle(circleX, circleY, circleRadius);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        music.dispose();
        sound.dispose();
        shapeRenderer.dispose();
    }
}