package android.support.v7.internal.view.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R.bool;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyCharacterMap.KeyData;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MenuBuilder
  implements SupportMenu
{
  private static final String ACTION_VIEW_STATES_KEY = "android:menu:actionviewstates";
  private static final String EXPANDED_ACTION_VIEW_ID = "android:menu:expandedactionview";
  private static final String PRESENTER_KEY = "android:menu:presenters";
  private static final String TAG = "MenuBuilder";
  private static final int[] sCategoryToOrder;
  private ArrayList<MenuItemImpl> mActionItems;
  private Callback mCallback;
  private final Context mContext;
  private ContextMenu.ContextMenuInfo mCurrentMenuInfo;
  private int mDefaultShowAsAction = 0;
  private MenuItemImpl mExpandedItem;
  private SparseArray<Parcelable> mFrozenViewStates;
  Drawable mHeaderIcon;
  CharSequence mHeaderTitle;
  View mHeaderView;
  private boolean mIsActionItemsStale;
  private boolean mIsClosing = false;
  private boolean mIsVisibleItemsStale;
  private ArrayList<MenuItemImpl> mItems;
  private boolean mItemsChangedWhileDispatchPrevented = false;
  private ArrayList<MenuItemImpl> mNonActionItems;
  private boolean mOptionalIconsVisible = false;
  private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters = new CopyOnWriteArrayList();
  private boolean mPreventDispatchingItemsChanged = false;
  private boolean mQwertyMode;
  private final Resources mResources;
  private boolean mShortcutsVisible;
  private ArrayList<MenuItemImpl> mTempShortcutItemList = new ArrayList();
  private ArrayList<MenuItemImpl> mVisibleItems;
  
  static
  {
    int[] arrayOfInt = new int[6];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 4;
    arrayOfInt[2] = 5;
    arrayOfInt[3] = 3;
    arrayOfInt[4] = 2;
    arrayOfInt[5] = 0;
    sCategoryToOrder = arrayOfInt;
  }
  
  public MenuBuilder(Context paramContext)
  {
    this.mContext = paramContext;
    this.mResources = paramContext.getResources();
    this.mItems = new ArrayList();
    this.mVisibleItems = new ArrayList();
    this.mIsVisibleItemsStale = true;
    this.mActionItems = new ArrayList();
    this.mNonActionItems = new ArrayList();
    this.mIsActionItemsStale = true;
    setShortcutsVisibleInner(true);
  }
  
  private MenuItem addInternal(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence)
  {
    int i = getOrdering(paramInt3);
    MenuItemImpl localMenuItemImpl = createNewMenuItem(paramInt1, paramInt2, paramInt3, i, paramCharSequence, this.mDefaultShowAsAction);
    if (this.mCurrentMenuInfo != null) {
      localMenuItemImpl.setMenuInfo(this.mCurrentMenuInfo);
    }
    this.mItems.add(findInsertIndex(this.mItems, i), localMenuItemImpl);
    onItemsChanged(true);
    return localMenuItemImpl;
  }
  
  private MenuItemImpl createNewMenuItem(int paramInt1, int paramInt2, int paramInt3, int paramInt4, CharSequence paramCharSequence, int paramInt5)
  {
    return new MenuItemImpl(this, paramInt1, paramInt2, paramInt3, paramInt4, paramCharSequence, paramInt5);
  }
  
  private void dispatchPresenterUpdate(boolean paramBoolean)
  {
    Iterator localIterator;
    if (!this.mPresenters.isEmpty())
    {
      stopDispatchingItemsChanged();
      localIterator = this.mPresenters.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        startDispatchingItemsChanged();
        return;
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
      if (localMenuPresenter != null) {
        localMenuPresenter.updateMenuView(paramBoolean);
      } else {
        this.mPresenters.remove(localWeakReference);
      }
    }
  }
  
  private void dispatchRestoreInstanceState(Bundle paramBundle)
  {
    SparseArray localSparseArray = paramBundle.getSparseParcelableArray("android:menu:presenters");
    Iterator localIterator;
    if ((localSparseArray != null) && (!this.mPresenters.isEmpty())) {
      localIterator = this.mPresenters.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
      Parcelable localParcelable;
      if (localMenuPresenter != null)
      {
        int i = localMenuPresenter.getId();
        if (i > 0)
        {
          localParcelable = (Parcelable)localSparseArray.get(i);
          if (localParcelable != null) {
            localMenuPresenter.onRestoreInstanceState(localParcelable);
          }
        }
      }
      else
      {
        this.mPresenters.remove(localParcelable);
      }
    }
  }
  
  private void dispatchSaveInstanceState(Bundle paramBundle)
  {
    SparseArray localSparseArray;
    Iterator localIterator;
    if (!this.mPresenters.isEmpty())
    {
      localSparseArray = new SparseArray();
      localIterator = this.mPresenters.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        paramBundle.putSparseParcelableArray("android:menu:presenters", localSparseArray);
        return;
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      Object localObject = (MenuPresenter)localWeakReference.get();
      int i;
      if (localObject != null)
      {
        i = ((MenuPresenter)localObject).getId();
        if (i > 0)
        {
          localObject = ((MenuPresenter)localObject).onSaveInstanceState();
          if (localObject != null) {
            localSparseArray.put(i, localObject);
          }
        }
      }
      else
      {
        this.mPresenters.remove(i);
      }
    }
  }
  
  private boolean dispatchSubMenuSelected(SubMenuBuilder paramSubMenuBuilder, MenuPresenter paramMenuPresenter)
  {
    if (!this.mPresenters.isEmpty())
    {
      bool = false;
      if (paramMenuPresenter != null) {
        bool = paramMenuPresenter.onSubMenuSelected(paramSubMenuBuilder);
      }
      Iterator localIterator = this.mPresenters.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
        if (localMenuPresenter != null)
        {
          if (!bool) {
            bool = localMenuPresenter.onSubMenuSelected(paramSubMenuBuilder);
          }
        }
        else {
          this.mPresenters.remove(localWeakReference);
        }
      }
    }
    boolean bool = false;
    return bool;
  }
  
  private static int findInsertIndex(ArrayList<MenuItemImpl> paramArrayList, int paramInt)
  {
    for (int i = -1 + paramArrayList.size();; i--)
    {
      if (i < 0) {
        return 0;
      }
      if (((MenuItemImpl)paramArrayList.get(i)).getOrdering() <= paramInt) {
        break;
      }
    }
    i += 1;
    return i;
  }
  
  private static int getOrdering(int paramInt)
  {
    int i = (0xFFFF0000 & paramInt) >> 16;
    if ((i >= 0) && (i < sCategoryToOrder.length)) {
      return sCategoryToOrder[i] << 16 | 0xFFFF & paramInt;
    }
    throw new IllegalArgumentException("order does not contain a valid category.");
  }
  
  private void removeItemAtInt(int paramInt, boolean paramBoolean)
  {
    if ((paramInt >= 0) && (paramInt < this.mItems.size()))
    {
      this.mItems.remove(paramInt);
      if (paramBoolean) {
        onItemsChanged(true);
      }
    }
  }
  
  private void setHeaderInternal(int paramInt1, CharSequence paramCharSequence, int paramInt2, Drawable paramDrawable, View paramView)
  {
    Resources localResources = getResources();
    if (paramView == null)
    {
      if (paramInt1 <= 0)
      {
        if (paramCharSequence != null) {
          this.mHeaderTitle = paramCharSequence;
        }
      }
      else {
        this.mHeaderTitle = localResources.getText(paramInt1);
      }
      if (paramInt2 <= 0)
      {
        if (paramDrawable != null) {
          this.mHeaderIcon = paramDrawable;
        }
      }
      else {
        this.mHeaderIcon = ContextCompat.getDrawable(getContext(), paramInt2);
      }
      this.mHeaderView = null;
    }
    else
    {
      this.mHeaderView = paramView;
      this.mHeaderTitle = null;
      this.mHeaderIcon = null;
    }
    onItemsChanged(false);
  }
  
  private void setShortcutsVisibleInner(boolean paramBoolean)
  {
    int i = 1;
    if ((!paramBoolean) || (this.mResources.getConfiguration().keyboard == i) || (!this.mResources.getBoolean(R.bool.abc_config_showMenuShortcutsWhenKeyboardPresent))) {
      i = 0;
    }
    this.mShortcutsVisible = i;
  }
  
  public MenuItem add(int paramInt)
  {
    return addInternal(0, 0, 0, this.mResources.getString(paramInt));
  }
  
  public MenuItem add(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return addInternal(paramInt1, paramInt2, paramInt3, this.mResources.getString(paramInt4));
  }
  
  public MenuItem add(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence)
  {
    return addInternal(paramInt1, paramInt2, paramInt3, paramCharSequence);
  }
  
  public MenuItem add(CharSequence paramCharSequence)
  {
    return addInternal(0, 0, 0, paramCharSequence);
  }
  
  public int addIntentOptions(int paramInt1, int paramInt2, int paramInt3, ComponentName paramComponentName, Intent[] paramArrayOfIntent, Intent paramIntent, int paramInt4, MenuItem[] paramArrayOfMenuItem)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    List localList = localPackageManager.queryIntentActivityOptions(paramComponentName, paramArrayOfIntent, paramIntent, 0);
    int j;
    if (localList == null) {
      j = 0;
    } else {
      j = localList.size();
    }
    if ((paramInt4 & 0x1) == 0) {
      removeGroup(paramInt1);
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return j;
      }
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      if (localResolveInfo.specificIndex >= 0) {
        localObject = paramArrayOfIntent[localResolveInfo.specificIndex];
      } else {
        localObject = paramIntent;
      }
      Object localObject = new Intent((Intent)localObject);
      ((Intent)localObject).setComponent(new ComponentName(localResolveInfo.activityInfo.applicationInfo.packageName, localResolveInfo.activityInfo.name));
      localObject = add(paramInt1, paramInt2, paramInt3, localResolveInfo.loadLabel(localPackageManager)).setIcon(localResolveInfo.loadIcon(localPackageManager)).setIntent((Intent)localObject);
      if ((paramArrayOfMenuItem != null) && (localResolveInfo.specificIndex >= 0)) {
        paramArrayOfMenuItem[localResolveInfo.specificIndex] = localObject;
      }
    }
  }
  
  public void addMenuPresenter(MenuPresenter paramMenuPresenter)
  {
    addMenuPresenter(paramMenuPresenter, this.mContext);
  }
  
  public void addMenuPresenter(MenuPresenter paramMenuPresenter, Context paramContext)
  {
    this.mPresenters.add(new WeakReference(paramMenuPresenter));
    paramMenuPresenter.initForMenu(paramContext, this);
    this.mIsActionItemsStale = true;
  }
  
  public SubMenu addSubMenu(int paramInt)
  {
    return addSubMenu(0, 0, 0, this.mResources.getString(paramInt));
  }
  
  public SubMenu addSubMenu(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return addSubMenu(paramInt1, paramInt2, paramInt3, this.mResources.getString(paramInt4));
  }
  
  public SubMenu addSubMenu(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence)
  {
    MenuItemImpl localMenuItemImpl = (MenuItemImpl)addInternal(paramInt1, paramInt2, paramInt3, paramCharSequence);
    SubMenuBuilder localSubMenuBuilder = new SubMenuBuilder(this.mContext, this, localMenuItemImpl);
    localMenuItemImpl.setSubMenu(localSubMenuBuilder);
    return localSubMenuBuilder;
  }
  
  public SubMenu addSubMenu(CharSequence paramCharSequence)
  {
    return addSubMenu(0, 0, 0, paramCharSequence);
  }
  
  public void changeMenuMode()
  {
    if (this.mCallback != null) {
      this.mCallback.onMenuModeChange(this);
    }
  }
  
  public void clear()
  {
    if (this.mExpandedItem != null) {
      collapseItemActionView(this.mExpandedItem);
    }
    this.mItems.clear();
    onItemsChanged(true);
  }
  
  public void clearAll()
  {
    this.mPreventDispatchingItemsChanged = true;
    clear();
    clearHeader();
    this.mPreventDispatchingItemsChanged = false;
    this.mItemsChangedWhileDispatchPrevented = false;
    onItemsChanged(true);
  }
  
  public void clearHeader()
  {
    this.mHeaderIcon = null;
    this.mHeaderTitle = null;
    this.mHeaderView = null;
    onItemsChanged(false);
  }
  
  public void close()
  {
    close(true);
  }
  
  public final void close(boolean paramBoolean)
  {
    Iterator localIterator;
    if (!this.mIsClosing)
    {
      this.mIsClosing = true;
      localIterator = this.mPresenters.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        this.mIsClosing = false;
        return;
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
      if (localMenuPresenter != null) {
        localMenuPresenter.onCloseMenu(this, paramBoolean);
      } else {
        this.mPresenters.remove(localWeakReference);
      }
    }
  }
  
  public boolean collapseItemActionView(MenuItemImpl paramMenuItemImpl)
  {
    if ((!this.mPresenters.isEmpty()) && (this.mExpandedItem == paramMenuItemImpl))
    {
      bool = false;
      stopDispatchingItemsChanged();
      Iterator localIterator = this.mPresenters.iterator();
      for (;;)
      {
        WeakReference localWeakReference;
        if (localIterator.hasNext())
        {
          localWeakReference = (WeakReference)localIterator.next();
          MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
          if (localMenuPresenter != null)
          {
            bool = localMenuPresenter.collapseItemActionView(this, paramMenuItemImpl);
            if (!bool) {
              continue;
            }
          }
        }
        else
        {
          startDispatchingItemsChanged();
          if (!bool) {
            break;
          }
          this.mExpandedItem = null;
          break;
        }
        this.mPresenters.remove(localWeakReference);
      }
    }
    boolean bool = false;
    return bool;
  }
  
  boolean dispatchMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    boolean bool;
    if ((this.mCallback == null) || (!this.mCallback.onMenuItemSelected(paramMenuBuilder, paramMenuItem))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean expandItemActionView(MenuItemImpl paramMenuItemImpl)
  {
    if (!this.mPresenters.isEmpty())
    {
      bool = false;
      stopDispatchingItemsChanged();
      Iterator localIterator = this.mPresenters.iterator();
      for (;;)
      {
        WeakReference localWeakReference;
        if (localIterator.hasNext())
        {
          localWeakReference = (WeakReference)localIterator.next();
          MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
          if (localMenuPresenter != null)
          {
            bool = localMenuPresenter.expandItemActionView(this, paramMenuItemImpl);
            if (!bool) {
              continue;
            }
          }
        }
        else
        {
          startDispatchingItemsChanged();
          if (!bool) {
            break;
          }
          this.mExpandedItem = paramMenuItemImpl;
          break;
        }
        this.mPresenters.remove(localWeakReference);
      }
    }
    boolean bool = false;
    return bool;
  }
  
  public int findGroupIndex(int paramInt)
  {
    return findGroupIndex(paramInt, 0);
  }
  
  public int findGroupIndex(int paramInt1, int paramInt2)
  {
    int i = size();
    if (paramInt2 < 0) {
      paramInt2 = 0;
    }
    for (int j = paramInt2;; j++)
    {
      if (j >= i)
      {
        j = -1;
        break;
      }
      if (((MenuItemImpl)this.mItems.get(j)).getGroupId() == paramInt1) {
        break;
      }
    }
    return j;
  }
  
  public MenuItem findItem(int paramInt)
  {
    int i = size();
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        localObject = null;
        break label76;
      }
      localObject = (MenuItemImpl)this.mItems.get(j);
      if (((MenuItemImpl)localObject).getItemId() == paramInt) {
        break label76;
      }
      if (((MenuItemImpl)localObject).hasSubMenu())
      {
        localObject = ((MenuItemImpl)localObject).getSubMenu().findItem(paramInt);
        if (localObject != null) {
          break;
        }
      }
    }
    Object localObject = localObject;
    label76:
    return (MenuItem)localObject;
  }
  
  public int findItemIndex(int paramInt)
  {
    int j = size();
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        i = -1;
        break;
      }
      if (((MenuItemImpl)this.mItems.get(i)).getItemId() == paramInt) {
        break;
      }
    }
    return i;
  }
  
  MenuItemImpl findItemWithShortcutForKey(int paramInt, KeyEvent paramKeyEvent)
  {
    Object localObject = null;
    ArrayList localArrayList = this.mTempShortcutItemList;
    localArrayList.clear();
    findItemsWithShortcutForKey(localArrayList, paramInt, paramKeyEvent);
    if (!localArrayList.isEmpty())
    {
      int k = paramKeyEvent.getMetaState();
      KeyCharacterMap.KeyData localKeyData = new KeyCharacterMap.KeyData();
      paramKeyEvent.getKeyData(localKeyData);
      int m = localArrayList.size();
      if (m != 1)
      {
        boolean bool = isQwertyMode();
        MenuItemImpl localMenuItemImpl;
        for (int j = 0;; j++)
        {
          if (j >= m) {
            break label191;
          }
          localMenuItemImpl = (MenuItemImpl)localArrayList.get(j);
          int i;
          if (!bool) {
            i = localMenuItemImpl.getNumericShortcut();
          } else {
            i = localMenuItemImpl.getAlphabeticShortcut();
          }
          if (((i == localKeyData.meta[0]) && ((k & 0x2) == 0)) || ((i == localKeyData.meta[2]) && ((k & 0x2) != 0)) || ((bool) && (i == 8) && (paramInt == 67))) {
            break;
          }
        }
        localObject = localMenuItemImpl;
      }
      else
      {
        localObject = (MenuItemImpl)localArrayList.get(0);
      }
    }
    label191:
    return (MenuItemImpl)localObject;
  }
  
  void findItemsWithShortcutForKey(List<MenuItemImpl> paramList, int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool = isQwertyMode();
    int j = paramKeyEvent.getMetaState();
    KeyCharacterMap.KeyData localKeyData = new KeyCharacterMap.KeyData();
    int i;
    if ((paramKeyEvent.getKeyData(localKeyData)) || (paramInt == 67)) {
      i = this.mItems.size();
    }
    for (int k = 0;; k++)
    {
      if (k >= i) {
        return;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(k);
      if (localMenuItemImpl.hasSubMenu()) {
        ((MenuBuilder)localMenuItemImpl.getSubMenu()).findItemsWithShortcutForKey(paramList, paramInt, paramKeyEvent);
      }
      int m;
      if (!bool) {
        m = localMenuItemImpl.getNumericShortcut();
      } else {
        m = localMenuItemImpl.getAlphabeticShortcut();
      }
      if (((j & 0x5) == 0) && (m != 0) && ((m == localKeyData.meta[0]) || (m == localKeyData.meta[2]) || ((bool) && (m == 8) && (paramInt == 67))) && (localMenuItemImpl.isEnabled())) {
        paramList.add(localMenuItemImpl);
      }
    }
  }
  
  public void flagActionItems()
  {
    ArrayList localArrayList = getVisibleItems();
    int i;
    Iterator localIterator;
    if (this.mIsActionItemsStale)
    {
      i = 0;
      localIterator = this.mPresenters.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        int j;
        if (i == 0)
        {
          this.mActionItems.clear();
          this.mNonActionItems.clear();
          this.mNonActionItems.addAll(getVisibleItems());
        }
        else
        {
          this.mActionItems.clear();
          this.mNonActionItems.clear();
          j = localArrayList.size();
        }
        for (i = 0;; i++)
        {
          if (i >= j)
          {
            this.mIsActionItemsStale = false;
            return;
          }
          localObject = (MenuItemImpl)localArrayList.get(i);
          if (!((MenuItemImpl)localObject).isActionButton()) {
            this.mNonActionItems.add(localObject);
          } else {
            this.mActionItems.add(localObject);
          }
        }
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      Object localObject = (MenuPresenter)localWeakReference.get();
      if (localObject != null)
      {
        boolean bool;
        i |= ((MenuPresenter)localObject).flagActionItems();
      }
      else
      {
        this.mPresenters.remove(localWeakReference);
      }
    }
  }
  
  public ArrayList<MenuItemImpl> getActionItems()
  {
    flagActionItems();
    return this.mActionItems;
  }
  
  protected String getActionViewStatesKey()
  {
    return "android:menu:actionviewstates";
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public MenuItemImpl getExpandedItem()
  {
    return this.mExpandedItem;
  }
  
  public Drawable getHeaderIcon()
  {
    return this.mHeaderIcon;
  }
  
  public CharSequence getHeaderTitle()
  {
    return this.mHeaderTitle;
  }
  
  public View getHeaderView()
  {
    return this.mHeaderView;
  }
  
  public MenuItem getItem(int paramInt)
  {
    return (MenuItem)this.mItems.get(paramInt);
  }
  
  public ArrayList<MenuItemImpl> getNonActionItems()
  {
    flagActionItems();
    return this.mNonActionItems;
  }
  
  boolean getOptionalIconsVisible()
  {
    return this.mOptionalIconsVisible;
  }
  
  Resources getResources()
  {
    return this.mResources;
  }
  
  public MenuBuilder getRootMenu()
  {
    return this;
  }
  
  public ArrayList<MenuItemImpl> getVisibleItems()
  {
    if (this.mIsVisibleItemsStale)
    {
      this.mVisibleItems.clear();
      int i = this.mItems.size();
      for (int j = 0;; j++)
      {
        if (j >= i)
        {
          this.mIsVisibleItemsStale = false;
          this.mIsActionItemsStale = true;
          localArrayList = this.mVisibleItems;
          break;
        }
        MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(j);
        if (localMenuItemImpl.isVisible()) {
          this.mVisibleItems.add(localMenuItemImpl);
        }
      }
    }
    ArrayList localArrayList = this.mVisibleItems;
    return localArrayList;
  }
  
  public boolean hasVisibleItems()
  {
    int i = size();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return 0;
      }
      if (((MenuItemImpl)this.mItems.get(j)).isVisible()) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  boolean isQwertyMode()
  {
    return this.mQwertyMode;
  }
  
  public boolean isShortcutKey(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    if (findItemWithShortcutForKey(paramInt, paramKeyEvent) == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isShortcutsVisible()
  {
    return this.mShortcutsVisible;
  }
  
  void onItemActionRequestChanged(MenuItemImpl paramMenuItemImpl)
  {
    this.mIsActionItemsStale = true;
    onItemsChanged(true);
  }
  
  void onItemVisibleChanged(MenuItemImpl paramMenuItemImpl)
  {
    this.mIsVisibleItemsStale = true;
    onItemsChanged(true);
  }
  
  public void onItemsChanged(boolean paramBoolean)
  {
    if (this.mPreventDispatchingItemsChanged)
    {
      this.mItemsChangedWhileDispatchPrevented = true;
    }
    else
    {
      if (paramBoolean)
      {
        this.mIsVisibleItemsStale = true;
        this.mIsActionItemsStale = true;
      }
      dispatchPresenterUpdate(paramBoolean);
    }
  }
  
  public boolean performIdentifierAction(int paramInt1, int paramInt2)
  {
    return performItemAction(findItem(paramInt1), paramInt2);
  }
  
  public boolean performItemAction(MenuItem paramMenuItem, int paramInt)
  {
    return performItemAction(paramMenuItem, null, paramInt);
  }
  
  public boolean performItemAction(MenuItem paramMenuItem, MenuPresenter paramMenuPresenter, int paramInt)
  {
    Object localObject = (MenuItemImpl)paramMenuItem;
    boolean bool;
    if ((localObject != null) && (((MenuItemImpl)localObject).isEnabled()))
    {
      bool = ((MenuItemImpl)localObject).invoke();
      ActionProvider localActionProvider = ((MenuItemImpl)localObject).getSupportActionProvider();
      int i;
      if ((localActionProvider == null) || (!localActionProvider.hasSubMenu())) {
        i = 0;
      } else {
        i = 1;
      }
      if (!((MenuItemImpl)localObject).hasCollapsibleActionView())
      {
        if ((!((MenuItemImpl)localObject).hasSubMenu()) && (i == 0))
        {
          if ((paramInt & 0x1) == 0) {
            close(true);
          }
        }
        else
        {
          close(false);
          if (!((MenuItemImpl)localObject).hasSubMenu()) {
            ((MenuItemImpl)localObject).setSubMenu(new SubMenuBuilder(getContext(), this, (MenuItemImpl)localObject));
          }
          localObject = (SubMenuBuilder)((MenuItemImpl)localObject).getSubMenu();
          if (i != 0) {
            localActionProvider.onPrepareSubMenu((SubMenu)localObject);
          }
          bool |= dispatchSubMenuSelected((SubMenuBuilder)localObject, paramMenuPresenter);
          if (!bool) {
            close(true);
          }
        }
      }
      else
      {
        bool |= ((MenuItemImpl)localObject).expandActionView();
        if (bool) {
          close(true);
        }
      }
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  public boolean performShortcut(int paramInt1, KeyEvent paramKeyEvent, int paramInt2)
  {
    MenuItemImpl localMenuItemImpl = findItemWithShortcutForKey(paramInt1, paramKeyEvent);
    boolean bool = false;
    if (localMenuItemImpl != null) {
      bool = performItemAction(localMenuItemImpl, paramInt2);
    }
    if ((paramInt2 & 0x2) != 0) {
      close(true);
    }
    return bool;
  }
  
  public void removeGroup(int paramInt)
  {
    int k = findGroupIndex(paramInt);
    int j;
    if (k >= 0) {
      j = this.mItems.size() - k;
    }
    int i;
    for (int m = 0;; m = i)
    {
      i = m + 1;
      if ((m >= j) || (((MenuItemImpl)this.mItems.get(k)).getGroupId() != paramInt))
      {
        onItemsChanged(true);
        return;
      }
      removeItemAtInt(k, false);
    }
  }
  
  public void removeItem(int paramInt)
  {
    removeItemAtInt(findItemIndex(paramInt), true);
  }
  
  public void removeItemAt(int paramInt)
  {
    removeItemAtInt(paramInt, true);
  }
  
  public void removeMenuPresenter(MenuPresenter paramMenuPresenter)
  {
    Iterator localIterator = this.mPresenters.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      MenuPresenter localMenuPresenter = (MenuPresenter)localWeakReference.get();
      if ((localMenuPresenter == null) || (localMenuPresenter == paramMenuPresenter)) {
        this.mPresenters.remove(localWeakReference);
      }
    }
  }
  
  public void restoreActionViewStates(Bundle paramBundle)
  {
    SparseArray localSparseArray;
    int k;
    if (paramBundle != null)
    {
      localSparseArray = paramBundle.getSparseParcelableArray(getActionViewStatesKey());
      k = size();
    }
    for (int j = 0;; j++)
    {
      if (j >= k)
      {
        int i = paramBundle.getInt("android:menu:expandedactionview");
        if (i > 0)
        {
          localObject = findItem(i);
          if (localObject != null) {
            MenuItemCompat.expandActionView((MenuItem)localObject);
          }
        }
        return;
      }
      MenuItem localMenuItem = getItem(j);
      Object localObject = MenuItemCompat.getActionView(localMenuItem);
      if ((localObject != null) && (((View)localObject).getId() != -1)) {
        ((View)localObject).restoreHierarchyState(localSparseArray);
      }
      if (localMenuItem.hasSubMenu()) {
        ((SubMenuBuilder)localMenuItem.getSubMenu()).restoreActionViewStates(paramBundle);
      }
    }
  }
  
  public void restorePresenterStates(Bundle paramBundle)
  {
    dispatchRestoreInstanceState(paramBundle);
  }
  
  public void saveActionViewStates(Bundle paramBundle)
  {
    SparseArray localSparseArray = null;
    int i = size();
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        if (localSparseArray != null) {
          paramBundle.putSparseParcelableArray(getActionViewStatesKey(), localSparseArray);
        }
        return;
      }
      MenuItem localMenuItem = getItem(j);
      View localView = MenuItemCompat.getActionView(localMenuItem);
      if ((localView != null) && (localView.getId() != -1))
      {
        if (localSparseArray == null) {
          localSparseArray = new SparseArray();
        }
        localView.saveHierarchyState(localSparseArray);
        if (MenuItemCompat.isActionViewExpanded(localMenuItem)) {
          paramBundle.putInt("android:menu:expandedactionview", localMenuItem.getItemId());
        }
      }
      if (localMenuItem.hasSubMenu()) {
        ((SubMenuBuilder)localMenuItem.getSubMenu()).saveActionViewStates(paramBundle);
      }
    }
  }
  
  public void savePresenterStates(Bundle paramBundle)
  {
    dispatchSaveInstanceState(paramBundle);
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setCurrentMenuInfo(ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    this.mCurrentMenuInfo = paramContextMenuInfo;
  }
  
  public MenuBuilder setDefaultShowAsAction(int paramInt)
  {
    this.mDefaultShowAsAction = paramInt;
    return this;
  }
  
  void setExclusiveItemChecked(MenuItem paramMenuItem)
  {
    int j = paramMenuItem.getGroupId();
    int k = this.mItems.size();
    for (int i = 0;; i++)
    {
      if (i >= k) {
        return;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(i);
      if ((localMenuItemImpl.getGroupId() == j) && (localMenuItemImpl.isExclusiveCheckable()) && (localMenuItemImpl.isCheckable()))
      {
        boolean bool;
        if (localMenuItemImpl != paramMenuItem) {
          bool = false;
        } else {
          bool = true;
        }
        localMenuItemImpl.setCheckedInt(bool);
      }
    }
  }
  
  public void setGroupCheckable(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = this.mItems.size();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(j);
      if (localMenuItemImpl.getGroupId() == paramInt)
      {
        localMenuItemImpl.setExclusiveCheckable(paramBoolean2);
        localMenuItemImpl.setCheckable(paramBoolean1);
      }
    }
  }
  
  public void setGroupEnabled(int paramInt, boolean paramBoolean)
  {
    int i = this.mItems.size();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(j);
      if (localMenuItemImpl.getGroupId() == paramInt) {
        localMenuItemImpl.setEnabled(paramBoolean);
      }
    }
  }
  
  public void setGroupVisible(int paramInt, boolean paramBoolean)
  {
    int k = this.mItems.size();
    int j = 0;
    for (int i = 0;; i++)
    {
      if (i >= k)
      {
        if (j != 0) {
          onItemsChanged(true);
        }
        return;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)this.mItems.get(i);
      if ((localMenuItemImpl.getGroupId() == paramInt) && (localMenuItemImpl.setVisibleInt(paramBoolean))) {
        j = 1;
      }
    }
  }
  
  protected MenuBuilder setHeaderIconInt(int paramInt)
  {
    setHeaderInternal(0, null, paramInt, null, null);
    return this;
  }
  
  protected MenuBuilder setHeaderIconInt(Drawable paramDrawable)
  {
    setHeaderInternal(0, null, 0, paramDrawable, null);
    return this;
  }
  
  protected MenuBuilder setHeaderTitleInt(int paramInt)
  {
    setHeaderInternal(paramInt, null, 0, null, null);
    return this;
  }
  
  protected MenuBuilder setHeaderTitleInt(CharSequence paramCharSequence)
  {
    setHeaderInternal(0, paramCharSequence, 0, null, null);
    return this;
  }
  
  protected MenuBuilder setHeaderViewInt(View paramView)
  {
    setHeaderInternal(0, null, 0, null, paramView);
    return this;
  }
  
  void setOptionalIconsVisible(boolean paramBoolean)
  {
    this.mOptionalIconsVisible = paramBoolean;
  }
  
  public void setQwertyMode(boolean paramBoolean)
  {
    this.mQwertyMode = paramBoolean;
    onItemsChanged(false);
  }
  
  public void setShortcutsVisible(boolean paramBoolean)
  {
    if (this.mShortcutsVisible != paramBoolean)
    {
      setShortcutsVisibleInner(paramBoolean);
      onItemsChanged(false);
    }
  }
  
  public int size()
  {
    return this.mItems.size();
  }
  
  public void startDispatchingItemsChanged()
  {
    this.mPreventDispatchingItemsChanged = false;
    if (this.mItemsChangedWhileDispatchPrevented)
    {
      this.mItemsChangedWhileDispatchPrevented = false;
      onItemsChanged(true);
    }
  }
  
  public void stopDispatchingItemsChanged()
  {
    if (!this.mPreventDispatchingItemsChanged)
    {
      this.mPreventDispatchingItemsChanged = true;
      this.mItemsChangedWhileDispatchPrevented = false;
    }
  }
  
  public static abstract interface ItemInvoker
  {
    public abstract boolean invokeItem(MenuItemImpl paramMenuItemImpl);
  }
  
  public static abstract interface Callback
  {
    public abstract boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
    
    public abstract void onMenuModeChange(MenuBuilder paramMenuBuilder);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\MenuBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */