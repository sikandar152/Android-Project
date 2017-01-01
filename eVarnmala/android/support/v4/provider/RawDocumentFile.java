package android.support.v4.provider;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class RawDocumentFile
  extends DocumentFile
{
  private File mFile;
  
  RawDocumentFile(DocumentFile paramDocumentFile, File paramFile)
  {
    super(paramDocumentFile);
    this.mFile = paramFile;
  }
  
  private static boolean deleteContents(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    boolean bool = true;
    int j;
    if (arrayOfFile != null) {
      j = arrayOfFile.length;
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return bool;
      }
      File localFile = arrayOfFile[i];
      if (localFile.isDirectory()) {
        bool &= deleteContents(localFile);
      }
      if (!localFile.delete())
      {
        Log.w("DocumentFile", "Failed to delete " + localFile);
        bool = false;
      }
    }
  }
  
  private static String getTypeForName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    String str;
    if (i >= 0)
    {
      str = paramString.substring(i + 1).toLowerCase();
      str = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
      if (str != null) {}
    }
    else
    {
      str = "application/octet-stream";
    }
    return str;
  }
  
  public boolean canRead()
  {
    return this.mFile.canRead();
  }
  
  public boolean canWrite()
  {
    return this.mFile.canWrite();
  }
  
  public DocumentFile createDirectory(String paramString)
  {
    Object localObject = new File(this.mFile, paramString);
    if ((!((File)localObject).isDirectory()) && (!((File)localObject).mkdir())) {
      localObject = null;
    } else {
      localObject = new RawDocumentFile(this, (File)localObject);
    }
    return (DocumentFile)localObject;
  }
  
  public DocumentFile createFile(String paramString1, String paramString2)
  {
    Object localObject1 = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString1);
    if (localObject1 != null) {
      paramString2 = paramString2 + "." + (String)localObject1;
    }
    localObject1 = new File(this.mFile, paramString2);
    try
    {
      ((File)localObject1).createNewFile();
      localObject1 = new RawDocumentFile(this, (File)localObject1);
      return (DocumentFile)localObject1;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.w("DocumentFile", "Failed to createFile: " + localIOException);
        Object localObject2 = null;
      }
    }
  }
  
  public boolean delete()
  {
    deleteContents(this.mFile);
    return this.mFile.delete();
  }
  
  public boolean exists()
  {
    return this.mFile.exists();
  }
  
  public String getName()
  {
    return this.mFile.getName();
  }
  
  public String getType()
  {
    String str;
    if (!this.mFile.isDirectory()) {
      str = getTypeForName(this.mFile.getName());
    } else {
      str = null;
    }
    return str;
  }
  
  public Uri getUri()
  {
    return Uri.fromFile(this.mFile);
  }
  
  public boolean isDirectory()
  {
    return this.mFile.isDirectory();
  }
  
  public boolean isFile()
  {
    return this.mFile.isFile();
  }
  
  public long lastModified()
  {
    return this.mFile.lastModified();
  }
  
  public long length()
  {
    return this.mFile.length();
  }
  
  public DocumentFile[] listFiles()
  {
    ArrayList localArrayList = new ArrayList();
    File[] arrayOfFile = this.mFile.listFiles();
    int j;
    if (arrayOfFile != null) {
      j = arrayOfFile.length;
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return (DocumentFile[])localArrayList.toArray(new DocumentFile[localArrayList.size()]);
      }
      localArrayList.add(new RawDocumentFile(this, arrayOfFile[i]));
    }
  }
  
  public boolean renameTo(String paramString)
  {
    File localFile = new File(this.mFile.getParentFile(), paramString);
    int i;
    if (!this.mFile.renameTo(localFile))
    {
      i = 0;
    }
    else
    {
      this.mFile = i;
      i = 1;
    }
    return i;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\provider\RawDocumentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */