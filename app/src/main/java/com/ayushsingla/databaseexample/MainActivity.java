package com.ayushsingla.databaseexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayushsingla.dblib.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DBHandler db;
    List<HashMap<String, String>> data = new ArrayList<>();

    MyAdapter mAdapter;
    ListView lvData;
    Button bAdd;

    public static int RESULT = 1;

    boolean filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filter = getIntent().getBooleanExtra("filter", false);

        lvData = (ListView) findViewById(R.id.lvData);
        bAdd = (Button) findViewById(R.id.bAdd);

        db = new DBHandler(this, DB.DB_NAME, DB.DB_VERSION);
        HashMap<String, String> columns = new HashMap<>();
        columns.put(DB.ROW_ID, "INTEGER PRIMARY KEY");
        columns.put(DB.NAME, "TEXT");
        columns.put(DB.AGE, "INTEGER");
        columns.put(DB.CITY, "TEXT");
        db.createTable(DB.TABLE_NAME, columns);

        if (filter == false) {
            data = db.getRowsWhereWithAnd(DB.TABLE_NAME, new String[]{"*"}, new String[]{}, 4);
        } else {
            data = db.getRowsWhereWithAnd(DB.TABLE_NAME, new String[]{"*"}, new String[]{DB.AGE + " > 20"}, 4);
        }

        mAdapter = new MyAdapter();
        lvData.setAdapter(mAdapter);
        bAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == bAdd) {
            Intent add = new Intent(this, EntryActivity.class);
            add.putExtra("name", "");
            add.putExtra("age", "");
            add.putExtra("city", "");
            add.putExtra("rowId", "");
            startActivityForResult(add, 1);
        }
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            RelativeLayout rlEntry;
            TextView tvName, tvAge, tvCity;
            Button bDelete;

            public ViewHolder(View v) {
                rlEntry = (RelativeLayout) v.findViewById(R.id.rlEntry);
                tvName = (TextView) v.findViewById(R.id.tvName);
                tvAge = (TextView) v.findViewById(R.id.tvAge);
                tvCity = (TextView) v.findViewById(R.id.tvCity);
                bDelete = (Button) v.findViewById(R.id.bDelete);
            }
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            ViewHolder vh;
            if (v == null) {
                v = View.inflate(MainActivity.this, R.layout.adapter_data, null);
                vh = new ViewHolder(v);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }

            vh.tvName.setText(data.get(position).get(DB.NAME));
            vh.tvAge.setText(data.get(position).get(DB.AGE));
            vh.tvCity.setText(data.get(position).get(DB.CITY));

            vh.bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> condition = new HashMap<>();
                    condition.put(DB.ROW_ID, data.get(position).get(DB.ROW_ID));
                    db.deleteRows(DB.TABLE_NAME, condition);

                    data = db.getRowsWhereWithAnd(DB.TABLE_NAME, new String[]{"*"}, new String[]{}, 4);
                    notifyDataSetChanged();
                }
            });

            vh.rlEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent add = new Intent(MainActivity.this, EntryActivity.class);
                    add.putExtra("name", data.get(position).get(DB.NAME));
                    add.putExtra("age", data.get(position).get(DB.AGE));
                    add.putExtra("city", data.get(position).get(DB.CITY));
                    add.putExtra("rowId", data.get(position).get(DB.ROW_ID));
                    startActivityForResult(add, 1);
                }
            });

            return v;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT) {
            data = db.getRowsWhereWithAnd(DB.TABLE_NAME, new String[]{"*"}, new String[]{}, 4);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (filter == false)
            getMenuInflater().inflate(R.menu.filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            Intent filter = new Intent(this, MainActivity.class);
            filter.putExtra("filter", true);
            startActivity(filter);
        }
        return super.onOptionsItemSelected(item);
    }
}
