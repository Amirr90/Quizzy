package com.e.quizzy.Models;

public class SubCategories {

    private String title;
    private String id;
    private String description;
    private  long difficulty;
    private long questions;
    private long timestamp;
    private boolean isNew;
    private long played;

    public SubCategories(String title, String id, String description, long difficulty, long questions, long timestamp, boolean isNew, long played) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.difficulty = difficulty;
        this.questions = questions;
        this.timestamp = timestamp;
        this.isNew = isNew;
        this.played = played;
    }


    public long getPlayed() {
        return played;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isNew() {
        return isNew;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getDifficulty() {
        return difficulty;
    }

    public long getQuestions() {
        return questions;
    }
}
