package com.example.sikandaryadav.agrosist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Language extends ActionBarActivity implements View.OnClickListener {

    private Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        proceed=(Button) findViewById(R.id.proceed);

        proceed.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v==proceed) {
            startActivity(new Intent(Language.this,Profile.class));
        }

    }
}
