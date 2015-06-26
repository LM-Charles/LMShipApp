package lmdelivery.longmen.com.android;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kaiyu on 2015-06-25.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
