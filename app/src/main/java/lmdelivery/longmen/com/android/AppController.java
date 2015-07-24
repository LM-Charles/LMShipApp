package lmdelivery.longmen.com.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kaiyu on 2015-06-25.
 */
public class AppController extends Application {

    private static Context context;
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;

    private RequestQueue mRequestQueue;

    public void onCreate(){
        super.onCreate();
        AppController.context = getApplicationContext();
        mInstance = this;
    }

    public static Context getAppContext() {
        return AppController.context;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public SharedPreferences getDefaultSharePreferences(){
        return getSharedPreferences(Constant.SHARE_NAME, MODE_PRIVATE);
    }

    public String getUserId(){
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_ID, "");
    }

    public String getUserToken(){
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_TOKEN,"");
    }

    public String getUserPhone(){
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_PHONE,"");
    }

    public String getUserEmail(){
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_EMAIL,"");
    }
}
