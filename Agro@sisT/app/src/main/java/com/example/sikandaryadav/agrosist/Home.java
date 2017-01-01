package com.example.sikandaryadav.agrosist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Home extends ActionBarActivity implements View.OnClickListener {

    private Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        proceed=(Button) findViewById(R.id.button2);
        proceed.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Language.class);
        startActivity(intent);
        finish();
    }
}
