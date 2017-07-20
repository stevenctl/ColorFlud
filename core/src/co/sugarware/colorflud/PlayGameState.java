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
    private int initialTimeLimit;
    private int timeLimit;
    private TileColor[][] grid;
    private boolean paused;

    private int[] moves;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private BitmapFont font;

    public PlayGameState(GameStateManager gameStateManager, int gridSize, int timeLimit){
        super(gameStateManager);
        this.gridSize = gridSize;
        this.initialTimeLimit = this.timeLimit = timeLimit;
        paused = false;
        vector2Pool = new Vector2Pool();

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 72;
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose();


        grid = new TileColor[gridSize][gridSize];

        for(int row = 0; row < gridSize; row++){
            for(int col = 0; col < gridSize; col++){
                grid[row][col] = TileColor.values()[(int)(Math.random() * TileColor.values().length)];
            }
        }

        long st = System.currentTimeMillis();
        moves = calculateMoves(grid);
        long tt = System.currentTimeMillis() - st;



    }



    public void update(float deltaTime){
        if(!paused){
            timeLimit -= deltaTime * 1000;

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
                shapeRenderer.rect(tileSize * col + 1, height - (tileSize * row  - 1) - tileSize, tileSize - 2, tileSize - 2);
            }
        }

        for(int i = 0; i < TileColor.values().length; i++){
            TileColor col = TileColor.values()[i];
            shapeRenderer.setColor(col.getColor());
            shapeRenderer.rect(i * (width / TileColor.values().length), 0, width / TileColor.values().length, width / TileColor.values().length);
        }

        shapeRenderer.end();

        spriteBatch.begin();

        font.draw(spriteBatch, String.valueOf(timeLimit / 1000l), 10, font.getLineHeight() +  width / TileColor.values().length);

        spriteBatch.end();

    }



    public void touch(int x, int y){
        int width = Gdx.graphics.getWidth();

        if(y < width / TileColor.values().length){
            int index = x / (width / TileColor.values().length);
            moves[index]--;
            fill(0, 0, grid[0][0], TileColor.values()[index], grid);
        }
    }



    private int fill(int row, int col, TileColor targetColor, TileColor replacementColor, TileColor[][] grid){
        if(targetColor == replacementColor || grid[row][col] != targetColor){
            return 0;
        }
        int n = 0;
        Queue<Vector2> nodeQueue = new Queue<Vector2>();
        nodeQueue.addLast(vector2Pool.obtain(col, row));
        while(nodeQueue.size > 0){
            int curSize = nodeQueue.size;
            for(int i = 0; i < curSize; i++){
                Vector2 node = nodeQueue.removeFirst();
                i--;
                curSize--;
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
        }
        return n;
    }

    private int[] calculateMoves(TileColor[][] grid){
        TileColor[][] tempGrid = copyGrid(grid);

        int[] moves = new int[TileColor.values().length];

        while(!isSolved(tempGrid)){
            int bestColorIndex = -1;
            int bestCount = -1;

            for(int i = 0; i < TileColor.values().length; i++){
                TileColor[][] innerTempGrid = copyGrid(tempGrid);
                TileColor targetColor = innerTempGrid[0][0];
                TileColor replacementColor = TileColor.values()[i];
                fill(0, 0, targetColor, replacementColor, innerTempGrid);
                int n =   fill(0, 0, replacementColor, targetColor, innerTempGrid);
                if(n > bestCount){
                    bestCount = n;
                    bestColorIndex = i;
                }
            }
            fill(0, 0, tempGrid[0][0], TileColor.values()[bestColorIndex], tempGrid);
            moves[bestColorIndex]++;
        }

        return moves;

    }


    private boolean isSolved(TileColor[][] grid){
        TileColor color = grid[0][0];
        for (TileColor[] row : grid) {
            for (TileColor curColor : row) {
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
