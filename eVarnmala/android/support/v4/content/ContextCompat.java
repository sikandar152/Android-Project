package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;

public class ContextCompat
{
  private static final String DIR_ANDROID = "Android";
  private static final String DIR_CACHE = "cache";
  private static final String DIR_DATA = "data";
  private static final String DIR_FILES = "files";
  private static final String DIR_OBB = "obb";
  private static final String TAG = "ContextCompat";
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int i = paramVarArgs.length;
    int j = 0;
    for (File localFile = paramFile;; localFile = localFile)
    {
      if (j >= i) {
        return localFile;
      }
      String str = paramVarArgs[j];
      if (localFile != null)
      {
        if (str == null) {
          localFile = localFile;
        } else {
          localFile = new File(localFile, str);
        }
      }
      else {
        localFile = new File(str);
      }
      j++;
    }
  }
  
  /* Error */
  /**
   * @deprecated
   */
  private static File createFilesDir(File paramFile)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: invokevirtual 43	java/io/File:exists	()Z
    //   7: ifne +19 -> 26
    //   10: aload_0
    //   11: invokevirtual 46	java/io/File:mkdirs	()Z
    //   14: ifne +12 -> 26
    //   17: aload_0
    //   18: invokevirtual 43	java/io/File:exists	()Z
    //   21: istore_1
    //   22: iload_1
    //   23: ifeq +8 -> 31
    //   26: ldc 2
    //   28: monitorexit
    //   29: aload_0
    //   30: areturn
    //   31: ldc 23
    //   33: new 48	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 49	java/lang/StringBuilder:<init>	()V
    //   40: ldc 51
    //   42: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_0
    //   46: invokevirtual 59	java/io/File:getPath	()Ljava/lang/String;
    //   49: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 68	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aconst_null
    //   60: astore_0
    //   61: goto -35 -> 26
    //   64: astore_1
    //   65: ldc 2
    //   67: monitorexit
    //   68: aload_1
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	paramFile	File
    //   21	2	1	bool	boolean
    //   64	5	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	22	64	finally
    //   31	59	64	finally
  }
  
  public static final Drawable getDrawable(Context paramContext, int paramInt)
  {
    Drawable localDrawable;
    if (Build.VERSION.SDK_INT < 21) {
      localDrawable = paramContext.getResources().getDrawable(paramInt);
    } else {
      localDrawable = ContextCompatApi21.getDrawable(paramContext, paramInt);
    }
    return localDrawable;
  }
  
  public static File[] getExternalCacheDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    Object localObject1;
    if (i < 19)
    {
      Object localObject2;
      if (i < 8)
      {
        localObject1 = Environment.getExternalStorageDirectory();
        localObject2 = new String[4];
        localObject2[0] = "Android";
        localObject2[1] = "data";
        localObject2[2] = paramContext.getPackageName();
        localObject2[3] = "cache";
        localObject2 = buildPath((File)localObject1, (String[])localObject2);
      }
      else
      {
        localObject2 = ContextCompatFroyo.getExternalCacheDir(paramContext);
      }
      localObject1 = new File[1];
      localObject1[0] = localObject2;
    }
    else
    {
      localObject1 = ContextCompatKitKat.getExternalCacheDirs(paramContext);
    }
    return (File[])localObject1;
  }
  
  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    Object localObject;
    if (i < 19)
    {
      File localFile;
      if (i < 8)
      {
        localFile = Environment.getExternalStorageDirectory();
        localObject = new String[5];
        localObject[0] = "Android";
        localObject[1] = "data";
        localObject[2] = paramContext.getPackageName();
        localObject[3] = "files";
        localObject[4] = paramString;
        localFile = buildPath(localFile, (String[])localObject);
      }
      else
      {
        localFile = ContextCompatFroyo.getExternalFilesDir(paramContext, paramString);
      }
      localObject = new File[1];
      localObject[0] = localFile;
    }
    else
    {
      localObject = ContextCompatKitKat.getExternalFilesDirs(paramContext, paramString);
    }
    return (File[])localObject;
  }
  
  public static File[] getObbDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    Object localObject;
    if (i < 19)
    {
      File localFile;
      if (i < 11)
      {
        localFile = Environment.getExternalStorageDirectory();
        localObject = new String[3];
        localObject[0] = "Android";
        localObject[1] = "obb";
        localObject[2] = paramContext.getPackageName();
        localFile = buildPath(localFile, (String[])localObject);
      }
      else
      {
        localFile = ContextCompatHoneycomb.getObbDir(paramContext);
      }
      localObject = new File[1];
      localObject[0] = localFile;
    }
    else
    {
      localObject = ContextCompatKitKat.getObbDirs(paramContext);
    }
    return (File[])localObject;
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent)
  {
    return startActivities(paramContext, paramArrayOfIntent, null);
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    boolean bool = true;
    int i = Build.VERSION.SDK_INT;
    if (i < 16)
    {
      if (i < 11) {
        bool = false;
      } else {
        ContextCompatHoneycomb.startActivities(paramContext, paramArrayOfIntent);
      }
    }
    else {
      ContextCompatJellybean.startActivities(paramContext, paramArrayOfIntent, paramBundle);
    }
    return bool;
  }
  
  public final File getCodeCacheDir(Context paramContext)
  {
    File localFile;
    if (Build.VERSION.SDK_INT < 21) {
      localFile = createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "code_cache"));
    } else {
      localFile = ContextCompatApi21.getCodeCacheDir(paramContext);
    }
    return localFile;
  }
  
  public final File getNoBackupFilesDir(Context paramContext)
  {
    File localFile;
    if (Build.VERSION.SDK_INT < 21) {
      localFile = createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "no_backup"));
    } else {
      localFile = ContextCompatApi21.getNoBackupFilesDir(paramContext);
    }
    return localFile;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\content\ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */