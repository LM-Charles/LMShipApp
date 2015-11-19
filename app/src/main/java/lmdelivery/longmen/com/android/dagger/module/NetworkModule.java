package lmdelivery.longmen.com.android.dagger.module;

import android.app.Application;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import lmdelivery.longmen.com.android.api.LMXService;
import lmdelivery.longmen.com.android.dagger.scope.PerApp;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class NetworkModule {

    static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);

    @Provides
    @PerApp
    OkHttpClient provideOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);
        client.networkInterceptors().add(new StethoInterceptor());
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }

    private static final String END_POINT = "https://ws.test.paygateway.com";


    @Provides
    @PerApp
    GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @PerApp
    Retrofit provideRetrofit(GsonConverterFactory gsonConverterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(END_POINT)
                .addConverterFactory(gsonConverterFactory)
                .build();
    }


    @Provides
    @PerApp
    LMXService provideLMXService(Retrofit retrofit) {
        return retrofit.create(LMXService.class);
    }

}
