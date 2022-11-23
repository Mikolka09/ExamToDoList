package com.example.examtodolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText nameBox;
    private EditText yearBox;
    private DatabaseAdapterUser adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameBox = findViewById(R.id.name);
        yearBox = findViewById(R.id.year);
        Button enterButton = findViewById(R.id.enter_user);
        adapter = new DatabaseAdapterUser(this);

        enterButton.setOnClickListener(view -> enterName());
    }

    public void enterName() {
        String name = nameBox.getText().toString();
        int year = Integer.parseInt(yearBox.getText().toString());
        adapter.open();
        if (adapter.getCountUser() == 0) {
            User user = new User(1, name, year);
            adapter.insert(user);
            adapter.close();
            showToast("Users " + name + " added!");
            startWindow(user);
        } else {
            if (adapter.findUserForName(name, year) != null) {
                User user = adapter.findUserForName(name, year);
                adapter.close();
                startWindow(user);
            } else {
                User user = new User(1, name, year);
                confirmAction(user);
            }
        }
    }

    public void confirmAction(User user) {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder
                .setTitle("Confirm Your Action")
                .setMessage("There is no such user! Want to create a new user or try again?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            adapter.insert(user);
                            User userNew = adapter.findUserForName(user.getName(), user.getYear());
                            adapter.close();
                            showToast("Users " + user.getName() + " added!");
                            startWindow(userNew);
                        })
                .setNegativeButton("No",
                        (dialog, id) -> {
                            yearBox.setText("");
                            nameBox.setText("");
                            dialog.cancel();
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void startWindow(User user) {
        yearBox.setText("");
        nameBox.setText("");
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("year", user.getYear());
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}