package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ViewUtils;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import java.util.List;

public class SwitchCompat
  extends CompoundButton
{
  private static final int[] CHECKED_STATE_SET;
  private static final int MONOSPACE = 3;
  private static final int SANS = 1;
  private static final int SERIF = 2;
  private static final int[] TEXT_APPEARANCE_ATTRS;
  private static final int THUMB_ANIMATION_DURATION = 250;
  private static final int TOUCH_MODE_DOWN = 1;
  private static final int TOUCH_MODE_DRAGGING = 2;
  private static final int TOUCH_MODE_IDLE;
  private int mMinFlingVelocity;
  private Layout mOffLayout;
  private Layout mOnLayout;
  private Animation mPositionAnimator;
  private boolean mShowText;
  private boolean mSplitTrack;
  private int mSwitchBottom;
  private int mSwitchHeight;
  private int mSwitchLeft;
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTop;
  private TransformationMethod mSwitchTransformationMethod;
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private ColorStateList mTextColors;
  private CharSequence mTextOff;
  private CharSequence mTextOn;
  private TextPaint mTextPaint = new TextPaint(1);
  private Drawable mThumbDrawable;
  private float mThumbPosition;
  private int mThumbTextPadding;
  private int mThumbWidth;
  private final TintManager mTintManager;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private Drawable mTrackDrawable;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  
  static
  {
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = 16842904;
    arrayOfInt[1] = 16842901;
    arrayOfInt[2] = R.attr.textAllCaps;
    TEXT_APPEARANCE_ATTRS = arrayOfInt;
    arrayOfInt = new int[1];
    arrayOfInt[0] = 16842912;
    CHECKED_STATE_SET = arrayOfInt;
  }
  
  public SwitchCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.switchStyle);
  }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = getResources();
    this.mTextPaint.density = localResources.getDisplayMetrics().density;
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.SwitchCompat, paramInt, 0);
    this.mThumbDrawable = localTintTypedArray.getDrawable(R.styleable.SwitchCompat_android_thumb);
    this.mTrackDrawable = localTintTypedArray.getDrawable(R.styleable.SwitchCompat_track);
    this.mTextOn = localTintTypedArray.getText(R.styleable.SwitchCompat_android_textOn);
    this.mTextOff = localTintTypedArray.getText(R.styleable.SwitchCompat_android_textOff);
    this.mShowText = localTintTypedArray.getBoolean(R.styleable.SwitchCompat_showText, true);
    this.mThumbTextPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_thumbTextPadding, 0);
    this.mSwitchMinWidth = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchMinWidth, 0);
    this.mSwitchPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchPadding, 0);
    this.mSplitTrack = localTintTypedArray.getBoolean(R.styleable.SwitchCompat_splitTrack, false);
    int i = localTintTypedArray.getResourceId(R.styleable.SwitchCompat_switchTextAppearance, 0);
    if (i != 0) {
      setSwitchTextAppearance(paramContext, i);
    }
    this.mTintManager = localTintTypedArray.getTintManager();
    localTintTypedArray.recycle();
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    refreshDrawableState();
    setChecked(isChecked());
  }
  
  private void animateThumbToCheckedState(boolean paramBoolean)
  {
    final float f1 = this.mThumbPosition;
    float f2;
    if (!paramBoolean) {
      f2 = 0.0F;
    } else {
      f2 = 1.0F;
    }
    this.mPositionAnimator = new Animation()
    {
      protected void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        SwitchCompat.this.setThumbPosition(f1 + paramAnonymousFloat * this.val$diff);
      }
    };
    this.mPositionAnimator.setDuration(250L);
    startAnimation(this.mPositionAnimator);
  }
  
  private void cancelPositionAnimator()
  {
    if (this.mPositionAnimator != null)
    {
      clearAnimation();
      this.mPositionAnimator = null;
    }
  }
  
  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    localMotionEvent.setAction(3);
    super.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }
  
  private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 >= paramFloat2) {
      if (paramFloat1 <= paramFloat3) {
        paramFloat2 = paramFloat1;
      } else {
        paramFloat2 = paramFloat3;
      }
    }
    return paramFloat2;
  }
  
  private boolean getTargetCheckedState()
  {
    boolean bool;
    if (this.mThumbPosition <= 0.5F) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private int getThumbOffset()
  {
    float f;
    if (!ViewUtils.isLayoutRtl(this)) {
      f = this.mThumbPosition;
    } else {
      f = 1.0F - this.mThumbPosition;
    }
    return (int)(0.5F + f * getThumbScrollRange());
  }
  
  private int getThumbScrollRange()
  {
    int j;
    if (this.mTrackDrawable == null)
    {
      int i = 0;
    }
    else
    {
      Rect localRect = this.mTempRect;
      this.mTrackDrawable.getPadding(localRect);
      j = this.mSwitchWidth - this.mThumbWidth - localRect.left - localRect.right;
    }
    return j;
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    int j = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i = this.mSwitchTop - this.mTouchSlop;
    int m = j + this.mSwitchLeft - this.mTouchSlop;
    int k = m + this.mThumbWidth + this.mTempRect.left + this.mTempRect.right + this.mTouchSlop;
    j = this.mSwitchBottom + this.mTouchSlop;
    if ((paramFloat1 <= m) || (paramFloat1 >= k) || (paramFloat2 <= i) || (paramFloat2 >= j)) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  private Layout makeLayout(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence;
    if (this.mSwitchTransformationMethod == null) {
      localCharSequence = paramCharSequence;
    } else {
      localCharSequence = this.mSwitchTransformationMethod.getTransformation(paramCharSequence, this);
    }
    return new StaticLayout(localCharSequence, this.mTextPaint, (int)Math.ceil(Layout.getDesiredWidth(localCharSequence, this.mTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
  }
  
  private void setThumbPosition(float paramFloat)
  {
    this.mThumbPosition = paramFloat;
    invalidate();
  }
  
  private void stopDrag(MotionEvent paramMotionEvent)
  {
    int i = 1;
    this.mTouchMode = 0;
    int j;
    if ((paramMotionEvent.getAction() != i) || (!isEnabled())) {
      j = 0;
    } else {
      j = i;
    }
    boolean bool;
    if (j == 0)
    {
      bool = isChecked();
    }
    else
    {
      this.mVelocityTracker.computeCurrentVelocity(1000);
      float f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) <= this.mMinFlingVelocity) {
        bool = getTargetCheckedState();
      } else if (!ViewUtils.isLayoutRtl(this))
      {
        if (f <= 0.0F) {
          bool = false;
        }
      }
      else if (f >= 0.0F) {
        bool = false;
      }
    }
    setChecked(bool);
    cancelSuperTouch(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = this.mTempRect;
    int i1 = this.mSwitchLeft;
    int j = this.mSwitchTop;
    int n = this.mSwitchRight;
    int i = this.mSwitchBottom;
    int m = i1 + getThumbOffset();
    if (this.mTrackDrawable != null)
    {
      this.mTrackDrawable.getPadding(localRect);
      m += localRect.left;
      this.mTrackDrawable.setBounds(i1, j, n, i);
    }
    if (this.mThumbDrawable != null)
    {
      this.mThumbDrawable.getPadding(localRect);
      n = m - localRect.left;
      int k = m + this.mThumbWidth + localRect.right;
      this.mThumbDrawable.setBounds(n, j, k, i);
      Drawable localDrawable = getBackground();
      if (localDrawable != null) {
        DrawableCompat.setHotspotBounds(localDrawable, n, j, k, i);
      }
    }
    super.draw(paramCanvas);
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mThumbDrawable != null) {
      DrawableCompat.setHotspot(this.mThumbDrawable, paramFloat1, paramFloat2);
    }
    if (this.mTrackDrawable != null) {
      DrawableCompat.setHotspot(this.mTrackDrawable, paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setState(arrayOfInt);
    }
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.setState(arrayOfInt);
    }
    invalidate();
  }
  
  public int getCompoundPaddingLeft()
  {
    int i;
    if (ViewUtils.isLayoutRtl(this))
    {
      i = super.getCompoundPaddingLeft() + this.mSwitchWidth;
      if (!TextUtils.isEmpty(getText())) {
        i += this.mSwitchPadding;
      }
    }
    else
    {
      i = super.getCompoundPaddingLeft();
    }
    return i;
  }
  
  public int getCompoundPaddingRight()
  {
    int i;
    if (!ViewUtils.isLayoutRtl(this))
    {
      i = super.getCompoundPaddingRight() + this.mSwitchWidth;
      if (!TextUtils.isEmpty(getText())) {
        i += this.mSwitchPadding;
      }
    }
    else
    {
      i = super.getCompoundPaddingRight();
    }
    return i;
  }
  
  public boolean getShowText()
  {
    return this.mShowText;
  }
  
  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }
  
  public int getSwitchMinWidth()
  {
    return this.mSwitchMinWidth;
  }
  
  public int getSwitchPadding()
  {
    return this.mSwitchPadding;
  }
  
  public CharSequence getTextOff()
  {
    return this.mTextOff;
  }
  
  public CharSequence getTextOn()
  {
    return this.mTextOn;
  }
  
  public Drawable getThumbDrawable()
  {
    return this.mThumbDrawable;
  }
  
  public int getThumbTextPadding()
  {
    return this.mThumbTextPadding;
  }
  
  public Drawable getTrackDrawable()
  {
    return this.mTrackDrawable;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      super.jumpDrawablesToCurrentState();
      if (this.mThumbDrawable != null) {
        this.mThumbDrawable.jumpToCurrentState();
      }
      if (this.mTrackDrawable != null) {
        this.mTrackDrawable.jumpToCurrentState();
      }
      if ((this.mPositionAnimator != null) && (this.mPositionAnimator.hasStarted()) && (!this.mPositionAnimator.hasEnded()))
      {
        clearAnimation();
        this.mPositionAnimator = null;
      }
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked()) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    return arrayOfInt;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Rect localRect1 = this.mTempRect;
    Object localObject = this.mTrackDrawable;
    if (localObject == null) {
      localRect1.setEmpty();
    } else {
      ((Drawable)localObject).getPadding(localRect1);
    }
    int i = this.mSwitchTop;
    int k = this.mSwitchBottom;
    i += localRect1.top;
    int j = k - localRect1.bottom;
    Drawable localDrawable = this.mThumbDrawable;
    if (localObject != null) {
      ((Drawable)localObject).draw(paramCanvas);
    }
    k = paramCanvas.save();
    if (localDrawable != null) {
      localDrawable.draw(paramCanvas);
    }
    if (!getTargetCheckedState()) {
      localObject = this.mOffLayout;
    } else {
      localObject = this.mOnLayout;
    }
    if (localObject != null)
    {
      int[] arrayOfInt = getDrawableState();
      if (this.mTextColors != null) {
        this.mTextPaint.setColor(this.mTextColors.getColorForState(arrayOfInt, 0));
      }
      this.mTextPaint.drawableState = arrayOfInt;
      int m;
      if (localDrawable == null)
      {
        m = getWidth();
      }
      else
      {
        Rect localRect2 = m.getBounds();
        n = localRect2.left + localRect2.right;
      }
      int n = n / 2 - ((Layout)localObject).getWidth() / 2;
      i = (i + j) / 2 - ((Layout)localObject).getHeight() / 2;
      paramCanvas.translate(n, i);
      ((Layout)localObject).draw(paramCanvas);
    }
    paramCanvas.restoreToCount(k);
  }
  
  @TargetApi(14)
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(SwitchCompat.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(SwitchCompat.class.getName());
      CharSequence localCharSequence1;
      if (!isChecked()) {
        localCharSequence1 = this.mTextOff;
      } else {
        localCharSequence1 = this.mTextOn;
      }
      if (!TextUtils.isEmpty(localCharSequence1))
      {
        CharSequence localCharSequence2 = paramAccessibilityNodeInfo.getText();
        if (!TextUtils.isEmpty(localCharSequence2))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(localCharSequence2).append(' ').append(localCharSequence1);
          paramAccessibilityNodeInfo.setText(localStringBuilder);
        }
        else
        {
          paramAccessibilityNodeInfo.setText(localCharSequence1);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mThumbDrawable != null)
    {
      Rect localRect = this.mTempRect;
      if (this.mTrackDrawable == null) {
        localRect.setEmpty();
      } else {
        this.mTrackDrawable.getPadding(localRect);
      }
    }
    int j;
    int m;
    if (!ViewUtils.isLayoutRtl(this))
    {
      j = 0 + (getWidth() - getPaddingRight());
      m = 0 + (0 + (j - this.mSwitchWidth));
    }
    else
    {
      m = 0 + getPaddingLeft();
      j = 0 + (0 + (m + this.mSwitchWidth));
    }
    int i;
    int k;
    switch (0x70 & getGravity())
    {
    default: 
      i = getPaddingTop();
      k = i + this.mSwitchHeight;
      break;
    case 16: 
      i = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - this.mSwitchHeight / 2;
      k = i + this.mSwitchHeight;
      break;
    case 80: 
      k = getHeight() - getPaddingBottom();
      i = k - this.mSwitchHeight;
    }
    this.mSwitchLeft = m;
    this.mSwitchTop = i;
    this.mSwitchBottom = k;
    this.mSwitchRight = j;
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mShowText)
    {
      if (this.mOnLayout == null) {
        this.mOnLayout = makeLayout(this.mTextOn);
      }
      if (this.mOffLayout == null) {
        this.mOffLayout = makeLayout(this.mTextOff);
      }
    }
    Rect localRect = this.mTempRect;
    int k;
    if (this.mThumbDrawable == null)
    {
      k = 0;
      i = 0;
    }
    else
    {
      this.mThumbDrawable.getPadding(localRect);
      k = this.mThumbDrawable.getIntrinsicWidth() - localRect.left - localRect.right;
      i = this.mThumbDrawable.getIntrinsicHeight();
    }
    if (!this.mShowText) {
      m = 0;
    } else {
      m = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + 2 * this.mThumbTextPadding;
    }
    this.mThumbWidth = Math.max(m, k);
    if (this.mTrackDrawable == null)
    {
      localRect.setEmpty();
      k = 0;
    }
    else
    {
      this.mTrackDrawable.getPadding(localRect);
      k = this.mTrackDrawable.getIntrinsicHeight();
    }
    int m = localRect.left;
    int j = localRect.right;
    j = Math.max(this.mSwitchMinWidth, j + (m + 2 * this.mThumbWidth));
    int i = Math.max(k, i);
    this.mSwitchWidth = j;
    this.mSwitchHeight = i;
    super.onMeasure(paramInt1, paramInt2);
    if (getMeasuredHeight() < i) {
      setMeasuredDimension(ViewCompat.getMeasuredWidthAndState(this), i);
    }
  }
  
  @TargetApi(14)
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEvent(paramAccessibilityEvent);
    CharSequence localCharSequence;
    if (!isChecked()) {
      localCharSequence = this.mTextOff;
    } else {
      localCharSequence = this.mTextOn;
    }
    if (localCharSequence != null) {
      paramAccessibilityEvent.getText().add(localCharSequence);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (MotionEventCompat.getActionMasked(paramMotionEvent))
    {
    case 0: 
      float f1 = paramMotionEvent.getX();
      f4 = paramMotionEvent.getY();
      if ((isEnabled()) && (hitThumb(f1, f4)))
      {
        this.mTouchMode = 1;
        this.mTouchX = f1;
        this.mTouchY = f4;
      }
      break;
    case 1: 
    case 3: 
      if (this.mTouchMode != 2)
      {
        this.mTouchMode = 0;
        this.mVelocityTracker.clear();
      }
      else
      {
        stopDrag(paramMotionEvent);
        super.onTouchEvent(paramMotionEvent);
        int i = 1;
      }
      break;
    case 2: 
      switch (this.mTouchMode)
      {
      default: 
        break;
      case 1: 
        f4 = paramMotionEvent.getX();
        float f2 = paramMotionEvent.getY();
        if ((Math.abs(f4 - this.mTouchX) > this.mTouchSlop) || (Math.abs(f2 - this.mTouchY) > this.mTouchSlop)) {
          break label214;
        }
      }
      break;
    }
    boolean bool1 = super.onTouchEvent(paramMotionEvent);
    return bool1;
    label214:
    this.mTouchMode = 2;
    getParent().requestDisallowInterceptTouchEvent(true);
    this.mTouchX = f4;
    this.mTouchY = bool1;
    return true;
    float f3 = paramMotionEvent.getX();
    int j = getThumbScrollRange();
    float f4 = f3 - this.mTouchX;
    if (j == 0)
    {
      if (f4 <= 0.0F) {
        f4 = -1.0F;
      } else {
        f4 = 1.0F;
      }
    }
    else {
      f4 /= j;
    }
    if (ViewUtils.isLayoutRtl(this)) {
      f4 = -f4;
    }
    f4 = constrain(f4 + this.mThumbPosition, 0.0F, 1.0F);
    if (f4 != this.mThumbPosition)
    {
      this.mTouchX = f3;
      setThumbPosition(f4);
    }
    boolean bool2 = true;
    return bool2;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    boolean bool = isChecked();
    float f;
    if (getWindowToken() == null)
    {
      cancelPositionAnimator();
      if (!bool) {
        f = 0.0F;
      } else {
        f = 1.0F;
      }
      setThumbPosition(f);
    }
    else
    {
      animateThumbToCheckedState(f);
    }
  }
  
  public void setShowText(boolean paramBoolean)
  {
    if (this.mShowText != paramBoolean)
    {
      this.mShowText = paramBoolean;
      requestLayout();
    }
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSwitchMinWidth(int paramInt)
  {
    this.mSwitchMinWidth = paramInt;
    requestLayout();
  }
  
  public void setSwitchPadding(int paramInt)
  {
    this.mSwitchPadding = paramInt;
    requestLayout();
  }
  
  public void setSwitchTextAppearance(Context paramContext, int paramInt)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramInt, TEXT_APPEARANCE_ATTRS);
    ColorStateList localColorStateList = localTypedArray.getColorStateList(0);
    if (localColorStateList == null) {
      this.mTextColors = getTextColors();
    } else {
      this.mTextColors = localColorStateList;
    }
    int i = localTypedArray.getDimensionPixelSize(1, 0);
    if ((i != 0) && (i != this.mTextPaint.getTextSize()))
    {
      this.mTextPaint.setTextSize(i);
      requestLayout();
    }
    if (!localTypedArray.getBoolean(2, false)) {
      this.mSwitchTransformationMethod = null;
    } else {
      this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
    }
    localTypedArray.recycle();
  }
  
  public void setSwitchTypeface(Typeface paramTypeface)
  {
    if (this.mTextPaint.getTypeface() != paramTypeface)
    {
      this.mTextPaint.setTypeface(paramTypeface);
      requestLayout();
      invalidate();
    }
  }
  
  public void setSwitchTypeface(Typeface paramTypeface, int paramInt)
  {
    boolean bool = false;
    if (paramInt <= 0)
    {
      this.mTextPaint.setFakeBoldText(false);
      this.mTextPaint.setTextSkewX(0.0F);
      setSwitchTypeface(paramTypeface);
    }
    else
    {
      Typeface localTypeface;
      if (paramTypeface != null) {
        localTypeface = Typeface.create(paramTypeface, paramInt);
      } else {
        localTypeface = Typeface.defaultFromStyle(paramInt);
      }
      setSwitchTypeface(localTypeface);
      if (localTypeface == null) {
        i = 0;
      } else {
        i = i.getStyle();
      }
      int i = paramInt & (i ^ 0xFFFFFFFF);
      TextPaint localTextPaint2 = this.mTextPaint;
      if ((i & 0x1) != 0) {
        bool = true;
      }
      localTextPaint2.setFakeBoldText(bool);
      TextPaint localTextPaint1 = this.mTextPaint;
      float f;
      if ((i & 0x2) == 0) {
        f = 0.0F;
      } else {
        f = -0.25F;
      }
      localTextPaint1.setTextSkewX(f);
    }
  }
  
  public void setTextOff(CharSequence paramCharSequence)
  {
    this.mTextOff = paramCharSequence;
    requestLayout();
  }
  
  public void setTextOn(CharSequence paramCharSequence)
  {
    this.mTextOn = paramCharSequence;
    requestLayout();
  }
  
  public void setThumbDrawable(Drawable paramDrawable)
  {
    this.mThumbDrawable = paramDrawable;
    requestLayout();
  }
  
  public void setThumbResource(int paramInt)
  {
    setThumbDrawable(this.mTintManager.getDrawable(paramInt));
  }
  
  public void setThumbTextPadding(int paramInt)
  {
    this.mThumbTextPadding = paramInt;
    requestLayout();
  }
  
  public void setTrackDrawable(Drawable paramDrawable)
  {
    this.mTrackDrawable = paramDrawable;
    requestLayout();
  }
  
  public void setTrackResource(int paramInt)
  {
    setTrackDrawable(this.mTintManager.getDrawable(paramInt));
  }
  
  public void toggle()
  {
    boolean bool;
    if (isChecked()) {
      bool = false;
    } else {
      bool = true;
    }
    setChecked(bool);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((!super.verifyDrawable(paramDrawable)) && (paramDrawable != this.mThumbDrawable) && (paramDrawable != this.mTrackDrawable)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\SwitchCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */