package android.support.v4.util;

import java.io.PrintWriter;

public class TimeUtils
{
  public static final int HUNDRED_DAY_FIELD_LEN = 19;
  private static final int SECONDS_PER_DAY = 86400;
  private static final int SECONDS_PER_HOUR = 3600;
  private static final int SECONDS_PER_MINUTE = 60;
  private static char[] sFormatStr = new char[24];
  private static final Object sFormatSync = new Object();
  
  private static int accumField(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    int i;
    if ((paramInt1 <= 99) && ((!paramBoolean) || (paramInt3 < 3)))
    {
      if ((paramInt1 <= 9) && ((!paramBoolean) || (paramInt3 < 2)))
      {
        if ((!paramBoolean) && (paramInt1 <= 0)) {
          i = 0;
        } else {
          i = paramInt2 + 1;
        }
      }
      else {
        i = paramInt2 + 2;
      }
    }
    else {
      i = paramInt2 + 3;
    }
    return i;
  }
  
  public static void formatDuration(long paramLong1, long paramLong2, PrintWriter paramPrintWriter)
  {
    if (paramLong1 != 0L) {
      formatDuration(paramLong1 - paramLong2, paramPrintWriter, 0);
    } else {
      paramPrintWriter.print("--");
    }
  }
  
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter)
  {
    formatDuration(paramLong, paramPrintWriter, 0);
  }
  
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter, int paramInt)
  {
    synchronized (sFormatSync)
    {
      int i = formatDurationLocked(paramLong, paramInt);
      paramPrintWriter.print(new String(sFormatStr, 0, i));
      return;
    }
  }
  
  public static void formatDuration(long paramLong, StringBuilder paramStringBuilder)
  {
    synchronized (sFormatSync)
    {
      int i = formatDurationLocked(paramLong, 0);
      paramStringBuilder.append(sFormatStr, 0, i);
      return;
    }
  }
  
  private static int formatDurationLocked(long paramLong, int paramInt)
  {
    if (sFormatStr.length < paramInt) {
      sFormatStr = new char[paramInt];
    }
    char[] arrayOfChar = sFormatStr;
    int i;
    if (paramLong != 0L)
    {
      int i3;
      if (paramLong <= 0L)
      {
        i3 = 45;
        paramLong = -paramLong;
      }
      else
      {
        i3 = 43;
      }
      j = (int)(paramLong % 1000L);
      int k = (int)Math.floor(paramLong / 1000L);
      int i1 = 0;
      int n = 0;
      int m = 0;
      if (k > 86400)
      {
        i1 = k / 86400;
        k -= 86400 * i1;
      }
      if (k > 3600)
      {
        n = k / 3600;
        k -= n * 3600;
      }
      if (k > 60)
      {
        m = k / 60;
        k -= m * 60;
      }
      int i4 = 0;
      int i6;
      int i8;
      if (paramInt != 0)
      {
        int i5 = accumField(i1, 1, false, 0);
        boolean bool2;
        if (i5 <= 0) {
          bool2 = false;
        } else {
          bool2 = true;
        }
        int i7 = i5 + accumField(n, 1, bool2, 2);
        if (i7 <= 0) {
          i5 = 0;
        } else {
          i5 = 1;
        }
        i6 = i7 + accumField(m, 1, i5, 2);
        if (i6 <= 0) {
          i7 = 0;
        } else {
          i7 = 1;
        }
        i6 += accumField(k, 1, i7, 2);
        if (i6 <= 0) {
          i8 = 0;
        } else {
          i8 = 3;
        }
        i6 += 1 + accumField(j, 2, true, i8);
      }
      for (;;)
      {
        if (i6 >= paramInt)
        {
          arrayOfChar[i4] = i3;
          i3 = i4 + 1;
          if (paramInt == 0) {
            i4 = 0;
          } else {
            i4 = 1;
          }
          i8 = printField(arrayOfChar, i1, 'd', i3, false, 0);
          if (i8 == i3) {
            i1 = 0;
          } else {
            i1 = 1;
          }
          if (i4 == 0) {
            i6 = 0;
          } else {
            i6 = 2;
          }
          i6 = printField(arrayOfChar, n, 'h', i8, i1, i6);
          boolean bool1;
          if (i6 == i3) {
            bool1 = false;
          } else {
            bool1 = true;
          }
          if (i4 == 0) {
            n = 0;
          } else {
            n = 2;
          }
          m = printField(arrayOfChar, m, 'm', i6, bool1, n);
          if (m == i3) {
            n = 0;
          } else {
            n = 1;
          }
          int i2;
          if (i4 == 0) {
            bool1 = false;
          } else {
            i2 = 2;
          }
          k = printField(arrayOfChar, k, 's', m, n, i2);
          if ((i4 == 0) || (k == i3)) {
            m = 0;
          } else {
            m = 3;
          }
          j = printField(arrayOfChar, j, 'm', k, true, m);
          arrayOfChar[j] = 's';
          i = j + 1;
          break;
        }
        i[i4] = 32;
        i4++;
        i6++;
      }
    }
    int j = paramInt - 1;
    for (;;)
    {
      if (j >= 0)
      {
        i[0] = 48;
        i = 1;
        return i;
      }
      i[0] = 32;
    }
  }
  
  private static int printField(char[] paramArrayOfChar, int paramInt1, char paramChar, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if ((paramBoolean) || (paramInt1 > 0))
    {
      int i = paramInt2;
      if (((paramBoolean) && (paramInt3 >= 3)) || (paramInt1 > 99))
      {
        int j = paramInt1 / 100;
        paramArrayOfChar[paramInt2] = ((char)(j + 48));
        paramInt2++;
        paramInt1 -= j * 100;
      }
      if (((paramBoolean) && (paramInt3 >= 2)) || (paramInt1 > 9) || (i != paramInt2))
      {
        i = paramInt1 / 10;
        paramArrayOfChar[paramInt2] = ((char)(i + 48));
        paramInt2++;
        paramInt1 -= i * 10;
      }
      paramArrayOfChar[paramInt2] = ((char)(paramInt1 + 48));
      i = paramInt2 + 1;
      paramArrayOfChar[i] = paramChar;
      paramInt2 = i + 1;
    }
    return paramInt2;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\TimeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */