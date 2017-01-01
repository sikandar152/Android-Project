package android.support.v7.internal.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.internal.VersionUtils;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

public class ActionBarOverlayLayout
  extends ViewGroup
  implements DecorContentParent
{
  static final int[] ATTRS;
  private static final String TAG = "ActionBarOverlayLayout";
  private final int ACTION_BAR_ANIMATE_DELAY = 600;
  private ActionBarContainer mActionBarBottom;
  private int mActionBarHeight;
  private ActionBarContainer mActionBarTop;
  private ActionBarVisibilityCallback mActionBarVisibilityCallback;
  private final Runnable mAddActionBarHideOffset = new Runnable()
  {
    public void run()
    {
      ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
      ActionBarOverlayLayout.access$002(ActionBarOverlayLayout.this, ViewCompat.animate(ActionBarOverlayLayout.this.mActionBarTop).translationY(-ActionBarOverlayLayout.this.mActionBarTop.getHeight()).setListener(ActionBarOverlayLayout.this.mTopAnimatorListener));
      if ((ActionBarOverlayLayout.this.mActionBarBottom != null) && (ActionBarOverlayLayout.this.mActionBarBottom.getVisibility() != 8)) {
        ActionBarOverlayLayout.access$202(ActionBarOverlayLayout.this, ViewCompat.animate(ActionBarOverlayLayout.this.mActionBarBottom).translationY(ActionBarOverlayLayout.this.mActionBarBottom.getHeight()).setListener(ActionBarOverlayLayout.this.mBottomAnimatorListener));
      }
    }
  };
  private boolean mAnimatingForFling;
  private final Rect mBaseContentInsets = new Rect();
  private final Rect mBaseInnerInsets = new Rect();
  private final ViewPropertyAnimatorListener mBottomAnimatorListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationCancel(View paramAnonymousView)
    {
      ActionBarOverlayLayout.access$202(ActionBarOverlayLayout.this, null);
      ActionBarOverlayLayout.access$102(ActionBarOverlayLayout.this, false);
    }
    
    public void onAnimationEnd(View paramAnonymousView)
    {
      ActionBarOverlayLayout.access$202(ActionBarOverlayLayout.this, null);
      ActionBarOverlayLayout.access$102(ActionBarOverlayLayout.this, false);
    }
  };
  private ContentFrameLayout mContent;
  private final Rect mContentInsets = new Rect();
  private ViewPropertyAnimatorCompat mCurrentActionBarBottomAnimator;
  private ViewPropertyAnimatorCompat mCurrentActionBarTopAnimator;
  private DecorToolbar mDecorToolbar;
  private ScrollerCompat mFlingEstimator;
  private boolean mHasNonEmbeddedTabs;
  private boolean mHideOnContentScroll;
  private int mHideOnContentScrollReference;
  private boolean mIgnoreWindowContentOverlay;
  private final Rect mInnerInsets = new Rect();
  private final Rect mLastBaseContentInsets = new Rect();
  private final Rect mLastInnerInsets = new Rect();
  private int mLastSystemUiVisibility;
  private boolean mOverlayMode;
  private final Runnable mRemoveActionBarHideOffset = new Runnable()
  {
    public void run()
    {
      ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
      ActionBarOverlayLayout.access$002(ActionBarOverlayLayout.this, ViewCompat.animate(ActionBarOverlayLayout.this.mActionBarTop).translationY(0.0F).setListener(ActionBarOverlayLayout.this.mTopAnimatorListener));
      if ((ActionBarOverlayLayout.this.mActionBarBottom != null) && (ActionBarOverlayLayout.this.mActionBarBottom.getVisibility() != 8)) {
        ActionBarOverlayLayout.access$202(ActionBarOverlayLayout.this, ViewCompat.animate(ActionBarOverlayLayout.this.mActionBarBottom).translationY(0.0F).setListener(ActionBarOverlayLayout.this.mBottomAnimatorListener));
      }
    }
  };
  private final ViewPropertyAnimatorListener mTopAnimatorListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationCancel(View paramAnonymousView)
    {
      ActionBarOverlayLayout.access$002(ActionBarOverlayLayout.this, null);
      ActionBarOverlayLayout.access$102(ActionBarOverlayLayout.this, false);
    }
    
    public void onAnimationEnd(View paramAnonymousView)
    {
      ActionBarOverlayLayout.access$002(ActionBarOverlayLayout.this, null);
      ActionBarOverlayLayout.access$102(ActionBarOverlayLayout.this, false);
    }
  };
  private Drawable mWindowContentOverlay;
  private int mWindowVisibility = 0;
  
  static
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = R.attr.actionBarSize;
    arrayOfInt[1] = 16842841;
    ATTRS = arrayOfInt;
  }
  
  public ActionBarOverlayLayout(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public ActionBarOverlayLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  private void addActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    this.mAddActionBarHideOffset.run();
  }
  
  private boolean applyInsets(View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    boolean bool = false;
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((paramBoolean1) && (localLayoutParams.leftMargin != paramRect.left))
    {
      bool = true;
      localLayoutParams.leftMargin = paramRect.left;
    }
    if ((paramBoolean2) && (localLayoutParams.topMargin != paramRect.top))
    {
      bool = true;
      localLayoutParams.topMargin = paramRect.top;
    }
    if ((paramBoolean4) && (localLayoutParams.rightMargin != paramRect.right))
    {
      bool = true;
      localLayoutParams.rightMargin = paramRect.right;
    }
    if ((paramBoolean3) && (localLayoutParams.bottomMargin != paramRect.bottom))
    {
      bool = true;
      localLayoutParams.bottomMargin = paramRect.bottom;
    }
    return bool;
  }
  
  private DecorToolbar getDecorToolbar(View paramView)
  {
    DecorToolbar localDecorToolbar;
    if (!(paramView instanceof DecorToolbar))
    {
      if (!(paramView instanceof Toolbar)) {
        throw new IllegalStateException("Can't make a decor toolbar out of " + paramView.getClass().getSimpleName());
      }
      localDecorToolbar = ((Toolbar)paramView).getWrapper();
    }
    else
    {
      localDecorToolbar = (DecorToolbar)paramView;
    }
    return localDecorToolbar;
  }
  
  private void haltActionBarHideOffsetAnimations()
  {
    removeCallbacks(this.mRemoveActionBarHideOffset);
    removeCallbacks(this.mAddActionBarHideOffset);
    if (this.mCurrentActionBarTopAnimator != null) {
      this.mCurrentActionBarTopAnimator.cancel();
    }
    if (this.mCurrentActionBarBottomAnimator != null) {
      this.mCurrentActionBarBottomAnimator.cancel();
    }
  }
  
  private void init(Context paramContext)
  {
    int i = 1;
    TypedArray localTypedArray = getContext().getTheme().obtainStyledAttributes(ATTRS);
    this.mActionBarHeight = localTypedArray.getDimensionPixelSize(0, 0);
    this.mWindowContentOverlay = localTypedArray.getDrawable(i);
    boolean bool;
    if (this.mWindowContentOverlay != null) {
      bool = false;
    } else {
      bool = i;
    }
    setWillNotDraw(bool);
    localTypedArray.recycle();
    if (paramContext.getApplicationInfo().targetSdkVersion >= 19) {
      i = 0;
    }
    this.mIgnoreWindowContentOverlay = i;
    this.mFlingEstimator = ScrollerCompat.create(paramContext);
  }
  
  private void postAddActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    postDelayed(this.mAddActionBarHideOffset, 600L);
  }
  
  private void postRemoveActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    postDelayed(this.mRemoveActionBarHideOffset, 600L);
  }
  
  private void removeActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    this.mRemoveActionBarHideOffset.run();
  }
  
  private boolean shouldHideActionBarOnFling(float paramFloat1, float paramFloat2)
  {
    boolean bool = false;
    this.mFlingEstimator.fling(0, 0, 0, (int)paramFloat2, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    if (this.mFlingEstimator.getFinalY() > this.mActionBarTop.getHeight()) {
      bool = true;
    }
    return bool;
  }
  
  public boolean canShowOverflowMenu()
  {
    pullChildren();
    return this.mDecorToolbar.canShowOverflowMenu();
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void dismissPopups()
  {
    pullChildren();
    this.mDecorToolbar.dismissPopupMenus();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if ((this.mWindowContentOverlay != null) && (!this.mIgnoreWindowContentOverlay))
    {
      int i;
      if (this.mActionBarTop.getVisibility() != 0) {
        i = 0;
      } else {
        i = (int)(0.5F + (this.mActionBarTop.getBottom() + ViewCompat.getTranslationY(this.mActionBarTop)));
      }
      this.mWindowContentOverlay.setBounds(0, i, getWidth(), i + this.mWindowContentOverlay.getIntrinsicHeight());
      this.mWindowContentOverlay.draw(paramCanvas);
    }
  }
  
  protected boolean fitSystemWindows(Rect paramRect)
  {
    pullChildren();
    if ((0x100 & ViewCompat.getWindowSystemUiVisibility(this)) == 0) {}
    boolean bool = applyInsets(this.mActionBarTop, paramRect, true, true, false, true);
    if (this.mActionBarBottom != null) {
      bool |= applyInsets(this.mActionBarBottom, paramRect, true, false, true, true);
    }
    this.mBaseInnerInsets.set(paramRect);
    ViewUtils.computeFitSystemWindows(this, this.mBaseInnerInsets, this.mBaseContentInsets);
    if (!this.mLastBaseContentInsets.equals(this.mBaseContentInsets))
    {
      bool = true;
      this.mLastBaseContentInsets.set(this.mBaseContentInsets);
    }
    if (bool) {
      requestLayout();
    }
    return true;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getActionBarHideOffset()
  {
    int i;
    if (this.mActionBarTop == null) {
      i = 0;
    } else {
      i = -(int)ViewCompat.getTranslationY(this.mActionBarTop);
    }
    return i;
  }
  
  public CharSequence getTitle()
  {
    pullChildren();
    return this.mDecorToolbar.getTitle();
  }
  
  public boolean hasIcon()
  {
    pullChildren();
    return this.mDecorToolbar.hasIcon();
  }
  
  public boolean hasLogo()
  {
    pullChildren();
    return this.mDecorToolbar.hasLogo();
  }
  
  public boolean hideOverflowMenu()
  {
    pullChildren();
    return this.mDecorToolbar.hideOverflowMenu();
  }
  
  public void initFeature(int paramInt)
  {
    pullChildren();
    switch (paramInt)
    {
    case 2: 
      this.mDecorToolbar.initProgress();
      break;
    case 5: 
      this.mDecorToolbar.initIndeterminateProgress();
      break;
    case 9: 
      setOverlayMode(true);
    }
  }
  
  public boolean isHideOnContentScrollEnabled()
  {
    return this.mHideOnContentScroll;
  }
  
  public boolean isInOverlayMode()
  {
    return this.mOverlayMode;
  }
  
  public boolean isOverflowMenuShowPending()
  {
    pullChildren();
    return this.mDecorToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing()
  {
    pullChildren();
    return this.mDecorToolbar.isOverflowMenuShowing();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (Build.VERSION.SDK_INT >= 8) {
      super.onConfigurationChanged(paramConfiguration);
    }
    init(getContext());
    ViewCompat.requestApplyInsets(this);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    haltActionBarHideOffsetAnimations();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = getChildCount();
    int i1 = getPaddingLeft();
    (paramInt3 - paramInt1 - getPaddingRight());
    int n = getPaddingTop();
    int i3 = paramInt4 - paramInt2 - getPaddingBottom();
    for (int k = 0;; k++)
    {
      if (k >= j) {
        return;
      }
      View localView = getChildAt(k);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i2 = localView.getMeasuredWidth();
        int m = localView.getMeasuredHeight();
        int i = i1 + localLayoutParams.leftMargin;
        int i4;
        if (localView != this.mActionBarBottom) {
          i4 = n + localLayoutParams.topMargin;
        } else {
          i4 = i3 - m - i4.bottomMargin;
        }
        localView.layout(i, i4, i + i2, i4 + m);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    pullChildren();
    int n = 0;
    int m = 0;
    measureChildWithMargins(this.mActionBarTop, paramInt1, 0, paramInt2, 0);
    LayoutParams localLayoutParams1 = (LayoutParams)this.mActionBarTop.getLayoutParams();
    int k = Math.max(0, this.mActionBarTop.getMeasuredWidth() + localLayoutParams1.leftMargin + localLayoutParams1.rightMargin);
    int j = Math.max(0, this.mActionBarTop.getMeasuredHeight() + localLayoutParams1.topMargin + localLayoutParams1.bottomMargin);
    int i = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(this.mActionBarTop));
    if (this.mActionBarBottom != null)
    {
      measureChildWithMargins(this.mActionBarBottom, paramInt1, 0, paramInt2, 0);
      LayoutParams localLayoutParams3 = (LayoutParams)this.mActionBarBottom.getLayoutParams();
      k = Math.max(k, this.mActionBarBottom.getMeasuredWidth() + localLayoutParams3.leftMargin + localLayoutParams3.rightMargin);
      j = Math.max(j, this.mActionBarBottom.getMeasuredHeight() + localLayoutParams3.topMargin + localLayoutParams3.bottomMargin);
      i = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(this.mActionBarBottom));
    }
    int i1;
    if ((0x100 & ViewCompat.getWindowSystemUiVisibility(this)) == 0) {
      i1 = 0;
    } else {
      i1 = 1;
    }
    if (i1 == 0)
    {
      if (this.mActionBarTop.getVisibility() != 8) {
        n = this.mActionBarTop.getMeasuredHeight();
      }
    }
    else
    {
      n = this.mActionBarHeight;
      if ((this.mHasNonEmbeddedTabs) && (this.mActionBarTop.getTabContainer() != null)) {
        n += this.mActionBarHeight;
      }
    }
    if ((this.mDecorToolbar.isSplit()) && (this.mActionBarBottom != null)) {
      if (i1 == 0) {
        m = this.mActionBarBottom.getMeasuredHeight();
      } else {
        m = this.mActionBarHeight;
      }
    }
    this.mContentInsets.set(this.mBaseContentInsets);
    this.mInnerInsets.set(this.mBaseInnerInsets);
    Rect localRect2;
    Rect localRect1;
    if ((this.mOverlayMode) || (i1 != 0))
    {
      localRect2 = this.mInnerInsets;
      localRect2.top = (n + localRect2.top);
      localRect1 = this.mInnerInsets;
      localRect1.bottom = (m + localRect1.bottom);
    }
    else
    {
      localRect2 = this.mContentInsets;
      localRect2.top = (localRect1 + localRect2.top);
      localRect1 = this.mContentInsets;
      localRect1.bottom = (m + localRect1.bottom);
    }
    applyInsets(this.mContent, this.mContentInsets, true, true, true, true);
    if (!this.mLastInnerInsets.equals(this.mInnerInsets))
    {
      this.mLastInnerInsets.set(this.mInnerInsets);
      this.mContent.dispatchFitSystemWindows(this.mInnerInsets);
    }
    measureChildWithMargins(this.mContent, paramInt1, 0, paramInt2, 0);
    LayoutParams localLayoutParams2 = (LayoutParams)this.mContent.getLayoutParams();
    k = Math.max(k, this.mContent.getMeasuredWidth() + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin);
    j = Math.max(j, this.mContent.getMeasuredHeight() + localLayoutParams2.topMargin + localLayoutParams2.bottomMargin);
    i = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(this.mContent));
    k += getPaddingLeft() + getPaddingRight();
    j = Math.max(j + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
    setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(k, getSuggestedMinimumWidth()), paramInt1, i), ViewCompat.resolveSizeAndState(j, paramInt2, i << 16));
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    boolean bool = true;
    if ((this.mHideOnContentScroll) && (paramBoolean))
    {
      if (!shouldHideActionBarOnFling(paramFloat1, paramFloat2)) {
        removeActionBarHideOffset();
      } else {
        addActionBarHideOffset();
      }
      this.mAnimatingForFling = bool;
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mHideOnContentScrollReference = (paramInt2 + this.mHideOnContentScrollReference);
    setActionBarHideOffset(this.mHideOnContentScrollReference);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    super.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    this.mHideOnContentScrollReference = getActionBarHideOffset();
    haltActionBarHideOffsetAnimations();
    if (this.mActionBarVisibilityCallback != null) {
      this.mActionBarVisibilityCallback.onContentScrollStarted();
    }
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    boolean bool;
    if (((paramInt & 0x2) != 0) && (this.mActionBarTop.getVisibility() == 0)) {
      bool = this.mHideOnContentScroll;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    super.onStopNestedScroll(paramView);
    if ((this.mHideOnContentScroll) && (!this.mAnimatingForFling)) {
      if (this.mHideOnContentScrollReference > this.mActionBarTop.getHeight()) {
        postAddActionBarHideOffset();
      } else {
        postRemoveActionBarHideOffset();
      }
    }
    if (this.mActionBarVisibilityCallback != null) {
      this.mActionBarVisibilityCallback.onContentScrollStopped();
    }
  }
  
  public void onWindowSystemUiVisibilityChanged(int paramInt)
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 16) {
      super.onWindowSystemUiVisibilityChanged(paramInt);
    }
    pullChildren();
    int j = paramInt ^ this.mLastSystemUiVisibility;
    this.mLastSystemUiVisibility = paramInt;
    int i;
    if ((paramInt & 0x4) != 0) {
      i = 0;
    } else {
      i = bool;
    }
    int k;
    if ((paramInt & 0x100) == 0) {
      k = 0;
    } else {
      k = bool;
    }
    if (this.mActionBarVisibilityCallback != null)
    {
      ActionBarVisibilityCallback localActionBarVisibilityCallback = this.mActionBarVisibilityCallback;
      if (k != 0) {
        bool = false;
      }
      localActionBarVisibilityCallback.enableContentAnimations(bool);
      if ((i == 0) && (k != 0)) {
        this.mActionBarVisibilityCallback.hideForSystem();
      } else {
        this.mActionBarVisibilityCallback.showForSystem();
      }
    }
    if (((j & 0x100) != 0) && (this.mActionBarVisibilityCallback != null)) {
      ViewCompat.requestApplyInsets(this);
    }
  }
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    super.onWindowVisibilityChanged(paramInt);
    this.mWindowVisibility = paramInt;
    if (this.mActionBarVisibilityCallback != null) {
      this.mActionBarVisibilityCallback.onWindowVisibilityChanged(paramInt);
    }
  }
  
  void pullChildren()
  {
    if (this.mContent == null)
    {
      this.mContent = ((ContentFrameLayout)findViewById(R.id.action_bar_activity_content));
      this.mActionBarTop = ((ActionBarContainer)findViewById(R.id.action_bar_container));
      this.mDecorToolbar = getDecorToolbar(findViewById(R.id.action_bar));
      this.mActionBarBottom = ((ActionBarContainer)findViewById(R.id.split_action_bar));
    }
  }
  
  public void restoreToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    pullChildren();
    this.mDecorToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    pullChildren();
    this.mDecorToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setActionBarHideOffset(int paramInt)
  {
    haltActionBarHideOffsetAnimations();
    int j = this.mActionBarTop.getHeight();
    int i = Math.max(0, Math.min(paramInt, j));
    ViewCompat.setTranslationY(this.mActionBarTop, -i);
    if ((this.mActionBarBottom != null) && (this.mActionBarBottom.getVisibility() != 8))
    {
      i = (int)(i / j * this.mActionBarBottom.getHeight());
      ViewCompat.setTranslationY(this.mActionBarBottom, i);
    }
  }
  
  public void setActionBarVisibilityCallback(ActionBarVisibilityCallback paramActionBarVisibilityCallback)
  {
    this.mActionBarVisibilityCallback = paramActionBarVisibilityCallback;
    if (getWindowToken() != null)
    {
      this.mActionBarVisibilityCallback.onWindowVisibilityChanged(this.mWindowVisibility);
      if (this.mLastSystemUiVisibility != 0)
      {
        onWindowSystemUiVisibilityChanged(this.mLastSystemUiVisibility);
        ViewCompat.requestApplyInsets(this);
      }
    }
  }
  
  public void setHasNonEmbeddedTabs(boolean paramBoolean)
  {
    this.mHasNonEmbeddedTabs = paramBoolean;
  }
  
  public void setHideOnContentScrollEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mHideOnContentScroll)
    {
      this.mHideOnContentScroll = paramBoolean;
      if (!paramBoolean)
      {
        if (VersionUtils.isAtLeastL()) {
          stopNestedScroll();
        }
        haltActionBarHideOffsetAnimations();
        setActionBarHideOffset(0);
      }
    }
  }
  
  public void setIcon(int paramInt)
  {
    pullChildren();
    this.mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    pullChildren();
    this.mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setLogo(int paramInt)
  {
    pullChildren();
    this.mDecorToolbar.setLogo(paramInt);
  }
  
  public void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback)
  {
    pullChildren();
    this.mDecorToolbar.setMenu(paramMenu, paramCallback);
  }
  
  public void setMenuPrepared()
  {
    pullChildren();
    this.mDecorToolbar.setMenuPrepared();
  }
  
  public void setOverlayMode(boolean paramBoolean)
  {
    this.mOverlayMode = paramBoolean;
    boolean bool;
    if ((!paramBoolean) || (getContext().getApplicationInfo().targetSdkVersion >= 19)) {
      bool = false;
    } else {
      bool = true;
    }
    this.mIgnoreWindowContentOverlay = bool;
  }
  
  public void setShowingForActionMode(boolean paramBoolean) {}
  
  public void setUiOptions(int paramInt) {}
  
  public void setWindowCallback(WindowCallback paramWindowCallback)
  {
    pullChildren();
    this.mDecorToolbar.setWindowCallback(paramWindowCallback);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    pullChildren();
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public boolean showOverflowMenu()
  {
    pullChildren();
    return this.mDecorToolbar.showOverflowMenu();
  }
  
  public static abstract interface ActionBarVisibilityCallback
  {
    public abstract void enableContentAnimations(boolean paramBoolean);
    
    public abstract void hideForSystem();
    
    public abstract void onContentScrollStarted();
    
    public abstract void onContentScrollStopped();
    
    public abstract void onWindowVisibilityChanged(int paramInt);
    
    public abstract void showForSystem();
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActionBarOverlayLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */