package com.example.sikandaryadav.agrosist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Expert1 extends ActionBarActivity implements View.OnClickListener {
    Button call,query,record,send,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert1);
        call=(Button) findViewById(R.id.button4);
        query=(Button) findViewById(R.id.button5);
        record=(Button) findViewById(R.id.button6);
        send=(Button) findViewById(R.id.button8);
        login=(Button) findViewById(R.id.button7);

        call.setOnClickListener(this);
        query.setOnClickListener(this);
        record.setOnClickListener(this);
        send.setOnClickListener(this);
        login.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expert1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v==call)
        {
            startActivity(new Intent(Expert1.this,Call.class));
        }
        if(v==query)
        {
            startActivity(new Intent(Expert1.this,ExpertDetails.class));
        }
        if(v==record)
        {
            startActivity(new Intent(Expert1.this,Welcome.class));
        }
        if(v==send)
        {
            startActivity(new Intent(Expert1.this,VoiceSend.class));
        }
        if(v==login){
            startActivity(new Intent(Expert1.this,LoginExp.class));
        }
    }
}
