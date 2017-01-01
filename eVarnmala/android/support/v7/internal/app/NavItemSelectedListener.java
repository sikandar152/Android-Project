package android.support.v7.internal.app;

import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat.OnItemSelectedListener;
import android.view.View;

class NavItemSelectedListener
  implements AdapterViewCompat.OnItemSelectedListener
{
  private final ActionBar.OnNavigationListener mListener;
  
  public NavItemSelectedListener(ActionBar.OnNavigationListener paramOnNavigationListener)
  {
    this.mListener = paramOnNavigationListener;
  }
  
  public void onItemSelected(AdapterViewCompat<?> paramAdapterViewCompat, View paramView, int paramInt, long paramLong)
  {
    if (this.mListener != null) {
      this.mListener.onNavigationItemSelected(paramInt, paramLong);
    }
  }
  
  public void onNothingSelected(AdapterViewCompat<?> paramAdapterViewCompat) {}
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\app\NavItemSelectedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */