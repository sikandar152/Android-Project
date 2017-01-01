package android.support.v4.app;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.Arrays;

class BundleUtil
{
  public static Bundle[] getBundleArrayFromBundle(Bundle paramBundle, String paramString)
  {
    Object localObject = paramBundle.getParcelableArray(paramString);
    if ((!(localObject instanceof Bundle[])) && (localObject != null))
    {
      localObject = (Bundle[])Arrays.copyOf((Object[])localObject, localObject.length, Bundle[].class);
      paramBundle.putParcelableArray(paramString, (Parcelable[])localObject);
    }
    else
    {
      localObject = (Bundle[])localObject;
    }
    return (Bundle[])localObject;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\BundleUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */