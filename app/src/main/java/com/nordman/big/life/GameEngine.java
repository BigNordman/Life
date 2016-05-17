package com.nordman.big.life;

import android.content.Context;
import android.util.Log;

/**
 * Created by s_vershinin on 17.05.2016.
 */
public class GameEngine {
    public static final int CELL_SIZE = 36;
    public static final int WIDTH = 648 / CELL_SIZE;
    public static final int HEIGHT = 720 / CELL_SIZE;
    public static final int ALIVE = 1;
    public static final int DEAD = 0;

    private Context context;
    private static final int[][] gameGrid = new int[HEIGHT][WIDTH];

    public GameEngine(Context context) {
        this.context = context;
        resetGrid();
    }

    public static int[][] getGrid() {
        return gameGrid;
    }

    public void resetGrid() {
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                gameGrid[h][w] = DEAD;
            }
        }
    }

    public void changeState(float x, float y){
        int h = (int)(y / CELL_SIZE );
        int w = (int)(x / CELL_SIZE );

        if (h >= 0 && w >=0 && h < gameGrid.length && w < gameGrid[0].length){
            if (gameGrid[h][w] == ALIVE) gameGrid[h][w] = DEAD;
            else gameGrid[h][w] = ALIVE;
        }
    }

    public void generateNextGenerationClassic() {
        int neighbours;
        int[][] nextGenerationGrid = new int[HEIGHT][WIDTH];

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                neighbours = calculateNeighbours(h, w);
                switch(gameGrid[h][w]){

                    case ALIVE:
                        if (neighbours < 2 || neighbours > 3)
                            nextGenerationGrid[h][w] = DEAD;
                        break;
                    case DEAD:
                        if (neighbours == 3)
                            nextGenerationGrid[h][w] = ALIVE;
                        break;
                }
            }
        }
        copyGrid(nextGenerationGrid, gameGrid);
    }

    public void generateNextGenerationFunny() {
        int neighbours;
        int[][] nextGenerationGrid = new int[HEIGHT][WIDTH];

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                neighbours = calculateNeighbours(h, w);
                switch(gameGrid[h][w]){

                    case ALIVE:
                        nextGenerationGrid[h][w] = DEAD;
                        break;
                    case DEAD:
                        if (neighbours >= 2)
                            nextGenerationGrid[h][w] = ALIVE;
                        break;
                }
            }
        }
        copyGrid(nextGenerationGrid, gameGrid);
    }

    // цикличное поле
    private int calculateNeighbours(int y, int x) {
        int total =  0;
        for (int h = -1; h <= 1; h++) {
            for (int w = -1; w <= 1; w++) {
                //if (h != 0 || w != 0) { // не считаем соседом саму клетку
                    if (gameGrid[(HEIGHT + (y + h)) % HEIGHT][(WIDTH + (x + w)) % WIDTH] == ALIVE) {
                        total++;
                    }
                //}
            }
        }
        //Log.d("LOG","["+y+"]["+x+"] = " + total);
        return total;
    }

    private void copyGrid(int[][] source, int[][] destination) {
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                destination[h][w] = source[h][w];
            }
        }
    }

}
