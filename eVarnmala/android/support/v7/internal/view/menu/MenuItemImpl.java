package android.support.v7.internal.view.menu;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider.VisibilityListener;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.internal.widget.TintManager;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.LinearLayout;

public final class MenuItemImpl
  implements SupportMenuItem
{
  private static final int CHECKABLE = 1;
  private static final int CHECKED = 2;
  private static final int ENABLED = 16;
  private static final int EXCLUSIVE = 4;
  private static final int HIDDEN = 8;
  private static final int IS_ACTION = 32;
  static final int NO_ICON = 0;
  private static final int SHOW_AS_ACTION_MASK = 3;
  private static final String TAG = "MenuItemImpl";
  private static String sDeleteShortcutLabel;
  private static String sEnterShortcutLabel;
  private static String sPrependShortcutLabel;
  private static String sSpaceShortcutLabel;
  private android.support.v4.view.ActionProvider mActionProvider;
  private View mActionView;
  private final int mCategoryOrder;
  private MenuItem.OnMenuItemClickListener mClickListener;
  private int mFlags = 16;
  private final int mGroup;
  private Drawable mIconDrawable;
  private int mIconResId = 0;
  private final int mId;
  private Intent mIntent;
  private boolean mIsActionViewExpanded = false;
  private Runnable mItemCallback;
  private MenuBuilder mMenu;
  private ContextMenu.ContextMenuInfo mMenuInfo;
  private MenuItemCompat.OnActionExpandListener mOnActionExpandListener;
  private final int mOrdering;
  private char mShortcutAlphabeticChar;
  private char mShortcutNumericChar;
  private int mShowAsAction = 0;
  private SubMenuBuilder mSubMenu;
  private CharSequence mTitle;
  private CharSequence mTitleCondensed;
  
  MenuItemImpl(MenuBuilder paramMenuBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, CharSequence paramCharSequence, int paramInt5)
  {
    this.mMenu = paramMenuBuilder;
    this.mId = paramInt2;
    this.mGroup = paramInt1;
    this.mCategoryOrder = paramInt3;
    this.mOrdering = paramInt4;
    this.mTitle = paramCharSequence;
    this.mShowAsAction = paramInt5;
  }
  
  public void actionFormatChanged()
  {
    this.mMenu.onItemActionRequestChanged(this);
  }
  
  public boolean collapseActionView()
  {
    boolean bool = false;
    if ((0x8 & this.mShowAsAction) != 0) {
      if (this.mActionView != null)
      {
        if ((this.mOnActionExpandListener == null) || (this.mOnActionExpandListener.onMenuItemActionCollapse(this))) {
          bool = this.mMenu.collapseItemActionView(this);
        }
      }
      else {
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean expandActionView()
  {
    boolean bool = false;
    if ((hasCollapsibleActionView()) && ((this.mOnActionExpandListener == null) || (this.mOnActionExpandListener.onMenuItemActionExpand(this)))) {
      bool = this.mMenu.expandItemActionView(this);
    }
    return bool;
  }
  
  public android.view.ActionProvider getActionProvider()
  {
    throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()");
  }
  
  public View getActionView()
  {
    View localView;
    if (this.mActionView == null)
    {
      if (this.mActionProvider == null)
      {
        localView = null;
      }
      else
      {
        this.mActionView = this.mActionProvider.onCreateActionView(this);
        localView = this.mActionView;
      }
    }
    else {
      localView = this.mActionView;
    }
    return localView;
  }
  
  public char getAlphabeticShortcut()
  {
    return this.mShortcutAlphabeticChar;
  }
  
  Runnable getCallback()
  {
    return this.mItemCallback;
  }
  
  public int getGroupId()
  {
    return this.mGroup;
  }
  
  public Drawable getIcon()
  {
    Drawable localDrawable;
    if (this.mIconDrawable == null)
    {
      if (this.mIconResId == 0)
      {
        localDrawable = null;
      }
      else
      {
        localDrawable = TintManager.getDrawable(this.mMenu.getContext(), this.mIconResId);
        this.mIconResId = 0;
        this.mIconDrawable = localDrawable;
      }
    }
    else {
      localDrawable = this.mIconDrawable;
    }
    return localDrawable;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  @ViewDebug.CapturedViewProperty
  public int getItemId()
  {
    return this.mId;
  }
  
  public ContextMenu.ContextMenuInfo getMenuInfo()
  {
    return this.mMenuInfo;
  }
  
  public char getNumericShortcut()
  {
    return this.mShortcutNumericChar;
  }
  
  public int getOrder()
  {
    return this.mCategoryOrder;
  }
  
  public int getOrdering()
  {
    return this.mOrdering;
  }
  
  char getShortcut()
  {
    char c;
    if (!this.mMenu.isQwertyMode()) {
      c = this.mShortcutNumericChar;
    } else {
      c = this.mShortcutAlphabeticChar;
    }
    return c;
  }
  
  String getShortcutLabel()
  {
    char c = getShortcut();
    String str;
    if (c != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder(sPrependShortcutLabel);
      switch (c)
      {
      default: 
        localStringBuilder.append(c);
        break;
      case '\b': 
        localStringBuilder.append(sDeleteShortcutLabel);
        break;
      case '\n': 
        localStringBuilder.append(sEnterShortcutLabel);
        break;
      case ' ': 
        localStringBuilder.append(sSpaceShortcutLabel);
      }
      str = localStringBuilder.toString();
    }
    else
    {
      str = "";
    }
    return str;
  }
  
  public SubMenu getSubMenu()
  {
    return this.mSubMenu;
  }
  
  public android.support.v4.view.ActionProvider getSupportActionProvider()
  {
    return this.mActionProvider;
  }
  
  @ViewDebug.CapturedViewProperty
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public CharSequence getTitleCondensed()
  {
    Object localObject;
    if (this.mTitleCondensed == null) {
      localObject = this.mTitle;
    } else {
      localObject = this.mTitleCondensed;
    }
    if ((Build.VERSION.SDK_INT < 18) && (localObject != null) && (!(localObject instanceof String))) {
      localObject = ((CharSequence)localObject).toString();
    }
    return (CharSequence)localObject;
  }
  
  CharSequence getTitleForItemView(MenuView.ItemView paramItemView)
  {
    CharSequence localCharSequence;
    if ((paramItemView == null) || (!paramItemView.prefersCondensedTitle())) {
      localCharSequence = getTitle();
    } else {
      localCharSequence = getTitleCondensed();
    }
    return localCharSequence;
  }
  
  public boolean hasCollapsibleActionView()
  {
    boolean bool = false;
    if ((0x8 & this.mShowAsAction) != 0)
    {
      if ((this.mActionView == null) && (this.mActionProvider != null)) {
        this.mActionView = this.mActionProvider.onCreateActionView(this);
      }
      if (this.mActionView != null) {
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean hasSubMenu()
  {
    boolean bool;
    if (this.mSubMenu == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean invoke()
  {
    boolean bool = true;
    if ((this.mClickListener != null) && (this.mClickListener.onMenuItemClick(this))) {}
    for (;;)
    {
      return bool;
      if (!this.mMenu.dispatchMenuItemSelected(this.mMenu.getRootMenu(), this)) {
        if (this.mItemCallback != null) {
          this.mItemCallback.run();
        } else if (this.mIntent != null) {
          try
          {
            this.mMenu.getContext().startActivity(this.mIntent);
          }
          catch (ActivityNotFoundException localActivityNotFoundException)
          {
            Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", localActivityNotFoundException);
          }
        } else if ((this.mActionProvider == null) || (!this.mActionProvider.onPerformDefaultAction())) {
          bool = false;
        }
      }
    }
  }
  
  public boolean isActionButton()
  {
    boolean bool;
    if ((0x20 & this.mFlags) != 32) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isActionViewExpanded()
  {
    return this.mIsActionViewExpanded;
  }
  
  public boolean isCheckable()
  {
    int i = 1;
    if ((0x1 & this.mFlags) != i) {
      i = 0;
    }
    return i;
  }
  
  public boolean isChecked()
  {
    boolean bool;
    if ((0x2 & this.mFlags) != 2) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isEnabled()
  {
    boolean bool;
    if ((0x10 & this.mFlags) == 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isExclusiveCheckable()
  {
    boolean bool;
    if ((0x4 & this.mFlags) == 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isVisible()
  {
    boolean bool = true;
    if ((this.mActionProvider == null) || (!this.mActionProvider.overridesItemVisibility()))
    {
      if ((0x8 & this.mFlags) != 0) {
        bool = false;
      }
    }
    else if (((0x8 & this.mFlags) != 0) || (!this.mActionProvider.isVisible())) {
      bool = false;
    }
    return bool;
  }
  
  public boolean requestsActionButton()
  {
    int i = 1;
    if ((0x1 & this.mShowAsAction) != i) {
      i = 0;
    }
    return i;
  }
  
  public boolean requiresActionButton()
  {
    boolean bool;
    if ((0x2 & this.mShowAsAction) != 2) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public MenuItem setActionProvider(android.view.ActionProvider paramActionProvider)
  {
    throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()");
  }
  
  public SupportMenuItem setActionView(int paramInt)
  {
    Context localContext = this.mMenu.getContext();
    setActionView(LayoutInflater.from(localContext).inflate(paramInt, new LinearLayout(localContext), false));
    return this;
  }
  
  public SupportMenuItem setActionView(View paramView)
  {
    this.mActionView = paramView;
    this.mActionProvider = null;
    if ((paramView != null) && (paramView.getId() == -1) && (this.mId > 0)) {
      paramView.setId(this.mId);
    }
    this.mMenu.onItemActionRequestChanged(this);
    return this;
  }
  
  public void setActionViewExpanded(boolean paramBoolean)
  {
    this.mIsActionViewExpanded = paramBoolean;
    this.mMenu.onItemsChanged(false);
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar)
  {
    if (this.mShortcutAlphabeticChar != paramChar)
    {
      this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar);
      this.mMenu.onItemsChanged(false);
    }
    return this;
  }
  
  public MenuItem setCallback(Runnable paramRunnable)
  {
    this.mItemCallback = paramRunnable;
    return this;
  }
  
  public MenuItem setCheckable(boolean paramBoolean)
  {
    int i = this.mFlags;
    int k = 0xFFFFFFFE & this.mFlags;
    int j;
    if (!paramBoolean) {
      j = 0;
    } else {
      j = 1;
    }
    this.mFlags = (j | k);
    if (i != this.mFlags) {
      this.mMenu.onItemsChanged(false);
    }
    return this;
  }
  
  public MenuItem setChecked(boolean paramBoolean)
  {
    if ((0x4 & this.mFlags) == 0) {
      setCheckedInt(paramBoolean);
    } else {
      this.mMenu.setExclusiveItemChecked(this);
    }
    return this;
  }
  
  void setCheckedInt(boolean paramBoolean)
  {
    int k = this.mFlags;
    int i = 0xFFFFFFFD & this.mFlags;
    int j;
    if (!paramBoolean) {
      j = 0;
    } else {
      j = 2;
    }
    this.mFlags = (j | i);
    if (k != this.mFlags) {
      this.mMenu.onItemsChanged(false);
    }
  }
  
  public MenuItem setEnabled(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mFlags = (0xFFFFFFEF & this.mFlags);
    } else {
      this.mFlags = (0x10 | this.mFlags);
    }
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public void setExclusiveCheckable(boolean paramBoolean)
  {
    int i = 0xFFFFFFFB & this.mFlags;
    int j;
    if (!paramBoolean) {
      j = 0;
    } else {
      j = 4;
    }
    this.mFlags = (j | i);
  }
  
  public MenuItem setIcon(int paramInt)
  {
    this.mIconDrawable = null;
    this.mIconResId = paramInt;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIcon(Drawable paramDrawable)
  {
    this.mIconResId = 0;
    this.mIconDrawable = paramDrawable;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
    return this;
  }
  
  public void setIsActionButton(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mFlags = (0xFFFFFFDF & this.mFlags);
    } else {
      this.mFlags = (0x20 | this.mFlags);
    }
  }
  
  void setMenuInfo(ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    this.mMenuInfo = paramContextMenuInfo;
  }
  
  public MenuItem setNumericShortcut(char paramChar)
  {
    if (this.mShortcutNumericChar != paramChar)
    {
      this.mShortcutNumericChar = paramChar;
      this.mMenu.onItemsChanged(false);
    }
    return this;
  }
  
  public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener paramOnActionExpandListener)
  {
    throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setOnActionExpandListener()");
  }
  
  public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mClickListener = paramOnMenuItemClickListener;
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2)
  {
    this.mShortcutNumericChar = paramChar1;
    this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar2);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public void setShowAsAction(int paramInt)
  {
    switch (paramInt & 0x3)
    {
    default: 
      throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
    }
    this.mShowAsAction = paramInt;
    this.mMenu.onItemActionRequestChanged(this);
  }
  
  public SupportMenuItem setShowAsActionFlags(int paramInt)
  {
    setShowAsAction(paramInt);
    return this;
  }
  
  void setSubMenu(SubMenuBuilder paramSubMenuBuilder)
  {
    this.mSubMenu = paramSubMenuBuilder;
    paramSubMenuBuilder.setHeaderTitle(getTitle());
  }
  
  public SupportMenuItem setSupportActionProvider(android.support.v4.view.ActionProvider paramActionProvider)
  {
    if (this.mActionProvider != null) {
      this.mActionProvider.setVisibilityListener(null);
    }
    this.mActionView = null;
    this.mActionProvider = paramActionProvider;
    this.mMenu.onItemsChanged(true);
    if (this.mActionProvider != null) {
      this.mActionProvider.setVisibilityListener(new ActionProvider.VisibilityListener()
      {
        public void onActionProviderVisibilityChanged(boolean paramAnonymousBoolean)
        {
          MenuItemImpl.this.mMenu.onItemVisibleChanged(MenuItemImpl.this);
        }
      });
    }
    return this;
  }
  
  public SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener paramOnActionExpandListener)
  {
    this.mOnActionExpandListener = paramOnActionExpandListener;
    return this;
  }
  
  public MenuItem setTitle(int paramInt)
  {
    return setTitle(this.mMenu.getContext().getString(paramInt));
  }
  
  public MenuItem setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    this.mMenu.onItemsChanged(false);
    if (this.mSubMenu != null) {
      this.mSubMenu.setHeaderTitle(paramCharSequence);
    }
    return this;
  }
  
  public MenuItem setTitleCondensed(CharSequence paramCharSequence)
  {
    this.mTitleCondensed = paramCharSequence;
    if (paramCharSequence == null) {}
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setVisible(boolean paramBoolean)
  {
    if (setVisibleInt(paramBoolean)) {
      this.mMenu.onItemVisibleChanged(this);
    }
    return this;
  }
  
  boolean setVisibleInt(boolean paramBoolean)
  {
    boolean bool = false;
    int i = this.mFlags;
    int k = 0xFFFFFFF7 & this.mFlags;
    int j;
    if (!paramBoolean) {
      j = 8;
    } else {
      j = 0;
    }
    this.mFlags = (j | k);
    if (i != this.mFlags) {
      bool = true;
    }
    return bool;
  }
  
  public boolean shouldShowIcon()
  {
    return this.mMenu.getOptionalIconsVisible();
  }
  
  boolean shouldShowShortcut()
  {
    boolean bool;
    if ((!this.mMenu.isShortcutsVisible()) || (getShortcut() == 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean showsTextAsAction()
  {
    boolean bool;
    if ((0x4 & this.mShowAsAction) != 4) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return this.mTitle.toString();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\MenuItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */