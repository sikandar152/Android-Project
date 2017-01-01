package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.reflect.Field;

public class ListViewCompat
  extends ListView
{
  public static final int INVALID_POSITION = -1;
  public static final int NO_POSITION = -1;
  private static final int[] STATE_SET_NOTHING;
  private Field mIsChildViewEnabled;
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  int mSelectionTopPadding = 0;
  private GateKeeperDrawable mSelector;
  final Rect mSelectorRect = new Rect();
  
  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    STATE_SET_NOTHING = arrayOfInt;
  }
  
  public ListViewCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    try
    {
      this.mIsChildViewEnabled = AbsListView.class.getDeclaredField("mIsChildViewEnabled");
      this.mIsChildViewEnabled.setAccessible(true);
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        localNoSuchFieldException.printStackTrace();
      }
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    drawSelectorCompat(paramCanvas);
    super.dispatchDraw(paramCanvas);
  }
  
  protected void drawSelectorCompat(Canvas paramCanvas)
  {
    if (!this.mSelectorRect.isEmpty())
    {
      Drawable localDrawable = getSelector();
      localDrawable.setBounds(this.mSelectorRect);
      localDrawable.draw(paramCanvas);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    this.mSelector.setEnabled(true);
    updateSelectorStateCompat();
  }
  
  public int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    int k = -1;
    ListAdapter localListAdapter = getAdapter();
    int j;
    if ((localListAdapter != null) && (!isInTouchMode()))
    {
      j = localListAdapter.getCount();
      if (getAdapter().areAllItemsEnabled())
      {
        if ((paramInt >= 0) && (paramInt < j)) {
          k = paramInt;
        }
      }
      else if (!paramBoolean) {
        for (i = Math.min(paramInt, j - 1); (i >= 0) && (!localListAdapter.isEnabled(i)); i--) {}
      }
    }
    for (int i = Math.max(0, paramInt);; i++) {
      if ((i >= j) || (localListAdapter.isEnabled(i)))
      {
        if ((i >= 0) && (i < j)) {
          k = i;
        }
        return k;
      }
    }
  }
  
  public int measureHeightOfChildrenCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = getListPaddingTop();
    int m = getListPaddingBottom();
    getListPaddingLeft();
    getListPaddingRight();
    int j = getDividerHeight();
    Drawable localDrawable = getDivider();
    ListAdapter localListAdapter = getAdapter();
    int i1;
    View localView;
    if (localListAdapter != null)
    {
      i += m;
      int k;
      if ((j <= 0) || (localDrawable == null)) {
        k = 0;
      } else {
        k = j;
      }
      i1 = 0;
      localView = null;
      int n = 0;
      j = localListAdapter.getCount();
      for (int i2 = 0;; i2++)
      {
        if (i2 >= j)
        {
          i1 = i;
          break label285;
        }
        int i3 = localListAdapter.getItemViewType(i2);
        if (i3 != n)
        {
          localView = null;
          n = i3;
        }
        localView = localListAdapter.getView(i2, localView, this);
        ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
        int i4;
        if ((localLayoutParams == null) || (localLayoutParams.height <= 0)) {
          i4 = View.MeasureSpec.makeMeasureSpec(0, 0);
        } else {
          i4 = View.MeasureSpec.makeMeasureSpec(i4.height, 1073741824);
        }
        localView.measure(paramInt1, i4);
        if (i2 > 0) {
          i += k;
        }
        i += localView.getMeasuredHeight();
        if (i >= paramInt4) {
          break;
        }
        if ((paramInt5 >= 0) && (i2 >= paramInt5)) {
          i1 = i;
        }
      }
      if ((paramInt5 < 0) || (i2 <= paramInt5) || (i1 <= 0) || (i == paramInt4)) {
        i1 = paramInt4;
      }
    }
    else
    {
      i1 = i + localView;
    }
    label285:
    return i1;
  }
  
  protected void positionSelectorCompat(int paramInt, View paramView)
  {
    Object localObject = this.mSelectorRect;
    ((Rect)localObject).set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    ((Rect)localObject).left -= this.mSelectionLeftPadding;
    ((Rect)localObject).top -= this.mSelectionTopPadding;
    ((Rect)localObject).right += this.mSelectionRightPadding;
    ((Rect)localObject).bottom += this.mSelectionBottomPadding;
    try
    {
      boolean bool = this.mIsChildViewEnabled.getBoolean(this);
      if (paramView.isEnabled() != bool)
      {
        localObject = this.mIsChildViewEnabled;
        if (bool) {
          break label130;
        }
      }
      label130:
      for (bool = true;; bool = false)
      {
        ((Field)localObject).set(this, Boolean.valueOf(bool));
        if (paramInt != -1) {
          refreshDrawableState();
        }
        return;
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
  
  protected void positionSelectorLikeFocusCompat(int paramInt, View paramView)
  {
    boolean bool = true;
    Drawable localDrawable = getSelector();
    int i;
    if ((localDrawable == null) || (paramInt == -1)) {
      i = 0;
    } else {
      i = bool;
    }
    if (i != 0) {
      localDrawable.setVisible(false, false);
    }
    positionSelectorCompat(paramInt, paramView);
    if (i != 0)
    {
      Rect localRect = this.mSelectorRect;
      float f1 = localRect.exactCenterX();
      float f2 = localRect.exactCenterY();
      if (getVisibility() != 0) {
        bool = false;
      }
      localDrawable.setVisible(bool, false);
      DrawableCompat.setHotspot(localDrawable, f1, f2);
    }
  }
  
  protected void positionSelectorLikeTouchCompat(int paramInt, View paramView, float paramFloat1, float paramFloat2)
  {
    positionSelectorLikeFocusCompat(paramInt, paramView);
    Drawable localDrawable = getSelector();
    if ((localDrawable != null) && (paramInt != -1)) {
      DrawableCompat.setHotspot(localDrawable, paramFloat1, paramFloat2);
    }
  }
  
  public void setSelector(Drawable paramDrawable)
  {
    this.mSelector = new GateKeeperDrawable(paramDrawable);
    super.setSelector(this.mSelector);
    Rect localRect = new Rect();
    paramDrawable.getPadding(localRect);
    this.mSelectionLeftPadding = localRect.left;
    this.mSelectionTopPadding = localRect.top;
    this.mSelectionRightPadding = localRect.right;
    this.mSelectionBottomPadding = localRect.bottom;
  }
  
  protected void setSelectorEnabled(boolean paramBoolean)
  {
    this.mSelector.setEnabled(paramBoolean);
  }
  
  protected boolean shouldShowSelectorCompat()
  {
    boolean bool;
    if ((!touchModeDrawsInPressedStateCompat()) || (!isPressed())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected boolean touchModeDrawsInPressedStateCompat()
  {
    return false;
  }
  
  protected void updateSelectorStateCompat()
  {
    Drawable localDrawable = getSelector();
    if ((localDrawable != null) && (shouldShowSelectorCompat())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  private static class GateKeeperDrawable
    extends DrawableWrapper
  {
    private boolean mEnabled = true;
    
    public GateKeeperDrawable(Drawable paramDrawable)
    {
      super();
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (this.mEnabled) {
        super.draw(paramCanvas);
      }
    }
    
    void setEnabled(boolean paramBoolean)
    {
      this.mEnabled = paramBoolean;
    }
    
    public void setHotspot(float paramFloat1, float paramFloat2)
    {
      if (this.mEnabled) {
        super.setHotspot(paramFloat1, paramFloat2);
      }
    }
    
    public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.mEnabled) {
        super.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public boolean setState(int[] paramArrayOfInt)
    {
      boolean bool;
      if (!this.mEnabled) {
        bool = false;
      } else {
        bool = super.setState(paramArrayOfInt);
      }
      return bool;
    }
    
    public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
    {
      boolean bool;
      if (!this.mEnabled) {
        bool = false;
      } else {
        bool = super.setVisible(paramBoolean1, paramBoolean2);
      }
      return bool;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ListViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */