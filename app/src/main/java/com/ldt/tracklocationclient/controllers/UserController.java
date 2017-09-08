package com.ldt.tracklocationclient.controllers;

import android.util.Log;

import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.TestUserEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;
import com.ldt.tracklocationclient.helper.RetrofitHelper;
import com.ldt.tracklocationclient.interfaces.IResponse;
import com.ldt.tracklocationclient.interfaces.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ldt on 9/8/2017.
 */

public class UserController<T> {

    private final String TAG = UserController.class.getSimpleName();
    private UserService service;
    private Call<ResponseEntity<T>> call;

    private void init(){
        service = RetrofitHelper.getInstance().create(UserService.class);
    }

    private void getResponse(Call<ResponseEntity<T>> call, final IResponse<T> iResponse){
        call.enqueue(new Callback<ResponseEntity<T>>() {
            @Override
            public void onResponse(Call<ResponseEntity<T>> call, Response<ResponseEntity<T>> response) {
                if(response.isSuccessful()){
                    iResponse.onResponse(response.body());
                }else {
                    iResponse.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity<T>> call, Throwable t) {
                iResponse.onFailure(t);
            }
        });
    }

    public void loginOrRegister(String userId, final IResponse<TestUserEntity> iResponse){
        init();
        Call<ResponseEntity<TestUserEntity>> call = service.loginOrRegister(userId);
        call.enqueue(new Callback<ResponseEntity<TestUserEntity>>() {
            @Override
            public void onResponse(Call<ResponseEntity<TestUserEntity>> call, Response<ResponseEntity<TestUserEntity>> response) {
                if(response.isSuccessful()){
                    iResponse.onResponse(response.body());
                }else {
                    iResponse.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity<TestUserEntity>> call, Throwable t) {
                iResponse.onFailure(t);
            }
        });
    }

    public void createUserLocation(UserLocationEntity entity, final IResponse<UserLocationEntity> iResponse){
        init();
        Call<ResponseEntity<UserLocationEntity>> call = service.createUserLocation(entity);
        call.enqueue(new Callback<ResponseEntity<UserLocationEntity>>() {
            @Override
            public void onResponse(Call<ResponseEntity<UserLocationEntity>> call, Response<ResponseEntity<UserLocationEntity>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: isSuccessful");
                    iResponse.onResponse(response.body());
                }else {
                    Log.d(TAG, "onResponse: not isSuccessful " + response.message());
                    iResponse.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity<UserLocationEntity>> call, Throwable t) {
                iResponse.onFailure(t);
            }
        });
    }

    public void getUserLocation(String userId, final IResponse<List<UserLocationEntity>> iResponse){
        init();
        Call<ResponseEntity<List<UserLocationEntity>>> call = service.getUserLocation(userId);
        call.enqueue(new Callback<ResponseEntity<List<UserLocationEntity>>>() {
            @Override
            public void onResponse(Call<ResponseEntity<List<UserLocationEntity>>> call, Response<ResponseEntity<List<UserLocationEntity>>> response) {
                if(response.isSuccessful()){
                    iResponse.onResponse(response.body());
                }else {
                    iResponse.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity<List<UserLocationEntity>>> call, Throwable t) {
                iResponse.onFailure(t);
            }
        });
    }

    public void getAllUsers(final IResponse<List<TestUserEntity>> iResponse){
        init();
        Call<ResponseEntity<List<TestUserEntity>>> call = service.getAllUsers();
        call.enqueue(new Callback<ResponseEntity<List<TestUserEntity>>>() {
            @Override
            public void onResponse(Call<ResponseEntity<List<TestUserEntity>>> call, Response<ResponseEntity<List<TestUserEntity>>> response) {
                if(response.isSuccessful()){
                    iResponse.onResponse(response.body());
                }else {
                    iResponse.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity<List<TestUserEntity>>> call, Throwable t) {
                iResponse.onFailure(t);
            }
        });
    }
}
