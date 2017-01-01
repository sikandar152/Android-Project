package android.support.v7.app;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.style;
import android.support.v7.appcompat.R.styleable;

abstract class DrawerArrowDrawable
  extends Drawable
{
  private static final float ARROW_HEAD_ANGLE = (float)Math.toRadians(45.0D);
  private final float mBarGap;
  private final float mBarSize;
  private final float mBarThickness;
  private final float mMiddleArrowSize;
  private final Paint mPaint = new Paint();
  private final Path mPath = new Path();
  private float mProgress;
  private final int mSize;
  private final boolean mSpin;
  private final float mTopBottomArrowSize;
  private boolean mVerticalMirror = false;
  
  DrawerArrowDrawable(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, R.attr.drawerArrowStyle, R.style.Base_Widget_AppCompat_DrawerArrowToggle);
    this.mPaint.setAntiAlias(true);
    this.mPaint.setColor(localTypedArray.getColor(R.styleable.DrawerArrowToggle_color, 0));
    this.mSize = localTypedArray.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
    this.mBarSize = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_barSize, 0.0F);
    this.mTopBottomArrowSize = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_topBottomBarArrowSize, 0.0F);
    this.mBarThickness = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0F);
    this.mBarGap = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0F);
    this.mSpin = localTypedArray.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true);
    this.mMiddleArrowSize = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_middleBarArrowSize, 0.0F);
    localTypedArray.recycle();
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mPaint.setStrokeJoin(Paint.Join.ROUND);
    this.mPaint.setStrokeCap(Paint.Cap.SQUARE);
    this.mPaint.setStrokeWidth(this.mBarThickness);
  }
  
  private static float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat1 + paramFloat3 * (paramFloat2 - paramFloat1);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    boolean bool = isLayoutRtl();
    float f2 = lerp(this.mBarSize, this.mTopBottomArrowSize, this.mProgress);
    float f4 = lerp(this.mBarSize, this.mMiddleArrowSize, this.mProgress);
    float f6 = lerp(0.0F, this.mBarThickness / 2.0F, this.mProgress);
    float f1 = lerp(0.0F, ARROW_HEAD_ANGLE, this.mProgress);
    if (!bool) {
      f5 = -180.0F;
    } else {
      f5 = 0.0F;
    }
    if (!bool) {
      f3 = 0.0F;
    } else {
      f3 = 180.0F;
    }
    float f3 = lerp(f5, f3, this.mProgress);
    float f7 = lerp(this.mBarGap + this.mBarThickness, 0.0F, this.mProgress);
    this.mPath.rewind();
    float f5 = -f4 / 2.0F;
    this.mPath.moveTo(f5 + f6, 0.0F);
    this.mPath.rLineTo(f4 - f6, 0.0F);
    f4 = (float)Math.round(f2 * Math.cos(f1));
    f1 = (float)Math.round(f2 * Math.sin(f1));
    this.mPath.moveTo(f5, f7);
    this.mPath.rLineTo(f4, f1);
    this.mPath.moveTo(f5, -f7);
    this.mPath.rLineTo(f4, -f1);
    this.mPath.moveTo(0.0F, 0.0F);
    this.mPath.close();
    paramCanvas.save();
    if (!this.mSpin)
    {
      if (bool) {
        paramCanvas.rotate(180.0F, localRect.centerX(), localRect.centerY());
      }
    }
    else
    {
      int i;
      if (!(bool ^ this.mVerticalMirror)) {
        bool = true;
      } else {
        i = -1;
      }
      paramCanvas.rotate(f3 * i, localRect.centerX(), localRect.centerY());
    }
    paramCanvas.translate(localRect.centerX(), localRect.centerY());
    paramCanvas.drawPath(this.mPath, this.mPaint);
    paramCanvas.restore();
  }
  
  public int getIntrinsicHeight()
  {
    return this.mSize;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mSize;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public float getProgress()
  {
    return this.mProgress;
  }
  
  public boolean isAutoMirrored()
  {
    return true;
  }
  
  abstract boolean isLayoutRtl();
  
  public void setAlpha(int paramInt)
  {
    this.mPaint.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mPaint.setColorFilter(paramColorFilter);
  }
  
  public void setProgress(float paramFloat)
  {
    this.mProgress = paramFloat;
    invalidateSelf();
  }
  
  protected void setVerticalMirror(boolean paramBoolean)
  {
    this.mVerticalMirror = paramBoolean;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\DrawerArrowDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */