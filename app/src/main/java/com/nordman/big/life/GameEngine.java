package com.nordman.big.life;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by s_vershinin on 17.05.2016.
 */
public class GameEngine {
    public static int CELL_SIZE = 0;
    public static int WIDTH;
    public static int HEIGHT;
    public static final int ALIVE = 1;
    public static final int DEAD = 0;

    private Context context;
    private static int[][] gameGrid;
    private static DBHelper dbHelper;

    public GameEngine(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public static void setGridArray() {
        gameGrid = new int[HEIGHT][WIDTH];
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

    public void generateNextGeneration() {
        int neighbours;
        int[][] nextGenerationGrid = new int[HEIGHT][WIDTH];

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                neighbours = calculateNeighbours(h, w);
                switch(gameGrid[h][w]){

                    case ALIVE:
                        if (neighbours < 2 || neighbours > 3)
                            nextGenerationGrid[h][w] = DEAD;
                        else
                            nextGenerationGrid[h][w] = ALIVE;
                        break;
                    case DEAD:
                        if (neighbours == 3)
                            nextGenerationGrid[h][w] = ALIVE;
                        else
                            nextGenerationGrid[h][w] = DEAD;
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
                if (h != 0 || w != 0) { // не считаем соседом саму клетку
                    if (gameGrid[(HEIGHT + (y + h)) % HEIGHT][(WIDTH + (x + w)) % WIDTH] == ALIVE) {
                        total++;
                    }
                }
            }
        }
        return total;
    }

    private void copyGrid(int[][] source, int[][] destination) {
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                destination[h][w] = source[h][w];
            }
        }
    }

    public void randomGrid() {
        // битовые операции
        long zeroBits;

        for (int h = 0; h < HEIGHT; h++) {
            zeroBits = (long) Math.pow(2, WIDTH);

            Random r = new Random();
            r.nextDouble();
            long randomLong = (long)(r.nextDouble()*((long) Math.pow(2, WIDTH+1)));

            for (int w = 0; w < WIDTH; w++) {
                if ((randomLong & zeroBits)==0) gameGrid[h][w] = DEAD;
                else gameGrid[h][w] = ALIVE;
                zeroBits = zeroBits >> 1;
            }
        }
    }

    public void saveGrid(String name) {
        ContentValues cvCombination = new ContentValues();
        ContentValues cvMatrix = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String rowString;

        cvCombination.put("name", name);
        long combinationID = db.insert("combination", null, cvCombination);
        //Log.d("LOG", "...combination inserted, ID = " + combinationID);

        for (int h = 0; h < HEIGHT; h++) {
            rowString = "";
            for (int w = 0; w < WIDTH; w++) {
                rowString += String.valueOf(gameGrid[h][w]) ;
            }
            cvMatrix.put("combination",combinationID);
            cvMatrix.put("rownumber",h);
            cvMatrix.put("rowstring",rowString);
            long rowID = db.insert("matrix", null, cvMatrix);
            //Log.d("LOG", "...matrix row inserted, ID = " + rowID + " value = " + rowString);
        }
    }

    public void loadGrid(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "combination = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };
        Cursor c = db.query("matrix", null, selection, selectionArgs, null, null, null);

        resetGrid();
        if (c.moveToFirst()) {
            int rowNumberColIndex = c.getColumnIndex("rownumber");
            int rowStringColIndex = c.getColumnIndex("rowstring");

            do {
                //Log.d("LOG","row[" + c.getInt(rowNumberColIndex) + "] = " + c.getString(rowStringColIndex));
                String row = c.getString(rowStringColIndex);
                for (int i = 0; i < row.length(); i++){
                    gameGrid[c.getInt(rowNumberColIndex)][i] = Integer.parseInt(row.substring(i,i+1));
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    public static ArrayList<GridHeader> getSavedGridList () {
        ArrayList<GridHeader> result = new ArrayList<GridHeader>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("combination", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");

            do {
                GridHeader item = new GridHeader(c.getInt(idColIndex),c.getString(nameColIndex));
                result.add(item);
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("LOG", "...onCreate database...");
            // создаем таблицы
            db.execSQL("create table combination ("
                    + "id integer primary key autoincrement,"
                    + "name text" + ");");

            db.execSQL("create table matrix ("
                    + "id integer primary key autoincrement,"
                    + "combination integer,"
                    + "rownumber integer,"
                    + "rowstring text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
