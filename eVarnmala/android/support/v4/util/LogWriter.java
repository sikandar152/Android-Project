package android.support.v4.util;

import android.util.Log;
import java.io.Writer;

public class LogWriter
  extends Writer
{
  private StringBuilder mBuilder = new StringBuilder(128);
  private final String mTag;
  
  public LogWriter(String paramString)
  {
    this.mTag = paramString;
  }
  
  private void flushBuilder()
  {
    if (this.mBuilder.length() > 0)
    {
      Log.d(this.mTag, this.mBuilder.toString());
      this.mBuilder.delete(0, this.mBuilder.length());
    }
  }
  
  public void close()
  {
    flushBuilder();
  }
  
  public void flush()
  {
    flushBuilder();
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0;; i++)
    {
      if (i >= paramInt2) {
        return;
      }
      char c = paramArrayOfChar[(paramInt1 + i)];
      if (c != '\n') {
        this.mBuilder.append(c);
      } else {
        flushBuilder();
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\LogWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */