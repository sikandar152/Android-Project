package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import java.util.List;
import java.util.Map;

public class ActivityCompat
  extends ContextCompat
{
  private static ActivityCompat21.SharedElementCallback21 createCallback(SharedElementCallback paramSharedElementCallback)
  {
    SharedElementCallback21Impl localSharedElementCallback21Impl = null;
    if (paramSharedElementCallback != null) {
      localSharedElementCallback21Impl = new SharedElementCallback21Impl(paramSharedElementCallback);
    }
    return localSharedElementCallback21Impl;
  }
  
  public static void finishAffinity(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT < 16) {
      paramActivity.finish();
    } else {
      ActivityCompatJB.finishAffinity(paramActivity);
    }
  }
  
  public static void finishAfterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT < 21) {
      paramActivity.finish();
    } else {
      ActivityCompat21.finishAfterTransition(paramActivity);
    }
  }
  
  public static boolean invalidateOptionsMenu(Activity paramActivity)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 11)
    {
      bool = false;
    }
    else
    {
      ActivityCompatHoneycomb.invalidateOptionsMenu(paramActivity);
      bool = true;
    }
    return bool;
  }
  
  public static void postponeEnterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.postponeEnterTransition(paramActivity);
    }
  }
  
  public static void setEnterSharedElementCallback(Activity paramActivity, SharedElementCallback paramSharedElementCallback)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.setEnterSharedElementCallback(paramActivity, createCallback(paramSharedElementCallback));
    }
  }
  
  public static void setExitSharedElementCallback(Activity paramActivity, SharedElementCallback paramSharedElementCallback)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.setExitSharedElementCallback(paramActivity, createCallback(paramSharedElementCallback));
    }
  }
  
  public static void startActivity(Activity paramActivity, Intent paramIntent, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT < 16) {
      paramActivity.startActivity(paramIntent);
    } else {
      ActivityCompatJB.startActivity(paramActivity, paramIntent, paramBundle);
    }
  }
  
  public static void startActivityForResult(Activity paramActivity, Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT < 16) {
      paramActivity.startActivityForResult(paramIntent, paramInt);
    } else {
      ActivityCompatJB.startActivityForResult(paramActivity, paramIntent, paramInt, paramBundle);
    }
  }
  
  public static void startPostponedEnterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.startPostponedEnterTransition(paramActivity);
    }
  }
  
  private static class SharedElementCallback21Impl
    extends ActivityCompat21.SharedElementCallback21
  {
    private SharedElementCallback mCallback;
    
    public SharedElementCallback21Impl(SharedElementCallback paramSharedElementCallback)
    {
      this.mCallback = paramSharedElementCallback;
    }
    
    public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
    {
      return this.mCallback.onCaptureSharedElementSnapshot(paramView, paramMatrix, paramRectF);
    }
    
    public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
    {
      return this.mCallback.onCreateSnapshotView(paramContext, paramParcelable);
    }
    
    public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap)
    {
      this.mCallback.onMapSharedElements(paramList, paramMap);
    }
    
    public void onRejectSharedElements(List<View> paramList)
    {
      this.mCallback.onRejectSharedElements(paramList);
    }
    
    public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      this.mCallback.onSharedElementEnd(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      this.mCallback.onSharedElementStart(paramList, paramList1, paramList2);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\ActivityCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */