package com.example.sikandaryadav.evarnmala;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class about
  extends ActionBarActivity
{
  public void onBackPressed()
  {
    super.onBackPressed();
    Intent localIntent = new Intent(this, MainActivity.class);
    startActivity(localIntent);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903063);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131558400, paramMenu);
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    boolean bool2;
    if (paramMenuItem.getItemId() != 2131296346)
    {
      boolean bool1 = super.onOptionsItemSelected(paramMenuItem);
    }
    else
    {
      Intent localIntent = new Intent(this, MainActivity.class);
      startActivity(localIntent);
      finish();
      bool2 = true;
    }
    return bool2;
  }
}


/* Location:              E:\classes_dex2jar.jar!\com\example\sikandaryadav\evarnmala\about.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */