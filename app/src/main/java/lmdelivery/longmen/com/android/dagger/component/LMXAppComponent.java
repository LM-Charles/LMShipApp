package lmdelivery.longmen.com.android.dagger.component;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import dagger.Component;
import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.activity.MainActivity;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.dagger.module.DataModule;
import lmdelivery.longmen.com.android.dagger.module.NetworkModule;
import lmdelivery.longmen.com.android.dagger.module.ZoroModule;
import lmdelivery.longmen.com.android.dagger.scope.PerApp;
import lmdelivery.longmen.com.android.fragments.LoginFragment;
import retrofit.Retrofit;

/**
 * Created by rzhu on 11/12/2015.
 */
@PerApp
@Component(
        modules = {
                ZoroModule.class,
                DataModule.class,
                NetworkModule.class
        }
)

public interface LMXAppComponent {
    Application getApplication();

    AppController getAppController();

    Gson getGson();

    OkHttpClient getOkHttpClient();

    Retrofit getRetrofit();

    SharedPreferences getSharedPreferences();

    void inject(MainActivity mainActivity);
    void inject(NewBookingActivity newBookingActivity);
    void inject(LoginFragment loginFragment);
}