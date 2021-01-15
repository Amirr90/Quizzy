package com.e.quizzy.Models;

public class Categories {
    private String id;
    private long question;
    private String image;
    private String title;
    private boolean isNew;
    private String category;
    private long viewType;
    private long sets;

    public Categories(String id, String image, String title, boolean isNew) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.isNew = isNew;
    }

    public Categories(String id, long question, String image, String title,long sets) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.title = title;
        this.sets = sets;
    }

    public long getSets() {
        return sets;
    }

    public Categories(String id, long question, String image) {
        this.id = id;
        this.question = question;
        this.image = image;
    }

    public Categories(String title, long viewType) {
        this.title = title;
        this.viewType = viewType;
    }

    public Categories(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public long getQuestion() {
        return question;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public boolean isNew() {
        return isNew;
    }

    public String getCategory() {
        return category;
    }

    public long getViewType() {
        return viewType;
    }
}
