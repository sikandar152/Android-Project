package android.support.v7.app;

import android.annotation.TargetApi;
import android.support.v7.internal.view.SupportActionModeWrapper;
import android.support.v7.internal.view.SupportActionModeWrapper.CallbackWrapper;
import android.support.v7.internal.widget.NativeActionModeAwareLayout;
import android.support.v7.internal.widget.NativeActionModeAwareLayout.OnActionModeForChildListener;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.View;

@TargetApi(11)
class ActionBarActivityDelegateHC
  extends ActionBarActivityDelegateBase
  implements NativeActionModeAwareLayout.OnActionModeForChildListener
{
  private NativeActionModeAwareLayout mNativeActionModeAwareLayout;
  
  ActionBarActivityDelegateHC(ActionBarActivity paramActionBarActivity)
  {
    super(paramActionBarActivity);
  }
  
  boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  void onSubDecorInstalled()
  {
    this.mNativeActionModeAwareLayout = ((NativeActionModeAwareLayout)this.mActivity.findViewById(16908290));
    if (this.mNativeActionModeAwareLayout != null) {
      this.mNativeActionModeAwareLayout.setActionModeForChildListener(this);
    }
  }
  
  public android.view.ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback)
  {
    Object localObject = startSupportActionMode(new SupportActionModeWrapper.CallbackWrapper(paramView.getContext(), paramCallback));
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = new SupportActionModeWrapper(this.mActivity, (android.support.v7.view.ActionMode)localObject);
    }
    return (android.view.ActionMode)localObject;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\ActionBarActivityDelegateHC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */