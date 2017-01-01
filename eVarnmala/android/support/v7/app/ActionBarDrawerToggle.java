package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class ActionBarDrawerToggle
  implements DrawerLayout.DrawerListener
{
  private final Delegate mActivityImpl;
  private final int mCloseDrawerContentDescRes;
  private boolean mDrawerIndicatorEnabled = true;
  private final DrawerLayout mDrawerLayout;
  private boolean mHasCustomUpIndicator;
  private Drawable mHomeAsUpIndicator;
  private final int mOpenDrawerContentDescRes;
  private DrawerToggle mSlider;
  private View.OnClickListener mToolbarNavigationClickListener;
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, int paramInt1, int paramInt2)
  {
    this(paramActivity, null, paramDrawerLayout, null, paramInt1, paramInt2);
  }
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, Toolbar paramToolbar, int paramInt1, int paramInt2)
  {
    this(paramActivity, paramToolbar, paramDrawerLayout, null, paramInt1, paramInt2);
  }
  
  <T extends Drawable,  extends DrawerToggle> ActionBarDrawerToggle(Activity paramActivity, Toolbar paramToolbar, DrawerLayout paramDrawerLayout, T paramT, int paramInt1, int paramInt2)
  {
    if (paramToolbar == null)
    {
      if (!(paramActivity instanceof DelegateProvider))
      {
        if (!(paramActivity instanceof TmpDelegateProvider))
        {
          if (Build.VERSION.SDK_INT < 18)
          {
            if (Build.VERSION.SDK_INT < 11) {
              this.mActivityImpl = new DummyDelegate(paramActivity);
            } else {
              this.mActivityImpl = new HoneycombDelegate(paramActivity, null);
            }
          }
          else {
            this.mActivityImpl = new JellybeanMr2Delegate(paramActivity, null);
          }
        }
        else {
          this.mActivityImpl = ((TmpDelegateProvider)paramActivity).getV7DrawerToggleDelegate();
        }
      }
      else {
        this.mActivityImpl = ((DelegateProvider)paramActivity).getDrawerToggleDelegate();
      }
    }
    else
    {
      this.mActivityImpl = new ToolbarCompatDelegate(paramToolbar);
      paramToolbar.setNavigationOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!ActionBarDrawerToggle.this.mDrawerIndicatorEnabled)
          {
            if (ActionBarDrawerToggle.this.mToolbarNavigationClickListener != null) {
              ActionBarDrawerToggle.this.mToolbarNavigationClickListener.onClick(paramAnonymousView);
            }
          }
          else {
            ActionBarDrawerToggle.this.toggle();
          }
        }
      });
    }
    this.mDrawerLayout = paramDrawerLayout;
    this.mOpenDrawerContentDescRes = paramInt1;
    this.mCloseDrawerContentDescRes = paramInt2;
    if (paramT != null) {
      this.mSlider = ((DrawerToggle)paramT);
    } else {
      this.mSlider = new DrawerArrowDrawableToggle(paramActivity, this.mActivityImpl.getActionBarThemedContext());
    }
    this.mHomeAsUpIndicator = getThemeUpIndicator();
  }
  
  private void toggle()
  {
    if (!this.mDrawerLayout.isDrawerVisible(8388611)) {
      this.mDrawerLayout.openDrawer(8388611);
    } else {
      this.mDrawerLayout.closeDrawer(8388611);
    }
  }
  
  Drawable getThemeUpIndicator()
  {
    return this.mActivityImpl.getThemeUpIndicator();
  }
  
  public View.OnClickListener getToolbarNavigationClickListener()
  {
    return this.mToolbarNavigationClickListener;
  }
  
  public boolean isDrawerIndicatorEnabled()
  {
    return this.mDrawerIndicatorEnabled;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (!this.mHasCustomUpIndicator) {
      this.mHomeAsUpIndicator = getThemeUpIndicator();
    }
    syncState();
  }
  
  public void onDrawerClosed(View paramView)
  {
    this.mSlider.setPosition(0.0F);
    if (this.mDrawerIndicatorEnabled) {
      setActionBarDescription(this.mOpenDrawerContentDescRes);
    }
  }
  
  public void onDrawerOpened(View paramView)
  {
    this.mSlider.setPosition(1.0F);
    if (this.mDrawerIndicatorEnabled) {
      setActionBarDescription(this.mCloseDrawerContentDescRes);
    }
  }
  
  public void onDrawerSlide(View paramView, float paramFloat)
  {
    this.mSlider.setPosition(Math.min(1.0F, Math.max(0.0F, paramFloat)));
  }
  
  public void onDrawerStateChanged(int paramInt) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    boolean bool;
    if ((paramMenuItem == null) || (paramMenuItem.getItemId() != 16908332) || (!this.mDrawerIndicatorEnabled))
    {
      bool = false;
    }
    else
    {
      toggle();
      bool = true;
    }
    return bool;
  }
  
  void setActionBarDescription(int paramInt)
  {
    this.mActivityImpl.setActionBarDescription(paramInt);
  }
  
  void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
  {
    this.mActivityImpl.setActionBarUpIndicator(paramDrawable, paramInt);
  }
  
  public void setDrawerIndicatorEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mDrawerIndicatorEnabled)
    {
      if (!paramBoolean)
      {
        setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
      }
      else
      {
        Drawable localDrawable = (Drawable)this.mSlider;
        int i;
        if (!this.mDrawerLayout.isDrawerOpen(8388611)) {
          i = this.mOpenDrawerContentDescRes;
        } else {
          i = this.mCloseDrawerContentDescRes;
        }
        setActionBarUpIndicator(localDrawable, i);
      }
      this.mDrawerIndicatorEnabled = paramBoolean;
    }
  }
  
  public void setHomeAsUpIndicator(int paramInt)
  {
    Drawable localDrawable = null;
    if (paramInt != 0) {
      localDrawable = this.mDrawerLayout.getResources().getDrawable(paramInt);
    }
    setHomeAsUpIndicator(localDrawable);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      this.mHomeAsUpIndicator = paramDrawable;
      this.mHasCustomUpIndicator = true;
    }
    else
    {
      this.mHomeAsUpIndicator = getThemeUpIndicator();
      this.mHasCustomUpIndicator = false;
    }
    if (!this.mDrawerIndicatorEnabled) {
      setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
    }
  }
  
  public void setToolbarNavigationClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mToolbarNavigationClickListener = paramOnClickListener;
  }
  
  public void syncState()
  {
    if (!this.mDrawerLayout.isDrawerOpen(8388611)) {
      this.mSlider.setPosition(0.0F);
    } else {
      this.mSlider.setPosition(1.0F);
    }
    if (this.mDrawerIndicatorEnabled)
    {
      Drawable localDrawable = (Drawable)this.mSlider;
      int i;
      if (!this.mDrawerLayout.isDrawerOpen(8388611)) {
        i = this.mOpenDrawerContentDescRes;
      } else {
        i = this.mCloseDrawerContentDescRes;
      }
      setActionBarUpIndicator(localDrawable, i);
    }
  }
  
  static class DummyDelegate
    implements ActionBarDrawerToggle.Delegate
  {
    final Activity mActivity;
    
    DummyDelegate(Activity paramActivity)
    {
      this.mActivity = paramActivity;
    }
    
    public Context getActionBarThemedContext()
    {
      return this.mActivity;
    }
    
    public Drawable getThemeUpIndicator()
    {
      return null;
    }
    
    public void setActionBarDescription(int paramInt) {}
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt) {}
  }
  
  static class ToolbarCompatDelegate
    implements ActionBarDrawerToggle.Delegate
  {
    final Toolbar mToolbar;
    
    ToolbarCompatDelegate(Toolbar paramToolbar)
    {
      this.mToolbar = paramToolbar;
    }
    
    public Context getActionBarThemedContext()
    {
      return this.mToolbar.getContext();
    }
    
    public Drawable getThemeUpIndicator()
    {
      Object localObject2 = this.mToolbar.getContext();
      Object localObject1 = new int[1];
      localObject1[0] = 16908332;
      localObject2 = ((Context)localObject2).obtainStyledAttributes((int[])localObject1);
      localObject1 = ((TypedArray)localObject2).getDrawable(0);
      ((TypedArray)localObject2).recycle();
      return (Drawable)localObject1;
    }
    
    public void setActionBarDescription(int paramInt)
    {
      this.mToolbar.setNavigationContentDescription(paramInt);
    }
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
    {
      this.mToolbar.setNavigationIcon(paramDrawable);
      this.mToolbar.setNavigationContentDescription(paramInt);
    }
  }
  
  private static class JellybeanMr2Delegate
    implements ActionBarDrawerToggle.Delegate
  {
    final Activity mActivity;
    
    private JellybeanMr2Delegate(Activity paramActivity)
    {
      this.mActivity = paramActivity;
    }
    
    public Context getActionBarThemedContext()
    {
      Object localObject = this.mActivity.getActionBar();
      if (localObject == null) {
        localObject = this.mActivity;
      } else {
        localObject = ((ActionBar)localObject).getThemedContext();
      }
      return (Context)localObject;
    }
    
    public Drawable getThemeUpIndicator()
    {
      Object localObject1 = getActionBarThemedContext();
      Object localObject2 = new int[1];
      localObject2[0] = 16843531;
      localObject1 = ((Context)localObject1).obtainStyledAttributes(null, (int[])localObject2, 16843470, 0);
      localObject2 = ((TypedArray)localObject1).getDrawable(0);
      ((TypedArray)localObject1).recycle();
      return (Drawable)localObject2;
    }
    
    public void setActionBarDescription(int paramInt)
    {
      ActionBar localActionBar = this.mActivity.getActionBar();
      if (localActionBar != null) {
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
    {
      ActionBar localActionBar = this.mActivity.getActionBar();
      if (localActionBar != null)
      {
        localActionBar.setHomeAsUpIndicator(paramDrawable);
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
  }
  
  private static class HoneycombDelegate
    implements ActionBarDrawerToggle.Delegate
  {
    final Activity mActivity;
    ActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;
    
    private HoneycombDelegate(Activity paramActivity)
    {
      this.mActivity = paramActivity;
    }
    
    public Context getActionBarThemedContext()
    {
      Object localObject = this.mActivity.getActionBar();
      if (localObject == null) {
        localObject = this.mActivity;
      } else {
        localObject = ((ActionBar)localObject).getThemedContext();
      }
      return (Context)localObject;
    }
    
    public Drawable getThemeUpIndicator()
    {
      return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(this.mActivity);
    }
    
    public void setActionBarDescription(int paramInt)
    {
      this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, paramInt);
    }
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
    {
      this.mActivity.getActionBar().setDisplayShowHomeEnabled(true);
      this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, paramDrawable, paramInt);
      this.mActivity.getActionBar().setDisplayShowHomeEnabled(false);
    }
  }
  
  static abstract interface DrawerToggle
  {
    public abstract float getPosition();
    
    public abstract void setPosition(float paramFloat);
  }
  
  static class DrawerArrowDrawableToggle
    extends DrawerArrowDrawable
    implements ActionBarDrawerToggle.DrawerToggle
  {
    private final Activity mActivity;
    
    public DrawerArrowDrawableToggle(Activity paramActivity, Context paramContext)
    {
      super();
      this.mActivity = paramActivity;
    }
    
    public float getPosition()
    {
      return super.getProgress();
    }
    
    boolean isLayoutRtl()
    {
      int i = 1;
      if (ViewCompat.getLayoutDirection(this.mActivity.getWindow().getDecorView()) != i) {
        i = 0;
      }
      return i;
    }
    
    public void setPosition(float paramFloat)
    {
      if (paramFloat != 1.0F)
      {
        if (paramFloat == 0.0F) {
          setVerticalMirror(false);
        }
      }
      else {
        setVerticalMirror(true);
      }
      super.setProgress(paramFloat);
    }
  }
  
  public static abstract interface Delegate
  {
    public abstract Context getActionBarThemedContext();
    
    public abstract Drawable getThemeUpIndicator();
    
    public abstract void setActionBarDescription(int paramInt);
    
    public abstract void setActionBarUpIndicator(Drawable paramDrawable, int paramInt);
  }
  
  static abstract interface TmpDelegateProvider
  {
    @Nullable
    public abstract ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate();
  }
  
  public static abstract interface DelegateProvider
  {
    @Nullable
    public abstract ActionBarDrawerToggle.Delegate getDrawerToggleDelegate();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\ActionBarDrawerToggle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */