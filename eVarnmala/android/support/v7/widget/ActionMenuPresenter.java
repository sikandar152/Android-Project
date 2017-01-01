package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ActionProvider.SubUiVisibilityListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.integer;
import android.support.v7.appcompat.R.layout;
import android.support.v7.internal.transition.ActionBarTransition;
import android.support.v7.internal.view.ActionBarPolicy;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.view.menu.ActionMenuItemView.PopupCallback;
import android.support.v7.internal.view.menu.BaseMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.view.menu.MenuView.ItemView;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.internal.widget.TintImageView;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;

public class ActionMenuPresenter
  extends BaseMenuPresenter
  implements ActionProvider.SubUiVisibilityListener
{
  private static final String TAG = "ActionMenuPresenter";
  private final SparseBooleanArray mActionButtonGroups = new SparseBooleanArray();
  private ActionButtonSubmenu mActionButtonPopup;
  private int mActionItemWidthLimit;
  private boolean mExpandedActionViewsExclusive;
  private int mMaxItems;
  private boolean mMaxItemsSet;
  private int mMinCellSize;
  int mOpenSubMenuId;
  private View mOverflowButton;
  private OverflowPopup mOverflowPopup;
  private ActionMenuPopupCallback mPopupCallback;
  final PopupPresenterCallback mPopupPresenterCallback = new PopupPresenterCallback(null);
  private OpenOverflowRunnable mPostedOpenRunnable;
  private boolean mReserveOverflow;
  private boolean mReserveOverflowSet;
  private View mScrapActionButtonView;
  private boolean mStrictWidthLimit;
  private int mWidthLimit;
  private boolean mWidthLimitSet;
  
  public ActionMenuPresenter(Context paramContext)
  {
    super(paramContext, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
  }
  
  private View findViewForItem(MenuItem paramMenuItem)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mMenuView;
    if (localViewGroup != null)
    {
      int j = localViewGroup.getChildCount();
      for (int i = 0;; i++)
      {
        if (i >= j)
        {
          localView = null;
          break;
        }
        localView = localViewGroup.getChildAt(i);
        if (((localView instanceof MenuView.ItemView)) && (((MenuView.ItemView)localView).getItemData() == paramMenuItem)) {
          break;
        }
      }
    }
    View localView = null;
    return localView;
  }
  
  public void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView)
  {
    paramItemView.initialize(paramMenuItemImpl, 0);
    ActionMenuView localActionMenuView = (ActionMenuView)this.mMenuView;
    ActionMenuItemView localActionMenuItemView = (ActionMenuItemView)paramItemView;
    localActionMenuItemView.setItemInvoker(localActionMenuView);
    if (this.mPopupCallback == null) {
      this.mPopupCallback = new ActionMenuPopupCallback(null);
    }
    localActionMenuItemView.setPopupCallback(this.mPopupCallback);
  }
  
  public boolean dismissPopupMenus()
  {
    return hideOverflowMenu() | hideSubMenus();
  }
  
  public boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt)
  {
    boolean bool;
    if (paramViewGroup.getChildAt(paramInt) != this.mOverflowButton) {
      bool = super.filterLeftoverView(paramViewGroup, paramInt);
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean flagActionItems()
  {
    ArrayList localArrayList = this.mMenu.getVisibleItems();
    int k = localArrayList.size();
    int i3 = this.mMaxItems;
    int j = this.mActionItemWidthLimit;
    int i = View.MeasureSpec.makeMeasureSpec(0, 0);
    ViewGroup localViewGroup = (ViewGroup)this.mMenuView;
    int n = 0;
    int i2 = 0;
    int m = 0;
    int i4 = 0;
    for (int i1 = 0;; i1++)
    {
      SparseBooleanArray localSparseBooleanArray;
      if (i1 >= k)
      {
        if ((this.mReserveOverflow) && ((i4 != 0) || (n + i2 > i3))) {
          i3--;
        }
        i2 = i3 - n;
        localSparseBooleanArray = this.mActionButtonGroups;
        localSparseBooleanArray.clear();
        i3 = 0;
        i1 = 0;
        if (this.mStrictWidthLimit)
        {
          i1 = j / this.mMinCellSize;
          i3 = j % this.mMinCellSize;
          i3 = this.mMinCellSize + i3 / i1;
        }
        for (i4 = 0;; i4++)
        {
          if (i4 >= k) {
            return true;
          }
          localMenuItemImpl1 = (MenuItemImpl)localArrayList.get(i4);
          if (!localMenuItemImpl1.requiresActionButton())
          {
            if (!localMenuItemImpl1.requestsActionButton())
            {
              localMenuItemImpl1.setIsActionButton(false);
            }
            else
            {
              int i5 = localMenuItemImpl1.getGroupId();
              int i8 = localSparseBooleanArray.get(i5);
              int i7;
              if (((i2 <= 0) && (i8 == 0)) || (j <= 0) || ((this.mStrictWidthLimit) && (i1 <= 0))) {
                i7 = 0;
              } else {
                i7 = 1;
              }
              if (i7 != 0)
              {
                View localView2 = getItemView(localMenuItemImpl1, this.mScrapActionButtonView, localViewGroup);
                if (this.mScrapActionButtonView == null) {
                  this.mScrapActionButtonView = localView2;
                }
                if (!this.mStrictWidthLimit)
                {
                  localView2.measure(i, i);
                }
                else
                {
                  i9 = ActionMenuView.measureChildForCells(localView2, i3, i1, i, 0);
                  i1 -= i9;
                  if (i9 == 0) {
                    i7 = 0;
                  }
                }
                int i9 = localView2.getMeasuredWidth();
                j -= i9;
                if (m == 0) {
                  m = i9;
                }
                if (!this.mStrictWidthLimit)
                {
                  if (j + m <= 0) {
                    i9 = 0;
                  } else {
                    i9 = 1;
                  }
                  i7 &= i9;
                }
                else
                {
                  int i10;
                  if (j < 0) {
                    i10 = 0;
                  } else {
                    i10 = 1;
                  }
                  i7 &= i10;
                }
              }
              if ((i7 == 0) || (i5 == 0))
              {
                if (i8 != 0)
                {
                  localSparseBooleanArray.put(i5, false);
                  i8 = 0;
                }
              }
              else {
                while (i8 < i4)
                {
                  MenuItemImpl localMenuItemImpl2 = (MenuItemImpl)localArrayList.get(i8);
                  if (localMenuItemImpl2.getGroupId() == i5)
                  {
                    if (localMenuItemImpl2.isActionButton()) {
                      i2++;
                    }
                    localMenuItemImpl2.setIsActionButton(false);
                  }
                  i8++;
                  continue;
                  localSparseBooleanArray.put(i5, true);
                }
              }
              if (i7 != 0) {
                i2--;
              }
              localMenuItemImpl1.setIsActionButton(i7);
            }
          }
          else
          {
            View localView1 = getItemView(localMenuItemImpl1, this.mScrapActionButtonView, localViewGroup);
            if (this.mScrapActionButtonView == null) {
              this.mScrapActionButtonView = localView1;
            }
            if (!this.mStrictWidthLimit) {
              localView1.measure(i, i);
            } else {
              i1 -= ActionMenuView.measureChildForCells(localView1, i3, i1, i, 0);
            }
            int i6 = localView1.getMeasuredWidth();
            j -= i6;
            if (m == 0) {
              m = i6;
            }
            i6 = localMenuItemImpl1.getGroupId();
            if (i6 != 0) {
              localSparseBooleanArray.put(i6, true);
            }
            localMenuItemImpl1.setIsActionButton(true);
          }
        }
      }
      MenuItemImpl localMenuItemImpl1 = (MenuItemImpl)localArrayList.get(i1);
      if (!localMenuItemImpl1.requiresActionButton())
      {
        if (!localMenuItemImpl1.requestsActionButton()) {
          i4 = 1;
        } else {
          i2++;
        }
      }
      else {
        localSparseBooleanArray++;
      }
      if ((this.mExpandedActionViewsExclusive) && (localMenuItemImpl1.isActionViewExpanded())) {
        i3 = 0;
      }
    }
  }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramMenuItemImpl.getActionView();
    if ((localView == null) || (paramMenuItemImpl.hasCollapsibleActionView())) {
      localView = super.getItemView(paramMenuItemImpl, paramView, paramViewGroup);
    }
    int i;
    if (!paramMenuItemImpl.isActionViewExpanded()) {
      i = 0;
    } else {
      i = 8;
    }
    localView.setVisibility(i);
    ActionMenuView localActionMenuView = (ActionMenuView)paramViewGroup;
    ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
    if (!localActionMenuView.checkLayoutParams(localLayoutParams)) {
      localView.setLayoutParams(localActionMenuView.generateLayoutParams(localLayoutParams));
    }
    return localView;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup)
  {
    MenuView localMenuView = super.getMenuView(paramViewGroup);
    ((ActionMenuView)localMenuView).setPresenter(this);
    return localMenuView;
  }
  
  public boolean hideOverflowMenu()
  {
    boolean bool;
    if ((this.mPostedOpenRunnable == null) || (this.mMenuView == null))
    {
      OverflowPopup localOverflowPopup = this.mOverflowPopup;
      if (localOverflowPopup == null)
      {
        bool = false;
      }
      else
      {
        bool.dismiss();
        bool = true;
      }
    }
    else
    {
      ((View)this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
      this.mPostedOpenRunnable = null;
      bool = true;
    }
    return bool;
  }
  
  public boolean hideSubMenus()
  {
    boolean bool;
    if (this.mActionButtonPopup == null)
    {
      bool = false;
    }
    else
    {
      this.mActionButtonPopup.dismiss();
      bool = true;
    }
    return bool;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
  {
    super.initForMenu(paramContext, paramMenuBuilder);
    Resources localResources = paramContext.getResources();
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(paramContext);
    if (!this.mReserveOverflowSet) {
      this.mReserveOverflow = localActionBarPolicy.showsOverflowMenuButton();
    }
    if (!this.mWidthLimitSet) {
      this.mWidthLimit = localActionBarPolicy.getEmbeddedMenuWidthLimit();
    }
    if (!this.mMaxItemsSet) {
      this.mMaxItems = localActionBarPolicy.getMaxActionButtons();
    }
    int i = this.mWidthLimit;
    if (!this.mReserveOverflow)
    {
      this.mOverflowButton = null;
    }
    else
    {
      if (this.mOverflowButton == null)
      {
        this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mOverflowButton.measure(j, j);
      }
      i -= this.mOverflowButton.getMeasuredWidth();
    }
    this.mActionItemWidthLimit = i;
    this.mMinCellSize = ((int)(56.0F * localResources.getDisplayMetrics().density));
    this.mScrapActionButtonView = null;
  }
  
  public boolean isOverflowMenuShowPending()
  {
    boolean bool;
    if ((this.mPostedOpenRunnable == null) && (!isOverflowMenuShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    boolean bool;
    if ((this.mOverflowPopup == null) || (!this.mOverflowPopup.isShowing())) {
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
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    dismissPopupMenus();
    super.onCloseMenu(paramMenuBuilder, paramBoolean);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (!this.mMaxItemsSet) {
      this.mMaxItems = this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
    }
    if (this.mMenu != null) {
      this.mMenu.onItemsChanged(true);
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    Object localObject = (SavedState)paramParcelable;
    if (((SavedState)localObject).openSubMenuId > 0)
    {
      localObject = this.mMenu.findItem(((SavedState)localObject).openSubMenuId);
      if (localObject != null) {
        onSubMenuSelected((SubMenuBuilder)((MenuItem)localObject).getSubMenu());
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState();
    localSavedState.openSubMenuId = this.mOpenSubMenuId;
    return localSavedState;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    boolean bool = false;
    if (paramSubMenuBuilder.hasVisibleItems()) {}
    for (Object localObject = paramSubMenuBuilder;; localObject = (SubMenuBuilder)((SubMenuBuilder)localObject).getParentMenu()) {
      if (((SubMenuBuilder)localObject).getParentMenu() == this.mMenu)
      {
        localObject = findViewForItem(((SubMenuBuilder)localObject).getItem());
        if (localObject == null)
        {
          if (this.mOverflowButton != null) {
            localObject = this.mOverflowButton;
          }
        }
        else
        {
          this.mOpenSubMenuId = paramSubMenuBuilder.getItem().getItemId();
          this.mActionButtonPopup = new ActionButtonSubmenu(this.mContext, paramSubMenuBuilder);
          this.mActionButtonPopup.setAnchorView((View)localObject);
          this.mActionButtonPopup.show();
          super.onSubMenuSelected(paramSubMenuBuilder);
          bool = true;
        }
        return bool;
      }
    }
  }
  
  public void onSubUiVisibilityChanged(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mMenu.close(false);
    } else {
      super.onSubMenuSelected(null);
    }
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    this.mExpandedActionViewsExclusive = paramBoolean;
  }
  
  public void setItemLimit(int paramInt)
  {
    this.mMaxItems = paramInt;
    this.mMaxItemsSet = true;
  }
  
  public void setMenuView(ActionMenuView paramActionMenuView)
  {
    this.mMenuView = paramActionMenuView;
    paramActionMenuView.initialize(this.mMenu);
  }
  
  public void setReserveOverflow(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
    this.mReserveOverflowSet = true;
  }
  
  public void setWidthLimit(int paramInt, boolean paramBoolean)
  {
    this.mWidthLimit = paramInt;
    this.mStrictWidthLimit = paramBoolean;
    this.mWidthLimitSet = true;
  }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl)
  {
    return paramMenuItemImpl.isActionButton();
  }
  
  public boolean showOverflowMenu()
  {
    boolean bool = true;
    if ((!this.mReserveOverflow) || (isOverflowMenuShowing()) || (this.mMenu == null) || (this.mMenuView == null) || (this.mPostedOpenRunnable != null) || (this.mMenu.getNonActionItems().isEmpty()))
    {
      bool = false;
    }
    else
    {
      this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this.mContext, this.mMenu, this.mOverflowButton, bool));
      ((View)this.mMenuView).post(this.mPostedOpenRunnable);
      super.onSubMenuSelected(null);
    }
    return bool;
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    ViewGroup localViewGroup = (ViewGroup)((View)this.mMenuView).getParent();
    if (localViewGroup != null) {
      ActionBarTransition.beginDelayedTransition(localViewGroup);
    }
    super.updateMenuView(paramBoolean);
    ((View)this.mMenuView).requestLayout();
    ArrayList localArrayList;
    int i;
    if (this.mMenu != null)
    {
      localArrayList = this.mMenu.getActionItems();
      i = localArrayList.size();
    }
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        Object localObject;
        if (this.mMenu == null) {
          localObject = null;
        } else {
          localObject = this.mMenu.getNonActionItems();
        }
        j = 0;
        if ((this.mReserveOverflow) && (localObject != null))
        {
          j = ((ArrayList)localObject).size();
          if (j != 1)
          {
            if (j <= 0) {
              j = 0;
            } else {
              j = 1;
            }
          }
          else if (((MenuItemImpl)((ArrayList)localObject).get(0)).isActionViewExpanded()) {
            j = 0;
          } else {
            j = 1;
          }
        }
        if (j == 0)
        {
          if ((this.mOverflowButton != null) && (this.mOverflowButton.getParent() == this.mMenuView)) {
            ((ViewGroup)this.mMenuView).removeView(this.mOverflowButton);
          }
        }
        else
        {
          if (this.mOverflowButton == null) {
            this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
          }
          localObject = (ViewGroup)this.mOverflowButton.getParent();
          if (localObject != this.mMenuView)
          {
            if (localObject != null) {
              ((ViewGroup)localObject).removeView(this.mOverflowButton);
            }
            localObject = (ActionMenuView)this.mMenuView;
            ((ActionMenuView)localObject).addView(this.mOverflowButton, ((ActionMenuView)localObject).generateOverflowButtonLayoutParams());
          }
        }
        ((ActionMenuView)this.mMenuView).setOverflowReserved(this.mReserveOverflow);
        return;
      }
      ActionProvider localActionProvider = ((MenuItemImpl)localArrayList.get(j)).getSupportActionProvider();
      if (localActionProvider != null) {
        localActionProvider.setSubUiVisibilityListener(this);
      }
    }
  }
  
  private class ActionMenuPopupCallback
    extends ActionMenuItemView.PopupCallback
  {
    private ActionMenuPopupCallback() {}
    
    public ListPopupWindow getPopup()
    {
      ListPopupWindow localListPopupWindow;
      if (ActionMenuPresenter.this.mActionButtonPopup == null) {
        localListPopupWindow = null;
      } else {
        localListPopupWindow = ActionMenuPresenter.this.mActionButtonPopup.getPopup();
      }
      return localListPopupWindow;
    }
  }
  
  private class OpenOverflowRunnable
    implements Runnable
  {
    private ActionMenuPresenter.OverflowPopup mPopup;
    
    public OpenOverflowRunnable(ActionMenuPresenter.OverflowPopup paramOverflowPopup)
    {
      this.mPopup = paramOverflowPopup;
    }
    
    public void run()
    {
      ActionMenuPresenter.this.mMenu.changeMenuMode();
      View localView = (View)ActionMenuPresenter.this.mMenuView;
      if ((localView != null) && (localView.getWindowToken() != null) && (this.mPopup.tryShow())) {
        ActionMenuPresenter.access$202(ActionMenuPresenter.this, this.mPopup);
      }
      ActionMenuPresenter.access$302(ActionMenuPresenter.this, null);
    }
  }
  
  private class PopupPresenterCallback
    implements MenuPresenter.Callback
  {
    private PopupPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if ((paramMenuBuilder instanceof SubMenuBuilder)) {
        ((SubMenuBuilder)paramMenuBuilder).getRootMenu().close(false);
      }
      MenuPresenter.Callback localCallback = ActionMenuPresenter.this.getCallback();
      if (localCallback != null) {
        localCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      int i = 0;
      boolean bool;
      if (paramMenuBuilder != null)
      {
        ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder)paramMenuBuilder).getItem().getItemId();
        MenuPresenter.Callback localCallback = ActionMenuPresenter.this.getCallback();
        if (localCallback == null) {
          bool = false;
        } else {
          bool = bool.onOpenSubMenu(paramMenuBuilder);
        }
        bool = bool;
      }
      return bool;
    }
  }
  
  private class ActionButtonSubmenu
    extends MenuPopupHelper
  {
    private SubMenuBuilder mSubMenu;
    
    public ActionButtonSubmenu(Context paramContext, SubMenuBuilder paramSubMenuBuilder)
    {
      super(paramSubMenuBuilder, null, false, R.attr.actionOverflowMenuStyle);
      this.mSubMenu = paramSubMenuBuilder;
      if (!((MenuItemImpl)paramSubMenuBuilder.getItem()).isActionButton())
      {
        View localView;
        if (ActionMenuPresenter.this.mOverflowButton != null) {
          localView = ActionMenuPresenter.this.mOverflowButton;
        } else {
          localView = (View)ActionMenuPresenter.this.mMenuView;
        }
        setAnchorView(localView);
      }
      setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
      boolean bool = false;
      int i = paramSubMenuBuilder.size();
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
      setForceShowIcon(bool);
    }
    
    public void onDismiss()
    {
      super.onDismiss();
      ActionMenuPresenter.access$702(ActionMenuPresenter.this, null);
      ActionMenuPresenter.this.mOpenSubMenuId = 0;
    }
  }
  
  private class OverflowPopup
    extends MenuPopupHelper
  {
    public OverflowPopup(Context paramContext, MenuBuilder paramMenuBuilder, View paramView, boolean paramBoolean)
    {
      super(paramMenuBuilder, paramView, paramBoolean, R.attr.actionOverflowMenuStyle);
      setGravity(8388613);
      setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
    }
    
    public void onDismiss()
    {
      super.onDismiss();
      ActionMenuPresenter.this.mMenu.close();
      ActionMenuPresenter.access$202(ActionMenuPresenter.this, null);
    }
  }
  
  private class OverflowMenuButton
    extends TintImageView
    implements ActionMenuView.ActionMenuChildView
  {
    private final float[] mTempPts = new float[2];
    
    public OverflowMenuButton(Context paramContext)
    {
      super(null, R.attr.actionOverflowButtonStyle);
      setClickable(true);
      setFocusable(true);
      setVisibility(0);
      setEnabled(true);
      setOnTouchListener(new ListPopupWindow.ForwardingListener(this)
      {
        public ListPopupWindow getPopup()
        {
          ListPopupWindow localListPopupWindow;
          if (ActionMenuPresenter.this.mOverflowPopup != null) {
            localListPopupWindow = ActionMenuPresenter.this.mOverflowPopup.getPopup();
          } else {
            localListPopupWindow = null;
          }
          return localListPopupWindow;
        }
        
        public boolean onForwardingStarted()
        {
          ActionMenuPresenter.this.showOverflowMenu();
          return true;
        }
        
        public boolean onForwardingStopped()
        {
          boolean bool;
          if (ActionMenuPresenter.this.mPostedOpenRunnable == null)
          {
            ActionMenuPresenter.this.hideOverflowMenu();
            bool = true;
          }
          else
          {
            bool = false;
          }
          return bool;
        }
      });
    }
    
    public boolean needsDividerAfter()
    {
      return false;
    }
    
    public boolean needsDividerBefore()
    {
      return false;
    }
    
    public boolean performClick()
    {
      if (!super.performClick())
      {
        playSoundEffect(0);
        ActionMenuPresenter.this.showOverflowMenu();
      }
      return true;
    }
    
    protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
      Drawable localDrawable2 = getDrawable();
      Drawable localDrawable1 = getBackground();
      if ((localDrawable2 != null) && (localDrawable1 != null))
      {
        float[] arrayOfFloat = this.mTempPts;
        arrayOfFloat[0] = localDrawable2.getBounds().centerX();
        getImageMatrix().mapPoints(arrayOfFloat);
        int i = (int)arrayOfFloat[0] - getWidth() / 2;
        DrawableCompat.setHotspotBounds(localDrawable1, i, 0, i + getWidth(), getHeight());
      }
      return bool;
    }
  }
  
  private static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ActionMenuPresenter.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActionMenuPresenter.SavedState(paramAnonymousParcel);
      }
      
      public ActionMenuPresenter.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ActionMenuPresenter.SavedState[paramAnonymousInt];
      }
    };
    public int openSubMenuId;
    
    SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.openSubMenuId = paramParcel.readInt();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.openSubMenuId);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\ActionMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */