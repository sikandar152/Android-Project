package android.support.v7.internal.widget;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

class ActionBarBackgroundDrawableV21
  extends ActionBarBackgroundDrawable
{
  public ActionBarBackgroundDrawableV21(ActionBarContainer paramActionBarContainer)
  {
    super(paramActionBarContainer);
  }
  
  public void getOutline(@NonNull Outline paramOutline)
  {
    if (!this.mContainer.mIsSplit)
    {
      if (this.mContainer.mBackground != null) {
        this.mContainer.mBackground.getOutline(paramOutline);
      }
    }
    else if (this.mContainer.mSplitBackground != null) {
      this.mContainer.mSplitBackground.getOutline(paramOutline);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActionBarBackgroundDrawableV21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */