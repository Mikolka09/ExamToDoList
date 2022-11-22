package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {

    private ListView taskList;
    private TextView header;
    private int userId;
    private Button editButton;

    SimpleCursorAdapter arrayAdapter;

    private DatabaseAdapterTask adapter;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        header = findViewById(R.id.header);
        taskList = findViewById(R.id.listItem);
        editButton = findViewById(R.id.edit_button);
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.get("name").toString();
            int year = Integer.parseInt(args.get("year").toString());
            userId = Integer.parseInt(args.get("userId").toString());
            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            header.setText("My Tasks, " + name.toUpperCase(Locale.ROOT) + ", " + age);
        }
        taskList.setOnItemClickListener((parent, view, position, id) -> {
            editForDelete(view);
        });
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
            adapter.close();
        } else {
            if (adapter.findUserTasks(userId).size() != 0) {
                Cursor tasks = adapter.cursorTasks(userId);
                String[] list = new String[]{DatabaseHelper.COLUMN_TEXT};
                arrayAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                        tasks, list, new int[]{android.R.id.text1}, 0);
                taskList.setAdapter(arrayAdapter);
                adapter.close();
            }


        }

    }

    public void editForDelete(View v) {

    }

    public void edit(View v) {

    }

    public void added(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        EditText userInput = (EditText) promptsView.findViewById(R.id.input_task);
        adapter.open();
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            String text = userInput.getText().toString();
                            adapter.insert(new Task(1, text, userId));
                            adapter.close();
                            this.onRestart();
                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
