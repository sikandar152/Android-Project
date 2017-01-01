package com.example.sikandaryadav.agrosist;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sikandaryadav.agrosist.data.Channel;
import com.example.sikandaryadav.agrosist.data.Item;
import com.example.sikandaryadav.agrosist.service.WeatherServiceCallback;
import com.example.sikandaryadav.agrosist.service.YahooWeatherService;


public class Wearher extends ActionBarActivity implements WeatherServiceCallback {

    private ImageView weatherIconImageView;
    private TextView tempratureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private YahooWeatherService service;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearher);

        weatherIconImageView=(ImageView) findViewById(R.id.weatherIonimageView);
        tempratureTextView=(TextView) findViewById(R.id.tempratureTextView);
        conditionTextView=(TextView) findViewById(R.id.conditionTextView3);
        locationTextView=(TextView) findViewById(R.id.locationtextView);


        service= new YahooWeatherService(this);
        dialog= new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();;

        service.refreshService("Austin, TX");
    }


    @Override
    public void serviceSuccess(Channel channel) {

        Item item = channel.getItem();
        int resourcesId= getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(), null, getPackageName());
        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawable=getResources().getDrawable(resourcesId);

        weatherIconImageView.invalidateDrawable(weatherIconDrawable);

        tempratureTextView.setText(item.getCondition().getTemperature()+"\u00B0"+channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
        dialog.hide();
    }

    @Override
    public void serviceFailure(Exception e) {
        dialog.hide();
        Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
    }
}
