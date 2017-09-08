package com.ldt.tracklocationclient.entities;

/**
 * Created by ldt on 9/8/2017.
 */

public class ResponseEntity<T> {
    private int code;
    private String message;
    private T data;
    public ResponseEntity(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
