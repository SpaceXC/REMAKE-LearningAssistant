package com.bakamcu.remake.learningassistant;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Problem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    String probSRC = "";
    String probDesc = "";
    String probWrongAns = "";
    String probCorrAns = "";
    String probImgPath = "";
    String wrongAnsImgPath = "";
    String corrImgPath = "";
    String reason = "";
    boolean corrAnsInvisible = false;
    float probRate = 0;

    public Problem(String probSRC, String probDesc, String probWrongAns, String probCorrAns, String probImgPath, String wrongAnsImgPath, String corrImgPath, String reason, float probRate) {
        this.probSRC = probSRC;
        this.probDesc = probDesc;
        this.probWrongAns = probWrongAns;
        this.probCorrAns = probCorrAns;
        this.probImgPath = probImgPath;
        this.wrongAnsImgPath = wrongAnsImgPath;
        this.corrImgPath = corrImgPath;
        this.reason = reason;
        this.probRate = probRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProbSRC() {
        return probSRC;
    }

    public void setProbSRC(String probSRC) {
        this.probSRC = probSRC;
    }

    public String getProbDesc() {
        return probDesc;
    }

    public void setProbDesc(String probDesc) {
        this.probDesc = probDesc;
    }

    public String getProbWrongAns() {
        return probWrongAns;
    }

    public void setProbWrongAns(String probWrongAns) {
        this.probWrongAns = probWrongAns;
    }

    public String getProbCorrAns() {
        return probCorrAns;
    }

    public void setProbCorrAns(String probCorrAns) {
        this.probCorrAns = probCorrAns;
    }

    public String getProbImgPath() {
        return probImgPath;
    }

    public void setProbImgPath(String probImgPath) {
        this.probImgPath = probImgPath;
    }

    public String getWrongAnsImgPath() {
        return wrongAnsImgPath;
    }

    public void setWrongAnsImgPath(String wrongAnsImgPath) {
        this.wrongAnsImgPath = wrongAnsImgPath;
    }

    public String getCorrImgPath() {
        return corrImgPath;
    }

    public void setCorrImgPath(String corrImgPath) {
        this.corrImgPath = corrImgPath;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public float getProbRate() {
        return probRate;
    }

    public void setProbRate(float probRate) {
        this.probRate = probRate;
    }

    public boolean isCorrAnsInvisible() {
        return corrAnsInvisible;
    }

    public void setCorrAnsInvisible(boolean corrAnsInvisible) {
        this.corrAnsInvisible = corrAnsInvisible;
    }
}
