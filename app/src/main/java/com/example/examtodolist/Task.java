package com.example.examtodolist;

public class Task {
    private long id;
    private String text;
    private long id_user;

    Task(long id, String text, long id_user) {
        this.id = id;
        this.text = text;
        this.id_user = id_user;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUserId() {
        return id_user;
    }

}
