package android.support.v4.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

class ActionBarDrawerToggleJellybeanMR2
{
  private static final String TAG = "ActionBarDrawerToggleImplJellybeanMR2";
  private static final int[] THEME_ATTRS;
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16843531;
    THEME_ATTRS = arrayOfInt;
  }
  
  public static Drawable getThemeUpIndicator(Activity paramActivity)
  {
    Object localObject = paramActivity.getActionBar();
    if (localObject == null) {
      localObject = paramActivity;
    } else {
      localObject = ((ActionBar)localObject).getThemedContext();
    }
    TypedArray localTypedArray = ((Context)localObject).obtainStyledAttributes(null, THEME_ATTRS, 16843470, 0);
    localObject = localTypedArray.getDrawable(0);
    localTypedArray.recycle();
    return (Drawable)localObject;
  }
  
  public static Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
  {
    ActionBar localActionBar = paramActivity.getActionBar();
    if (localActionBar != null) {
      localActionBar.setHomeActionContentDescription(paramInt);
    }
    return paramObject;
  }
  
  public static Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
  {
    ActionBar localActionBar = paramActivity.getActionBar();
    if (localActionBar != null)
    {
      localActionBar.setHomeAsUpIndicator(paramDrawable);
      localActionBar.setHomeActionContentDescription(paramInt);
    }
    return paramObject;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\ActionBarDrawerToggleJellybeanMR2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */