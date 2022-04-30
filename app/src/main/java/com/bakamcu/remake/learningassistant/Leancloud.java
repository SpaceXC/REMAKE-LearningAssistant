package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import cn.leancloud.LCLogger;
import cn.leancloud.LCObject;
import cn.leancloud.LCUser;
import cn.leancloud.LeanCloud;

public class Leancloud extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeanCloud.setLogLevel(LCLogger.Level.ALL);
        LeanCloud.initialize(this, "1rG00zBIl97ITGRiXHTxpGa2-gzGzoHsz", "Fm77njR24SpW7RWbhJJvwvpG", "https://xcapi.mengxiblog.top");
        Log.d(TAG, "onCreate: Instantiated Leancloud!");
    }
}
