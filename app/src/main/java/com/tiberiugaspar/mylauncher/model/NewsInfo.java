package com.tiberiugaspar.mylauncher.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.Calendar;

public class NewsInfo {
    private String title;
    private String description;
    private Drawable image;
    private Calendar pubDate;
    private String link;
    private String source;

    public NewsInfo() {
    }

    public NewsInfo(String title, String description, Drawable image, Calendar pubDate, String link, String source) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.pubDate = pubDate;
        this.link = link;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Calendar getPubDate() {
        return pubDate;
    }

    public void setPubDate(Calendar pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
