package co.sugarware.colorflud;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class GridUtil {

    private static Vector2Pool vector2Pool = new Vector2Pool();

    public static boolean isSolved(TileColor[][] grid){
        TileColor color = grid[0][0];
        for (TileColor[] row : grid) {
            for (TileColor curColor : row) {
                if (curColor == TileColor.NONE) continue;
                if (curColor != color) return false;
            }
        }
        return true;
    }

    public static TileColor[][] copyGrid(TileColor[][] grid){
        TileColor[][] tempGrid = new TileColor[grid.length][grid.length];
        for(int row = 0; row < grid.length; row++){
            System.arraycopy(grid[row], 0, tempGrid[row], 0, grid.length);
        }
        return tempGrid;
    }

    public static int fillStep(TileColor[][] grid, Queue<Vector2> nodeQueue, TileColor targetColor, TileColor replacementColor){
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

    public static int fill(int row, int col, TileColor targetColor, TileColor replacementColor, TileColor[][] grid){
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

    public static int[] calculateMoves(TileColor[][] grid){
        TileColor[][] tempGrid =  GridUtil.copyGrid(grid);

        int[] moves = new int[TileColor.playableValues().length];

        while(!GridUtil.isSolved(tempGrid)){
            int bestColorIndex = -1;
            int bestCount = -1;

            for(int i = 0; i < TileColor.playableValues().length; i++){
                TileColor[][] innerTempGrid = GridUtil.copyGrid(tempGrid);
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


}