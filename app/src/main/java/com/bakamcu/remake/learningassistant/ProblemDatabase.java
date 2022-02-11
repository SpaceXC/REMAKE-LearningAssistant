package com.bakamcu.remake.learningassistant;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Problem.class}, version = 1, exportSchema = false)
public abstract class ProblemDatabase extends RoomDatabase{
    private static ProblemDatabase INSTANCE;
    static synchronized ProblemDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ProblemDatabase.class,"word_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract ProblemDao getProblemDAO();
}
