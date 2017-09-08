package com.ldt.tracklocationclient.interfaces;

import com.ldt.tracklocationclient.entities.ResponseEntity;

/**
 * Created by ldt on 9/8/2017.
 */

public interface IResponse<T> {

    void onResponse(ResponseEntity<T> response);

    void onFailure(Throwable t);
}
