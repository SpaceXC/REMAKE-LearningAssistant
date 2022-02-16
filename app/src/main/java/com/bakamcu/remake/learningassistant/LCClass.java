package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.app.Application;
import android.util.Log;

import cn.leancloud.LCObject;
import cn.leancloud.LeanCloud;

public class LCClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeanCloud.initialize(this, "1rG00zBIl97ITGRiXHTxpGa2-gzGzoHsz", "Fm77njR24SpW7RWbhJJvwvpG", "https://1rg00zbi.lc-cn-n1-shared.com");    //https://1rg00zbi.lc-cn-n1-shared.com
        Log.d(TAG, "onCreate: Instantiated Leancloud!");
    }
}
