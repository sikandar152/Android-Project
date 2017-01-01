package android.support.v4.util;

import java.util.Map;

public class SimpleArrayMap<K, V>
{
  private static final int BASE_SIZE = 4;
  private static final int CACHE_SIZE = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "ArrayMap";
  static Object[] mBaseCache;
  static int mBaseCacheSize;
  static Object[] mTwiceBaseCache;
  static int mTwiceBaseCacheSize;
  Object[] mArray;
  int[] mHashes;
  int mSize;
  
  public SimpleArrayMap()
  {
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt)
  {
    if (paramInt != 0)
    {
      allocArrays(paramInt);
    }
    else
    {
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    }
    this.mSize = 0;
  }
  
  public SimpleArrayMap(SimpleArrayMap paramSimpleArrayMap)
  {
    this();
    if (paramSimpleArrayMap != null) {
      putAll(paramSimpleArrayMap);
    }
  }
  
  private void allocArrays(int paramInt)
  {
    if (paramInt == 8) {}
    for (;;)
    {
      try
      {
        if (mTwiceBaseCache != null)
        {
          Object[] arrayOfObject1 = mTwiceBaseCache;
          this.mArray = arrayOfObject1;
          mTwiceBaseCache = (Object[])arrayOfObject1[0];
          this.mHashes = ((int[])arrayOfObject1[1]);
          arrayOfObject1[1] = null;
          arrayOfObject1[0] = null;
          mTwiceBaseCacheSize = -1 + mTwiceBaseCacheSize;
        }
        else
        {
          this.mHashes = new int[paramInt];
          this.mArray = new Object[paramInt << 1];
        }
      }
      finally {}
      if (paramInt == 4) {
        try
        {
          if (mBaseCache != null)
          {
            Object[] arrayOfObject2 = mBaseCache;
            this.mArray = arrayOfObject2;
            mBaseCache = (Object[])arrayOfObject2[0];
            this.mHashes = ((int[])arrayOfObject2[1]);
            arrayOfObject2[1] = null;
            arrayOfObject2[0] = null;
            mBaseCacheSize = -1 + mBaseCacheSize;
          }
        }
        finally
        {
          throw ((Throwable)localObject2);
        }
      }
    }
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length == 8) {
      try
      {
        if (mTwiceBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mTwiceBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          for (int i = -1 + (paramInt << 1); i >= 2; i--) {
            paramArrayOfObject[i] = null;
          }
          mTwiceBaseCache = paramArrayOfObject;
          mTwiceBaseCacheSize = 1 + mTwiceBaseCacheSize;
        }
        return;
      }
      finally
      {
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
    } else if (paramArrayOfInt.length == 4) {
      try
      {
        if (mBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          for (int j = -1 + (paramInt << 1); j >= 2; j--) {
            paramArrayOfObject[j] = null;
          }
          mBaseCache = paramArrayOfObject;
          mBaseCacheSize = 1 + mBaseCacheSize;
        }
      }
      finally
      {
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
  }
  
  public void clear()
  {
    if (this.mSize != 0)
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    boolean bool;
    if (indexOfKey(paramObject) < 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean containsValue(Object paramObject)
  {
    boolean bool;
    if (indexOfValue(paramObject) < 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (this.mHashes.length < paramInt)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, this.mSize);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, this.mSize << 1);
      }
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool1;
      if ((paramObject instanceof Map))
      {
        Map localMap = (Map)paramObject;
        if (size() != localMap.size())
        {
          bool1 = false;
        }
        else
        {
          int i = 0;
          try
          {
            while (i < this.mSize)
            {
              Object localObject1 = keyAt(i);
              Object localObject2 = valueAt(i);
              Object localObject3 = localMap.get(localObject1);
              if (localObject2 == null)
              {
                if (localObject3 != null) {
                  break label146;
                }
                if (!localMap.containsKey(localObject1)) {
                  break label146;
                }
              }
              else
              {
                boolean bool2 = localObject2.equals(localObject3);
                if (!bool2)
                {
                  bool1 = false;
                  break;
                }
              }
              i++;
            }
          }
          catch (NullPointerException localNullPointerException)
          {
            bool1 = false;
          }
          catch (ClassCastException localClassCastException)
          {
            bool1 = false;
          }
        }
      }
      else
      {
        bool1 = false;
        continue;
        label146:
        bool1 = false;
      }
    }
  }
  
  public V get(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    Object localObject;
    if (i < 0) {
      localObject = null;
    } else {
      localObject = this.mArray[(1 + (localObject << 1))];
    }
    return (V)localObject;
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    int i = 0;
    int m = 0;
    int k = 1;
    int j = this.mSize;
    for (;;)
    {
      if (m >= j) {
        return i;
      }
      Object localObject = arrayOfObject[k];
      int n = arrayOfInt[m];
      int i1;
      if (localObject != null) {
        i1 = localObject.hashCode();
      } else {
        i1 = 0;
      }
      i += (i1 ^ n);
      m++;
      k += 2;
    }
  }
  
  int indexOf(Object paramObject, int paramInt)
  {
    int j = this.mSize;
    int k;
    if (j != 0)
    {
      k = ContainerHelpers.binarySearch(this.mHashes, j, paramInt);
      if ((k >= 0) && (!paramObject.equals(this.mArray[(k << 1)])))
      {
        for (int i = k + 1;; i++)
        {
          if ((i >= j) || (this.mHashes[i] != paramInt))
          {
            for (j = k - 1;; j--)
            {
              if ((j < 0) || (this.mHashes[j] != paramInt))
              {
                k = i ^ 0xFFFFFFFF;
                break label156;
              }
              if (paramObject.equals(this.mArray[(j << 1)])) {
                break;
              }
            }
            k = j;
            break label156;
          }
          if (paramObject.equals(this.mArray[(i << 1)])) {
            break;
          }
        }
        k = i;
      }
    }
    else
    {
      k = -1;
    }
    label156:
    return k;
  }
  
  public int indexOfKey(Object paramObject)
  {
    int i;
    if (paramObject != null) {
      i = indexOf(paramObject, paramObject.hashCode());
    } else {
      i = indexOfNull();
    }
    return i;
  }
  
  int indexOfNull()
  {
    int j = this.mSize;
    int k;
    if (j != 0)
    {
      k = ContainerHelpers.binarySearch(this.mHashes, j, 0);
      if ((k >= 0) && (this.mArray[(k << 1)] != null))
      {
        for (int i = k + 1;; i++)
        {
          if ((i >= j) || (this.mHashes[i] != 0))
          {
            for (j = k - 1;; j--)
            {
              if ((j < 0) || (this.mHashes[j] != 0)) {
                return i ^ 0xFFFFFFFF;
              }
              if (this.mArray[(j << 1)] == null) {
                break;
              }
            }
            return j;
          }
          if (this.mArray[(i << 1)] == null) {
            break;
          }
        }
        k = i;
      }
    }
    else
    {
      k = -1;
    }
    return k;
  }
  
  int indexOfValue(Object paramObject)
  {
    int j = 2 * this.mSize;
    Object[] arrayOfObject = this.mArray;
    if (paramObject != null)
    {
      k = 1;
      while (k < j) {
        if (!paramObject.equals(arrayOfObject[k])) {
          k += 2;
        } else {
          return k >> 1;
        }
      }
    }
    for (int k = 1;; k += 2)
    {
      if (k >= j) {
        return -1;
      }
      if (i[k] == null) {
        break;
      }
    }
    int i = k >> 1;
    return i;
  }
  
  public boolean isEmpty()
  {
    boolean bool;
    if (this.mSize > 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public K keyAt(int paramInt)
  {
    return (K)this.mArray[(paramInt << 1)];
  }
  
  public V put(K paramK, V paramV)
  {
    int j = 8;
    int i;
    int k;
    if (paramK != null)
    {
      i = paramK.hashCode();
      k = indexOf(paramK, i);
    }
    else
    {
      i = 0;
      k = indexOfNull();
    }
    Object[] arrayOfObject;
    Object localObject;
    if (k < 0)
    {
      int m = k ^ 0xFFFFFFFF;
      if (this.mSize >= this.mHashes.length)
      {
        if (this.mSize < j)
        {
          if (this.mSize < 4) {
            j = 4;
          }
        }
        else {
          j = this.mSize + (this.mSize >> 1);
        }
        int[] arrayOfInt = this.mHashes;
        arrayOfObject = this.mArray;
        allocArrays(j);
        if (this.mHashes.length > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
        }
        freeArrays(arrayOfInt, arrayOfObject, this.mSize);
      }
      if (m < this.mSize)
      {
        System.arraycopy(this.mHashes, m, this.mHashes, m + 1, this.mSize - m);
        System.arraycopy(this.mArray, m << 1, this.mArray, m + 1 << 1, this.mSize - m << 1);
      }
      this.mHashes[m] = i;
      this.mArray[(m << 1)] = paramK;
      this.mArray[(1 + (m << 1))] = paramV;
      this.mSize = (1 + this.mSize);
      localObject = null;
    }
    else
    {
      j = 1 + (arrayOfObject << 1);
      localObject = this.mArray[j];
      this.mArray[j] = paramV;
    }
    return (V)localObject;
  }
  
  public void putAll(SimpleArrayMap<? extends K, ? extends V> paramSimpleArrayMap)
  {
    int i = paramSimpleArrayMap.mSize;
    ensureCapacity(i + this.mSize);
    if (this.mSize != 0) {
      for (int j = 0; j < i; j++) {
        put(paramSimpleArrayMap.keyAt(j), paramSimpleArrayMap.valueAt(j));
      }
    }
    if (i > 0)
    {
      System.arraycopy(paramSimpleArrayMap.mHashes, 0, this.mHashes, 0, i);
      System.arraycopy(paramSimpleArrayMap.mArray, 0, this.mArray, 0, i << 1);
      this.mSize = i;
    }
  }
  
  public V remove(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    Object localObject;
    if (i < 0) {
      localObject = null;
    } else {
      localObject = removeAt(localObject);
    }
    return (V)localObject;
  }
  
  public V removeAt(int paramInt)
  {
    int i = 8;
    Object localObject = this.mArray[(1 + (paramInt << 1))];
    if (this.mSize > 1)
    {
      if ((this.mHashes.length <= i) || (this.mSize >= this.mHashes.length / 3))
      {
        this.mSize = (-1 + this.mSize);
        if (paramInt < this.mSize)
        {
          System.arraycopy(this.mHashes, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
          System.arraycopy(this.mArray, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
        }
        this.mArray[(this.mSize << 1)] = null;
        this.mArray[(1 + (this.mSize << 1))] = null;
      }
      else
      {
        if (this.mSize > i) {
          i = this.mSize + (this.mSize >> 1);
        }
        int[] arrayOfInt = this.mHashes;
        Object[] arrayOfObject = this.mArray;
        allocArrays(i);
        this.mSize = (-1 + this.mSize);
        if (paramInt > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt << 1);
        }
        if (paramInt < this.mSize)
        {
          System.arraycopy(arrayOfInt, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
          System.arraycopy(arrayOfObject, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
        }
      }
    }
    else
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
    }
    return (V)localObject;
  }
  
  public V setValueAt(int paramInt, V paramV)
  {
    int i = 1 + (paramInt << 1);
    Object localObject = this.mArray[i];
    this.mArray[i] = paramV;
    return (V)localObject;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public String toString()
  {
    if (!isEmpty())
    {
      StringBuilder localStringBuilder = new StringBuilder(28 * this.mSize);
      localStringBuilder.append('{');
      for (int i = 0;; str++)
      {
        if (i >= this.mSize)
        {
          localStringBuilder.append('}');
          str = localStringBuilder.toString();
          break;
        }
        if (str > 0) {
          localStringBuilder.append(", ");
        }
        Object localObject = keyAt(str);
        if (localObject == this) {
          localStringBuilder.append("(this Map)");
        } else {
          localStringBuilder.append(localObject);
        }
        localStringBuilder.append('=');
        localObject = valueAt(str);
        if (localObject == this) {
          localStringBuilder.append("(this Map)");
        } else {
          localStringBuilder.append(localObject);
        }
      }
    }
    String str = "{}";
    return str;
  }
  
  public V valueAt(int paramInt)
  {
    return (V)this.mArray[(1 + (paramInt << 1))];
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\SimpleArrayMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */