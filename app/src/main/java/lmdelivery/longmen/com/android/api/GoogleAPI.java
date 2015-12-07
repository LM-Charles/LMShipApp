package lmdelivery.longmen.com.android.api;

import lmdelivery.longmen.com.android.data.googleAPI.GeocodingResult;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by rufus on 2015-12-06.
 */
public interface GoogleAPI {
    @GET("maps/api/geocode/json")
    Observable<GeocodingResult> geocoding(@Query("address") String address, @Query("key") String key);
}
