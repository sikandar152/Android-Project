package android.support.v4.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

final class SwipeProgressBar
{
  private static final int ANIMATION_DURATION_MS = 2000;
  private static final int COLOR1 = -1291845632;
  private static final int COLOR2 = Integer.MIN_VALUE;
  private static final int COLOR3 = 1291845632;
  private static final int COLOR4 = 436207616;
  private static final int FINISH_ANIMATION_DURATION_MS = 1000;
  private static final Interpolator INTERPOLATOR = ;
  private Rect mBounds = new Rect();
  private final RectF mClipRect = new RectF();
  private int mColor1;
  private int mColor2;
  private int mColor3;
  private int mColor4;
  private long mFinishTime;
  private final Paint mPaint = new Paint();
  private View mParent;
  private boolean mRunning;
  private long mStartTime;
  private float mTriggerPercentage;
  
  public SwipeProgressBar(View paramView)
  {
    this.mParent = paramView;
    this.mColor1 = -1291845632;
    this.mColor2 = Integer.MIN_VALUE;
    this.mColor3 = 1291845632;
    this.mColor4 = 436207616;
  }
  
  private void drawCircle(Canvas paramCanvas, float paramFloat1, float paramFloat2, int paramInt, float paramFloat3)
  {
    this.mPaint.setColor(paramInt);
    paramCanvas.save();
    paramCanvas.translate(paramFloat1, paramFloat2);
    float f = INTERPOLATOR.getInterpolation(paramFloat3);
    paramCanvas.scale(f, f);
    paramCanvas.drawCircle(0.0F, 0.0F, paramFloat1, this.mPaint);
    paramCanvas.restore();
  }
  
  private void drawTrigger(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    this.mPaint.setColor(this.mColor1);
    paramCanvas.drawCircle(paramInt1, paramInt2, paramInt1 * this.mTriggerPercentage, this.mPaint);
  }
  
  void draw(Canvas paramCanvas)
  {
    int i2 = this.mBounds.width();
    int i1 = this.mBounds.height();
    int j = i2 / 2;
    int k = i1 / 2;
    int m = 0;
    int i = paramCanvas.save();
    paramCanvas.clipRect(this.mBounds);
    if ((!this.mRunning) && (this.mFinishTime <= 0L))
    {
      if ((this.mTriggerPercentage > 0.0F) && (this.mTriggerPercentage <= 1.0D)) {
        drawTrigger(paramCanvas, j, k);
      }
    }
    else
    {
      long l2 = AnimationUtils.currentAnimationTimeMillis();
      long l3 = (l2 - this.mStartTime) % 2000L;
      long l1 = (l2 - this.mStartTime) / 2000L;
      float f2 = (float)l3 / 20.0F;
      int n;
      if (!this.mRunning)
      {
        if (l2 - this.mFinishTime >= 1000L) {
          break label647;
        }
        float f1 = (float)((l2 - this.mFinishTime) % 1000L) / 10.0F / 100.0F;
        f1 = i2 / 2 * INTERPOLATOR.getInterpolation(f1);
        this.mClipRect.set(j - f1, 0.0F, f1 + j, i1);
        paramCanvas.saveLayerAlpha(this.mClipRect, 0, 0);
        n = 1;
      }
      if (l1 != 0L)
      {
        if ((f2 < 0.0F) || (f2 >= 25.0F))
        {
          if ((f2 < 25.0F) || (f2 >= 50.0F))
          {
            if ((f2 < 50.0F) || (f2 >= 75.0F)) {
              paramCanvas.drawColor(this.mColor3);
            } else {
              paramCanvas.drawColor(this.mColor2);
            }
          }
          else {
            paramCanvas.drawColor(this.mColor1);
          }
        }
        else {
          paramCanvas.drawColor(this.mColor4);
        }
      }
      else {
        paramCanvas.drawColor(this.mColor1);
      }
      float f3;
      if ((f2 >= 0.0F) && (f2 <= 25.0F))
      {
        f3 = 2.0F * (25.0F + f2) / 100.0F;
        drawCircle(paramCanvas, j, k, this.mColor1, f3);
      }
      if ((f2 >= 0.0F) && (f2 <= 50.0F))
      {
        f3 = 2.0F * f2 / 100.0F;
        drawCircle(paramCanvas, j, k, this.mColor2, f3);
      }
      if ((f2 >= 25.0F) && (f2 <= 75.0F))
      {
        f3 = 2.0F * (f2 - 25.0F) / 100.0F;
        drawCircle(paramCanvas, j, k, this.mColor3, f3);
      }
      if ((f2 >= 50.0F) && (f2 <= 100.0F))
      {
        f3 = 2.0F * (f2 - 50.0F) / 100.0F;
        drawCircle(paramCanvas, j, k, this.mColor4, f3);
      }
      if ((f2 >= 75.0F) && (f2 <= 100.0F))
      {
        f2 = 2.0F * (f2 - 75.0F) / 100.0F;
        drawCircle(paramCanvas, j, k, this.mColor1, f2);
      }
      if ((this.mTriggerPercentage > 0.0F) && (n != 0))
      {
        paramCanvas.restoreToCount(i);
        i = paramCanvas.save();
        paramCanvas.clipRect(this.mBounds);
        drawTrigger(paramCanvas, j, k);
      }
      ViewCompat.postInvalidateOnAnimation(this.mParent, this.mBounds.left, this.mBounds.top, this.mBounds.right, this.mBounds.bottom);
    }
    paramCanvas.restoreToCount(i);
    return;
    label647:
    this.mFinishTime = 0L;
  }
  
  boolean isRunning()
  {
    boolean bool;
    if ((!this.mRunning) && (this.mFinishTime <= 0L)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mBounds.left = paramInt1;
    this.mBounds.top = paramInt2;
    this.mBounds.right = paramInt3;
    this.mBounds.bottom = paramInt4;
  }
  
  void setColorScheme(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mColor1 = paramInt1;
    this.mColor2 = paramInt2;
    this.mColor3 = paramInt3;
    this.mColor4 = paramInt4;
  }
  
  void setTriggerPercentage(float paramFloat)
  {
    this.mTriggerPercentage = paramFloat;
    this.mStartTime = 0L;
    ViewCompat.postInvalidateOnAnimation(this.mParent, this.mBounds.left, this.mBounds.top, this.mBounds.right, this.mBounds.bottom);
  }
  
  void start()
  {
    if (!this.mRunning)
    {
      this.mTriggerPercentage = 0.0F;
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mRunning = true;
      this.mParent.postInvalidate();
    }
  }
  
  void stop()
  {
    if (this.mRunning)
    {
      this.mTriggerPercentage = 0.0F;
      this.mFinishTime = AnimationUtils.currentAnimationTimeMillis();
      this.mRunning = false;
      this.mParent.postInvalidate();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\SwipeProgressBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */