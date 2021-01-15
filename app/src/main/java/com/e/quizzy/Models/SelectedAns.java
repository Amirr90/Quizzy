package com.e.quizzy.Models;

public class SelectedAns {
    private String selectedAns;
    private String answer;
    private String question;
    private String questionId;

    public SelectedAns(String selectedAns, String answer, String question, String questionId) {
        this.selectedAns = selectedAns;
        this.answer = answer;
        this.question = question;
        this.questionId = questionId;
    }

    public String getSelectedAns() {
        return selectedAns;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionId() {
        return questionId;
    }
}
