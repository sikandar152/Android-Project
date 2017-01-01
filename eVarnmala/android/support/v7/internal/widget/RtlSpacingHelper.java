package android.support.v7.internal.widget;

public class RtlSpacingHelper
{
  public static final int UNDEFINED = Integer.MIN_VALUE;
  private int mEnd = Integer.MIN_VALUE;
  private int mExplicitLeft = 0;
  private int mExplicitRight = 0;
  private boolean mIsRelative = false;
  private boolean mIsRtl = false;
  private int mLeft = 0;
  private int mRight = 0;
  private int mStart = Integer.MIN_VALUE;
  
  public int getEnd()
  {
    int i;
    if (!this.mIsRtl) {
      i = this.mRight;
    } else {
      i = this.mLeft;
    }
    return i;
  }
  
  public int getLeft()
  {
    return this.mLeft;
  }
  
  public int getRight()
  {
    return this.mRight;
  }
  
  public int getStart()
  {
    int i;
    if (!this.mIsRtl) {
      i = this.mLeft;
    } else {
      i = this.mRight;
    }
    return i;
  }
  
  public void setAbsolute(int paramInt1, int paramInt2)
  {
    this.mIsRelative = false;
    if (paramInt1 != Integer.MIN_VALUE)
    {
      this.mExplicitLeft = paramInt1;
      this.mLeft = paramInt1;
    }
    if (paramInt2 != Integer.MIN_VALUE)
    {
      this.mExplicitRight = paramInt2;
      this.mRight = paramInt2;
    }
  }
  
  public void setDirection(boolean paramBoolean)
  {
    if (paramBoolean != this.mIsRtl)
    {
      this.mIsRtl = paramBoolean;
      if (!this.mIsRelative)
      {
        this.mLeft = this.mExplicitLeft;
        this.mRight = this.mExplicitRight;
      }
      else
      {
        int i;
        if (!paramBoolean)
        {
          if (this.mStart == Integer.MIN_VALUE) {
            i = this.mExplicitLeft;
          } else {
            i = this.mStart;
          }
          this.mLeft = i;
          if (this.mEnd == Integer.MIN_VALUE) {
            i = this.mExplicitRight;
          } else {
            i = this.mEnd;
          }
          this.mRight = i;
        }
        else
        {
          if (this.mEnd == Integer.MIN_VALUE) {
            i = this.mExplicitLeft;
          } else {
            i = this.mEnd;
          }
          this.mLeft = i;
          if (this.mStart == Integer.MIN_VALUE) {
            i = this.mExplicitRight;
          } else {
            i = this.mStart;
          }
          this.mRight = i;
        }
      }
    }
  }
  
  public void setRelative(int paramInt1, int paramInt2)
  {
    this.mStart = paramInt1;
    this.mEnd = paramInt2;
    this.mIsRelative = true;
    if (!this.mIsRtl)
    {
      if (paramInt1 != Integer.MIN_VALUE) {
        this.mLeft = paramInt1;
      }
      if (paramInt2 != Integer.MIN_VALUE) {
        this.mRight = paramInt2;
      }
    }
    else
    {
      if (paramInt2 != Integer.MIN_VALUE) {
        this.mLeft = paramInt2;
      }
      if (paramInt1 != Integer.MIN_VALUE) {
        this.mRight = paramInt1;
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\RtlSpacingHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */