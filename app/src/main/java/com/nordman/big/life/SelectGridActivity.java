package com.nordman.big.life;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SelectGridActivity extends ListActivity {
    ArrayList<GridHeader> gridList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_grid);

        gridList = GameEngine.getSavedGridList();

        setListAdapter(getListAdapter());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        GridHeader item = (GridHeader) getListAdapter().getItem(position);
        Intent intent = new Intent();
        intent.putExtra("id", item.id);
        intent.putExtra("name", item.name);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public ListAdapter getListAdapter() {
        return new MyAdapter(this, R.layout.listview_row, gridList);
    }

    public class MyAdapter extends ArrayAdapter<GridHeader> {
        private Context context;
        private int resource;
        private List<GridHeader> objects;

        public MyAdapter(Context context, int resource, List<GridHeader> objects) {
            super(context, resource, objects);
            this.context=context;
            this.resource=resource;
            this.objects=objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=((Activity) context).getLayoutInflater();
            View row=inflater.inflate(resource, parent, false);

            TextView textView = (TextView) row.findViewById(R.id.label);
            textView.setText(objects.get(position).name);

            return row;
        }
    }

}
