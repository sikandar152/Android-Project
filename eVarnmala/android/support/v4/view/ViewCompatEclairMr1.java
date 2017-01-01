package android.support.v4.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ViewCompatEclairMr1
{
  public static final String TAG = "ViewCompat";
  private static Method sChildrenDrawingOrderMethod;
  
  public static boolean isOpaque(View paramView)
  {
    return paramView.isOpaque();
  }
  
  public static void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (sChildrenDrawingOrderMethod == null) {}
    try
    {
      localObject = new Class[1];
      localObject[0] = Boolean.TYPE;
      sChildrenDrawingOrderMethod = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", (Class[])localObject);
      sChildrenDrawingOrderMethod.setAccessible(true);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      try
      {
        Method localMethod = sChildrenDrawingOrderMethod;
        Object localObject = new Object[1];
        localObject[0] = Boolean.valueOf(paramBoolean);
        localMethod.invoke(paramViewGroup, (Object[])localObject);
        return;
        localNoSuchMethodException = localNoSuchMethodException;
        Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", localNoSuchMethodException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;)
        {
          Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", localIllegalAccessException);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", localIllegalArgumentException);
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        for (;;)
        {
          Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", localInvocationTargetException);
        }
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\view\ViewCompatEclairMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */