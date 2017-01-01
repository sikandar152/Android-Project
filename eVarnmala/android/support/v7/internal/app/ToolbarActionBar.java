package android.support.v7.internal.app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBar.OnMenuVisibilityListener;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.internal.view.menu.ListMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.internal.widget.DecorToolbar;
import android.support.v7.internal.widget.ToolbarWidgetWrapper;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.support.v7.widget.WindowCallbackWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;

public class ToolbarActionBar
  extends ActionBar
{
  private DecorToolbar mDecorToolbar;
  private boolean mLastMenuVisibility;
  private ListMenuPresenter mListMenuPresenter;
  private boolean mMenuCallbackSet;
  private final Toolbar.OnMenuItemClickListener mMenuClicker = new Toolbar.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      return ToolbarActionBar.this.mWindowCallback.onMenuItemSelected(0, paramAnonymousMenuItem);
    }
  };
  private final Runnable mMenuInvalidator = new Runnable()
  {
    public void run()
    {
      ToolbarActionBar.this.populateOptionsMenu();
    }
  };
  private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList();
  private Toolbar mToolbar;
  private boolean mToolbarMenuPrepared;
  private Window mWindow;
  private WindowCallback mWindowCallback;
  
  public ToolbarActionBar(Toolbar paramToolbar, CharSequence paramCharSequence, Window paramWindow, WindowCallback paramWindowCallback)
  {
    this.mToolbar = paramToolbar;
    this.mDecorToolbar = new ToolbarWidgetWrapper(paramToolbar, false);
    this.mWindowCallback = new ToolbarCallbackWrapper(paramWindowCallback);
    this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
    paramToolbar.setOnMenuItemClickListener(this.mMenuClicker);
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
    this.mWindow = paramWindow;
  }
  
  private View getListMenuView(Menu paramMenu)
  {
    View localView = null;
    if ((paramMenu != null) && (this.mListMenuPresenter != null) && (this.mListMenuPresenter.getAdapter().getCount() > 0)) {
      localView = (View)this.mListMenuPresenter.getMenuView(this.mToolbar);
    }
    return localView;
  }
  
  private Menu getMenu()
  {
    if (!this.mMenuCallbackSet)
    {
      this.mToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(null), new MenuBuilderCallback(null));
      this.mMenuCallbackSet = true;
    }
    return this.mToolbar.getMenu();
  }
  
  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.add(paramOnMenuVisibilityListener);
  }
  
  public void addTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public boolean collapseActionView()
  {
    boolean bool;
    if (!this.mToolbar.hasExpandedActionView())
    {
      bool = false;
    }
    else
    {
      this.mToolbar.collapseActionView();
      bool = true;
    }
    return bool;
  }
  
  public void dispatchMenuVisibilityChanged(boolean paramBoolean)
  {
    int j;
    if (paramBoolean != this.mLastMenuVisibility)
    {
      this.mLastMenuVisibility = paramBoolean;
      j = this.mMenuVisibilityListeners.size();
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return;
      }
      ((ActionBar.OnMenuVisibilityListener)this.mMenuVisibilityListeners.get(i)).onMenuVisibilityChanged(paramBoolean);
    }
  }
  
  public View getCustomView()
  {
    return this.mDecorToolbar.getCustomView();
  }
  
  public int getDisplayOptions()
  {
    return this.mDecorToolbar.getDisplayOptions();
  }
  
  public float getElevation()
  {
    return ViewCompat.getElevation(this.mToolbar);
  }
  
  public int getHeight()
  {
    return this.mToolbar.getHeight();
  }
  
  public int getNavigationItemCount()
  {
    return 0;
  }
  
  public int getNavigationMode()
  {
    return 0;
  }
  
  public int getSelectedNavigationIndex()
  {
    return -1;
  }
  
  public ActionBar.Tab getSelectedTab()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public CharSequence getSubtitle()
  {
    return this.mToolbar.getSubtitle();
  }
  
  public ActionBar.Tab getTabAt(int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public int getTabCount()
  {
    return 0;
  }
  
  public Context getThemedContext()
  {
    return this.mToolbar.getContext();
  }
  
  public CharSequence getTitle()
  {
    return this.mToolbar.getTitle();
  }
  
  public WindowCallback getWrappedWindowCallback()
  {
    return this.mWindowCallback;
  }
  
  public void hide()
  {
    this.mToolbar.setVisibility(8);
  }
  
  public boolean invalidateOptionsMenu()
  {
    this.mToolbar.removeCallbacks(this.mMenuInvalidator);
    ViewCompat.postOnAnimation(this.mToolbar, this.mMenuInvalidator);
    return true;
  }
  
  public boolean isShowing()
  {
    boolean bool;
    if (this.mToolbar.getVisibility() != 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isTitleTruncated()
  {
    return super.isTitleTruncated();
  }
  
  public ActionBar.Tab newTab()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
  }
  
  public boolean onMenuKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 1) {
      openOptionsMenu();
    }
    return true;
  }
  
  public boolean openOptionsMenu()
  {
    return this.mToolbar.showOverflowMenu();
  }
  
  void populateOptionsMenu()
  {
    MenuBuilder localMenuBuilder = null;
    Menu localMenu = getMenu();
    if ((localMenu instanceof MenuBuilder)) {
      localMenuBuilder = (MenuBuilder)localMenu;
    }
    if (localMenuBuilder != null) {
      localMenuBuilder.stopDispatchingItemsChanged();
    }
    try
    {
      localMenu.clear();
      if ((!this.mWindowCallback.onCreatePanelMenu(0, localMenu)) || (!this.mWindowCallback.onPreparePanel(0, null, localMenu))) {
        localMenu.clear();
      }
      return;
    }
    finally
    {
      if (localMenuBuilder != null) {
        localMenuBuilder.startDispatchingItemsChanged();
      }
    }
  }
  
  public void removeAllTabs()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.remove(paramOnMenuVisibilityListener);
  }
  
  public void removeTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void removeTabAt(int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void selectTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void setBackgroundDrawable(@Nullable Drawable paramDrawable)
  {
    this.mToolbar.setBackgroundDrawable(paramDrawable);
  }
  
  public void setCustomView(int paramInt)
  {
    setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(paramInt, this.mToolbar, false));
  }
  
  public void setCustomView(View paramView)
  {
    setCustomView(paramView, new ActionBar.LayoutParams(-2, -2));
  }
  
  public void setCustomView(View paramView, ActionBar.LayoutParams paramLayoutParams)
  {
    paramView.setLayoutParams(paramLayoutParams);
    this.mDecorToolbar.setCustomView(paramView);
  }
  
  public void setDefaultDisplayHomeAsUpEnabled(boolean paramBoolean) {}
  
  public void setDisplayHomeAsUpEnabled(boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = 0;
    } else {
      i = 4;
    }
    setDisplayOptions(i, 4);
  }
  
  public void setDisplayOptions(int paramInt)
  {
    setDisplayOptions(paramInt, -1);
  }
  
  public void setDisplayOptions(int paramInt1, int paramInt2)
  {
    int i = this.mDecorToolbar.getDisplayOptions();
    this.mDecorToolbar.setDisplayOptions(paramInt1 & paramInt2 | i & (paramInt2 ^ 0xFFFFFFFF));
  }
  
  public void setDisplayShowCustomEnabled(boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = 0;
    } else {
      i = 16;
    }
    setDisplayOptions(i, 16);
  }
  
  public void setDisplayShowHomeEnabled(boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = 0;
    } else {
      i = 2;
    }
    setDisplayOptions(i, 2);
  }
  
  public void setDisplayShowTitleEnabled(boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = 0;
    } else {
      i = 8;
    }
    setDisplayOptions(i, 8);
  }
  
  public void setDisplayUseLogoEnabled(boolean paramBoolean)
  {
    int i;
    if (!paramBoolean) {
      i = 0;
    } else {
      i = 1;
    }
    setDisplayOptions(i, 1);
  }
  
  public void setElevation(float paramFloat)
  {
    ViewCompat.setElevation(this.mToolbar, paramFloat);
  }
  
  public void setHomeActionContentDescription(int paramInt)
  {
    this.mDecorToolbar.setNavigationContentDescription(paramInt);
  }
  
  public void setHomeActionContentDescription(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setNavigationContentDescription(paramCharSequence);
  }
  
  public void setHomeAsUpIndicator(int paramInt)
  {
    this.mToolbar.setNavigationIcon(paramInt);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    this.mToolbar.setNavigationIcon(paramDrawable);
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt)
  {
    this.mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setListMenuPresenter(ListMenuPresenter paramListMenuPresenter)
  {
    Object localObject = getMenu();
    if ((localObject instanceof MenuBuilder))
    {
      localObject = (MenuBuilder)localObject;
      if (this.mListMenuPresenter != null)
      {
        this.mListMenuPresenter.setCallback(null);
        ((MenuBuilder)localObject).removeMenuPresenter(this.mListMenuPresenter);
      }
      this.mListMenuPresenter = paramListMenuPresenter;
      if (paramListMenuPresenter != null)
      {
        paramListMenuPresenter.setCallback(new PanelMenuPresenterCallback(null));
        ((MenuBuilder)localObject).addMenuPresenter(paramListMenuPresenter);
      }
    }
  }
  
  public void setListNavigationCallbacks(SpinnerAdapter paramSpinnerAdapter, ActionBar.OnNavigationListener paramOnNavigationListener)
  {
    this.mDecorToolbar.setDropdownParams(paramSpinnerAdapter, new NavItemSelectedListener(paramOnNavigationListener));
  }
  
  public void setLogo(int paramInt)
  {
    this.mDecorToolbar.setLogo(paramInt);
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    this.mDecorToolbar.setLogo(paramDrawable);
  }
  
  public void setNavigationMode(int paramInt)
  {
    if (paramInt != 2)
    {
      this.mDecorToolbar.setNavigationMode(paramInt);
      return;
    }
    throw new IllegalArgumentException("Tabs not supported in this configuration");
  }
  
  public void setSelectedNavigationItem(int paramInt)
  {
    switch (this.mDecorToolbar.getNavigationMode())
    {
    default: 
      throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
    }
    this.mDecorToolbar.setDropdownSelectedPosition(paramInt);
  }
  
  public void setShowHideAnimationEnabled(boolean paramBoolean) {}
  
  public void setSplitBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setStackedBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setSubtitle(int paramInt)
  {
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    CharSequence localCharSequence;
    if (paramInt == 0) {
      localCharSequence = null;
    } else {
      localCharSequence = this.mDecorToolbar.getContext().getText(paramInt);
    }
    localDecorToolbar.setSubtitle(localCharSequence);
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setSubtitle(paramCharSequence);
  }
  
  public void setTitle(int paramInt)
  {
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    CharSequence localCharSequence;
    if (paramInt == 0) {
      localCharSequence = null;
    } else {
      localCharSequence = this.mDecorToolbar.getContext().getText(paramInt);
    }
    localDecorToolbar.setTitle(localCharSequence);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setTitle(paramCharSequence);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public void show()
  {
    this.mToolbar.setVisibility(0);
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    return this.mWindowCallback.startActionMode(paramCallback);
  }
  
  private final class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    private MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      return false;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (ToolbarActionBar.this.mWindowCallback != null) {
        if (!ToolbarActionBar.this.mToolbar.isOverflowMenuShowing())
        {
          if (ToolbarActionBar.this.mWindowCallback.onPreparePanel(0, null, paramMenuBuilder)) {
            ToolbarActionBar.this.mWindowCallback.onMenuOpened(8, paramMenuBuilder);
          }
        }
        else {
          ToolbarActionBar.this.mWindowCallback.onPanelClosed(8, paramMenuBuilder);
        }
      }
    }
  }
  
  private final class PanelMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private PanelMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if (ToolbarActionBar.this.mWindowCallback != null) {
        ToolbarActionBar.this.mWindowCallback.onPanelClosed(0, paramMenuBuilder);
      }
      ToolbarActionBar.this.mWindow.closePanel(0);
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if ((paramMenuBuilder == null) && (ToolbarActionBar.this.mWindowCallback != null)) {
        ToolbarActionBar.this.mWindowCallback.onMenuOpened(0, paramMenuBuilder);
      }
      return true;
    }
  }
  
  private final class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private boolean mClosingActionMenu;
    
    private ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if (!this.mClosingActionMenu)
      {
        this.mClosingActionMenu = true;
        ToolbarActionBar.this.mToolbar.dismissPopupMenus();
        if (ToolbarActionBar.this.mWindowCallback != null) {
          ToolbarActionBar.this.mWindowCallback.onPanelClosed(8, paramMenuBuilder);
        }
        this.mClosingActionMenu = false;
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      boolean bool;
      if (ToolbarActionBar.this.mWindowCallback == null)
      {
        bool = false;
      }
      else
      {
        ToolbarActionBar.this.mWindowCallback.onMenuOpened(8, paramMenuBuilder);
        bool = true;
      }
      return bool;
    }
  }
  
  private class ToolbarCallbackWrapper
    extends WindowCallbackWrapper
  {
    public ToolbarCallbackWrapper(WindowCallback paramWindowCallback)
    {
      super();
    }
    
    public View onCreatePanelView(int paramInt)
    {
      switch (paramInt)
      {
      case 0: 
        if (!ToolbarActionBar.this.mToolbarMenuPrepared)
        {
          ToolbarActionBar.this.populateOptionsMenu();
          ToolbarActionBar.this.mToolbar.removeCallbacks(ToolbarActionBar.this.mMenuInvalidator);
        }
        if ((ToolbarActionBar.this.mToolbarMenuPrepared) && (ToolbarActionBar.this.mWindowCallback != null))
        {
          localObject = ToolbarActionBar.this.getMenu();
          if ((ToolbarActionBar.this.mWindowCallback.onPreparePanel(paramInt, null, (Menu)localObject)) && (ToolbarActionBar.this.mWindowCallback.onMenuOpened(paramInt, (Menu)localObject))) {
            break label127;
          }
        }
        break;
      }
      Object localObject = super.onCreatePanelView(paramInt);
      return (View)localObject;
      label127:
      localObject = ToolbarActionBar.this.getListMenuView((Menu)localObject);
      return (View)localObject;
    }
    
    public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
    {
      boolean bool = super.onPreparePanel(paramInt, paramView, paramMenu);
      if ((bool) && (!ToolbarActionBar.this.mToolbarMenuPrepared))
      {
        ToolbarActionBar.this.mDecorToolbar.setMenuPrepared();
        ToolbarActionBar.access$102(ToolbarActionBar.this, true);
      }
      return bool;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\app\ToolbarActionBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */