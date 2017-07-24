package co.sugarware.colorflud;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;

import java.util.Arrays;


public class PlayGameState extends GameState {


    private int gridSize;
    private int timeLimit;
    private boolean paused;

    private TileColor[][] grid;
    private int[] moves;

    private boolean filling;
    private Queue<Vector2> nodeQueue;
    private TileColor targetColor;
    private TileColor replacementColor;

    private EnhancedShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private BitmapFont font;

    public PlayGameState(GameStateManager gameStateManager, int gridSize, int timeLimit){
        super(gameStateManager);
        this.gridSize = gridSize;
        this.timeLimit = timeLimit;
        paused = false;

        shapeRenderer = new EnhancedShapeRenderer();
        spriteBatch = new SpriteBatch();

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 72;
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose();

        filling = false;
        nodeQueue = new Queue<Vector2>();

        grid = new TileColor[gridSize][gridSize];

        for(int row = 0; row < gridSize; row++){
            for(int col = 0; col < gridSize; col++){
                grid[row][col] = TileColor.values()[(int)(Math.random() * TileColor.values().length)];
            }
        }

        moves = GridUtil.calculateMoves(grid);
    }



    public void update(float deltaTime){
        if(!paused){
            timeLimit -= deltaTime * 1000;

            if(filling){
                if(GridUtil.fillStep(grid, nodeQueue, targetColor, replacementColor) == 0){
                    filling = false;
                }
            }

            if(GridUtil.isSolved(grid)){
                gameStateManager.setGameState(GameStateManager.GameStateName.PLAY_STATE, String.valueOf(gridSize + 2), String.valueOf(100 * (int) Math.pow(gridSize, 2)));
            }
        }


    }

    public void draw(float deltaTime){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int tileSize = width / grid.length;


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int row = 0; row < grid.length; row++){
            for(int col = 0; col < grid.length; col++){

                boolean n,s,e,w;
                n = (row > 0 && grid[row][col] == grid[row - 1][col]);
                s = (row < grid.length - 1 && grid[row][col] == grid[row + 1][col]);
                w = (col > 0 && grid[row][col] == grid[row][col - 1]);
                e = (col < grid.length - 1 && grid[row][col] == grid[row][col + 1]);

                int tw = tileSize - (w ? 0 : 1) - (e ? 0 : 1);
                int th = tileSize - (n ? 0 : 1) - (s ? 0 : 1);
                shapeRenderer.setColor(grid[row][col].getColor());
                shapeRenderer.rect(
                        tileSize * col + (w ? 0 : 1),
                        height - (tileSize * row  - (s ? 0 : 1)) - tileSize,
                        tw,
                        th
                );
            }
        }

        for(int i = 0; i < TileColor.playableValues().length; i++){
            TileColor col = TileColor.playableValues()[i];
            shapeRenderer.setColor(col.getColor());
            shapeRenderer.roundedRect(i * (width / TileColor.playableValues().length), 0, width / TileColor.playableValues().length, width / TileColor.playableValues().length, 10);
        }

        shapeRenderer.end();

        spriteBatch.begin();

        font.draw(spriteBatch, String.valueOf(timeLimit / 1000L), 10, font.getLineHeight() +  width / TileColor.playableValues().length);

        spriteBatch.end();

    }



    public void touch(int x, int y){
        int width = Gdx.graphics.getWidth();

        if(!filling && y < width / TileColor.playableValues().length){
            int index = x / (width / TileColor.playableValues().length);
            moves[index]--;
            animatedFill(TileColor.playableValues()[index]);
        }
    }

    private void animatedFill(TileColor replacementColor){
        TileColor targetColor = grid[0][0];
        if(targetColor == replacementColor){
            return;
        }

        nodeQueue.addLast(new Vector2(0, 0));

        this.replacementColor = replacementColor;
        this.targetColor = targetColor;
        filling = true;
    }





    public void dispose(){
        shapeRenderer.dispose();
        font.dispose();
    }
}
