package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;

class DocumentsContractApi21
{
  private static final String TAG = "DocumentFile";
  
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
  
  public static Uri createDirectory(Context paramContext, Uri paramUri, String paramString)
  {
    return createFile(paramContext, paramUri, "vnd.android.document/directory", paramString);
  }
  
  public static Uri createFile(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    return DocumentsContract.createDocument(paramContext.getContentResolver(), paramUri, paramString1, paramString2);
  }
  
  /* Error */
  public static Uri[] listFiles(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 37	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore 5
    //   6: aload_1
    //   7: aload_1
    //   8: invokestatic 49	android/provider/DocumentsContract:getDocumentId	(Landroid/net/Uri;)Ljava/lang/String;
    //   11: invokestatic 53	android/provider/DocumentsContract:buildChildDocumentsUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   14: astore 6
    //   16: new 55	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 56	java/util/ArrayList:<init>	()V
    //   23: astore_3
    //   24: aconst_null
    //   25: astore_2
    //   26: iconst_1
    //   27: anewarray 58	java/lang/String
    //   30: astore 4
    //   32: aload 4
    //   34: iconst_0
    //   35: ldc 60
    //   37: aastore
    //   38: aload 5
    //   40: aload 6
    //   42: aload 4
    //   44: aconst_null
    //   45: aconst_null
    //   46: aconst_null
    //   47: invokevirtual 66	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   50: astore_2
    //   51: aload_2
    //   52: invokeinterface 72 1 0
    //   57: ifeq +69 -> 126
    //   60: aload_3
    //   61: aload_1
    //   62: aload_2
    //   63: iconst_0
    //   64: invokeinterface 76 2 0
    //   69: invokestatic 79	android/provider/DocumentsContract:buildDocumentUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   72: invokevirtual 83	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   75: pop
    //   76: goto -25 -> 51
    //   79: astore 4
    //   81: ldc 8
    //   83: new 85	java/lang/StringBuilder
    //   86: dup
    //   87: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   90: ldc 88
    //   92: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: aload 4
    //   97: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   100: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: invokestatic 105	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   106: pop
    //   107: aload_2
    //   108: invokestatic 107	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   111: aload_3
    //   112: aload_3
    //   113: invokevirtual 111	java/util/ArrayList:size	()I
    //   116: anewarray 113	android/net/Uri
    //   119: invokevirtual 117	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   122: checkcast 119	[Landroid/net/Uri;
    //   125: areturn
    //   126: aload_2
    //   127: invokestatic 107	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   130: goto -19 -> 111
    //   133: astore_3
    //   134: aload_2
    //   135: invokestatic 107	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   138: aload_3
    //   139: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	paramContext	Context
    //   0	140	1	paramUri	Uri
    //   25	110	2	localCursor	android.database.Cursor
    //   23	90	3	localArrayList	java.util.ArrayList
    //   133	6	3	localObject	Object
    //   30	13	4	arrayOfString	String[]
    //   79	17	4	localException	Exception
    //   4	35	5	localContentResolver	android.content.ContentResolver
    //   14	27	6	localUri	Uri
    // Exception table:
    //   from	to	target	type
    //   26	76	79	java/lang/Exception
    //   26	76	133	finally
    //   81	107	133	finally
  }
  
  public static Uri prepareTreeUri(Uri paramUri)
  {
    return DocumentsContract.buildDocumentUriUsingTree(paramUri, DocumentsContract.getTreeDocumentId(paramUri));
  }
  
  public static Uri renameTo(Context paramContext, Uri paramUri, String paramString)
  {
    return DocumentsContract.renameDocument(paramContext.getContentResolver(), paramUri, paramString);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\provider\DocumentsContractApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */