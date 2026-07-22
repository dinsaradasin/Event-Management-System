package com.eventsystem.models;

public class PreferenceResult {
    private String fieldLabel;
    private String answerText;
    private int voteCount;

    
    public PreferenceResult(String fieldLabel, String answerText, int voteCount) {
        this.fieldLabel = fieldLabel;
        this.answerText = answerText;
        this.voteCount = voteCount;
    }

    
    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}