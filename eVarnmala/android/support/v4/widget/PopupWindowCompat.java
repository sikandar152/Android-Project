package android.support.v4.widget;

import android.os.Build.VERSION;
import android.view.View;
import android.widget.PopupWindow;

public class PopupWindowCompat
{
  static final PopupWindowImpl IMPL;
  
  static
  {
    if (Build.VERSION.SDK_INT < 19) {
      IMPL = new BasePopupWindowImpl();
    } else {
      IMPL = new KitKatPopupWindowImpl();
    }
  }
  
  public static void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    IMPL.showAsDropDown(paramPopupWindow, paramView, paramInt1, paramInt2, paramInt3);
  }
  
  static class KitKatPopupWindowImpl
    extends PopupWindowCompat.BasePopupWindowImpl
  {
    public void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      PopupWindowCompatKitKat.showAsDropDown(paramPopupWindow, paramView, paramInt1, paramInt2, paramInt3);
    }
  }
  
  static class BasePopupWindowImpl
    implements PopupWindowCompat.PopupWindowImpl
  {
    public void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      paramPopupWindow.showAsDropDown(paramView, paramInt1, paramInt2);
    }
  }
  
  static abstract interface PopupWindowImpl
  {
    public abstract void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\PopupWindowCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */