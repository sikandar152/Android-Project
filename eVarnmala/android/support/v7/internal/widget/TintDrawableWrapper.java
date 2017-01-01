package android.support.v7.internal.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

class TintDrawableWrapper
  extends DrawableWrapper
{
  private int mCurrentColor;
  private final PorterDuff.Mode mTintMode;
  private final ColorStateList mTintStateList;
  
  public TintDrawableWrapper(Drawable paramDrawable, ColorStateList paramColorStateList)
  {
    this(paramDrawable, paramColorStateList, TintManager.DEFAULT_MODE);
  }
  
  public TintDrawableWrapper(Drawable paramDrawable, ColorStateList paramColorStateList, PorterDuff.Mode paramMode)
  {
    super(paramDrawable);
    this.mTintStateList = paramColorStateList;
    this.mTintMode = paramMode;
  }
  
  private boolean updateTint(int[] paramArrayOfInt)
  {
    if (this.mTintStateList != null)
    {
      i = this.mTintStateList.getColorForState(paramArrayOfInt, this.mCurrentColor);
      if (i != this.mCurrentColor) {}
    }
    else
    {
      return 0;
    }
    if (i == 0) {
      clearColorFilter();
    } else {
      setColorFilter(i, this.mTintMode);
    }
    this.mCurrentColor = i;
    int i = 1;
    return i;
  }
  
  public boolean isStateful()
  {
    boolean bool;
    if (((this.mTintStateList == null) || (!this.mTintStateList.isStateful())) && (!super.isStateful())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean setState(int[] paramArrayOfInt)
  {
    boolean bool = super.setState(paramArrayOfInt);
    if ((!updateTint(paramArrayOfInt)) && (!bool)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\TintDrawableWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */