package co.sugarware.colorflud;

public abstract class GameState {

    protected GameStateManager gameStateManager;

    public GameState(GameStateManager gameStateManager){
        this.gameStateManager = gameStateManager;
    }

    public abstract void update(float deltaTime);

    public abstract void draw(float deltaTime);

    public abstract void touch(int x, int y);

    public abstract void dispose();
}
