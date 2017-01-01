package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.VersionUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ActionBarContainer
  extends FrameLayout
{
  private View mActionBarView;
  Drawable mBackground;
  private View mContextView;
  private int mHeight;
  boolean mIsSplit;
  boolean mIsStacked;
  private boolean mIsTransitioning;
  Drawable mSplitBackground;
  Drawable mStackedBackground;
  private View mTabContainer;
  
  public ActionBarContainer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (!VersionUtils.isAtLeastL()) {
      localObject = new ActionBarBackgroundDrawable(this);
    } else {
      localObject = new ActionBarBackgroundDrawableV21(this);
    }
    setBackgroundDrawable((Drawable)localObject);
    Object localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActionBar);
    this.mBackground = ((TypedArray)localObject).getDrawable(R.styleable.ActionBar_background);
    this.mStackedBackground = ((TypedArray)localObject).getDrawable(R.styleable.ActionBar_backgroundStacked);
    this.mHeight = ((TypedArray)localObject).getDimensionPixelSize(R.styleable.ActionBar_height, -1);
    if (getId() == R.id.split_action_bar)
    {
      this.mIsSplit = bool;
      this.mSplitBackground = ((TypedArray)localObject).getDrawable(R.styleable.ActionBar_backgroundSplit);
    }
    ((TypedArray)localObject).recycle();
    if (!this.mIsSplit)
    {
      if ((this.mBackground != null) || (this.mStackedBackground != null)) {
        bool = false;
      }
    }
    else if (this.mSplitBackground != null) {
      bool = false;
    }
    setWillNotDraw(bool);
  }
  
  private int getMeasuredHeightWithMargins(View paramView)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    return paramView.getMeasuredHeight() + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
  }
  
  private boolean isCollapsed(View paramView)
  {
    boolean bool;
    if ((paramView != null) && (paramView.getVisibility() != 8) && (paramView.getMeasuredHeight() != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if ((this.mBackground != null) && (this.mBackground.isStateful())) {
      this.mBackground.setState(getDrawableState());
    }
    if ((this.mStackedBackground != null) && (this.mStackedBackground.isStateful())) {
      this.mStackedBackground.setState(getDrawableState());
    }
    if ((this.mSplitBackground != null) && (this.mSplitBackground.isStateful())) {
      this.mSplitBackground.setState(getDrawableState());
    }
  }
  
  public View getTabContainer()
  {
    return this.mTabContainer;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      super.jumpDrawablesToCurrentState();
      if (this.mBackground != null) {
        this.mBackground.jumpToCurrentState();
      }
      if (this.mStackedBackground != null) {
        this.mStackedBackground.jumpToCurrentState();
      }
      if (this.mSplitBackground != null) {
        this.mSplitBackground.jumpToCurrentState();
      }
    }
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mActionBarView = findViewById(R.id.action_bar);
    this.mContextView = findViewById(R.id.action_context_bar);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((!this.mIsTransitioning) && (!super.onInterceptTouchEvent(paramMotionEvent))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = this.mTabContainer;
    boolean bool;
    if ((localView == null) || (localView.getVisibility() == 8)) {
      bool = false;
    } else {
      bool = true;
    }
    if ((localView != null) && (localView.getVisibility() != 8))
    {
      i = getMeasuredHeight();
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      localView.layout(paramInt1, i - localView.getMeasuredHeight() - localLayoutParams.bottomMargin, paramInt3, i - localLayoutParams.bottomMargin);
    }
    int i = 0;
    if (!this.mIsSplit)
    {
      if (this.mBackground != null)
      {
        if (this.mActionBarView.getVisibility() != 0)
        {
          if ((this.mContextView == null) || (this.mContextView.getVisibility() != 0)) {
            this.mBackground.setBounds(0, 0, 0, 0);
          } else {
            this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
          }
        }
        else {
          this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
        }
        i = 1;
      }
      this.mIsStacked = bool;
      if ((bool) && (this.mStackedBackground != null))
      {
        this.mStackedBackground.setBounds(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom());
        i = 1;
      }
    }
    else if (this.mSplitBackground != null)
    {
      this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      i = 1;
    }
    if (i != 0) {
      invalidate();
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.mActionBarView == null) && (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE) && (this.mHeight >= 0)) {
      paramInt2 = View.MeasureSpec.makeMeasureSpec(Math.min(this.mHeight, View.MeasureSpec.getSize(paramInt2)), Integer.MIN_VALUE);
    }
    super.onMeasure(paramInt1, paramInt2);
    if (this.mActionBarView != null)
    {
      int j = View.MeasureSpec.getMode(paramInt2);
      if ((this.mTabContainer != null) && (this.mTabContainer.getVisibility() != 8) && (j != 1073741824))
      {
        int i;
        if (isCollapsed(this.mActionBarView))
        {
          if (isCollapsed(this.mContextView)) {
            i = 0;
          } else {
            i = getMeasuredHeightWithMargins(this.mContextView);
          }
        }
        else {
          i = getMeasuredHeightWithMargins(this.mActionBarView);
        }
        if (j != Integer.MIN_VALUE) {
          j = Integer.MAX_VALUE;
        } else {
          j = View.MeasureSpec.getSize(paramInt2);
        }
        setMeasuredDimension(getMeasuredWidth(), Math.min(i + getMeasuredHeightWithMargins(this.mTabContainer), j));
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void setPrimaryBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (this.mBackground != null)
    {
      this.mBackground.setCallback(null);
      unscheduleDrawable(this.mBackground);
    }
    this.mBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (this.mActionBarView != null) {
        this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
      }
    }
    if (!this.mIsSplit)
    {
      if ((this.mBackground != null) || (this.mStackedBackground != null)) {
        bool = false;
      }
    }
    else if (this.mSplitBackground != null) {
      bool = false;
    }
    setWillNotDraw(bool);
    invalidate();
  }
  
  public void setSplitBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (this.mSplitBackground != null)
    {
      this.mSplitBackground.setCallback(null);
      unscheduleDrawable(this.mSplitBackground);
    }
    this.mSplitBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if ((this.mIsSplit) && (this.mSplitBackground != null)) {
        this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      }
    }
    if (!this.mIsSplit)
    {
      if ((this.mBackground != null) || (this.mStackedBackground != null)) {
        bool = false;
      }
    }
    else if (this.mSplitBackground != null) {
      bool = false;
    }
    setWillNotDraw(bool);
    invalidate();
  }
  
  public void setStackedBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (this.mStackedBackground != null)
    {
      this.mStackedBackground.setCallback(null);
      unscheduleDrawable(this.mStackedBackground);
    }
    this.mStackedBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if ((this.mIsStacked) && (this.mStackedBackground != null)) {
        this.mStackedBackground.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
      }
    }
    if (!this.mIsSplit)
    {
      if ((this.mBackground != null) || (this.mStackedBackground != null)) {
        bool = false;
      }
    }
    else if (this.mSplitBackground != null) {
      bool = false;
    }
    setWillNotDraw(bool);
    invalidate();
  }
  
  public void setTabContainer(ScrollingTabContainerView paramScrollingTabContainerView)
  {
    if (this.mTabContainer != null) {
      removeView(this.mTabContainer);
    }
    this.mTabContainer = paramScrollingTabContainerView;
    if (paramScrollingTabContainerView != null)
    {
      addView(paramScrollingTabContainerView);
      ViewGroup.LayoutParams localLayoutParams = paramScrollingTabContainerView.getLayoutParams();
      localLayoutParams.width = -1;
      localLayoutParams.height = -2;
      paramScrollingTabContainerView.setAllowCollapse(false);
    }
  }
  
  public void setTransitioning(boolean paramBoolean)
  {
    this.mIsTransitioning = paramBoolean;
    int i;
    if (!paramBoolean) {
      i = 262144;
    } else {
      i = 393216;
    }
    setDescendantFocusability(i);
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    boolean bool;
    if (paramInt != 0) {
      bool = false;
    } else {
      bool = true;
    }
    if (this.mBackground != null) {
      this.mBackground.setVisible(bool, false);
    }
    if (this.mStackedBackground != null) {
      this.mStackedBackground.setVisible(bool, false);
    }
    if (this.mSplitBackground != null) {
      this.mSplitBackground.setVisible(bool, false);
    }
  }
  
  public android.support.v7.view.ActionMode startActionModeForChild(View paramView, android.support.v7.view.ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  public android.view.ActionMode startActionModeForChild(View paramView, android.view.ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if (((paramDrawable != this.mBackground) || (this.mIsSplit)) && ((paramDrawable != this.mStackedBackground) || (!this.mIsStacked)) && ((paramDrawable != this.mSplitBackground) || (!this.mIsSplit)) && (!super.verifyDrawable(paramDrawable))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActionBarContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */