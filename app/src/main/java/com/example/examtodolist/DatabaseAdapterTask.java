package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DatabaseAdapterTask {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseAdapterTask(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapterTask open() throws SQLException {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID_TASK,
                DatabaseHelper.COLUMN_TEXT, DatabaseHelper.COLUMN_ID_USER};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_TASK, columns,
                null, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_TASK));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USER));
            tasks.add(new Task(id, text, user_id));
        }
        cursor.close();
        return tasks;
    }

    public long getCountTask() {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseHelper.TABLE_TASK);
    }

    public List<Task> findUserTasks(long idUser) {
        List<Task> tasks = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s = ?", DatabaseHelper.TABLE_TASK, DatabaseHelper.COLUMN_ID_USER);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(idUser)});
        if (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_TASK));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            tasks.add(new Task(id, text, idUser));
        }
        cursor.close();
        return tasks;
    }

    public Task getTask(long id) {
        Task task = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ?", DatabaseHelper.TABLE_TASK, DatabaseHelper.COLUMN_ID_TASK);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USER));
            task = new Task(id, text, user_id);
        }
        cursor.close();
        return task;
    }

    public long insert(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TEXT, task.getText());
        contentValues.put(DatabaseHelper.COLUMN_ID_USER, task.getUserId());
        return sqLiteDatabase.insert(DatabaseHelper.TABLE_TASK, null, contentValues);
    }

    public long delete(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return sqLiteDatabase.delete(DatabaseHelper.TABLE_TASK, whereClause, whereArgs);
    }

    public long update(Task task) {
        String whereClause = DatabaseHelper.COLUMN_ID_TASK + " = " + task.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TEXT, task.getText());
        contentValues.put(DatabaseHelper.COLUMN_ID_USER, task.getUserId());
        return sqLiteDatabase.update(DatabaseHelper.TABLE_TASK, contentValues, whereClause, null);
    }

}
