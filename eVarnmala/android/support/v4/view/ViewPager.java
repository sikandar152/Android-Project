package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewPager
  extends ViewGroup
{
  private static final int CLOSE_ENOUGH = 2;
  private static final Comparator<ItemInfo> COMPARATOR = new Comparator()
  {
    public int compare(ViewPager.ItemInfo paramAnonymousItemInfo1, ViewPager.ItemInfo paramAnonymousItemInfo2)
    {
      return paramAnonymousItemInfo1.position - paramAnonymousItemInfo2.position;
    }
  };
  private static final boolean DEBUG = false;
  private static final int DEFAULT_GUTTER_SIZE = 16;
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  private static final int DRAW_ORDER_DEFAULT = 0;
  private static final int DRAW_ORDER_FORWARD = 1;
  private static final int DRAW_ORDER_REVERSE = 2;
  private static final int INVALID_POINTER = -1;
  private static final int[] LAYOUT_ATTRS;
  private static final int MAX_SETTLE_DURATION = 600;
  private static final int MIN_DISTANCE_FOR_FLING = 25;
  private static final int MIN_FLING_VELOCITY = 400;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "ViewPager";
  private static final boolean USE_CACHE;
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      float f = paramAnonymousFloat - 1.0F;
      return 1.0F + f * (f * (f * (f * f)));
    }
  };
  private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
  private int mActivePointerId = -1;
  private PagerAdapter mAdapter;
  private OnAdapterChangeListener mAdapterChangeListener;
  private int mBottomPageBounds;
  private boolean mCalledSuper;
  private int mChildHeightMeasureSpec;
  private int mChildWidthMeasureSpec;
  private int mCloseEnough;
  private int mCurItem;
  private int mDecorChildCount;
  private int mDefaultGutterSize;
  private int mDrawingOrder;
  private ArrayList<View> mDrawingOrderedChildren;
  private final Runnable mEndScrollRunnable = new Runnable()
  {
    public void run()
    {
      ViewPager.this.setScrollState(0);
      ViewPager.this.populate();
    }
  };
  private int mExpectedAdapterCount;
  private long mFakeDragBeginTime;
  private boolean mFakeDragging;
  private boolean mFirstLayout = true;
  private float mFirstOffset = -3.4028235E38F;
  private int mFlingDistance;
  private int mGutterSize;
  private boolean mIgnoreGutter;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private OnPageChangeListener mInternalPageChangeListener;
  private boolean mIsBeingDragged;
  private boolean mIsUnableToDrag;
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  private float mLastMotionX;
  private float mLastMotionY;
  private float mLastOffset = Float.MAX_VALUE;
  private EdgeEffectCompat mLeftEdge;
  private Drawable mMarginDrawable;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private boolean mNeedCalculatePageOffsets = false;
  private PagerObserver mObserver;
  private int mOffscreenPageLimit = 1;
  private OnPageChangeListener mOnPageChangeListener;
  private int mPageMargin;
  private PageTransformer mPageTransformer;
  private boolean mPopulatePending;
  private Parcelable mRestoredAdapterState = null;
  private ClassLoader mRestoredClassLoader = null;
  private int mRestoredCurItem = -1;
  private EdgeEffectCompat mRightEdge;
  private int mScrollState = 0;
  private Scroller mScroller;
  private boolean mScrollingCacheEnabled;
  private Method mSetChildrenDrawingOrderEnabled;
  private final ItemInfo mTempItem = new ItemInfo();
  private final Rect mTempRect = new Rect();
  private int mTopPageBounds;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16842931;
    LAYOUT_ATTRS = arrayOfInt;
  }
  
  public ViewPager(Context paramContext)
  {
    super(paramContext);
    initViewPager();
  }
  
  public ViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }
  
  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2)
  {
    int i = this.mAdapter.getCount();
    int j = getClientWidth();
    float f1;
    if (j <= 0) {
      f1 = 0.0F;
    } else {
      f1 = this.mPageMargin / f1;
    }
    if (paramItemInfo2 != null)
    {
      int i1 = paramItemInfo2.position;
      ItemInfo localItemInfo1;
      if (i1 >= paramItemInfo1.position)
      {
        if (i1 > paramItemInfo1.position)
        {
          int k = -1 + this.mItems.size();
          float f4 = paramItemInfo2.offset;
          int i4 = i1 - 1;
          if ((i4 >= paramItemInfo1.position) && (k >= 0)) {
            for (localItemInfo1 = (ItemInfo)this.mItems.get(k);; localItemInfo1 = (ItemInfo)this.mItems.get(k))
            {
              if ((i4 >= localItemInfo1.position) || (k <= 0)) {
                for (;;)
                {
                  if (i4 <= localItemInfo1.position)
                  {
                    f4 -= f1 + localItemInfo1.widthFactor;
                    localItemInfo1.offset = f4;
                    i4--;
                    break;
                  }
                  f4 -= f1 + this.mAdapter.getPageWidth(i4);
                  i4--;
                }
              }
              k--;
            }
          }
        }
      }
      else
      {
        n = 0;
        float f2 = f1 + (paramItemInfo2.offset + paramItemInfo2.widthFactor);
        int i2;
        localItemInfo1 += 1;
        if ((i2 <= paramItemInfo1.position) && (n < this.mItems.size())) {
          break label611;
        }
      }
    }
    int m = this.mItems.size();
    float f5 = paramItemInfo1.offset;
    int n = -1 + paramItemInfo1.position;
    float f7;
    if (paramItemInfo1.position != 0) {
      f7 = -3.4028235E38F;
    } else {
      f7 = paramItemInfo1.offset;
    }
    this.mFirstOffset = f7;
    if (paramItemInfo1.position != i - 1) {
      f7 = Float.MAX_VALUE;
    } else {
      f7 = paramItemInfo1.offset + paramItemInfo1.widthFactor - 1.0F;
    }
    this.mLastOffset = f7;
    int i6 = paramInt - 1;
    int i3;
    PagerAdapter localPagerAdapter1;
    if (i6 < 0)
    {
      float f8 = f1 + (paramItemInfo1.offset + paramItemInfo1.widthFactor);
      i6 = 1 + paramItemInfo1.position;
      i3 = paramInt + 1;
      if (i3 >= m)
      {
        this.mNeedCalculatePageOffsets = false;
        return;
      }
      ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(i3);
      for (;;)
      {
        if (i6 >= localItemInfo2.position)
        {
          if (localItemInfo2.position == i - 1) {
            this.mLastOffset = (f8 + localItemInfo2.widthFactor - 1.0F);
          }
          localItemInfo2.offset = f8;
          f8 += f1 + localItemInfo2.widthFactor;
          i3++;
          i6++;
          break;
        }
        localPagerAdapter1 = this.mAdapter;
        int i5 = i6 + 1;
        f8 += f1 + localPagerAdapter1.getPageWidth(i6);
        i6 = i5;
      }
    }
    ItemInfo localItemInfo4 = (ItemInfo)this.mItems.get(i6);
    float f6;
    for (;;)
    {
      if (localPagerAdapter1 <= localItemInfo4.position)
      {
        i3 -= f1 + localItemInfo4.widthFactor;
        localItemInfo4.offset = f6;
        if (localItemInfo4.position == 0) {
          this.mFirstOffset = f6;
        }
        i6--;
        localPagerAdapter1--;
        break;
      }
      PagerAdapter localPagerAdapter3 = this.mAdapter;
      PagerAdapter localPagerAdapter2 = localPagerAdapter1 - 1;
      f6 -= f1 + localPagerAdapter3.getPageWidth(localPagerAdapter1);
      localPagerAdapter1 = localPagerAdapter2;
    }
    label611:
    for (ItemInfo localItemInfo3 = (ItemInfo)this.mItems.get(localPagerAdapter1);; localItemInfo3 = (ItemInfo)this.mItems.get(localPagerAdapter1))
    {
      if ((f6 <= localItemInfo3.position) || (localPagerAdapter1 >= -1 + this.mItems.size())) {
        for (;;)
        {
          float f3;
          if (f6 >= localItemInfo3.position)
          {
            localItemInfo3.offset = m;
            m += f1 + localItemInfo3.widthFactor;
            f6++;
            break;
          }
          f3 += f1 + this.mAdapter.getPageWidth(f6);
          f6++;
        }
      }
      localPagerAdapter1++;
    }
  }
  
  private void completeScroll(boolean paramBoolean)
  {
    int i;
    if (this.mScrollState != 2) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0)
    {
      setScrollingCacheEnabled(false);
      this.mScroller.abortAnimation();
      int n = getScrollX();
      int k = getScrollY();
      int m = this.mScroller.getCurrX();
      j = this.mScroller.getCurrY();
      if ((n != m) || (k != j)) {
        scrollTo(m, j);
      }
    }
    this.mPopulatePending = false;
    for (int j = 0;; j++)
    {
      if (j >= this.mItems.size())
      {
        if (i != 0) {
          if (!paramBoolean) {
            this.mEndScrollRunnable.run();
          } else {
            ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
          }
        }
        return;
      }
      ItemInfo localItemInfo = (ItemInfo)this.mItems.get(j);
      if (localItemInfo.scrolling)
      {
        i = 1;
        localItemInfo.scrolling = false;
      }
    }
  }
  
  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    int i;
    if ((Math.abs(paramInt3) <= this.mFlingDistance) || (Math.abs(paramInt2) <= this.mMinimumVelocity))
    {
      float f;
      if (paramInt1 < this.mCurItem) {
        f = 0.6F;
      } else {
        f = 0.4F;
      }
      i = (int)(f + (paramFloat + paramInt1));
    }
    else if (paramInt2 <= 0)
    {
      i = paramInt1 + 1;
    }
    else
    {
      i = paramInt1;
    }
    if (this.mItems.size() > 0)
    {
      ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(0);
      ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(-1 + this.mItems.size());
      i = Math.max(localItemInfo2.position, Math.min(i, localItemInfo1.position));
    }
    return i;
  }
  
  private void enableLayers(boolean paramBoolean)
  {
    int j = getChildCount();
    for (int k = 0;; k++)
    {
      if (k >= j) {
        return;
      }
      int i;
      if (!paramBoolean) {
        i = 0;
      } else {
        i = 2;
      }
      ViewCompat.setLayerType(getChildAt(k), i, null);
    }
  }
  
  private void endDrag()
  {
    this.mIsBeingDragged = false;
    this.mIsUnableToDrag = false;
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView)
  {
    if (paramRect == null) {
      paramRect = new Rect();
    }
    if (paramView != null)
    {
      paramRect.left = paramView.getLeft();
      paramRect.right = paramView.getRight();
      paramRect.top = paramView.getTop();
      paramRect.bottom = paramView.getBottom();
      for (Object localObject = paramView.getParent(); ((localObject instanceof ViewGroup)) && (localObject != this); localObject = ((ViewGroup)localObject).getParent())
      {
        localObject = (ViewGroup)localObject;
        paramRect.left += ((ViewGroup)localObject).getLeft();
        paramRect.right += ((ViewGroup)localObject).getRight();
        paramRect.top += ((ViewGroup)localObject).getTop();
        paramRect.bottom += ((ViewGroup)localObject).getBottom();
      }
    }
    paramRect.set(0, 0, 0, 0);
    return paramRect;
  }
  
  private int getClientWidth()
  {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }
  
  private ItemInfo infoForCurrentScrollPosition()
  {
    float f2 = 0.0F;
    int i = getClientWidth();
    float f1;
    if (i <= 0) {
      f1 = 0.0F;
    } else {
      f1 = getScrollX() / i;
    }
    if (i > 0) {
      f2 = this.mPageMargin / i;
    }
    int m = -1;
    float f4 = 0.0F;
    float f5 = 0.0F;
    int j = 1;
    ItemInfo localItemInfo2 = null;
    int k = 0;
    while (k < this.mItems.size())
    {
      ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(k);
      if ((j == 0) && (localItemInfo1.position != m + 1))
      {
        localItemInfo1 = this.mTempItem;
        localItemInfo1.offset = (f2 + (f4 + f5));
        localItemInfo1.position = (m + 1);
        localItemInfo1.widthFactor = this.mAdapter.getPageWidth(localItemInfo1.position);
        k--;
      }
      f4 = localItemInfo1.offset;
      float f3 = f2 + (f4 + localItemInfo1.widthFactor);
      if ((j != 0) || (f1 >= f4)) {
        if ((f1 >= f3) && (k != -1 + this.mItems.size()))
        {
          j = 0;
          int n = localItemInfo1.position;
          f4 = f4;
          f5 = localItemInfo1.widthFactor;
          localItemInfo2 = localItemInfo1;
          k++;
        }
        else
        {
          localItemInfo2 = localItemInfo1;
        }
      }
    }
    return localItemInfo2;
  }
  
  private boolean isGutterDrag(float paramFloat1, float paramFloat2)
  {
    boolean bool;
    if (((paramFloat1 >= this.mGutterSize) || (paramFloat2 <= 0.0F)) && ((paramFloat1 <= getWidth() - this.mGutterSize) || (paramFloat2 >= 0.0F))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
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
      this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, i);
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, i);
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.clear();
      }
    }
  }
  
  private boolean pageScrolled(int paramInt)
  {
    int i = 0;
    if (this.mItems.size() != 0)
    {
      ItemInfo localItemInfo = infoForCurrentScrollPosition();
      int k = getClientWidth();
      int j = k + this.mPageMargin;
      float f2 = this.mPageMargin / k;
      i = localItemInfo.position;
      float f1 = (paramInt / k - localItemInfo.offset) / (f2 + localItemInfo.widthFactor);
      j = (int)(f1 * j);
      this.mCalledSuper = false;
      onPageScrolled(i, f1, j);
      if (this.mCalledSuper) {
        i = 1;
      } else {
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
      }
    }
    else
    {
      this.mCalledSuper = false;
      onPageScrolled(0, 0.0F, 0);
      if (!this.mCalledSuper) {
        break label137;
      }
    }
    return i;
    label137:
    throw new IllegalStateException("onPageScrolled did not call superclass implementation");
  }
  
  private boolean performDrag(float paramFloat)
  {
    boolean bool = false;
    float f1 = this.mLastMotionX - paramFloat;
    this.mLastMotionX = paramFloat;
    float f4 = f1 + getScrollX();
    int i = getClientWidth();
    float f2 = i * this.mFirstOffset;
    float f3 = i * this.mLastOffset;
    int j = 1;
    int k = 1;
    ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(0);
    ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(-1 + this.mItems.size());
    if (localItemInfo2.position != 0)
    {
      j = 0;
      f2 = localItemInfo2.offset * i;
    }
    if (localItemInfo1.position != -1 + this.mAdapter.getCount())
    {
      k = 0;
      f3 = localItemInfo1.offset * i;
    }
    if (f4 >= f2)
    {
      if (f4 > f3)
      {
        if (k != 0)
        {
          f2 = f4 - f3;
          bool = this.mRightEdge.onPull(Math.abs(f2) / i);
        }
        f4 = f3;
      }
    }
    else
    {
      if (j != 0)
      {
        f3 = f2 - f4;
        bool = this.mLeftEdge.onPull(Math.abs(f3) / i);
      }
      f4 = f2;
    }
    this.mLastMotionX += f4 - (int)f4;
    scrollTo((int)f4, getScrollY());
    pageScrolled((int)f4);
    return bool;
  }
  
  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    if ((paramInt2 <= 0) || (this.mItems.isEmpty()))
    {
      ItemInfo localItemInfo1 = infoForPosition(this.mCurItem);
      float f;
      if (localItemInfo1 == null) {
        f = 0.0F;
      } else {
        f = Math.min(f.offset, this.mLastOffset);
      }
      i = (int)(f * (paramInt1 - getPaddingLeft() - getPaddingRight()));
      if (i != getScrollX())
      {
        completeScroll(false);
        scrollTo(i, getScrollY());
      }
    }
    else
    {
      i = paramInt3 + (paramInt1 - getPaddingLeft() - getPaddingRight());
      int j = paramInt4 + (paramInt2 - getPaddingLeft() - getPaddingRight());
      int k = (int)(getScrollX() / j * i);
      scrollTo(k, getScrollY());
      if (!this.mScroller.isFinished())
      {
        i = this.mScroller.getDuration() - this.mScroller.timePassed();
        ItemInfo localItemInfo2 = infoForPosition(this.mCurItem);
        this.mScroller.startScroll(k, 0, (int)(localItemInfo2.offset * paramInt1), 0, i);
      }
    }
  }
  
  private void removeNonDecorViews()
  {
    for (int i = 0;; i++)
    {
      if (i >= getChildCount()) {
        return;
      }
      if (!((LayoutParams)getChildAt(i).getLayoutParams()).isDecor)
      {
        removeViewAt(i);
        i--;
      }
    }
  }
  
  private void requestParentDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    ViewParent localViewParent = getParent();
    if (localViewParent != null) {
      localViewParent.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    ItemInfo localItemInfo = infoForPosition(paramInt1);
    int i = 0;
    if (localItemInfo != null) {
      i = (int)(getClientWidth() * Math.max(this.mFirstOffset, Math.min(localItemInfo.offset, this.mLastOffset)));
    }
    if (!paramBoolean1)
    {
      if ((paramBoolean2) && (this.mOnPageChangeListener != null)) {
        this.mOnPageChangeListener.onPageSelected(paramInt1);
      }
      if ((paramBoolean2) && (this.mInternalPageChangeListener != null)) {
        this.mInternalPageChangeListener.onPageSelected(paramInt1);
      }
      completeScroll(false);
      scrollTo(i, 0);
      pageScrolled(i);
    }
    else
    {
      smoothScrollTo(i, 0, paramInt2);
      if ((paramBoolean2) && (this.mOnPageChangeListener != null)) {
        this.mOnPageChangeListener.onPageSelected(paramInt1);
      }
      if ((paramBoolean2) && (this.mInternalPageChangeListener != null)) {
        this.mInternalPageChangeListener.onPageSelected(paramInt1);
      }
    }
  }
  
  private void setScrollState(int paramInt)
  {
    if (this.mScrollState != paramInt)
    {
      this.mScrollState = paramInt;
      if (this.mPageTransformer != null)
      {
        boolean bool;
        if (paramInt == 0) {
          bool = false;
        } else {
          bool = true;
        }
        enableLayers(bool);
      }
      if (this.mOnPageChangeListener != null) {
        this.mOnPageChangeListener.onPageScrollStateChanged(paramInt);
      }
    }
  }
  
  private void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if (this.mScrollingCacheEnabled != paramBoolean) {
      this.mScrollingCacheEnabled = paramBoolean;
    }
  }
  
  private void sortChildDrawingOrder()
  {
    int i;
    if (this.mDrawingOrder != 0)
    {
      if (this.mDrawingOrderedChildren != null) {
        this.mDrawingOrderedChildren.clear();
      } else {
        this.mDrawingOrderedChildren = new ArrayList();
      }
      i = getChildCount();
    }
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        return;
      }
      View localView = getChildAt(j);
      this.mDrawingOrderedChildren.add(localView);
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    int j = paramArrayList.size();
    int k = getDescendantFocusability();
    if (k != 393216) {}
    for (int i = 0;; i++)
    {
      if (i >= getChildCount())
      {
        if (((k != 262144) || (j == paramArrayList.size())) && (isFocusable()) && (((paramInt2 & 0x1) != 1) || (!isInTouchMode()) || (isFocusableInTouchMode())) && (paramArrayList != null)) {
          paramArrayList.add(this);
        }
        return;
      }
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
          localView.addFocusables(paramArrayList, paramInt1, paramInt2);
        }
      }
    }
  }
  
  ItemInfo addNewItem(int paramInt1, int paramInt2)
  {
    ItemInfo localItemInfo = new ItemInfo();
    localItemInfo.position = paramInt1;
    localItemInfo.object = this.mAdapter.instantiateItem(this, paramInt1);
    localItemInfo.widthFactor = this.mAdapter.getPageWidth(paramInt1);
    if ((paramInt2 >= 0) && (paramInt2 < this.mItems.size())) {
      this.mItems.add(paramInt2, localItemInfo);
    } else {
      this.mItems.add(localItemInfo);
    }
    return localItemInfo;
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    for (int i = 0;; i++)
    {
      if (i >= getChildCount()) {
        return;
      }
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
          localView.addTouchables(paramArrayList);
        }
      }
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!checkLayoutParams(paramLayoutParams)) {
      paramLayoutParams = generateLayoutParams(paramLayoutParams);
    }
    LayoutParams localLayoutParams = (LayoutParams)paramLayoutParams;
    localLayoutParams.isDecor |= paramView instanceof Decor;
    if (!this.mInLayout)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
    }
    else
    {
      if ((localLayoutParams != null) && (localLayoutParams.isDecor)) {
        break label80;
      }
      localLayoutParams.needsMeasure = true;
      addViewInLayout(paramView, paramInt, paramLayoutParams);
    }
    return;
    label80:
    throw new IllegalStateException("Cannot add pager decor view during layout");
  }
  
  public boolean arrowScroll(int paramInt)
  {
    Object localObject1 = findFocus();
    if (localObject1 != this)
    {
      if (localObject1 != null)
      {
        bool1 = false;
        localObject2 = ((View)localObject1).getParent();
        while ((localObject2 instanceof ViewGroup)) {
          if (localObject2 != this) {
            localObject2 = ((ViewParent)localObject2).getParent();
          } else {
            bool1 = true;
          }
        }
        if (!bool1)
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append(localObject1.getClass().getSimpleName());
          for (localObject1 = ((View)localObject1).getParent();; localObject1 = ((ViewParent)localObject1).getParent())
          {
            if (!(localObject1 instanceof ViewGroup))
            {
              Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + ((StringBuilder)localObject2).toString());
              localObject1 = null;
              break;
            }
            ((StringBuilder)localObject2).append(" => ").append(localObject1.getClass().getSimpleName());
          }
        }
      }
    }
    else {
      localObject1 = null;
    }
    boolean bool1 = false;
    Object localObject2 = FocusFinder.getInstance().findNextFocus(this, (View)localObject1, paramInt);
    boolean bool3;
    if ((localObject2 == null) || (localObject2 == localObject1))
    {
      if ((paramInt != 17) && (paramInt != 1))
      {
        if ((paramInt == 66) || (paramInt == 2)) {
          bool1 = pageRight();
        }
      }
      else {
        bool1 = pageLeft();
      }
    }
    else
    {
      int k;
      if (paramInt != 17)
      {
        if (paramInt == 66)
        {
          int i = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject2).left;
          k = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject1).left;
          boolean bool2;
          if ((localObject1 == null) || (i > k)) {
            bool2 = ((View)localObject2).requestFocus();
          } else {
            bool2 = pageRight();
          }
        }
      }
      else
      {
        int j = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject2).left;
        k = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject1).left;
        if ((localObject1 == null) || (j < k)) {
          bool3 = ((View)localObject2).requestFocus();
        } else {
          bool3 = pageLeft();
        }
      }
    }
    if (bool3) {
      playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
    }
    return bool3;
  }
  
  public boolean beginFakeDrag()
  {
    int i = 0;
    boolean bool;
    if (!this.mIsBeingDragged)
    {
      this.mFakeDragging = true;
      setScrollState(1);
      this.mLastMotionX = 0.0F;
      this.mInitialMotionX = 0.0F;
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.clear();
      } else {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
      this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      this.mFakeDragBeginTime = l;
      bool = true;
    }
    return bool;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
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
        if ((!paramBoolean) || (!ViewCompat.canScrollHorizontally(paramView, -paramInt1)))
        {
          i = 0;
          break label168;
        }
        i = 1;
        break label168;
      }
      View localView = localViewGroup.getChildAt(k);
      if ((paramInt2 + j >= localView.getLeft()) && (paramInt2 + j < localView.getRight()) && (paramInt3 + i >= localView.getTop()) && (paramInt3 + i < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2 + j - localView.getLeft(), paramInt3 + i - localView.getTop()))) {
        break;
      }
    }
    int i = 1;
    label168:
    return i;
  }
  
  public boolean canScrollHorizontally(int paramInt)
  {
    boolean bool2 = true;
    boolean bool1 = false;
    if (this.mAdapter != null)
    {
      int j = getClientWidth();
      int i = getScrollX();
      if (paramInt >= 0)
      {
        if (paramInt > 0)
        {
          if (i >= (int)(j * this.mLastOffset)) {
            bool2 = false;
          }
          bool1 = bool2;
        }
      }
      else
      {
        if (i <= (int)(j * this.mFirstOffset)) {
          bool2 = false;
        }
        bool1 = bool2;
      }
    }
    return bool1;
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
  
  public void computeScroll()
  {
    if ((this.mScroller.isFinished()) || (!this.mScroller.computeScrollOffset()))
    {
      completeScroll(true);
    }
    else
    {
      int j = getScrollX();
      int k = getScrollY();
      int i = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      if ((j != i) || (k != m))
      {
        scrollTo(i, m);
        if (!pageScrolled(i))
        {
          this.mScroller.abortAnimation();
          scrollTo(0, m);
        }
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  void dataSetChanged()
  {
    int i1 = this.mAdapter.getCount();
    this.mExpectedAdapterCount = i1;
    int m;
    if ((this.mItems.size() >= 1 + 2 * this.mOffscreenPageLimit) || (this.mItems.size() >= i1)) {
      m = 0;
    } else {
      m = 1;
    }
    int i = this.mCurItem;
    int j = 0;
    for (int i2 = 0;; i2++)
    {
      LayoutParams localLayoutParams;
      if (i2 >= this.mItems.size())
      {
        if (j != 0) {
          this.mAdapter.finishUpdate(this);
        }
        Collections.sort(this.mItems, COMPARATOR);
        if (m != 0) {
          n = getChildCount();
        }
        for (m = 0;; m++)
        {
          if (m >= n)
          {
            setCurrentItemInternal(i, false, true);
            requestLayout();
            return;
          }
          localLayoutParams = (LayoutParams)getChildAt(m).getLayoutParams();
          if (!localLayoutParams.isDecor) {
            localLayoutParams.widthFactor = 0.0F;
          }
        }
      }
      ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i2);
      int n = this.mAdapter.getItemPosition(localItemInfo.object);
      if (n != -1) {
        if (n != -2)
        {
          if (localItemInfo.position != n)
          {
            if (localItemInfo.position == this.mCurItem) {
              i = n;
            }
            localItemInfo.position = n;
            m = 1;
          }
        }
        else
        {
          this.mItems.remove(i2);
          i2--;
          if (localLayoutParams == 0)
          {
            this.mAdapter.startUpdate(this);
            int k = 1;
          }
          this.mAdapter.destroyItem(this, localItemInfo.position, localItemInfo.object);
          m = 1;
          if (this.mCurItem == localItemInfo.position)
          {
            i = Math.max(0, Math.min(this.mCurItem, i1 - 1));
            m = 1;
          }
        }
      }
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((!super.dispatchKeyEvent(paramKeyEvent)) && (!executeKeyEvent(paramKeyEvent))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    boolean bool;
    if (paramAccessibilityEvent.getEventType() != 4096)
    {
      int j = getChildCount();
      for (int k = 0;; k++)
      {
        if (k >= j)
        {
          int i = 0;
          return ???;
        }
        View localView = getChildAt(k);
        if (localView.getVisibility() == 0)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent))) {
            break;
          }
        }
      }
      bool = true;
    }
    else
    {
      bool = super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
    }
    return bool;
  }
  
  float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)(0.4712389167638204D * (paramFloat - 0.5F)));
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int i = 0;
    int j = ViewCompat.getOverScrollMode(this);
    boolean bool;
    if ((j != 0) && ((j != 1) || (this.mAdapter == null) || (this.mAdapter.getCount() <= 1)))
    {
      this.mLeftEdge.finish();
      this.mRightEdge.finish();
    }
    else
    {
      int k;
      if (!this.mLeftEdge.isFinished())
      {
        j = paramCanvas.save();
        i = getHeight() - getPaddingTop() - getPaddingBottom();
        k = getWidth();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-i + getPaddingTop(), this.mFirstOffset * k);
        this.mLeftEdge.setSize(i, k);
        bool = false | this.mLeftEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(j);
      }
      if (!this.mRightEdge.isFinished())
      {
        int m = paramCanvas.save();
        j = getWidth();
        k = getHeight() - getPaddingTop() - getPaddingBottom();
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-getPaddingTop(), -(1.0F + this.mLastOffset) * j);
        this.mRightEdge.setSize(k, j);
        bool |= this.mRightEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(m);
      }
    }
    if (bool) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mMarginDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  public void endFakeDrag()
  {
    if (this.mFakeDragging)
    {
      VelocityTracker localVelocityTracker = this.mVelocityTracker;
      localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
      int j = (int)VelocityTrackerCompat.getXVelocity(localVelocityTracker, this.mActivePointerId);
      this.mPopulatePending = true;
      int i = getClientWidth();
      int k = getScrollX();
      ItemInfo localItemInfo = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(localItemInfo.position, (k / i - localItemInfo.offset) / localItemInfo.widthFactor, j, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, j);
      endDrag();
      this.mFakeDragging = false;
      return;
    }
    throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = false;
    if (paramKeyEvent.getAction() == 0) {
      switch (paramKeyEvent.getKeyCode())
      {
      case 21: 
        bool = arrowScroll(17);
        break;
      case 22: 
        bool = arrowScroll(66);
        break;
      case 61: 
        if (Build.VERSION.SDK_INT >= 11) {
          if (!KeyEventCompat.hasNoModifiers(paramKeyEvent))
          {
            if (KeyEventCompat.hasModifiers(paramKeyEvent, 1)) {
              bool = arrowScroll(1);
            }
          }
          else {
            bool = arrowScroll(2);
          }
        }
        break;
      }
    }
    return bool;
  }
  
  public void fakeDragBy(float paramFloat)
  {
    if (this.mFakeDragging)
    {
      this.mLastMotionX = (paramFloat + this.mLastMotionX);
      float f2 = getScrollX() - paramFloat;
      int i = getClientWidth();
      float f1 = i * this.mFirstOffset;
      float f3 = i * this.mLastOffset;
      ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
      ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(-1 + this.mItems.size());
      if (localItemInfo1.position != 0) {
        f1 = localItemInfo1.offset * i;
      }
      if (localItemInfo2.position != -1 + this.mAdapter.getCount()) {
        f3 = localItemInfo2.offset * i;
      }
      if (f2 >= f1)
      {
        if (f2 > f3) {
          f2 = f3;
        }
      }
      else {
        f2 = f1;
      }
      this.mLastMotionX += f2 - (int)f2;
      scrollTo((int)f2, getScrollY());
      pageScrolled((int)f2);
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(this.mFakeDragBeginTime, l, 2, this.mLastMotionX, 0.0F, 0);
      this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      return;
    }
    throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
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
    return generateDefaultLayoutParams();
  }
  
  public PagerAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    int i;
    if (this.mDrawingOrder != 2) {
      i = paramInt2;
    } else {
      i = paramInt1 - 1 - paramInt2;
    }
    return ((LayoutParams)((View)this.mDrawingOrderedChildren.get(i)).getLayoutParams()).childIndex;
  }
  
  public int getCurrentItem()
  {
    return this.mCurItem;
  }
  
  public int getOffscreenPageLimit()
  {
    return this.mOffscreenPageLimit;
  }
  
  public int getPageMargin()
  {
    return this.mPageMargin;
  }
  
  ItemInfo infoForAnyChild(View paramView)
  {
    for (;;)
    {
      localObject = paramView.getParent();
      if (localObject == this) {
        return infoForChild(paramView);
      }
      if ((localObject == null) || (!(localObject instanceof View))) {
        break;
      }
      paramView = (View)localObject;
    }
    Object localObject = null;
    return (ItemInfo)localObject;
  }
  
  ItemInfo infoForChild(View paramView)
  {
    ItemInfo localItemInfo;
    for (int i = 0;; i++)
    {
      if (i >= this.mItems.size())
      {
        localItemInfo = null;
        break;
      }
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (this.mAdapter.isViewFromObject(paramView, localItemInfo.object)) {
        break;
      }
    }
    return localItemInfo;
  }
  
  ItemInfo infoForPosition(int paramInt)
  {
    ItemInfo localItemInfo;
    for (int i = 0;; i++)
    {
      if (i >= this.mItems.size())
      {
        localItemInfo = null;
        break;
      }
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (localItemInfo.position == paramInt) {
        break;
      }
    }
    return localItemInfo;
  }
  
  void initViewPager()
  {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context localContext = getContext();
    this.mScroller = new Scroller(localContext, sInterpolator);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(localContext);
    float f = localContext.getResources().getDisplayMetrics().density;
    this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(localViewConfiguration);
    this.mMinimumVelocity = ((int)(400.0F * f));
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mLeftEdge = new EdgeEffectCompat(localContext);
    this.mRightEdge = new EdgeEffectCompat(localContext);
    this.mFlingDistance = ((int)(25.0F * f));
    this.mCloseEnough = ((int)(2.0F * f));
    this.mDefaultGutterSize = ((int)(16.0F * f));
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) == 0) {
      ViewCompat.setImportantForAccessibility(this, 1);
    }
  }
  
  public boolean isFakeDragging()
  {
    return this.mFakeDragging;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mEndScrollRunnable);
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i;
    int k;
    float f1;
    int j;
    Object localObject;
    float f2;
    int n;
    int i1;
    if ((this.mPageMargin > 0) && (this.mMarginDrawable != null) && (this.mItems.size() > 0) && (this.mAdapter != null))
    {
      i = getScrollX();
      k = getWidth();
      f1 = this.mPageMargin / k;
      j = 0;
      localObject = (ItemInfo)this.mItems.get(0);
      f2 = ((ItemInfo)localObject).offset;
      n = this.mItems.size();
      i1 = ((ItemInfo)localObject).position;
      int m = ((ItemInfo)this.mItems.get(n - 1)).position;
      i1 = i1;
      if (i1 >= m) {}
    }
    for (;;)
    {
      if ((i1 <= ((ItemInfo)localObject).position) || (j >= n))
      {
        float f4;
        if (i1 != ((ItemInfo)localObject).position)
        {
          float f3 = this.mAdapter.getPageWidth(i1);
          f4 = (f2 + f3) * k;
          f2 += f3 + f1;
        }
        else
        {
          f4 = (((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor) * k;
          f2 = f1 + (((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor);
        }
        if (f4 + this.mPageMargin > i)
        {
          this.mMarginDrawable.setBounds((int)f4, this.mTopPageBounds, (int)(0.5F + (f4 + this.mPageMargin)), this.mBottomPageBounds);
          this.mMarginDrawable.draw(paramCanvas);
        }
        if (f4 <= i + k)
        {
          i1++;
          break;
        }
        return;
      }
      localObject = this.mItems;
      j++;
      localObject = (ItemInfo)((ArrayList)localObject).get(j);
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 0xFF & paramMotionEvent.getAction();
    boolean bool;
    if ((i != 3) && (i != 1))
    {
      if (i != 0)
      {
        if (this.mIsBeingDragged) {
          break label488;
        }
        if (this.mIsUnableToDrag) {}
      }
      else
      {
        switch (i)
        {
        case 0: 
          float f1 = paramMotionEvent.getX();
          this.mInitialMotionX = f1;
          this.mLastMotionX = f1;
          f1 = paramMotionEvent.getY();
          this.mInitialMotionY = f1;
          this.mLastMotionY = f1;
          this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
          this.mIsUnableToDrag = false;
          this.mScroller.computeScrollOffset();
          if ((this.mScrollState != 2) || (Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) <= this.mCloseEnough))
          {
            completeScroll(false);
            this.mIsBeingDragged = false;
          }
          else
          {
            this.mScroller.abortAnimation();
            this.mPopulatePending = false;
            populate();
            this.mIsBeingDragged = true;
            requestParentDisallowInterceptTouchEvent(true);
            setScrollState(1);
          }
          break;
        case 2: 
          int j = this.mActivePointerId;
          if (j != -1)
          {
            int k = MotionEventCompat.findPointerIndex(paramMotionEvent, j);
            float f2 = MotionEventCompat.getX(paramMotionEvent, k);
            float f5 = f2 - this.mLastMotionX;
            float f3 = Math.abs(f5);
            float f4 = MotionEventCompat.getY(paramMotionEvent, k);
            float f6 = Math.abs(f4 - this.mInitialMotionY);
            if ((f5 == 0.0F) || (isGutterDrag(this.mLastMotionX, f5)) || (!canScroll(this, false, (int)f5, (int)f2, (int)f4)))
            {
              if ((f3 <= this.mTouchSlop) || (0.5F * f3 <= f6))
              {
                if (f6 > this.mTouchSlop) {
                  this.mIsUnableToDrag = true;
                }
              }
              else
              {
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                setScrollState(1);
                if (f5 <= 0.0F) {
                  f3 = this.mInitialMotionX - this.mTouchSlop;
                } else {
                  f3 = this.mInitialMotionX + this.mTouchSlop;
                }
                this.mLastMotionX = f3;
                this.mLastMotionY = f4;
                setScrollingCacheEnabled(true);
              }
              if ((this.mIsBeingDragged) && (performDrag(f2))) {
                ViewCompat.postInvalidateOnAnimation(this);
              }
            }
            else
            {
              this.mLastMotionX = f2;
              this.mLastMotionY = f4;
              this.mIsUnableToDrag = true;
              bool = false;
            }
          }
          break;
        case 6: 
          onSecondaryPointerUp(paramMotionEvent);
        }
        if (this.mVelocityTracker == null) {
          this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(paramMotionEvent);
        return this.mIsBeingDragged;
      }
      return false;
      label488:
      bool = true;
    }
    else
    {
      this.mIsBeingDragged = false;
      this.mIsUnableToDrag = false;
      this.mActivePointerId = -1;
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
      bool = false;
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    LayoutParams localLayoutParams2 = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    View localView1 = getPaddingLeft();
    int n = getPaddingTop();
    int i2 = getPaddingRight();
    int m = getPaddingBottom();
    int i1 = getScrollX();
    int k = 0;
    for (int i4 = 0;; i4++)
    {
      View localView2;
      LayoutParams localLayoutParams1;
      View localView3;
      if (i4 >= i)
      {
        localLayoutParams2 = localLayoutParams2 - localView1 - i2;
        for (i4 = 0;; i4++)
        {
          if (i4 >= i)
          {
            this.mTopPageBounds = n;
            this.mBottomPageBounds = (j - m);
            this.mDecorChildCount = k;
            if (this.mFirstLayout) {
              scrollToItem(this.mCurItem, false, 0, false);
            }
            this.mFirstLayout = false;
            return;
          }
          localView2 = getChildAt(i4);
          if (localView2.getVisibility() != 8)
          {
            localLayoutParams1 = (LayoutParams)localView2.getLayoutParams();
            if (!localLayoutParams1.isDecor)
            {
              localObject = infoForChild(localView2);
              if (localObject != null)
              {
                localView3 = localView1 + (int)(localLayoutParams2 * ((ItemInfo)localObject).offset);
                localObject = n;
                if (localLayoutParams1.needsMeasure)
                {
                  localLayoutParams1.needsMeasure = false;
                  localView2.measure(View.MeasureSpec.makeMeasureSpec((int)(localLayoutParams2 * localLayoutParams1.widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec(j - n - m, 1073741824));
                }
                localView2.layout(localView3, localObject, localView3 + localView2.getMeasuredWidth(), localObject + localView2.getMeasuredHeight());
              }
            }
          }
        }
      }
      Object localObject = getChildAt(i4);
      if (((View)localObject).getVisibility() != 8)
      {
        LayoutParams localLayoutParams3 = (LayoutParams)((View)localObject).getLayoutParams();
        if (localLayoutParams3.isDecor)
        {
          localView3 = 0x7 & localLayoutParams3.gravity;
          int i6 = 0x70 & localLayoutParams3.gravity;
          switch (localView3)
          {
          case 2: 
          case 4: 
          default: 
            localView3 = localView1;
            break;
          case 1: 
            localView3 = Math.max((localLayoutParams2 - ((View)localObject).getMeasuredWidth()) / 2, localView1);
            break;
          case 3: 
            localView3 = localView1;
            localView1 += ((View)localObject).getMeasuredWidth();
            break;
          case 5: 
            localView3 = localLayoutParams2 - localLayoutParams1 - ((View)localObject).getMeasuredWidth();
            int i3;
            localLayoutParams1 += ((View)localObject).getMeasuredWidth();
          }
          switch (i6)
          {
          default: 
            i6 = n;
            break;
          case 16: 
            i6 = Math.max((j - ((View)localObject).getMeasuredHeight()) / 2, n);
            break;
          case 48: 
            i6 = n;
            n += ((View)localObject).getMeasuredHeight();
            break;
          case 80: 
            i6 = j - m - ((View)localObject).getMeasuredHeight();
            m += ((View)localObject).getMeasuredHeight();
          }
          int i5;
          localView3 += localView2;
          ((View)localObject).layout(i5, i6, i5 + ((View)localObject).getMeasuredWidth(), i6 + ((View)localObject).getMeasuredHeight());
          k++;
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
    int i = getMeasuredWidth();
    this.mGutterSize = Math.min(i / 10, this.mDefaultGutterSize);
    i = i - getPaddingLeft() - getPaddingRight();
    int m = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    int i2 = getChildCount();
    LayoutParams localLayoutParams2;
    for (int i1 = 0;; localLayoutParams2++)
    {
      int k;
      View localView1;
      if (i1 >= i2)
      {
        this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(m, 1073741824);
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int j = getChildCount();
        for (k = 0;; k++)
        {
          if (k >= j) {
            return;
          }
          localView1 = getChildAt(k);
          if (localView1.getVisibility() != 8)
          {
            localLayoutParams2 = (LayoutParams)localView1.getLayoutParams();
            if ((localLayoutParams2 == null) || (!localLayoutParams2.isDecor)) {
              localView1.measure(View.MeasureSpec.makeMeasureSpec((int)(i * localLayoutParams2.widthFactor), 1073741824), this.mChildHeightMeasureSpec);
            }
          }
        }
      }
      View localView2 = getChildAt(localLayoutParams2);
      if (localView2.getVisibility() != 8)
      {
        LayoutParams localLayoutParams1 = (LayoutParams)localView2.getLayoutParams();
        if ((localLayoutParams1 != null) && (localLayoutParams1.isDecor))
        {
          int i5 = 0x7 & localLayoutParams1.gravity;
          int i4 = 0x70 & localLayoutParams1.gravity;
          k = Integer.MIN_VALUE;
          int i3 = Integer.MIN_VALUE;
          if ((i4 != 48) && (i4 != 80)) {
            i4 = 0;
          } else {
            i4 = 1;
          }
          int i8;
          if ((i5 != 3) && (i5 != 5)) {
            i8 = 0;
          } else {
            i8 = 1;
          }
          if (i4 == 0)
          {
            if (i8 != 0) {
              i3 = 1073741824;
            }
          }
          else {
            k = 1073741824;
          }
          i5 = i;
          int i6 = localView1;
          if (localLayoutParams1.width != -2)
          {
            k = 1073741824;
            if (localLayoutParams1.width != -1) {
              i5 = localLayoutParams1.width;
            }
          }
          int i7;
          if (localLayoutParams1.height != -2)
          {
            i3 = 1073741824;
            if (localLayoutParams1.height != -1) {
              i7 = localLayoutParams1.height;
            }
          }
          localView2.measure(View.MeasureSpec.makeMeasureSpec(i5, k), View.MeasureSpec.makeMeasureSpec(i7, i3));
          if (i4 == 0)
          {
            if (i8 != 0) {
              i -= localView2.getMeasuredWidth();
            }
          }
          else
          {
            int n;
            localView1 -= localView2.getMeasuredHeight();
          }
        }
      }
    }
  }
  
  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    int n;
    LayoutParams localLayoutParams1;
    int m;
    int j;
    int k;
    if (this.mDecorChildCount > 0)
    {
      n = getScrollX();
      localLayoutParams1 = getPaddingLeft();
      m = getPaddingRight();
      j = getWidth();
      k = getChildCount();
    }
    for (int i = 0;; i++)
    {
      if (i >= k)
      {
        if (this.mOnPageChangeListener != null) {
          this.mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
        }
        if (this.mInternalPageChangeListener != null) {
          this.mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
        }
        if (this.mPageTransformer != null)
        {
          i = getScrollX();
          j = getChildCount();
        }
        for (localLayoutParams1 = 0;; localLayoutParams1++)
        {
          if (localLayoutParams1 >= j)
          {
            this.mCalledSuper = true;
            return;
          }
          View localView1 = getChildAt(localLayoutParams1);
          if (!((LayoutParams)localView1.getLayoutParams()).isDecor)
          {
            float f = (localView1.getLeft() - i) / getClientWidth();
            this.mPageTransformer.transformPage(localView1, f);
          }
        }
      }
      View localView2 = getChildAt(i);
      LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
      if (localLayoutParams2.isDecor)
      {
        switch (0x7 & localLayoutParams2.gravity)
        {
        case 2: 
        case 4: 
        default: 
          localLayoutParams2 = localLayoutParams1;
          break;
        case 1: 
          i1 = Math.max((j - localView2.getMeasuredWidth()) / 2, localLayoutParams1);
          break;
        case 3: 
          i1 = localLayoutParams1;
          localLayoutParams1 += localView2.getWidth();
          break;
        case 5: 
          i1 = j - m - localView2.getMeasuredWidth();
          m += localView2.getMeasuredWidth();
        }
        int i1 = i1 + n - localView2.getLeft();
        if (i1 != 0) {
          localView2.offsetLeftAndRight(i1);
        }
      }
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int j = getChildCount();
    int k;
    if ((paramInt & 0x2) == 0)
    {
      k = j - 1;
      i = -1;
      j = -1;
    }
    else
    {
      k = 0;
      i = 1;
      j = j;
    }
    int m = k;
    for (;;)
    {
      if (m == j) {
        return 0;
      }
      View localView = getChildAt(m);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem) && (localView.requestFocus(paramInt, paramRect))) {
          break;
        }
      }
      int n;
      m += i;
    }
    int i = 1;
    return i;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      SavedState localSavedState = (SavedState)paramParcelable;
      super.onRestoreInstanceState(localSavedState.getSuperState());
      if (this.mAdapter == null)
      {
        this.mRestoredCurItem = localSavedState.position;
        this.mRestoredAdapterState = localSavedState.adapterState;
        this.mRestoredClassLoader = localSavedState.loader;
      }
      else
      {
        this.mAdapter.restoreState(localSavedState.adapterState, localSavedState.loader);
        setCurrentItemInternal(localSavedState.position, false, true);
      }
    }
    else
    {
      super.onRestoreInstanceState(paramParcelable);
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.position = this.mCurItem;
    if (this.mAdapter != null) {
      localSavedState.adapterState = this.mAdapter.saveState();
    }
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      recomputeScrollPosition(paramInt1, paramInt3, this.mPageMargin, this.mPageMargin);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (!this.mFakeDragging)
    {
      if ((paramMotionEvent.getAction() != 0) || (paramMotionEvent.getEdgeFlags() == 0))
      {
        if ((this.mAdapter != null) && (this.mAdapter.getCount() != 0))
        {
          if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
          }
          this.mVelocityTracker.addMovement(paramMotionEvent);
          int k = paramMotionEvent.getAction();
          int i = 0;
          switch (k & 0xFF)
          {
          case 0: 
            this.mScroller.abortAnimation();
            this.mPopulatePending = false;
            populate();
            float f1 = paramMotionEvent.getX();
            this.mInitialMotionX = f1;
            this.mLastMotionX = f1;
            f1 = paramMotionEvent.getY();
            this.mInitialMotionY = f1;
            this.mLastMotionY = f1;
            this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
            break;
          case 1: 
            if (this.mIsBeingDragged)
            {
              VelocityTracker localVelocityTracker = this.mVelocityTracker;
              localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
              int i2 = (int)VelocityTrackerCompat.getXVelocity(localVelocityTracker, this.mActivePointerId);
              this.mPopulatePending = true;
              int j = getClientWidth();
              int m = getScrollX();
              ItemInfo localItemInfo = infoForCurrentScrollPosition();
              setCurrentItemInternal(determineTargetPage(localItemInfo.position, (m / j - localItemInfo.offset) / localItemInfo.widthFactor, i2, (int)(MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)) - this.mInitialMotionX)), true, true, i2);
              this.mActivePointerId = -1;
              endDrag();
              bool = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
            }
            break;
          case 2: 
            if (!this.mIsBeingDragged)
            {
              int i1 = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
              float f4 = MotionEventCompat.getX(paramMotionEvent, i1);
              float f2 = Math.abs(f4 - this.mLastMotionX);
              float f3 = MotionEventCompat.getY(paramMotionEvent, i1);
              float f5 = Math.abs(f3 - this.mLastMotionY);
              if ((f2 > this.mTouchSlop) && (f2 > f5))
              {
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                if (f4 - this.mInitialMotionX <= 0.0F) {
                  f2 = this.mInitialMotionX - this.mTouchSlop;
                } else {
                  f2 = this.mInitialMotionX + this.mTouchSlop;
                }
                this.mLastMotionX = f2;
                this.mLastMotionY = f3;
                setScrollState(1);
                setScrollingCacheEnabled(true);
                ViewParent localViewParent = getParent();
                if (localViewParent != null) {
                  localViewParent.requestDisallowInterceptTouchEvent(true);
                }
              }
            }
            if (this.mIsBeingDragged) {
              bool = false | performDrag(MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)));
            }
            break;
          case 3: 
            if (this.mIsBeingDragged)
            {
              scrollToItem(this.mCurItem, true, 0, false);
              this.mActivePointerId = -1;
              endDrag();
              bool = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
            }
            break;
          case 5: 
            int n = MotionEventCompat.getActionIndex(paramMotionEvent);
            this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, n);
            this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, n);
            break;
          case 6: 
            onSecondaryPointerUp(paramMotionEvent);
            this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId));
          }
          if (bool) {
            ViewCompat.postInvalidateOnAnimation(this);
          }
          bool = true;
        }
        else
        {
          bool = false;
        }
      }
      else {
        bool = false;
      }
    }
    else {
      bool = true;
    }
    return bool;
  }
  
  boolean pageLeft()
  {
    boolean bool = true;
    if (this.mCurItem <= 0) {
      bool = false;
    } else {
      setCurrentItem(-1 + this.mCurItem, bool);
    }
    return bool;
  }
  
  boolean pageRight()
  {
    boolean bool = true;
    if ((this.mAdapter == null) || (this.mCurItem >= -1 + this.mAdapter.getCount())) {
      bool = false;
    } else {
      setCurrentItem(1 + this.mCurItem, bool);
    }
    return bool;
  }
  
  void populate()
  {
    populate(this.mCurItem);
  }
  
  void populate(int paramInt)
  {
    Object localObject5 = null;
    int i = 2;
    if (this.mCurItem != paramInt)
    {
      if (this.mCurItem < paramInt)
      {
        i = 66;
        localObject5 = infoForPosition(this.mCurItem);
        this.mCurItem = paramInt;
      }
    }
    else
    {
      if (this.mAdapter != null) {
        break label57;
      }
      sortChildDrawingOrder();
    }
    label57:
    label357:
    label372:
    label458:
    label467:
    label644:
    label656:
    label680:
    label766:
    label772:
    label885:
    label891:
    label909:
    label1000:
    label1006:
    label1139:
    label1250:
    for (;;)
    {
      return;
      i = 17;
      break;
      if (this.mPopulatePending)
      {
        sortChildDrawingOrder();
      }
      else if (getWindowToken() != null)
      {
        this.mAdapter.startUpdate(this);
        int m = this.mOffscreenPageLimit;
        int i6 = Math.max(0, this.mCurItem - m);
        int j = this.mAdapter.getCount();
        int i4 = Math.min(j - 1, m + this.mCurItem);
        String str;
        if (j != this.mExpectedAdapterCount)
        {
          try
          {
            str = getResources().getResourceName(getId());
            str = str;
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
            for (;;)
            {
              str = Integer.toHexString(getId());
            }
          }
          throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + j + " Pager id: " + str + " Pager class: " + getClass() + " Problematic adapter: " + this.mAdapter.getClass());
        }
        Object localObject3 = null;
        int i3 = 0;
        if (i3 < this.mItems.size())
        {
          ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(i3);
          if (localItemInfo2.position < this.mCurItem) {
            break label644;
          }
          if (localItemInfo2.position == this.mCurItem) {
            localObject3 = localItemInfo2;
          }
        }
        if ((localObject3 == null) && (j > 0)) {
          localObject3 = addNewItem(this.mCurItem, i3);
        }
        int i7;
        Object localObject8;
        float f5;
        int i8;
        float f2;
        ItemInfo localItemInfo3;
        float f1;
        if (localObject3 != null)
        {
          float f3 = 0.0F;
          i7 = i3 - 1;
          if (i7 >= 0)
          {
            localObject8 = (ItemInfo)this.mItems.get(i7);
            int i5 = getClientWidth();
            if (i5 > 0) {
              break label656;
            }
            f5 = 0.0F;
            i8 = -1 + this.mCurItem;
            if (i8 >= 0)
            {
              if ((f3 < f5) || (i8 >= i6)) {
                break label772;
              }
              if (localObject8 != null) {
                break label680;
              }
            }
            f2 = ((ItemInfo)localObject3).widthFactor;
            i7 = i3 + 1;
            if (f2 < 2.0F)
            {
              i8 = this.mItems.size();
              if (i7 >= i8) {
                break label885;
              }
              localItemInfo3 = (ItemInfo)this.mItems.get(i7);
              if (i5 > 0) {
                break label891;
              }
              f1 = 0.0F;
              i8 = 1 + this.mCurItem;
              if (i8 < j)
              {
                if ((f2 < f1) || (i8 <= i4)) {
                  break label1006;
                }
                if (localItemInfo3 != null) {
                  break label909;
                }
              }
            }
            calculatePageOffsets((ItemInfo)localObject3, i3, (ItemInfo)localObject5);
          }
        }
        else
        {
          localObject5 = this.mAdapter;
          j = this.mCurItem;
          if (localObject3 == null) {
            break label1139;
          }
        }
        Object localObject1;
        Object localObject4;
        for (localObject3 = ((ItemInfo)localObject3).object;; localObject4 = null)
        {
          ((PagerAdapter)localObject5).setPrimaryItem(this, j, localObject3);
          this.mAdapter.finishUpdate(this);
          int i2 = getChildCount();
          Object localObject6;
          for (int n = 0; n < i2; n++)
          {
            localObject6 = getChildAt(n);
            localObject1 = (LayoutParams)((View)localObject6).getLayoutParams();
            ((LayoutParams)localObject1).childIndex = n;
            if ((!((LayoutParams)localObject1).isDecor) && (((LayoutParams)localObject1).widthFactor == 0.0F))
            {
              localObject6 = infoForChild((View)localObject6);
              if (localObject6 != null)
              {
                ((LayoutParams)localObject1).widthFactor = ((ItemInfo)localObject6).widthFactor;
                ((LayoutParams)localObject1).position = ((ItemInfo)localObject6).position;
              }
            }
          }
          localObject6++;
          break;
          localObject8 = null;
          break label357;
          f5 = 2.0F - n.widthFactor + getPaddingLeft() / f1;
          break label372;
          int i15 = ((ItemInfo)localObject8).position;
          if ((i8 == i15) && (!((ItemInfo)localObject8).scrolling))
          {
            this.mItems.remove(i7);
            PagerAdapter localPagerAdapter2 = this.mAdapter;
            localObject8 = ((ItemInfo)localObject8).object;
            localPagerAdapter2.destroyItem(this, i8, localObject8);
            i7--;
            localObject6--;
            if (i7 < 0) {
              break label766;
            }
          }
          for (localObject8 = (ItemInfo)this.mItems.get(i7);; localObject8 = null)
          {
            i8--;
            break;
          }
          float f4;
          if (localObject8 != null)
          {
            int i16 = ((ItemInfo)localObject8).position;
            if (i8 == i16)
            {
              localItemInfo3 += ((ItemInfo)localObject8).widthFactor;
              i7--;
              if (i7 >= 0) {}
              for (localObject8 = (ItemInfo)this.mItems.get(i7);; localObject8 = null) {
                break;
              }
            }
          }
          int i14 = i7 + 1;
          f4 += addNewItem(i8, i14).widthFactor;
          localObject6++;
          if (i7 >= 0) {}
          for (ItemInfo localItemInfo7 = (ItemInfo)this.mItems.get(i7);; localItemInfo7 = null) {
            break;
          }
          Object localObject7 = null;
          break label458;
          f1 = 2.0F + getPaddingRight() / f1;
          break label467;
          int i12 = ((ItemInfo)localObject7).position;
          if ((i8 == i12) && (!((ItemInfo)localObject7).scrolling))
          {
            this.mItems.remove(i7);
            PagerAdapter localPagerAdapter1 = this.mAdapter;
            localObject7 = ((ItemInfo)localObject7).object;
            localPagerAdapter1.destroyItem(this, i8, localObject7);
            int i9 = this.mItems.size();
            if (i7 >= i9) {
              break label1000;
            }
          }
          for (ItemInfo localItemInfo4 = (ItemInfo)this.mItems.get(i7);; localItemInfo4 = null)
          {
            i8++;
            break;
          }
          if (localItemInfo4 != null)
          {
            int i13 = localItemInfo4.position;
            if (i8 == i13)
            {
              f2 += localItemInfo4.widthFactor;
              i7++;
              int i10 = this.mItems.size();
              if (i7 < i10) {}
              for (localItemInfo5 = (ItemInfo)this.mItems.get(i7);; localItemInfo5 = null) {
                break;
              }
            }
          }
          ItemInfo localItemInfo5 = addNewItem(i8, i7);
          i7++;
          f2 += localItemInfo5.widthFactor;
          int i11 = this.mItems.size();
          if (i7 < i11) {}
          for (ItemInfo localItemInfo6 = (ItemInfo)this.mItems.get(i7);; localItemInfo6 = null) {
            break;
          }
        }
        sortChildDrawingOrder();
        if (hasFocus())
        {
          localObject1 = findFocus();
          if (localObject1 != null) {}
          Object localObject2;
          for (localObject1 = infoForAnyChild((View)localObject1);; localObject2 = null)
          {
            if ((localObject1 != null) && (((ItemInfo)localObject1).position == this.mCurItem)) {
              break label1250;
            }
            for (int k = 0;; k++)
            {
              int i1 = getChildCount();
              if (k >= i1) {
                break;
              }
              View localView = getChildAt(k);
              ItemInfo localItemInfo1 = infoForChild(localView);
              if ((localItemInfo1 != null) && (localItemInfo1.position == this.mCurItem) && (localView.requestFocus(str))) {
                break;
              }
            }
          }
        }
      }
    }
  }
  
  public void removeView(View paramView)
  {
    if (!this.mInLayout) {
      super.removeView(paramView);
    } else {
      removeViewInLayout(paramView);
    }
  }
  
  public void setAdapter(PagerAdapter paramPagerAdapter)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterDataSetObserver(this.mObserver);
      this.mAdapter.startUpdate(this);
    }
    PagerAdapter localPagerAdapter;
    for (int i = 0;; localPagerAdapter++)
    {
      if (i >= this.mItems.size())
      {
        this.mAdapter.finishUpdate(this);
        this.mItems.clear();
        removeNonDecorViews();
        this.mCurItem = 0;
        scrollTo(0, 0);
        localPagerAdapter = this.mAdapter;
        this.mAdapter = paramPagerAdapter;
        this.mExpectedAdapterCount = 0;
        if (this.mAdapter != null)
        {
          if (this.mObserver == null) {
            this.mObserver = new PagerObserver(null);
          }
          this.mAdapter.registerDataSetObserver(this.mObserver);
          this.mPopulatePending = false;
          boolean bool = this.mFirstLayout;
          this.mFirstLayout = true;
          this.mExpectedAdapterCount = this.mAdapter.getCount();
          if (this.mRestoredCurItem < 0)
          {
            if (bool) {
              requestLayout();
            } else {
              populate();
            }
          }
          else
          {
            this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
            setCurrentItemInternal(this.mRestoredCurItem, false, true);
            this.mRestoredCurItem = -1;
            this.mRestoredAdapterState = null;
            this.mRestoredClassLoader = null;
          }
        }
        if ((this.mAdapterChangeListener != null) && (localPagerAdapter != paramPagerAdapter)) {
          this.mAdapterChangeListener.onAdapterChanged(localPagerAdapter, paramPagerAdapter);
        }
        return;
      }
      ItemInfo localItemInfo = (ItemInfo)this.mItems.get(localPagerAdapter);
      this.mAdapter.destroyItem(this, localItemInfo.position, localItemInfo.object);
    }
  }
  
  void setChildrenDrawingOrderEnabledCompat(boolean paramBoolean)
  {
    if ((Build.VERSION.SDK_INT < 7) || (this.mSetChildrenDrawingOrderEnabled == null)) {}
    try
    {
      localObject = new Class[1];
      localObject[0] = Boolean.TYPE;
      this.mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", (Class[])localObject);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      try
      {
        for (;;)
        {
          Method localMethod = this.mSetChildrenDrawingOrderEnabled;
          Object localObject = new Object[1];
          localObject[0] = Boolean.valueOf(paramBoolean);
          localMethod.invoke(this, (Object[])localObject);
          return;
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("ViewPager", "Can't find setChildrenDrawingOrderEnabled", localNoSuchMethodException);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("ViewPager", "Error changing children drawing order", localException);
        }
      }
    }
  }
  
  public void setCurrentItem(int paramInt)
  {
    this.mPopulatePending = false;
    boolean bool;
    if (this.mFirstLayout) {
      bool = false;
    } else {
      bool = true;
    }
    setCurrentItemInternal(paramInt, bool, false);
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }
  
  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
  }
  
  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    boolean bool = true;
    if ((this.mAdapter != null) && (this.mAdapter.getCount() > 0))
    {
      if ((paramBoolean2) || (this.mCurItem != paramInt1) || (this.mItems.size() == 0))
      {
        if (paramInt1 >= 0)
        {
          if (paramInt1 >= this.mAdapter.getCount()) {
            paramInt1 = -1 + this.mAdapter.getCount();
          }
        }
        else {
          paramInt1 = 0;
        }
        int i = this.mOffscreenPageLimit;
        if ((paramInt1 > i + this.mCurItem) || (paramInt1 < this.mCurItem - i)) {}
        for (i = 0;; i++)
        {
          if (i >= this.mItems.size())
          {
            if (this.mCurItem == paramInt1) {
              bool = false;
            }
            if (!this.mFirstLayout)
            {
              populate(paramInt1);
              scrollToItem(paramInt1, paramBoolean1, paramInt2, bool);
              break;
            }
            this.mCurItem = paramInt1;
            if ((bool) && (this.mOnPageChangeListener != null)) {
              this.mOnPageChangeListener.onPageSelected(paramInt1);
            }
            if ((bool) && (this.mInternalPageChangeListener != null)) {
              this.mInternalPageChangeListener.onPageSelected(paramInt1);
            }
            requestLayout();
            break;
          }
          ((ItemInfo)this.mItems.get(i)).scrolling = bool;
        }
      }
      setScrollingCacheEnabled(false);
    }
    else
    {
      setScrollingCacheEnabled(false);
    }
  }
  
  OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    OnPageChangeListener localOnPageChangeListener = this.mInternalPageChangeListener;
    this.mInternalPageChangeListener = paramOnPageChangeListener;
    return localOnPageChangeListener;
  }
  
  public void setOffscreenPageLimit(int paramInt)
  {
    if (paramInt < 1)
    {
      Log.w("ViewPager", "Requested offscreen page limit " + paramInt + " too small; defaulting to " + 1);
      paramInt = 1;
    }
    if (paramInt != this.mOffscreenPageLimit)
    {
      this.mOffscreenPageLimit = paramInt;
      populate();
    }
  }
  
  void setOnAdapterChangeListener(OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    this.mAdapterChangeListener = paramOnAdapterChangeListener;
  }
  
  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    this.mOnPageChangeListener = paramOnPageChangeListener;
  }
  
  public void setPageMargin(int paramInt)
  {
    int i = this.mPageMargin;
    this.mPageMargin = paramInt;
    int j = getWidth();
    recomputeScrollPosition(j, j, paramInt, i);
    requestLayout();
  }
  
  public void setPageMarginDrawable(int paramInt)
  {
    setPageMarginDrawable(getContext().getResources().getDrawable(paramInt));
  }
  
  public void setPageMarginDrawable(Drawable paramDrawable)
  {
    this.mMarginDrawable = paramDrawable;
    if (paramDrawable != null) {
      refreshDrawableState();
    }
    boolean bool;
    if (paramDrawable != null) {
      bool = false;
    } else {
      bool = true;
    }
    setWillNotDraw(bool);
    invalidate();
  }
  
  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer)
  {
    int i = 1;
    if (Build.VERSION.SDK_INT >= 11)
    {
      boolean bool1;
      if (paramPageTransformer == null) {
        bool1 = false;
      } else {
        bool1 = i;
      }
      boolean bool2;
      if (this.mPageTransformer == null) {
        bool2 = false;
      } else {
        bool2 = i;
      }
      if (bool1 == bool2) {
        bool2 = false;
      } else {
        bool2 = i;
      }
      this.mPageTransformer = paramPageTransformer;
      setChildrenDrawingOrderEnabledCompat(bool1);
      if (!bool1)
      {
        this.mDrawingOrder = 0;
      }
      else
      {
        if (paramBoolean) {
          i = 2;
        }
        this.mDrawingOrder = i;
      }
      if (bool2) {
        populate();
      }
    }
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollTo(paramInt1, paramInt2, 0);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getChildCount() != 0)
    {
      int k = getScrollX();
      int m = getScrollY();
      int i = paramInt1 - k;
      int j = paramInt2 - m;
      if ((i != 0) || (j != 0))
      {
        setScrollingCacheEnabled(true);
        setScrollState(2);
        int n = getClientWidth();
        int i2 = n / 2;
        float f3 = Math.min(1.0F, 1.0F * Math.abs(i) / n);
        float f2 = i2 + i2 * distanceInfluenceForSnapDuration(f3);
        int i3 = Math.abs(paramInt3);
        if (i3 <= 0)
        {
          float f1 = n * this.mAdapter.getPageWidth(this.mCurItem);
          i1 = (int)(100.0F * (1.0F + Math.abs(i) / (f1 + this.mPageMargin)));
        }
        else
        {
          i1 = 4 * Math.round(1000.0F * Math.abs(f2 / i3));
        }
        int i1 = Math.min(i1, 600);
        this.mScroller.startScroll(k, m, i, j, i1);
        ViewCompat.postInvalidateOnAnimation(this);
      }
      else
      {
        completeScroll(false);
        populate();
        setScrollState(0);
      }
    }
    else
    {
      setScrollingCacheEnabled(false);
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((!super.verifyDrawable(paramDrawable)) && (paramDrawable != this.mMarginDrawable)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  static class ViewPositionComparator
    implements Comparator<View>
  {
    public int compare(View paramView1, View paramView2)
    {
      ViewPager.LayoutParams localLayoutParams2 = (ViewPager.LayoutParams)paramView1.getLayoutParams();
      ViewPager.LayoutParams localLayoutParams1 = (ViewPager.LayoutParams)paramView2.getLayoutParams();
      int i;
      if (localLayoutParams2.isDecor == localLayoutParams1.isDecor) {
        i = localLayoutParams2.position - localLayoutParams1.position;
      } else if (!localLayoutParams2.isDecor) {
        i = -1;
      } else {
        i = 1;
      }
      return i;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    int childIndex;
    public int gravity;
    public boolean isDecor;
    boolean needsMeasure;
    int position;
    float widthFactor = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ViewPager.LAYOUT_ATTRS);
      this.gravity = localTypedArray.getInteger(0, 48);
      localTypedArray.recycle();
    }
  }
  
  private class PagerObserver
    extends DataSetObserver
  {
    private PagerObserver() {}
    
    public void onChanged()
    {
      ViewPager.this.dataSetChanged();
    }
    
    public void onInvalidated()
    {
      ViewPager.this.dataSetChanged();
    }
  }
  
  class MyAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    MyAccessibilityDelegate() {}
    
    private boolean canScroll()
    {
      int i = 1;
      if ((ViewPager.this.mAdapter == null) || (ViewPager.this.mAdapter.getCount() <= i)) {
        i = 0;
      }
      return i;
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ViewPager.class.getName());
      AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityRecordCompat.obtain();
      localAccessibilityRecordCompat.setScrollable(canScroll());
      if ((paramAccessibilityEvent.getEventType() == 4096) && (ViewPager.this.mAdapter != null))
      {
        localAccessibilityRecordCompat.setItemCount(ViewPager.this.mAdapter.getCount());
        localAccessibilityRecordCompat.setFromIndex(ViewPager.this.mCurItem);
        localAccessibilityRecordCompat.setToIndex(ViewPager.this.mCurItem);
      }
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
      paramAccessibilityNodeInfoCompat.setScrollable(canScroll());
      if (ViewPager.this.canScrollHorizontally(1)) {
        paramAccessibilityNodeInfoCompat.addAction(4096);
      }
      if (ViewPager.this.canScrollHorizontally(-1)) {
        paramAccessibilityNodeInfoCompat.addAction(8192);
      }
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      int i = 1;
      if (!super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
        switch (paramInt)
        {
        default: 
          i = 0;
          break;
        case 4096: 
          if (!ViewPager.this.canScrollHorizontally(i)) {
            i = 0;
          } else {
            ViewPager.this.setCurrentItem(1 + ViewPager.this.mCurItem);
          }
          break;
        case 8192: 
          if (!ViewPager.this.canScrollHorizontally(-1)) {
            i = 0;
          } else {
            ViewPager.this.setCurrentItem(-1 + ViewPager.this.mCurItem);
          }
          break;
        }
      }
      return i;
    }
  }
  
  public static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public ViewPager.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new ViewPager.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public ViewPager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ViewPager.SavedState[paramAnonymousInt];
      }
    });
    Parcelable adapterState;
    ClassLoader loader;
    int position;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super();
      if (paramClassLoader == null) {
        paramClassLoader = getClass().getClassLoader();
      }
      this.position = paramParcel.readInt();
      this.adapterState = paramParcel.readParcelable(paramClassLoader);
      this.loader = paramClassLoader;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.position);
      paramParcel.writeParcelable(this.adapterState, paramInt);
    }
  }
  
  static abstract interface Decor {}
  
  static abstract interface OnAdapterChangeListener
  {
    public abstract void onAdapterChanged(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2);
  }
  
  public static abstract interface PageTransformer
  {
    public abstract void transformPage(View paramView, float paramFloat);
  }
  
  public static class SimpleOnPageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    public void onPageScrollStateChanged(int paramInt) {}
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
    
    public void onPageSelected(int paramInt) {}
  }
  
  public static abstract interface OnPageChangeListener
  {
    public abstract void onPageScrollStateChanged(int paramInt);
    
    public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
    
    public abstract void onPageSelected(int paramInt);
  }
  
  static class ItemInfo
  {
    Object object;
    float offset;
    int position;
    boolean scrolling;
    float widthFactor;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\view\ViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */