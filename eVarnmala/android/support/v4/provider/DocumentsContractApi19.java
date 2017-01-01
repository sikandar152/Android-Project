package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;

class DocumentsContractApi19
{
  private static final String TAG = "DocumentFile";
  
  public static boolean canRead(Context paramContext, Uri paramUri)
  {
    boolean bool = false;
    if ((paramContext.checkCallingOrSelfUriPermission(paramUri, 1) == 0) && (!TextUtils.isEmpty(getRawType(paramContext, paramUri)))) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean canWrite(Context paramContext, Uri paramUri)
  {
    boolean bool = false;
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 2) == 0)
    {
      String str = getRawType(paramContext, paramUri);
      int i = queryForInt(paramContext, paramUri, "flags", 0);
      if (!TextUtils.isEmpty(str)) {
        if ((i & 0x4) == 0)
        {
          if ((!"vnd.android.document/directory".equals(str)) || ((i & 0x8) == 0))
          {
            if ((!TextUtils.isEmpty(str)) && ((i & 0x2) != 0)) {
              bool = true;
            }
          }
          else {
            bool = true;
          }
        }
        else {
          bool = true;
        }
      }
    }
    return bool;
  }
  
  private static void closeQuietly(AutoCloseable paramAutoCloseable)
  {
    if (paramAutoCloseable != null) {}
    try
    {
      paramAutoCloseable.close();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public static boolean delete(Context paramContext, Uri paramUri)
  {
    return DocumentsContract.deleteDocument(paramContext.getContentResolver(), paramUri);
  }
  
  public static boolean exists(Context paramContext, Uri paramUri)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    localCursor = null;
    for (;;)
    {
      try
      {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "document_id";
        localCursor = localContentResolver.query(paramUri, arrayOfString, null, null, null);
        i = localCursor.getCount();
        if (i <= 0) {
          continue;
        }
        i = 1;
      }
      catch (Exception localException)
      {
        int i;
        int j;
        Log.w("DocumentFile", "Failed query: " + localException);
        closeQuietly(localCursor);
        int k = 0;
        continue;
      }
      finally
      {
        closeQuietly(localCursor);
      }
      return i;
      j = 0;
    }
  }
  
  public static String getName(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "_display_name", null);
  }
  
  private static String getRawType(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "mime_type", null);
  }
  
  public static String getType(Context paramContext, Uri paramUri)
  {
    String str = getRawType(paramContext, paramUri);
    if ("vnd.android.document/directory".equals(str)) {
      str = null;
    }
    return str;
  }
  
  public static boolean isDirectory(Context paramContext, Uri paramUri)
  {
    return "vnd.android.document/directory".equals(getRawType(paramContext, paramUri));
  }
  
  public static boolean isDocumentUri(Context paramContext, Uri paramUri)
  {
    return DocumentsContract.isDocumentUri(paramContext, paramUri);
  }
  
  public static boolean isFile(Context paramContext, Uri paramUri)
  {
    String str = getRawType(paramContext, paramUri);
    boolean bool;
    if ((!"vnd.android.document/directory".equals(str)) && (!TextUtils.isEmpty(str))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static long lastModified(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "last_modified", 0L);
  }
  
  public static long length(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "_size", 0L);
  }
  
  private static int queryForInt(Context paramContext, Uri paramUri, String paramString, int paramInt)
  {
    return (int)queryForLong(paramContext, paramUri, paramString, paramInt);
  }
  
  private static long queryForLong(Context paramContext, Uri paramUri, String paramString, long paramLong)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    localCursor = null;
    for (;;)
    {
      try
      {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = paramString;
        localCursor = localContentResolver.query(paramUri, arrayOfString, null, null, null);
        if ((!localCursor.moveToFirst()) || (localCursor.isNull(0))) {
          continue;
        }
        long l = localCursor.getLong(0);
        paramLong = l;
      }
      catch (Exception localException)
      {
        Log.w("DocumentFile", "Failed query: " + localException);
        closeQuietly(localCursor);
        continue;
      }
      finally
      {
        closeQuietly(localCursor);
      }
      return paramLong;
      closeQuietly(localCursor);
    }
  }
  
  private static String queryForString(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    Object localObject1 = paramContext.getContentResolver();
    localCursor = null;
    for (;;)
    {
      try
      {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = paramString1;
        localCursor = ((ContentResolver)localObject1).query(paramUri, arrayOfString, null, null, null);
        if ((!localCursor.moveToFirst()) || (localCursor.isNull(0))) {
          continue;
        }
        localObject1 = localCursor.getString(0);
        paramString2 = (String)localObject1;
      }
      catch (Exception localException)
      {
        Log.w("DocumentFile", "Failed query: " + localException);
        closeQuietly(localCursor);
        continue;
      }
      finally
      {
        closeQuietly(localCursor);
      }
      return paramString2;
      closeQuietly(localCursor);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\provider\DocumentsContractApi19.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */