package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

class CircleImageView
  extends ImageView
{
  private static final int FILL_SHADOW_COLOR = 1023410176;
  private static final int KEY_SHADOW_COLOR = 503316480;
  private static final int SHADOW_ELEVATION = 4;
  private static final float SHADOW_RADIUS = 3.5F;
  private static final float X_OFFSET = 0.0F;
  private static final float Y_OFFSET = 1.75F;
  private Animation.AnimationListener mListener;
  private int mShadowRadius;
  
  public CircleImageView(Context paramContext, int paramInt, float paramFloat)
  {
    super(paramContext);
    float f = getContext().getResources().getDisplayMetrics().density;
    int i = (int)(2.0F * (paramFloat * f));
    int k = (int)(1.75F * f);
    int m = (int)(0.0F * f);
    this.mShadowRadius = ((int)(3.5F * f));
    ShapeDrawable localShapeDrawable;
    int j;
    if (!elevationSupported())
    {
      localShapeDrawable = new ShapeDrawable(new OvalShadow(this.mShadowRadius, i));
      ViewCompat.setLayerType(this, 1, localShapeDrawable.getPaint());
      localShapeDrawable.getPaint().setShadowLayer(this.mShadowRadius, m, k, 503316480);
      j = this.mShadowRadius;
      setPadding(j, j, j, j);
    }
    else
    {
      localShapeDrawable = new ShapeDrawable(new OvalShape());
      ViewCompat.setElevation(this, 4.0F * j);
    }
    localShapeDrawable.getPaint().setColor(paramInt);
    setBackgroundDrawable(localShapeDrawable);
  }
  
  private boolean elevationSupported()
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 21) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void onAnimationEnd()
  {
    super.onAnimationEnd();
    if (this.mListener != null) {
      this.mListener.onAnimationEnd(getAnimation());
    }
  }
  
  public void onAnimationStart()
  {
    super.onAnimationStart();
    if (this.mListener != null) {
      this.mListener.onAnimationStart(getAnimation());
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (!elevationSupported()) {
      setMeasuredDimension(getMeasuredWidth() + 2 * this.mShadowRadius, getMeasuredHeight() + 2 * this.mShadowRadius);
    }
  }
  
  public void setAnimationListener(Animation.AnimationListener paramAnimationListener)
  {
    this.mListener = paramAnimationListener;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    if ((getBackground() instanceof ShapeDrawable))
    {
      Resources localResources = getResources();
      ((ShapeDrawable)getBackground()).getPaint().setColor(localResources.getColor(paramInt));
    }
  }
  
  private class OvalShadow
    extends OvalShape
  {
    private int mCircleDiameter;
    private RadialGradient mRadialGradient;
    private Paint mShadowPaint = new Paint();
    private int mShadowRadius;
    
    public OvalShadow(int paramInt1, int paramInt2)
    {
      this.mShadowRadius = paramInt1;
      this.mCircleDiameter = paramInt2;
      float f1 = this.mCircleDiameter / 2;
      float f3 = this.mCircleDiameter / 2;
      float f2 = this.mShadowRadius;
      int[] arrayOfInt = new int[2];
      arrayOfInt[0] = 1023410176;
      arrayOfInt[1] = 0;
      this.mRadialGradient = new RadialGradient(f1, f3, f2, arrayOfInt, null, Shader.TileMode.CLAMP);
      this.mShadowPaint.setShader(this.mRadialGradient);
    }
    
    public void draw(Canvas paramCanvas, Paint paramPaint)
    {
      int i = CircleImageView.this.getWidth();
      int j = CircleImageView.this.getHeight();
      paramCanvas.drawCircle(i / 2, j / 2, this.mCircleDiameter / 2 + this.mShadowRadius, this.mShadowPaint);
      paramCanvas.drawCircle(i / 2, j / 2, this.mCircleDiameter / 2, paramPaint);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\CircleImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */