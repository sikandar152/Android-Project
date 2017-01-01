package android.support.v7.internal.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

class ActionBarBackgroundDrawable
  extends Drawable
{
  final ActionBarContainer mContainer;
  
  public ActionBarBackgroundDrawable(ActionBarContainer paramActionBarContainer)
  {
    this.mContainer = paramActionBarContainer;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (!this.mContainer.mIsSplit)
    {
      if (this.mContainer.mBackground != null) {
        this.mContainer.mBackground.draw(paramCanvas);
      }
      if ((this.mContainer.mStackedBackground != null) && (this.mContainer.mIsStacked)) {
        this.mContainer.mStackedBackground.draw(paramCanvas);
      }
    }
    else if (this.mContainer.mSplitBackground != null)
    {
      this.mContainer.mSplitBackground.draw(paramCanvas);
    }
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActionBarBackgroundDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */