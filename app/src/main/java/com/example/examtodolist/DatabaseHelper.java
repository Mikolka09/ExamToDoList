package com.example.examtodolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "table_tasks.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "tasks";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final Boolean COLUMN_PERFORMED = Boolean.valueOf("performed");


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TEXT
                + " TEXT, " + COLUMN_PERFORMED + " BOOLEAN);");
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_TEXT
                + ", " + COLUMN_PERFORMED  + ") VALUES ('Task_1', false);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
