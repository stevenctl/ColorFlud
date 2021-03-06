package co.sugarware.colorflud;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

public class GameStateManager{

    private GdxGame game;
    private GameState currentState;
    private static final GameStateName DEFAULT_GAME_STATE = GameStateName.MENU_STATE;


    public GameStateManager(GdxGame game){
        this.game = game;
        setGameState(DEFAULT_GAME_STATE);
    }

    public GameStateManager(GdxGame game, String... args){
        this.game = game;
        setGameState(DEFAULT_GAME_STATE, args);
    }

    public synchronized void setGameState(GameStateName gameStateName, String... args){
        if(currentState !=null) {
            currentState.dispose();
        }
        currentState = getGameState(gameStateName, args);
    }

    public synchronized void update(float deltaTime){
        currentState.update(deltaTime);
    }

    public synchronized void draw(float deltaTime){
        currentState.draw(deltaTime);
    }

    public void touch(int x, int y){
        currentState.touch(x, y);
    }

    public void dispose(){
        currentState.dispose();
    }

    private GameState getGameState(GameStateName gameStateName, String... args){
        switch (gameStateName){
            case MENU_STATE:
                return new MenuState(this);
            case PLAY_STATE:
                return new PlayGameState(this, new Integer(args[0]), new Integer(args[1]));
        }

        throw new IllegalArgumentException(gameStateName.name() + " is not a valid GameStateType");
    }

    public enum GameStateName{
        PLAY_STATE, MENU_STATE
    }

    public GoogleResolver getGoogleResolver(){
        return game.getGoogleResolver();
    }

}
