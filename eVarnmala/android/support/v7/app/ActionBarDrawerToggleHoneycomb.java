package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.lang.reflect.Method;

class ActionBarDrawerToggleHoneycomb
{
  private static final String TAG = "ActionBarDrawerToggleHoneycomb";
  private static final int[] THEME_ATTRS;
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16843531;
    THEME_ATTRS = arrayOfInt;
  }
  
  public static Drawable getThemeUpIndicator(Activity paramActivity)
  {
    TypedArray localTypedArray = paramActivity.obtainStyledAttributes(THEME_ATTRS);
    Drawable localDrawable = localTypedArray.getDrawable(0);
    localTypedArray.recycle();
    return localDrawable;
  }
  
  public static SetIndicatorInfo setActionBarDescription(SetIndicatorInfo paramSetIndicatorInfo, Activity paramActivity, int paramInt)
  {
    if (paramSetIndicatorInfo == null) {
      paramSetIndicatorInfo = new SetIndicatorInfo(paramActivity);
    }
    if (paramSetIndicatorInfo.setHomeAsUpIndicator != null) {}
    try
    {
      ActionBar localActionBar = paramActivity.getActionBar();
      Method localMethod = paramSetIndicatorInfo.setHomeActionContentDescription;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt);
      localMethod.invoke(localActionBar, arrayOfObject);
      if (Build.VERSION.SDK_INT <= 19) {
        localActionBar.setSubtitle(localActionBar.getSubtitle());
      }
      return paramSetIndicatorInfo;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set content description via JB-MR2 API", localException);
      }
    }
  }
  
  public static SetIndicatorInfo setActionBarUpIndicator(SetIndicatorInfo paramSetIndicatorInfo, Activity paramActivity, Drawable paramDrawable, int paramInt)
  {
    SetIndicatorInfo localSetIndicatorInfo = new SetIndicatorInfo(paramActivity);
    if (localSetIndicatorInfo.setHomeAsUpIndicator != null) {}
    for (;;)
    {
      try
      {
        ActionBar localActionBar = paramActivity.getActionBar();
        Object localObject1 = localSetIndicatorInfo.setHomeAsUpIndicator;
        Object localObject2 = new Object[1];
        localObject2[0] = paramDrawable;
        ((Method)localObject1).invoke(localActionBar, (Object[])localObject2);
        localObject2 = localSetIndicatorInfo.setHomeActionContentDescription;
        localObject1 = new Object[1];
        localObject1[0] = Integer.valueOf(paramInt);
        ((Method)localObject2).invoke(localActionBar, (Object[])localObject1);
        return localSetIndicatorInfo;
      }
      catch (Exception localException)
      {
        Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator via JB-MR2 API", localException);
        continue;
      }
      if (localSetIndicatorInfo.upIndicatorView != null) {
        localSetIndicatorInfo.upIndicatorView.setImageDrawable(paramDrawable);
      } else {
        Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator");
      }
    }
  }
  
  static class SetIndicatorInfo
  {
    public Method setHomeActionContentDescription;
    public Method setHomeAsUpIndicator;
    public ImageView upIndicatorView;
    
    SetIndicatorInfo(Activity paramActivity)
    {
      label122:
      for (;;)
      {
        Object localObject2;
        try
        {
          localObject1 = new Class[1];
          localObject1[0] = Drawable.class;
          this.setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", (Class[])localObject1);
          localObject1 = new Class[1];
          localObject1[0] = Integer.TYPE;
          this.setHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", (Class[])localObject1);
          return;
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          localObject1 = paramActivity.findViewById(16908332);
          if (localObject1 == null) {
            continue;
          }
          localObject2 = (ViewGroup)((View)localObject1).getParent();
          if (((ViewGroup)localObject2).getChildCount() != 2) {
            continue;
          }
          localObject1 = ((ViewGroup)localObject2).getChildAt(0);
          localObject2 = ((ViewGroup)localObject2).getChildAt(1);
          if (((View)localObject1).getId() != 16908332) {}
        }
        for (Object localObject1 = localObject2;; localObject1 = localObject1)
        {
          if (!(localObject1 instanceof ImageView)) {
            break label122;
          }
          this.upIndicatorView = ((ImageView)localObject1);
          break;
        }
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\ActionBarDrawerToggleHoneycomb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */