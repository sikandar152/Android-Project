package android.support.v4.util;

public class Pair<F, S>
{
  public final F first;
  public final S second;
  
  public Pair(F paramF, S paramS)
  {
    this.first = paramF;
    this.second = paramS;
  }
  
  public static <A, B> Pair<A, B> create(A paramA, B paramB)
  {
    return new Pair(paramA, paramB);
  }
  
  private static boolean objectsEqual(Object paramObject1, Object paramObject2)
  {
    boolean bool;
    if ((paramObject1 != paramObject2) && ((paramObject1 == null) || (!paramObject1.equals(paramObject2)))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof Pair))
    {
      Pair localPair = (Pair)paramObject;
      if ((objectsEqual(localPair.first, this.first)) && (objectsEqual(localPair.second, this.second))) {
        bool = true;
      }
    }
    return bool;
  }
  
  public int hashCode()
  {
    int i = 0;
    int j;
    if (this.first != null) {
      j = this.first.hashCode();
    } else {
      j = 0;
    }
    if (this.second != null) {
      i = this.second.hashCode();
    }
    return j ^ i;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\util\Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */