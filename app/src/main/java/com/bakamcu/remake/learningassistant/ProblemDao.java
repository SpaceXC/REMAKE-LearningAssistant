package com.bakamcu.remake.learningassistant;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProblemDao {
    @Insert
    void insertProb(Problem... problems);

    @Update
    void updateProb(Problem... problems);

    @Delete
    void deleteProb(Problem... problems);

    @Query("DELETE FROM PROBLEM")
    void deleteAllProb();

    @Query("SELECT * FROM PROBLEM ORDER BY ID DESC")
    LiveData<List<Problem>> getAllProbLive();

    @Query("SELECT * FROM PROBLEM WHERE problemSource LIKE :pattern ORDER BY ID DESC")
    LiveData<List<Problem>>findProblemsWithPattern(String pattern);
}
