package com.example.sikandaryadav.agrosist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Controller extends Activity{
    Intent i=null;
    ImageView im=null;
    EditText tv1,tv4;
    boolean flag=false;
    SQLiteDatabase db=null;
    //String name="ABC";
    //String mobile_no="6666666666";
    //String email_id="abc@gmail.com";
    //String password="666666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        im=(ImageView)findViewById(R.id.show_hide2);
        tv1=(EditText)findViewById(R.id.phone2);
        tv4=(EditText)findViewById(R.id.password2);
        db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
        db.execSQL("create table if not exists controller(name varchar,mobile_no varchar,email_id varchar,password varchar,flag varchar)");
        //db.execSQL("insert into controller values('"+name+"','"+mobile_no+"','"+email_id+"','"+password+"','nothing')");

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(flag==false)
                {
                    im.setImageResource(R.drawable.hide);
                    tv4.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    flag=true;
                }
                else
                {
                    im.setImageResource(R.drawable.show);
                    tv4.setInputType(129);
                    flag=false;

                }
            }
        });
    }

    public void action(View v)
    {
        switch(v.getId())
        {
            case R.id.start:
                String mobile_no=tv1.getText().toString();
                String password=tv4.getText().toString();
                if(mobile_no==null||mobile_no==""||mobile_no.length()<10)
                {
                    show("Please Enter Correct mobile number.");
                }
                else if(password==null||password==""||password.length()<6)
                {
                    show("Please Enter Correct Password.");
                }
                else
                {
                    Cursor c=db.rawQuery("select * from controller where mobile_no='"+mobile_no+"' and password='"+password+"'",null);
                    c.moveToFirst();
                    if(c.getCount()>0)
                    {
                        i=new Intent(this,Signin.class);
                        startActivityForResult(i,500);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        db.close();
                        finish();
                    }
                    else
                        show("Wrong Password or Mobile number.");

                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void show(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

}
