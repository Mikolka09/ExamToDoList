package com.example.examtodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView taskList;
    TextView header;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor taskCursor;
    SimpleCursorAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        taskList = findViewById(R.id.listItem);

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        header.setText("TASKS");
        db = databaseHelper.getReadableDatabase();

        taskCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_TASK, null);
        String[] headers = new String[]{DatabaseHelper.COLUMN_TEXT};
        taskAdapter = new SimpleCursorAdapter(this, R.layout.line_list_item,
                taskCursor, headers, new int[]{R.id.text}, 0);
        taskList.setAdapter(taskAdapter);
    }

    public void addTask(View v){
        CustomDialogFragment dialog = new CustomDialogFragment();
        dialog.show(getSupportFragmentManager(), "custom");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        taskCursor.close();
    }
}