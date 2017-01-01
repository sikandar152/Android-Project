package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.support.v7.internal.view.menu.MenuBuilder.ItemInvoker;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.widget.ViewUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

public class ActionMenuView
  extends LinearLayoutCompat
  implements MenuBuilder.ItemInvoker, MenuView
{
  static final int GENERATED_ITEM_PADDING = 4;
  static final int MIN_CELL_SIZE = 56;
  private static final String TAG = "ActionMenuView";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private Context mContext;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mGeneratedItemPadding;
  private MenuBuilder mMenu;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private int mMinCellSize;
  private OnMenuItemClickListener mOnMenuItemClickListener;
  private Context mPopupContext;
  private int mPopupTheme;
  private ActionMenuPresenter mPresenter;
  private boolean mReserveOverflow;
  
  public ActionMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    setBaselineAligned(false);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mMinCellSize = ((int)(56.0F * f));
    this.mGeneratedItemPadding = ((int)(4.0F * f));
    this.mPopupContext = paramContext;
    this.mPopupTheme = 0;
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    ActionMenuItemView localActionMenuItemView;
    if (!(paramView instanceof ActionMenuItemView)) {
      localActionMenuItemView = null;
    } else {
      localActionMenuItemView = (ActionMenuItemView)paramView;
    }
    boolean bool;
    if ((localActionMenuItemView == null) || (!localActionMenuItemView.hasText())) {
      bool = false;
    } else {
      bool = true;
    }
    int j = 0;
    if ((paramInt2 > 0) && ((!bool) || (paramInt2 >= 2)))
    {
      paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, Integer.MIN_VALUE), i);
      int k = paramView.getMeasuredWidth();
      j = k / paramInt1;
      if (k % paramInt1 != 0) {
        j++;
      }
      if ((bool) && (j < 2)) {
        j = 2;
      }
    }
    if ((localLayoutParams.isOverflowButton) || (!bool)) {
      bool = false;
    } else {
      bool = true;
    }
    localLayoutParams.expandable = bool;
    localLayoutParams.cellsUsed = j;
    paramView.measure(View.MeasureSpec.makeMeasureSpec(j * paramInt1, 1073741824), i);
    return j;
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt2);
    int n = getPaddingLeft() + getPaddingRight();
    int i11 = getPaddingTop() + getPaddingBottom();
    int j = getChildMeasureSpec(paramInt2, i11, -2);
    m -= n;
    int i1 = m / this.mMinCellSize;
    n = m % this.mMinCellSize;
    if (i1 != 0)
    {
      n = this.mMinCellSize + n / i1;
      int i4 = i1;
      int i2 = 0;
      int i7 = 0;
      int i10 = 0;
      int i6 = 0;
      int i8 = 0;
      long l1 = 0L;
      i1 = getChildCount();
      long l3;
      for (int i13 = 0;; l3++)
      {
        int i3;
        LayoutParams localLayoutParams1;
        LayoutParams localLayoutParams2;
        int i9;
        int i12;
        boolean bool;
        if (i13 >= i1)
        {
          int i15;
          if ((i8 == 0) || (i6 != 2)) {
            i15 = 0;
          } else {
            i15 = 1;
          }
          i11 = 0;
          int i20;
          int i16;
          if ((i10 > 0) && (i4 > 0))
          {
            i20 = Integer.MAX_VALUE;
            l3 = 0L;
            i16 = 0;
          }
          for (int i19 = 0;; i19++)
          {
            LayoutParams localLayoutParams4;
            if (i19 >= i1)
            {
              l1 |= l3;
              View localView3;
              if (i16 <= i4)
              {
                int i18 = i20 + 1;
                for (i19 = 0;; i19++)
                {
                  if (i19 >= i1)
                  {
                    i11 = 1;
                    break;
                  }
                  localView3 = getChildAt(i19);
                  localLayoutParams4 = (LayoutParams)localView3.getLayoutParams();
                  if ((l3 & 1 << i19) != 0L)
                  {
                    if ((i15 != 0) && (localLayoutParams4.preventEdgeOffset) && (i4 == 1)) {
                      localView3.setPadding(n + this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                    }
                    localLayoutParams4.cellsUsed = (1 + localLayoutParams4.cellsUsed);
                    localLayoutParams4.expanded = true;
                    i4--;
                  }
                  else if (localLayoutParams4.cellsUsed == i18)
                  {
                    l1 |= 1 << i19;
                  }
                }
              }
              if ((i8 != 0) || (i6 != 1)) {
                i8 = 0;
              } else {
                i8 = 1;
              }
              if ((i4 > 0) && (l1 != 0L) && ((i4 < i6 - 1) || (i8 != 0) || (i7 > 1)))
              {
                float f = Long.bitCount(l1);
                if (i8 == 0)
                {
                  if (((0x1 & l1) != 0L) && (!((LayoutParams)getChildAt(0).getLayoutParams()).preventEdgeOffset)) {
                    f -= 0.5F;
                  }
                  if (((l1 & 1 << i1 - 1) != 0L) && (!((LayoutParams)getChildAt(i1 - 1).getLayoutParams()).preventEdgeOffset)) {
                    f -= 0.5F;
                  }
                }
                if (f <= 0.0F) {
                  i4 = 0;
                } else {
                  i4 = (int)(i4 * n / f);
                }
              }
              for (i7 = 0;; i7++)
              {
                if (i7 >= i1)
                {
                  if (localView3 != 0) {}
                  for (i3 = 0;; i3++)
                  {
                    if (i3 >= i1)
                    {
                      if (i != 1073741824) {
                        k = i2;
                      }
                      setMeasuredDimension(m, k);
                      break;
                    }
                    View localView1 = getChildAt(i3);
                    localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
                    if (localLayoutParams1.expanded) {
                      localView1.measure(View.MeasureSpec.makeMeasureSpec(n * localLayoutParams1.cellsUsed + localLayoutParams1.extraPixels, 1073741824), j);
                    }
                  }
                }
                if ((i3 & 1 << i7) != 0L)
                {
                  View localView2 = getChildAt(i7);
                  localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
                  if (!(localView2 instanceof ActionMenuItemView))
                  {
                    if (!localLayoutParams2.isOverflowButton)
                    {
                      if (i7 != 0) {
                        localLayoutParams2.leftMargin = (localLayoutParams1 / 2);
                      }
                      i9 = i1 - 1;
                      if (i7 != i9) {
                        localLayoutParams2.rightMargin = (localLayoutParams1 / 2);
                      }
                    }
                    else
                    {
                      localLayoutParams2.extraPixels = localLayoutParams1;
                      localLayoutParams2.expanded = true;
                      localLayoutParams2.rightMargin = (-localLayoutParams1 / 2);
                      i12 = 1;
                    }
                  }
                  else
                  {
                    localLayoutParams2.extraPixels = localLayoutParams1;
                    localLayoutParams2.expanded = true;
                    if ((i7 == 0) && (!localLayoutParams2.preventEdgeOffset)) {
                      localLayoutParams2.leftMargin = (-localLayoutParams1 / 2);
                    }
                    i12 = 1;
                  }
                }
              }
            }
            LayoutParams localLayoutParams6 = (LayoutParams)getChildAt(i19).getLayoutParams();
            if (localLayoutParams6.expandable) {
              if (localLayoutParams6.cellsUsed >= i20)
              {
                if (localLayoutParams6.cellsUsed == i20)
                {
                  l3 |= 1 << i19;
                  localLayoutParams4++;
                }
              }
              else
              {
                i20 = localLayoutParams6.cellsUsed;
                l3 = 1 << i19;
                bool = true;
              }
            }
          }
        }
        View localView4 = getChildAt(l3);
        if (localView4.getVisibility() != 8)
        {
          bool = localView4 instanceof ActionMenuItemView;
          localLayoutParams2++;
          if (bool) {
            localView4.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
          }
          LayoutParams localLayoutParams3 = (LayoutParams)localView4.getLayoutParams();
          localLayoutParams3.expanded = false;
          localLayoutParams3.extraPixels = 0;
          localLayoutParams3.cellsUsed = 0;
          localLayoutParams3.expandable = false;
          localLayoutParams3.leftMargin = 0;
          localLayoutParams3.rightMargin = 0;
          if ((!bool) || (!((ActionMenuItemView)localView4).hasText())) {
            bool = false;
          } else {
            bool = true;
          }
          localLayoutParams3.preventEdgeOffset = bool;
          if (!localLayoutParams3.isOverflowButton) {
            int i17 = localLayoutParams1;
          } else {
            localLayoutParams5 = 1;
          }
          LayoutParams localLayoutParams5 = measureChildForCells(localView4, n, localLayoutParams5, j, i12);
          i7 = Math.max(i7, localLayoutParams5);
          if (localLayoutParams3.expandable) {
            i10++;
          }
          if (localLayoutParams3.isOverflowButton) {
            i9 = 1;
          }
          int i5;
          localLayoutParams1 -= localLayoutParams5;
          int i14 = localView4.getMeasuredHeight();
          i2 = Math.max(i2, i14);
          if (localLayoutParams5 == 1)
          {
            long l2;
            i3 |= 1 << l3;
          }
        }
      }
    }
    setMeasuredDimension(m, 0);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((paramLayoutParams == null) || (!(paramLayoutParams instanceof LayoutParams))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void dismissPopupMenus()
  {
    if (this.mPresenter != null) {
      this.mPresenter.dismissPopupMenus();
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams = new LayoutParams(-2, -2);
    localLayoutParams.gravity = 16;
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    LayoutParams localLayoutParams;
    if (paramLayoutParams == null)
    {
      localLayoutParams = generateDefaultLayoutParams();
    }
    else
    {
      if (!(paramLayoutParams instanceof LayoutParams)) {
        localLayoutParams = new LayoutParams(paramLayoutParams);
      } else {
        localLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
      }
      if (localLayoutParams.gravity <= 0) {
        localLayoutParams.gravity = 16;
      }
    }
    return localLayoutParams;
  }
  
  public LayoutParams generateOverflowButtonLayoutParams()
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    localLayoutParams.isOverflowButton = true;
    return localLayoutParams;
  }
  
  public Menu getMenu()
  {
    if (this.mMenu == null)
    {
      Object localObject = getContext();
      this.mMenu = new MenuBuilder((Context)localObject);
      this.mMenu.setCallback(new MenuBuilderCallback(null));
      this.mPresenter = new ActionMenuPresenter((Context)localObject);
      this.mPresenter.setReserveOverflow(true);
      ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
      if (this.mActionMenuPresenterCallback == null) {
        localObject = new ActionMenuPresenterCallback(null);
      } else {
        localObject = this.mActionMenuPresenterCallback;
      }
      localActionMenuPresenter.setCallback((MenuPresenter.Callback)localObject);
      this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
      this.mPresenter.setMenuView(this);
    }
    return this.mMenu;
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  public int getWindowAnimations()
  {
    return 0;
  }
  
  protected boolean hasSupportDividerBeforeChildAt(int paramInt)
  {
    boolean bool;
    if (paramInt != 0)
    {
      View localView1 = getChildAt(paramInt - 1);
      View localView2 = getChildAt(paramInt);
      bool = false;
      if ((paramInt < getChildCount()) && ((localView1 instanceof ActionMenuChildView))) {
        bool = false | ((ActionMenuChildView)localView1).needsDividerAfter();
      }
      if ((paramInt > 0) && ((localView2 instanceof ActionMenuChildView))) {
        bool |= ((ActionMenuChildView)localView2).needsDividerBefore();
      }
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  public boolean hideOverflowMenu()
  {
    boolean bool;
    if ((this.mPresenter == null) || (!this.mPresenter.hideOverflowMenu())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    this.mMenu = paramMenuBuilder;
  }
  
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl)
  {
    return this.mMenu.performItemAction(paramMenuItemImpl, 0);
  }
  
  public boolean isOverflowMenuShowPending()
  {
    boolean bool;
    if ((this.mPresenter == null) || (!this.mPresenter.isOverflowMenuShowPending())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    boolean bool;
    if ((this.mPresenter == null) || (!this.mPresenter.isOverflowMenuShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOverflowReserved()
  {
    return this.mReserveOverflow;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (Build.VERSION.SDK_INT >= 8) {
      super.onConfigurationChanged(paramConfiguration);
    }
    this.mPresenter.updateMenuView(false);
    if ((this.mPresenter != null) && (this.mPresenter.isOverflowMenuShowing()))
    {
      this.mPresenter.hideOverflowMenu();
      this.mPresenter.showOverflowMenu();
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    dismissPopupMenus();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mFormatItems)
    {
      int j = getChildCount();
      int i = (paramInt4 - paramInt2) / 2;
      int i3 = getDividerWidth();
      int i4 = 0;
      int n = 0;
      int m = paramInt3 - paramInt1 - getPaddingRight() - getPaddingLeft();
      int i7 = 0;
      boolean bool = ViewUtils.isLayoutRtl(this);
      int i2;
      for (int i1 = 0;; i2++)
      {
        View localView1;
        int k;
        if (i1 >= j)
        {
          if ((j != 1) || (i7 != 0))
          {
            if (i7 == 0) {
              i1 = 1;
            } else {
              i1 = 0;
            }
            n -= i1;
            if (n <= 0) {
              m = 0;
            } else {
              m /= n;
            }
            m = Math.max(0, m);
            Object localObject;
            int i5;
            if (!bool)
            {
              i3 = getPaddingLeft();
              for (n = 0; n < j; n++)
              {
                localObject = getChildAt(n);
                LayoutParams localLayoutParams1 = (LayoutParams)((View)localObject).getLayoutParams();
                if ((((View)localObject).getVisibility() != 8) && (!localLayoutParams1.isOverflowButton))
                {
                  i4 = i3 + localLayoutParams1.leftMargin;
                  i7 = ((View)localObject).getMeasuredWidth();
                  i3 = ((View)localObject).getMeasuredHeight();
                  i5 = i - i3 / 2;
                  ((View)localObject).layout(i4, i5, i4 + i7, i5 + i3);
                  i3 = i4 + (m + (i7 + localLayoutParams1.rightMargin));
                }
              }
            }
            i3 = getWidth() - getPaddingRight();
            for (i2 = 0; i2 < j; i2++)
            {
              localView1 = getChildAt(i2);
              localObject = (LayoutParams)localView1.getLayoutParams();
              if ((localView1.getVisibility() != 8) && (!((LayoutParams)localObject).isOverflowButton))
              {
                i4 = i3 - ((LayoutParams)localObject).rightMargin;
                i5 = localView1.getMeasuredWidth();
                i7 = localView1.getMeasuredHeight();
                i3 = i - i7 / 2;
                localView1.layout(i4 - i5, i3, i4, i3 + i7);
                i3 = i4 - (m + (i5 + ((LayoutParams)localObject).leftMargin));
              }
            }
          }
          localView1 = getChildAt(0);
          j = localView1.getMeasuredWidth();
          k = localView1.getMeasuredHeight();
          m = (paramInt3 - paramInt1) / 2 - j / 2;
          i -= k / 2;
          localView1.layout(m, i, m + j, i + k);
          break;
        }
        View localView2 = getChildAt(i2);
        if (localView2.getVisibility() != 8)
        {
          LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
          int i6;
          if (!localLayoutParams2.isOverflowButton)
          {
            i6 = localView2.getMeasuredWidth() + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin;
            i4 += i6;
            m -= i6;
            if (hasSupportDividerBeforeChildAt(i2)) {
              i4 += i3;
            }
            localView1++;
          }
          else
          {
            i7 = i6.getMeasuredWidth();
            if (hasSupportDividerBeforeChildAt(i2)) {
              i7 += i3;
            }
            int i8 = i6.getMeasuredHeight();
            int i11;
            int i10;
            if (k == 0)
            {
              i11 = getWidth() - getPaddingRight() - localLayoutParams2.rightMargin;
              i10 = i11 - i7;
            }
            else
            {
              i10 = getPaddingLeft() + localLayoutParams2.leftMargin;
              i11 = i10 + i7;
            }
            int i9 = i - i8 / 2;
            i8 = i9 + i8;
            i6.layout(i10, i9, i11, i8);
            m -= i7;
            i7 = 1;
          }
        }
      }
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool = this.mFormatItems;
    if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {
      j = 0;
    } else {
      j = 1;
    }
    this.mFormatItems = j;
    if (bool != this.mFormatItems) {
      this.mFormatItemsWidth = 0;
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    if ((this.mFormatItems) && (this.mMenu != null) && (i != this.mFormatItemsWidth))
    {
      this.mFormatItemsWidth = i;
      this.mMenu.onItemsChanged(true);
    }
    int j = getChildCount();
    if ((!this.mFormatItems) || (j <= 0)) {
      for (i = 0;; i++)
      {
        if (i >= j)
        {
          super.onMeasure(paramInt1, paramInt2);
          break;
        }
        LayoutParams localLayoutParams = (LayoutParams)getChildAt(i).getLayoutParams();
        localLayoutParams.rightMargin = 0;
        localLayoutParams.leftMargin = 0;
      }
    }
    onMeasureExactFormat(paramInt1, paramInt2);
  }
  
  public MenuBuilder peekMenu()
  {
    return this.mMenu;
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    this.mPresenter.setExpandedActionViewsExclusive(paramBoolean);
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowReserved(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
  }
  
  public void setPopupTheme(int paramInt)
  {
    if (this.mPopupTheme != paramInt)
    {
      this.mPopupTheme = paramInt;
      if (paramInt != 0) {
        this.mPopupContext = new ContextThemeWrapper(this.mContext, paramInt);
      } else {
        this.mPopupContext = this.mContext;
      }
    }
  }
  
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter)
  {
    this.mPresenter = paramActionMenuPresenter;
    this.mPresenter.setMenuView(this);
  }
  
  public boolean showOverflowMenu()
  {
    boolean bool;
    if ((this.mPresenter == null) || (!this.mPresenter.showOverflowMenu())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static class LayoutParams
    extends LinearLayoutCompat.LayoutParams
  {
    @ViewDebug.ExportedProperty
    public int cellsUsed;
    @ViewDebug.ExportedProperty
    public boolean expandable;
    boolean expanded;
    @ViewDebug.ExportedProperty
    public int extraPixels;
    @ViewDebug.ExportedProperty
    public boolean isOverflowButton;
    @ViewDebug.ExportedProperty
    public boolean preventEdgeOffset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.isOverflowButton = false;
    }
    
    LayoutParams(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt2);
      this.isOverflowButton = paramBoolean;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.isOverflowButton = paramLayoutParams.isOverflowButton;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
  
  public static abstract interface ActionMenuChildView
  {
    public abstract boolean needsDividerAfter();
    
    public abstract boolean needsDividerBefore();
  }
  
  private class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      return false;
    }
  }
  
  private class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    private MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      boolean bool;
      if ((ActionMenuView.this.mOnMenuItemClickListener == null) || (!ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(paramMenuItem))) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (ActionMenuView.this.mMenuBuilderCallback != null) {
        ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(paramMenuBuilder);
      }
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\ActionMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */