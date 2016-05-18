package com.nordman.big.life;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private SeekBar speedBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.grid_view);
        speedBar = (SeekBar) findViewById(R.id.speedBar);
        if (speedBar != null) speedBar.setProgress(50);
        gridView.setSpeed(50);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gridView.setSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("О программе")
                        .setMessage("Game of Life by John Conway")
                        .setPositiveButton(R.string.ok, null)
                        .show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gridView.clearLoop();
        gridView.setMode(GridView.STOPPED);
    }

    public void onButtonClear(View view) {
        gridView.clear();
    }

    public void onButtonStart(View view) {
        gridView.startStop();
    }

    public void onButtonStep(View view) {
        gridView.step();
    }
}
