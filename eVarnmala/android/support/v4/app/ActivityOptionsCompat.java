package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

public class ActivityOptionsCompat
{
  public static ActivityOptionsCompat makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 16) {
      localObject = new ActivityOptionsCompat();
    } else {
      localObject = new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeCustomAnimation(paramContext, paramInt1, paramInt2));
    }
    return (ActivityOptionsCompat)localObject;
  }
  
  public static ActivityOptionsCompat makeScaleUpAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 16) {
      localObject = new ActivityOptionsCompat();
    } else {
      localObject = new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeScaleUpAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
    }
    return (ActivityOptionsCompat)localObject;
  }
  
  public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity paramActivity, View paramView, String paramString)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 21) {
      localObject = new ActivityOptionsCompat();
    } else {
      localObject = new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(paramActivity, paramView, paramString));
    }
    return (ActivityOptionsCompat)localObject;
  }
  
  public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity paramActivity, Pair<View, String>... paramVarArgs)
  {
    Object localObject;
    String[] arrayOfString;
    if (Build.VERSION.SDK_INT < 21)
    {
      localObject = new ActivityOptionsCompat();
    }
    else
    {
      localObject = null;
      arrayOfString = null;
      if (paramVarArgs != null)
      {
        localObject = new View[paramVarArgs.length];
        arrayOfString = new String[paramVarArgs.length];
      }
    }
    for (int i = 0;; i++)
    {
      if (i >= paramVarArgs.length)
      {
        localObject = new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(paramActivity, (View[])localObject, arrayOfString));
        return (ActivityOptionsCompat)localObject;
      }
      localObject[i] = ((View)paramVarArgs[i].first);
      arrayOfString[i] = ((String)paramVarArgs[i].second);
    }
  }
  
  public static ActivityOptionsCompat makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 16) {
      localObject = new ActivityOptionsCompat();
    } else {
      localObject = new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeThumbnailScaleUpAnimation(paramView, paramBitmap, paramInt1, paramInt2));
    }
    return (ActivityOptionsCompat)localObject;
  }
  
  public Bundle toBundle()
  {
    return null;
  }
  
  public void update(ActivityOptionsCompat paramActivityOptionsCompat) {}
  
  private static class ActivityOptionsImpl21
    extends ActivityOptionsCompat
  {
    private final ActivityOptionsCompat21 mImpl;
    
    ActivityOptionsImpl21(ActivityOptionsCompat21 paramActivityOptionsCompat21)
    {
      this.mImpl = paramActivityOptionsCompat21;
    }
    
    public Bundle toBundle()
    {
      return this.mImpl.toBundle();
    }
    
    public void update(ActivityOptionsCompat paramActivityOptionsCompat)
    {
      if ((paramActivityOptionsCompat instanceof ActivityOptionsImpl21))
      {
        ActivityOptionsImpl21 localActivityOptionsImpl21 = (ActivityOptionsImpl21)paramActivityOptionsCompat;
        this.mImpl.update(localActivityOptionsImpl21.mImpl);
      }
    }
  }
  
  private static class ActivityOptionsImplJB
    extends ActivityOptionsCompat
  {
    private final ActivityOptionsCompatJB mImpl;
    
    ActivityOptionsImplJB(ActivityOptionsCompatJB paramActivityOptionsCompatJB)
    {
      this.mImpl = paramActivityOptionsCompatJB;
    }
    
    public Bundle toBundle()
    {
      return this.mImpl.toBundle();
    }
    
    public void update(ActivityOptionsCompat paramActivityOptionsCompat)
    {
      if ((paramActivityOptionsCompat instanceof ActivityOptionsImplJB))
      {
        ActivityOptionsImplJB localActivityOptionsImplJB = (ActivityOptionsImplJB)paramActivityOptionsCompat;
        this.mImpl.update(localActivityOptionsImplJB.mImpl);
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\ActivityOptionsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */