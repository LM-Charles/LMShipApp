package lmdelivery.longmen.com.android.dagger.module;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.dagger.scope.PerApp;

@Module
public class ZoroModule {

  final AppController app;

  public ZoroModule(AppController app) {
    this.app = app;
  }

  @Provides
  @PerApp
  AppController provideAppController() {
    return app;
  }

  @Provides
  @PerApp Application provideApplication(AppController app) {
    return app;
  }

}
