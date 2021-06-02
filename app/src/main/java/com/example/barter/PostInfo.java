package com.example.barter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private ArrayList<String> content;
    private String publisher;
    private Date createdAt;
    private String id;



    public PostInfo(String title, ArrayList<String> content, String publisher, Date createdAt, String id) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
    }

    public PostInfo(String title, ArrayList<String> content, String publisher, Date createdAt) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }
    public String getPublisher() {
        return publisher;
    }
    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("content",content);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);

        return docData;
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

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
