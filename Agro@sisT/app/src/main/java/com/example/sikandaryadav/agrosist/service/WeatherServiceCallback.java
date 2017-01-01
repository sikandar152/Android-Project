package com.example.sikandaryadav.agrosist.service;

import com.example.sikandaryadav.agrosist.data.Channel;

/**
 * Created by Sikandar Yadav on 10-Mar-16.
 */
public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
