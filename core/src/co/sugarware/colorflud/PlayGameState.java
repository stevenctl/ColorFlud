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


    private Vector2Pool vector2Pool;
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
        vector2Pool = new Vector2Pool();

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

        moves = calculateMoves(grid);
    }



    public void update(float deltaTime){
        if(!paused){
            timeLimit -= deltaTime * 1000;

            if(filling){
                if(fillStep(grid, nodeQueue, targetColor, replacementColor) == 0){
                    filling = false;
                }
            }

            if(isSolved(grid)){
                gameStateManager.setGameState(GameStateManager.GameStateName.PLAY_STATE, String.valueOf(gridSize + 2), String.valueOf(100 * (int) Math.pow(gridSize, 2)));
            }
        }


    }

    public void draw(float deltaTime){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int tileSize = width / gridSize;


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int row = 0; row < gridSize; row++){
            for(int col = 0; col < gridSize; col++){
                shapeRenderer.setColor(grid[row][col].getColor());
                shapeRenderer.roundedRect(tileSize * col + 1, height - (tileSize * row  - 1) - tileSize, tileSize - 2, tileSize - 2, 10);
            }
        }

        for(int i = 0; i < TileColor.playableValues().length; i++){
            TileColor col = TileColor.playableValues()[i];
            shapeRenderer.setColor(col.getColor());
            shapeRenderer.roundedRect(i * (width / TileColor.playableValues().length), 0, width / TileColor.playableValues().length, width / TileColor.playableValues().length, 10);
        }

        shapeRenderer.end();

        spriteBatch.begin();

        font.draw(spriteBatch, String.valueOf(timeLimit / 1000l), 10, font.getLineHeight() +  width / TileColor.playableValues().length);

        spriteBatch.end();

    }



    public void touch(int x, int y){
        int width = Gdx.graphics.getWidth();

        if(!filling && y < width / TileColor.playableValues().length){
            int index = x / (width / TileColor.playableValues().length);
            moves[index]--;
           // fill(0, 0, grid[0][0], TileColor.playableValues()[index], grid);
            animatedFill(TileColor.playableValues()[index]);
        }
    }

    private void animatedFill(TileColor replacementColor){
        TileColor targetColor = grid[0][0];
        if(targetColor == replacementColor){
            return;
        }

        nodeQueue.addLast(vector2Pool.obtain(0, 0));

        this.replacementColor = replacementColor;
        this.targetColor = targetColor;
        filling = true;
    }

    private int fillStep(TileColor[][] grid, Queue<Vector2> nodeQueue, TileColor targetColor, TileColor replacementColor){
        int curSize = nodeQueue.size;
        int n = 0;
        for(int i = 0; i < curSize; i++){
            Vector2 node = nodeQueue.removeFirst();

            Vector2 e = vector2Pool.obtain(node);
            Vector2 w = vector2Pool.obtain(node);
            while(e.x < grid.length && grid[(int)e.y][(int)e.x] == targetColor){
                e.x++;
            }
            while(w.x >= 0 && grid[(int)w.y][(int)w.x] == targetColor){
                w.x--;
            }
            for(int x = (int) w.x + 1; x < e.x; x++){
                grid[(int)node.y][x] = replacementColor;
                n++;
                if(node.y > 0 && grid[(int)node.y - 1][x] == targetColor){
                    nodeQueue.addLast(vector2Pool.obtain(x, node.y - 1));
                }
                if(node.y < grid.length - 1 && grid[(int)node.y + 1][x] == targetColor){
                    nodeQueue.addLast(vector2Pool.obtain(x, node.y + 1));
                }
            }
        }
        return n;
    }

    private int fill(int row, int col, TileColor targetColor, TileColor replacementColor, TileColor[][] grid){
        if(targetColor == replacementColor || grid[row][col] != targetColor){
            return 0;
        }
        int n = 0;
        Queue<Vector2> nodeQueue = new Queue<Vector2>();
        nodeQueue.addLast(vector2Pool.obtain(col, row));
        while(nodeQueue.size > 0){
            n+= fillStep(grid, nodeQueue, targetColor, replacementColor);
        }
        return n;
    }

    private int[] calculateMoves(TileColor[][] grid){
        TileColor[][] tempGrid = copyGrid(grid);

        int[] moves = new int[TileColor.playableValues().length];

        while(!isSolved(tempGrid)){
            int bestColorIndex = -1;
            int bestCount = -1;

            for(int i = 0; i < TileColor.playableValues().length; i++){
                TileColor[][] innerTempGrid = copyGrid(tempGrid);
                TileColor targetColor = innerTempGrid[0][0];
                TileColor replacementColor = TileColor.playableValues()[i];
                fill(0, 0, targetColor, replacementColor, innerTempGrid);
                int n =   fill(0, 0, replacementColor, targetColor, innerTempGrid);
                if(n > bestCount){
                    bestCount = n;
                    bestColorIndex = i;
                }
            }
            fill(0, 0, tempGrid[0][0], TileColor.playableValues()[bestColorIndex], tempGrid);
            moves[bestColorIndex]++;
        }

        return moves;

    }


    private boolean isSolved(TileColor[][] grid){
        TileColor color = grid[0][0];
        for (TileColor[] row : grid) {
            for (TileColor curColor : row) {
                if (curColor == TileColor.NONE) continue;
                if (curColor != color) return false;
            }
        }
        return true;
    }


    private TileColor[][] copyGrid(TileColor[][] grid){
        TileColor[][] tempGrid = new TileColor[gridSize][gridSize];
        for(int row = 0; row < gridSize; row++){
            System.arraycopy(grid[row], 0, tempGrid[row], 0, gridSize);
        }
        return tempGrid;
    }

    public void dispose(){
        shapeRenderer.dispose();
        font.dispose();
    }
}
