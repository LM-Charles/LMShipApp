package lmdelivery.longmen.com.android.api;


import lmdelivery.longmen.com.android.data.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface LMXService {
    @GET("/rest/user/{id}")
    Observable<User> getUser(@Path("id") int id);

    @POST("/rest/user/{id}")
    Observable<User> updateUser(@Path("id") int id, @Body User user);

}
