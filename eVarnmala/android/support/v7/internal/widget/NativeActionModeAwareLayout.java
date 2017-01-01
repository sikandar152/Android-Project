package android.support.v7.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.View;

@TargetApi(11)
public class NativeActionModeAwareLayout
  extends ContentFrameLayout
{
  private OnActionModeForChildListener mActionModeForChildListener;
  
  public NativeActionModeAwareLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void setActionModeForChildListener(OnActionModeForChildListener paramOnActionModeForChildListener)
  {
    this.mActionModeForChildListener = paramOnActionModeForChildListener;
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback)
  {
    ActionMode localActionMode;
    if (this.mActionModeForChildListener == null) {
      localActionMode = super.startActionModeForChild(paramView, paramCallback);
    } else {
      localActionMode = this.mActionModeForChildListener.startActionModeForChild(paramView, paramCallback);
    }
    return localActionMode;
  }
  
  public static abstract interface OnActionModeForChildListener
  {
    public abstract ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\NativeActionModeAwareLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */