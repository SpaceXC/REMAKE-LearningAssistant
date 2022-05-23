package com.bakamcu.remake.learningassistant;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProblemRepo {
    private final LiveData<List<Problem>> allProbLive;
    private final ProblemDao problemDao;

    public ProblemRepo(Context context) {
        ProblemDatabase problemDatabase = ProblemDatabase.getDatabase(context.getApplicationContext());
        problemDao = problemDatabase.getProblemDAO();
        allProbLive = problemDao.getAllProbLive();
    }

    public void insertProbs(Problem... problems) {
        problemDao.insertProb(problems);
    }

    public void deleteProbs(Problem... problems) {
        problemDao.deleteProb(problems);
    }

    public LiveData<List<Problem>> getAllProbLive(){
        return allProbLive;
    }


}