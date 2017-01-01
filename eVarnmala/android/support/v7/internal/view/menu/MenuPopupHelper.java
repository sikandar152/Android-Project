package android.support.v7.internal.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.layout;
import android.support.v7.widget.ListPopupWindow;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import java.util.ArrayList;

public class MenuPopupHelper
  implements AdapterView.OnItemClickListener, View.OnKeyListener, ViewTreeObserver.OnGlobalLayoutListener, PopupWindow.OnDismissListener, MenuPresenter
{
  static final int ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
  private static final String TAG = "MenuPopupHelper";
  private final MenuAdapter mAdapter;
  private View mAnchorView;
  private int mContentWidth;
  private final Context mContext;
  private int mDropDownGravity = 0;
  boolean mForceShowIcon;
  private boolean mHasContentWidth;
  private final LayoutInflater mInflater;
  private ViewGroup mMeasureParent;
  private final MenuBuilder mMenu;
  private final boolean mOverflowOnly;
  private ListPopupWindow mPopup;
  private final int mPopupMaxWidth;
  private final int mPopupStyleAttr;
  private final int mPopupStyleRes;
  private MenuPresenter.Callback mPresenterCallback;
  private ViewTreeObserver mTreeObserver;
  
  public MenuPopupHelper(Context paramContext, MenuBuilder paramMenuBuilder)
  {
    this(paramContext, paramMenuBuilder, null, false, R.attr.popupMenuStyle);
  }
  
  public MenuPopupHelper(Context paramContext, MenuBuilder paramMenuBuilder, View paramView)
  {
    this(paramContext, paramMenuBuilder, paramView, false, R.attr.popupMenuStyle);
  }
  
  public MenuPopupHelper(Context paramContext, MenuBuilder paramMenuBuilder, View paramView, boolean paramBoolean, int paramInt)
  {
    this(paramContext, paramMenuBuilder, paramView, paramBoolean, paramInt, 0);
  }
  
  public MenuPopupHelper(Context paramContext, MenuBuilder paramMenuBuilder, View paramView, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(paramContext);
    this.mMenu = paramMenuBuilder;
    this.mAdapter = new MenuAdapter(this.mMenu);
    this.mOverflowOnly = paramBoolean;
    this.mPopupStyleAttr = paramInt1;
    this.mPopupStyleRes = paramInt2;
    Resources localResources = paramContext.getResources();
    this.mPopupMaxWidth = Math.max(localResources.getDisplayMetrics().widthPixels / 2, localResources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
    this.mAnchorView = paramView;
    paramMenuBuilder.addMenuPresenter(this, paramContext);
  }
  
  private int measureContentWidth()
  {
    int k = 0;
    View localView = null;
    int n = 0;
    MenuAdapter localMenuAdapter = this.mAdapter;
    int m = View.MeasureSpec.makeMeasureSpec(0, 0);
    int j = View.MeasureSpec.makeMeasureSpec(0, 0);
    int i1 = localMenuAdapter.getCount();
    int i = 0;
    while (i < i1)
    {
      int i2 = localMenuAdapter.getItemViewType(i);
      if (i2 != n)
      {
        n = i2;
        localView = null;
      }
      if (this.mMeasureParent == null) {
        this.mMeasureParent = new FrameLayout(this.mContext);
      }
      localView = localMenuAdapter.getView(i, localView, this.mMeasureParent);
      localView.measure(m, j);
      i2 = localView.getMeasuredWidth();
      if (i2 < this.mPopupMaxWidth)
      {
        if (i2 > k) {
          k = i2;
        }
        i++;
      }
      else
      {
        k = this.mPopupMaxWidth;
      }
    }
    return k;
  }
  
  public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
  {
    return false;
  }
  
  public void dismiss()
  {
    if (isShowing()) {
      this.mPopup.dismiss();
    }
  }
  
  public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
  {
    return false;
  }
  
  public boolean flagActionItems()
  {
    return false;
  }
  
  public int getId()
  {
    return 0;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup)
  {
    throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
  }
  
  public ListPopupWindow getPopup()
  {
    return this.mPopup;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder) {}
  
  public boolean isShowing()
  {
    boolean bool;
    if ((this.mPopup == null) || (!this.mPopup.isShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    if (paramMenuBuilder == this.mMenu)
    {
      dismiss();
      if (this.mPresenterCallback != null) {
        this.mPresenterCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
      }
    }
  }
  
  public void onDismiss()
  {
    this.mPopup = null;
    this.mMenu.close();
    if (this.mTreeObserver != null)
    {
      if (!this.mTreeObserver.isAlive()) {
        this.mTreeObserver = this.mAnchorView.getViewTreeObserver();
      }
      this.mTreeObserver.removeGlobalOnLayoutListener(this);
      this.mTreeObserver = null;
    }
  }
  
  public void onGlobalLayout()
  {
    if (isShowing())
    {
      View localView = this.mAnchorView;
      if ((localView != null) && (localView.isShown()))
      {
        if (isShowing()) {
          this.mPopup.show();
        }
      }
      else {
        dismiss();
      }
    }
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    MenuAdapter localMenuAdapter = this.mAdapter;
    localMenuAdapter.mAdapterMenu.performItemAction(localMenuAdapter.getItem(paramInt), 0);
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    int i = 1;
    if ((paramKeyEvent.getAction() != i) || (paramInt != 82)) {
      i = 0;
    } else {
      dismiss();
    }
    return i;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {}
  
  public Parcelable onSaveInstanceState()
  {
    return null;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    if (paramSubMenuBuilder.hasVisibleItems())
    {
      MenuPopupHelper localMenuPopupHelper = new MenuPopupHelper(this.mContext, paramSubMenuBuilder, this.mAnchorView);
      localMenuPopupHelper.setCallback(this.mPresenterCallback);
      boolean bool = false;
      i = paramSubMenuBuilder.size();
      int j = 0;
      while (j < i)
      {
        MenuItem localMenuItem = paramSubMenuBuilder.getItem(j);
        if ((!localMenuItem.isVisible()) || (localMenuItem.getIcon() == null)) {
          j++;
        } else {
          bool = true;
        }
      }
      localMenuPopupHelper.setForceShowIcon(bool);
      if (localMenuPopupHelper.tryShow()) {}
    }
    else
    {
      return 0;
    }
    if (this.mPresenterCallback != null) {
      this.mPresenterCallback.onOpenSubMenu(paramSubMenuBuilder);
    }
    int i = 1;
    return i;
  }
  
  public void setAnchorView(View paramView)
  {
    this.mAnchorView = paramView;
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback)
  {
    this.mPresenterCallback = paramCallback;
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    this.mForceShowIcon = paramBoolean;
  }
  
  public void setGravity(int paramInt)
  {
    this.mDropDownGravity = paramInt;
  }
  
  public void show()
  {
    if (tryShow()) {
      return;
    }
    throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
  }
  
  public boolean tryShow()
  {
    int i = 0;
    boolean bool = true;
    this.mPopup = new ListPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
    this.mPopup.setOnDismissListener(this);
    this.mPopup.setOnItemClickListener(this);
    this.mPopup.setAdapter(this.mAdapter);
    this.mPopup.setModal(bool);
    View localView = this.mAnchorView;
    if (localView == null)
    {
      bool = false;
    }
    else
    {
      if (this.mTreeObserver == null) {
        i = bool;
      }
      this.mTreeObserver = localView.getViewTreeObserver();
      if (i != 0) {
        this.mTreeObserver.addOnGlobalLayoutListener(this);
      }
      this.mPopup.setAnchorView(localView);
      this.mPopup.setDropDownGravity(this.mDropDownGravity);
      if (!this.mHasContentWidth)
      {
        this.mContentWidth = measureContentWidth();
        this.mHasContentWidth = bool;
      }
      this.mPopup.setContentWidth(this.mContentWidth);
      this.mPopup.setInputMethodMode(2);
      this.mPopup.show();
      this.mPopup.getListView().setOnKeyListener(this);
    }
    return bool;
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    this.mHasContentWidth = false;
    if (this.mAdapter != null) {
      this.mAdapter.notifyDataSetChanged();
    }
  }
  
  private class MenuAdapter
    extends BaseAdapter
  {
    private MenuBuilder mAdapterMenu;
    private int mExpandedIndex = -1;
    
    public MenuAdapter(MenuBuilder paramMenuBuilder)
    {
      this.mAdapterMenu = paramMenuBuilder;
      findExpandedIndex();
    }
    
    void findExpandedIndex()
    {
      MenuItemImpl localMenuItemImpl = MenuPopupHelper.this.mMenu.getExpandedItem();
      ArrayList localArrayList;
      int i;
      if (localMenuItemImpl != null)
      {
        localArrayList = MenuPopupHelper.this.mMenu.getNonActionItems();
        i = localArrayList.size();
      }
      for (int j = 0;; j++)
      {
        if (j >= i)
        {
          this.mExpandedIndex = -1;
          return;
        }
        if ((MenuItemImpl)localArrayList.get(j) == localMenuItemImpl) {
          break;
        }
      }
      this.mExpandedIndex = j;
    }
    
    public int getCount()
    {
      ArrayList localArrayList;
      if (!MenuPopupHelper.this.mOverflowOnly) {
        localArrayList = this.mAdapterMenu.getVisibleItems();
      } else {
        localArrayList = this.mAdapterMenu.getNonActionItems();
      }
      int i;
      if (this.mExpandedIndex >= 0) {
        i = -1 + localArrayList.size();
      } else {
        i = i.size();
      }
      return i;
    }
    
    public MenuItemImpl getItem(int paramInt)
    {
      ArrayList localArrayList;
      if (!MenuPopupHelper.this.mOverflowOnly) {
        localArrayList = this.mAdapterMenu.getVisibleItems();
      } else {
        localArrayList = this.mAdapterMenu.getNonActionItems();
      }
      if ((this.mExpandedIndex >= 0) && (paramInt >= this.mExpandedIndex)) {
        paramInt++;
      }
      return (MenuItemImpl)localArrayList.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null) {
        paramView = MenuPopupHelper.this.mInflater.inflate(MenuPopupHelper.ITEM_LAYOUT, paramViewGroup, false);
      }
      MenuView.ItemView localItemView = (MenuView.ItemView)paramView;
      if (MenuPopupHelper.this.mForceShowIcon) {
        ((ListMenuItemView)paramView).setForceShowIcon(true);
      }
      localItemView.initialize(getItem(paramInt), 0);
      return paramView;
    }
    
    public void notifyDataSetChanged()
    {
      findExpandedIndex();
      super.notifyDataSetChanged();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\MenuPopupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */