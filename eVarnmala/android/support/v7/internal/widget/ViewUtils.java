package android.support.v7.internal.widget;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewUtils
{
  private static final String TAG = "ViewUtils";
  private static Method sComputeFitSystemWindowsMethod;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 18) {}
    try
    {
      Class[] arrayOfClass = new Class[2];
      arrayOfClass[0] = Rect.class;
      arrayOfClass[1] = Rect.class;
      sComputeFitSystemWindowsMethod = View.class.getDeclaredMethod("computeFitSystemWindows", arrayOfClass);
      if (!sComputeFitSystemWindowsMethod.isAccessible()) {
        sComputeFitSystemWindowsMethod.setAccessible(true);
      }
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        Log.d("ViewUtils", "Could not find method computeFitSystemWindows. Oh well.");
      }
    }
  }
  
  public static int combineMeasuredStates(int paramInt1, int paramInt2)
  {
    return paramInt1 | paramInt2;
  }
  
  public static void computeFitSystemWindows(View paramView, Rect paramRect1, Rect paramRect2)
  {
    if (sComputeFitSystemWindowsMethod != null) {}
    try
    {
      Method localMethod = sComputeFitSystemWindowsMethod;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramRect1;
      arrayOfObject[1] = paramRect2;
      localMethod.invoke(paramView, arrayOfObject);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.d("ViewUtils", "Could not invoke computeFitSystemWindows", localException);
      }
    }
  }
  
  public static boolean isLayoutRtl(View paramView)
  {
    int i = 1;
    if (ViewCompat.getLayoutDirection(paramView) != i) {
      i = 0;
    }
    return i;
  }
  
  public static void makeOptionalFitsSystemWindows(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {}
    try
    {
      Method localMethod = paramView.getClass().getMethod("makeOptionalFitsSystemWindows", new Class[0]);
      if (!localMethod.isAccessible()) {
        localMethod.setAccessible(true);
      }
      localMethod.invoke(paramView, new Object[0]);
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        Log.d("ViewUtils", "Could not find method makeOptionalFitsSystemWindows. Oh well...");
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", localInvocationTargetException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", localIllegalAccessException);
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ViewUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */