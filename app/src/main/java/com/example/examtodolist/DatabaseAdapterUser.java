package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseAdapterUser {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseAdapterUser(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapterUser open() throws SQLException {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_YEAR};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_USER, columns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            users.add(new User(id, name, year));
        }
        cursor.close();
        return users;
    }

    public long getCountUser() {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseHelper.TABLE_USER);
    }

    public User getUserForId(long id) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ?", DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_ID);
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            user = new User(id, name, year);
        }
        cursor.close();
        return user;
    }

    public User findUserForName(String name) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ?", DatabaseHelper.TABLE_USER,
                DatabaseHelper.COLUMN_NAME.toUpperCase(Locale.ROOT));
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{name.toUpperCase(Locale.ROOT)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            user = new User(id, name, year);
        }
        cursor.close();
        return user;
    }

    public long insert(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, user.getName());
        contentValues.put(DatabaseHelper.COLUMN_YEAR, user.getYear());
        return sqLiteDatabase.insert(DatabaseHelper.TABLE_USER, null, contentValues);
    }

    public long delete(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return sqLiteDatabase.delete(DatabaseHelper.TABLE_USER, whereClause, whereArgs);
    }

    public long update(User user) {
        String whereClause = DatabaseHelper.COLUMN_ID + " = " + user.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, user.getName());
        contentValues.put(DatabaseHelper.COLUMN_YEAR, user.getYear());
        return sqLiteDatabase.update(DatabaseHelper.TABLE_USER, contentValues, whereClause, null);
    }

}
