package com.nordman.big.life;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

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
        // раскомментировать, если не нужно фоновое выполнение
        /*
        gridView.clearLoop();
        gridView.setMode(GridView.STOPPED);
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gridView.clearLoop();
        gridView.setMode(GridView.STOPPED);
    }

    public void onButtonClear(View view) {
        gridView.clear();
    }

    public void onButtonStart(View view) {
        gridView.startStop();

        Button buttonStart = (Button)findViewById(R.id.buttonStart);
        Button buttonStep = (Button)findViewById(R.id.buttonStep);
        Button buttonClear = (Button)findViewById(R.id.buttonClear);
        Button buttonRandom = (Button)findViewById(R.id.buttonRandom);

        if (gridView.getMode()==GridView.RUNNING) {
            if (buttonStart != null) buttonStart.setText("Стоп");
            if (buttonStep != null) buttonStep.setEnabled(false);
            if (buttonClear != null) buttonClear.setEnabled(false);
            if (buttonRandom != null) buttonRandom.setEnabled(false);
        } else {
            if (buttonStart != null) buttonStart.setText("Старт");
            if (buttonStep != null) buttonStep.setEnabled(true);
            if (buttonClear != null) buttonClear.setEnabled(true);
            if (buttonRandom != null) buttonRandom.setEnabled(true);
        }
    }

    public void onButtonStep(View view) {
        gridView.step();
    }

    public void onButtonRandom(View view) {
        gridView.fillRandom();
    }

    public void onButtonSave(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.save_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.nametext);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gridView.saveGrid(String.valueOf(editText.getText()));
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void onButtonLoad(View view) {
        // TODO: сделать загрузку сохраненной матрицы
        //gridView.loadGrid();
    }

}
