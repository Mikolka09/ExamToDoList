package com.example.examtodolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tableTasks.db";
    private static final int SCHEMA = 1;
    static final String TABLE_USER = "users";
    static final String TABLE_TASK = "tasks";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_YEAR = "year";

    public static final String COLUMN_ID_TASK = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_ID_USER = "_id_user";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
                + " TEXT, " + COLUMN_YEAR + " INTEGER);";

        String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASK + "(" + COLUMN_ID_TASK
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TEXT
                + " TEXT, " + COLUMN_ID_USER + " INTEGER);";

        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }
}
