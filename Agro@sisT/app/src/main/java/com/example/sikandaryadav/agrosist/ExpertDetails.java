package com.example.sikandaryadav.agrosist;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ExpertDetails extends Activity {
    SQLiteDatabase db=null;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_details);

        tv=(TextView)findViewById(R.id.textView1);
        db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
        //db.execSQL("create table if not exists login(name varchar,mobile_no varchar,email_id varchar,password varchar,flag varchar)");
       }

    //This method will call when we click on display button
    public void display(View v)
    {

        Cursor c=db.rawQuery("select * from login", null);
        tv.setText("");

        c.moveToFirst();

        do
        {
            //we can use c.getString(0) here
            //or we can get data using column index
            String name=c.getString(c.getColumnIndex("name"));
            String mobile=c.getString(1);
            String email=c.getString(2);


            tv.append("Name:"+name+"\n Mobile:"+mobile+"\n Email:"+email+"\n\n");

        }while(c.moveToNext());
        db.close();
    }
}