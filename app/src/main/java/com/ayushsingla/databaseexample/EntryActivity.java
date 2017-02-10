package com.ayushsingla.databaseexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ayushsingla.dblib.DBHandler;

import java.util.HashMap;

/**
 * Created by ayushsingla on 02/07/16.
 */
public class EntryActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etAge, etCity;
    Button bSubmit;

    DBHandler db;

    String name, age, city, rowId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        db = new DBHandler(this, DB.DB_NAME, DB.DB_VERSION);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etCity = (EditText) findViewById(R.id.etCity);
        bSubmit = (Button) findViewById(R.id.bSubmit);

        bSubmit.setOnClickListener(this);

        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        city = getIntent().getStringExtra("city");
        rowId = getIntent().getStringExtra("rowId");

        etName.setText(name);
        etAge.setText(age);
        etCity.setText(city);

    }

    @Override
    public void onClick(View v) {
        if (v == bSubmit) {

            name = etName.getText().toString().trim();
            age = etAge.getText().toString().trim();
            city = etCity.getText().toString().trim();

            if (rowId.equals("")) {
                HashMap<String, String> data = new HashMap<>();
                data.put(DB.NAME, name);
                data.put(DB.AGE, age);
                data.put(DB.CITY, city);
                db.insertRow(DB.TABLE_NAME, data);
            } else {
                HashMap<String, String> data = new HashMap<>();
                data.put(DB.NAME, name);
                data.put(DB.AGE, age);
                data.put(DB.CITY, city);

                HashMap<String, String> condition = new HashMap<>();
                condition.put(DB.ROW_ID, rowId);
                db.updateRows(DB.TABLE_NAME, data, condition);
            }
            setResult(MainActivity.RESULT);
            finish();
        }
    }
}
