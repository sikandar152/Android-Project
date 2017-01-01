package android.support.v7.internal;

import android.os.Build.VERSION;

public class VersionUtils
{
  public static boolean isAtLeastL()
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 21) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\VersionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */