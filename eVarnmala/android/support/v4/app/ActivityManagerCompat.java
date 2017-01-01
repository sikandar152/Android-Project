package android.support.v4.app;

import android.app.ActivityManager;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

public final class ActivityManagerCompat
{
  public static boolean isLowRamDevice(@NonNull ActivityManager paramActivityManager)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 19) {
      bool = false;
    } else {
      bool = ActivityManagerCompatKitKat.isLowRamDevice(paramActivityManager);
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\ActivityManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */