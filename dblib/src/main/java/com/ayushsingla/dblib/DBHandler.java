package com.ayushsingla.dblib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ayushsingla on 02/07/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    String dbName;

    public DBHandler(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
        this.dbName = dbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ArrayList<String> tables = new ArrayList<>();
        Cursor cur = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);
        if (cur != null)
            cur.moveToFirst();

        if (cur.moveToFirst()) {
            do {
                tables.add(cur.getString(0));
            } while (cur.moveToNext());
        }

        for (int i = 0; i < tables.size(); i++) {
            db.execSQL("DROP TABLE IF EXISTS " + tables.get(i));
        }
    }

    public void createTable(String tableName, String[] columns) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor counter = db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tableName});
        int exists = counter.getCount();
        if (exists == 0) {
            if (columns.length % 2 == 0) {
                String query = "";
                for (int i = 0; i < columns.length; i++) {
                    query += columns[i] + " ";
                    if (i % 2 == 1 && i < columns.length - 1) {
                        query += ", ";
                    }
                }
                String CREATE_TABLE = "CREATE TABLE " + tableName + "(" + query + ")";
                db.execSQL(CREATE_TABLE);
            } else {
                Log.e("DBLib", "Specify the columns correctly");
            }
        }
        db.close();
    }

    public void createTable(String tableName, HashMap<String, String> columns) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor counter = db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tableName});
        int exists = counter.getCount();
        if (exists == 0) {
            String query = "";
            for (String key : columns.keySet()) {
                query += key + " " + columns.get(key) + ", ";
            }
            if (query.length() > 0) {
                query = query.substring(0, query.length() - 2);
            }
            String CREATE_TABLE = "CREATE TABLE " + tableName + "(" + query + ")";
            db.execSQL(CREATE_TABLE);
        }
        db.close();
    }

    public void insertRow(String tableName, HashMap<String, String> rowData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (String key : rowData.keySet()) {
            values.put(key, rowData.get(key));
        }

        db.insert(tableName, null, values);
        db.close();
    }

    public List<HashMap<String, String>> getRowsWhereWithAnd(String tableName, String[] requiredColumns, String[] where) {
        List<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (requiredColumns == null || requiredColumns.length == 0) {
            requiredColumns = new String[]{"*"};
        }

        if (where.length % 2 == 0 || where.length == 1) {
            String required = "";
            for (String r : requiredColumns) {
                required += r + ", ";
            }
            if (required.length() > 0) {
                required = required.substring(0, required.length() - 2);
            }

            String condition = "";
            if (where != null && where.length != 1) {
                for (int i = 0; i < where.length; i += 2) {
                    condition += where[i] + " = " + "\'" + where[i + 1] + "\'";
                    if (i < where.length - 2) {
                        condition += " and ";
                    }
                }
            }

            if (where.length == 1) {
                condition = where[0];
            }

            if (required.equals("*")) {
                Log.e("DBLib", "Please specify number of columns you want to fetch");
                return data;
            }

            String query = "";
            if (condition.equals("")) {
                query = "SELECT " + required + " FROM " + tableName;
            } else {
                query = "SELECT " + required + " FROM " + tableName + " WHERE " + condition;
            }

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> row = new HashMap<>();
                    for (String column : requiredColumns) {
                        row.put(column, cursor.getString(cursor.getColumnIndex(column)));
                    }
                    data.add(row);
                } while (cursor.moveToNext());
            }
        } else {
            Log.e("DBLib", "Specify the columns correctly");
        }
        db.close();
        return data;
    }

    public List<HashMap<String, String>> getRowsWhereWithAnd(String tableName, String[] requiredColumns, String[] where, int numColumns) {
        List<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (requiredColumns == null || requiredColumns.length == 0) {
            requiredColumns = new String[]{"*"};
        }

        if (where.length % 2 == 0 || where.length == 1) {
            String required = "";
            for (String r : requiredColumns) {
                required += r + ", ";
            }
            if (required.length() > 0) {
                required = required.substring(0, required.length() - 2);
            }

            String condition = "";
            if (where != null && where.length != 1) {
                for (int i = 0; i < where.length; i += 2) {
                    condition += where[i] + " = " + "\'" + where[i + 1] + "\'";
                    if (i < where.length - 2) {
                        condition += " and ";
                    }
                }
            }

            if (where.length == 1) {
                condition = where[0];
            }

            if (required.equals("*") && numColumns == 0) {
                Log.e("DBLib", "Please specify number of columns you want to fetch");
                return data;
            }
            String query = "";
            if (condition.equals("")) {
                query = "SELECT " + required + " FROM " + tableName;
            } else {
                query = "SELECT " + required + " FROM " + tableName + " WHERE " + condition;
            }

            Log.i("DBLib", query);

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> row = new HashMap<>();
                    if (required.equals("*")) {
                        for (int i = 0; i < numColumns; i++) {
                            row.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                    } else {
                        for (String column : requiredColumns) {
                            row.put(column, cursor.getString(cursor.getColumnIndex(column)));
                        }
                    }
                    data.add(row);
                } while (cursor.moveToNext());
            }
        } else {
            Log.i("DBLib", "Specify the columns correctly");
        }
        db.close();
        return data;
    }

    public List<HashMap<String, String>> getRowsWhereWithAnd(String tableName, String[] requiredColumns, HashMap<String, String> where) {
        List<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (requiredColumns == null || requiredColumns.length == 0) {
            requiredColumns = new String[]{"*"};
        }

        String required = "";
        for (String r : requiredColumns) {
            required += r + ", ";
        }
        if (required.length() > 0) {
            required = required.substring(0, required.length() - 2);
        }

        String condition = "";
        if (where != null) {
            for (String key : where.keySet()) {
                condition += key + " = " + "\'" + where.get(key) + "\'" + " and ";
            }
            if (condition.length() > 0) {
                condition = condition.substring(0, condition.length() - 5);
            }
        }

        if (required.equals("*")) {
            Log.i("DBLib", "Please specify number of columns you want to fetch");
            return data;
        }

        String query = "";
        if (condition.equals("")) {
            query = "SELECT " + required + " FROM " + tableName;
        } else {
            query = "SELECT " + required + " FROM " + tableName + " WHERE " + condition;
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> row = new HashMap<>();
                for (String column : requiredColumns) {
                    row.put(column, cursor.getString(cursor.getColumnIndex(column)));
                }
                data.add(row);
            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }

    public List<HashMap<String, String>> getRowsWhereWithAnd(String tableName, String[] requiredColumns, HashMap<String, String> where, int numColumns) {
        List<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (requiredColumns == null || requiredColumns.length == 0) {
            requiredColumns = new String[]{"*"};
        }

        String required = "";
        for (String r : requiredColumns) {
            required += r + ", ";
        }
        if (required.length() > 0) {
            required = required.substring(0, required.length() - 2);
        }

        String condition = "";
        if (where != null) {
            for (String key : where.keySet()) {
                condition += key + " = " + "\'" + where.get(key) + "\'" + " and ";
            }
            if (condition.length() > 0) {
                condition = condition.substring(0, condition.length() - 5);
            }
        }
        if (required.equals("*") && numColumns == 0) {
            Log.e("DBLib", "Please specify number of columns you want to fetch");
            return data;
        }

        String query = "";
        if (condition.equals("")) {
            query = "SELECT " + required + " FROM " + tableName;
        } else {
            query = "SELECT " + required + " FROM " + tableName + " WHERE " + condition;
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> row = new HashMap<>();
                if (required.equals("*")) {
                    for (int i = 0; i < numColumns; i++) {
                        row.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    Log.e("DBLib", "Fetch columns with index upto number of columns");
                } else {
                    for (String column : requiredColumns) {
                        row.put(column, cursor.getString(cursor.getColumnIndex(column)));
                    }
                    Log.e("DBLib", "Number of columns will work only with *");
                }
                data.add(row);
            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }

    public int updateRows(String tableName, HashMap<String, String> rowData, String[] where) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (String key : rowData.keySet()) {
            values.put(key, rowData.get(key));
        }

        String condition = "";
        if (where != null) {
            for (int i = 0; i < where.length; i += 2) {
                condition += where[i] + " = " + where[i + 1];
                if (i < where.length - 2) {
                    condition += " and ";
                }
            }
        }

        int result = db.update(tableName, values, condition, null);
        db.close();
        return result;
    }

    public int updateRows(String tableName, HashMap<String, String> rowData, HashMap<String, String> where) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (String key : rowData.keySet()) {
            values.put(key, rowData.get(key));
        }

        String condition = "";
        if (where != null) {
            for (String key : where.keySet()) {
                condition += key + " = " + where.get(key) + " and ";
            }
            if (condition.length() > 0) {
                condition = condition.substring(0, condition.length() - 5);
            }
        }

        int result = db.update(tableName, values, condition, null);
        db.close();
        return result;
    }

    public void deleteRows(String tableName, String[] where) {
        SQLiteDatabase db = this.getWritableDatabase();
        String condition = "";
        if (where != null) {
            for (int i = 0; i < where.length; i += 2) {
                condition += where[i] + " = " + where[i + 1];
                if (i < where.length - 2) {
                    condition += " and ";
                }
            }
        }
        db.delete(tableName, condition, null);
        db.close();
    }

    public void deleteRows(String tableName, HashMap<String, String> where) {
        SQLiteDatabase db = this.getWritableDatabase();
        String condition = "";
        if (where != null) {
            for (String key : where.keySet()) {
                condition += key + " = " + where.get(key) + " and ";
            }
            if (condition.length() > 0) {
                condition = condition.substring(0, condition.length() - 5);
            }
        }
        db.delete(tableName, condition, null);
        db.close();
    }

    public void executeQuery(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public List<HashMap<String, String>> runQueryGetResult(String query, int numColumns) {
        List<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> row = new HashMap<>();
                for (int i = 0; i < numColumns; i++) {
                    row.put(cursor.getColumnName(i), cursor.getString(i));
                }
                data.add(row);
            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }

}
