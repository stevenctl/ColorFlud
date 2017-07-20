package co.sugarware.colorflud;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GdxGame extends ApplicationAdapter implements InputProcessor {

	private GameStateManager gameStateManager;
	
	@Override
	public void create () {
		gameStateManager = new GameStateManager("4", "60000");
		Gdx.input.setInputProcessor(this);

	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		gameStateManager.update(dt);
		gameStateManager.draw(dt);
	}
	
	@Override
	public void dispose () {
		gameStateManager.dispose();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		gameStateManager.touch(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	//These are unused

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
