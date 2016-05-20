package com.nordman.big.life;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by s_vershinin on 17.05.2016.
 */
public class GridView extends View{
    public static final int STOPPED = 0;
    public static final int RUNNING = 1;
    public static final long MIN_LOOP_INTERVAL = 20;
    public static final long MAX_LOOP_INTERVAL = 500;
    public static final int DEFAULT_CELL_SIZE = 36;

    private Context context;
    private GameEngine engine;
    private int currentMode = STOPPED;
    private long loopInterval = 500;
    private RefreshHandler refreshHandler = new RefreshHandler(this);

    private Paint aliveCell;
    private Paint deadCell;



    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        engine = new GameEngine(context);
        initGridView();
    }

    private void initGridView() {
        setFocusable(true);

        aliveCell = new Paint();
        aliveCell.setColor(ContextCompat.getColor(context, R.color.cell));

        deadCell = new Paint();
        deadCell.setColor(ContextCompat.getColor(context, R.color.cell2));

    }

    public void setMode(int mode) {
        currentMode = mode;
        if (mode == RUNNING) update();
    }

    public int getMode(){
        return currentMode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d("LOG","...onDraw");

        // draw cells
        for (int h = 0; h < GameEngine.HEIGHT; h++) {
            for (int w = 0; w < GameEngine.WIDTH; w++) {

                if (GameEngine.getGrid()[h][w] == GameEngine.ALIVE) {
                    canvas.drawRect(
                            w * GameEngine.CELL_SIZE,
                            h * GameEngine.CELL_SIZE,
                            (w * GameEngine.CELL_SIZE) + (GameEngine.CELL_SIZE -2),
                            (h * GameEngine.CELL_SIZE) + (GameEngine.CELL_SIZE -2),
                            aliveCell);
                }

                if (GameEngine.getGrid()[h][w] == GameEngine.DEAD) {
                    canvas.drawRect(
                            w * GameEngine.CELL_SIZE,
                            h * GameEngine.CELL_SIZE,
                            (w * GameEngine.CELL_SIZE) + (GameEngine.CELL_SIZE -2),
                            (h * GameEngine.CELL_SIZE) + (GameEngine.CELL_SIZE -2),
                            deadCell);
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x;
        float y;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                x = event.getX();
                y = event.getY();
                engine.changeState(x, y);
                GridView.this.invalidate();
                break;
        }
        return true;
    }

    private void update() {
        engine.generateNextGeneration();
        refreshHandler.sleep(loopInterval);
    }

    static class RefreshHandler extends Handler {
        WeakReference<GridView> mGridView;  // нет утечкам памяти!!!
        Handler h = new Handler();
        Run run = new Run();

        RefreshHandler(GridView v) {
            mGridView = new WeakReference<GridView>(v);
        }

        class Run implements Runnable {

            @Override
            public void run() {
                GridView theGrid = mGridView.get();
                //Log.d("LOG","...handle message...");
                if (theGrid.currentMode == RUNNING) {
                    theGrid.update();
                    theGrid.invalidate();
                }
            }
        }

        public void sleep(long delayMillis) {
            h.postDelayed(run, delayMillis);
        }

        public void sleepCanceled() {
            h.removeCallbacks(run);
        }
    }

    public void clearLoop() {
        refreshHandler.sleepCanceled();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (GameEngine.CELL_SIZE == 0) {
            GameEngine.CELL_SIZE = DEFAULT_CELL_SIZE;
            GameEngine.WIDTH = MeasureSpec.getSize(widthMeasureSpec)/ GameEngine.CELL_SIZE;
            GameEngine.HEIGHT = MeasureSpec.getSize(heightMeasureSpec)/ GameEngine.CELL_SIZE;
            GameEngine.setGridArray();
        }
    }

    public void clear(){
        engine.resetGrid();
        this.invalidate();
    }

    public void startStop(){
        if (currentMode==STOPPED) {
            setMode(RUNNING);
        } else {
            setMode(STOPPED);
        }
    }

    public void step() {
        engine.generateNextGeneration();
        this.invalidate();
    }

    public void setSpeed(int progress) {
        loopInterval = (100*MAX_LOOP_INTERVAL - 100*(MAX_LOOP_INTERVAL - MIN_LOOP_INTERVAL)/100*progress)/100;
    }

    public void fillRandom() {
        engine.randomGrid();
        this.invalidate();
    }

    public void saveGrid(String name) {
        engine.saveGrid(name);
    }

    public void loadGrid(int id) {
        engine.loadGrid(id);
        this.invalidate();
    }

}
