package com.example.examtodolist;

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
    private Button enterButton;
    private DatabaseAdapterUser adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameBox = findViewById(R.id.name);
        yearBox = findViewById(R.id.year);
        enterButton = findViewById(R.id.enter_user);
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
                adapter.insert(user);
                adapter.close();
                showToast("Users " + name + " added!");
                startWindow(user);
            }
        }
    }

    public void startWindow(User user) {
        nameBox.setText("");
        yearBox.setText("");
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