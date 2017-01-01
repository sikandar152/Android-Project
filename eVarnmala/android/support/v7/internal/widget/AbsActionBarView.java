package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.ActionMenuView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

abstract class AbsActionBarView
  extends ViewGroup
{
  private static final int FADE_DURATION = 200;
  private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
  protected ActionMenuPresenter mActionMenuPresenter;
  protected int mContentHeight;
  protected ActionMenuView mMenuView;
  protected final Context mPopupContext;
  protected boolean mSplitActionBar;
  protected ViewGroup mSplitView;
  protected boolean mSplitWhenNarrow;
  protected final VisibilityAnimListener mVisAnimListener = new VisibilityAnimListener();
  protected ViewPropertyAnimatorCompat mVisibilityAnim;
  
  AbsActionBarView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  AbsActionBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  AbsActionBarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedValue localTypedValue = new TypedValue();
    if ((!paramContext.getTheme().resolveAttribute(R.attr.actionBarPopupTheme, localTypedValue, true)) || (localTypedValue.resourceId == 0)) {
      this.mPopupContext = paramContext;
    } else {
      this.mPopupContext = new ContextThemeWrapper(paramContext, localTypedValue.resourceId);
    }
  }
  
  protected static int next(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = paramInt1 + paramInt2;
    } else {
      i = paramInt1 - paramInt2;
    }
    return i;
  }
  
  public void animateToVisibility(int paramInt)
  {
    if (this.mVisibilityAnim != null) {
      this.mVisibilityAnim.cancel();
    }
    Object localObject1;
    Object localObject2;
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat;
    if (paramInt != 0)
    {
      localObject1 = ViewCompat.animate(this).alpha(0.0F);
      ((ViewPropertyAnimatorCompat)localObject1).setDuration(200L);
      ((ViewPropertyAnimatorCompat)localObject1).setInterpolator(sAlphaInterpolator);
      if ((this.mSplitView == null) || (this.mMenuView == null))
      {
        ((ViewPropertyAnimatorCompat)localObject1).setListener(this.mVisAnimListener.withFinalVisibility((ViewPropertyAnimatorCompat)localObject1, paramInt));
        ((ViewPropertyAnimatorCompat)localObject1).start();
      }
      else
      {
        localObject2 = new ViewPropertyAnimatorCompatSet();
        localViewPropertyAnimatorCompat = ViewCompat.animate(this.mMenuView).alpha(0.0F);
        localViewPropertyAnimatorCompat.setDuration(200L);
        ((ViewPropertyAnimatorCompatSet)localObject2).setListener(this.mVisAnimListener.withFinalVisibility((ViewPropertyAnimatorCompat)localObject1, paramInt));
        ((ViewPropertyAnimatorCompatSet)localObject2).play((ViewPropertyAnimatorCompat)localObject1).play(localViewPropertyAnimatorCompat);
        ((ViewPropertyAnimatorCompatSet)localObject2).start();
      }
    }
    else
    {
      if (getVisibility() != 0)
      {
        ViewCompat.setAlpha(this, 0.0F);
        if ((this.mSplitView != null) && (this.mMenuView != null)) {
          ViewCompat.setAlpha(this.mMenuView, 0.0F);
        }
      }
      localViewPropertyAnimatorCompat = ViewCompat.animate(this).alpha(1.0F);
      localViewPropertyAnimatorCompat.setDuration(200L);
      localViewPropertyAnimatorCompat.setInterpolator(sAlphaInterpolator);
      if ((this.mSplitView == null) || (this.mMenuView == null))
      {
        localViewPropertyAnimatorCompat.setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat, paramInt));
        localViewPropertyAnimatorCompat.start();
      }
      else
      {
        localObject1 = new ViewPropertyAnimatorCompatSet();
        localObject2 = ViewCompat.animate(this.mMenuView).alpha(1.0F);
        ((ViewPropertyAnimatorCompat)localObject2).setDuration(200L);
        ((ViewPropertyAnimatorCompatSet)localObject1).setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat, paramInt));
        ((ViewPropertyAnimatorCompatSet)localObject1).play(localViewPropertyAnimatorCompat).play((ViewPropertyAnimatorCompat)localObject2);
        ((ViewPropertyAnimatorCompatSet)localObject1).start();
      }
    }
  }
  
  public boolean canShowOverflowMenu()
  {
    boolean bool;
    if ((!isOverflowReserved()) || (getVisibility() != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void dismissPopupMenus()
  {
    if (this.mActionMenuPresenter != null) {
      this.mActionMenuPresenter.dismissPopupMenus();
    }
  }
  
  public int getAnimatedVisibility()
  {
    int i;
    if (this.mVisibilityAnim == null) {
      i = getVisibility();
    } else {
      i = this.mVisAnimListener.mFinalVisibility;
    }
    return i;
  }
  
  public int getContentHeight()
  {
    return this.mContentHeight;
  }
  
  public boolean hideOverflowMenu()
  {
    boolean bool;
    if (this.mActionMenuPresenter == null) {
      bool = false;
    } else {
      bool = this.mActionMenuPresenter.hideOverflowMenu();
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowPending()
  {
    boolean bool;
    if (this.mActionMenuPresenter == null) {
      bool = false;
    } else {
      bool = this.mActionMenuPresenter.isOverflowMenuShowPending();
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    boolean bool;
    if (this.mActionMenuPresenter == null) {
      bool = false;
    } else {
      bool = this.mActionMenuPresenter.isOverflowMenuShowing();
    }
    return bool;
  }
  
  public boolean isOverflowReserved()
  {
    boolean bool;
    if ((this.mActionMenuPresenter == null) || (!this.mActionMenuPresenter.isOverflowReserved())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected int measureChildView(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE), paramInt2);
    return Math.max(0, paramInt1 - paramView.getMeasuredWidth() - paramInt3);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (Build.VERSION.SDK_INT >= 8) {
      super.onConfigurationChanged(paramConfiguration);
    }
    TypedArray localTypedArray = getContext().obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
    setContentHeight(localTypedArray.getLayoutDimension(R.styleable.ActionBar_height, 0));
    localTypedArray.recycle();
    if (this.mActionMenuPresenter != null) {
      this.mActionMenuPresenter.onConfigurationChanged(paramConfiguration);
    }
  }
  
  protected int positionChild(View paramView, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int k = paramView.getMeasuredWidth();
    int i = paramView.getMeasuredHeight();
    int j = paramInt2 + (paramInt3 - i) / 2;
    if (!paramBoolean) {
      paramView.layout(paramInt1, j, paramInt1 + k, j + i);
    } else {
      paramView.layout(paramInt1 - k, j, paramInt1, j + i);
    }
    if (paramBoolean) {
      k = -k;
    }
    return k;
  }
  
  public void postShowOverflowMenu()
  {
    post(new Runnable()
    {
      public void run()
      {
        AbsActionBarView.this.showOverflowMenu();
      }
    });
  }
  
  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = paramInt;
    requestLayout();
  }
  
  public void setSplitToolbar(boolean paramBoolean)
  {
    this.mSplitActionBar = paramBoolean;
  }
  
  public void setSplitView(ViewGroup paramViewGroup)
  {
    this.mSplitView = paramViewGroup;
  }
  
  public void setSplitWhenNarrow(boolean paramBoolean)
  {
    this.mSplitWhenNarrow = paramBoolean;
  }
  
  public boolean showOverflowMenu()
  {
    boolean bool;
    if (this.mActionMenuPresenter == null) {
      bool = false;
    } else {
      bool = this.mActionMenuPresenter.showOverflowMenu();
    }
    return bool;
  }
  
  protected class VisibilityAnimListener
    implements ViewPropertyAnimatorListener
  {
    private boolean mCanceled = false;
    int mFinalVisibility;
    
    protected VisibilityAnimListener() {}
    
    public void onAnimationCancel(View paramView)
    {
      this.mCanceled = true;
    }
    
    public void onAnimationEnd(View paramView)
    {
      if (!this.mCanceled)
      {
        AbsActionBarView.this.mVisibilityAnim = null;
        AbsActionBarView.this.setVisibility(this.mFinalVisibility);
        if ((AbsActionBarView.this.mSplitView != null) && (AbsActionBarView.this.mMenuView != null)) {
          AbsActionBarView.this.mMenuView.setVisibility(this.mFinalVisibility);
        }
      }
    }
    
    public void onAnimationStart(View paramView)
    {
      AbsActionBarView.this.setVisibility(0);
      this.mCanceled = false;
    }
    
    public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, int paramInt)
    {
      AbsActionBarView.this.mVisibilityAnim = paramViewPropertyAnimatorCompat;
      this.mFinalVisibility = paramInt;
      return this;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\AbsActionBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */