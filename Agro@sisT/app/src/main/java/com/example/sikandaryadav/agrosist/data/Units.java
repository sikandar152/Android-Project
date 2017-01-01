package com.example.sikandaryadav.agrosist.data;

import org.json.JSONObject;

/**
 * Created by Sikandar Yadav on 10-Mar-16.
 */
public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature=data.optString("temperature");

    }
}
