package com.bakamcu.remake.learningassistant;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Problem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    String subject;
    String problemSource;
    String problem;
    String wrongAnswer;
    String correctAnswer;
    String problemImgPath;
    String wrongAnswerImgPath;
    String correctImgPath;
    String reason;
    String addTime;
    boolean corrAnsInvisible;
    String probRate = "0";

    public Problem(String subject, String problemSource, String problem, String wrongAnswer, String correctAnswer, String problemImgPath, String wrongAnswerImgPath, String correctImgPath, String reason, String addTime, boolean corrAnsInvisible, String probRate) {
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


    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getProblemImgPath() {
        return problemImgPath;
    }

    public String getWrongAnswerImgPath() {
        return wrongAnswerImgPath;
    }

    public String getCorrectImgPath() {
        return correctImgPath;
    }

    public String getReason() {
        return reason;
    }

    public boolean isCorrAnsInvisible() {
        return corrAnsInvisible;
    }

    public String getProbRate() {
        return probRate;
    }

    public String getProblemSource() {
        return problemSource;
    }

    public String getAddTime() {
        return addTime;
    }

}
