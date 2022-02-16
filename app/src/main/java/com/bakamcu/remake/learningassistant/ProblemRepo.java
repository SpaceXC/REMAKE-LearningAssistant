package com.bakamcu.remake.learningassistant;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProblemRepo {
    private final LiveData<List<Problem>> allProbLive;
    private final ProblemDao problemDao;

    public LiveData<List<Problem>> searchedProblems(String pattern) {
        return problemDao.findProblemsWithPattern("%" + pattern + "%");
    }

    public ProblemRepo(Context context) {
        ProblemDatabase problemDatabase = ProblemDatabase.getDatabase(context.getApplicationContext());
        problemDao = problemDatabase.getProblemDAO();
        allProbLive = problemDao.getAllProbLive();
    }

    public void insertProbs(Problem... problems) {
        problemDao.insertProb(problems);
    }

    public void updateProbs(Problem... problems) {
        problemDao.updateProb(problems);
    }

    public void deleteProbs(Problem... problems) {
        problemDao.deleteProb(problems);
    }

    public void deleteAllProbs() {
        problemDao.deleteAllProb();
    }

    public LiveData<List<Problem>> getAllProbLive(){
        return allProbLive;
    }


}