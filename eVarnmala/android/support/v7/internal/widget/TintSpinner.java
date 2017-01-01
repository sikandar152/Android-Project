package android.support.v7.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import java.lang.reflect.Field;

public class TintSpinner
  extends Spinner
{
  private static final int[] TINT_ATTRS;
  
  static
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 16842964;
    arrayOfInt[1] = 16843126;
    TINT_ATTRS = arrayOfInt;
  }
  
  public TintSpinner(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TintSpinner(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842881);
  }
  
  public TintSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, TINT_ATTRS, paramInt, 0);
    setBackgroundDrawable(localTintTypedArray.getDrawable(0));
    if (localTintTypedArray.hasValue(1))
    {
      Drawable localDrawable = localTintTypedArray.getDrawable(1);
      if (Build.VERSION.SDK_INT < 16)
      {
        if (Build.VERSION.SDK_INT >= 11) {
          setPopupBackgroundDrawableV11(this, localDrawable);
        }
      }
      else {
        setPopupBackgroundDrawable(localDrawable);
      }
    }
    localTintTypedArray.recycle();
  }
  
  @TargetApi(11)
  private static void setPopupBackgroundDrawableV11(Spinner paramSpinner, Drawable paramDrawable)
  {
    try
    {
      Object localObject = Spinner.class.getDeclaredField("mPopup");
      ((Field)localObject).setAccessible(true);
      localObject = ((Field)localObject).get(paramSpinner);
      if ((localObject instanceof ListPopupWindow)) {
        ((ListPopupWindow)localObject).setBackgroundDrawable(paramDrawable);
      }
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        localNoSuchFieldException.printStackTrace();
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        localIllegalAccessException.printStackTrace();
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\TintSpinner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */