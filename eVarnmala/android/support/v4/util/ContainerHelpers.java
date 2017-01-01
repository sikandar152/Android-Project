package android.support.v4.util;

class ContainerHelpers
{
  static final int[] EMPTY_INTS = new int[0];
  static final long[] EMPTY_LONGS = new long[0];
  static final Object[] EMPTY_OBJECTS = new Object[0];
  
  static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int m = paramInt1 - 1;
    for (;;)
    {
      int k;
      if (i > m)
      {
        k = i ^ 0xFFFFFFFF;
      }
      else
      {
        k = i + m >>> 1;
        int j = paramArrayOfInt[k];
        if (j < paramInt2) {
          break label60;
        }
        if (j > paramInt2) {
          break label51;
        }
      }
      return k;
      label51:
      m = k - 1;
      continue;
      label60:
      i = k + 1;
    }
  }
  
  static int binarySearch(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    int j = 0;
    int i = paramInt - 1;
    for (;;)
    {
      int k;
      if (j > i)
      {
        k = j ^ 0xFFFFFFFF;
      }
      else
      {
        k = j + i >>> 1;
        long l = paramArrayOfLong[k];
        if (l < paramLong) {
          break label66;
        }
        if (l > paramLong) {
          break label57;
        }
      }
      return k;
      label57:
      i = k - 1;
      continue;
      label66:
      j = k + 1;
    }
  }
  
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    boolean bool;
    if ((paramObject1 != paramObject2) && ((paramObject1 == null) || (!paramObject1.equals(paramObject2)))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static int idealByteArraySize(int paramInt)
  {
    int i = 4;
    while (i < 32) {
      if (paramInt > -12 + (1 << i)) {
        i++;
      } else {
        paramInt = -12 + (1 << i);
      }
    }
    return paramInt;
  }
  
  public static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\ContainerHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */