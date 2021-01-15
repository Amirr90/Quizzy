package com.e.quizzy.Models;

public class StatsModel {
    private String title;
    private String status;
    private String id;
    private long timestamp;

    public StatsModel(String title, String status, String id, long timestamp) {
        this.title = title;
        this.status = status;
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
