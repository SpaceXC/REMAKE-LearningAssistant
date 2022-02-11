package com.bakamcu.remake.learningassistant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bakamcu.remake.learningassistant.Problem;
import com.bakamcu.remake.learningassistant.ProblemRepo;

import java.util.List;

public class ProblemsListViewModel extends AndroidViewModel {
    ProblemRepo problemRepo;
    public ProblemsListViewModel(@NonNull Application application) {
        super(application);
        problemRepo = new ProblemRepo(application);
    }

    LiveData<List<Problem>> getAllProblemssLive() {
        return problemRepo.getAllProbLive();
    }
    LiveData<List<Problem>> findProblemWithPattern(String pattern) {
        return problemRepo.searchedProblems(pattern);
    }

    void insertProbs(Problem... problems) {
        problemRepo.insertProbs(problems);
    }
    void updateProbs(Problem... problems) {
        problemRepo.updateProbs(problems);
    }
    void deleteProbs(Problem... problems) {
        problemRepo.deleteProbs(problems);
    }
    void deleteAllProbs() {
        problemRepo.deleteAllProbs();
    }


}