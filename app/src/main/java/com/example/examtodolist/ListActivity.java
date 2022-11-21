package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    private ListView taskList;
    private TextView header;
    private int userId;
    ArrayAdapter<Task> arrayAdapter;

    private DatabaseAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        header = findViewById(R.id.header);
        taskList = findViewById(R.id.listItem);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.get("name").toString();
            int year = Integer.parseInt(args.get("year").toString());
            userId = Integer.parseInt(args.get("userId").toString());
            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            header.setText("My Tasks, " + name.toUpperCase(Locale.ROOT) + ", " + age);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new DatabaseAdapter(this);

        if ((long) adapter.findUserTasks(userId).size() != 0) {
            List<Task> tasks = adapter.findUserTasks(userId);
            arrayAdapter = new ArrayAdapter<>(this,R.layout.line_list_item, tasks);
            taskList.setAdapter(arrayAdapter);
            adapter.closeBase();
        }

    }

    public void deleteTask(View v) {

    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void editTask(View v) {

    }

    public void addTask(View v) {

    }


}
