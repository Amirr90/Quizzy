package com.e.quizzy.Models;

public class Question {
    private String setId;
    private String questionId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private boolean isNew;
    private String title;
    private String category;
    private String answer;


    public Question(String setId, String questionId, String question, String category, String answer) {
        this.setId = setId;
        this.questionId = questionId;
        this.question = question;
        this.category = category;
        this.answer = answer;
    }

    public Question(String setId, String questionId) {
        this.setId = setId;
        this.questionId = questionId;
    }

    public String getSetId() {
        return setId;
    }

    public Question(String setId, String questionId, String question, String optionA, String optionB, String optionC, String optionD, String category, String answer) {
        this.setId = setId;
        this.questionId = questionId;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.category = category;
        this.answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public boolean isNew() {
        return isNew;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getAnswer() {
        return answer;
    }
}
