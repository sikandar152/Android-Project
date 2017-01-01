package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class DrawerLayout
  extends ViewGroup
  implements DrawerLayoutImpl
{
  private static final boolean ALLOW_EDGE_LOCK = false;
  private static final boolean CAN_HIDE_DESCENDANTS = false;
  private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
  private static final int DEFAULT_SCRIM_COLOR = -1728053248;
  static final DrawerLayoutCompatImpl IMPL;
  private static final int[] LAYOUT_ATTRS;
  public static final int LOCK_MODE_LOCKED_CLOSED = 1;
  public static final int LOCK_MODE_LOCKED_OPEN = 2;
  public static final int LOCK_MODE_UNLOCKED = 0;
  private static final int MIN_DRAWER_MARGIN = 64;
  private static final int MIN_FLING_VELOCITY = 400;
  private static final int PEEK_DELAY = 160;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "DrawerLayout";
  private static final float TOUCH_SLOP_SENSITIVITY = 1.0F;
  private final ChildAccessibilityDelegate mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
  private boolean mChildrenCanceledTouch;
  private boolean mDisallowInterceptRequested;
  private boolean mDrawStatusBarBackground;
  private int mDrawerState;
  private boolean mFirstLayout = true;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private Object mLastInsets;
  private final ViewDragCallback mLeftCallback;
  private final ViewDragHelper mLeftDragger;
  private DrawerListener mListener;
  private int mLockModeLeft;
  private int mLockModeRight;
  private int mMinDrawerMargin;
  private final ViewDragCallback mRightCallback;
  private final ViewDragHelper mRightDragger;
  private int mScrimColor = -1728053248;
  private float mScrimOpacity;
  private Paint mScrimPaint = new Paint();
  private Drawable mShadowLeft;
  private Drawable mShadowRight;
  private Drawable mStatusBarBackground;
  private CharSequence mTitleLeft;
  private CharSequence mTitleRight;
  
  static
  {
    boolean bool = true;
    int[] arrayOfInt = new int[bool];
    arrayOfInt[0] = 16842931;
    LAYOUT_ATTRS = arrayOfInt;
    if (Build.VERSION.SDK_INT < 19) {
      bool = false;
    }
    CAN_HIDE_DESCENDANTS = bool;
    if (Build.VERSION.SDK_INT < 21) {
      IMPL = new DrawerLayoutCompatImplBase();
    } else {
      IMPL = new DrawerLayoutCompatImplApi21();
    }
  }
  
  public DrawerLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setDescendantFocusability(262144);
    float f = getResources().getDisplayMetrics().density;
    this.mMinDrawerMargin = ((int)(0.5F + 64.0F * f));
    f = 400.0F * f;
    this.mLeftCallback = new ViewDragCallback(3);
    this.mRightCallback = new ViewDragCallback(5);
    this.mLeftDragger = ViewDragHelper.create(this, 1.0F, this.mLeftCallback);
    this.mLeftDragger.setEdgeTrackingEnabled(1);
    this.mLeftDragger.setMinVelocity(f);
    this.mLeftCallback.setDragger(this.mLeftDragger);
    this.mRightDragger = ViewDragHelper.create(this, 1.0F, this.mRightCallback);
    this.mRightDragger.setEdgeTrackingEnabled(2);
    this.mRightDragger.setMinVelocity(f);
    this.mRightCallback.setDragger(this.mRightDragger);
    setFocusableInTouchMode(true);
    ViewCompat.setImportantForAccessibility(this, 1);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
    if (ViewCompat.getFitsSystemWindows(this)) {
      IMPL.configureApplyInsets(this);
    }
  }
  
  private View findVisibleDrawer()
  {
    int i = getChildCount();
    View localView;
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        localView = null;
        break;
      }
      localView = getChildAt(j);
      if ((isDrawerView(localView)) && (isDrawerVisible(localView))) {
        break;
      }
    }
    return localView;
  }
  
  static String gravityToString(int paramInt)
  {
    String str;
    if ((paramInt & 0x3) != 3)
    {
      if ((paramInt & 0x5) != 5) {
        str = Integer.toHexString(paramInt);
      } else {
        str = "RIGHT";
      }
    }
    else {
      str = "LEFT";
    }
    return str;
  }
  
  private static boolean hasOpaqueBackground(View paramView)
  {
    boolean bool = false;
    Drawable localDrawable = paramView.getBackground();
    if ((localDrawable != null) && (localDrawable.getOpacity() == -1)) {
      bool = true;
    }
    return bool;
  }
  
  private boolean hasPeekingDrawer()
  {
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return 0;
      }
      if (((LayoutParams)getChildAt(j).getLayoutParams()).isPeeking) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  private boolean hasVisibleDrawer()
  {
    boolean bool;
    if (findVisibleDrawer() == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private static boolean includeChildForAccessibility(View paramView)
  {
    boolean bool;
    if ((ViewCompat.getImportantForAccessibility(paramView) == 4) || (ViewCompat.getImportantForAccessibility(paramView) == 2)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void updateChildrenImportantForAccessibility(View paramView, boolean paramBoolean)
  {
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      View localView = getChildAt(j);
      if (((paramBoolean) || (isDrawerView(localView))) && ((!paramBoolean) || (localView != paramView))) {
        ViewCompat.setImportantForAccessibility(localView, 4);
      } else {
        ViewCompat.setImportantForAccessibility(localView, 1);
      }
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    if ((findOpenDrawer() == null) && (!isDrawerView(paramView))) {
      ViewCompat.setImportantForAccessibility(paramView, 1);
    } else {
      ViewCompat.setImportantForAccessibility(paramView, 4);
    }
    if (!CAN_HIDE_DESCENDANTS) {
      ViewCompat.setAccessibilityDelegate(paramView, this.mChildAccessibilityDelegate);
    }
  }
  
  void cancelChildViewTouch()
  {
    MotionEvent localMotionEvent;
    int i;
    if (!this.mChildrenCanceledTouch)
    {
      long l = SystemClock.uptimeMillis();
      localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      i = getChildCount();
    }
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        localMotionEvent.recycle();
        this.mChildrenCanceledTouch = true;
        return;
      }
      getChildAt(j).dispatchTouchEvent(localMotionEvent);
    }
  }
  
  boolean checkDrawerViewAbsoluteGravity(View paramView, int paramInt)
  {
    boolean bool;
    if ((paramInt & getDrawerViewAbsoluteGravity(paramView)) != paramInt) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
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
  
  public void closeDrawer(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null)
    {
      closeDrawer(localView);
      return;
    }
    throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
  }
  
  public void closeDrawer(View paramView)
  {
    if (isDrawerView(paramView))
    {
      if (!this.mFirstLayout)
      {
        if (!checkDrawerViewAbsoluteGravity(paramView, 3)) {
          this.mRightDragger.smoothSlideViewTo(paramView, getWidth(), paramView.getTop());
        } else {
          this.mLeftDragger.smoothSlideViewTo(paramView, -paramView.getWidth(), paramView.getTop());
        }
      }
      else
      {
        LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
        localLayoutParams.onScreen = 0.0F;
        localLayoutParams.knownOpen = false;
      }
      invalidate();
      return;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
  }
  
  public void closeDrawers()
  {
    closeDrawers(false);
  }
  
  void closeDrawers(boolean paramBoolean)
  {
    boolean bool = false;
    int k = getChildCount();
    for (int i = 0;; i++)
    {
      if (i >= k)
      {
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (bool) {
          invalidate();
        }
        return;
      }
      View localView = getChildAt(i);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if ((isDrawerView(localView)) && ((!paramBoolean) || (localLayoutParams.isPeeking)))
      {
        int j = localView.getWidth();
        if (!checkDrawerViewAbsoluteGravity(localView, 3)) {
          bool |= this.mRightDragger.smoothSlideViewTo(localView, getWidth(), localView.getTop());
        } else {
          bool |= this.mLeftDragger.smoothSlideViewTo(localView, -j, localView.getTop());
        }
        localLayoutParams.isPeeking = false;
      }
    }
  }
  
  public void computeScroll()
  {
    int i = getChildCount();
    float f = 0.0F;
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        this.mScrimOpacity = f;
        if ((this.mLeftDragger.continueSettling(true) | this.mRightDragger.continueSettling(true))) {
          ViewCompat.postInvalidateOnAnimation(this);
        }
        return;
      }
      f = Math.max(f, ((LayoutParams)getChildAt(j).getLayoutParams()).onScreen);
    }
  }
  
  void dispatchOnDrawerClosed(View paramView)
  {
    Object localObject = (LayoutParams)paramView.getLayoutParams();
    if (((LayoutParams)localObject).knownOpen)
    {
      ((LayoutParams)localObject).knownOpen = false;
      if (this.mListener != null) {
        this.mListener.onDrawerClosed(paramView);
      }
      updateChildrenImportantForAccessibility(paramView, false);
      if (hasWindowFocus())
      {
        localObject = getRootView();
        if (localObject != null) {
          ((View)localObject).sendAccessibilityEvent(32);
        }
      }
    }
  }
  
  void dispatchOnDrawerOpened(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.knownOpen)
    {
      localLayoutParams.knownOpen = true;
      if (this.mListener != null) {
        this.mListener.onDrawerOpened(paramView);
      }
      updateChildrenImportantForAccessibility(paramView, true);
      paramView.requestFocus();
    }
  }
  
  void dispatchOnDrawerSlide(View paramView, float paramFloat)
  {
    if (this.mListener != null) {
      this.mListener.onDrawerSlide(paramView, paramFloat);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = getHeight();
    boolean bool = isContentView(paramView);
    int k = 0;
    int m = getWidth();
    int n = paramCanvas.save();
    int i4;
    if (bool) {
      i4 = getChildCount();
    }
    for (int i5 = 0;; i5++)
    {
      int j;
      if (i5 >= i4)
      {
        paramCanvas.clipRect(k, 0, m, getHeight());
        j = super.drawChild(paramCanvas, paramView, paramLong);
        paramCanvas.restoreToCount(n);
        if ((this.mScrimOpacity <= 0.0F) || (!bool))
        {
          if ((this.mShadowLeft == null) || (!checkDrawerViewAbsoluteGravity(paramView, 3)))
          {
            if ((this.mShadowRight != null) && (checkDrawerViewAbsoluteGravity(paramView, 5)))
            {
              k = this.mShadowRight.getIntrinsicWidth();
              m = paramView.getLeft();
              n = getWidth() - m;
              int i3 = this.mRightDragger.getEdgeSize();
              float f1 = Math.max(0.0F, Math.min(n / i3, 1.0F));
              this.mShadowRight.setBounds(m - k, paramView.getTop(), m, paramView.getBottom());
              this.mShadowRight.setAlpha((int)(255.0F * f1));
              this.mShadowRight.draw(paramCanvas);
            }
          }
          else
          {
            k = this.mShadowLeft.getIntrinsicWidth();
            m = paramView.getRight();
            int i1 = this.mLeftDragger.getEdgeSize();
            float f2 = Math.max(0.0F, Math.min(m / i1, 1.0F));
            this.mShadowLeft.setBounds(m, paramView.getTop(), m + k, paramView.getBottom());
            this.mShadowLeft.setAlpha((int)(255.0F * f2));
            this.mShadowLeft.draw(paramCanvas);
          }
        }
        else
        {
          int i2 = (int)(((0xFF000000 & this.mScrimColor) >>> 24) * this.mScrimOpacity) << 24 | 0xFFFFFF & this.mScrimColor;
          this.mScrimPaint.setColor(i2);
          paramCanvas.drawRect(k, 0.0F, m, getHeight(), this.mScrimPaint);
        }
        return j;
      }
      View localView = getChildAt(i5);
      if ((localView != paramView) && (localView.getVisibility() == 0) && (hasOpaqueBackground(localView)) && (isDrawerView(localView)) && (localView.getHeight() >= j))
      {
        int i6;
        if (!checkDrawerViewAbsoluteGravity(localView, 3))
        {
          i6 = localView.getLeft();
          if (i6 < m) {
            m = i6;
          }
        }
        else
        {
          i6 = i6.getRight();
          if (i6 > k) {
            k = i6;
          }
        }
      }
    }
  }
  
  View findDrawerWithGravity(int paramInt)
  {
    int k = 0x7 & GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    int j = getChildCount();
    View localView;
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        localView = null;
        break;
      }
      localView = getChildAt(i);
      if ((0x7 & getDrawerViewAbsoluteGravity(localView)) == k) {
        break;
      }
    }
    return localView;
  }
  
  View findOpenDrawer()
  {
    int j = getChildCount();
    View localView;
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        localView = null;
        break;
      }
      localView = getChildAt(i);
      if (((LayoutParams)localView.getLayoutParams()).knownOpen) {
        break;
      }
    }
    return localView;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    LayoutParams localLayoutParams;
    if (!(paramLayoutParams instanceof LayoutParams))
    {
      if (!(paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
        localLayoutParams = new LayoutParams(paramLayoutParams);
      } else {
        localLayoutParams = new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
      }
    }
    else {
      localLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return localLayoutParams;
  }
  
  public int getDrawerLockMode(int paramInt)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (i != 3)
    {
      if (i != 5) {
        i = 0;
      } else {
        i = this.mLockModeRight;
      }
    }
    else {
      i = this.mLockModeLeft;
    }
    return i;
  }
  
  public int getDrawerLockMode(View paramView)
  {
    int i = getDrawerViewAbsoluteGravity(paramView);
    if (i != 3)
    {
      if (i != 5) {
        i = 0;
      } else {
        i = this.mLockModeRight;
      }
    }
    else {
      i = this.mLockModeLeft;
    }
    return i;
  }
  
  @Nullable
  public CharSequence getDrawerTitle(int paramInt)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    CharSequence localCharSequence;
    if (i != 3)
    {
      if (i != 5) {
        localCharSequence = null;
      } else {
        localCharSequence = this.mTitleRight;
      }
    }
    else {
      localCharSequence = this.mTitleLeft;
    }
    return localCharSequence;
  }
  
  int getDrawerViewAbsoluteGravity(View paramView)
  {
    return GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this));
  }
  
  float getDrawerViewOffset(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).onScreen;
  }
  
  boolean isContentView(View paramView)
  {
    boolean bool;
    if (((LayoutParams)paramView.getLayoutParams()).gravity != 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDrawerOpen(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    boolean bool;
    if (localView == null) {
      bool = false;
    } else {
      bool = isDrawerOpen(bool);
    }
    return bool;
  }
  
  public boolean isDrawerOpen(View paramView)
  {
    if (isDrawerView(paramView)) {
      return ((LayoutParams)paramView.getLayoutParams()).knownOpen;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a drawer");
  }
  
  boolean isDrawerView(View paramView)
  {
    boolean bool;
    if ((0x7 & GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(paramView))) == 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDrawerVisible(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    boolean bool;
    if (localView == null) {
      bool = false;
    } else {
      bool = isDrawerVisible(bool);
    }
    return bool;
  }
  
  public boolean isDrawerVisible(View paramView)
  {
    if (isDrawerView(paramView))
    {
      boolean bool;
      if (((LayoutParams)paramView.getLayoutParams()).onScreen <= 0.0F) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a drawer");
  }
  
  void moveDrawerToOffset(View paramView, float paramFloat)
  {
    float f = getDrawerViewOffset(paramView);
    int i = paramView.getWidth();
    int j = (int)(f * i);
    i = (int)(paramFloat * i) - j;
    if (!checkDrawerViewAbsoluteGravity(paramView, 3)) {
      i = -i;
    }
    paramView.offsetLeftAndRight(i);
    setDrawerViewOffset(paramView, paramFloat);
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
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.mDrawStatusBarBackground) && (this.mStatusBarBackground != null))
    {
      int i = IMPL.getTopInset(this.mLastInsets);
      if (i > 0)
      {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), i);
        this.mStatusBarBackground.draw(paramCanvas);
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    int j = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool1 = this.mLeftDragger.shouldInterceptTouchEvent(paramMotionEvent) | this.mRightDragger.shouldInterceptTouchEvent(paramMotionEvent);
    int i = 0;
    switch (j)
    {
    case 0: 
      float f2 = paramMotionEvent.getX();
      float f1 = paramMotionEvent.getY();
      this.mInitialMotionX = f2;
      this.mInitialMotionY = f1;
      if (this.mScrimOpacity > 0.0F)
      {
        View localView = this.mLeftDragger.findTopChildUnder((int)f2, (int)f1);
        if ((localView != null) && (isContentView(localView))) {
          i = 1;
        }
      }
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      break;
    case 1: 
    case 3: 
      closeDrawers(true);
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      break;
    case 2: 
      if (this.mLeftDragger.checkTouchSlop(3))
      {
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
      }
      break;
    }
    if ((bool1) || (i != 0) || (hasPeekingDrawer()) || (this.mChildrenCanceledTouch)) {
      bool2 = true;
    }
    return bool2;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((paramInt != 4) || (!hasVisibleDrawer()))
    {
      bool = super.onKeyDown(paramInt, paramKeyEvent);
    }
    else
    {
      KeyEventCompat.startTracking(paramKeyEvent);
      bool = true;
    }
    return bool;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool2;
    if (paramInt != 4)
    {
      boolean bool1 = super.onKeyUp(paramInt, paramKeyEvent);
    }
    else
    {
      View localView = findVisibleDrawer();
      if ((localView != null) && (getDrawerLockMode(localView) == 0)) {
        closeDrawers();
      }
      if (localView == null) {
        bool2 = false;
      } else {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mInLayout = true;
    int k = paramInt3 - paramInt1;
    int j = getChildCount();
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        this.mInLayout = false;
        this.mFirstLayout = false;
        return;
      }
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (!isContentView(localView))
        {
          int n = localView.getMeasuredWidth();
          int i4 = localView.getMeasuredHeight();
          int i1;
          float f;
          if (!checkDrawerViewAbsoluteGravity(localView, 3))
          {
            i1 = k - (int)(n * localLayoutParams.onScreen);
            f = (k - i1) / n;
          }
          else
          {
            i1 = -n + (int)(n * localLayoutParams.onScreen);
            f = (n + i1) / n;
          }
          int m;
          if (f == localLayoutParams.onScreen) {
            m = 0;
          } else {
            m = 1;
          }
          int i2;
          switch (0x70 & localLayoutParams.gravity)
          {
          default: 
            localView.layout(i1, localLayoutParams.topMargin, i1 + n, i4 + localLayoutParams.topMargin);
            break;
          case 16: 
            i2 = paramInt4 - paramInt2;
            int i3 = (i2 - i4) / 2;
            if (i3 >= localLayoutParams.topMargin)
            {
              if (i3 + i4 > i2 - localLayoutParams.bottomMargin) {
                i3 = i2 - localLayoutParams.bottomMargin - i4;
              }
            }
            else {
              i3 = localLayoutParams.topMargin;
            }
            localView.layout(i1, i3, i1 + n, i3 + i4);
            break;
          case 80: 
            i2 = paramInt4 - paramInt2;
            localView.layout(i1, i2 - localLayoutParams.bottomMargin - localView.getMeasuredHeight(), i1 + n, i2 - localLayoutParams.bottomMargin);
          }
          if (m != 0) {
            setDrawerViewOffset(localView, f);
          }
          if (localLayoutParams.onScreen <= 0.0F) {
            m = 4;
          } else {
            m = 0;
          }
          if (localView.getVisibility() != m) {
            localView.setVisibility(m);
          }
        }
        else
        {
          localView.layout(localLayoutParams.leftMargin, localLayoutParams.topMargin, localLayoutParams.leftMargin + localView.getMeasuredWidth(), localLayoutParams.topMargin + localView.getMeasuredHeight());
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int k = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt2);
    if ((k != 1073741824) || (m != 1073741824))
    {
      if (!isInEditMode()) {
        throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
      }
      if ((k != Integer.MIN_VALUE) && (k == 0)) {
        j = 300;
      }
      if ((m != Integer.MIN_VALUE) && (m == 0)) {
        i = 300;
      }
    }
    setMeasuredDimension(j, i);
    int i1;
    if ((this.mLastInsets == null) || (!ViewCompat.getFitsSystemWindows(this))) {
      i1 = 0;
    } else {
      i1 = 1;
    }
    k = ViewCompat.getLayoutDirection(this);
    m = getChildCount();
    for (int n = 0;; n++)
    {
      if (n >= m) {
        return;
      }
      View localView = getChildAt(n);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i2;
        if (i1 != 0)
        {
          i2 = GravityCompat.getAbsoluteGravity(localLayoutParams.gravity, k);
          if (!ViewCompat.getFitsSystemWindows(localView)) {
            IMPL.applyMarginInsets(localLayoutParams, this.mLastInsets, i2);
          } else {
            IMPL.dispatchChildInsets(localView, this.mLastInsets, i2);
          }
        }
        if (!isContentView(localView))
        {
          if (!isDrawerView(localView)) {
            throw new IllegalStateException("Child " + localView + " at index " + n + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
          }
          i2 = 0x7 & getDrawerViewAbsoluteGravity(localView);
          if ((0x0 & i2) == 0) {
            localView.measure(getChildMeasureSpec(paramInt1, this.mMinDrawerMargin + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width), getChildMeasureSpec(paramInt2, localLayoutParams.topMargin + localLayoutParams.bottomMargin, localLayoutParams.height));
          } else {
            throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(i2) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
          }
        }
        else
        {
          localView.measure(View.MeasureSpec.makeMeasureSpec(j - localLayoutParams.leftMargin - localLayoutParams.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(i - localLayoutParams.topMargin - localLayoutParams.bottomMargin, 1073741824));
        }
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (localSavedState.openDrawerGravity != 0)
    {
      View localView = findDrawerWithGravity(localSavedState.openDrawerGravity);
      if (localView != null) {
        openDrawer(localView);
      }
    }
    setDrawerLockMode(localSavedState.lockModeLeft, 3);
    setDrawerLockMode(localSavedState.lockModeRight, 5);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    View localView = findOpenDrawer();
    if (localView != null) {
      localSavedState.openDrawerGravity = ((LayoutParams)localView.getLayoutParams()).gravity;
    }
    localSavedState.lockModeLeft = this.mLockModeLeft;
    localSavedState.lockModeRight = this.mLockModeRight;
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mLeftDragger.processTouchEvent(paramMotionEvent);
    this.mRightDragger.processTouchEvent(paramMotionEvent);
    switch (0xFF & paramMotionEvent.getAction())
    {
    case 0: 
      float f2 = paramMotionEvent.getX();
      float f1 = paramMotionEvent.getY();
      this.mInitialMotionX = f2;
      this.mInitialMotionY = f1;
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      break;
    case 1: 
      float f5 = paramMotionEvent.getX();
      float f4 = paramMotionEvent.getY();
      boolean bool = true;
      View localView1 = this.mLeftDragger.findTopChildUnder((int)f5, (int)f4);
      if ((localView1 != null) && (isContentView(localView1)))
      {
        float f3 = f5 - this.mInitialMotionX;
        f5 = f4 - this.mInitialMotionY;
        int i = this.mLeftDragger.getTouchSlop();
        if (f3 * f3 + f5 * f5 < i * i)
        {
          View localView2 = findOpenDrawer();
          if (localView2 != null) {
            if (getDrawerLockMode(localView2) != 2) {
              bool = false;
            } else {
              bool = true;
            }
          }
        }
      }
      closeDrawers(bool);
      this.mDisallowInterceptRequested = false;
      break;
    case 3: 
      closeDrawers(true);
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
    }
    return true;
  }
  
  public void openDrawer(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null)
    {
      openDrawer(localView);
      return;
    }
    throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
  }
  
  public void openDrawer(View paramView)
  {
    if (isDrawerView(paramView))
    {
      if (!this.mFirstLayout)
      {
        if (!checkDrawerViewAbsoluteGravity(paramView, 3)) {
          this.mRightDragger.smoothSlideViewTo(paramView, getWidth() - paramView.getWidth(), paramView.getTop());
        } else {
          this.mLeftDragger.smoothSlideViewTo(paramView, 0, paramView.getTop());
        }
      }
      else
      {
        LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
        localLayoutParams.onScreen = 1.0F;
        localLayoutParams.knownOpen = true;
        updateChildrenImportantForAccessibility(paramView, true);
      }
      invalidate();
      return;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    this.mDisallowInterceptRequested = paramBoolean;
    if (paramBoolean) {
      closeDrawers(true);
    }
  }
  
  public void requestLayout()
  {
    if (!this.mInLayout) {
      super.requestLayout();
    }
  }
  
  public void setChildInsets(Object paramObject, boolean paramBoolean)
  {
    this.mLastInsets = paramObject;
    this.mDrawStatusBarBackground = paramBoolean;
    boolean bool;
    if ((paramBoolean) || (getBackground() != null)) {
      bool = false;
    } else {
      bool = true;
    }
    setWillNotDraw(bool);
    requestLayout();
  }
  
  public void setDrawerListener(DrawerListener paramDrawerListener)
  {
    this.mListener = paramDrawerListener;
  }
  
  public void setDrawerLockMode(int paramInt)
  {
    setDrawerLockMode(paramInt, 3);
    setDrawerLockMode(paramInt, 5);
  }
  
  public void setDrawerLockMode(int paramInt1, int paramInt2)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this));
    if (i != 3)
    {
      if (i == 5) {
        this.mLockModeRight = paramInt1;
      }
    }
    else {
      this.mLockModeLeft = paramInt1;
    }
    Object localObject;
    if (paramInt1 != 0)
    {
      if (i != 3) {
        localObject = this.mRightDragger;
      } else {
        localObject = this.mLeftDragger;
      }
      ((ViewDragHelper)localObject).cancel();
    }
    switch (paramInt1)
    {
    case 1: 
      localObject = findDrawerWithGravity(i);
      if (localObject != null) {
        closeDrawer((View)localObject);
      }
      break;
    case 2: 
      localObject = findDrawerWithGravity(i);
      if (localObject != null) {
        openDrawer((View)localObject);
      }
      break;
    }
  }
  
  public void setDrawerLockMode(int paramInt, View paramView)
  {
    if (isDrawerView(paramView))
    {
      setDrawerLockMode(paramInt, ((LayoutParams)paramView.getLayoutParams()).gravity);
      return;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a " + "drawer with appropriate layout_gravity");
  }
  
  public void setDrawerShadow(int paramInt1, int paramInt2)
  {
    setDrawerShadow(getResources().getDrawable(paramInt1), paramInt2);
  }
  
  public void setDrawerShadow(Drawable paramDrawable, int paramInt)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if ((i & 0x3) == 3)
    {
      this.mShadowLeft = paramDrawable;
      invalidate();
    }
    if ((i & 0x5) == 5)
    {
      this.mShadowRight = paramDrawable;
      invalidate();
    }
  }
  
  public void setDrawerTitle(int paramInt, CharSequence paramCharSequence)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (i != 3)
    {
      if (i == 5) {
        this.mTitleRight = paramCharSequence;
      }
    }
    else {
      this.mTitleLeft = paramCharSequence;
    }
  }
  
  void setDrawerViewOffset(View paramView, float paramFloat)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramFloat != localLayoutParams.onScreen)
    {
      localLayoutParams.onScreen = paramFloat;
      dispatchOnDrawerSlide(paramView, paramFloat);
    }
  }
  
  public void setScrimColor(int paramInt)
  {
    this.mScrimColor = paramInt;
    invalidate();
  }
  
  public void setStatusBarBackground(int paramInt)
  {
    Drawable localDrawable;
    if (paramInt == 0) {
      localDrawable = null;
    } else {
      localDrawable = ContextCompat.getDrawable(getContext(), paramInt);
    }
    this.mStatusBarBackground = localDrawable;
  }
  
  public void setStatusBarBackground(Drawable paramDrawable)
  {
    this.mStatusBarBackground = paramDrawable;
  }
  
  public void setStatusBarBackgroundColor(int paramInt)
  {
    this.mStatusBarBackground = new ColorDrawable(paramInt);
  }
  
  void updateDrawerState(int paramInt1, int paramInt2, View paramView)
  {
    int i = this.mLeftDragger.getViewDragState();
    int j = this.mRightDragger.getViewDragState();
    if ((i != 1) && (j != 1))
    {
      if ((i != 2) && (j != 2)) {
        i = 0;
      } else {
        i = 2;
      }
    }
    else {
      i = 1;
    }
    if ((paramView != null) && (paramInt2 == 0))
    {
      LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
      if (localLayoutParams.onScreen != 0.0F)
      {
        if (localLayoutParams.onScreen == 1.0F) {
          dispatchOnDrawerOpened(paramView);
        }
      }
      else {
        dispatchOnDrawerClosed(paramView);
      }
    }
    if (i != this.mDrawerState)
    {
      this.mDrawerState = i;
      if (this.mListener != null) {
        this.mListener.onDrawerStateChanged(i);
      }
    }
  }
  
  final class ChildAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    ChildAccessibilityDelegate() {}
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      if (!DrawerLayout.includeChildForAccessibility(paramView)) {
        paramAccessibilityNodeInfoCompat.setParent(null);
      }
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void addChildrenForAccessibility(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat, ViewGroup paramViewGroup)
    {
      int i = paramViewGroup.getChildCount();
      for (int j = 0;; j++)
      {
        if (j >= i) {
          return;
        }
        View localView = paramViewGroup.getChildAt(j);
        if (DrawerLayout.includeChildForAccessibility(localView)) {
          paramAccessibilityNodeInfoCompat.addChild(localView);
        }
      }
    }
    
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
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool2;
      if (paramAccessibilityEvent.getEventType() != 32)
      {
        boolean bool1 = super.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
      }
      else
      {
        List localList = paramAccessibilityEvent.getText();
        View localView = DrawerLayout.this.findVisibleDrawer();
        if (localView != null)
        {
          int i = DrawerLayout.this.getDrawerViewAbsoluteGravity(localView);
          CharSequence localCharSequence = DrawerLayout.this.getDrawerTitle(i);
          if (localCharSequence != null) {
            localList.add(localCharSequence);
          }
        }
        bool2 = true;
      }
      return bool2;
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(DrawerLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if (!DrawerLayout.CAN_HIDE_DESCENDANTS)
      {
        AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
        super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
        paramAccessibilityNodeInfoCompat.setSource(paramView);
        ViewParent localViewParent = ViewCompat.getParentForAccessibility(paramView);
        if ((localViewParent instanceof View)) {
          paramAccessibilityNodeInfoCompat.setParent((View)localViewParent);
        }
        copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
        localAccessibilityNodeInfoCompat.recycle();
        addChildrenForAccessibility(paramAccessibilityNodeInfoCompat, (ViewGroup)paramView);
      }
      else
      {
        super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      }
      paramAccessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool;
      if ((!DrawerLayout.CAN_HIDE_DESCENDANTS) && (!DrawerLayout.includeChildForAccessibility(paramView))) {
        bool = false;
      } else {
        bool = super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return bool;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int gravity = 0;
    boolean isPeeking;
    boolean knownOpen;
    float onScreen;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, DrawerLayout.LAYOUT_ATTRS);
      this.gravity = localTypedArray.getInt(0, 0);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.gravity = paramLayoutParams.gravity;
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
  
  private class ViewDragCallback
    extends ViewDragHelper.Callback
  {
    private final int mAbsGravity;
    private ViewDragHelper mDragger;
    private final Runnable mPeekRunnable = new Runnable()
    {
      public void run()
      {
        DrawerLayout.ViewDragCallback.this.peekDrawer();
      }
    };
    
    public ViewDragCallback(int paramInt)
    {
      this.mAbsGravity = paramInt;
    }
    
    private void closeOtherDrawer()
    {
      int i = 3;
      if (this.mAbsGravity == i) {
        i = 5;
      }
      View localView = DrawerLayout.this.findDrawerWithGravity(i);
      if (localView != null) {
        DrawerLayout.this.closeDrawer(localView);
      }
    }
    
    private void peekDrawer()
    {
      int j = 0;
      int k = this.mDragger.getEdgeSize();
      int i;
      if (this.mAbsGravity != 3) {
        i = 0;
      } else {
        i = 1;
      }
      View localView;
      if (i == 0)
      {
        localView = DrawerLayout.this.findDrawerWithGravity(5);
        j = DrawerLayout.this.getWidth() - k;
      }
      else
      {
        localView = DrawerLayout.this.findDrawerWithGravity(3);
        if (localView != null) {
          j = -localView.getWidth();
        }
        j += k;
      }
      if ((localView != null) && (((i != 0) && (localView.getLeft() < j)) || ((i == 0) && (localView.getLeft() > j) && (DrawerLayout.this.getDrawerLockMode(localView) == 0))))
      {
        DrawerLayout.LayoutParams localLayoutParams = (DrawerLayout.LayoutParams)localView.getLayoutParams();
        this.mDragger.smoothSlideViewTo(localView, j, localView.getTop());
        localLayoutParams.isPeeking = true;
        DrawerLayout.this.invalidate();
        closeOtherDrawer();
        DrawerLayout.this.cancelChildViewTouch();
      }
    }
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      int i;
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        i = DrawerLayout.this.getWidth();
        i = Math.max(i - paramView.getWidth(), Math.min(paramInt1, i));
      }
      else
      {
        i = Math.max(-paramView.getWidth(), Math.min(paramInt1, 0));
      }
      return i;
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      int i;
      if (!DrawerLayout.this.isDrawerView(paramView)) {
        i = 0;
      } else {
        i = paramView.getWidth();
      }
      return i;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      View localView;
      if ((paramInt1 & 0x1) != 1) {
        localView = DrawerLayout.this.findDrawerWithGravity(5);
      } else {
        localView = DrawerLayout.this.findDrawerWithGravity(3);
      }
      if ((localView != null) && (DrawerLayout.this.getDrawerLockMode(localView) == 0)) {
        this.mDragger.captureChildView(localView, paramInt2);
      }
    }
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2)
    {
      DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      ((DrawerLayout.LayoutParams)paramView.getLayoutParams()).isPeeking = false;
      closeOtherDrawer();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      DrawerLayout.this.updateDrawerState(this.mAbsGravity, paramInt, this.mDragger.getCapturedView());
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = paramView.getWidth();
      float f;
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3)) {
        f = (DrawerLayout.this.getWidth() - paramInt1) / i;
      } else {
        f = (f + paramInt1) / f;
      }
      DrawerLayout.this.setDrawerViewOffset(paramView, f);
      int j;
      if (f != 0.0F) {
        j = 0;
      } else {
        j = 4;
      }
      paramView.setVisibility(j);
      DrawerLayout.this.invalidate();
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      float f1 = DrawerLayout.this.getDrawerViewOffset(paramView);
      int j = paramView.getWidth();
      int i;
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        float f2 = DrawerLayout.this.getWidth();
        if ((paramFloat1 >= 0.0F) && ((paramFloat1 != 0.0F) || (f1 <= 0.5F))) {
          f1 = f2;
        } else {
          i = f2 - j;
        }
      }
      else if ((paramFloat1 <= 0.0F) && ((paramFloat1 != 0.0F) || (i <= 0.5F)))
      {
        i = -j;
      }
      else
      {
        i = 0;
      }
      this.mDragger.settleCapturedViewAt(i, paramView.getTop());
      DrawerLayout.this.invalidate();
    }
    
    public void removeCallbacks()
    {
      DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
    }
    
    public void setDragger(ViewDragHelper paramViewDragHelper)
    {
      this.mDragger = paramViewDragHelper;
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      boolean bool;
      if ((!DrawerLayout.this.isDrawerView(paramView)) || (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, this.mAbsGravity)) || (DrawerLayout.this.getDrawerLockMode(paramView) != 0)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
  }
  
  protected static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public DrawerLayout.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DrawerLayout.SavedState(paramAnonymousParcel);
      }
      
      public DrawerLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new DrawerLayout.SavedState[paramAnonymousInt];
      }
    };
    int lockModeLeft = 0;
    int lockModeRight = 0;
    int openDrawerGravity = 0;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.openDrawerGravity = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.openDrawerGravity);
    }
  }
  
  static class DrawerLayoutCompatImplApi21
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.applyMarginInsets(paramMarginLayoutParams, paramObject, paramInt);
    }
    
    public void configureApplyInsets(View paramView)
    {
      DrawerLayoutCompatApi21.configureApplyInsets(paramView);
    }
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.dispatchChildInsets(paramView, paramObject, paramInt);
    }
    
    public int getTopInset(Object paramObject)
    {
      return DrawerLayoutCompatApi21.getTopInset(paramObject);
    }
  }
  
  static class DrawerLayoutCompatImplBase
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt) {}
    
    public void configureApplyInsets(View paramView) {}
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt) {}
    
    public int getTopInset(Object paramObject)
    {
      return 0;
    }
  }
  
  static abstract interface DrawerLayoutCompatImpl
  {
    public abstract void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt);
    
    public abstract void configureApplyInsets(View paramView);
    
    public abstract void dispatchChildInsets(View paramView, Object paramObject, int paramInt);
    
    public abstract int getTopInset(Object paramObject);
  }
  
  public static abstract class SimpleDrawerListener
    implements DrawerLayout.DrawerListener
  {
    public void onDrawerClosed(View paramView) {}
    
    public void onDrawerOpened(View paramView) {}
    
    public void onDrawerSlide(View paramView, float paramFloat) {}
    
    public void onDrawerStateChanged(int paramInt) {}
  }
  
  public static abstract interface DrawerListener
  {
    public abstract void onDrawerClosed(View paramView);
    
    public abstract void onDrawerOpened(View paramView);
    
    public abstract void onDrawerSlide(View paramView, float paramFloat);
    
    public abstract void onDrawerStateChanged(int paramInt);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({3L, 5L, 8388611L, 8388613L})
  private static @interface EdgeGravity {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface LockMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface State {}
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\DrawerLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */