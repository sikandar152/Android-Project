package android.support.v4.util;

public class SparseArrayCompat<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private int[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public SparseArrayCompat()
  {
    this(10);
  }
  
  public SparseArrayCompat(int paramInt)
  {
    if (paramInt != 0)
    {
      int i = ContainerHelpers.idealIntArraySize(paramInt);
      this.mKeys = new int[i];
      this.mValues = new Object[i];
    }
    else
    {
      this.mKeys = ContainerHelpers.EMPTY_INTS;
      this.mValues = ContainerHelpers.EMPTY_OBJECTS;
    }
    this.mSize = 0;
  }
  
  private void gc()
  {
    int i = this.mSize;
    int k = 0;
    int[] arrayOfInt = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        this.mGarbage = false;
        this.mSize = k;
        return;
      }
      Object localObject = arrayOfObject[j];
      if (localObject != DELETED)
      {
        if (j != k)
        {
          arrayOfInt[k] = arrayOfInt[j];
          arrayOfObject[k] = localObject;
          arrayOfObject[j] = null;
        }
        k++;
      }
    }
  }
  
  public void append(int paramInt, E paramE)
  {
    if ((this.mSize == 0) || (paramInt > this.mKeys[(-1 + this.mSize)]))
    {
      if ((this.mGarbage) && (this.mSize >= this.mKeys.length)) {
        gc();
      }
      int i = this.mSize;
      if (i >= this.mKeys.length)
      {
        int j = ContainerHelpers.idealIntArraySize(i + 1);
        int[] arrayOfInt = new int[j];
        Object[] arrayOfObject = new Object[j];
        System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
        System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
        this.mKeys = arrayOfInt;
        this.mValues = arrayOfObject;
      }
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramE;
      this.mSize = (i + 1);
    }
    else
    {
      put(paramInt, paramE);
    }
  }
  
  public void clear()
  {
    int j = this.mSize;
    Object[] arrayOfObject = this.mValues;
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        this.mSize = 0;
        this.mGarbage = false;
        return;
      }
      arrayOfObject[i] = null;
    }
  }
  
  public SparseArrayCompat<E> clone()
  {
    SparseArrayCompat localSparseArrayCompat = null;
    try
    {
      localSparseArrayCompat = (SparseArrayCompat)super.clone();
      localSparseArrayCompat.mKeys = ((int[])this.mKeys.clone());
      localSparseArrayCompat.mValues = ((Object[])this.mValues.clone());
      return localSparseArrayCompat;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      for (;;) {}
    }
  }
  
  public void delete(int paramInt)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if ((i >= 0) && (this.mValues[i] != DELETED))
    {
      this.mValues[i] = DELETED;
      this.mGarbage = true;
    }
  }
  
  public E get(int paramInt)
  {
    return (E)get(paramInt, null);
  }
  
  public E get(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if ((i >= 0) && (this.mValues[i] != DELETED)) {
      paramE = this.mValues[i];
    }
    return paramE;
  }
  
  public int indexOfKey(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
  }
  
  public int indexOfValue(E paramE)
  {
    if (this.mGarbage) {
      gc();
    }
    for (int i = 0;; i++)
    {
      if (i >= this.mSize)
      {
        i = -1;
        break;
      }
      if (this.mValues[i] == paramE) {
        break;
      }
    }
    return i;
  }
  
  public int keyAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mKeys[paramInt];
  }
  
  public void put(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    int[] arrayOfInt;
    if (i < 0)
    {
      int j = i ^ 0xFFFFFFFF;
      if ((j >= this.mSize) || (this.mValues[j] != DELETED))
      {
        if ((this.mGarbage) && (this.mSize >= this.mKeys.length))
        {
          gc();
          j = 0xFFFFFFFF ^ ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
        }
        if (this.mSize >= this.mKeys.length)
        {
          int k = ContainerHelpers.idealIntArraySize(1 + this.mSize);
          arrayOfInt = new int[k];
          Object[] arrayOfObject = new Object[k];
          System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
          System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
          this.mKeys = arrayOfInt;
          this.mValues = arrayOfObject;
        }
        if (this.mSize - j != 0)
        {
          System.arraycopy(this.mKeys, j, this.mKeys, j + 1, this.mSize - j);
          System.arraycopy(this.mValues, j, this.mValues, j + 1, this.mSize - j);
        }
        this.mKeys[j] = paramInt;
        this.mValues[j] = paramE;
        this.mSize = (1 + this.mSize);
      }
      else
      {
        this.mKeys[j] = paramInt;
        this.mValues[j] = paramE;
      }
    }
    else
    {
      this.mValues[arrayOfInt] = paramE;
    }
  }
  
  public void remove(int paramInt)
  {
    delete(paramInt);
  }
  
  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] != DELETED)
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
  }
  
  public void removeAtRange(int paramInt1, int paramInt2)
  {
    int j = Math.min(this.mSize, paramInt1 + paramInt2);
    for (int i = paramInt1;; i++)
    {
      if (i >= j) {
        return;
      }
      removeAt(i);
    }
  }
  
  public void setValueAt(int paramInt, E paramE)
  {
    if (this.mGarbage) {
      gc();
    }
    this.mValues[paramInt] = paramE;
  }
  
  public int size()
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mSize;
  }
  
  public String toString()
  {
    if (size() > 0)
    {
      StringBuilder localStringBuilder = new StringBuilder(28 * this.mSize);
      localStringBuilder.append('{');
      for (int i = 0;; i++)
      {
        if (i >= this.mSize)
        {
          localStringBuilder.append('}');
          localObject = localStringBuilder.toString();
          break;
        }
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(keyAt(i));
        localStringBuilder.append('=');
        localObject = valueAt(i);
        if (localObject == this) {
          localStringBuilder.append("(this Map)");
        } else {
          localStringBuilder.append(localObject);
        }
      }
    }
    Object localObject = "{}";
    return (String)localObject;
  }
  
  public E valueAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return (E)this.mValues[paramInt];
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\SparseArrayCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */