package android.support.v4.util;

public class CircularArray<E>
{
  private int mCapacityBitmask;
  private E[] mElements;
  private int mHead;
  private int mTail;
  
  public CircularArray()
  {
    this(8);
  }
  
  public CircularArray(int paramInt)
  {
    if (paramInt > 0)
    {
      int i = paramInt;
      if (Integer.bitCount(paramInt) != 1) {
        i = 1 << 1 + Integer.highestOneBit(paramInt);
      }
      this.mCapacityBitmask = (i - 1);
      this.mElements = ((Object[])new Object[i]);
      return;
    }
    throw new IllegalArgumentException("capacity must be positive");
  }
  
  private void doubleCapacity()
  {
    int k = this.mElements.length;
    int i = k - this.mHead;
    int j = k << 1;
    if (j >= 0)
    {
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mElements, this.mHead, arrayOfObject, 0, i);
      System.arraycopy(this.mElements, 0, arrayOfObject, i, this.mHead);
      this.mElements = ((Object[])arrayOfObject);
      this.mHead = 0;
      this.mTail = k;
      this.mCapacityBitmask = (j - 1);
      return;
    }
    throw new RuntimeException("Too big");
  }
  
  public final void addFirst(E paramE)
  {
    this.mHead = (-1 + this.mHead & this.mCapacityBitmask);
    this.mElements[this.mHead] = paramE;
    if (this.mHead == this.mTail) {
      doubleCapacity();
    }
  }
  
  public final void addLast(E paramE)
  {
    this.mElements[this.mTail] = paramE;
    this.mTail = (1 + this.mTail & this.mCapacityBitmask);
    if (this.mTail == this.mHead) {
      doubleCapacity();
    }
  }
  
  public final E get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < size()))
    {
      int i = paramInt + this.mHead & this.mCapacityBitmask;
      return (E)this.mElements[i];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final E getFirst()
  {
    if (this.mHead != this.mTail) {
      return (E)this.mElements[this.mHead];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final E getLast()
  {
    if (this.mHead != this.mTail) {
      return (E)this.mElements[(-1 + this.mTail & this.mCapacityBitmask)];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final boolean isEmpty()
  {
    boolean bool;
    if (this.mHead != this.mTail) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public final E popFirst()
  {
    if (this.mHead != this.mTail)
    {
      Object localObject = this.mElements[this.mHead];
      this.mElements[this.mHead] = null;
      this.mHead = (1 + this.mHead & this.mCapacityBitmask);
      return (E)localObject;
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final E popLast()
  {
    if (this.mHead != this.mTail)
    {
      int i = -1 + this.mTail & this.mCapacityBitmask;
      Object localObject = this.mElements[i];
      this.mElements[i] = null;
      this.mTail = i;
      return (E)localObject;
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final int size()
  {
    return this.mTail - this.mHead & this.mCapacityBitmask;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\CircularArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */