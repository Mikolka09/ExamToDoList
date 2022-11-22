package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {

    private ListView taskList;
    private TextView header;
    private int userId;
    private Button editButton;
    private FloatingActionButton addButton;

    ArrayAdapter<Task> arrayAdapter;

    private DatabaseAdapterTask adapter;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        header = findViewById(R.id.header);
        taskList = findViewById(R.id.listItem);
        editButton = findViewById(R.id.edit_button);
        addButton = findViewById(R.id.floatingActionButton);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.get("name").toString();
            int year = Integer.parseInt(args.get("year").toString());
            userId = Integer.parseInt(args.get("userId").toString());
            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            header.setText("My Tasks, " + name.toUpperCase(Locale.ROOT) + ", " + age);
        }
        editButton.setOnClickListener(this::edit);
        addButton.setOnClickListener(this::added);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        adapter = new DatabaseAdapterTask(this);
        adapter.open();
        if (adapter.getCountTask() == 0) {
            header.setText("There are no tasks!");
            header.setTextColor(Color.RED);
            editButton.setVisibility(View.INVISIBLE);
        } else {
            List<Task> tasks = adapter.findUserTasks(userId);
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
            taskList.setAdapter(arrayAdapter);
            adapter.close();

        }

    }

    public void delete(View v) {

    }

    public void edit(View v) {

    }

    public void added(View v) {

    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
