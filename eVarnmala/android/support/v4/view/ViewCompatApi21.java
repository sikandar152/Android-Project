package android.support.v4.view;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

class ViewCompatApi21
{
  public static float getElevation(View paramView)
  {
    return paramView.getElevation();
  }
  
  public static String getTransitionName(View paramView)
  {
    return paramView.getTransitionName();
  }
  
  public static float getTranslationZ(View paramView)
  {
    return paramView.getTranslationZ();
  }
  
  public static void requestApplyInsets(View paramView)
  {
    paramView.requestApplyInsets();
  }
  
  public static void setElevation(View paramView, float paramFloat)
  {
    paramView.setElevation(paramFloat);
  }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    paramView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
    {
      public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
      {
        WindowInsetsCompatApi21 localWindowInsetsCompatApi21 = new WindowInsetsCompatApi21(paramAnonymousWindowInsets);
        return ((WindowInsetsCompatApi21)ViewCompatApi21.this.onApplyWindowInsets(paramAnonymousView, localWindowInsetsCompatApi21)).unwrap();
      }
    });
  }
  
  public static void setTransitionName(View paramView, String paramString)
  {
    paramView.setTransitionName(paramString);
  }
  
  public static void setTranslationZ(View paramView, float paramFloat)
  {
    paramView.setTranslationZ(paramFloat);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\view\ViewCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */