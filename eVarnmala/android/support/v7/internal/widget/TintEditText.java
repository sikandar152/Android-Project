package android.support.v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class TintEditText
  extends EditText
{
  private static final int[] TINT_ATTRS;
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16842964;
    TINT_ATTRS = arrayOfInt;
  }
  
  public TintEditText(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TintEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842862);
  }
  
  public TintEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, TINT_ATTRS, paramInt, 0);
    setBackgroundDrawable(localTintTypedArray.getDrawable(0));
    localTintTypedArray.recycle();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\TintEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */