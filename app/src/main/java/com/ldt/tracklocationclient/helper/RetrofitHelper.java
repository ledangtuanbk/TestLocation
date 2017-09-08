package com.ldt.tracklocationclient.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.interfaces.UserService;
import com.ldt.tracklocationclient.utilities.AppConst;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ldt on 9/8/2017.
 */

public class RetrofitHelper {

    public  static Retrofit getInstance(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConst.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
}
