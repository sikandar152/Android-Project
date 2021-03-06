package android.support.v7.app;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

abstract class ActionBarActivityDelegate
{
  static final String METADATA_UI_OPTIONS = "android.support.UI_OPTIONS";
  private static final String TAG = "ActionBarActivityDelegate";
  private ActionBar mActionBar;
  final ActionBarActivity mActivity;
  final WindowCallback mDefaultWindowCallback = new WindowCallback()
  {
    public boolean onCreatePanelMenu(int paramAnonymousInt, Menu paramAnonymousMenu)
    {
      return ActionBarActivityDelegate.this.mActivity.superOnCreatePanelMenu(paramAnonymousInt, paramAnonymousMenu);
    }
    
    public View onCreatePanelView(int paramAnonymousInt)
    {
      return null;
    }
    
    public boolean onMenuItemSelected(int paramAnonymousInt, MenuItem paramAnonymousMenuItem)
    {
      return ActionBarActivityDelegate.this.mActivity.onMenuItemSelected(paramAnonymousInt, paramAnonymousMenuItem);
    }
    
    public boolean onMenuOpened(int paramAnonymousInt, Menu paramAnonymousMenu)
    {
      return ActionBarActivityDelegate.this.mActivity.onMenuOpened(paramAnonymousInt, paramAnonymousMenu);
    }
    
    public void onPanelClosed(int paramAnonymousInt, Menu paramAnonymousMenu)
    {
      ActionBarActivityDelegate.this.mActivity.onPanelClosed(paramAnonymousInt, paramAnonymousMenu);
    }
    
    public boolean onPreparePanel(int paramAnonymousInt, View paramAnonymousView, Menu paramAnonymousMenu)
    {
      return ActionBarActivityDelegate.this.mActivity.superOnPreparePanel(paramAnonymousInt, paramAnonymousView, paramAnonymousMenu);
    }
    
    public ActionMode startActionMode(ActionMode.Callback paramAnonymousCallback)
    {
      return ActionBarActivityDelegate.this.startSupportActionModeFromWindow(paramAnonymousCallback);
    }
  };
  boolean mHasActionBar;
  private boolean mIsDestroyed;
  boolean mIsFloating;
  private MenuInflater mMenuInflater;
  boolean mOverlayActionBar;
  boolean mOverlayActionMode;
  private WindowCallback mWindowCallback;
  
  ActionBarActivityDelegate(ActionBarActivity paramActionBarActivity)
  {
    this.mActivity = paramActionBarActivity;
    this.mWindowCallback = this.mDefaultWindowCallback;
  }
  
