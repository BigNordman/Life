package com.nordman.big.life;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.grid_view);
    }

    public void onButtonClear(View view) {
        gridView.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gridView.clearLoop();
        gridView.setMode(GridView.STOPPED);
    }

    public void onButtonStart(View view) {
        gridView.startStop();
    }

    public void onButtonStep(View view) {
        gridView.step();
    }
}
