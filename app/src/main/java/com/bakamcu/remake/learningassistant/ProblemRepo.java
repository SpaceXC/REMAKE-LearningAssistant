package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProblemRepo {
    private LiveData<List<Problem>> allProbLive;
    private ProblemDao problemDao;

    public  LiveData<List<Problem>> searchedProblems(String pattern){
        return problemDao.findProblemsWithPattern("%" + pattern + "%");
    }

    public ProblemRepo(Context context){
        ProblemDatabase problemDatabase = ProblemDatabase.getDatabase(context.getApplicationContext());
        problemDao = problemDatabase.getProblemDAO();
        allProbLive = problemDao.getAllProbLive();
    }

    public void insertProbs(Problem... problems) {
        //new InsertAsyncTask(problemDao).execute(problems);
        problemDao.insertProb(problems);
        Log.d(TAG, "insertProbs: " + problems);
    }

    public void updateProbs(Problem... problems) {
        //new UpdateAsyncTask(problemDao).execute(problems);
        problemDao.updateProb(problems);
    }

    public void deleteProbs(Problem... problems) {
        //new DeleteAsyncTask(problemDao).execute(problems);
        problemDao.deleteProb(problems);
    }

    public void deleteAllProbs() {
        //new DeleteAllAsyncTask(problemDao).execute();
        problemDao.deleteAllProb();
    }

    public LiveData<List<Problem>> getAllProbLive(){
        return allProbLive;
    }


}