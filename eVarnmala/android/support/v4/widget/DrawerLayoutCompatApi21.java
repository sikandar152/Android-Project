package android.support.v4.widget;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;

class DrawerLayoutCompatApi21
{
  public static void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt != 3)
    {
      if (paramInt == 5) {
        localWindowInsets = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      }
    }
    else {
      localWindowInsets = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    }
    paramMarginLayoutParams.leftMargin = localWindowInsets.getSystemWindowInsetLeft();
    paramMarginLayoutParams.topMargin = localWindowInsets.getSystemWindowInsetTop();
    paramMarginLayoutParams.rightMargin = localWindowInsets.getSystemWindowInsetRight();
    paramMarginLayoutParams.bottomMargin = localWindowInsets.getSystemWindowInsetBottom();
  }
  
  public static void configureApplyInsets(View paramView)
  {
    if ((paramView instanceof DrawerLayoutImpl))
    {
      paramView.setOnApplyWindowInsetsListener(new InsetsListener());
      paramView.setSystemUiVisibility(1280);
    }
  }
  
  public static void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt != 3)
    {
      if (paramInt == 5) {
        localWindowInsets = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      }
    }
    else {
      localWindowInsets = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    }
    paramView.dispatchApplyWindowInsets(localWindowInsets);
  }
  
  public static int getTopInset(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      i = 0;
    } else {
      i = ((WindowInsets)paramObject).getSystemWindowInsetTop();
    }
    return i;
  }
  
  static class InsetsListener
    implements View.OnApplyWindowInsetsListener
  {
    public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
    {
      DrawerLayoutImpl localDrawerLayoutImpl = (DrawerLayoutImpl)paramView;
      boolean bool;
      if (paramWindowInsets.getSystemWindowInsetTop() <= 0) {
        bool = false;
      } else {
        bool = true;
      }
      localDrawerLayoutImpl.setChildInsets(paramWindowInsets, bool);
      return paramWindowInsets.consumeSystemWindowInsets();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\DrawerLayoutCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */