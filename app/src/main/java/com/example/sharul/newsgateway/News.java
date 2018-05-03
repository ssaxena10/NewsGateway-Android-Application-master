package com.example.sharul.newsgateway;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharul on 5/6/17.
 */

public class News implements Serializable {
    //private String source = "";
    private String title = "";
    private String author = "";
    private String url = "";
    private String description = "";
    private int count;
    private String time = "";
    private Bitmap b;
    public static ArrayList<News> all = new ArrayList<>();

    public  News()
    {

    }
    public News(String title, String author, Bitmap bm, String description, int count, String time,String u) {
        //this.source = source;
        this.title = title;
        this.author = author;
        this.b = bm;
        this.description = description;
        this.count = count;
        this.time = time;
        this.url = u;
    }

    public static void addnew(String t, String a, Bitmap b, String d, int c, String ti, String u)
    {

        News nd = new News(t,a,b,d,c,ti,u);
        all.add(nd);
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static News get(int idx) {
        return all.get(idx);
    }
}