  static ActionBarActivityDelegate createDelegate(ActionBarActivity paramActionBarActivity)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 11) {
      localObject = new ActionBarActivityDelegateBase(paramActionBarActivity);
    } else {
      localObject = new ActionBarActivityDelegateHC(paramActionBarActivity);
    }
    return (ActionBarActivityDelegate)localObject;
  }
  
  abstract void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams);
  
  abstract ActionBar createSupportActionBar();
  
  abstract View createView(String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet);
  
  final void destroy()
  {
    this.mIsDestroyed = true;
  }
  
  protected final Context getActionBarThemedContext()
  {
    Object localObject = null;
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null) {
      localObject = localActionBar.getThemedContext();
    }
    if (localObject == null) {
      localObject = this.mActivity;
    }
    return (Context)localObject;
  }
  
  final android.support.v4.app.ActionBarDrawerToggle.Delegate getDrawerToggleDelegate()
  {
    return new ActionBarDrawableToggleImpl(null);
  }
  
  abstract int getHomeAsUpIndicatorAttrId();
  
  MenuInflater getMenuInflater()
  {
    if (this.mMenuInflater == null) {
      this.mMenuInflater = new SupportMenuInflater(getActionBarThemedContext());
    }
    return this.mMenuInflater;
  }
  
  final ActionBar getSupportActionBar()
  {
    if ((this.mHasActionBar) && (this.mActionBar == null)) {
      this.mActionBar = createSupportActionBar();
    }
    return this.mActionBar;
  }
  
  final String getUiOptionsFromMetadata()
  {
    try
    {
      Object localObject1 = this.mActivity.getPackageManager().getActivityInfo(this.mActivity.getComponentName(), 128);
      localObject2 = null;
      if (((ActivityInfo)localObject1).metaData != null)
      {
        localObject1 = ((ActivityInfo)localObject1).metaData.getString("android.support.UI_OPTIONS");
        localObject2 = localObject1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Log.e("ActionBarActivityDelegate", "getUiOptionsFromMetadata: Activity '" + this.mActivity.getClass().getSimpleName() + "' not in manifest");
        Object localObject2 = null;
      }
    }
    return (String)localObject2;
  }
  
  final ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate()
  {
    return new ActionBarDrawableToggleImpl(null);
  }
  
  final WindowCallback getWindowCallback()
  {
    return this.mWindowCallback;
  }
  
  final boolean isDestroyed()
  {
    return this.mIsDestroyed;
  }
  
  abstract boolean onBackPressed();
  
  abstract void onConfigurationChanged(Configuration paramConfiguration);
  
  abstract void onContentChanged();
  
  void onCreate(Bundle paramBundle)
  {
    TypedArray localTypedArray = this.mActivity.obtainStyledAttributes(R.styleable.Theme);
    if (localTypedArray.hasValue(R.styleable.Theme_windowActionBar))
    {
      if (localTypedArray.getBoolean(R.styleable.Theme_windowActionBar, false)) {
        this.mHasActionBar = true;
      }
      if (localTypedArray.getBoolean(R.styleable.Theme_windowActionBarOverlay, false)) {
        this.mOverlayActionBar = true;
      }
      if (localTypedArray.getBoolean(R.styleable.Theme_windowActionModeOverlay, false)) {
        this.mOverlayActionMode = true;
      }
      this.mIsFloating = localTypedArray.getBoolean(R.styleable.Theme_android_windowIsFloating, false);
      localTypedArray.recycle();
      return;
    }
    localTypedArray.recycle();
    throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
  }
  
  abstract boolean onCreatePanelMenu(int paramInt, Menu paramMenu);
  
  abstract View onCreatePanelView(int paramInt);
  
  boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  abstract boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent);
  
  abstract boolean onMenuOpened(int paramInt, Menu paramMenu);
  
  abstract void onPanelClosed(int paramInt, Menu paramMenu);
  
  abstract void onPostResume();
  
  boolean onPrepareOptionsPanel(View paramView, Menu paramMenu)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 16) {
      bool = this.mActivity.superOnPrepareOptionsPanel(paramView, paramMenu);
    } else {
      bool = this.mActivity.onPrepareOptionsMenu(paramMenu);
    }
    return bool;
  }
  
  abstract boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu);
  
  abstract void onStop();
  
  abstract void onTitleChanged(CharSequence paramCharSequence);
  
  final ActionBar peekSupportActionBar()
  {
    return this.mActionBar;
  }
  
  abstract void setContentView(int paramInt);
  
  abstract void setContentView(View paramView);
  
  abstract void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams);
  
  protected final void setSupportActionBar(ActionBar paramActionBar)
  {
    this.mActionBar = paramActionBar;
  }
  
  abstract void setSupportActionBar(Toolbar paramToolbar);
  
  abstract void setSupportProgress(int paramInt);
  
  abstract void setSupportProgressBarIndeterminate(boolean paramBoolean);
  
  abstract void setSupportProgressBarIndeterminateVisibility(boolean paramBoolean);
  
  abstract void setSupportProgressBarVisibility(boolean paramBoolean);
  
  final void setWindowCallback(WindowCallback paramWindowCallback)
  {
    if (paramWindowCallback != null)
    {
      this.mWindowCallback = paramWindowCallback;
      return;
    }
    throw new IllegalArgumentException("callback can not be null");
  }
  
  abstract ActionMode startSupportActionMode(ActionMode.Callback paramCallback);
  
  abstract ActionMode startSupportActionModeFromWindow(ActionMode.Callback paramCallback);
  
  abstract void supportInvalidateOptionsMenu();
  
  abstract boolean supportRequestWindowFeature(int paramInt);
  
  private class ActionBarDrawableToggleImpl
    implements ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
  {
    private ActionBarDrawableToggleImpl() {}
    
    public Context getActionBarThemedContext()
    {
      return ActionBarActivityDelegate.this.getActionBarThemedContext();
    }
    
    public Drawable getThemeUpIndicator()
    {
      Object localObject2 = ActionBarActivityDelegate.this.getActionBarThemedContext();
      Object localObject1 = new int[1];
      localObject1[0] = ActionBarActivityDelegate.this.getHomeAsUpIndicatorAttrId();
      localObject1 = ((Context)localObject2).obtainStyledAttributes((int[])localObject1);
      localObject2 = ((TypedArray)localObject1).getDrawable(0);
      ((TypedArray)localObject1).recycle();
      return (Drawable)localObject2;
    }
    
    public void setActionBarDescription(int paramInt)
    {
      ActionBar localActionBar = ActionBarActivityDelegate.this.getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
    {
      ActionBar localActionBar = ActionBarActivityDelegate.this.getSupportActionBar();
      if (localActionBar != null)
      {
        localActionBar.setHomeAsUpIndicator(paramDrawable);
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\ActionBarActivityDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */