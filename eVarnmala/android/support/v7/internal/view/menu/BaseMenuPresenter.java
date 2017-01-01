package android.support.v7.internal.view.menu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public abstract class BaseMenuPresenter
  implements MenuPresenter
{
  private MenuPresenter.Callback mCallback;
  protected Context mContext;
  private int mId;
  protected LayoutInflater mInflater;
  private int mItemLayoutRes;
  protected MenuBuilder mMenu;
  private int mMenuLayoutRes;
  protected MenuView mMenuView;
  protected Context mSystemContext;
  protected LayoutInflater mSystemInflater;
  
  public BaseMenuPresenter(Context paramContext, int paramInt1, int paramInt2)
  {
    this.mSystemContext = paramContext;
    this.mSystemInflater = LayoutInflater.from(paramContext);
    this.mMenuLayoutRes = paramInt1;
    this.mItemLayoutRes = paramInt2;
  }
  
  protected void addItemView(View paramView, int paramInt)
  {
    ViewGroup localViewGroup = (ViewGroup)paramView.getParent();
    if (localViewGroup != null) {
      localViewGroup.removeView(paramView);
    }
    ((ViewGroup)this.mMenuView).addView(paramView, paramInt);
  }
  
  public abstract void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView);
  
  public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
  {
    return false;
  }
  
  public MenuView.ItemView createItemView(ViewGroup paramViewGroup)
  {
    return (MenuView.ItemView)this.mSystemInflater.inflate(this.mItemLayoutRes, paramViewGroup, false);
  }
  
  public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
  {
    return false;
  }
  
  protected boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup.removeViewAt(paramInt);
    return true;
  }
  
  public boolean flagActionItems()
  {
    return false;
  }
  
  public MenuPresenter.Callback getCallback()
  {
    return this.mCallback;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup)
  {
    MenuView.ItemView localItemView;
    if (!(paramView instanceof MenuView.ItemView)) {
      localItemView = createItemView(paramViewGroup);
    } else {
      localItemView = (MenuView.ItemView)paramView;
    }
    bindItemView(paramMenuItemImpl, localItemView);
    return (View)localItemView;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup)
  {
    if (this.mMenuView == null)
    {
      this.mMenuView = ((MenuView)this.mSystemInflater.inflate(this.mMenuLayoutRes, paramViewGroup, false));
      this.mMenuView.initialize(this.mMenu);
      updateMenuView(true);
    }
    return this.mMenuView;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
  {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mMenu = paramMenuBuilder;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    if (this.mCallback != null) {
      this.mCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
    }
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    boolean bool;
    if (this.mCallback == null) {
      bool = false;
    } else {
      bool = this.mCallback.onOpenSubMenu(paramSubMenuBuilder);
    }
    return bool;
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl)
  {
    return true;
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mMenuView;
    int k;
    ArrayList localArrayList;
    int i;
    if (localViewGroup != null)
    {
      k = 0;
      if (this.mMenu != null)
      {
        this.mMenu.flagActionItems();
        localArrayList = this.mMenu.getVisibleItems();
        i = localArrayList.size();
      }
    }
    for (int j = 0;; j++)
    {
      if (j >= i) {
        for (;;)
        {
          if (k >= localViewGroup.getChildCount()) {
            return;
          }
          if (!filterLeftoverView(localViewGroup, k)) {
            k++;
          }
        }
      }
      MenuItemImpl localMenuItemImpl1 = (MenuItemImpl)localArrayList.get(j);
      if (shouldIncludeItem(k, localMenuItemImpl1))
      {
        View localView1 = localViewGroup.getChildAt(k);
        MenuItemImpl localMenuItemImpl2;
        if (!(localView1 instanceof MenuView.ItemView)) {
          localMenuItemImpl2 = null;
        } else {
          localMenuItemImpl2 = ((MenuView.ItemView)localView1).getItemData();
        }
        View localView2 = getItemView(localMenuItemImpl1, localView1, localViewGroup);
        if (localMenuItemImpl1 != localMenuItemImpl2)
        {
          localView2.setPressed(false);
          ViewCompat.jumpDrawablesToCurrentState(localView2);
        }
        if (localView2 != localView1) {
          addItemView(localView2, k);
        }
        k++;
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\BaseMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */