package android.support.v4.media.routing;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class MediaRouterJellybeanMr1
  extends MediaRouterJellybean
{
  private static final String TAG = "MediaRouterJellybeanMr1";
  
  public static Object createCallback(Callback paramCallback)
  {
    return new CallbackProxy(paramCallback);
  }
  
  static class CallbackProxy<T extends MediaRouterJellybeanMr1.Callback>
    extends MediaRouterJellybean.CallbackProxy<T>
  {
    public CallbackProxy(T paramT)
    {
      super();
    }
    
    public void onRoutePresentationDisplayChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      ((MediaRouterJellybeanMr1.Callback)this.mCallback).onRoutePresentationDisplayChanged(paramRouteInfo);
    }
  }
  
  public static final class IsConnectingWorkaround
  {
    private Method mGetStatusCodeMethod;
    private int mStatusConnecting;
    
    public IsConnectingWorkaround()
    {
      if (Build.VERSION.SDK_INT != 17) {
        throw new UnsupportedOperationException();
      }
      try
      {
        this.mStatusConnecting = MediaRouter.RouteInfo.class.getField("STATUS_CONNECTING").getInt(null);
        this.mGetStatusCodeMethod = MediaRouter.RouteInfo.class.getMethod("getStatusCode", new Class[0]);
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;) {}
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;) {}
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;) {}
      }
    }
    
    public boolean isConnecting(Object paramObject)
    {
      MediaRouter.RouteInfo localRouteInfo = (MediaRouter.RouteInfo)paramObject;
      if (this.mGetStatusCodeMethod != null) {
        for (;;)
        {
          try
          {
            int k = ((Integer)this.mGetStatusCodeMethod.invoke(localRouteInfo, new Object[0])).intValue();
            i = this.mStatusConnecting;
            if (k != i) {
              continue;
            }
            i = 1;
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            int i;
            int j = 0;
            continue;
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            continue;
          }
          return i;
          j = 0;
        }
      }
    }
  }
  
  public static final class ActiveScanWorkaround
    implements Runnable
  {
    private static final int WIFI_DISPLAY_SCAN_INTERVAL = 15000;
    private boolean mActivelyScanningWifiDisplays;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;
    private Method mScanWifiDisplaysMethod;
    
    public ActiveScanWorkaround(Context paramContext, Handler paramHandler)
    {
      if (Build.VERSION.SDK_INT != 17) {
        throw new UnsupportedOperationException();
      }
      this.mDisplayManager = ((DisplayManager)paramContext.getSystemService("display"));
      this.mHandler = paramHandler;
      try
      {
        this.mScanWifiDisplaysMethod = DisplayManager.class.getMethod("scanWifiDisplays", new Class[0]);
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;) {}
      }
    }
    
    public void run()
    {
      if (this.mActivelyScanningWifiDisplays) {}
      try
      {
        this.mScanWifiDisplaysMethod.invoke(this.mDisplayManager, new Object[0]);
        this.mHandler.postDelayed(this, 15000L);
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;)
        {
          Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays.", localIllegalAccessException);
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        for (;;)
        {
          Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays.", localInvocationTargetException);
        }
      }
    }
    
    public void setActiveScanRouteTypes(int paramInt)
    {
      if ((paramInt & 0x2) == 0)
      {
        if (this.mActivelyScanningWifiDisplays)
        {
          this.mActivelyScanningWifiDisplays = false;
          this.mHandler.removeCallbacks(this);
        }
      }
      else if (!this.mActivelyScanningWifiDisplays) {
        if (this.mScanWifiDisplaysMethod == null)
        {
          Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays because the DisplayManager.scanWifiDisplays() method is not available on this device.");
        }
        else
        {
          this.mActivelyScanningWifiDisplays = true;
          this.mHandler.post(this);
        }
      }
    }
  }
  
  public static abstract interface Callback
    extends MediaRouterJellybean.Callback
  {
    public abstract void onRoutePresentationDisplayChanged(Object paramObject);
  }
  
  public static final class RouteInfo
  {
    public static Display getPresentationDisplay(Object paramObject)
    {
      return ((MediaRouter.RouteInfo)paramObject).getPresentationDisplay();
    }
    
    public static boolean isEnabled(Object paramObject)
    {
      return ((MediaRouter.RouteInfo)paramObject).isEnabled();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\media\routing\MediaRouterJellybeanMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */