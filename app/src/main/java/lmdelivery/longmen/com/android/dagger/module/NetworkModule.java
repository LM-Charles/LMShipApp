package lmdelivery.longmen.com.android.dagger.module;

import android.app.Application;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.api.GoogleAPI;
import lmdelivery.longmen.com.android.api.LMXApi;
import lmdelivery.longmen.com.android.dagger.scope.PerApp;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

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
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(logging);
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }

    @Provides
    @Named("LMX")
    @PerApp
    Retrofit provideLMXRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constant.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                return new Date(json.getAsJsonPrimitive().getAsLong());
                            }
                        })
                        .create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Named("Google")
    @PerApp
    Retrofit provideGoogleRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constant.ENDPOINT_GOOGLE)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                return new Date(json.getAsJsonPrimitive().getAsLong());
                            }
                        })
                        .create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }


    @Provides
    @PerApp
    LMXApi provideLMXService(@Named("LMX") Retrofit retrofit) {
        return retrofit.create(LMXApi.class);
    }

    @Provides
    @PerApp
    GoogleAPI provideGoogleService(@Named("Google") Retrofit retrofit) {
        return retrofit.create(GoogleAPI.class);
    }

}
