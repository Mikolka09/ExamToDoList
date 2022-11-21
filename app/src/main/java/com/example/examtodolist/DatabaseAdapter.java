package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DatabaseAdapter {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter openBase() {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void closeBase() {
        databaseHelper.close();
    }

    private Cursor getAllEntriesTask() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID_TASK,
                DatabaseHelper.COLUMN_TEXT, DatabaseHelper.COLUMN_ID_USER};
        return sqLiteDatabase.query(DatabaseHelper.TABLE_TASK, columns,
                null, null, null, null, null);
    }

    private Cursor getAllEntriesUser() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_YEAR};
        return sqLiteDatabase.query(DatabaseHelper.TABLE_USER, columns,
                null, null, null, null, null);
    }

    public List<User> getUser() {
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = getAllEntriesUser();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            users.add(new User(id, name, year));
        }
        cursor.close();
        return users;
    }

    public List<Task> getTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = getAllEntriesTask();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_TASK));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USER));
            tasks.add(new Task(id, text, user_id));
        }
        cursor.close();
        return tasks;
    }

    public long getCountUser() {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseHelper.TABLE_USER);
    }

    public long getCountTask() {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseHelper.TABLE_TASK);
    }

    public User getUser(long id) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_ID);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            user = new User(id, name, year);
        }
        cursor.close();
        return user;
    }

    public List<Task> findUserTasks(long idUser) {
        List<Task> tasks = getTask();
        Predicate<Task> byAge = p -> p.getUserId()==idUser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tasks = tasks.stream().filter(byAge).collect(Collectors.toList());
        }

        /*String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE_TASK, DatabaseHelper.COLUMN_ID_USER);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(idUser)});
        if (cursor.moveToNext()) {
            @SuppressLint("Range") int idTask = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_TASK));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            tasks.add(new Task(idTask, text, idUser));
        }
        cursor.close();*/
        return tasks;
    }

    public User findUser(String name) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_NAME);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{name});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            user = new User(id, name, year);
        }
        cursor.close();
        return user;
    }

    public Task getTask(long id) {
        Task task = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE_TASK, DatabaseHelper.COLUMN_ID_TASK);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USER));
            task = new Task(id, text, user_id);
        }
        cursor.close();
        return task;
    }

    public long insertUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, user.getName());
        contentValues.put(DatabaseHelper.COLUMN_YEAR, user.getYear());
        return sqLiteDatabase.insert(DatabaseHelper.TABLE_USER, null, contentValues);
    }

    public long insertTask(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TEXT, task.getText());
        contentValues.put(DatabaseHelper.COLUMN_ID_USER, task.getUserId());
        return sqLiteDatabase.insert(DatabaseHelper.TABLE_TASK, null, contentValues);
    }

    public long deleteUser(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return sqLiteDatabase.delete(DatabaseHelper.TABLE_USER, whereClause, whereArgs);
    }

    public long deleteTask(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return sqLiteDatabase.delete(DatabaseHelper.TABLE_TASK, whereClause, whereArgs);
    }

    public long updateUser(User user) {
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + user.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, user.getName());
        contentValues.put(DatabaseHelper.COLUMN_YEAR, user.getYear());
        return sqLiteDatabase.update(DatabaseHelper.TABLE_USER, contentValues, whereClause, null);
    }

    public long updateTask(Task task) {
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + task.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TEXT, task.getText());
        contentValues.put(DatabaseHelper.COLUMN_ID_USER, task.getUserId());
        return sqLiteDatabase.update(DatabaseHelper.TABLE_TASK, contentValues, whereClause, null);
    }

}
