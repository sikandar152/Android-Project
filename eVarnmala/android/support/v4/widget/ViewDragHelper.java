package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;

public class ViewDragHelper
{
  private static final int BASE_SETTLE_DURATION = 256;
  public static final int DIRECTION_ALL = 3;
  public static final int DIRECTION_HORIZONTAL = 1;
  public static final int DIRECTION_VERTICAL = 2;
  public static final int EDGE_ALL = 15;
  public static final int EDGE_BOTTOM = 8;
  public static final int EDGE_LEFT = 1;
  public static final int EDGE_RIGHT = 2;
  private static final int EDGE_SIZE = 20;
  public static final int EDGE_TOP = 4;
  public static final int INVALID_POINTER = -1;
  private static final int MAX_SETTLE_DURATION = 600;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "ViewDragHelper";
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      float f = paramAnonymousFloat - 1.0F;
      return 1.0F + f * (f * (f * (f * f)));
    }
  };
  private int mActivePointerId = -1;
  private final Callback mCallback;
  private View mCapturedView;
  private int mDragState;
  private int[] mEdgeDragsInProgress;
  private int[] mEdgeDragsLocked;
  private int mEdgeSize;
  private int[] mInitialEdgesTouched;
  private float[] mInitialMotionX;
  private float[] mInitialMotionY;
  private float[] mLastMotionX;
  private float[] mLastMotionY;
  private float mMaxVelocity;
  private float mMinVelocity;
  private final ViewGroup mParentView;
  private int mPointersDown;
  private boolean mReleaseInProgress;
  private ScrollerCompat mScroller;
  private final Runnable mSetIdleRunnable = new Runnable()
  {
    public void run()
    {
      ViewDragHelper.this.setDragState(0);
    }
  };
  private int mTouchSlop;
  private int mTrackingEdges;
  private VelocityTracker mVelocityTracker;
  
  private ViewDragHelper(Context paramContext, ViewGroup paramViewGroup, Callback paramCallback)
  {
    if (paramViewGroup != null)
    {
      if (paramCallback != null)
      {
        this.mParentView = paramViewGroup;
        this.mCallback = paramCallback;
        ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
        this.mEdgeSize = ((int)(0.5F + 20.0F * paramContext.getResources().getDisplayMetrics().density));
        this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
        this.mMaxVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
        this.mMinVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
        this.mScroller = ScrollerCompat.create(paramContext, sInterpolator);
        return;
      }
      throw new IllegalArgumentException("Callback may not be null");
    }
    throw new IllegalArgumentException("Parent view may not be null");
  }
  
  private boolean checkNewEdgeDrag(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    boolean bool = false;
    float f1 = Math.abs(paramFloat1);
    float f2 = Math.abs(paramFloat2);
    if (((paramInt2 & this.mInitialEdgesTouched[paramInt1]) == paramInt2) && ((paramInt2 & this.mTrackingEdges) != 0) && ((paramInt2 & this.mEdgeDragsLocked[paramInt1]) != paramInt2) && ((paramInt2 & this.mEdgeDragsInProgress[paramInt1]) != paramInt2) && ((f1 > this.mTouchSlop) || (f2 > this.mTouchSlop))) {
      if ((f1 >= 0.5F * f2) || (!this.mCallback.onEdgeLock(paramInt2)))
      {
        if (((paramInt2 & this.mEdgeDragsInProgress[paramInt1]) == 0) && (f1 > this.mTouchSlop)) {
          bool = true;
        }
      }
      else
      {
        int[] arrayOfInt = this.mEdgeDragsLocked;
        arrayOfInt[paramInt1] = (paramInt2 | arrayOfInt[paramInt1]);
      }
    }
    return bool;
  }
  
  private boolean checkTouchSlop(View paramView, float paramFloat1, float paramFloat2)
  {
    boolean bool = true;
    if (paramView != null)
    {
      int j;
      if (this.mCallback.getViewHorizontalDragRange(paramView) <= 0) {
        j = 0;
      } else {
        j = bool;
      }
      int i;
      if (this.mCallback.getViewVerticalDragRange(paramView) <= 0) {
        i = 0;
      } else {
        i = bool;
      }
      if ((j == 0) || (i == 0))
      {
        if (j == 0)
        {
          if (i == 0) {
            bool = false;
          } else if (Math.abs(paramFloat2) <= this.mTouchSlop) {
            bool = false;
          }
        }
        else if (Math.abs(paramFloat1) <= this.mTouchSlop) {
          bool = false;
        }
      }
      else if (paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 <= this.mTouchSlop * this.mTouchSlop) {
        bool = false;
      }
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  private float clampMag(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f = Math.abs(paramFloat1);
    if (f >= paramFloat2)
    {
      if (f <= paramFloat3) {
        paramFloat3 = paramFloat1;
      } else if (paramFloat1 <= 0.0F) {
        paramFloat3 = -paramFloat3;
      }
    }
    else {
      paramFloat3 = 0.0F;
    }
    return paramFloat3;
  }
  
  private int clampMag(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Math.abs(paramInt1);
    if (i >= paramInt2)
    {
      if (i <= paramInt3) {
        paramInt3 = paramInt1;
      } else if (paramInt1 <= 0) {
        paramInt3 = -paramInt3;
      }
    }
    else {
      paramInt3 = 0;
    }
    return paramInt3;
  }
  
  private void clearMotionHistory()
  {
    if (this.mInitialMotionX != null)
    {
      Arrays.fill(this.mInitialMotionX, 0.0F);
      Arrays.fill(this.mInitialMotionY, 0.0F);
      Arrays.fill(this.mLastMotionX, 0.0F);
      Arrays.fill(this.mLastMotionY, 0.0F);
      Arrays.fill(this.mInitialEdgesTouched, 0);
      Arrays.fill(this.mEdgeDragsInProgress, 0);
      Arrays.fill(this.mEdgeDragsLocked, 0);
      this.mPointersDown = 0;
    }
  }
  
  private void clearMotionHistory(int paramInt)
  {
    if (this.mInitialMotionX != null)
    {
      this.mInitialMotionX[paramInt] = 0.0F;
      this.mInitialMotionY[paramInt] = 0.0F;
      this.mLastMotionX[paramInt] = 0.0F;
      this.mLastMotionY[paramInt] = 0.0F;
      this.mInitialEdgesTouched[paramInt] = 0;
      this.mEdgeDragsInProgress[paramInt] = 0;
      this.mEdgeDragsLocked[paramInt] = 0;
      this.mPointersDown &= (0xFFFFFFFF ^ 1 << paramInt);
    }
  }
  
  private int computeAxisDuration(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if (paramInt1 != 0)
    {
      int j = this.mParentView.getWidth();
      i = j / 2;
      float f = Math.min(1.0F, Math.abs(paramInt1) / j);
      f = i + i * distanceInfluenceForSnapDuration(f);
      i = Math.abs(paramInt2);
      if (i <= 0) {
        i = (int)(256.0F * (1.0F + Math.abs(paramInt1) / paramInt3));
      } else {
        i = 4 * Math.round(1000.0F * Math.abs(f / i));
      }
      i = Math.min(i, 600);
    }
    else
    {
      i = 0;
    }
    return i;
  }
  
  private int computeSettleDuration(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = clampMag(paramInt3, (int)this.mMinVelocity, (int)this.mMaxVelocity);
    int i = clampMag(paramInt4, (int)this.mMinVelocity, (int)this.mMaxVelocity);
    int k = Math.abs(paramInt1);
    int n = Math.abs(paramInt2);
    int i3 = Math.abs(j);
    int i1 = Math.abs(i);
    int i2 = i3 + i1;
    int m = k + n;
    float f1;
    if (j == 0) {
      f1 = k / m;
    } else {
      f1 = i3 / i2;
    }
    float f2;
    if (i == 0) {
      f2 = n / m;
    } else {
      f2 = i1 / i2;
    }
    j = computeAxisDuration(paramInt1, j, this.mCallback.getViewHorizontalDragRange(paramView));
    i = computeAxisDuration(paramInt2, i, this.mCallback.getViewVerticalDragRange(paramView));
    return (int)(f1 * j + f2 * i);
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, float paramFloat, Callback paramCallback)
  {
    ViewDragHelper localViewDragHelper = create(paramViewGroup, paramCallback);
    localViewDragHelper.mTouchSlop = ((int)(localViewDragHelper.mTouchSlop * (1.0F / paramFloat)));
    return localViewDragHelper;
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, Callback paramCallback)
  {
    return new ViewDragHelper(paramViewGroup.getContext(), paramViewGroup, paramCallback);
  }
  
  private void dispatchViewReleased(float paramFloat1, float paramFloat2)
  {
    this.mReleaseInProgress = true;
    this.mCallback.onViewReleased(this.mCapturedView, paramFloat1, paramFloat2);
    this.mReleaseInProgress = false;
    if (this.mDragState == 1) {
      setDragState(0);
    }
  }
  
  private float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)(0.4712389167638204D * (paramFloat - 0.5F)));
  }
  
  private void dragTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    int j = paramInt2;
    int k = this.mCapturedView.getLeft();
    int m = this.mCapturedView.getTop();
    if (paramInt3 != 0)
    {
      i = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, paramInt1, paramInt3);
      this.mCapturedView.offsetLeftAndRight(i - k);
    }
    if (paramInt4 != 0)
    {
      j = this.mCallback.clampViewPositionVertical(this.mCapturedView, paramInt2, paramInt4);
      this.mCapturedView.offsetTopAndBottom(j - m);
    }
    if ((paramInt3 != 0) || (paramInt4 != 0))
    {
      k = i - k;
      m = j - m;
      this.mCallback.onViewPositionChanged(this.mCapturedView, i, j, k, m);
    }
  }
  
  private void ensureMotionHistorySizeForId(int paramInt)
  {
    if ((this.mInitialMotionX == null) || (this.mInitialMotionX.length <= paramInt))
    {
      float[] arrayOfFloat2 = new float[paramInt + 1];
      float[] arrayOfFloat3 = new float[paramInt + 1];
      float[] arrayOfFloat4 = new float[paramInt + 1];
      float[] arrayOfFloat1 = new float[paramInt + 1];
      int[] arrayOfInt1 = new int[paramInt + 1];
      int[] arrayOfInt2 = new int[paramInt + 1];
      int[] arrayOfInt3 = new int[paramInt + 1];
      if (this.mInitialMotionX != null)
      {
        System.arraycopy(this.mInitialMotionX, 0, arrayOfFloat2, 0, this.mInitialMotionX.length);
        System.arraycopy(this.mInitialMotionY, 0, arrayOfFloat3, 0, this.mInitialMotionY.length);
        System.arraycopy(this.mLastMotionX, 0, arrayOfFloat4, 0, this.mLastMotionX.length);
        System.arraycopy(this.mLastMotionY, 0, arrayOfFloat1, 0, this.mLastMotionY.length);
        System.arraycopy(this.mInitialEdgesTouched, 0, arrayOfInt1, 0, this.mInitialEdgesTouched.length);
        System.arraycopy(this.mEdgeDragsInProgress, 0, arrayOfInt2, 0, this.mEdgeDragsInProgress.length);
        System.arraycopy(this.mEdgeDragsLocked, 0, arrayOfInt3, 0, this.mEdgeDragsLocked.length);
      }
      this.mInitialMotionX = arrayOfFloat2;
      this.mInitialMotionY = arrayOfFloat3;
      this.mLastMotionX = arrayOfFloat4;
      this.mLastMotionY = arrayOfFloat1;
      this.mInitialEdgesTouched = arrayOfInt1;
      this.mEdgeDragsInProgress = arrayOfInt2;
      this.mEdgeDragsLocked = arrayOfInt3;
    }
  }
  
  private boolean forceSettleCapturedViewAt(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int n = 0;
    int m = this.mCapturedView.getLeft();
    int k = this.mCapturedView.getTop();
    int j = paramInt1 - m;
    int i = paramInt2 - k;
    if ((j != 0) || (i != 0))
    {
      n = computeSettleDuration(this.mCapturedView, j, i, paramInt3, paramInt4);
      this.mScroller.startScroll(m, k, j, i, n);
      setDragState(2);
      n = 1;
    }
    else
    {
      this.mScroller.abortAnimation();
      setDragState(0);
    }
    return n;
  }
  
  private int getEdgesTouched(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (paramInt1 < this.mParentView.getLeft() + this.mEdgeSize) {
      i = 0x0 | 0x1;
    }
    if (paramInt2 < this.mParentView.getTop() + this.mEdgeSize) {
      i |= 0x4;
    }
    if (paramInt1 > this.mParentView.getRight() - this.mEdgeSize) {
      i |= 0x2;
    }
    if (paramInt2 > this.mParentView.getBottom() - this.mEdgeSize) {
      i |= 0x8;
    }
    return i;
  }
  
  private void releaseViewForPointerUp()
  {
    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
    dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
  }
  
  private void reportNewEdgeDrags(float paramFloat1, float paramFloat2, int paramInt)
  {
    int i = 0;
    if (checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 1)) {
      i = 0x0 | 0x1;
    }
    if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 4)) {
      i |= 0x4;
    }
    if (checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 2)) {
      i |= 0x2;
    }
    if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 8)) {
      i |= 0x8;
    }
    if (i != 0)
    {
      int[] arrayOfInt = this.mEdgeDragsInProgress;
      arrayOfInt[paramInt] = (i | arrayOfInt[paramInt]);
      this.mCallback.onEdgeDragStarted(i, paramInt);
    }
  }
  
  private void saveInitialMotion(float paramFloat1, float paramFloat2, int paramInt)
  {
    ensureMotionHistorySizeForId(paramInt);
    float[] arrayOfFloat = this.mInitialMotionX;
    this.mLastMotionX[paramInt] = paramFloat1;
    arrayOfFloat[paramInt] = paramFloat1;
    arrayOfFloat = this.mInitialMotionY;
    this.mLastMotionY[paramInt] = paramFloat2;
    arrayOfFloat[paramInt] = paramFloat2;
    this.mInitialEdgesTouched[paramInt] = getEdgesTouched((int)paramFloat1, (int)paramFloat2);
    this.mPointersDown |= 1 << paramInt;
  }
  
  private void saveLastMotion(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getPointerCount(paramMotionEvent);
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      int k = MotionEventCompat.getPointerId(paramMotionEvent, j);
      float f2 = MotionEventCompat.getX(paramMotionEvent, j);
      float f1 = MotionEventCompat.getY(paramMotionEvent, j);
      this.mLastMotionX[k] = f2;
      this.mLastMotionY[k] = f1;
    }
  }
  
  public void abort()
  {
    cancel();
    if (this.mDragState == 2)
    {
      int m = this.mScroller.getCurrX();
      int j = this.mScroller.getCurrY();
      this.mScroller.abortAnimation();
      int i = this.mScroller.getCurrX();
      int k = this.mScroller.getCurrY();
      this.mCallback.onViewPositionChanged(this.mCapturedView, i, k, i - m, k - j);
    }
    setDragState(0);
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ViewGroup localViewGroup;
    int j;
    if ((paramView instanceof ViewGroup))
    {
      localViewGroup = (ViewGroup)paramView;
      j = paramView.getScrollX();
      i = paramView.getScrollY();
    }
    for (int k = -1 + localViewGroup.getChildCount();; k--)
    {
      if (k < 0)
      {
        if ((!paramBoolean) || ((!ViewCompat.canScrollHorizontally(paramView, -paramInt1)) && (!ViewCompat.canScrollVertically(paramView, -paramInt2))))
        {
          i = 0;
          break label180;
        }
        i = 1;
        break label180;
      }
      View localView = localViewGroup.getChildAt(k);
      if ((paramInt3 + j >= localView.getLeft()) && (paramInt3 + j < localView.getRight()) && (paramInt4 + i >= localView.getTop()) && (paramInt4 + i < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2, paramInt3 + j - localView.getLeft(), paramInt4 + i - localView.getTop()))) {
        break;
      }
    }
    int i = 1;
    label180:
    return i;
  }
  
  public void cancel()
  {
    this.mActivePointerId = -1;
    clearMotionHistory();
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  public void captureChildView(View paramView, int paramInt)
  {
    if (paramView.getParent() == this.mParentView)
    {
      this.mCapturedView = paramView;
      this.mActivePointerId = paramInt;
      this.mCallback.onViewCaptured(paramView, paramInt);
      setDragState(1);
      return;
    }
    throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
  }
  
  public boolean checkTouchSlop(int paramInt)
  {
    int i = this.mInitialMotionX.length;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return 0;
      }
      if (checkTouchSlop(paramInt, j)) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  public boolean checkTouchSlop(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (isPointerDown(paramInt2))
    {
      int j;
      if ((paramInt1 & 0x1) != i) {
        j = 0;
      } else {
        j = i;
      }
      int k;
      if ((paramInt1 & 0x2) != 2) {
        k = 0;
      } else {
        k = i;
      }
      float f1 = this.mLastMotionX[paramInt2] - this.mInitialMotionX[paramInt2];
      float f2 = this.mLastMotionY[paramInt2] - this.mInitialMotionY[paramInt2];
      if ((j == 0) || (k == 0))
      {
        if (j == 0)
        {
          if (k == 0) {
            i = 0;
          } else if (Math.abs(f2) <= this.mTouchSlop) {
            i = 0;
          }
        }
        else if (Math.abs(f1) <= this.mTouchSlop) {
          i = 0;
        }
      }
      else if (f1 * f1 + f2 * f2 <= this.mTouchSlop * this.mTouchSlop) {
        i = 0;
      }
    }
    else
    {
      i = 0;
    }
    return i;
  }
  
  public boolean continueSettling(boolean paramBoolean)
  {
    int i;
    if (this.mDragState == 2)
    {
      boolean bool = this.mScroller.computeScrollOffset();
      int j = this.mScroller.getCurrX();
      i = this.mScroller.getCurrY();
      int m = j - this.mCapturedView.getLeft();
      int k = i - this.mCapturedView.getTop();
      if (m != 0) {
        this.mCapturedView.offsetLeftAndRight(m);
      }
      if (k != 0) {
        this.mCapturedView.offsetTopAndBottom(k);
      }
      if ((m != 0) || (k != 0)) {
        this.mCallback.onViewPositionChanged(this.mCapturedView, j, i, m, k);
      }
      if ((bool) && (j == this.mScroller.getFinalX()) && (i == this.mScroller.getFinalY()))
      {
        this.mScroller.abortAnimation();
        bool = false;
      }
      if (!bool) {
        if (!paramBoolean) {
          setDragState(0);
        } else {
          this.mParentView.post(this.mSetIdleRunnable);
        }
      }
    }
    if (this.mDragState != 2) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  public View findTopChildUnder(int paramInt1, int paramInt2)
  {
    View localView;
    for (int i = -1 + this.mParentView.getChildCount();; i--)
    {
      if (i < 0)
      {
        localView = null;
        break;
      }
      localView = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(i));
      if ((paramInt1 >= localView.getLeft()) && (paramInt1 < localView.getRight()) && (paramInt2 >= localView.getTop()) && (paramInt2 < localView.getBottom())) {
        break;
      }
    }
    return localView;
  }
  
  public void flingCapturedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mReleaseInProgress)
    {
      this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int)VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), paramInt1, paramInt3, paramInt2, paramInt4);
      setDragState(2);
      return;
    }
    throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
  }
  
  public int getActivePointerId()
  {
    return this.mActivePointerId;
  }
  
  public View getCapturedView()
  {
    return this.mCapturedView;
  }
  
  public int getEdgeSize()
  {
    return this.mEdgeSize;
  }
  
  public float getMinVelocity()
  {
    return this.mMinVelocity;
  }
  
  public int getTouchSlop()
  {
    return this.mTouchSlop;
  }
  
  public int getViewDragState()
  {
    return this.mDragState;
  }
  
  public boolean isCapturedViewUnder(int paramInt1, int paramInt2)
  {
    return isViewUnder(this.mCapturedView, paramInt1, paramInt2);
  }
  
  public boolean isEdgeTouched(int paramInt)
  {
    int j = this.mInitialEdgesTouched.length;
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return 0;
      }
      if (isEdgeTouched(paramInt, i)) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  public boolean isEdgeTouched(int paramInt1, int paramInt2)
  {
    boolean bool;
    if ((!isPointerDown(paramInt2)) || ((paramInt1 & this.mInitialEdgesTouched[paramInt2]) == 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isPointerDown(int paramInt)
  {
    int i = 1;
    if ((this.mPointersDown & i << paramInt) == 0) {
      i = 0;
    }
    return i;
  }
  
  public boolean isViewUnder(View paramView, int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if ((paramView != null) && (paramInt1 >= paramView.getLeft()) && (paramInt1 < paramView.getRight()) && (paramInt2 >= paramView.getTop()) && (paramInt2 < paramView.getBottom())) {
      bool = true;
    }
    return bool;
  }
  
  public void processTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int i1 = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (i == 0) {
      cancel();
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int k;
    int i2;
    int i5;
    float f10;
    int j;
    float f7;
    int n;
    switch (i)
    {
    case 0: 
      float f6 = paramMotionEvent.getX();
      float f3 = paramMotionEvent.getY();
      i = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      View localView1 = findTopChildUnder((int)f6, (int)f3);
      saveInitialMotion(f6, f3, i);
      tryCaptureViewForDrag(localView1, i);
      k = this.mInitialEdgesTouched[i];
      if ((k & this.mTrackingEdges) != 0) {
        this.mCallback.onEdgeTouched(k & this.mTrackingEdges, i);
      }
      break;
    case 1: 
      if (this.mDragState == 1) {
        releaseViewForPointerUp();
      }
      cancel();
      break;
    case 2: 
      if (this.mDragState != 1)
      {
        k = MotionEventCompat.getPointerCount(paramMotionEvent);
        for (i2 = 0;; i2++)
        {
          if (i2 < k)
          {
            i5 = MotionEventCompat.getPointerId(paramMotionEvent, i2);
            float f9 = MotionEventCompat.getX(paramMotionEvent, i2);
            f10 = MotionEventCompat.getY(paramMotionEvent, i2);
            f1 = f9 - this.mInitialMotionX[i5];
            float f8 = f10 - this.mInitialMotionY[i5];
            reportNewEdgeDrags(f1, f8, i5);
            if (this.mDragState != 1)
            {
              View localView2 = findTopChildUnder((int)f9, (int)f10);
              if ((!checkTouchSlop(localView2, f1, f8)) || (!tryCaptureViewForDrag(localView2, i5))) {
                continue;
              }
            }
          }
          saveLastMotion(paramMotionEvent);
          break;
        }
      }
      k = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
      float f1 = MotionEventCompat.getX(paramMotionEvent, k);
      float f4 = MotionEventCompat.getY(paramMotionEvent, k);
      j = (int)(f1 - this.mLastMotionX[this.mActivePointerId]);
      int m = (int)(f4 - this.mLastMotionY[this.mActivePointerId]);
      dragTo(j + this.mCapturedView.getLeft(), m + this.mCapturedView.getTop(), j, m);
      saveLastMotion(paramMotionEvent);
      break;
    case 3: 
      if (this.mDragState == 1) {
        dispatchViewReleased(0.0F, 0.0F);
      }
      cancel();
      break;
    case 5: 
      j = MotionEventCompat.getPointerId(paramMotionEvent, i2);
      float f5 = MotionEventCompat.getX(paramMotionEvent, i2);
      f7 = MotionEventCompat.getY(paramMotionEvent, i2);
      saveInitialMotion(f5, f7, j);
      if (this.mDragState != 0)
      {
        if (isCapturedViewUnder((int)f5, (int)f7)) {
          tryCaptureViewForDrag(this.mCapturedView, j);
        }
      }
      else
      {
        tryCaptureViewForDrag(findTopChildUnder((int)f5, (int)f7), j);
        n = this.mInitialEdgesTouched[j];
        if ((n & this.mTrackingEdges) != 0) {
          this.mCallback.onEdgeTouched(n & this.mTrackingEdges, j);
        }
      }
      break;
    case 6: 
      int i4 = MotionEventCompat.getPointerId(paramMotionEvent, f7);
      if ((this.mDragState == 1) && (i4 == this.mActivePointerId))
      {
        int i3 = -1;
        n = MotionEventCompat.getPointerCount(paramMotionEvent);
        int i6 = 0;
        while (i6 < n)
        {
          i5 = MotionEventCompat.getPointerId(paramMotionEvent, i6);
          if (i5 != this.mActivePointerId)
          {
            f10 = MotionEventCompat.getX(paramMotionEvent, i6);
            float f2 = MotionEventCompat.getY(paramMotionEvent, i6);
            if ((findTopChildUnder((int)f10, (int)f2) == this.mCapturedView) && (tryCaptureViewForDrag(this.mCapturedView, i5))) {}
          }
          else
          {
            i6++;
            continue;
          }
          i3 = this.mActivePointerId;
        }
        if (i3 == -1) {
          releaseViewForPointerUp();
        }
      }
      clearMotionHistory(i4);
    }
  }
  
  void setDragState(int paramInt)
  {
    if (this.mDragState != paramInt)
    {
      this.mDragState = paramInt;
      this.mCallback.onViewDragStateChanged(paramInt);
      if (this.mDragState == 0) {
        this.mCapturedView = null;
      }
    }
  }
  
  public void setEdgeTrackingEnabled(int paramInt)
  {
    this.mTrackingEdges = paramInt;
  }
  
  public void setMinVelocity(float paramFloat)
  {
    this.mMinVelocity = paramFloat;
  }
  
  public boolean settleCapturedViewAt(int paramInt1, int paramInt2)
  {
    if (this.mReleaseInProgress) {
      return forceSettleCapturedViewAt(paramInt1, paramInt2, (int)VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
    }
    throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
  }
  
  public boolean shouldInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int k = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (i == 0) {
      cancel();
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    float f3;
    int j;
    int n;
    switch (i)
    {
    case 0: 
      float f2 = paramMotionEvent.getX();
      float f4 = paramMotionEvent.getY();
      i = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      saveInitialMotion(f2, f4, i);
      View localView1 = findTopChildUnder((int)f2, (int)f4);
      if ((localView1 == this.mCapturedView) && (this.mDragState == 2)) {
        tryCaptureViewForDrag(localView1, i);
      }
      int m = this.mInitialEdgesTouched[i];
      if ((m & this.mTrackingEdges) != 0) {
        this.mCallback.onEdgeTouched(m & this.mTrackingEdges, i);
      }
      break;
    case 1: 
    case 3: 
      cancel();
      break;
    case 2: 
      int i3 = MotionEventCompat.getPointerCount(paramMotionEvent);
      for (int i2 = 0;; i2++)
      {
        if (i2 < i3)
        {
          int i1 = MotionEventCompat.getPointerId(paramMotionEvent, i2);
          float f6 = MotionEventCompat.getX(paramMotionEvent, i2);
          float f7 = MotionEventCompat.getY(paramMotionEvent, i2);
          float f1 = f6 - this.mInitialMotionX[i1];
          f3 = f7 - this.mInitialMotionY[i1];
          View localView3 = findTopChildUnder((int)f6, (int)f7);
          int i5;
          if ((localView3 == null) || (!checkTouchSlop(localView3, f1, f3))) {
            i5 = 0;
          } else {
            i5 = 1;
          }
          if (i5 != 0)
          {
            int i4 = localView3.getLeft();
            int i6 = i4 + (int)f1;
            i6 = this.mCallback.clampViewPositionHorizontal(localView3, i6, (int)f1);
            int i7 = localView3.getTop();
            int i8 = i7 + (int)f3;
            int i9 = this.mCallback.clampViewPositionVertical(localView3, i8, (int)f3);
            i8 = this.mCallback.getViewHorizontalDragRange(localView3);
            int i10 = this.mCallback.getViewVerticalDragRange(localView3);
            if (((i8 == 0) || ((i8 > 0) && (i6 == i4))) && ((i10 == 0) || ((i10 > 0) && (i9 == i7)))) {}
          }
          else
          {
            reportNewEdgeDrags(f1, f3, i1);
            if ((this.mDragState != 1) && ((i5 == 0) || (!tryCaptureViewForDrag(localView3, i1)))) {
              continue;
            }
          }
        }
        saveLastMotion(paramMotionEvent);
        break;
      }
    case 5: 
      j = MotionEventCompat.getPointerId(paramMotionEvent, f3);
      float f5 = MotionEventCompat.getX(paramMotionEvent, f3);
      f3 = MotionEventCompat.getY(paramMotionEvent, f3);
      saveInitialMotion(f5, f3, j);
      if (this.mDragState != 0)
      {
        if (this.mDragState == 2)
        {
          View localView2 = findTopChildUnder((int)f5, (int)f3);
          if (localView2 == this.mCapturedView) {
            tryCaptureViewForDrag(localView2, j);
          }
        }
      }
      else
      {
        n = this.mInitialEdgesTouched[j];
        if ((n & this.mTrackingEdges) != 0) {
          this.mCallback.onEdgeTouched(n & this.mTrackingEdges, j);
        }
      }
      break;
    case 6: 
      clearMotionHistory(MotionEventCompat.getPointerId(paramMotionEvent, n));
    }
    if (this.mDragState != 1) {
      j = 0;
    } else {
      j = 1;
    }
    return j;
  }
  
  public boolean smoothSlideViewTo(View paramView, int paramInt1, int paramInt2)
  {
    this.mCapturedView = paramView;
    this.mActivePointerId = -1;
    boolean bool = forceSettleCapturedViewAt(paramInt1, paramInt2, 0, 0);
    if ((!bool) && (this.mDragState == 0) && (this.mCapturedView != null)) {
      this.mCapturedView = null;
    }
    return bool;
  }
  
  boolean tryCaptureViewForDrag(View paramView, int paramInt)
  {
    boolean bool = true;
    if ((paramView != this.mCapturedView) || (this.mActivePointerId != paramInt)) {
      if ((paramView == null) || (!this.mCallback.tryCaptureView(paramView, paramInt)))
      {
        bool = false;
      }
      else
      {
        this.mActivePointerId = paramInt;
        captureChildView(paramView, paramInt);
      }
    }
    return bool;
  }
  
  public static abstract class Callback
  {
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int getOrderedChildIndex(int paramInt)
    {
      return paramInt;
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return 0;
    }
    
    public int getViewVerticalDragRange(View paramView)
    {
      return 0;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2) {}
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2) {}
    
    public void onViewCaptured(View paramView, int paramInt) {}
    
    public void onViewDragStateChanged(int paramInt) {}
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2) {}
    
    public abstract boolean tryCaptureView(View paramView, int paramInt);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\ViewDragHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */