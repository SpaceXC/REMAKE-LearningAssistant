package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.app.Application;
import android.util.Log;

import cn.leancloud.LCLogger;
import cn.leancloud.LeanCloud;

public class Leancloud extends Application {
    final static private String APP_ID = "1rG00zBIl97ITGRiXHTxpGa2-gzGzoHsz";
    final static private String APP_KEY = "Fm77njR24SpW7RWbhJJvwvpG";
    final static private String SERVER_URL = "https://xcapi.mengxiblog.top";

    @Override
    public void onCreate() {
        super.onCreate();
        LeanCloud.setLogLevel(LCLogger.Level.ALL);
        LeanCloud.initialize(this, APP_ID, APP_KEY, SERVER_URL);
        Log.d(TAG, "onCreate: Instantiated Leancloud!");
    }
}
