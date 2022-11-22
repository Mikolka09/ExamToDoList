package com.example.examtodolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {

    private ListView taskList;
    private TextView header;
    private int userId;
    private Bundle args;
    private String name;
    private int year;

    SimpleCursorAdapter arrayAdapter;

    private DatabaseAdapterTask adapter;
    private DatabaseAdapterUser adapterUser;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        header = findViewById(R.id.header);
        taskList = findViewById(R.id.listItem);
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton);

        args = getIntent().getExtras();
        if (args != null) {
            name = args.get("name").toString();
            year = Integer.parseInt(args.get("year").toString());
            userId = Integer.parseInt(args.get("userId").toString());
            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            header.setText("My Tasks, " + name.toUpperCase(Locale.ROOT) + ", " + age);
        }
        taskList.setOnItemClickListener((parent, view, position, id) -> {
            editForDelete(id);
        });
        header.setOnClickListener(this::edit);
        addButton.setOnClickListener(this::added);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        adapter = new DatabaseAdapterTask(this);
        adapterUser = new DatabaseAdapterUser(this);
        adapter.open();
        if (adapter.findUserTasks(userId).size() != 0) {
            Cursor tasks = adapter.cursorTasks(userId);
            String[] list = new String[]{DatabaseHelper.COLUMN_TEXT};
            arrayAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                    tasks, list, new int[]{android.R.id.text1}, 0);
            taskList.setAdapter(arrayAdapter);
            adapter.close();
        }
    }

    public void editForDelete(long idTask) {
        LayoutInflater li = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View editDeleteView = li.inflate(R.layout.edit_delete_task, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(editDeleteView);
        EditText userInput = editDeleteView.findViewById(R.id.enter_task);
        adapter.open();
        Task task = adapter.getTask(idTask);
        userInput.setText(task.getText());
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        (dialog, id) -> {
                            String text = userInput.getText().toString();
                            adapter.update(new Task(idTask, text, userId));
                            adapter.close();
                            showToast("Task edited!");
                            this.onResume();
                        })
                .setNegativeButton("Delete",
                        (dialog, id) -> {
                            adapter.delete(idTask);
                            adapter.close();
                            showToast("Task deleted!");
                            this.onResume();
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void edit(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View editDeleteUserView = li.inflate(R.layout.edit_user_del, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(editDeleteUserView);
        EditText userName = editDeleteUserView.findViewById(R.id.enter_name_user);
        EditText userYear = editDeleteUserView.findViewById(R.id.enter_year_user);
        adapterUser.open();
        User user = adapterUser.getUserForId(userId);
        userName.setText(user.getName());
        userYear.setText(String.valueOf(user.getYear()));
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        (dialog, id) -> {
                            String name = userName.getText().toString();
                            int year = Integer.parseInt(userYear.getText().toString());
                            adapterUser.update(new User(userId, name, year));
                            adapterUser.close();
                            renameHeader(name, year);
                            showToast("User edited!");
                            this.onResume();
                        })
                .setNegativeButton("Delete",
                        (dialog, id) -> {
                            confirmAction();
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void renameHeader(String name, int year) {
        int age = Calendar.getInstance().get(Calendar.YEAR) - year;
        header.setText("My Tasks, " + name.toUpperCase(Locale.ROOT) + ", " + age);
    }

    public void deletedAllTasks(long id) {
        adapter.open();
        List<Task> taskList = adapter.findUserTasks(id);
        for (Task ts : taskList) {
            adapter.delete(ts.getId());
        }
        adapter.close();
    }

    public void added(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.added_task, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        EditText userInput = promptsView.findViewById(R.id.input_task);
        adapter.open();
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            String text = userInput.getText().toString();
                            adapter.insert(new Task(1, text, userId));
                            adapter.close();
                            showToast("New Task added!");
                            this.onResume();
                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void confirmAction() {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder
                .setTitle("Confirm Your Action")
                .setMessage("Are you sure you want to delete the user?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            adapterUser.delete(userId);
                            adapterUser.close();
                            deletedAllTasks(userId);
                            showToast("User deleted!");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        })
                .setNegativeButton("No",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
