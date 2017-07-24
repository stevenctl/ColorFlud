package co.sugarware.colorflud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class MenuState extends GameState {


    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private TileColor[][] grid;

    public MenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        spriteBatch = new SpriteBatch();

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = (int)(.15f * Gdx.graphics.getWidth());
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose();


        gameStateManager.getGoogleResolver().signIn();
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(float deltaTime) {
        spriteBatch.begin();

        font.draw(spriteBatch, "ColorFlud", 0, Gdx.graphics.getHeight() - font.getLineHeight() - 10, Gdx.graphics.getWidth() - 20, Align.center, false);

        spriteBatch.end();
    }

    @Override
    public void touch(int x, int y) {
        gameStateManager.setGameState(GameStateManager.GameStateName.PLAY_STATE, "10", "20");
    }

    @Override
    public void dispose() {

    }
}
