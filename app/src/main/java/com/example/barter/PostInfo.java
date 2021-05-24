package com.example.barter;

import android.text.Editable;

import java.util.ArrayList;

public class PostInfo {
    private String title;
    private String content;
    private String publisher;

    public PostInfo(String title, String content, String publisher) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}