package com.e.quizzy.Models;

public class Banner {

    private String title;
    private String image;
    private String id;
    private String description;
    private String setId;

    public Banner(String title, String image, String id, String description, String setId) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.description = description;
        this.setId = setId;
    }

    public Banner(String title, String image, String id) {
        this.title = title;
        this.image = image;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSetId() {
        return setId;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
