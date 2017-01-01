package com.example.sikandaryadav.agrosist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class Module extends ActionBarActivity implements View.OnClickListener {

    private ImageButton btn1;
    private ImageButton btn2;
    private ImageButton btn3;
    private ImageButton btn4;
    private ImageButton btn5;
    private ImageButton btn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        btn1=(ImageButton) findViewById(R.id.imageButton);
        btn2=(ImageButton) findViewById(R.id.imageButton2);
        btn3=(ImageButton) findViewById(R.id.imageButton3);
        btn4=(ImageButton) findViewById(R.id.imageButton4);
        btn5=(ImageButton) findViewById(R.id.imageButton5);
        btn6=(ImageButton) findViewById(R.id.imageButton6);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);


    }


    @Override

    public void onClick(View v) {
        //Intent intent = new Intent(this, Wearher.class);
        if(v == btn1){

            Uri uri=Uri.parse("http://farmer.gov.in/#");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
        if(v == btn2){
            //startActivity(new Intent(Module.this,Wearher.class));
            Uri uri=Uri.parse("https://weather.yahoo.com");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }

        if(v == btn3){
            startActivity(new Intent(Module.this,Expert1.class));
            //Uri uri=Uri.parse("https://weather.yahoo.com");
            //Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            //startActivity(intent);
        }
        if(v == btn4){
            //startActivity(new Intent(Module.this,Wearher.class));
            Uri uri=Uri.parse("http://agmarknet.dac.gov.in/MarketingBoards/Default.aspx");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
        if(v == btn5){
            //startActivity(new Intent(Module.this,Wearher.class));
            Uri uri=Uri.parse("http://m.businesstoday.in/category/agriculture/1/15.html");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
        if(v == btn6){
            startActivity(new Intent(Module.this,Controller.class));
            //Uri uri=Uri.parse("https://weather.yahoo.com");
            //Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            //startActivity(intent);
        }
   }
}
