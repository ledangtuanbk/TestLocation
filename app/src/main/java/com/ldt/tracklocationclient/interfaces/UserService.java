package com.ldt.tracklocationclient.interfaces;

import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.TestUserEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ldt on 9/8/2017.
 */

public interface UserService {
    @PUT("Test/CreateUserLocation")
    Call<ResponseEntity<UserLocationEntity>> createUserLocation(@Body UserLocationEntity user);

    @GET("Test/GetUserLocation/{userId}")
    Call<ResponseEntity<List<UserLocationEntity>>> getUserLocation(@Path("userId") String userId, @Query("start") long start, @Query("end") long end);

    @PUT("Test/loginOrRegister/{userId}")
    Call<ResponseEntity<TestUserEntity>> loginOrRegister(@Path("userId") String userId);

    @GET("Test/Users")
    Call<ResponseEntity<List<TestUserEntity>>> getAllUsers();
}

