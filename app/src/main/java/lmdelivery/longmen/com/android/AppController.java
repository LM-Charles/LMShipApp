package lmdelivery.longmen.com.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;

import lmdelivery.longmen.com.android.dagger.component.DaggerLMXAppComponent;
import lmdelivery.longmen.com.android.dagger.component.LMXAppComponent;
import lmdelivery.longmen.com.android.dagger.module.ZoroModule;
import timber.log.Timber;

/**
 * Created by Kaiyu on 2015-06-25.
 */
public class AppController extends com.activeandroid.app.Application {

    private static Context context;
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private static LMXAppComponent component;
    private RequestQueue mRequestQueue;

    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        AppController.context = getApplicationContext();
        mInstance = this;
        component = DaggerLMXAppComponent.builder()
                .zoroModule(new ZoroModule(this)).build();

        //debug only
        Stetho.initializeWithDefaults(this);
        Timber.plant(new Timber.DebugTree() {
            //add line number to the tag
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return super.createStackElementTag(element) + ':' + element.getLineNumber();
            }
        });
    }

    public static LMXAppComponent getComponent() {
        return component;
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
        req.setRetryPolicy(new DefaultRetryPolicy(15000, //15 seconds time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(10000, //10 seconds time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public SharedPreferences getDefaultSharePreferences() {
        return getSharedPreferences(Constant.SHARE_NAME, MODE_PRIVATE);
    }

    public int getUserId() {
        return getDefaultSharePreferences().getInt(Constant.SHARE_USER_ID, -1);
    }

    public String getUserToken() {
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_TOKEN, "");
    }

    public String getUserPhone() {
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_PHONE, "");
    }

    public String getUserEmail() {
        return getDefaultSharePreferences().getString(Constant.SHARE_USER_EMAIL, "");
    }

    public boolean isUserActivated() {
        return getDefaultSharePreferences().getBoolean(Constant.SHARE_IS_USER_ACTIVATED, false);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
