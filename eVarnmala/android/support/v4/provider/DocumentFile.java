package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import java.io.File;

public abstract class DocumentFile
{
  static final String TAG = "DocumentFile";
  private final DocumentFile mParent;
  
  DocumentFile(DocumentFile paramDocumentFile)
  {
    this.mParent = paramDocumentFile;
  }
  
  public static DocumentFile fromFile(File paramFile)
  {
    return new RawDocumentFile(null, paramFile);
  }
  
  public static DocumentFile fromSingleUri(Context paramContext, Uri paramUri)
  {
    SingleDocumentFile localSingleDocumentFile;
    if (Build.VERSION.SDK_INT < 19) {
      localSingleDocumentFile = null;
    } else {
      localSingleDocumentFile = new SingleDocumentFile(null, paramContext, paramUri);
    }
    return localSingleDocumentFile;
  }
  
  public static DocumentFile fromTreeUri(Context paramContext, Uri paramUri)
  {
    TreeDocumentFile localTreeDocumentFile;
    if (Build.VERSION.SDK_INT < 21) {
      localTreeDocumentFile = null;
    } else {
      localTreeDocumentFile = new TreeDocumentFile(null, paramContext, DocumentsContractApi21.prepareTreeUri(paramUri));
    }
    return localTreeDocumentFile;
  }
  
  public static boolean isDocumentUri(Context paramContext, Uri paramUri)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 19) {
      bool = false;
    } else {
      bool = DocumentsContractApi19.isDocumentUri(paramContext, paramUri);
    }
    return bool;
  }
  
  public abstract boolean canRead();
  
  public abstract boolean canWrite();
  
  public abstract DocumentFile createDirectory(String paramString);
  
  public abstract DocumentFile createFile(String paramString1, String paramString2);
  
  public abstract boolean delete();
  
  public abstract boolean exists();
  
  public DocumentFile findFile(String paramString)
  {
    DocumentFile[] arrayOfDocumentFile = listFiles();
    int i = arrayOfDocumentFile.length;
    DocumentFile localDocumentFile;
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        localDocumentFile = null;
        break;
      }
      localDocumentFile = arrayOfDocumentFile[j];
      if (paramString.equals(localDocumentFile.getName())) {
        break;
      }
    }
    return localDocumentFile;
  }
  
  public abstract String getName();
  
  public DocumentFile getParentFile()
  {
    return this.mParent;
  }
  
  public abstract String getType();
  
  public abstract Uri getUri();
  
  public abstract boolean isDirectory();
  
  public abstract boolean isFile();
  
  public abstract long lastModified();
  
  public abstract long length();
  
  public abstract DocumentFile[] listFiles();
  
  public abstract boolean renameTo(String paramString);
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\provider\DocumentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */