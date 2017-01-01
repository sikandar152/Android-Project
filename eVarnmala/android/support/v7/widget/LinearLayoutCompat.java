package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.IntDef;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ViewUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat
  extends ViewGroup
{
  public static final int HORIZONTAL = 0;
  private static final int INDEX_BOTTOM = 2;
  private static final int INDEX_CENTER_VERTICAL = 0;
  private static final int INDEX_FILL = 3;
  private static final int INDEX_TOP = 1;
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  public static final int SHOW_DIVIDER_END = 4;
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  public static final int SHOW_DIVIDER_NONE = 0;
  public static final int VERTICAL = 1;
  private static final int VERTICAL_GRAVITY_COUNT = 4;
  private boolean mBaselineAligned = true;
  private int mBaselineAlignedChildIndex = -1;
  private int mBaselineChildTop = 0;
  private Drawable mDivider;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mDividerWidth;
  private int mGravity = 8388659;
  private int[] mMaxAscent;
  private int[] mMaxDescent;
  private int mOrientation;
  private int mShowDividers;
  private int mTotalLength;
  private boolean mUseLargestChild;
  private float mWeightSum;
  
  public LinearLayoutCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.LinearLayoutCompat, paramInt, 0);
    int i = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
    if (i >= 0) {
      setOrientation(i);
    }
    i = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
    if (i >= 0) {
      setGravity(i);
    }
    boolean bool = localTintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
    if (!bool) {
      setBaselineAligned(bool);
    }
    this.mWeightSum = localTintTypedArray.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0F);
    this.mBaselineAlignedChildIndex = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
    this.mUseLargestChild = localTintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
    setDividerDrawable(localTintTypedArray.getDrawable(R.styleable.LinearLayoutCompat_divider));
    this.mShowDividers = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
    this.mDividerPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
    localTintTypedArray.recycle();
  }
  
  private void forceUniformHeight(int paramInt1, int paramInt2)
  {
    int k = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
    for (int i = 0;; i++)
    {
      if (i >= paramInt1) {
        return;
      }
      View localView = getVirtualChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.height == -1)
        {
          int j = localLayoutParams.width;
          localLayoutParams.width = localView.getMeasuredWidth();
          measureChildWithMargins(localView, paramInt2, 0, k, 0);
          localLayoutParams.width = j;
        }
      }
    }
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    for (int i = 0;; i++)
    {
      if (i >= paramInt1) {
        return;
      }
      View localView = getVirtualChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.width == -1)
        {
          int k = localLayoutParams.height;
          localLayoutParams.height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, j, 0, paramInt2, 0);
          localLayoutParams.height = k;
        }
      }
    }
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void drawDividersHorizontal(Canvas paramCanvas)
  {
    int k = getVirtualChildCount();
    boolean bool = ViewUtils.isLayoutRtl(this);
    View localView1;
    for (int j = 0;; localView1++)
    {
      int i;
      if (j >= k)
      {
        if (hasDividerBeforeChildAt(k))
        {
          localView1 = getVirtualChildAt(k - 1);
          if (localView1 != null)
          {
            LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
            if (!bool) {
              i = localView1.getRight() + localLayoutParams1.rightMargin;
            } else {
              i = localView1.getLeft() - localLayoutParams1.leftMargin - this.mDividerWidth;
            }
          }
          else if (i == 0)
          {
            i = getWidth() - getPaddingRight() - this.mDividerWidth;
          }
          else
          {
            i = getPaddingLeft();
          }
          drawVerticalDivider(paramCanvas, i);
        }
        return;
      }
      View localView2 = getVirtualChildAt(localView1);
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(localView1)))
      {
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        int m;
        if (i == 0) {
          m = localView2.getLeft() - localLayoutParams2.leftMargin - this.mDividerWidth;
        } else {
          m = m.getRight() + localLayoutParams2.rightMargin;
        }
        drawVerticalDivider(paramCanvas, m);
      }
    }
  }
  
  void drawDividersVertical(Canvas paramCanvas)
  {
    int j = getVirtualChildCount();
    for (int k = 0;; k++)
    {
      if (k >= j)
      {
        if (hasDividerBeforeChildAt(j))
        {
          View localView1 = getVirtualChildAt(j - 1);
          int i;
          if (localView1 != null)
          {
            LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
            i = localView1.getBottom() + localLayoutParams1.bottomMargin;
          }
          else
          {
            i = getHeight() - getPaddingBottom() - this.mDividerHeight;
          }
          drawHorizontalDivider(paramCanvas, i);
        }
        return;
      }
      View localView2 = getVirtualChildAt(k);
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(k)))
      {
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        drawHorizontalDivider(paramCanvas, localView2.getTop() - localLayoutParams2.topMargin - this.mDividerHeight);
      }
    }
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, paramInt, getWidth() - getPaddingRight() - this.mDividerPadding, paramInt + this.mDividerHeight);
    this.mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(paramInt, getPaddingTop() + this.mDividerPadding, paramInt + this.mDividerWidth, getHeight() - getPaddingBottom() - this.mDividerPadding);
    this.mDivider.draw(paramCanvas);
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams;
    if (this.mOrientation != 0)
    {
      if (this.mOrientation != 1) {
        localLayoutParams = null;
      } else {
        localLayoutParams = new LayoutParams(-1, -2);
      }
    }
    else {
      localLayoutParams = new LayoutParams(-2, -2);
    }
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getBaseline()
  {
    int j = -1;
    if (this.mBaselineAlignedChildIndex >= 0)
    {
      if (getChildCount() > this.mBaselineAlignedChildIndex)
      {
        View localView = getChildAt(this.mBaselineAlignedChildIndex);
        int i = localView.getBaseline();
        if (i != j)
        {
          j = this.mBaselineChildTop;
          if (this.mOrientation == 1)
          {
            int k = 0x70 & this.mGravity;
            if (k != 48) {
              switch (k)
              {
              case 16: 
                j += (getBottom() - getTop() - getPaddingTop() - getPaddingBottom() - this.mTotalLength) / 2;
                break;
              case 80: 
                j = getBottom() - getTop() - getPaddingBottom() - this.mTotalLength;
              }
            }
          }
          j = i + (j + ((LayoutParams)localView.getLayoutParams()).topMargin);
        }
        else if (this.mBaselineAlignedChildIndex != 0)
        {
          throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
      }
      else
      {
        throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
      }
    }
    else {
      j = super.getBaseline();
    }
    return j;
  }
  
  public int getBaselineAlignedChildIndex()
  {
    return this.mBaselineAlignedChildIndex;
  }
  
  int getChildrenSkipCount(View paramView, int paramInt)
  {
    return 0;
  }
  
  public Drawable getDividerDrawable()
  {
    return this.mDivider;
  }
  
  public int getDividerPadding()
  {
    return this.mDividerPadding;
  }
  
  public int getDividerWidth()
  {
    return this.mDividerWidth;
  }
  
  int getLocationOffset(View paramView)
  {
    return 0;
  }
  
  int getNextLocationOffset(View paramView)
  {
    return 0;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getShowDividers()
  {
    return this.mShowDividers;
  }
  
  View getVirtualChildAt(int paramInt)
  {
    return getChildAt(paramInt);
  }
  
  int getVirtualChildCount()
  {
    return getChildCount();
  }
  
  public float getWeightSum()
  {
    return this.mWeightSum;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    int i = 1;
    if (paramInt != 0)
    {
      if (paramInt != getChildCount())
      {
        if ((0x2 & this.mShowDividers) == 0)
        {
          i = 0;
        }
        else
        {
          int j = 0;
          i = paramInt - 1;
          while (i >= 0) {
            if (getChildAt(i).getVisibility() == 8) {
              i--;
            } else {
              j = 1;
            }
          }
          i = j;
        }
      }
      else if ((0x4 & this.mShowDividers) == 0) {
        i = 0;
      }
    }
    else if ((0x1 & this.mShowDividers) == 0) {
      i = 0;
    }
    return i;
  }
  
  public boolean isBaselineAligned()
  {
    return this.mBaselineAligned;
  }
  
  public boolean isMeasureWithLargestChildEnabled()
  {
    return this.mUseLargestChild;
  }
  
  void layoutHorizontal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool2 = ViewUtils.isLayoutRtl(this);
    int i = getPaddingTop();
    int k = paramInt4 - paramInt2;
    int j = k - getPaddingBottom();
    int i1 = k - i - getPaddingBottom();
    int m = getVirtualChildCount();
    int i2 = 0x800007 & this.mGravity;
    int n = 0x70 & this.mGravity;
    boolean bool1 = this.mBaselineAligned;
    int[] arrayOfInt2 = this.mMaxAscent;
    int[] arrayOfInt1 = this.mMaxDescent;
    int i6;
    switch (GravityCompat.getAbsoluteGravity(i2, ViewCompat.getLayoutDirection(this)))
    {
    default: 
      i6 = getPaddingLeft();
      break;
    case 1: 
      i6 = getPaddingLeft() + (paramInt3 - paramInt1 - this.mTotalLength) / 2;
      break;
    case 5: 
      i6 = paramInt3 + getPaddingLeft() - paramInt1 - this.mTotalLength;
    }
    i2 = 0;
    int i3 = 1;
    if (bool2)
    {
      i2 = m - 1;
      i3 = -1;
    }
    for (int i5 = 0;; i5++)
    {
      if (i5 >= m) {
        return;
      }
      int i7 = i2 + i3 * i5;
      View localView = getVirtualChildAt(i7);
      if (localView != null)
      {
        if (localView.getVisibility() != 8)
        {
          int i4 = localView.getMeasuredWidth();
          int i8 = localView.getMeasuredHeight();
          int i10 = -1;
          LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if ((bool1) && (localLayoutParams.height != -1)) {
            i10 = localView.getBaseline();
          }
          int i9 = localLayoutParams.gravity;
          if (i9 < 0) {
            i9 = n;
          }
          switch (i9 & 0x70)
          {
          default: 
            i9 = i;
            break;
          case 16: 
            i9 = i + (i1 - i8) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
            break;
          case 48: 
            i9 = i + localLayoutParams.topMargin;
            if (i10 != -1) {
              i9 += arrayOfInt2[1] - i10;
            }
            break;
          case 80: 
            i9 = j - i8 - localLayoutParams.bottomMargin;
            if (i10 != -1)
            {
              i10 = localView.getMeasuredHeight() - i10;
              i9 -= arrayOfInt1[2] - i10;
            }
            break;
          }
          if (hasDividerBeforeChildAt(i7)) {
            i6 += this.mDividerWidth;
          }
          i6 += localLayoutParams.leftMargin;
          setChildFrame(localView, i6 + getLocationOffset(localView), i9, i4, i8);
          i6 += i4 + localLayoutParams.rightMargin + getNextLocationOffset(localView);
          i5 += getChildrenSkipCount(localView, i7);
        }
      }
      else {
        i6 += measureNullChild(i7);
      }
    }
  }
  
  void layoutVertical(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getPaddingLeft();
    int k = paramInt3 - paramInt1;
    int j = k - getPaddingRight();
    int n = k - i - getPaddingRight();
    int m = getVirtualChildCount();
    int i1 = 0x70 & this.mGravity;
    k = 0x800007 & this.mGravity;
    int i4;
    switch (i1)
    {
    default: 
      i4 = getPaddingTop();
      break;
    case 16: 
      i4 = getPaddingTop() + (paramInt4 - paramInt2 - this.mTotalLength) / 2;
      break;
    case 80: 
      i4 = paramInt4 + getPaddingTop() - paramInt2 - this.mTotalLength;
    }
    for (int i3 = 0;; i3++)
    {
      if (i3 >= m) {
        return;
      }
      View localView = getVirtualChildAt(i3);
      if (localView != null)
      {
        if (localView.getVisibility() != 8)
        {
          i1 = localView.getMeasuredWidth();
          int i2 = localView.getMeasuredHeight();
          LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
          int i5 = localLayoutParams.gravity;
          if (i5 < 0) {
            i5 = k;
          }
          switch (0x7 & GravityCompat.getAbsoluteGravity(i5, ViewCompat.getLayoutDirection(this)))
          {
          default: 
            i5 = i + localLayoutParams.leftMargin;
            break;
          case 1: 
            i5 = i + (n - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
            break;
          case 5: 
            i5 = j - i1 - localLayoutParams.rightMargin;
          }
          if (hasDividerBeforeChildAt(i3)) {
            i4 += this.mDividerHeight;
          }
          i4 += localLayoutParams.topMargin;
          setChildFrame(localView, i5, i4 + getLocationOffset(localView), i1, i2);
          i4 += i2 + localLayoutParams.bottomMargin + getNextLocationOffset(localView);
          i3 += getChildrenSkipCount(localView, i3);
        }
      }
      else {
        i4 += measureNullChild(i3);
      }
    }
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    measureChildWithMargins(paramView, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void measureHorizontal(int paramInt1, int paramInt2)
  {
    this.mTotalLength = 0;
    int i2 = 0;
    int k = 0;
    int m = 0;
    int i7 = 0;
    int n = 1;
    float f2 = 0.0F;
    int i = getVirtualChildCount();
    int i3 = View.MeasureSpec.getMode(paramInt1);
    int i1 = View.MeasureSpec.getMode(paramInt2);
    int j = 0;
    int i10 = 0;
    if ((this.mMaxAscent == null) || (this.mMaxDescent == null))
    {
      this.mMaxAscent = new int[4];
      this.mMaxDescent = new int[4];
    }
    Object localObject1 = this.mMaxAscent;
    int[] arrayOfInt = this.mMaxDescent;
    localObject1[3] = -1;
    localObject1[2] = -1;
    localObject1[1] = -1;
    localObject1[0] = -1;
    arrayOfInt[3] = -1;
    arrayOfInt[2] = -1;
    arrayOfInt[1] = -1;
    arrayOfInt[0] = -1;
    boolean bool1 = this.mBaselineAligned;
    boolean bool2 = this.mUseLargestChild;
    int i4;
    if (i3 != 1073741824) {
      i4 = 0;
    } else {
      i4 = 1;
    }
    int i5 = Integer.MIN_VALUE;
    int i13;
    for (int i12 = 0;; i13++)
    {
      float f1;
      int i9;
      int i8;
      if (i12 >= i)
      {
        if ((this.mTotalLength > 0) && (hasDividerBeforeChildAt(i))) {
          this.mTotalLength += this.mDividerWidth;
        }
        float f4;
        if ((localObject1[1] != -1) || (localObject1[0] != -1) || (localObject1[2] != -1) || (localObject1[3] != -1))
        {
          f4 = Math.max(localObject1[3], Math.max(localObject1[0], Math.max(localObject1[1], localObject1[2]))) + Math.max(arrayOfInt[3], Math.max(arrayOfInt[0], Math.max(arrayOfInt[1], arrayOfInt[2])));
          i2 = Math.max(i2, f4);
        }
        if ((bool2) && ((i3 == Integer.MIN_VALUE) || (i3 == 0))) {
          this.mTotalLength = 0;
        }
        for (i12 = 0;; i13++)
        {
          if (i12 >= i)
          {
            this.mTotalLength += getPaddingLeft() + getPaddingRight();
            f4 = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), paramInt1, 0);
            int i11 = (f4 & 0xFFFFFF) - this.mTotalLength;
            if ((i10 == 0) && ((i11 == 0) || (f2 <= 0.0F)))
            {
              m = Math.max(m, i7);
              if ((bool2) && (i3 != 1073741824)) {
                i3 = 0;
              }
            }
            else
            {
              while (i3 < i)
              {
                localObject1 = getVirtualChildAt(i3);
                if ((localObject1 != null) && (((View)localObject1).getVisibility() != 8) && (((LayoutParams)((View)localObject1).getLayoutParams()).weight > 0.0F)) {
                  ((View)localObject1).measure(View.MeasureSpec.makeMeasureSpec(i5, 1073741824), View.MeasureSpec.makeMeasureSpec(((View)localObject1).getMeasuredHeight(), 1073741824));
                }
                i3++;
                continue;
                if (this.mWeightSum <= 0.0F) {
                  f1 = f2;
                } else {
                  f1 = this.mWeightSum;
                }
                localObject1[3] = -1;
                localObject1[2] = -1;
                localObject1[1] = -1;
                localObject1[0] = -1;
                arrayOfInt[3] = -1;
                arrayOfInt[2] = -1;
                arrayOfInt[1] = -1;
                arrayOfInt[0] = -1;
                i2 = -1;
                this.mTotalLength = 0;
              }
            }
            for (i7 = 0;; i7++)
            {
              if (i7 >= i)
              {
                this.mTotalLength += getPaddingLeft() + getPaddingRight();
                if ((localObject1[1] != -1) || (localObject1[0] != -1) || (localObject1[2] != -1) || (localObject1[3] != -1))
                {
                  i3 = Math.max(localObject1[3], Math.max(localObject1[0], Math.max(localObject1[1], localObject1[2]))) + Math.max(arrayOfInt[3], Math.max(arrayOfInt[0], Math.max(arrayOfInt[1], arrayOfInt[2])));
                  i2 = Math.max(i2, i3);
                }
                if ((n == 0) && (i1 != 1073741824)) {
                  i2 = m;
                }
                m = Math.max(i2 + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
                setMeasuredDimension(f4 | 0xFF000000 & k, ViewCompat.resolveSizeAndState(m, paramInt2, k << 16));
                if (j != 0) {
                  forceUniformHeight(i, paramInt1);
                }
                return;
              }
              View localView1 = getVirtualChildAt(i7);
              if ((localView1 != null) && (localView1.getVisibility() != 8))
              {
                LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
                float f5 = localLayoutParams1.weight;
                if (f5 > 0.0F)
                {
                  i10 = (int)(f5 * i11 / f1);
                  f1 -= f5;
                  i11 -= i10;
                  i13 = getChildMeasureSpec(paramInt2, getPaddingTop() + getPaddingBottom() + localLayoutParams1.topMargin + localLayoutParams1.bottomMargin, localLayoutParams1.height);
                  if ((localLayoutParams1.width == 0) && (i3 == 1073741824))
                  {
                    if (i10 <= 0) {
                      i10 = 0;
                    }
                    localView1.measure(View.MeasureSpec.makeMeasureSpec(i10, 1073741824), i13);
                  }
                  else
                  {
                    i10 += localView1.getMeasuredWidth();
                    if (i10 < 0) {
                      i10 = 0;
                    }
                    localView1.measure(View.MeasureSpec.makeMeasureSpec(i10, 1073741824), i13);
                  }
                  i10 = 0xFF000000 & ViewCompat.getMeasuredState(localView1);
                  k = ViewUtils.combineMeasuredStates(k, i10);
                }
                if (i4 == 0)
                {
                  i10 = this.mTotalLength;
                  this.mTotalLength = Math.max(i10, i10 + localView1.getMeasuredWidth() + localLayoutParams1.leftMargin + localLayoutParams1.rightMargin + getNextLocationOffset(localView1));
                }
                else
                {
                  this.mTotalLength += localView1.getMeasuredWidth() + localLayoutParams1.leftMargin + localLayoutParams1.rightMargin + getNextLocationOffset(localView1);
                }
                if ((i1 == 1073741824) || (localLayoutParams1.height != -1)) {
                  i13 = 0;
                } else {
                  i13 = 1;
                }
                int i14 = localLayoutParams1.topMargin + localLayoutParams1.bottomMargin;
                i10 = i14 + localView1.getMeasuredHeight();
                i2 = Math.max(i2, i10);
                if (i13 == 0) {
                  i14 = i10;
                }
                m = Math.max(m, i14);
                if ((n == 0) || (localLayoutParams1.height != -1)) {
                  n = 0;
                } else {
                  n = 1;
                }
                if (bool1)
                {
                  i9 = localView1.getBaseline();
                  if (i9 != -1)
                  {
                    if (localLayoutParams1.gravity >= 0) {
                      i8 = localLayoutParams1.gravity;
                    } else {
                      i8 = this.mGravity;
                    }
                    i8 = (0xFFFFFFFE & (i8 & 0x70) >> 4) >> 1;
                    localObject1[i8] = Math.max(localObject1[i8], i9);
                    arrayOfInt[i8] = Math.max(arrayOfInt[i8], i10 - i9);
                  }
                }
              }
            }
          }
          View localView2 = getVirtualChildAt(i13);
          if (localView2 != null)
          {
            if (localView2.getVisibility() != 8)
            {
              localObject2 = (LayoutParams)localView2.getLayoutParams();
              if (i4 == 0)
              {
                f4 = this.mTotalLength;
                this.mTotalLength = Math.max(f4, f4 + f1 + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset(localView2));
              }
              else
              {
                this.mTotalLength += f1 + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset(localView2);
              }
            }
            else
            {
              i13 += getChildrenSkipCount(localView2, i13);
            }
          }
          else {
            this.mTotalLength += measureNullChild(i13);
          }
        }
      }
      Object localObject2 = getVirtualChildAt(i13);
      if (localObject2 != null)
      {
        if (((View)localObject2).getVisibility() != 8)
        {
          if (hasDividerBeforeChildAt(i13)) {
            this.mTotalLength += this.mDividerWidth;
          }
          LayoutParams localLayoutParams2 = (LayoutParams)((View)localObject2).getLayoutParams();
          float f3;
          i9 += localLayoutParams2.weight;
          if ((i3 != 1073741824) || (localLayoutParams2.width != 0) || (localLayoutParams2.weight <= 0.0F))
          {
            i15 = Integer.MIN_VALUE;
            if ((localLayoutParams2.width == 0) && (localLayoutParams2.weight > 0.0F))
            {
              i15 = 0;
              localLayoutParams2.width = -2;
            }
            if (f3 != 0.0F) {
              i16 = 0;
            } else {
              i16 = this.mTotalLength;
            }
            measureChildBeforeLayout((View)localObject2, i13, paramInt1, i16, paramInt2, 0);
            if (i15 != Integer.MIN_VALUE) {
              localLayoutParams2.width = i15;
            }
            i16 = ((View)localObject2).getMeasuredWidth();
            if (i4 == 0)
            {
              i15 = this.mTotalLength;
              this.mTotalLength = Math.max(i15, i15 + i16 + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin + getNextLocationOffset((View)localObject2));
            }
            else
            {
              this.mTotalLength += i16 + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin + getNextLocationOffset((View)localObject2);
            }
            if (i8 != 0) {
              int i6 = Math.max(i16, f1);
            }
          }
          else
          {
            if (i4 == 0)
            {
              i15 = this.mTotalLength;
              this.mTotalLength = Math.max(i15, i15 + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin);
            }
            else
            {
              this.mTotalLength += localLayoutParams2.leftMargin + localLayoutParams2.rightMargin;
            }
            if (!bool1)
            {
              i10 = 1;
            }
            else
            {
              i15 = View.MeasureSpec.makeMeasureSpec(0, 0);
              ((View)localObject2).measure(i15, i15);
            }
          }
          int i17 = 0;
          if ((i1 != 1073741824) && (localLayoutParams2.height == -1))
          {
            j = 1;
            i17 = 1;
          }
          int i15 = localLayoutParams2.topMargin + localLayoutParams2.bottomMargin;
          int i16 = i15 + ((View)localObject2).getMeasuredHeight();
          int i18 = ViewCompat.getMeasuredState((View)localObject2);
          k = ViewUtils.combineMeasuredStates(k, i18);
          if (bool1)
          {
            i18 = ((View)localObject2).getBaseline();
            if (i18 != -1)
            {
              if (localLayoutParams2.gravity >= 0) {
                i19 = localLayoutParams2.gravity;
              } else {
                i19 = this.mGravity;
              }
              int i19 = (0xFFFFFFFE & (i19 & 0x70) >> 4) >> 1;
              localObject1[i19] = Math.max(localObject1[i19], i18);
              arrayOfInt[i19] = Math.max(arrayOfInt[i19], i16 - i18);
            }
          }
          i2 = Math.max(i2, i16);
          if ((n == 0) || (localLayoutParams2.height != -1)) {
            n = 0;
          } else {
            n = 1;
          }
          if (localLayoutParams2.weight <= 0.0F)
          {
            if (i17 == 0) {
              i15 = i16;
            }
            m = Math.max(m, i15);
          }
          else
          {
            if (i17 == 0) {
              i15 = i16;
            }
            i7 = Math.max(i7, i15);
          }
          i13 += getChildrenSkipCount((View)localObject2, i13);
        }
        else
        {
          i13 += getChildrenSkipCount((View)localObject2, i13);
        }
      }
      else {
        this.mTotalLength += measureNullChild(i13);
      }
    }
  }
  
  int measureNullChild(int paramInt)
  {
    return 0;
  }
  
  void measureVertical(int paramInt1, int paramInt2)
  {
    this.mTotalLength = 0;
    int n = 0;
    int i = 0;
    int m = 0;
    int i7 = 0;
    int j = 1;
    float f2 = 0.0F;
    int i2 = getVirtualChildCount();
    int i1 = View.MeasureSpec.getMode(paramInt1);
    int i3 = View.MeasureSpec.getMode(paramInt2);
    int k = 0;
    int i9 = 0;
    int i13 = this.mBaselineAlignedChildIndex;
    boolean bool = this.mUseLargestChild;
    int i4 = Integer.MIN_VALUE;
    int i11;
    for (View localView3 = 0;; i11++)
    {
      float f1;
      int i6;
      View localView2;
      LayoutParams localLayoutParams1;
      int i10;
      View localView4;
      if (localView3 >= i2)
      {
        if ((this.mTotalLength > 0) && (hasDividerBeforeChildAt(i2))) {
          this.mTotalLength += this.mDividerHeight;
        }
        if ((bool) && ((i3 == Integer.MIN_VALUE) || (i3 == 0))) {
          this.mTotalLength = 0;
        }
        for (localView3 = 0;; localView3++)
        {
          float f5;
          if (localView3 >= i2)
          {
            this.mTotalLength += getPaddingTop() + getPaddingBottom();
            localView3 = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), paramInt2, 0);
            int i12 = (localView3 & 0xFFFFFF) - this.mTotalLength;
            if ((i9 == 0) && ((i12 == 0) || (f2 <= 0.0F)))
            {
              m = Math.max(m, i7);
              if ((bool) && (i3 != 1073741824)) {
                i3 = 0;
              }
            }
            else
            {
              while (i3 < i2)
              {
                View localView1 = getVirtualChildAt(i3);
                if ((localView1 != null) && (localView1.getVisibility() != 8) && (((LayoutParams)localView1.getLayoutParams()).weight > 0.0F)) {
                  localView1.measure(View.MeasureSpec.makeMeasureSpec(localView1.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i4, 1073741824));
                }
                i3++;
                continue;
                if (this.mWeightSum <= 0.0F) {
                  f1 = f2;
                } else {
                  f1 = this.mWeightSum;
                }
                this.mTotalLength = 0;
              }
            }
            for (i6 = 0;; i6++)
            {
              if (i6 >= i2)
              {
                this.mTotalLength += getPaddingTop() + getPaddingBottom();
                if ((j == 0) && (i1 != 1073741824)) {
                  n = m;
                }
                setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(n + (getPaddingLeft() + getPaddingRight()), getSuggestedMinimumWidth()), paramInt1, i), localView3);
                if (k != 0) {
                  forceUniformWidth(i2, paramInt2);
                }
                return;
              }
              localView2 = getVirtualChildAt(i6);
              if (localView2.getVisibility() != 8)
              {
                localLayoutParams1 = (LayoutParams)localView2.getLayoutParams();
                float f4 = localLayoutParams1.weight;
                if (f4 > 0.0F)
                {
                  f5 = (int)(f4 * i12 / f1);
                  f1 -= f4;
                  i12 -= f5;
                  i10 = getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + localLayoutParams1.leftMargin + localLayoutParams1.rightMargin, localLayoutParams1.width);
                  if ((localLayoutParams1.height == 0) && (i3 == 1073741824))
                  {
                    if (f5 <= 0) {
                      f5 = 0;
                    }
                    localView2.measure(i10, View.MeasureSpec.makeMeasureSpec(f5, 1073741824));
                  }
                  else
                  {
                    f5 += localView2.getMeasuredHeight();
                    if (f5 < 0) {
                      f5 = 0;
                    }
                    localView2.measure(i10, View.MeasureSpec.makeMeasureSpec(f5, 1073741824));
                  }
                  i = ViewUtils.combineMeasuredStates(i, 0xFF00 & ViewCompat.getMeasuredState(localView2));
                }
                i13 = localLayoutParams1.leftMargin + localLayoutParams1.rightMargin;
                f5 = i13 + localView2.getMeasuredWidth();
                n = Math.max(n, f5);
                if ((i1 == 1073741824) || (localLayoutParams1.width != -1)) {
                  i10 = 0;
                } else {
                  i10 = 1;
                }
                if (i10 == 0) {
                  i13 = f5;
                }
                m = Math.max(m, i13);
                if ((j == 0) || (localLayoutParams1.width != -1)) {
                  j = 0;
                } else {
                  j = 1;
                }
                i10 = this.mTotalLength;
                this.mTotalLength = Math.max(i10, i10 + localView2.getMeasuredHeight() + localLayoutParams1.topMargin + localLayoutParams1.bottomMargin + getNextLocationOffset(localView2));
              }
            }
          }
          localView4 = getVirtualChildAt(localView3);
          if (localView4 != null)
          {
            if (localView4.getVisibility() != 8)
            {
              localObject = (LayoutParams)localView4.getLayoutParams();
              f5 = this.mTotalLength;
              this.mTotalLength = Math.max(f5, f5 + f1 + ((LayoutParams)localObject).topMargin + ((LayoutParams)localObject).bottomMargin + getNextLocationOffset(localView4));
            }
            else
            {
              localView3 += getChildrenSkipCount(localView4, localView3);
            }
          }
          else {
            this.mTotalLength += measureNullChild(localView3);
          }
        }
      }
      Object localObject = getVirtualChildAt(localView3);
      if (localObject != null)
      {
        if (((View)localObject).getVisibility() != 8)
        {
          if (hasDividerBeforeChildAt(localView3)) {
            this.mTotalLength += this.mDividerHeight;
          }
          LayoutParams localLayoutParams2 = (LayoutParams)((View)localObject).getLayoutParams();
          float f3;
          localLayoutParams1 += localLayoutParams2.weight;
          int i14;
          int i15;
          if ((i3 != 1073741824) || (localLayoutParams2.height != 0) || (localLayoutParams2.weight <= 0.0F))
          {
            i14 = Integer.MIN_VALUE;
            if ((localLayoutParams2.height == 0) && (localLayoutParams2.weight > 0.0F))
            {
              i14 = 0;
              localLayoutParams2.height = -2;
            }
            if (f3 != 0.0F) {
              i15 = 0;
            } else {
              i15 = this.mTotalLength;
            }
            measureChildBeforeLayout((View)localObject, localView3, paramInt1, 0, paramInt2, i15);
            if (i14 != Integer.MIN_VALUE) {
              localLayoutParams2.height = i14;
            }
            i15 = ((View)localObject).getMeasuredHeight();
            i14 = this.mTotalLength;
            this.mTotalLength = Math.max(i14, i14 + i15 + localLayoutParams2.topMargin + localLayoutParams2.bottomMargin + getNextLocationOffset((View)localObject));
            if (i6 != 0) {
              int i5 = Math.max(i15, f1);
            }
          }
          else
          {
            i10 = this.mTotalLength;
            this.mTotalLength = Math.max(i10, i10 + localLayoutParams2.topMargin + localLayoutParams2.bottomMargin);
            i10 = 1;
          }
          if ((localView4 >= 0) && (localView4 == localView3 + 1)) {
            this.mBaselineChildTop = this.mTotalLength;
          }
          if ((localView3 >= localView4) || (localLayoutParams2.weight <= 0.0F))
          {
            i14 = 0;
            if ((i1 != 1073741824) && (localLayoutParams2.width == -1))
            {
              k = 1;
              i14 = 1;
            }
            i15 = localLayoutParams2.leftMargin + localLayoutParams2.rightMargin;
            int i16 = i15 + ((View)localObject).getMeasuredWidth();
            n = Math.max(n, i16);
            i = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState((View)localObject));
            if ((j == 0) || (localLayoutParams2.width != -1)) {
              j = 0;
            } else {
              j = 1;
            }
            if (localLayoutParams2.weight <= 0.0F)
            {
              if (i14 == 0) {
                i15 = i16;
              }
              m = Math.max(m, i15);
            }
            else
            {
              if (i14 == 0) {
                i15 = i16;
              }
              int i8 = Math.max(localView2, i15);
            }
            localView3 += getChildrenSkipCount((View)localObject, localView3);
          }
          else
          {
            throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
          }
        }
        else
        {
          i11 += getChildrenSkipCount((View)localObject, i11);
        }
      }
      else {
        this.mTotalLength += measureNullChild(i11);
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mDivider != null) {
      if (this.mOrientation != 1) {
        drawDividersHorizontal(paramCanvas);
      } else {
        drawDividersVertical(paramCanvas);
      }
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mOrientation != 1) {
      layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mOrientation != 1) {
      measureHorizontal(paramInt1, paramInt2);
    } else {
      measureVertical(paramInt1, paramInt2);
    }
  }
  
  public void setBaselineAligned(boolean paramBoolean)
  {
    this.mBaselineAligned = paramBoolean;
  }
  
  public void setBaselineAlignedChildIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getChildCount()))
    {
      this.mBaselineAlignedChildIndex = paramInt;
      return;
    }
    throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    boolean bool = false;
    if (paramDrawable != this.mDivider)
    {
      this.mDivider = paramDrawable;
      if (paramDrawable == null)
      {
        this.mDividerWidth = 0;
        this.mDividerHeight = 0;
      }
      else
      {
        this.mDividerWidth = paramDrawable.getIntrinsicWidth();
        this.mDividerHeight = paramDrawable.getIntrinsicHeight();
      }
      if (paramDrawable == null) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
    }
  }
  
  public void setDividerPadding(int paramInt)
  {
    this.mDividerPadding = paramInt;
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      if ((0x800007 & paramInt) == 0) {
        paramInt |= 0x800003;
      }
      if ((paramInt & 0x70) == 0) {
        paramInt |= 0x30;
      }
      this.mGravity = paramInt;
      requestLayout();
    }
  }
  
  public void setHorizontalGravity(int paramInt)
  {
    int i = paramInt & 0x800007;
    if ((0x800007 & this.mGravity) != i)
    {
      this.mGravity = (i | 0xFF7FFFF8 & this.mGravity);
      requestLayout();
    }
  }
  
  public void setMeasureWithLargestChildEnabled(boolean paramBoolean)
  {
    this.mUseLargestChild = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    if (this.mOrientation != paramInt)
    {
      this.mOrientation = paramInt;
      requestLayout();
    }
  }
  
  public void setShowDividers(int paramInt)
  {
    if (paramInt != this.mShowDividers) {
      requestLayout();
    }
    this.mShowDividers = paramInt;
  }
  
  public void setVerticalGravity(int paramInt)
  {
    int i = paramInt & 0x70;
    if ((0x70 & this.mGravity) != i)
    {
      this.mGravity = (i | 0xFFFFFF8F & this.mGravity);
      requestLayout();
    }
  }
  
  public void setWeightSum(float paramFloat)
  {
    this.mWeightSum = Math.max(0.0F, paramFloat);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int gravity = -1;
    public float weight;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.weight = 0.0F;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2);
      this.weight = paramFloat;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayoutCompat_Layout);
      this.weight = localTypedArray.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0F);
      this.gravity = localTypedArray.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.weight = paramLayoutParams.weight;
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
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(flag=true, value={0L, 1L, 2L, 4L})
  public static @interface DividerMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L})
  public static @interface OrientationMode {}
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\LinearLayoutCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */