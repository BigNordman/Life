package com.nordman.big.life;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

/**
 * Created by s_vershinin on 17.05.2016.
 */
public class GridView extends View{
    public static final int STOPPED = 0;
    public static final int RUNNING = 1;

    private Context context;
    private GameEngine engine;
    private int currentMode = STOPPED;
    private long loopInterval = 1000/6;
    private RefreshHandler refreshHandler = new RefreshHandler(this);

    private Paint background;
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

        background = new Paint();
        background.setColor(ContextCompat.getColor(context, R.color.background));

        aliveCell = new Paint();
        aliveCell.setColor(ContextCompat.getColor(context, R.color.cell));

        deadCell = new Paint();
        deadCell.setColor(ContextCompat.getColor(context, R.color.cell2));
    }

    public void setMode(int mode) {
        currentMode = mode;
        Button buttonStart = (Button)((MainActivity)context).findViewById(R.id.buttonStart);

        if (mode == RUNNING) {
            if (buttonStart != null) buttonStart.setText("Стоп");
            update();
        }

        if (mode == STOPPED) {
            if (buttonStart != null) buttonStart.setText("Старт");
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

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
        engine.generateNextGenerationFunny();
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
                Log.d("LOG","...handle message...");
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

    public void clearLoop() {
        refreshHandler.sleepCanceled();
    }

    public void step() {
        engine.generateNextGenerationFunny();
        this.invalidate();
    }
}
