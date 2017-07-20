package co.sugarware.colorflud;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Arrays;

public class PlayGameState extends GameState {

    private int gridSize;
    private int timeLimit;
    private TileColor[][] grid;
    private boolean paused;

    int[] moves;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    BitmapFont font;

    public PlayGameState(GameStateManager gameStateManager, int gridSize, int timeLimit){
        super(gameStateManager);
        this.gridSize = gridSize;
        this.timeLimit = timeLimit;
        paused = false;

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

        System.out.printf("Calculated moves in %s seconds", tt / 1000f);

    }



    public void update(float deltaTime){
        if(!paused){
            timeLimit -= deltaTime;
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

        spriteBatch.begin();;

        font.draw(spriteBatch, String.valueOf(timeLimit), 10, font.getLineHeight() +  width / TileColor.values().length);

        spriteBatch.end();

    }



    public void touch(int x, int y){
        int width = Gdx.graphics.getWidth();

        if(y < width / TileColor.values().length){
            fill(0, 0, grid[0][0], TileColor.values()[x / (width / TileColor.values().length)], grid);
        }
    }



    private int fill(int row, int col, TileColor targetColor, TileColor replacementColor, TileColor[][] grid){
        TileColor nodeColor = grid[row][col];

        if (targetColor == replacementColor){
            return 0;
        }

        if(nodeColor != targetColor){
            return 0;
        }

        int n = 1;
        grid[row][col] = replacementColor;

        //Navigate to other nodes
        if(row > 0){
            n += fill(row - 1, col, targetColor, replacementColor, grid);
        }
        if(row < grid.length - 1){
            n += fill(row + 1, col, targetColor, replacementColor, grid);
        }
        if(col > 0){
            n += fill(row, col - 1, targetColor, replacementColor, grid);
        }
        if(col < grid.length - 1){
            n += fill(row, col + 1, targetColor, replacementColor, grid);
        }

        return n;
    }

    static int bbb = 0;

    private int[] calculateMoves(TileColor[][] grid){
        TileColor[][] tempGrid = copyGrid(grid);

        int[] moves = new int[TileColor.values().length];

        while(!isSolved(tempGrid)){
            int bestColorIndex = -1;
            int bestCount = -1;

            for(int i = 0; i < TileColor.values().length; i++){
                TileColor[][] innerTempGrid = copyGrid(tempGrid);
                bbb += 8;
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
        for(int r = 0; r < grid.length; r++){
            for(int c = 0; c < grid.length; c++){
                if(grid[r][c] != color){
                    return false;
                }
            }
        }
        return true;
    }


    private TileColor[][] copyGrid(TileColor[][] grid){
        TileColor[][] tempGrid = new TileColor[gridSize][gridSize];
        for(int r = 0; r < gridSize; r++){
            for(int c = 0; c < gridSize; c++){
                tempGrid[r][c] = grid[r][c];
            }
        }
        return tempGrid;
    }

    public void dispose(){
        shapeRenderer.dispose();
        font.dispose();
    }
}
