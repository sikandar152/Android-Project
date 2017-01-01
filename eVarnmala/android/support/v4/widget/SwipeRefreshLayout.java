package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class SwipeRefreshLayout
  extends ViewGroup
{
  private static final int ALPHA_ANIMATION_DURATION = 300;
  private static final int ANIMATE_TO_START_DURATION = 200;
  private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
  private static final int CIRCLE_BG_LIGHT = -328966;
  private static final int CIRCLE_DIAMETER = 40;
  private static final int CIRCLE_DIAMETER_LARGE = 56;
  private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
  public static final int DEFAULT = 1;
  private static final int DEFAULT_CIRCLE_TARGET = 64;
  private static final float DRAG_RATE = 0.5F;
  private static final int INVALID_POINTER = -1;
  public static final int LARGE = 0;
  private static final int[] LAYOUT_ATTRS;
  private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
  private static final int MAX_ALPHA = 255;
  private static final float MAX_PROGRESS_ANGLE = 0.8F;
  private static final int SCALE_DOWN_DURATION = 150;
  private static final int STARTING_PROGRESS_ALPHA = 76;
  private int mActivePointerId = -1;
  private Animation mAlphaMaxAnimation;
  private Animation mAlphaStartAnimation;
  private final Animation mAnimateToCorrectPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      if (SwipeRefreshLayout.this.mUsingCustomStart) {
        i = (int)SwipeRefreshLayout.this.mSpinnerFinalOffset;
      } else {
        i = (int)(SwipeRefreshLayout.this.mSpinnerFinalOffset - Math.abs(SwipeRefreshLayout.this.mOriginalOffsetTop));
      }
      int i = SwipeRefreshLayout.this.mFrom + (int)(paramAnonymousFloat * (i - SwipeRefreshLayout.this.mFrom)) - SwipeRefreshLayout.this.mCircleView.getTop();
      SwipeRefreshLayout.this.setTargetOffsetTopAndBottom(i, false);
    }
  };
  private final Animation mAnimateToStartPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      SwipeRefreshLayout.this.moveToStart(paramAnonymousFloat);
    }
  };
  private int mCircleHeight;
  private CircleImageView mCircleView;
  private int mCircleViewIndex = -1;
  private int mCircleWidth;
  private int mCurrentTargetOffsetTop;
  private final DecelerateInterpolator mDecelerateInterpolator;
  protected int mFrom;
  private float mInitialMotionY;
  private boolean mIsBeingDragged;
  private OnRefreshListener mListener;
  private int mMediumAnimationDuration;
  private boolean mNotify;
  private boolean mOriginalOffsetCalculated = false;
  protected int mOriginalOffsetTop;
  private MaterialProgressDrawable mProgress;
  private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener()
  {
    public void onAnimationEnd(Animation paramAnonymousAnimation)
    {
      if (!SwipeRefreshLayout.this.mRefreshing)
      {
        SwipeRefreshLayout.this.mProgress.stop();
        SwipeRefreshLayout.this.mCircleView.setVisibility(8);
        SwipeRefreshLayout.this.setColorViewAlpha(255);
        if (!SwipeRefreshLayout.this.mScale) {
          SwipeRefreshLayout.this.setTargetOffsetTopAndBottom(SwipeRefreshLayout.this.mOriginalOffsetTop - SwipeRefreshLayout.this.mCurrentTargetOffsetTop, true);
        } else {
          SwipeRefreshLayout.this.setAnimationProgress(0.0F);
        }
      }
      else
      {
        SwipeRefreshLayout.this.mProgress.setAlpha(255);
        SwipeRefreshLayout.this.mProgress.start();
        if ((SwipeRefreshLayout.this.mNotify) && (SwipeRefreshLayout.this.mListener != null)) {
          SwipeRefreshLayout.this.mListener.onRefresh();
        }
      }
      SwipeRefreshLayout.access$802(SwipeRefreshLayout.this, SwipeRefreshLayout.this.mCircleView.getTop());
    }
    
    public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
    
    public void onAnimationStart(Animation paramAnonymousAnimation) {}
  };
  private boolean mRefreshing = false;
  private boolean mReturningToStart;
  private boolean mScale;
  private Animation mScaleAnimation;
  private Animation mScaleDownAnimation;
  private Animation mScaleDownToStartAnimation;
  private float mSpinnerFinalOffset;
  private float mStartingScale;
  private View mTarget;
  private float mTotalDragDistance = -1.0F;
  private int mTouchSlop;
  private boolean mUsingCustomStart;
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16842766;
    LAYOUT_ATTRS = arrayOfInt;
  }
  
  public SwipeRefreshLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwipeRefreshLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mMediumAnimationDuration = getResources().getInteger(17694721);
    setWillNotDraw(false);
    this.mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
    Object localObject = paramContext.obtainStyledAttributes(paramAttributeSet, LAYOUT_ATTRS);
    setEnabled(((TypedArray)localObject).getBoolean(0, true));
    ((TypedArray)localObject).recycle();
    localObject = getResources().getDisplayMetrics();
    this.mCircleWidth = ((int)(40.0F * ((DisplayMetrics)localObject).density));
    this.mCircleHeight = ((int)(40.0F * ((DisplayMetrics)localObject).density));
    createProgressView();
    ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    this.mSpinnerFinalOffset = (64.0F * ((DisplayMetrics)localObject).density);
    this.mTotalDragDistance = this.mSpinnerFinalOffset;
  }
  
  private void animateOffsetToCorrectPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    this.mFrom = paramInt;
    this.mAnimateToCorrectPosition.reset();
    this.mAnimateToCorrectPosition.setDuration(200L);
    this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
    if (paramAnimationListener != null) {
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
    this.mCircleView.clearAnimation();
    this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
  }
  
  private void animateOffsetToStartPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    if (!this.mScale)
    {
      this.mFrom = paramInt;
      this.mAnimateToStartPosition.reset();
      this.mAnimateToStartPosition.setDuration(200L);
      this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
      if (paramAnimationListener != null) {
        this.mCircleView.setAnimationListener(paramAnimationListener);
      }
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(this.mAnimateToStartPosition);
    }
    else
    {
      startScaleDownReturnToStartAnimation(paramInt, paramAnimationListener);
    }
  }
  
  private void createProgressView()
  {
    this.mCircleView = new CircleImageView(getContext(), -328966, 20.0F);
    this.mProgress = new MaterialProgressDrawable(getContext(), this);
    this.mProgress.setBackgroundColor(-328966);
    this.mCircleView.setImageDrawable(this.mProgress);
    this.mCircleView.setVisibility(8);
    addView(this.mCircleView);
  }
  
  private void ensureTarget()
  {
    if (this.mTarget == null)
    {
      int i = 0;
      while (i < getChildCount())
      {
        View localView = getChildAt(i);
        if (localView.equals(this.mCircleView)) {
          i++;
        } else {
          this.mTarget = localView;
        }
      }
    }
  }
  
  private float getMotionEventY(MotionEvent paramMotionEvent, int paramInt)
  {
    int i = MotionEventCompat.findPointerIndex(paramMotionEvent, paramInt);
    float f;
    if (i >= 0) {
      f = MotionEventCompat.getY(paramMotionEvent, i);
    } else {
      f = -1.0F;
    }
    return f;
  }
  
  private boolean isAlphaUsedForScale()
  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 11) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean isAnimationRunning(Animation paramAnimation)
  {
    boolean bool;
    if ((paramAnimation == null) || (!paramAnimation.hasStarted()) || (paramAnimation.hasEnded())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void moveToStart(float paramFloat)
  {
    setTargetOffsetTopAndBottom(this.mFrom + (int)(paramFloat * (this.mOriginalOffsetTop - this.mFrom)) - this.mCircleView.getTop(), false);
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (MotionEventCompat.getPointerId(paramMotionEvent, i) == this.mActivePointerId)
    {
      if (i != 0) {
        i = 0;
      } else {
        i = 1;
      }
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, i);
    }
  }
  
  private void setAnimationProgress(float paramFloat)
  {
    if (!isAlphaUsedForScale())
    {
      ViewCompat.setScaleX(this.mCircleView, paramFloat);
      ViewCompat.setScaleY(this.mCircleView, paramFloat);
    }
    else
    {
      setColorViewAlpha((int)(255.0F * paramFloat));
    }
  }
  
  private void setColorViewAlpha(int paramInt)
  {
    this.mCircleView.getBackground().setAlpha(paramInt);
    this.mProgress.setAlpha(paramInt);
  }
  
  private void setRefreshing(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mRefreshing != paramBoolean1)
    {
      this.mNotify = paramBoolean2;
      ensureTarget();
      this.mRefreshing = paramBoolean1;
      if (!this.mRefreshing) {
        startScaleDownAnimation(this.mRefreshListener);
      } else {
        animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
      }
    }
  }
  
  private void setTargetOffsetTopAndBottom(int paramInt, boolean paramBoolean)
  {
    this.mCircleView.bringToFront();
    this.mCircleView.offsetTopAndBottom(paramInt);
    this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    if ((paramBoolean) && (Build.VERSION.SDK_INT < 11)) {
      invalidate();
    }
  }
  
  private Animation startAlphaAnimation(final int paramInt1, final int paramInt2)
  {
    Animation local4;
    if ((!this.mScale) || (!isAlphaUsedForScale()))
    {
      local4 = new Animation()
      {
        public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
        {
          SwipeRefreshLayout.this.mProgress.setAlpha((int)(paramInt1 + paramAnonymousFloat * (paramInt2 - paramInt1)));
        }
      };
      local4.setDuration(300L);
      this.mCircleView.setAnimationListener(null);
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(local4);
    }
    else
    {
      local4 = null;
    }
    return local4;
  }
  
  private void startProgressAlphaMaxAnimation()
  {
    this.mAlphaMaxAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 255);
  }
  
  private void startProgressAlphaStartAnimation()
  {
    this.mAlphaStartAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 76);
  }
  
  private void startScaleDownAnimation(Animation.AnimationListener paramAnimationListener)
  {
    this.mScaleDownAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        SwipeRefreshLayout.this.setAnimationProgress(1.0F - paramAnonymousFloat);
      }
    };
    this.mScaleDownAnimation.setDuration(150L);
    this.mCircleView.setAnimationListener(paramAnimationListener);
    this.mCircleView.clearAnimation();
    this.mCircleView.startAnimation(this.mScaleDownAnimation);
  }
  
  private void startScaleDownReturnToStartAnimation(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    this.mFrom = paramInt;
    if (!isAlphaUsedForScale()) {
      this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
    } else {
      this.mStartingScale = this.mProgress.getAlpha();
    }
    this.mScaleDownToStartAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        float f = SwipeRefreshLayout.this.mStartingScale + paramAnonymousFloat * -SwipeRefreshLayout.this.mStartingScale;
        SwipeRefreshLayout.this.setAnimationProgress(f);
        SwipeRefreshLayout.this.moveToStart(paramAnonymousFloat);
      }
    };
    this.mScaleDownToStartAnimation.setDuration(150L);
    if (paramAnimationListener != null) {
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
    this.mCircleView.clearAnimation();
    this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
  }
  
  private void startScaleUpAnimation(Animation.AnimationListener paramAnimationListener)
  {
    this.mCircleView.setVisibility(0);
    if (Build.VERSION.SDK_INT >= 11) {
      this.mProgress.setAlpha(255);
    }
    this.mScaleAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        SwipeRefreshLayout.this.setAnimationProgress(paramAnonymousFloat);
      }
    };
    this.mScaleAnimation.setDuration(this.mMediumAnimationDuration);
    if (paramAnimationListener != null) {
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
    this.mCircleView.clearAnimation();
    this.mCircleView.startAnimation(this.mScaleAnimation);
  }
  
  public boolean canChildScrollUp()
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 14)
    {
      bool = ViewCompat.canScrollVertically(this.mTarget, -1);
    }
    else if (!(this.mTarget instanceof AbsListView))
    {
      if (this.mTarget.getScrollY() <= 0) {
        bool = false;
      }
    }
    else
    {
      AbsListView localAbsListView = (AbsListView)this.mTarget;
      if ((localAbsListView.getChildCount() <= 0) || ((localAbsListView.getFirstVisiblePosition() <= 0) && (localAbsListView.getChildAt(0).getTop() >= localAbsListView.getPaddingTop()))) {
        bool = false;
      }
    }
    return bool;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mCircleViewIndex >= 0) {
      if (paramInt2 != paramInt1 - 1)
      {
        if (paramInt2 >= this.mCircleViewIndex) {
          paramInt2++;
        }
      }
      else {
        paramInt2 = this.mCircleViewIndex;
      }
    }
    return paramInt2;
  }
  
  public boolean isRefreshing()
  {
    return this.mRefreshing;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    ensureTarget();
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((this.mReturningToStart) && (i == 0)) {
      this.mReturningToStart = false;
    }
    if ((isEnabled()) && (!this.mReturningToStart) && (!canChildScrollUp()) && (!this.mRefreshing))
    {
      float f;
      switch (i)
      {
      case 0: 
        setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop(), true);
        this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
        this.mIsBeingDragged = false;
        f = getMotionEventY(paramMotionEvent, this.mActivePointerId);
        if (f == -1.0F) {
          return bool;
        }
        this.mInitialMotionY = f;
      case 2: 
        if (this.mActivePointerId != -1)
        {
          f = getMotionEventY(paramMotionEvent, this.mActivePointerId);
          if (f == -1.0F) {
            return bool;
          }
          if ((f - this.mInitialMotionY > this.mTouchSlop) && (!this.mIsBeingDragged))
          {
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(76);
          }
        }
        else
        {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
        }
        break;
      case 1: 
      case 3: 
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
        break;
      case 6: 
        onSecondaryPointerUp(paramMotionEvent);
      }
      bool = this.mIsBeingDragged;
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getMeasuredWidth();
    int n = getMeasuredHeight();
    if (getChildCount() != 0)
    {
      if (this.mTarget == null) {
        ensureTarget();
      }
      if (this.mTarget != null)
      {
        View localView = this.mTarget;
        int k = getPaddingLeft();
        int j = getPaddingTop();
        int m = i - getPaddingLeft() - getPaddingRight();
        n = n - getPaddingTop() - getPaddingBottom();
        localView.layout(k, j, k + m, j + n);
        k = this.mCircleView.getMeasuredWidth();
        j = this.mCircleView.getMeasuredHeight();
        this.mCircleView.layout(i / 2 - k / 2, this.mCurrentTargetOffsetTop, i / 2 + k / 2, j + this.mCurrentTargetOffsetTop);
      }
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.mTarget == null) {
      ensureTarget();
    }
    if (this.mTarget != null)
    {
      this.mTarget.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824));
      this.mCircleView.measure(View.MeasureSpec.makeMeasureSpec(this.mCircleWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mCircleHeight, 1073741824));
      if ((!this.mUsingCustomStart) && (!this.mOriginalOffsetCalculated))
      {
        this.mOriginalOffsetCalculated = true;
        i = -this.mCircleView.getMeasuredHeight();
        this.mOriginalOffsetTop = i;
        this.mCurrentTargetOffsetTop = i;
      }
      this.mCircleViewIndex = -1;
      int i = 0;
      while (i < getChildCount()) {
        if (getChildAt(i) != this.mCircleView) {
          i++;
        } else {
          this.mCircleViewIndex = i;
        }
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((this.mReturningToStart) && (i == 0)) {
      this.mReturningToStart = false;
    }
    boolean bool;
    if ((isEnabled()) && (!this.mReturningToStart) && (!canChildScrollUp()))
    {
      int j;
      switch (i)
      {
      case 0: 
        this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
        this.mIsBeingDragged = false;
        break;
      case 1: 
      case 3: 
        if (this.mActivePointerId != -1)
        {
          float f1 = 0.5F * (MotionEventCompat.getY(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)) - this.mInitialMotionY);
          this.mIsBeingDragged = false;
          if (f1 <= this.mTotalDragDistance)
          {
            this.mRefreshing = false;
            this.mProgress.setStartEndTrim(0.0F, 0.0F);
            Animation.AnimationListener local5 = null;
            if (!this.mScale) {
              local5 = new Animation.AnimationListener()
              {
                public void onAnimationEnd(Animation paramAnonymousAnimation)
                {
                  if (!SwipeRefreshLayout.this.mScale) {
                    SwipeRefreshLayout.this.startScaleDownAnimation(null);
                  }
                }
                
                public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
                
                public void onAnimationStart(Animation paramAnonymousAnimation) {}
              };
            }
            animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, local5);
            this.mProgress.showArrow(false);
          }
          else
          {
            setRefreshing(true, true);
          }
          this.mActivePointerId = -1;
          j = 0;
        }
        else
        {
          if (j == 1) {
            Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
          }
          j = 0;
        }
        break;
      case 2: 
        j = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        if (j >= 0)
        {
          float f2 = 0.5F * (MotionEventCompat.getY(paramMotionEvent, j) - this.mInitialMotionY);
          if (this.mIsBeingDragged)
          {
            this.mProgress.showArrow(true);
            float f3 = f2 / this.mTotalDragDistance;
            if (f3 >= 0.0F)
            {
              float f4 = Math.min(1.0F, Math.abs(f3));
              f3 = 5.0F * (float)Math.max(f4 - 0.4D, 0.0D) / 3.0F;
              float f6 = Math.abs(f2) - this.mTotalDragDistance;
              float f5;
              if (!this.mUsingCustomStart) {
                f5 = this.mSpinnerFinalOffset;
              } else {
                f5 = this.mSpinnerFinalOffset - this.mOriginalOffsetTop;
              }
              f6 = Math.max(0.0F, Math.min(f6, 2.0F * f5) / f5);
              f6 = 2.0F * (float)(f6 / 4.0F - Math.pow(f6 / 4.0F, 2.0D));
              float f7 = 2.0F * (f5 * f6);
              int k = this.mOriginalOffsetTop + (int)(f7 + f5 * f4);
              if (this.mCircleView.getVisibility() != 0) {
                this.mCircleView.setVisibility(0);
              }
              if (!this.mScale)
              {
                ViewCompat.setScaleX(this.mCircleView, 1.0F);
                ViewCompat.setScaleY(this.mCircleView, 1.0F);
              }
              if (f2 >= this.mTotalDragDistance)
              {
                if ((this.mProgress.getAlpha() < 255) && (!isAnimationRunning(this.mAlphaMaxAnimation))) {
                  startProgressAlphaMaxAnimation();
                }
              }
              else
              {
                if (this.mScale) {
                  setAnimationProgress(f2 / this.mTotalDragDistance);
                }
                if ((this.mProgress.getAlpha() > 76) && (!isAnimationRunning(this.mAlphaStartAnimation))) {
                  startProgressAlphaStartAnimation();
                }
                f2 = f3 * 0.8F;
                this.mProgress.setStartEndTrim(0.0F, Math.min(0.8F, f2));
                this.mProgress.setArrowScale(Math.min(1.0F, f3));
              }
              f2 = 0.5F * (-0.25F + 0.4F * f3 + 2.0F * f6);
              this.mProgress.setProgressRotation(f2);
              setTargetOffsetTopAndBottom(k - this.mCurrentTargetOffsetTop, true);
            }
            else
            {
              return false;
            }
          }
        }
        else
        {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          bool = false;
        }
        break;
      case 5: 
        this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, MotionEventCompat.getActionIndex(paramMotionEvent));
        break;
      case 6: 
        onSecondaryPointerUp(paramMotionEvent);
      }
      bool = true;
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {}
  
  @Deprecated
  public void setColorScheme(int... paramVarArgs)
  {
    setColorSchemeResources(paramVarArgs);
  }
  
  public void setColorSchemeColors(int... paramVarArgs)
  {
    ensureTarget();
    this.mProgress.setColorSchemeColors(paramVarArgs);
  }
  
  public void setColorSchemeResources(int... paramVarArgs)
  {
    Resources localResources = getResources();
    int[] arrayOfInt = new int[paramVarArgs.length];
    for (int i = 0;; i++)
    {
      if (i >= paramVarArgs.length)
      {
        setColorSchemeColors(arrayOfInt);
        return;
      }
      arrayOfInt[i] = localResources.getColor(paramVarArgs[i]);
    }
  }
  
  public void setDistanceToTriggerSync(int paramInt)
  {
    this.mTotalDragDistance = paramInt;
  }
  
  public void setOnRefreshListener(OnRefreshListener paramOnRefreshListener)
  {
    this.mListener = paramOnRefreshListener;
  }
  
  public void setProgressBackgroundColor(int paramInt)
  {
    this.mCircleView.setBackgroundColor(paramInt);
    this.mProgress.setBackgroundColor(getResources().getColor(paramInt));
  }
  
  public void setProgressViewEndTarget(boolean paramBoolean, int paramInt)
  {
    this.mSpinnerFinalOffset = paramInt;
    this.mScale = paramBoolean;
    this.mCircleView.invalidate();
  }
  
  public void setProgressViewOffset(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mScale = paramBoolean;
    this.mCircleView.setVisibility(8);
    this.mCurrentTargetOffsetTop = paramInt1;
    this.mOriginalOffsetTop = paramInt1;
    this.mSpinnerFinalOffset = paramInt2;
    this.mUsingCustomStart = true;
    this.mCircleView.invalidate();
  }
  
  public void setRefreshing(boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mRefreshing == paramBoolean))
    {
      setRefreshing(paramBoolean, false);
    }
    else
    {
      this.mRefreshing = paramBoolean;
      int i;
      if (this.mUsingCustomStart) {
        i = (int)this.mSpinnerFinalOffset;
      } else {
        i = (int)(this.mSpinnerFinalOffset + this.mOriginalOffsetTop);
      }
      setTargetOffsetTopAndBottom(i - this.mCurrentTargetOffsetTop, true);
      this.mNotify = false;
      startScaleUpAnimation(this.mRefreshListener);
    }
  }
  
  public void setSize(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 1))
    {
      DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
      int i;
      if (paramInt != 0)
      {
        i = (int)(40.0F * localDisplayMetrics.density);
        this.mCircleWidth = i;
        this.mCircleHeight = i;
      }
      else
      {
        i = (int)(56.0F * i.density);
        this.mCircleWidth = i;
        this.mCircleHeight = i;
      }
      this.mCircleView.setImageDrawable(null);
      this.mProgress.updateSizes(paramInt);
      this.mCircleView.setImageDrawable(this.mProgress);
    }
  }
  
  public static abstract interface OnRefreshListener
  {
    public abstract void onRefresh();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\SwipeRefreshLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */