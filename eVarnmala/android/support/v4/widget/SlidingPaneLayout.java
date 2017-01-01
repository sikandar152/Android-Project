package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout
  extends ViewGroup
{
  private static final int DEFAULT_FADE_COLOR = -858993460;
  private static final int DEFAULT_OVERHANG_SIZE = 32;
  static final SlidingPanelLayoutImpl IMPL;
  private static final int MIN_FLING_VELOCITY = 400;
  private static final String TAG = "SlidingPaneLayout";
  private boolean mCanSlide;
  private int mCoveredFadeColor;
  private final ViewDragHelper mDragHelper;
  private boolean mFirstLayout = true;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private boolean mIsUnableToDrag;
  private final int mOverhangSize;
  private PanelSlideListener mPanelSlideListener;
  private int mParallaxBy;
  private float mParallaxOffset;
  private final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList();
  private boolean mPreservedOpenState;
  private Drawable mShadowDrawableLeft;
  private Drawable mShadowDrawableRight;
  private float mSlideOffset;
  private int mSlideRange;
  private View mSlideableView;
  private int mSliderFadeColor = -858993460;
  private final Rect mTmpRect = new Rect();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 17)
    {
      if (i < 16) {
        IMPL = new SlidingPanelLayoutImplBase();
      } else {
        IMPL = new SlidingPanelLayoutImplJB();
      }
    }
    else {
      IMPL = new SlidingPanelLayoutImplJBMR1();
    }
  }
  
  public SlidingPaneLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mOverhangSize = ((int)(0.5F + 32.0F * f));
    ViewConfiguration.get(paramContext);
    setWillNotDraw(false);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewCompat.setImportantForAccessibility(this, 1);
    this.mDragHelper = ViewDragHelper.create(this, 0.5F, new DragHelperCallback(null));
    this.mDragHelper.setMinVelocity(400.0F * f);
  }
  
  private boolean closePane(View paramView, int paramInt)
  {
    boolean bool = false;
    if ((this.mFirstLayout) || (smoothSlideTo(0.0F, paramInt)))
    {
      this.mPreservedOpenState = false;
      bool = true;
    }
    return bool;
  }
  
  private void dimChildView(View paramView, float paramFloat, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((paramFloat <= 0.0F) || (paramInt == 0))
    {
      if (ViewCompat.getLayerType(paramView) != 0)
      {
        if (localLayoutParams.dimPaint != null) {
          localLayoutParams.dimPaint.setColorFilter(null);
        }
        DisableLayerRunnable localDisableLayerRunnable = new DisableLayerRunnable(paramView);
        this.mPostedRunnables.add(localDisableLayerRunnable);
        ViewCompat.postOnAnimation(this, localDisableLayerRunnable);
      }
    }
    else
    {
      int i = (int)(paramFloat * ((0xFF000000 & paramInt) >>> 24)) << 24 | 0xFFFFFF & paramInt;
      if (localLayoutParams.dimPaint == null) {
        localLayoutParams.dimPaint = new Paint();
      }
      localLayoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_OVER));
      if (ViewCompat.getLayerType(paramView) != 2) {
        ViewCompat.setLayerType(paramView, 2, localLayoutParams.dimPaint);
      }
      invalidateChildRegion(paramView);
    }
  }
  
  private void invalidateChildRegion(View paramView)
  {
    IMPL.invalidateChildRegion(this, paramView);
  }
  
  private boolean isLayoutRtlSupport()
  {
    int i = 1;
    if (ViewCompat.getLayoutDirection(this) != i) {
      i = 0;
    }
    return i;
  }
  
  private void onPanelDragged(int paramInt)
  {
    if (this.mSlideableView != null)
    {
      boolean bool = isLayoutRtlSupport();
      LayoutParams localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
      int j = this.mSlideableView.getWidth();
      int k;
      if (!bool) {
        k = paramInt;
      } else {
        k = getWidth() - paramInt - j;
      }
      if (!bool) {
        j = getPaddingLeft();
      } else {
        j = getPaddingRight();
      }
      int i;
      if (!bool) {
        i = localLayoutParams.leftMargin;
      } else {
        i = localLayoutParams.rightMargin;
      }
      this.mSlideOffset = ((k - (j + i)) / this.mSlideRange);
      if (this.mParallaxBy != 0) {
        parallaxOtherViews(this.mSlideOffset);
      }
      if (localLayoutParams.dimWhenOffset) {
        dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
      }
      dispatchOnPanelSlide(this.mSlideableView);
    }
    else
    {
      this.mSlideOffset = 0.0F;
    }
  }
  
  private boolean openPane(View paramView, int paramInt)
  {
    boolean bool = true;
    if ((!this.mFirstLayout) && (!smoothSlideTo(1.0F, paramInt))) {
      bool = false;
    } else {
      this.mPreservedOpenState = bool;
    }
    return bool;
  }
  
  private void parallaxOtherViews(float paramFloat)
  {
    boolean bool = isLayoutRtlSupport();
    LayoutParams localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
    if (localLayoutParams.dimWhenOffset)
    {
      if (!bool) {
        i = localLayoutParams.leftMargin;
      } else {
        i = i.rightMargin;
      }
      if (i <= 0) {}
    }
    else
    {
      k = 0;
      break label53;
    }
    int k = 1;
    label53:
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      View localView = getChildAt(j);
      if (localView != this.mSlideableView)
      {
        int m = (int)((1.0F - this.mParallaxOffset) * this.mParallaxBy);
        this.mParallaxOffset = paramFloat;
        m -= (int)((1.0F - paramFloat) * this.mParallaxBy);
        if (bool) {
          m = -m;
        }
        localView.offsetLeftAndRight(m);
        if (k != 0)
        {
          float f;
          if (!bool) {
            f = 1.0F - this.mParallaxOffset;
          } else {
            f = this.mParallaxOffset - 1.0F;
          }
          dimChildView(localView, f, this.mCoveredFadeColor);
        }
      }
    }
  }
  
  private static boolean viewIsOpaque(View paramView)
  {
    boolean bool = true;
    if (!ViewCompat.isOpaque(paramView)) {
      if (Build.VERSION.SDK_INT < 18)
      {
        Drawable localDrawable = paramView.getBackground();
        if (localDrawable == null) {
          bool = false;
        } else if (localDrawable.getOpacity() != -1) {
          bool = false;
        }
      }
      else
      {
        bool = false;
      }
    }
    return bool;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    ViewGroup localViewGroup;
    int k;
    if ((paramView instanceof ViewGroup))
    {
      localViewGroup = (ViewGroup)paramView;
      k = paramView.getScrollX();
      i = paramView.getScrollY();
    }
    for (int n = -1 + localViewGroup.getChildCount();; n--)
    {
      if (n < 0)
      {
        if (paramBoolean)
        {
          if (!isLayoutRtlSupport()) {
            paramInt1 = -paramInt1;
          }
          if (ViewCompat.canScrollHorizontally(paramView, paramInt1)) {}
        }
        else
        {
          i = 0;
          break label185;
        }
        i = 1;
        break label185;
      }
      View localView = localViewGroup.getChildAt(n);
      if ((paramInt2 + k >= localView.getLeft()) && (paramInt2 + k < localView.getRight()) && (paramInt3 + i >= localView.getTop()) && (paramInt3 + i < localView.getBottom()))
      {
        int j = paramInt2 + k - localView.getLeft();
        int m = paramInt3 + i - localView.getTop();
        if (canScroll(localView, true, paramInt1, j, m)) {
          break;
        }
      }
    }
    int i = 1;
    label185:
    return i;
  }
  
  @Deprecated
  public boolean canSlide()
  {
    return this.mCanSlide;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((!(paramLayoutParams instanceof LayoutParams)) || (!super.checkLayoutParams(paramLayoutParams))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean closePane()
  {
    return closePane(this.mSlideableView, 0);
  }
  
  public void computeScroll()
  {
    if (this.mDragHelper.continueSettling(true)) {
      if (this.mCanSlide) {
        ViewCompat.postInvalidateOnAnimation(this);
      } else {
        this.mDragHelper.abort();
      }
    }
  }
  
  void dispatchOnPanelClosed(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelClosed(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelOpened(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelOpened(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelSlide(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelSlide(paramView, this.mSlideOffset);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    Drawable localDrawable;
    if (!isLayoutRtlSupport()) {
      localDrawable = this.mShadowDrawableLeft;
    } else {
      localDrawable = this.mShadowDrawableRight;
    }
    View localView;
    if (getChildCount() <= 1) {
      localView = null;
    } else {
      localView = getChildAt(1);
    }
    if ((localView != null) && (localDrawable != null))
    {
      int k = localView.getTop();
      int j = localView.getBottom();
      int i = localDrawable.getIntrinsicWidth();
      int m;
      int n;
      if (!isLayoutRtlSupport())
      {
        m = localView.getLeft();
        n = m - i;
      }
      else
      {
        n = m.getRight();
        m = n + i;
      }
      localDrawable.setBounds(n, k, m, j);
      localDrawable.draw(paramCanvas);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramCanvas.save(2);
    if ((this.mCanSlide) && (!localLayoutParams.slideable) && (this.mSlideableView != null))
    {
      paramCanvas.getClipBounds(this.mTmpRect);
      if (!isLayoutRtlSupport()) {
        this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
      } else {
        this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
      }
      paramCanvas.clipRect(this.mTmpRect);
    }
    boolean bool;
    if (Build.VERSION.SDK_INT < 11)
    {
      if ((!localLayoutParams.dimWhenOffset) || (this.mSlideOffset <= 0.0F))
      {
        if (paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(false);
        }
        bool = super.drawChild(paramCanvas, paramView, paramLong);
      }
      else
      {
        if (!paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(true);
        }
        Bitmap localBitmap = paramView.getDrawingCache();
        if (localBitmap == null)
        {
          Log.e("SlidingPaneLayout", "drawChild: child view " + paramView + " returned null drawing cache");
          bool = super.drawChild(paramCanvas, paramView, paramLong);
        }
        else
        {
          paramCanvas.drawBitmap(localBitmap, paramView.getLeft(), paramView.getTop(), bool.dimPaint);
          bool = false;
        }
      }
    }
    else {
      bool = super.drawChild(paramCanvas, paramView, paramLong);
    }
    paramCanvas.restoreToCount(i);
    return bool;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    LayoutParams localLayoutParams;
    if (!(paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      localLayoutParams = new LayoutParams(paramLayoutParams);
    } else {
      localLayoutParams = new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return localLayoutParams;
  }
  
  public int getCoveredFadeColor()
  {
    return this.mCoveredFadeColor;
  }
  
  public int getParallaxDistance()
  {
    return this.mParallaxBy;
  }
  
  public int getSliderFadeColor()
  {
    return this.mSliderFadeColor;
  }
  
  boolean isDimmed(View paramView)
  {
    boolean bool = false;
    if (paramView != null)
    {
      LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
      if ((this.mCanSlide) && (localLayoutParams.dimWhenOffset) && (this.mSlideOffset > 0.0F)) {
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean isOpen()
  {
    boolean bool;
    if ((this.mCanSlide) && (this.mSlideOffset != 1.0F)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSlideable()
  {
    return this.mCanSlide;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mFirstLayout = true;
    int j = 0;
    int i = this.mPostedRunnables.size();
    for (;;)
    {
      if (j >= i)
      {
        this.mPostedRunnables.clear();
        return;
      }
      ((DisableLayerRunnable)this.mPostedRunnables.get(j)).run();
      j++;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool;
    if ((!this.mCanSlide) && (i == 0) && (getChildCount() > 1))
    {
      View localView = getChildAt(1);
      if (localView != null)
      {
        if (this.mDragHelper.isViewUnder(localView, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
          bool = false;
        } else {
          bool = true;
        }
        this.mPreservedOpenState = bool;
      }
    }
    if ((this.mCanSlide) && ((!this.mIsUnableToDrag) || (i == 0)))
    {
      if ((i != 3) && (i != 1))
      {
        bool = false;
        float f1;
        float f2;
        switch (i)
        {
        case 0: 
          this.mIsUnableToDrag = false;
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          this.mInitialMotionX = f1;
          this.mInitialMotionY = f2;
          if ((this.mDragHelper.isViewUnder(this.mSlideableView, (int)f1, (int)f2)) && (isDimmed(this.mSlideableView))) {
            bool = true;
          }
          break;
        case 2: 
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          f1 = Math.abs(f1 - this.mInitialMotionX);
          f2 = Math.abs(f2 - this.mInitialMotionY);
          if ((f1 > this.mDragHelper.getTouchSlop()) && (f2 > f1)) {
            break label264;
          }
        }
        if ((!this.mDragHelper.shouldInterceptTouchEvent(paramMotionEvent)) && (!bool))
        {
          bool = false;
        }
        else
        {
          return true;
          label264:
          this.mDragHelper.cancel();
          this.mIsUnableToDrag = true;
          bool = false;
        }
      }
      else
      {
        this.mDragHelper.cancel();
        bool = false;
      }
    }
    else
    {
      this.mDragHelper.cancel();
      bool = super.onInterceptTouchEvent(paramMotionEvent);
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = isLayoutRtlSupport();
    if (j == 0) {
      this.mDragHelper.setEdgeTrackingEnabled(1);
    } else {
      this.mDragHelper.setEdgeTrackingEnabled(2);
    }
    int m = paramInt3 - paramInt1;
    if (j == 0) {
      i1 = getPaddingLeft();
    } else {
      i1 = getPaddingRight();
    }
    int n;
    if (j == 0) {
      n = getPaddingRight();
    } else {
      n = getPaddingLeft();
    }
    int k = getPaddingTop();
    int i = getChildCount();
    int i1 = i1;
    int i2 = i1;
    if (this.mFirstLayout)
    {
      float f;
      if ((!this.mCanSlide) || (!this.mPreservedOpenState)) {
        f = 0.0F;
      } else {
        f = 1.0F;
      }
      this.mSlideOffset = f;
    }
    for (int i4 = 0;; i4++)
    {
      if (i4 >= i)
      {
        if (this.mFirstLayout)
        {
          if (!this.mCanSlide) {
            for (j = 0; j < i; j++) {
              dimChildView(getChildAt(j), 0.0F, this.mSliderFadeColor);
            }
          }
          if (this.mParallaxBy != 0) {
            parallaxOtherViews(this.mSlideOffset);
          }
          if (((LayoutParams)this.mSlideableView.getLayoutParams()).dimWhenOffset) {
            dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
          }
          updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
        return;
      }
      View localView = getChildAt(i4);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i3 = localView.getMeasuredWidth();
        int i5 = 0;
        int i6;
        if (!localLayoutParams.slideable)
        {
          if ((!this.mCanSlide) || (this.mParallaxBy == 0))
          {
            i1 = i2;
          }
          else
          {
            i5 = (int)((1.0F - this.mSlideOffset) * this.mParallaxBy);
            i1 = i2;
          }
        }
        else
        {
          int i7 = localLayoutParams.leftMargin + localLayoutParams.rightMargin;
          int i8 = m - n - this.mOverhangSize;
          int i9 = Math.min(i2, i8) - i1 - i7;
          this.mSlideRange = i9;
          if (j == 0) {
            i7 = localLayoutParams.leftMargin;
          } else {
            i7 = localLayoutParams.rightMargin;
          }
          if (i9 + (i1 + i7) + i3 / 2 <= m - n) {
            i8 = 0;
          } else {
            i8 = 1;
          }
          localLayoutParams.dimWhenOffset = i8;
          i6 = (int)(i9 * this.mSlideOffset);
          i1 += i6 + i7;
          this.mSlideOffset = (i6 / this.mSlideRange);
        }
        if (j == 0)
        {
          i6 = i1 - i5;
          i5 = i6 + i3;
        }
        else
        {
          i5 += m - i1;
          i6 = i5 - i3;
        }
        localView.layout(i6, k, i5, k + localView.getMeasuredHeight());
        i2 += localView.getWidth();
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt1);
    int i3 = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt2);
    if (j == 1073741824)
    {
      if (i3 == 0)
      {
        if (!isInEditMode()) {
          throw new IllegalStateException("Height must not be UNSPECIFIED");
        }
        if (i3 == 0)
        {
          i3 = Integer.MIN_VALUE;
          m = 300;
        }
      }
    }
    else
    {
      if (!isInEditMode()) {
        throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
      }
      if ((j != Integer.MIN_VALUE) && (j == 0)) {
        i = 300;
      }
    }
    int k = 0;
    j = -1;
    switch (i3)
    {
    case -2147483648: 
      j = m - getPaddingTop() - getPaddingBottom();
      break;
    case 1073741824: 
      j = m - getPaddingTop() - getPaddingBottom();
      k = j;
    }
    float f = 0.0F;
    int n = 0;
    int i2 = i - getPaddingLeft() - getPaddingRight();
    int i1 = i2;
    m = getChildCount();
    if (m > 2) {
      Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
    }
    this.mSlideableView = null;
    View localView1;
    for (int i5 = 0;; localView1++)
    {
      int i7;
      int i8;
      if (i5 >= m)
      {
        int i4;
        if ((n != 0) || (f > 0.0F)) {
          i4 = i2 - this.mOverhangSize;
        }
        for (i3 = 0;; i3++)
        {
          if (i3 >= m)
          {
            setMeasuredDimension(i, k + getPaddingTop() + getPaddingBottom());
            this.mCanSlide = n;
            if ((this.mDragHelper.getViewDragState() != 0) && (n == 0)) {
              this.mDragHelper.abort();
            }
            return;
          }
          localView1 = getChildAt(i3);
          if (localView1.getVisibility() != 8)
          {
            LayoutParams localLayoutParams2 = (LayoutParams)localView1.getLayoutParams();
            if (localView1.getVisibility() != 8)
            {
              if ((localLayoutParams2.width != 0) || (localLayoutParams2.weight <= 0.0F)) {
                i7 = 0;
              } else {
                i7 = 1;
              }
              int i6;
              if (i7 == 0) {
                i6 = localView1.getMeasuredWidth();
              } else {
                i6 = 0;
              }
              if ((n == 0) || (localView1 == this.mSlideableView))
              {
                if (localLayoutParams2.weight > 0.0F)
                {
                  if (localLayoutParams2.width != 0) {
                    i7 = View.MeasureSpec.makeMeasureSpec(localView1.getMeasuredHeight(), 1073741824);
                  } else if (localLayoutParams2.height != -2)
                  {
                    if (localLayoutParams2.height != -1) {
                      i7 = View.MeasureSpec.makeMeasureSpec(localLayoutParams2.height, 1073741824);
                    } else {
                      i7 = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
                    }
                  }
                  else {
                    i7 = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
                  }
                  int i9;
                  if (n == 0)
                  {
                    i9 = Math.max(0, i1);
                    localView1.measure(View.MeasureSpec.makeMeasureSpec(i6 + (int)(localLayoutParams2.weight * i9 / f), 1073741824), i7);
                  }
                  else
                  {
                    i8 = i2 - (localLayoutParams2.leftMargin + localLayoutParams2.rightMargin);
                    i9 = View.MeasureSpec.makeMeasureSpec(i8, 1073741824);
                    if (i6 != i8) {
                      localView1.measure(i9, i7);
                    }
                  }
                }
              }
              else if ((i8.width < 0) && ((i6 > i4) || (i8.weight > 0.0F)))
              {
                if (i7 == 0) {
                  i6 = View.MeasureSpec.makeMeasureSpec(localView1.getMeasuredHeight(), 1073741824);
                } else if (i8.height != -2)
                {
                  if (i8.height != -1) {
                    i6 = View.MeasureSpec.makeMeasureSpec(i8.height, 1073741824);
                  } else {
                    i6 = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
                  }
                }
                else {
                  i6 = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
                }
                localView1.measure(View.MeasureSpec.makeMeasureSpec(i4, 1073741824), i6);
              }
            }
          }
        }
      }
      View localView2 = getChildAt(localView1);
      LayoutParams localLayoutParams1 = (LayoutParams)localView2.getLayoutParams();
      if (localView2.getVisibility() != 8)
      {
        if (localLayoutParams1.weight > 0.0F)
        {
          f += localLayoutParams1.weight;
          if (localLayoutParams1.width == 0) {}
        }
        else
        {
          i7 = localLayoutParams1.leftMargin + localLayoutParams1.rightMargin;
          if (localLayoutParams1.width != -2)
          {
            if (localLayoutParams1.width != -1) {
              i7 = View.MeasureSpec.makeMeasureSpec(localLayoutParams1.width, 1073741824);
            } else {
              i7 = View.MeasureSpec.makeMeasureSpec(i2 - i7, 1073741824);
            }
          }
          else {
            i7 = View.MeasureSpec.makeMeasureSpec(i2 - i7, Integer.MIN_VALUE);
          }
          if (localLayoutParams1.height != -2)
          {
            if (localLayoutParams1.height != -1) {
              i8 = View.MeasureSpec.makeMeasureSpec(localLayoutParams1.height, 1073741824);
            } else {
              i8 = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
            }
          }
          else {
            i8 = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
          }
          localView2.measure(i7, i8);
          i7 = localView2.getMeasuredWidth();
          i8 = localView2.getMeasuredHeight();
          if ((i3 == Integer.MIN_VALUE) && (i8 > k)) {
            k = Math.min(i8, j);
          }
          i1 -= i7;
          if (i1 >= 0) {
            i7 = 0;
          } else {
            i7 = 1;
          }
          localLayoutParams1.slideable = i7;
          n |= i7;
          if (localLayoutParams1.slideable) {
            this.mSlideableView = localView2;
          }
        }
      }
      else {
        localLayoutParams1.dimWhenOffset = false;
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (!localSavedState.isOpen) {
      closePane();
    } else {
      openPane();
    }
    this.mPreservedOpenState = localSavedState.isOpen;
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    boolean bool;
    if (!isSlideable()) {
      bool = this.mPreservedOpenState;
    } else {
      bool = isOpen();
    }
    localSavedState.isOpen = bool;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      this.mFirstLayout = true;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (this.mCanSlide)
    {
      this.mDragHelper.processTouchEvent(paramMotionEvent);
      int i = paramMotionEvent.getAction();
      bool = true;
      float f2;
      switch (i & 0xFF)
      {
      default: 
        break;
      case 0: 
        f2 = paramMotionEvent.getX();
        float f1 = paramMotionEvent.getY();
        this.mInitialMotionX = f2;
        this.mInitialMotionY = f1;
        break;
      case 1: 
        if (!isDimmed(this.mSlideableView)) {
          break;
        }
        f2 = paramMotionEvent.getX();
        float f3 = paramMotionEvent.getY();
        float f5 = f2 - this.mInitialMotionX;
        float f4 = f3 - this.mInitialMotionY;
        int j = this.mDragHelper.getTouchSlop();
        if ((f5 * f5 + f4 * f4 >= j * j) || (!this.mDragHelper.isViewUnder(this.mSlideableView, (int)f2, (int)f3))) {
          break;
        }
        closePane(this.mSlideableView, 0);
        break;
      }
    }
    else
    {
      bool = super.onTouchEvent(paramMotionEvent);
    }
    return bool;
  }
  
  public boolean openPane()
  {
    return openPane(this.mSlideableView, 0);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    if ((!isInTouchMode()) && (!this.mCanSlide))
    {
      boolean bool;
      if (paramView1 != this.mSlideableView) {
        bool = false;
      } else {
        bool = true;
      }
      this.mPreservedOpenState = bool;
    }
  }
  
  void setAllChildrenVisible()
  {
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      if (i >= j) {
        return;
      }
      View localView = getChildAt(i);
      if (localView.getVisibility() == 4) {
        localView.setVisibility(0);
      }
      i++;
    }
  }
  
  public void setCoveredFadeColor(int paramInt)
  {
    this.mCoveredFadeColor = paramInt;
  }
  
  public void setPanelSlideListener(PanelSlideListener paramPanelSlideListener)
  {
    this.mPanelSlideListener = paramPanelSlideListener;
  }
  
  public void setParallaxDistance(int paramInt)
  {
    this.mParallaxBy = paramInt;
    requestLayout();
  }
  
  @Deprecated
  public void setShadowDrawable(Drawable paramDrawable)
  {
    setShadowDrawableLeft(paramDrawable);
  }
  
  public void setShadowDrawableLeft(Drawable paramDrawable)
  {
    this.mShadowDrawableLeft = paramDrawable;
  }
  
  public void setShadowDrawableRight(Drawable paramDrawable)
  {
    this.mShadowDrawableRight = paramDrawable;
  }
  
  @Deprecated
  public void setShadowResource(int paramInt)
  {
    setShadowDrawable(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceLeft(int paramInt)
  {
    setShadowDrawableLeft(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceRight(int paramInt)
  {
    setShadowDrawableRight(getResources().getDrawable(paramInt));
  }
  
  public void setSliderFadeColor(int paramInt)
  {
    this.mSliderFadeColor = paramInt;
  }
  
  @Deprecated
  public void smoothSlideClosed()
  {
    closePane();
  }
  
  @Deprecated
  public void smoothSlideOpen()
  {
    openPane();
  }
  
  boolean smoothSlideTo(float paramFloat, int paramInt)
  {
    boolean bool1 = false;
    if (this.mCanSlide)
    {
      boolean bool2 = isLayoutRtlSupport();
      LayoutParams localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
      int i;
      if (!bool2)
      {
        i = (int)(getPaddingLeft() + localLayoutParams.leftMargin + paramFloat * this.mSlideRange);
      }
      else
      {
        i = getPaddingRight() + localLayoutParams.rightMargin;
        int j = this.mSlideableView.getWidth();
        i = (int)(getWidth() - (i + paramFloat * this.mSlideRange + j));
      }
      if (this.mDragHelper.smoothSlideViewTo(this.mSlideableView, i, this.mSlideableView.getTop()))
      {
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        bool1 = true;
      }
    }
    return bool1;
  }
  
  void updateObscuredViewsVisibility(View paramView)
  {
    boolean bool = isLayoutRtlSupport();
    int j;
    if (!bool) {
      j = getPaddingLeft();
    } else {
      j = getWidth() - getPaddingRight();
    }
    int i2;
    if (!bool) {
      i2 = getWidth() - getPaddingRight();
    } else {
      i2 = getPaddingLeft();
    }
    int i = getPaddingTop();
    int k = getHeight() - getPaddingBottom();
    int i1;
    int n;
    int i5;
    int m;
    if ((paramView == null) || (!viewIsOpaque(paramView)))
    {
      i1 = 0;
      n = 0;
      i5 = 0;
      m = 0;
    }
    else
    {
      m = paramView.getLeft();
      i5 = paramView.getRight();
      n = paramView.getTop();
      i1 = paramView.getBottom();
    }
    int i4 = 0;
    int i3 = getChildCount();
    while (i4 < i3)
    {
      View localView = getChildAt(i4);
      if (localView == paramView) {
        break;
      }
      if (!bool) {
        i6 = j;
      } else {
        i6 = i2;
      }
      int i7 = Math.max(i6, localView.getLeft());
      int i6 = Math.max(i, localView.getTop());
      if (!bool) {
        i8 = i2;
      } else {
        i8 = j;
      }
      int i8 = Math.min(i8, localView.getRight());
      int i9 = Math.min(k, localView.getBottom());
      if ((i7 < m) || (i6 < n) || (i8 > i5) || (i9 > i1)) {
        i6 = 0;
      } else {
        i6 = 4;
      }
      localView.setVisibility(i6);
      i4++;
    }
  }
  
  private class DisableLayerRunnable
    implements Runnable
  {
    final View mChildView;
    
    DisableLayerRunnable(View paramView)
    {
      this.mChildView = paramView;
    }
    
    public void run()
    {
      if (this.mChildView.getParent() == SlidingPaneLayout.this)
      {
        ViewCompat.setLayerType(this.mChildView, 0, null);
        SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
      }
      SlidingPaneLayout.this.mPostedRunnables.remove(this);
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat1, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat2)
    {
      Rect localRect = this.mTmpRect;
      paramAccessibilityNodeInfoCompat2.getBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat2.getBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setVisibleToUser(paramAccessibilityNodeInfoCompat2.isVisibleToUser());
      paramAccessibilityNodeInfoCompat1.setPackageName(paramAccessibilityNodeInfoCompat2.getPackageName());
      paramAccessibilityNodeInfoCompat1.setClassName(paramAccessibilityNodeInfoCompat2.getClassName());
      paramAccessibilityNodeInfoCompat1.setContentDescription(paramAccessibilityNodeInfoCompat2.getContentDescription());
      paramAccessibilityNodeInfoCompat1.setEnabled(paramAccessibilityNodeInfoCompat2.isEnabled());
      paramAccessibilityNodeInfoCompat1.setClickable(paramAccessibilityNodeInfoCompat2.isClickable());
      paramAccessibilityNodeInfoCompat1.setFocusable(paramAccessibilityNodeInfoCompat2.isFocusable());
      paramAccessibilityNodeInfoCompat1.setFocused(paramAccessibilityNodeInfoCompat2.isFocused());
      paramAccessibilityNodeInfoCompat1.setAccessibilityFocused(paramAccessibilityNodeInfoCompat2.isAccessibilityFocused());
      paramAccessibilityNodeInfoCompat1.setSelected(paramAccessibilityNodeInfoCompat2.isSelected());
      paramAccessibilityNodeInfoCompat1.setLongClickable(paramAccessibilityNodeInfoCompat2.isLongClickable());
      paramAccessibilityNodeInfoCompat1.addAction(paramAccessibilityNodeInfoCompat2.getActions());
      paramAccessibilityNodeInfoCompat1.setMovementGranularities(paramAccessibilityNodeInfoCompat2.getMovementGranularities());
    }
    
    public boolean filter(View paramView)
    {
      return SlidingPaneLayout.this.isDimmed(paramView);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      Object localObject = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
      super.onInitializeAccessibilityNodeInfo(paramView, (AccessibilityNodeInfoCompat)localObject);
      copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, (AccessibilityNodeInfoCompat)localObject);
      ((AccessibilityNodeInfoCompat)localObject).recycle();
      paramAccessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
      paramAccessibilityNodeInfoCompat.setSource(paramView);
      localObject = ViewCompat.getParentForAccessibility(paramView);
      if ((localObject instanceof View)) {
        paramAccessibilityNodeInfoCompat.setParent((View)localObject);
      }
      int i = SlidingPaneLayout.this.getChildCount();
      for (int j = 0;; j++)
      {
        if (j >= i) {
          return;
        }
        View localView = SlidingPaneLayout.this.getChildAt(j);
        if ((!filter(localView)) && (localView.getVisibility() == 0))
        {
          ViewCompat.setImportantForAccessibility(localView, 1);
          paramAccessibilityNodeInfoCompat.addChild(localView);
        }
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool;
      if (filter(paramView)) {
        bool = false;
      } else {
        bool = super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return bool;
    }
  }
  
  static class SlidingPanelLayoutImplJBMR1
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.setLayerPaint(paramView, ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).dimPaint);
    }
  }
  
  static class SlidingPanelLayoutImplJB
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    private Method mGetDisplayList;
    private Field mRecreateDisplayList;
    
    SlidingPanelLayoutImplJB()
    {
      try
      {
        this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[])null);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        try
        {
          for (;;)
          {
            this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
            this.mRecreateDisplayList.setAccessible(true);
            return;
            localNoSuchMethodException = localNoSuchMethodException;
            Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", localNoSuchMethodException);
          }
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          for (;;)
          {
            Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", localNoSuchFieldException);
          }
        }
      }
    }
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      if ((this.mGetDisplayList != null) && (this.mRecreateDisplayList != null)) {}
      for (;;)
      {
        try
        {
          this.mRecreateDisplayList.setBoolean(paramView, true);
          this.mGetDisplayList.invoke(paramView, (Object[])null);
          super.invalidateChildRegion(paramSlidingPaneLayout, paramView);
          return;
        }
        catch (Exception localException)
        {
          Log.e("SlidingPaneLayout", "Error refreshing display list state", localException);
          continue;
        }
        paramView.invalidate();
      }
    }
  }
  
  static class SlidingPanelLayoutImplBase
    implements SlidingPaneLayout.SlidingPanelLayoutImpl
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.postInvalidateOnAnimation(paramSlidingPaneLayout, paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    }
  }
  
  static abstract interface SlidingPanelLayoutImpl
  {
    public abstract void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SlidingPaneLayout.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SlidingPaneLayout.SavedState(paramAnonymousParcel, null);
      }
      
      public SlidingPaneLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SlidingPaneLayout.SavedState[paramAnonymousInt];
      }
    };
    boolean isOpen;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      boolean bool;
      if (paramParcel.readInt() == 0) {
        bool = false;
      } else {
        bool = true;
      }
      this.isOpen = bool;
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      int i;
      if (!this.isOpen) {
        i = 0;
      } else {
        i = 1;
      }
      paramParcel.writeInt(i);
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    private static final int[] ATTRS;
    Paint dimPaint;
    boolean dimWhenOffset;
    boolean slideable;
    public float weight = 0.0F;
    
    static
    {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = 16843137;
      ATTRS = arrayOfInt;
    }
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
      this.weight = localTypedArray.getFloat(0, 0.0F);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.weight = paramLayoutParams.weight;
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
  
  private class DragHelperCallback
    extends ViewDragHelper.Callback
  {
    private DragHelperCallback() {}
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
      int i;
      int j;
      if (!SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        i = SlidingPaneLayout.this.getPaddingLeft() + localLayoutParams.leftMargin;
        j = i + SlidingPaneLayout.this.mSlideRange;
        i = Math.min(Math.max(paramInt1, i), j);
      }
      else
      {
        j = SlidingPaneLayout.this.getWidth() - (SlidingPaneLayout.this.getPaddingRight() + i.rightMargin + SlidingPaneLayout.this.mSlideableView.getWidth());
        i = j - SlidingPaneLayout.this.mSlideRange;
        i = Math.max(Math.min(paramInt1, j), i);
      }
      return i;
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return SlidingPaneLayout.this.mSlideRange;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, paramInt2);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      SlidingPaneLayout.this.setAllChildrenVisible();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0) {
        if (SlidingPaneLayout.this.mSlideOffset != 0.0F)
        {
          SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.access$502(SlidingPaneLayout.this, true);
        }
        else
        {
          SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.access$502(SlidingPaneLayout.this, false);
        }
      }
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SlidingPaneLayout.this.onPanelDragged(paramInt1);
      SlidingPaneLayout.this.invalidate();
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)paramView.getLayoutParams();
      int i;
      if (!SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        i = SlidingPaneLayout.this.getPaddingLeft() + localLayoutParams.leftMargin;
        if ((paramFloat1 > 0.0F) || ((paramFloat1 == 0.0F) && (SlidingPaneLayout.this.mSlideOffset > 0.5F))) {
          i += SlidingPaneLayout.this.mSlideRange;
        }
      }
      else
      {
        int j = SlidingPaneLayout.this.getPaddingRight() + i.rightMargin;
        if ((paramFloat1 < 0.0F) || ((paramFloat1 == 0.0F) && (SlidingPaneLayout.this.mSlideOffset > 0.5F))) {
          j += SlidingPaneLayout.this.mSlideRange;
        }
        i = SlidingPaneLayout.this.mSlideableView.getWidth();
        i = SlidingPaneLayout.this.getWidth() - j - i;
      }
      SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(i, paramView.getTop());
      SlidingPaneLayout.this.invalidate();
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      boolean bool;
      if (!SlidingPaneLayout.this.mIsUnableToDrag) {
        bool = ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).slideable;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public static class SimplePanelSlideListener
    implements SlidingPaneLayout.PanelSlideListener
  {
    public void onPanelClosed(View paramView) {}
    
    public void onPanelOpened(View paramView) {}
    
    public void onPanelSlide(View paramView, float paramFloat) {}
  }
  
  public static abstract interface PanelSlideListener
  {
    public abstract void onPanelClosed(View paramView);
    
    public abstract void onPanelOpened(View paramView);
    
    public abstract void onPanelSlide(View paramView, float paramFloat);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\SlidingPaneLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */