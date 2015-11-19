package lmdelivery.longmen.com.android.dagger.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import lmdelivery.longmen.com.android.dagger.scope.PerApp;

@Module
public class DataModule {

  @Provides
  @PerApp
  SharedPreferences provideSharedPrefs(Application app) {
    return PreferenceManager.getDefaultSharedPreferences(app);
  }

  @Provides
  @PerApp Gson provideGson() {
    return new Gson();
  }


}
