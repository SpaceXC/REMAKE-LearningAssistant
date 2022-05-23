package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Problem类相关的viewmodel
 */

public class ProblemsListViewModel extends AndroidViewModel {
    ProblemRepo problemRepo;

    public ProblemsListViewModel(@NonNull Application application) {
        super(application);
        problemRepo = new ProblemRepo(application);
    }

    LiveData<List<Problem>> getAllProblemsLive() {
        return problemRepo.getAllProbLive();
    }

    void insertProbs(Problem... problems) {
        problemRepo.insertProbs(problems);
    }

    void deleteProbs(Problem... problems) {
        problemRepo.deleteProbs(problems);
    }

    void getAllProblems(String queryName, String query, MutableLiveData<List<Problem>> listObject) {
        LCQuery<LCObject> LeanCloudQuery = new LCQuery<>("Problems");
        List<Problem> tempList = new ArrayList<>();
        LeanCloudQuery.whereContains(queryName, query);
        LeanCloudQuery.whereEqualTo("user", LCUser.getCurrentUser());
        LeanCloudQuery.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(List<LCObject> LCProblems) {
                for (LCObject object : LCProblems) {
                    Problem temp = new Problem(
                            (String) object.get("subject"),
                            (String) object.get("problemSource"),
                            (String) object.get("problem"),
                            (String) object.get("wrongAnswer"),
                            (String) object.get("correctAnswer"),
                            (String) object.get("problemImagePath"),
                            (String) object.get("wrongAnswerImagePath"),
                            (String) object.get("correctAnswerImagePath"),
                            (String) object.get("reason"),
                            (String) object.get("addTime"),
                            (String) object.get("addTimeStamp"),
                            false,
                            (String) object.get("probRate")
                    );
                    temp.setProblemID(object.getObjectId());
                    tempList.add(temp);
                    Log.d(TAG, "onNext: " + tempList.size());
                }
                //Collections.reverse(tempList);
                Comparator<Problem> comparator = (problem, t1) -> {
                    long time1 = Long.parseLong(problem.updateTimeStamp);
                    Log.d(TAG, "compare: " + problem.problemSource + " " + time1);
                    long time2 = Long.parseLong(t1.updateTimeStamp);
                    Log.d(TAG, "compare: " + problem.problemSource + " " + time2);

                    if (time1 < time2) {
                        return 1;
                    } else if (time1 > time2) {
                        return -1;
                    }

                    return 0;
                };
                //Collections.sort(tempList);
                tempList.sort(comparator);
                listObject.setValue(tempList);
            }

            public void onError(Throwable throwable) {
                Toast.makeText(getApplication(), "无法获取数据!", Toast.LENGTH_SHORT).show();
            }

            public void onComplete() {
            }
        });


    }

    public LCObject BuildLeanCloudObject(Problem problem) {
        LCObject problemLC = new LCObject("Problems");
        problemLC.put("subject", problem.subject);
        problemLC.put("problemSource", problem.problemSource);
        problemLC.put("problem", problem.problem);
        problemLC.put("problemImagePath", problem.getProblemImgPath());
        problemLC.put("wrongAnswer", problem.wrongAnswer);
        problemLC.put("wrongAnswerImagePath", problem.getWrongAnswerImgPath());
        problemLC.put("correctAnswer", problem.correctAnswer);
        problemLC.put("correctAnswerImagePath", problem.getCorrectImgPath());
        problemLC.put("reason", problem.reason);
        problemLC.put("probRate", problem.probRate);
        problemLC.put("user", LCUser.getCurrentUser());
        problemLC.put("addTime", getCurrentTime());
        problemLC.put("addTimeStamp", problem.updateTimeStamp);
        return problemLC;
    }

    public String getCurrentTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }
}