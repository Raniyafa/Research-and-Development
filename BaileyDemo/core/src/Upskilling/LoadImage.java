package Upskilling;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class LoadImage extends ApplicationAdapter {

    SpriteBatch batch;
    Texture texture;

    public void create () {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("lol.png"));
    }

    public void render () {
        Gdx.gl.glClearColor(.125f, .125f, .125f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (int i = 0; i < 10000; i++) {
            batch.draw(texture, MathUtils.random(Gdx.graphics.getWidth()), MathUtils.random(Gdx.graphics.getHeight()), 50, 50, 0, 1, 1, 0);
        }
        batch.end();

        System.out.println(Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }
}