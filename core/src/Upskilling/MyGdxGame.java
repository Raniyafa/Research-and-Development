package Upskilling;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MyGdxGame extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;

	float circleX = 200;
	float circleY = 100;

	float xSpeed = 2;
	float ySpeed = 1;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Override
	public void render () {
		circleX = Gdx.input.getX();
		circleY = -Gdx.input.getY() + 480.0f;

		Gdx.app.log("#INFO", "y = "+Gdx.input.getY());
		Gdx.app.log("#INFO", "circle y = "+circleY);

		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.circle(circleX, circleY, 35);
		shapeRenderer.end();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}
}