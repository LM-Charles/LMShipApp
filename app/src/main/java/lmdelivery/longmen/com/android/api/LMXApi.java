package lmdelivery.longmen.com.android.api;


import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.data.TrackingDetail;
import lmdelivery.longmen.com.android.data.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface LMXApi {
    @GET("/rest/user/{id}")
    Observable<User> getUser(@Path("id") int id, @Query("authId") int authId, @Query("token") String token);

    @POST("/rest/user/{id}")
    Observable<User> updateUser(@Path("id") int id, @Query("authId") int authId, @Query("token") String token, @Body User user);

    @GET("/rest/order")
    Observable<ArrayList<TrackingDetail>> getOrderByUser(@Query("authId") int authId, @Query("userId") int id, @Query("token") String token, @Query("limit") int limit, @Query("offset") int offset);

}
