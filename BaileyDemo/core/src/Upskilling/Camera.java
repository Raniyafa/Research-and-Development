package Upskilling;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Camera extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;

    float bottomLeftX = 0;
    float bottomLeftY = 0;
    float rectWidth;
    float rectHeight;
    float moveSpeed = 100;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        rectWidth = Gdx.graphics.getWidth() / 2;
        rectHeight = Gdx.graphics.getHeight() / 2;
    }

    @Override
    public void render() {

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            bottomLeftY -= moveSpeed * Gdx.graphics.getDeltaTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            bottomLeftY += moveSpeed * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            bottomLeftX += moveSpeed * Gdx.graphics.getDeltaTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bottomLeftX -= moveSpeed * Gdx.graphics.getDeltaTime();
        }

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(bottomLeftX, bottomLeftY, rectWidth, rectHeight);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(bottomLeftX + rectWidth, bottomLeftY, rectWidth, rectHeight);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.rect(bottomLeftX + rectWidth, bottomLeftY + rectHeight, rectWidth, rectHeight);
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.rect(bottomLeftX, bottomLeftY + rectHeight, rectWidth, rectHeight);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}