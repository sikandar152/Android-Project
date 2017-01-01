package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBarContextView
  extends AbsActionBarView
  implements ViewPropertyAnimatorListener
{
  private static final int ANIMATE_IDLE = 0;
  private static final int ANIMATE_IN = 1;
  private static final int ANIMATE_OUT = 2;
  private static final String TAG = "ActionBarContextView";
  private boolean mAnimateInOnLayout;
  private int mAnimationMode;
  private View mClose;
  private int mCloseItemLayout;
  private ViewPropertyAnimatorCompatSet mCurrentAnimation;
  private View mCustomView;
  private Drawable mSplitBackground;
  private CharSequence mSubtitle;
  private int mSubtitleStyleRes;
  private TextView mSubtitleView;
  private CharSequence mTitle;
  private LinearLayout mTitleLayout;
  private boolean mTitleOptional;
  private int mTitleStyleRes;
  private TextView mTitleView;
  
  public ActionBarContextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.actionModeStyle);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.ActionMode, paramInt, 0);
    setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.ActionMode_background));
    this.mTitleStyleRes = localTintTypedArray.getResourceId(R.styleable.ActionMode_titleTextStyle, 0);
    this.mSubtitleStyleRes = localTintTypedArray.getResourceId(R.styleable.ActionMode_subtitleTextStyle, 0);
    this.mContentHeight = localTintTypedArray.getLayoutDimension(R.styleable.ActionMode_height, 0);
    this.mSplitBackground = localTintTypedArray.getDrawable(R.styleable.ActionMode_backgroundSplit);
    this.mCloseItemLayout = localTintTypedArray.getResourceId(R.styleable.ActionMode_closeItemLayout, R.layout.abc_action_mode_close_item_material);
    localTintTypedArray.recycle();
  }
  
  private void finishAnimation()
  {
    ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = this.mCurrentAnimation;
    if (localViewPropertyAnimatorCompatSet != null)
    {
      this.mCurrentAnimation = null;
      localViewPropertyAnimatorCompatSet.cancel();
    }
  }
  
  private void initTitle()
  {
    int k = 8;
    if (this.mTitleLayout == null)
    {
      LayoutInflater.from(getContext()).inflate(R.layout.abc_action_bar_title_item, this);
      this.mTitleLayout = ((LinearLayout)getChildAt(-1 + getChildCount()));
      this.mTitleView = ((TextView)this.mTitleLayout.findViewById(R.id.action_bar_title));
      this.mSubtitleView = ((TextView)this.mTitleLayout.findViewById(R.id.action_bar_subtitle));
      if (this.mTitleStyleRes != 0) {
        this.mTitleView.setTextAppearance(getContext(), this.mTitleStyleRes);
      }
      if (this.mSubtitleStyleRes != 0) {
        this.mSubtitleView.setTextAppearance(getContext(), this.mSubtitleStyleRes);
      }
    }
    this.mTitleView.setText(this.mTitle);
    this.mSubtitleView.setText(this.mSubtitle);
    int i;
    if (TextUtils.isEmpty(this.mTitle)) {
      i = 0;
    } else {
      i = 1;
    }
    int j;
    if (TextUtils.isEmpty(this.mSubtitle)) {
      j = 0;
    } else {
      j = 1;
    }
    Object localObject = this.mSubtitleView;
    int m;
    if (j == 0) {
      m = k;
    } else {
      m = 0;
    }
    ((TextView)localObject).setVisibility(m);
    localObject = this.mTitleLayout;
    if ((i != 0) || (j != 0)) {
      k = 0;
    }
    ((LinearLayout)localObject).setVisibility(k);
    if (this.mTitleLayout.getParent() == null) {
      addView(this.mTitleLayout);
    }
  }
  
  private ViewPropertyAnimatorCompatSet makeInAnimation()
  {
    ViewCompat.setTranslationX(this.mClose, -this.mClose.getWidth() - ((ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams()).leftMargin);
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(this.mClose).translationX(0.0F);
    localViewPropertyAnimatorCompat.setDuration(200L);
    localViewPropertyAnimatorCompat.setListener(this);
    localViewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
    ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
    localViewPropertyAnimatorCompatSet.play(localViewPropertyAnimatorCompat);
    int i;
    if (this.mMenuView != null)
    {
      i = this.mMenuView.getChildCount();
      if (i > 0) {
        i -= 1;
      }
    }
    for (int j = 0;; j++)
    {
      if (i < 0) {
        return localViewPropertyAnimatorCompatSet;
      }
      Object localObject = this.mMenuView.getChildAt(i);
      ViewCompat.setScaleY((View)localObject, 0.0F);
      localObject = ViewCompat.animate((View)localObject).scaleY(1.0F);
      ((ViewPropertyAnimatorCompat)localObject).setDuration(300L);
      localViewPropertyAnimatorCompatSet.play((ViewPropertyAnimatorCompat)localObject);
      i--;
    }
  }
  
  private ViewPropertyAnimatorCompatSet makeOutAnimation()
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(this.mClose).translationX(-this.mClose.getWidth() - ((ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams()).leftMargin);
    localViewPropertyAnimatorCompat.setDuration(200L);
    localViewPropertyAnimatorCompat.setListener(this);
    localViewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
    ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
    localViewPropertyAnimatorCompatSet.play(localViewPropertyAnimatorCompat);
    if ((this.mMenuView != null) && (this.mMenuView.getChildCount() > 0)) {}
    for (int i = 0;; i++)
    {
      if (i >= 0) {
        return localViewPropertyAnimatorCompatSet;
      }
      Object localObject = this.mMenuView.getChildAt(i);
      ViewCompat.setScaleY((View)localObject, 1.0F);
      localObject = ViewCompat.animate((View)localObject).scaleY(0.0F);
      ((ViewPropertyAnimatorCompat)localObject).setDuration(300L);
      localViewPropertyAnimatorCompatSet.play((ViewPropertyAnimatorCompat)localObject);
    }
  }
  
  public void closeMode()
  {
    if (this.mAnimationMode != 2) {
      if (this.mClose != null)
      {
        finishAnimation();
        this.mAnimationMode = 2;
        this.mCurrentAnimation = makeOutAnimation();
        this.mCurrentAnimation.start();
      }
      else
      {
        killMode();
      }
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.MarginLayoutParams(-1, -2);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new ViewGroup.MarginLayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getSubtitle()
  {
    return this.mSubtitle;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
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
  
  public void initForMode(final ActionMode paramActionMode)
  {
    if (this.mClose != null)
    {
      if (this.mClose.getParent() == null) {
        addView(this.mClose);
      }
    }
    else
    {
      this.mClose = LayoutInflater.from(getContext()).inflate(this.mCloseItemLayout, this, false);
      addView(this.mClose);
    }
    this.mClose.findViewById(R.id.action_mode_close_button).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramActionMode.finish();
      }
    });
    MenuBuilder localMenuBuilder = (MenuBuilder)paramActionMode.getMenu();
    if (this.mActionMenuPresenter != null) {
      this.mActionMenuPresenter.dismissPopupMenus();
    }
    this.mActionMenuPresenter = new ActionMenuPresenter(getContext());
    this.mActionMenuPresenter.setReserveOverflow(true);
    ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-2, -1);
    if (this.mSplitActionBar)
    {
      this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
      this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
      localLayoutParams.width = -1;
      localLayoutParams.height = this.mContentHeight;
      localMenuBuilder.addMenuPresenter(this.mActionMenuPresenter, this.mPopupContext);
      this.mMenuView = ((ActionMenuView)this.mActionMenuPresenter.getMenuView(this));
      this.mMenuView.setBackgroundDrawable(this.mSplitBackground);
      this.mSplitView.addView(this.mMenuView, localLayoutParams);
    }
    else
    {
      localMenuBuilder.addMenuPresenter(this.mActionMenuPresenter, this.mPopupContext);
      this.mMenuView = ((ActionMenuView)this.mActionMenuPresenter.getMenuView(this));
      this.mMenuView.setBackgroundDrawable(null);
      addView(this.mMenuView, localLayoutParams);
    }
    this.mAnimateInOnLayout = true;
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
  
  public boolean isTitleOptional()
  {
    return this.mTitleOptional;
  }
  
  public void killMode()
  {
    finishAnimation();
    removeAllViews();
    if (this.mSplitView != null) {
      this.mSplitView.removeView(this.mMenuView);
    }
    this.mCustomView = null;
    this.mMenuView = null;
    this.mAnimateInOnLayout = false;
  }
  
  public void onAnimationCancel(View paramView) {}
  
  public void onAnimationEnd(View paramView)
  {
    if (this.mAnimationMode == 2) {
      killMode();
    }
    this.mAnimationMode = 0;
  }
  
  public void onAnimationStart(View paramView) {}
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mActionMenuPresenter != null)
    {
      this.mActionMenuPresenter.hideOverflowMenu();
      this.mActionMenuPresenter.hideSubMenus();
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (Build.VERSION.SDK_INT >= 14) {
      if (paramAccessibilityEvent.getEventType() != 32)
      {
        super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      }
      else
      {
        paramAccessibilityEvent.setSource(this);
        paramAccessibilityEvent.setClassName(getClass().getName());
        paramAccessibilityEvent.setPackageName(getContext().getPackageName());
        paramAccessibilityEvent.setContentDescription(this.mTitle);
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = ViewUtils.isLayoutRtl(this);
    int n;
    if (!bool) {
      n = getPaddingLeft();
    } else {
      n = paramInt3 - paramInt1 - getPaddingRight();
    }
    int j = getPaddingTop();
    int i = paramInt4 - paramInt2 - getPaddingTop() - getPaddingBottom();
    int k;
    if ((this.mClose != null) && (this.mClose.getVisibility() != 8))
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams();
      if (!bool) {
        k = localMarginLayoutParams.leftMargin;
      } else {
        k = localMarginLayoutParams.rightMargin;
      }
      int m;
      if (!bool) {
        m = localMarginLayoutParams.rightMargin;
      } else {
        m = m.leftMargin;
      }
      k = next(n, k, bool);
      n = next(k + positionChild(this.mClose, k, j, i, bool), m, bool);
      if (this.mAnimateInOnLayout)
      {
        this.mAnimationMode = 1;
        this.mCurrentAnimation = makeInAnimation();
        this.mCurrentAnimation.start();
        this.mAnimateInOnLayout = false;
      }
    }
    if ((this.mTitleLayout != null) && (this.mCustomView == null) && (this.mTitleLayout.getVisibility() != 8)) {
      n += positionChild(this.mTitleLayout, n, j, i, bool);
    }
    if (this.mCustomView != null) {
      (n + positionChild(this.mCustomView, n, j, i, bool));
    }
    if (!bool) {
      k = paramInt3 - paramInt1 - getPaddingRight();
    } else {
      k = getPaddingLeft();
    }
    if (this.mMenuView != null)
    {
      ActionMenuView localActionMenuView = this.mMenuView;
      if (bool) {
        bool = false;
      } else {
        bool = true;
      }
      (k + positionChild(localActionMenuView, k, j, i, bool));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824)
    {
      if (View.MeasureSpec.getMode(paramInt2) != 0)
      {
        int i = View.MeasureSpec.getSize(paramInt1);
        int k;
        if (this.mContentHeight <= 0) {
          k = View.MeasureSpec.getSize(paramInt2);
        } else {
          k = this.mContentHeight;
        }
        int j = getPaddingTop() + getPaddingBottom();
        int n = i - getPaddingLeft() - getPaddingRight();
        int m = k - j;
        int i2 = View.MeasureSpec.makeMeasureSpec(m, Integer.MIN_VALUE);
        int i4;
        int i1;
        if (this.mClose != null)
        {
          i4 = measureChildView(this.mClose, n, i2, 0);
          ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams();
          i1 = i4 - (localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin);
        }
        if ((this.mMenuView != null) && (this.mMenuView.getParent() == this)) {
          i1 = measureChildView(this.mMenuView, i1, i2, 0);
        }
        if ((this.mTitleLayout != null) && (this.mCustomView == null)) {
          if (!this.mTitleOptional)
          {
            i1 = measureChildView(this.mTitleLayout, i1, i2, 0);
          }
          else
          {
            i4 = View.MeasureSpec.makeMeasureSpec(0, 0);
            this.mTitleLayout.measure(i4, i2);
            i2 = this.mTitleLayout.getMeasuredWidth();
            if (i2 > i1) {
              i4 = 0;
            } else {
              i4 = 1;
            }
            if (i4 != 0) {
              i1 -= i2;
            }
            LinearLayout localLinearLayout = this.mTitleLayout;
            if (i4 == 0) {
              i4 = 8;
            } else {
              i4 = 0;
            }
            localLinearLayout.setVisibility(i4);
          }
        }
        if (this.mCustomView != null)
        {
          ViewGroup.LayoutParams localLayoutParams = this.mCustomView.getLayoutParams();
          if (localLayoutParams.width == -2) {
            i3 = Integer.MIN_VALUE;
          } else {
            i3 = 1073741824;
          }
          if (localLayoutParams.width < 0) {
            i1 = i1;
          } else {
            i1 = Math.min(localLayoutParams.width, i1);
          }
          int i5;
          if (localLayoutParams.height == -2) {
            i5 = Integer.MIN_VALUE;
          } else {
            i5 = 1073741824;
          }
          if (localLayoutParams.height < 0) {
            m = m;
          } else {
            m = Math.min(localLayoutParams.height, m);
          }
          this.mCustomView.measure(View.MeasureSpec.makeMeasureSpec(i1, i3), View.MeasureSpec.makeMeasureSpec(m, i5));
        }
        if (this.mContentHeight > 0)
        {
          setMeasuredDimension(i, k);
        }
        else
        {
          m = 0;
          k = getChildCount();
        }
        for (int i3 = 0;; i3++)
        {
          if (i3 >= k)
          {
            setMeasuredDimension(i, m);
            return;
          }
          i1 = j + getChildAt(i3).getMeasuredHeight();
          if (i1 > m) {
            m = i1;
          }
        }
      }
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_height=\"wrap_content\"");
    }
    throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_width=\"match_parent\" (or fill_parent)");
  }
  
  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = paramInt;
  }
  
  public void setCustomView(View paramView)
  {
    if (this.mCustomView != null) {
      removeView(this.mCustomView);
    }
    this.mCustomView = paramView;
    if (this.mTitleLayout != null)
    {
      removeView(this.mTitleLayout);
      this.mTitleLayout = null;
    }
    if (paramView != null) {
      addView(paramView);
    }
    requestLayout();
  }
  
  public void setSplitToolbar(boolean paramBoolean)
  {
    if (this.mSplitActionBar != paramBoolean)
    {
      if (this.mActionMenuPresenter != null)
      {
        ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-2, -1);
        ViewGroup localViewGroup;
        if (paramBoolean)
        {
          this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
          this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
          localLayoutParams.width = -1;
          localLayoutParams.height = this.mContentHeight;
          this.mMenuView = ((ActionMenuView)this.mActionMenuPresenter.getMenuView(this));
          this.mMenuView.setBackgroundDrawable(this.mSplitBackground);
          localViewGroup = (ViewGroup)this.mMenuView.getParent();
          if (localViewGroup != null) {
            localViewGroup.removeView(this.mMenuView);
          }
          this.mSplitView.addView(this.mMenuView, localLayoutParams);
        }
        else
        {
          this.mMenuView = ((ActionMenuView)this.mActionMenuPresenter.getMenuView(this));
          this.mMenuView.setBackgroundDrawable(null);
          localViewGroup = (ViewGroup)this.mMenuView.getParent();
          if (localViewGroup != null) {
            localViewGroup.removeView(this.mMenuView);
          }
          addView(this.mMenuView, localLayoutParams);
        }
      }
      super.setSplitToolbar(paramBoolean);
    }
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mSubtitle = paramCharSequence;
    initTitle();
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    initTitle();
  }
  
  public void setTitleOptional(boolean paramBoolean)
  {
    if (paramBoolean != this.mTitleOptional) {
      requestLayout();
    }
    this.mTitleOptional = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
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
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActionBarContextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */