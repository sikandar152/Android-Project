package com.example.sikandaryadav.agrosist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Call extends ActionBarActivity implements View.OnClickListener {
    Button expt1,expt2,expt3,expt4,expt5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        expt1=(Button) findViewById(R.id.button13);
        expt2=(Button) findViewById(R.id.button12);
        expt3=(Button) findViewById(R.id.button11);
        expt4=(Button) findViewById(R.id.button10);
        expt5=(Button) findViewById(R.id.button9);

        expt1.setOnClickListener(this);
        expt2.setOnClickListener(this);
        expt3.setOnClickListener(this);
        expt4.setOnClickListener(this);
        expt5.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v==expt1){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:09151388272"));
            startActivity(callIntent);

        }
        if(v==expt2){

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:09994687528"));
            startActivity(callIntent);
        }
        if(v==expt3){

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:09696368200"));
            startActivity(callIntent);
        }
        if(v==expt4){

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:09994858033"));
            startActivity(callIntent);
        }
        if(v==expt5){

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:9788928317"));
            startActivity(callIntent);
        }

    }
}
