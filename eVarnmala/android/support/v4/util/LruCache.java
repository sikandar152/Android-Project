package android.support.v4.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache<K, V>
{
  private int createCount;
  private int evictionCount;
  private int hitCount;
  private final LinkedHashMap<K, V> map;
  private int maxSize;
  private int missCount;
  private int putCount;
  private int size;
  
  public LruCache(int paramInt)
  {
    if (paramInt > 0)
    {
      this.maxSize = paramInt;
      this.map = new LinkedHashMap(0, 0.75F, true);
      return;
    }
    throw new IllegalArgumentException("maxSize <= 0");
  }
  
  private int safeSizeOf(K paramK, V paramV)
  {
    int i = sizeOf(paramK, paramV);
    if (i >= 0) {
      return i;
    }
    throw new IllegalStateException("Negative size: " + paramK + "=" + paramV);
  }
  
  protected V create(K paramK)
  {
    return null;
  }
  
  /**
   * @deprecated
   */
  public final int createCount()
  {
    try
    {
      int i = this.createCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void entryRemoved(boolean paramBoolean, K paramK, V paramV1, V paramV2) {}
  
  public final void evictAll()
  {
    trimToSize(-1);
  }
  
  /**
   * @deprecated
   */
  public final int evictionCount()
  {
    try
    {
      int i = this.evictionCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final V get(K paramK)
  {
    if (paramK == null) {
      throw new NullPointerException("key == null");
    }
    Object localObject5;
    try
    {
      Object localObject1 = this.map.get(paramK);
      if (localObject1 != null)
      {
        this.hitCount = (1 + this.hitCount);
        localObject5 = localObject1;
      }
      else
      {
        this.missCount = (1 + this.missCount);
        localObject5 = create(paramK);
        if (localObject5 == null) {
          localObject5 = null;
        }
      }
    }
    finally {}
    try
    {
      this.createCount = (1 + this.createCount);
      Object localObject3 = this.map.put(paramK, localObject5);
      if (localObject3 != null) {
        this.map.put(paramK, localObject3);
      }
      for (;;)
      {
        if (localObject3 == null) {
          break;
        }
        entryRemoved(false, paramK, localObject5, localObject3);
        return (V)localObject3;
        this.size += safeSizeOf(paramK, localObject5);
      }
      trimToSize(this.maxSize);
    }
    finally {}
    return (V)localObject5;
  }
  
  /**
   * @deprecated
   */
  public final int hitCount()
  {
    try
    {
      int i = this.hitCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /**
   * @deprecated
   */
  public final int maxSize()
  {
    try
    {
      int i = this.maxSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /**
   * @deprecated
   */
  public final int missCount()
  {
    try
    {
      int i = this.missCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final V put(K paramK, V paramV)
  {
    if ((paramK == null) || (paramV == null)) {
      throw new NullPointerException("key == null || value == null");
    }
    try
    {
      this.putCount = (1 + this.putCount);
      this.size += safeSizeOf(paramK, paramV);
      Object localObject1 = this.map.put(paramK, paramV);
      if (localObject1 != null) {
        this.size -= safeSizeOf(paramK, localObject1);
      }
      if (localObject1 != null) {
        entryRemoved(false, paramK, localObject1, paramV);
      }
      trimToSize(this.maxSize);
      return (V)localObject1;
    }
    finally {}
  }
  
  /**
   * @deprecated
   */
  public final int putCount()
  {
    try
    {
      int i = this.putCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final V remove(K paramK)
  {
    if (paramK == null) {
      throw new NullPointerException("key == null");
    }
    try
    {
      Object localObject1 = this.map.remove(paramK);
      if (localObject1 != null) {
        this.size -= safeSizeOf(paramK, localObject1);
      }
      if (localObject1 != null) {
        entryRemoved(false, paramK, localObject1, null);
      }
      return (V)localObject1;
    }
    finally {}
  }
  
  public void resize(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    try
    {
      this.maxSize = paramInt;
      trimToSize(paramInt);
      return;
    }
    finally {}
  }
  
  /**
   * @deprecated
   */
  public final int size()
  {
    try
    {
      int i = this.size;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected int sizeOf(K paramK, V paramV)
  {
    return 1;
  }
  
  /**
   * @deprecated
   */
  public final Map<K, V> snapshot()
  {
    try
    {
      LinkedHashMap localLinkedHashMap = new LinkedHashMap(this.map);
      return localLinkedHashMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /**
   * @deprecated
   */
  public final String toString()
  {
    int i = 0;
    try
    {
      int j = this.hitCount + this.missCount;
      if (j != 0) {
        i = 100 * this.hitCount / j;
      }
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = Integer.valueOf(this.maxSize);
      arrayOfObject[1] = Integer.valueOf(this.hitCount);
      arrayOfObject[2] = Integer.valueOf(this.missCount);
      arrayOfObject[3] = Integer.valueOf(i);
      String str = String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", arrayOfObject);
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void trimToSize(int paramInt)
  {
    Object localObject3;
    Object localObject2;
    try
    {
      if ((this.size < 0) || ((this.map.isEmpty()) && (this.size != 0))) {
        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
      }
    }
    finally
    {
      throw ((Throwable)localObject1);
      if ((this.size <= paramInt) || (this.map.isEmpty())) {
        return;
      }
      localObject3 = (Map.Entry)this.map.entrySet().iterator().next();
      localObject2 = ((Map.Entry)localObject3).getKey();
      localObject3 = ((Map.Entry)localObject3).getValue();
      this.map.remove(localObject2);
      this.size -= safeSizeOf(localObject2, localObject3);
      this.evictionCount = (1 + this.evictionCount);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\LruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */