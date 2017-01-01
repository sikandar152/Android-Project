package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.ViewConfiguration;

public class ViewConfigurationCompat
{
  static final ViewConfigurationVersionImpl IMPL;
  
  static
  {
    if (Build.VERSION.SDK_INT < 14)
    {
      if (Build.VERSION.SDK_INT < 11)
      {
        if (Build.VERSION.SDK_INT < 8) {
          IMPL = new BaseViewConfigurationVersionImpl();
        } else {
          IMPL = new FroyoViewConfigurationVersionImpl();
        }
      }
      else {
        IMPL = new HoneycombViewConfigurationVersionImpl();
      }
    }
    else {
      IMPL = new IcsViewConfigurationVersionImpl();
    }
  }
  
  public static int getScaledPagingTouchSlop(ViewConfiguration paramViewConfiguration)
  {
    return IMPL.getScaledPagingTouchSlop(paramViewConfiguration);
  }
  
  public static boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration)
  {
    return IMPL.hasPermanentMenuKey(paramViewConfiguration);
  }
  
  static class IcsViewConfigurationVersionImpl
    extends ViewConfigurationCompat.HoneycombViewConfigurationVersionImpl
  {
    public boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration)
    {
      return ViewConfigurationCompatICS.hasPermanentMenuKey(paramViewConfiguration);
    }
  }
  
  static class HoneycombViewConfigurationVersionImpl
    extends ViewConfigurationCompat.FroyoViewConfigurationVersionImpl
  {
    public boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration)
    {
      return false;
    }
  }
  
  static class FroyoViewConfigurationVersionImpl
    extends ViewConfigurationCompat.BaseViewConfigurationVersionImpl
  {
    public int getScaledPagingTouchSlop(ViewConfiguration paramViewConfiguration)
    {
      return ViewConfigurationCompatFroyo.getScaledPagingTouchSlop(paramViewConfiguration);
    }
  }
  
  static class BaseViewConfigurationVersionImpl
    implements ViewConfigurationCompat.ViewConfigurationVersionImpl
  {
    public int getScaledPagingTouchSlop(ViewConfiguration paramViewConfiguration)
    {
      return paramViewConfiguration.getScaledTouchSlop();
    }
    
    public boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration)
    {
      return true;
    }
  }
  
  static abstract interface ViewConfigurationVersionImpl
  {
    public abstract int getScaledPagingTouchSlop(ViewConfiguration paramViewConfiguration);
    
    public abstract boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\view\ViewConfigurationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */