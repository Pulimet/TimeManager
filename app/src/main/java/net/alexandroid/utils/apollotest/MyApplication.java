package net.alexandroid.utils.apollotest;

import android.app.Application;

import net.alexandroid.shpref.ShPref;
import net.alexandroid.utils.mylog.MyLog;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShPref.init(this, ShPref.APPLY);
        MyLog.setTag("ZAQ");
    }
}
