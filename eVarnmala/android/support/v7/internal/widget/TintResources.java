package android.support.v7.internal.widget;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;

class TintResources
  extends Resources
{
  private final TintManager mTintManager;
  
  public TintResources(Resources paramResources, TintManager paramTintManager)
  {
    super(paramResources.getAssets(), paramResources.getDisplayMetrics(), paramResources.getConfiguration());
    this.mTintManager = paramTintManager;
  }
  
  public Drawable getDrawable(int paramInt)
    throws Resources.NotFoundException
  {
    Drawable localDrawable = super.getDrawable(paramInt);
    if (localDrawable != null) {
      this.mTintManager.tintDrawable(paramInt, localDrawable);
    }
    return localDrawable;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\TintResources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */