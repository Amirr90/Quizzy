package com.e.quizzy.Models;

public class AnsList {

    private String answer;
    private String question;
    private String selected_ans;

    public AnsList(String answer, String question, String selected_ans) {
        this.answer = answer;
        this.question = question;
        this.selected_ans = selected_ans;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getSelected_ans() {
        return selected_ans;
    }
}
