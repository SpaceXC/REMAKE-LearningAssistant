package com.bakamcu.remake.learningassistant;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Problem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    String subject = "";
    String problemSource = "";
    String problem = "";
    String wrongAnswer = "";
    String correctAnswer = "";
    String problemImgPath = "";
    String wrongAnswerImgPath = "";
    String correctImgPath = "";
    String reason = "";
    long addTime = 0;
    boolean corrAnsInvisible = false;
    float probRate = 0;

    public Problem(String subject, String problemSource, String problem, String wrongAnswer, String correctAnswer, String problemImgPath, String wrongAnswerImgPath, String correctImgPath, String reason, long addTime, boolean corrAnsInvisible, float probRate) {
        this.subject = subject;

        this.problemSource = problemSource;

        this.problem = problem;

        this.wrongAnswer = wrongAnswer;

        this.correctAnswer = correctAnswer;

        this.problemImgPath = problemImgPath;

        this.wrongAnswerImgPath = wrongAnswerImgPath;

        this.correctImgPath = correctImgPath;

        this.reason = reason;

        this.addTime = addTime;

        this.corrAnsInvisible = corrAnsInvisible;

        this.probRate = probRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getProblemImgPath() {
        return problemImgPath;
    }

    public void setProblemImgPath(String problemImgPath) {
        this.problemImgPath = problemImgPath;
    }

    public String getWrongAnswerImgPath() {
        return wrongAnswerImgPath;
    }

    public void setWrongAnswerImgPath(String wrongAnswerImgPath) {
        this.wrongAnswerImgPath = wrongAnswerImgPath;
    }

    public String getCorrectImgPath() {
        return correctImgPath;
    }

    public void setCorrectImgPath(String correctImgPath) {
        this.correctImgPath = correctImgPath;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isCorrAnsInvisible() {
        return corrAnsInvisible;
    }

    public void setCorrAnsInvisible(boolean corrAnsInvisible) {
        this.corrAnsInvisible = corrAnsInvisible;
    }

    public float getProbRate() {
        return probRate;
    }

    public void setProbRate(float probRate) {
        this.probRate = probRate;
    }

    public String getProblemSource() {
        return problemSource;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
}
