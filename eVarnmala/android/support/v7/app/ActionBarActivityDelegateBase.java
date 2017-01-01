package android.support.v7.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.color;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.style;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.app.ToolbarActionBar;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.app.WindowDecorActionBar;
import android.support.v7.internal.view.StandaloneActionMode;
import android.support.v7.internal.view.menu.ListMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.widget.ActionBarContextView;
import android.support.v7.internal.widget.DecorContentParent;
import android.support.v7.internal.widget.FitWindowsViewGroup;
import android.support.v7.internal.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.internal.widget.TintCheckedTextView;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.internal.widget.TintRadioButton;
import android.support.v7.internal.widget.TintSpinner;
import android.support.v7.internal.widget.ViewStubCompat;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

class ActionBarActivityDelegateBase
  extends ActionBarActivityDelegate
  implements MenuBuilder.Callback
{
  private static final String TAG = "ActionBarActivityDelegateBase";
  private ActionMenuPresenterCallback mActionMenuPresenterCallback;
  ActionMode mActionMode;
  PopupWindow mActionModePopup;
  ActionBarContextView mActionModeView;
  private boolean mClosingActionMenu;
  private DecorContentParent mDecorContentParent;
  private boolean mEnableDefaultActionBarUp;
  private boolean mFeatureIndeterminateProgress;
  private boolean mFeatureProgress;
  private int mInvalidatePanelMenuFeatures;
  private boolean mInvalidatePanelMenuPosted;
  private final Runnable mInvalidatePanelMenuRunnable = new Runnable()
  {
    public void run()
    {
      if ((0x1 & ActionBarActivityDelegateBase.this.mInvalidatePanelMenuFeatures) != 0) {
        ActionBarActivityDelegateBase.this.doInvalidatePanelMenu(0);
      }
      if ((0x100 & ActionBarActivityDelegateBase.this.mInvalidatePanelMenuFeatures) != 0) {
        ActionBarActivityDelegateBase.this.doInvalidatePanelMenu(8);
      }
      ActionBarActivityDelegateBase.access$202(ActionBarActivityDelegateBase.this, false);
      ActionBarActivityDelegateBase.access$002(ActionBarActivityDelegateBase.this, 0);
    }
  };
  private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
  private PanelFeatureState[] mPanels;
  private PanelFeatureState mPreparedPanel;
  Runnable mShowActionModePopup;
  private View mStatusGuard;
  private ViewGroup mSubDecor;
  private boolean mSubDecorInstalled;
  private Rect mTempRect1;
  private Rect mTempRect2;
  private CharSequence mTitleToSet;
  private ListMenuPresenter mToolbarListMenuPresenter;
  private ViewGroup mWindowDecor;
  
  ActionBarActivityDelegateBase(ActionBarActivity paramActionBarActivity)
  {
    super(paramActionBarActivity);
  }
  
  private void applyFixedSizeWindow()
  {
    TypedArray localTypedArray = this.mActivity.obtainStyledAttributes(R.styleable.Theme);
    Object localObject = null;
    TypedValue localTypedValue4 = null;
    TypedValue localTypedValue2 = null;
    TypedValue localTypedValue3 = null;
    if (localTypedArray.hasValue(R.styleable.Theme_windowFixedWidthMajor))
    {
      if (0 == 0) {
        localObject = new TypedValue();
      }
      localTypedArray.getValue(R.styleable.Theme_windowFixedWidthMajor, (TypedValue)localObject);
    }
    if (localTypedArray.hasValue(R.styleable.Theme_windowFixedWidthMinor))
    {
      if (0 == 0) {
        localTypedValue4 = new TypedValue();
      }
      localTypedArray.getValue(R.styleable.Theme_windowFixedWidthMinor, localTypedValue4);
    }
    if (localTypedArray.hasValue(R.styleable.Theme_windowFixedHeightMajor))
    {
      if (0 == 0) {
        localTypedValue2 = new TypedValue();
      }
      localTypedArray.getValue(R.styleable.Theme_windowFixedHeightMajor, localTypedValue2);
    }
    if (localTypedArray.hasValue(R.styleable.Theme_windowFixedHeightMinor))
    {
      if (0 == 0) {
        localTypedValue3 = new TypedValue();
      }
      localTypedArray.getValue(R.styleable.Theme_windowFixedHeightMinor, localTypedValue3);
    }
    DisplayMetrics localDisplayMetrics = this.mActivity.getResources().getDisplayMetrics();
    int k;
    if (localDisplayMetrics.widthPixels >= localDisplayMetrics.heightPixels) {
      k = 0;
    } else {
      k = 1;
    }
    int j = -1;
    int i = -1;
    if (k == 0) {
      localObject = localObject;
    } else {
      localObject = localTypedValue4;
    }
    if ((localObject != null) && (((TypedValue)localObject).type != 0)) {
      if (((TypedValue)localObject).type != 5)
      {
        if (((TypedValue)localObject).type == 6) {
          j = (int)((TypedValue)localObject).getFraction(localDisplayMetrics.widthPixels, localDisplayMetrics.widthPixels);
        }
      }
      else {
        j = (int)((TypedValue)localObject).getDimension(localDisplayMetrics);
      }
    }
    TypedValue localTypedValue1;
    if (k == 0) {
      localTypedValue1 = localTypedValue3;
    } else {
      localTypedValue1 = localTypedValue2;
    }
    if ((localTypedValue1 != null) && (localTypedValue1.type != 0)) {
      if (localTypedValue1.type != 5)
      {
        if (localTypedValue1.type == 6) {
          i = (int)localTypedValue1.getFraction(localDisplayMetrics.heightPixels, localDisplayMetrics.heightPixels);
        }
      }
      else {
        i = (int)localTypedValue1.getDimension(localDisplayMetrics);
      }
    }
    if ((j != -1) || (i != -1)) {
      this.mActivity.getWindow().setLayout(j, i);
    }
    localTypedArray.recycle();
  }
  
  private void callOnPanelClosed(int paramInt, PanelFeatureState paramPanelFeatureState, Menu paramMenu)
  {
    if (paramMenu == null)
    {
      if ((paramPanelFeatureState == null) && (paramInt >= 0) && (paramInt < this.mPanels.length)) {
        paramPanelFeatureState = this.mPanels[paramInt];
      }
      if (paramPanelFeatureState != null) {
        paramMenu = paramPanelFeatureState.menu;
      }
    }
    if ((paramPanelFeatureState == null) || (paramPanelFeatureState.isOpen)) {
      getWindowCallback().onPanelClosed(paramInt, paramMenu);
    }
  }
  
  private void checkCloseActionMenu(MenuBuilder paramMenuBuilder)
  {
    if (!this.mClosingActionMenu)
    {
      this.mClosingActionMenu = true;
      this.mDecorContentParent.dismissPopups();
      WindowCallback localWindowCallback = getWindowCallback();
      if ((localWindowCallback != null) && (!isDestroyed())) {
        localWindowCallback.onPanelClosed(8, paramMenuBuilder);
      }
      this.mClosingActionMenu = false;
    }
  }
  
  private void closePanel(PanelFeatureState paramPanelFeatureState, boolean paramBoolean)
  {
    if ((!paramBoolean) || (paramPanelFeatureState.featureId != 0) || (this.mDecorContentParent == null) || (!this.mDecorContentParent.isOverflowMenuShowing()))
    {
      if ((paramPanelFeatureState.isOpen) && (paramBoolean)) {
        callOnPanelClosed(paramPanelFeatureState.featureId, paramPanelFeatureState, null);
      }
      paramPanelFeatureState.isPrepared = false;
      paramPanelFeatureState.isHandled = false;
      paramPanelFeatureState.isOpen = false;
      paramPanelFeatureState.shownPanelView = null;
      paramPanelFeatureState.refreshDecorView = true;
      if (this.mPreparedPanel == paramPanelFeatureState) {
        this.mPreparedPanel = null;
      }
    }
    else
    {
      checkCloseActionMenu(paramPanelFeatureState.menu);
    }
  }
  
  private void doInvalidatePanelMenu(int paramInt)
  {
    PanelFeatureState localPanelFeatureState = getPanelState(paramInt, true);
    if (localPanelFeatureState.menu != null)
    {
      Bundle localBundle = new Bundle();
      localPanelFeatureState.menu.saveActionViewStates(localBundle);
      if (localBundle.size() > 0) {
        localPanelFeatureState.frozenActionViewState = localBundle;
      }
      localPanelFeatureState.menu.stopDispatchingItemsChanged();
      localPanelFeatureState.menu.clear();
    }
    localPanelFeatureState.refreshMenuContent = true;
    localPanelFeatureState.refreshDecorView = true;
    if (((paramInt == 8) || (paramInt == 0)) && (this.mDecorContentParent != null))
    {
      localPanelFeatureState = getPanelState(0, false);
      if (localPanelFeatureState != null)
      {
        localPanelFeatureState.isPrepared = false;
        preparePanel(localPanelFeatureState, null);
      }
    }
  }
  
  private void ensureToolbarListMenuPresenter()
  {
    if (this.mToolbarListMenuPresenter == null)
    {
      TypedValue localTypedValue = new TypedValue();
      this.mActivity.getTheme().resolveAttribute(R.attr.panelMenuListTheme, localTypedValue, true);
      ActionBarActivity localActionBarActivity = this.mActivity;
      int i;
      if (localTypedValue.resourceId == 0) {
        i = R.style.Theme_AppCompat_CompactMenu;
      } else {
        i = i.resourceId;
      }
      this.mToolbarListMenuPresenter = new ListMenuPresenter(new ContextThemeWrapper(localActionBarActivity, i), R.layout.abc_list_menu_item_layout);
    }
  }
  
  private PanelFeatureState findMenuPanel(Menu paramMenu)
  {
    PanelFeatureState[] arrayOfPanelFeatureState = this.mPanels;
    int j;
    if (arrayOfPanelFeatureState == null) {
      j = 0;
    } else {
      j = arrayOfPanelFeatureState.length;
    }
    PanelFeatureState localPanelFeatureState;
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        localPanelFeatureState = null;
        break;
      }
      localPanelFeatureState = arrayOfPanelFeatureState[i];
      if ((localPanelFeatureState != null) && (localPanelFeatureState.menu == paramMenu)) {
        break;
      }
    }
    return localPanelFeatureState;
  }
  
  private PanelFeatureState getPanelState(int paramInt, boolean paramBoolean)
  {
    Object localObject1 = this.mPanels;
    if ((localObject1 == null) || (localObject1.length <= paramInt))
    {
      localObject2 = new PanelFeatureState[paramInt + 1];
      if (localObject1 != null) {
        System.arraycopy(localObject1, 0, localObject2, 0, localObject1.length);
      }
      localObject1 = localObject2;
      this.mPanels = ((PanelFeatureState[])localObject2);
    }
    Object localObject2 = localObject1[paramInt];
    if (localObject2 == null)
    {
      localObject2 = new PanelFeatureState(paramInt);
      localObject1[paramInt] = localObject2;
    }
    return (PanelFeatureState)localObject2;
  }
  
  private boolean initializePanelContent(PanelFeatureState paramPanelFeatureState)
  {
    boolean bool = false;
    if (paramPanelFeatureState.menu != null)
    {
      if (this.mPanelMenuPresenterCallback == null) {
        this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback(null);
      }
      paramPanelFeatureState.shownPanelView = ((View)paramPanelFeatureState.getListMenuView(this.mPanelMenuPresenterCallback));
      if (paramPanelFeatureState.shownPanelView != null) {
        bool = true;
      }
    }
    return bool;
  }
  
  private void initializePanelDecor(PanelFeatureState paramPanelFeatureState)
  {
    paramPanelFeatureState.decorView = this.mWindowDecor;
    paramPanelFeatureState.setStyle(getActionBarThemedContext());
  }
  
  private boolean initializePanelMenu(PanelFeatureState paramPanelFeatureState)
  {
    Object localObject3 = this.mActivity;
    if (((paramPanelFeatureState.featureId == 0) || (paramPanelFeatureState.featureId == 8)) && (this.mDecorContentParent != null))
    {
      TypedValue localTypedValue = new TypedValue();
      Object localObject2 = ((Context)localObject3).getTheme();
      ((Resources.Theme)localObject2).resolveAttribute(R.attr.actionBarTheme, localTypedValue, true);
      localObject1 = null;
      if (localTypedValue.resourceId == 0)
      {
        ((Resources.Theme)localObject2).resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
      }
      else
      {
        localObject1 = ((Context)localObject3).getResources().newTheme();
        ((Resources.Theme)localObject1).setTo((Resources.Theme)localObject2);
        ((Resources.Theme)localObject1).applyStyle(localTypedValue.resourceId, true);
        ((Resources.Theme)localObject1).resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
      }
      if (localTypedValue.resourceId != 0)
      {
        if (localObject1 == null)
        {
          localObject1 = ((Context)localObject3).getResources().newTheme();
          ((Resources.Theme)localObject1).setTo((Resources.Theme)localObject2);
        }
        ((Resources.Theme)localObject1).applyStyle(localTypedValue.resourceId, true);
      }
      if (localObject1 != null)
      {
        localObject2 = new ContextThemeWrapper((Context)localObject3, 0);
        ((Context)localObject2).getTheme().setTo((Resources.Theme)localObject1);
        localObject3 = localObject2;
      }
    }
    Object localObject1 = new MenuBuilder((Context)localObject3);
    ((MenuBuilder)localObject1).setCallback(this);
    paramPanelFeatureState.setMenu((MenuBuilder)localObject1);
    return true;
  }
  
  private void invalidatePanelMenu(int paramInt)
  {
    this.mInvalidatePanelMenuFeatures |= 1 << paramInt;
    if ((!this.mInvalidatePanelMenuPosted) && (this.mWindowDecor != null))
    {
      ViewCompat.postOnAnimation(this.mWindowDecor, this.mInvalidatePanelMenuRunnable);
      this.mInvalidatePanelMenuPosted = true;
    }
  }
  
  private void openPanel(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt != 0) || (this.mDecorContentParent == null) || (!this.mDecorContentParent.canShowOverflowMenu()) || (ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mActivity)))) {
      openPanel(getPanelState(paramInt, true), paramKeyEvent);
    } else {
      this.mDecorContentParent.showOverflowMenu();
    }
  }
  
  private void openPanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent)
  {
    if ((!paramPanelFeatureState.isOpen) && (!isDestroyed())) {
      if (paramPanelFeatureState.featureId == 0)
      {
        ActionBarActivity localActionBarActivity = this.mActivity;
        int i;
        if ((0xF & localActionBarActivity.getResources().getConfiguration().screenLayout) != 4) {
          i = 0;
        } else {
          i = 1;
        }
        int j;
        if (localActionBarActivity.getApplicationInfo().targetSdkVersion < 11) {
          j = 0;
        } else {
          j = 1;
        }
        if ((i != 0) && (j != 0)) {}
      }
      else
      {
        WindowCallback localWindowCallback = getWindowCallback();
        if ((localWindowCallback == null) || (localWindowCallback.onMenuOpened(paramPanelFeatureState.featureId, paramPanelFeatureState.menu)))
        {
          if (preparePanel(paramPanelFeatureState, paramKeyEvent))
          {
            if ((paramPanelFeatureState.decorView == null) || (paramPanelFeatureState.refreshDecorView)) {
              initializePanelDecor(paramPanelFeatureState);
            }
            if ((initializePanelContent(paramPanelFeatureState)) && (paramPanelFeatureState.hasPanelItems()))
            {
              paramPanelFeatureState.isHandled = false;
              paramPanelFeatureState.isOpen = true;
            }
          }
        }
        else {
          closePanel(paramPanelFeatureState, true);
        }
      }
    }
  }
  
  private boolean preparePanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent)
  {
    int i = 0;
    if (!isDestroyed()) {
      if (!paramPanelFeatureState.isPrepared)
      {
        if ((this.mPreparedPanel != null) && (this.mPreparedPanel != paramPanelFeatureState)) {
          closePanel(this.mPreparedPanel, false);
        }
        int j;
        if ((paramPanelFeatureState.featureId != 0) && (paramPanelFeatureState.featureId != 8)) {
          j = 0;
        } else {
          j = 1;
        }
        if ((j != 0) && (this.mDecorContentParent != null)) {
          this.mDecorContentParent.setMenuPrepared();
        }
        if ((paramPanelFeatureState.menu == null) || (paramPanelFeatureState.refreshMenuContent))
        {
          if ((paramPanelFeatureState.menu == null) && ((!initializePanelMenu(paramPanelFeatureState)) || (paramPanelFeatureState.menu == null))) {
            return i;
          }
          if ((j != 0) && (this.mDecorContentParent != null))
          {
            if (this.mActionMenuPresenterCallback == null) {
              this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback(null);
            }
            this.mDecorContentParent.setMenu(paramPanelFeatureState.menu, this.mActionMenuPresenterCallback);
          }
          paramPanelFeatureState.menu.stopDispatchingItemsChanged();
          if (getWindowCallback().onCreatePanelMenu(paramPanelFeatureState.featureId, paramPanelFeatureState.menu)) {
            paramPanelFeatureState.refreshMenuContent = false;
          }
        }
        else
        {
          paramPanelFeatureState.menu.stopDispatchingItemsChanged();
          if (paramPanelFeatureState.frozenActionViewState != null)
          {
            paramPanelFeatureState.menu.restoreActionViewStates(paramPanelFeatureState.frozenActionViewState);
            paramPanelFeatureState.frozenActionViewState = null;
          }
          if (getWindowCallback().onPreparePanel(0, null, paramPanelFeatureState.menu))
          {
            if (paramKeyEvent == null) {
              i = -1;
            } else {
              i = paramKeyEvent.getDeviceId();
            }
            if (KeyCharacterMap.load(i).getKeyboardType() == 1) {
              i = 0;
            } else {
              i = 1;
            }
            paramPanelFeatureState.qwertyMode = i;
            paramPanelFeatureState.menu.setQwertyMode(paramPanelFeatureState.qwertyMode);
            paramPanelFeatureState.menu.startDispatchingItemsChanged();
            paramPanelFeatureState.isPrepared = true;
            paramPanelFeatureState.isHandled = false;
            this.mPreparedPanel = paramPanelFeatureState;
            return 1;
          }
          if ((j != 0) && (this.mDecorContentParent != null)) {
            this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
          }
          paramPanelFeatureState.menu.startDispatchingItemsChanged();
          return i;
        }
        paramPanelFeatureState.setMenu(null);
        if ((j != 0) && (this.mDecorContentParent != null)) {
          this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
        }
      }
      else
      {
        i = 1;
      }
    }
    return i;
  }
  
  private void reopenMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    Object localObject;
    if ((this.mDecorContentParent == null) || (!this.mDecorContentParent.canShowOverflowMenu()) || ((ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mActivity))) && (!this.mDecorContentParent.isOverflowMenuShowPending())))
    {
      localObject = getPanelState(0, true);
      ((PanelFeatureState)localObject).refreshDecorView = true;
      closePanel((PanelFeatureState)localObject, false);
      openPanel((PanelFeatureState)localObject, null);
    }
    else
    {
      localObject = getWindowCallback();
      if ((this.mDecorContentParent.isOverflowMenuShowing()) && (paramBoolean))
      {
        this.mDecorContentParent.hideOverflowMenu();
        if (!isDestroyed())
        {
          localObject = getPanelState(0, true);
          this.mActivity.onPanelClosed(8, ((PanelFeatureState)localObject).menu);
        }
      }
      else if ((localObject != null) && (!isDestroyed()))
      {
        if ((this.mInvalidatePanelMenuPosted) && ((0x1 & this.mInvalidatePanelMenuFeatures) != 0))
        {
          this.mWindowDecor.removeCallbacks(this.mInvalidatePanelMenuRunnable);
          this.mInvalidatePanelMenuRunnable.run();
        }
        PanelFeatureState localPanelFeatureState = getPanelState(0, true);
        if ((localPanelFeatureState.menu != null) && (!localPanelFeatureState.refreshMenuContent) && (((WindowCallback)localObject).onPreparePanel(0, null, localPanelFeatureState.menu)))
        {
          ((WindowCallback)localObject).onMenuOpened(8, localPanelFeatureState.menu);
          this.mDecorContentParent.showOverflowMenu();
        }
      }
    }
  }
  
  private void throwFeatureRequestIfSubDecorInstalled()
  {
    if (!this.mSubDecorInstalled) {
      return;
    }
    throw new AndroidRuntimeException("supportRequestWindowFeature() must be called before adding content");
  }
  
  private int updateStatusGuard(int paramInt)
  {
    int i = 0;
    int k = 0;
    int n;
    if ((this.mActionModeView != null) && ((this.mActionModeView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)))
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mActionModeView.getLayoutParams();
      int j = 0;
      if (!this.mActionModeView.isShown())
      {
        if (localMarginLayoutParams.topMargin != 0)
        {
          j = 1;
          localMarginLayoutParams.topMargin = 0;
        }
      }
      else
      {
        if (this.mTempRect1 == null)
        {
          this.mTempRect1 = new Rect();
          this.mTempRect2 = new Rect();
        }
        Rect localRect1 = this.mTempRect1;
        Rect localRect2 = this.mTempRect2;
        localRect1.set(0, paramInt, 0, 0);
        ViewUtils.computeFitSystemWindows(this.mSubDecor, localRect1, localRect2);
        int m;
        if (localRect2.top != 0) {
          m = 0;
        } else {
          m = paramInt;
        }
        if (localMarginLayoutParams.topMargin != m)
        {
          j = 1;
          localMarginLayoutParams.topMargin = paramInt;
          if (this.mStatusGuard != null)
          {
            ViewGroup.LayoutParams localLayoutParams = this.mStatusGuard.getLayoutParams();
            if (localLayoutParams.height != paramInt)
            {
              localLayoutParams.height = paramInt;
              this.mStatusGuard.setLayoutParams(localLayoutParams);
            }
          }
          else
          {
            this.mStatusGuard = new View(this.mActivity);
            this.mStatusGuard.setBackgroundColor(this.mActivity.getResources().getColor(R.color.abc_input_method_navigation_guard));
            this.mSubDecor.addView(this.mStatusGuard, -1, new ViewGroup.LayoutParams(-1, paramInt));
          }
        }
        if (this.mStatusGuard == null) {
          n = 0;
        } else {
          n = 1;
        }
        if ((!this.mOverlayActionMode) && (n != 0)) {
          paramInt = 0;
        }
      }
      if (j != 0) {
        this.mActionModeView.setLayoutParams(localMarginLayoutParams);
      }
    }
    if (this.mStatusGuard != null)
    {
      View localView = this.mStatusGuard;
      if (n == 0) {
        i = 8;
      }
      localView.setVisibility(i);
    }
    return paramInt;
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    ensureSubDecor();
    ((ViewGroup)this.mActivity.findViewById(16908290)).addView(paramView, paramLayoutParams);
    this.mActivity.onSupportContentChanged();
  }
  
  public ActionBar createSupportActionBar()
  {
    ensureSubDecor();
    WindowDecorActionBar localWindowDecorActionBar = new WindowDecorActionBar(this.mActivity, this.mOverlayActionBar);
    localWindowDecorActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
    return localWindowDecorActionBar;
  }
  
  View createView(String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet)
  {
    int i;
    if (Build.VERSION.SDK_INT < 21)
    {
      i = -1;
      switch (paramString.hashCode())
      {
      case -1455429095: 
        if (paramString.equals("CheckedTextView")) {
          i = 4;
        }
        break;
      case -339785223: 
        if (paramString.equals("Spinner")) {
          i = 1;
        }
        break;
      case 776382189: 
        if (paramString.equals("RadioButton")) {
          i = 3;
        }
        break;
      case 1601505219: 
        if (paramString.equals("CheckBox")) {
          i = 2;
        }
        break;
      case 1666676343: 
        if (paramString.equals("EditText")) {
          i = 0;
        }
        break;
      }
    }
    Object localObject;
    switch (i)
    {
    default: 
      localObject = null;
      break;
    case 0: 
      localObject = new TintEditText(paramContext, paramAttributeSet);
      break;
    case 1: 
      localObject = new TintSpinner(paramContext, paramAttributeSet);
      break;
    case 2: 
      localObject = new TintCheckBox(paramContext, paramAttributeSet);
      break;
    case 3: 
      localObject = new TintRadioButton(paramContext, paramAttributeSet);
      break;
    case 4: 
      localObject = new TintCheckedTextView(paramContext, paramAttributeSet);
    }
    return (View)localObject;
  }
  
  final void ensureSubDecor()
  {
    if (!this.mSubDecorInstalled)
    {
      if (!this.mHasActionBar)
      {
        if (!this.mOverlayActionMode) {
          this.mSubDecor = ((ViewGroup)LayoutInflater.from(this.mActivity).inflate(R.layout.abc_screen_simple, null));
        } else {
          this.mSubDecor = ((ViewGroup)LayoutInflater.from(this.mActivity).inflate(R.layout.abc_screen_simple_overlay_action_mode, null));
        }
        if (Build.VERSION.SDK_INT < 21) {
          ((FitWindowsViewGroup)this.mSubDecor).setOnFitSystemWindowsListener(new FitWindowsViewGroup.OnFitSystemWindowsListener()
          {
            public void onFitSystemWindows(Rect paramAnonymousRect)
            {
              paramAnonymousRect.top = ActionBarActivityDelegateBase.this.updateStatusGuard(paramAnonymousRect.top);
            }
          });
        } else {
          ViewCompat.setOnApplyWindowInsetsListener(this.mSubDecor, new OnApplyWindowInsetsListener()
          {
            public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
            {
              int j = paramAnonymousWindowInsetsCompat.getSystemWindowInsetTop();
              int i = ActionBarActivityDelegateBase.this.updateStatusGuard(j);
              if (j != i) {
                paramAnonymousWindowInsetsCompat = paramAnonymousWindowInsetsCompat.replaceSystemWindowInsets(paramAnonymousWindowInsetsCompat.getSystemWindowInsetLeft(), i, paramAnonymousWindowInsetsCompat.getSystemWindowInsetRight(), paramAnonymousWindowInsetsCompat.getSystemWindowInsetBottom());
              }
              return paramAnonymousWindowInsetsCompat;
            }
          });
        }
      }
      else
      {
        localObject = new TypedValue();
        this.mActivity.getTheme().resolveAttribute(R.attr.actionBarTheme, (TypedValue)localObject, true);
        if (((TypedValue)localObject).resourceId == 0) {
          localObject = this.mActivity;
        } else {
          localObject = new ContextThemeWrapper(this.mActivity, ((TypedValue)localObject).resourceId);
        }
        this.mSubDecor = ((ViewGroup)LayoutInflater.from((Context)localObject).inflate(R.layout.abc_screen_toolbar, null));
        this.mDecorContentParent = ((DecorContentParent)this.mSubDecor.findViewById(R.id.decor_content_parent));
        this.mDecorContentParent.setWindowCallback(getWindowCallback());
        if (this.mOverlayActionBar) {
          this.mDecorContentParent.initFeature(9);
        }
        if (this.mFeatureProgress) {
          this.mDecorContentParent.initFeature(2);
        }
        if (this.mFeatureIndeterminateProgress) {
          this.mDecorContentParent.initFeature(5);
        }
      }
      ViewUtils.makeOptionalFitsSystemWindows(this.mSubDecor);
      this.mActivity.superSetContentView(this.mSubDecor);
      Object localObject = this.mActivity.findViewById(16908290);
      ((View)localObject).setId(-1);
      this.mActivity.findViewById(R.id.action_bar_activity_content).setId(16908290);
      if ((localObject instanceof FrameLayout)) {
        ((FrameLayout)localObject).setForeground(null);
      }
      if ((this.mTitleToSet != null) && (this.mDecorContentParent != null))
      {
        this.mDecorContentParent.setWindowTitle(this.mTitleToSet);
        this.mTitleToSet = null;
      }
      applyFixedSizeWindow();
      onSubDecorInstalled();
      this.mSubDecorInstalled = true;
      localObject = getPanelState(0, false);
      if ((!isDestroyed()) && ((localObject == null) || (((PanelFeatureState)localObject).menu == null))) {
        invalidatePanelMenu(8);
      }
    }
  }
  
  int getHomeAsUpIndicatorAttrId()
  {
    return R.attr.homeAsUpIndicator;
  }
  
  public boolean onBackPressed()
  {
    boolean bool = true;
    if (this.mActionMode == null)
    {
      ActionBar localActionBar = getSupportActionBar();
      if ((localActionBar == null) || (!localActionBar.collapseActionView())) {
        bool = false;
      }
    }
    else
    {
      this.mActionMode.finish();
    }
    return bool;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if ((this.mHasActionBar) && (this.mSubDecorInstalled))
    {
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.onConfigurationChanged(paramConfiguration);
      }
    }
  }
  
  public void onContentChanged() {}
  
  void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWindowDecor = ((ViewGroup)this.mActivity.getWindow().getDecorView());
    if (NavUtils.getParentActivityName(this.mActivity) != null)
    {
      ActionBar localActionBar = peekSupportActionBar();
      if (localActionBar != null) {
        localActionBar.setDefaultDisplayHomeAsUpEnabled(true);
      } else {
        this.mEnableDefaultActionBarUp = true;
      }
    }
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    boolean bool;
    if (paramInt == 0) {
      bool = false;
    } else {
      bool = getWindowCallback().onCreatePanelMenu(paramInt, paramMenu);
    }
    return bool;
  }
  
  public View onCreatePanelView(int paramInt)
  {
    View localView = null;
    if (this.mActionMode == null)
    {
      Object localObject = getWindowCallback();
      if (localObject != null) {
        localView = ((WindowCallback)localObject).onCreatePanelView(paramInt);
      }
      if ((localView == null) && (this.mToolbarListMenuPresenter == null))
      {
        localObject = getPanelState(paramInt, true);
        openPanel((PanelFeatureState)localObject, null);
        if (((PanelFeatureState)localObject).isOpen) {
          localView = ((PanelFeatureState)localObject).shownPanelView;
        }
      }
    }
    return localView;
  }
  
  boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return onKeyShortcut(paramInt, paramKeyEvent);
  }
  
  boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    int i = 1;
    boolean bool2;
    if ((this.mPreparedPanel == null) || (!performPanelShortcut(this.mPreparedPanel, paramKeyEvent.getKeyCode(), paramKeyEvent, i)))
    {
      if (this.mPreparedPanel == null)
      {
        PanelFeatureState localPanelFeatureState = getPanelState(0, i);
        preparePanel(localPanelFeatureState, paramKeyEvent);
        boolean bool1 = performPanelShortcut(localPanelFeatureState, paramKeyEvent.getKeyCode(), paramKeyEvent, i);
        localPanelFeatureState.isPrepared = false;
        if (bool1) {}
      }
      else
      {
        bool2 = false;
      }
    }
    else if (this.mPreparedPanel != null) {
      this.mPreparedPanel.isHandled = bool2;
    }
    return bool2;
  }
  
  public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    WindowCallback localWindowCallback = getWindowCallback();
    if ((localWindowCallback != null) && (!isDestroyed()))
    {
      PanelFeatureState localPanelFeatureState = findMenuPanel(paramMenuBuilder.getRootMenu());
      if (localPanelFeatureState != null) {}
    }
    else
    {
      return false;
    }
    boolean bool = localWindowCallback.onMenuItemSelected(bool.featureId, paramMenuItem);
    return bool;
  }
  
  public void onMenuModeChange(MenuBuilder paramMenuBuilder)
  {
    reopenMenu(paramMenuBuilder, true);
  }
  
  boolean onMenuOpened(int paramInt, Menu paramMenu)
  {
    boolean bool = true;
    if (paramInt != 8)
    {
      bool = this.mActivity.superOnMenuOpened(paramInt, paramMenu);
    }
    else
    {
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.dispatchMenuVisibilityChanged(bool);
      }
    }
    return bool;
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    Object localObject = getPanelState(paramInt, false);
    if (localObject != null) {
      closePanel((PanelFeatureState)localObject, false);
    }
    if (paramInt != 8)
    {
      if (!isDestroyed()) {
        this.mActivity.superOnPanelClosed(paramInt, paramMenu);
      }
    }
    else
    {
      localObject = getSupportActionBar();
      if (localObject != null) {
        ((ActionBar)localObject).dispatchMenuVisibilityChanged(false);
      }
    }
  }
  
  public void onPostResume()
  {
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null) {
      localActionBar.setShowHideAnimationEnabled(true);
    }
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    boolean bool;
    if (paramInt == 0) {
      bool = false;
    } else {
      bool = getWindowCallback().onPreparePanel(paramInt, paramView, paramMenu);
    }
    return bool;
  }
  
  public void onStop()
  {
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null) {
      localActionBar.setShowHideAnimationEnabled(false);
    }
  }
  
  void onSubDecorInstalled() {}
  
  public void onTitleChanged(CharSequence paramCharSequence)
  {
    if (this.mDecorContentParent == null)
    {
      if (getSupportActionBar() == null) {
        this.mTitleToSet = paramCharSequence;
      } else {
        getSupportActionBar().setWindowTitle(paramCharSequence);
      }
    }
    else {
      this.mDecorContentParent.setWindowTitle(paramCharSequence);
    }
  }
  
  final boolean performPanelShortcut(PanelFeatureState paramPanelFeatureState, int paramInt1, KeyEvent paramKeyEvent, int paramInt2)
  {
    boolean bool;
    if (!paramKeyEvent.isSystem())
    {
      bool = false;
      if (((paramPanelFeatureState.isPrepared) || (preparePanel(paramPanelFeatureState, paramKeyEvent))) && (paramPanelFeatureState.menu != null)) {
        bool = paramPanelFeatureState.menu.performShortcut(paramInt1, paramKeyEvent, paramInt2);
      }
      if ((bool) && ((paramInt2 & 0x1) == 0) && (this.mDecorContentParent == null)) {
        closePanel(paramPanelFeatureState, true);
      }
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  public void setContentView(int paramInt)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)this.mActivity.findViewById(16908290);
    localViewGroup.removeAllViews();
    this.mActivity.getLayoutInflater().inflate(paramInt, localViewGroup);
    this.mActivity.onSupportContentChanged();
  }
  
  public void setContentView(View paramView)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)this.mActivity.findViewById(16908290);
    localViewGroup.removeAllViews();
    localViewGroup.addView(paramView);
    this.mActivity.onSupportContentChanged();
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)this.mActivity.findViewById(16908290);
    localViewGroup.removeAllViews();
    localViewGroup.addView(paramView, paramLayoutParams);
    this.mActivity.onSupportContentChanged();
  }
  
  void setSupportActionBar(Toolbar paramToolbar)
  {
    Object localObject = getSupportActionBar();
    if (!(localObject instanceof WindowDecorActionBar))
    {
      if ((localObject instanceof ToolbarActionBar)) {
        ((ToolbarActionBar)localObject).setListMenuPresenter(null);
      }
      localObject = new ToolbarActionBar(paramToolbar, this.mActivity.getTitle(), this.mActivity.getWindow(), this.mDefaultWindowCallback);
      ensureToolbarListMenuPresenter();
      ((ToolbarActionBar)localObject).setListMenuPresenter(this.mToolbarListMenuPresenter);
      setSupportActionBar((ActionBar)localObject);
      setWindowCallback(((ToolbarActionBar)localObject).getWrappedWindowCallback());
      ((ToolbarActionBar)localObject).invalidateOptionsMenu();
      return;
    }
    throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
  }
  
  void setSupportProgress(int paramInt) {}
  
  void setSupportProgressBarIndeterminate(boolean paramBoolean) {}
  
  void setSupportProgressBarIndeterminateVisibility(boolean paramBoolean) {}
  
  void setSupportProgressBarVisibility(boolean paramBoolean) {}
  
  public ActionMode startSupportActionMode(ActionMode.Callback paramCallback)
  {
    if (paramCallback != null)
    {
      if (this.mActionMode != null) {
        this.mActionMode.finish();
      }
      ActionModeCallbackWrapper localActionModeCallbackWrapper = new ActionModeCallbackWrapper(paramCallback);
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null)
      {
        this.mActionMode = localActionBar.startActionMode(localActionModeCallbackWrapper);
        if (this.mActionMode != null) {
          this.mActivity.onSupportActionModeStarted(this.mActionMode);
        }
      }
      if (this.mActionMode == null) {
        this.mActionMode = startSupportActionModeFromWindow(localActionModeCallbackWrapper);
      }
      return this.mActionMode;
    }
    throw new IllegalArgumentException("ActionMode callback can not be null.");
  }
  
  ActionMode startSupportActionModeFromWindow(ActionMode.Callback paramCallback)
  {
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
    Object localObject1 = new ActionModeCallbackWrapper(paramCallback);
    Context localContext = getActionBarThemedContext();
    if (this.mActionModeView == null)
    {
      Object localObject2;
      if (!this.mIsFloating)
      {
        localObject2 = (ViewStubCompat)this.mActivity.findViewById(R.id.action_mode_bar_stub);
        if (localObject2 != null)
        {
          ((ViewStubCompat)localObject2).setLayoutInflater(LayoutInflater.from(localContext));
          this.mActionModeView = ((ActionBarContextView)((ViewStubCompat)localObject2).inflate());
        }
      }
      else
      {
        this.mActionModeView = new ActionBarContextView(localContext);
        this.mActionModePopup = new PopupWindow(localContext, null, R.attr.actionModePopupWindowStyle);
        this.mActionModePopup.setContentView(this.mActionModeView);
        this.mActionModePopup.setWidth(-1);
        localObject2 = new TypedValue();
        this.mActivity.getTheme().resolveAttribute(R.attr.actionBarSize, (TypedValue)localObject2, true);
        int i = TypedValue.complexToDimensionPixelSize(((TypedValue)localObject2).data, this.mActivity.getResources().getDisplayMetrics());
        this.mActionModeView.setContentHeight(i);
        this.mActionModePopup.setHeight(-2);
        this.mShowActionModePopup = new Runnable()
        {
          public void run()
          {
            ActionBarActivityDelegateBase.this.mActionModePopup.showAtLocation(ActionBarActivityDelegateBase.this.mActionModeView, 55, 0, 0);
          }
        };
      }
    }
    if (this.mActionModeView != null)
    {
      this.mActionModeView.killMode();
      ActionBarContextView localActionBarContextView = this.mActionModeView;
      boolean bool;
      if (this.mActionModePopup != null) {
        bool = false;
      } else {
        bool = true;
      }
      localObject1 = new StandaloneActionMode(localContext, localActionBarContextView, (ActionMode.Callback)localObject1, bool);
      if (!paramCallback.onCreateActionMode((ActionMode)localObject1, ((ActionMode)localObject1).getMenu()))
      {
        this.mActionMode = null;
      }
      else
      {
        ((ActionMode)localObject1).invalidate();
        this.mActionModeView.initForMode((ActionMode)localObject1);
        this.mActionModeView.setVisibility(0);
        this.mActionMode = ((ActionMode)localObject1);
        if (this.mActionModePopup != null) {
          this.mActivity.getWindow().getDecorView().post(this.mShowActionModePopup);
        }
        this.mActionModeView.sendAccessibilityEvent(32);
        if (this.mActionModeView.getParent() != null) {
          ViewCompat.requestApplyInsets((View)this.mActionModeView.getParent());
        }
      }
    }
    if ((this.mActionMode != null) && (this.mActivity != null)) {
      this.mActivity.onSupportActionModeStarted(this.mActionMode);
    }
    return this.mActionMode;
  }
  
  public void supportInvalidateOptionsMenu()
  {
    ActionBar localActionBar = getSupportActionBar();
    if ((localActionBar == null) || (!localActionBar.invalidateOptionsMenu())) {
      invalidatePanelMenu(0);
    }
  }
  
  public boolean supportRequestWindowFeature(int paramInt)
  {
    boolean bool = true;
    switch (paramInt)
    {
    case 3: 
    case 4: 
    case 6: 
    case 7: 
    default: 
      bool = this.mActivity.requestWindowFeature(paramInt);
      break;
    case 2: 
      throwFeatureRequestIfSubDecorInstalled();
      this.mFeatureProgress = bool;
      break;
    case 5: 
      throwFeatureRequestIfSubDecorInstalled();
      this.mFeatureIndeterminateProgress = bool;
      break;
    case 8: 
      throwFeatureRequestIfSubDecorInstalled();
      this.mHasActionBar = bool;
      break;
    case 9: 
      throwFeatureRequestIfSubDecorInstalled();
      this.mOverlayActionBar = bool;
      break;
    case 10: 
      throwFeatureRequestIfSubDecorInstalled();
      this.mOverlayActionMode = bool;
    }
    return bool;
  }
  
  private static final class PanelFeatureState
  {
    ViewGroup decorView;
    int featureId;
    Bundle frozenActionViewState;
    Bundle frozenMenuState;
    boolean isHandled;
    boolean isOpen;
    boolean isPrepared;
    ListMenuPresenter listMenuPresenter;
    Context listPresenterContext;
    MenuBuilder menu;
    public boolean qwertyMode;
    boolean refreshDecorView;
    boolean refreshMenuContent;
    View shownPanelView;
    boolean wasLastOpen;
    
    PanelFeatureState(int paramInt)
    {
      this.featureId = paramInt;
      this.refreshDecorView = false;
    }
    
    void applyFrozenState()
    {
      if ((this.menu != null) && (this.frozenMenuState != null))
      {
        this.menu.restorePresenterStates(this.frozenMenuState);
        this.frozenMenuState = null;
      }
    }
    
    public void clearMenuPresenters()
    {
      if (this.menu != null) {
        this.menu.removeMenuPresenter(this.listMenuPresenter);
      }
      this.listMenuPresenter = null;
    }
    
    MenuView getListMenuView(MenuPresenter.Callback paramCallback)
    {
      MenuView localMenuView;
      if (this.menu != null)
      {
        if (this.listMenuPresenter == null)
        {
          this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, R.layout.abc_list_menu_item_layout);
          this.listMenuPresenter.setCallback(paramCallback);
          this.menu.addMenuPresenter(this.listMenuPresenter);
        }
        localMenuView = this.listMenuPresenter.getMenuView(this.decorView);
      }
      else
      {
        localMenuView = null;
      }
      return localMenuView;
    }
    
    public boolean hasPanelItems()
    {
      boolean bool = false;
      if ((this.shownPanelView != null) && (this.listMenuPresenter.getAdapter().getCount() > 0)) {
        bool = true;
      }
      return bool;
    }
    
    void onRestoreInstanceState(Parcelable paramParcelable)
    {
      SavedState localSavedState = (SavedState)paramParcelable;
      this.featureId = localSavedState.featureId;
      this.wasLastOpen = localSavedState.isOpen;
      this.frozenMenuState = localSavedState.menuState;
      this.shownPanelView = null;
      this.decorView = null;
    }
    
    Parcelable onSaveInstanceState()
    {
      SavedState localSavedState = new SavedState(null);
      localSavedState.featureId = this.featureId;
      localSavedState.isOpen = this.isOpen;
      if (this.menu != null)
      {
        localSavedState.menuState = new Bundle();
        this.menu.savePresenterStates(localSavedState.menuState);
      }
      return localSavedState;
    }
    
    void setMenu(MenuBuilder paramMenuBuilder)
    {
      if (paramMenuBuilder != this.menu)
      {
        if (this.menu != null) {
          this.menu.removeMenuPresenter(this.listMenuPresenter);
        }
        this.menu = paramMenuBuilder;
        if ((paramMenuBuilder != null) && (this.listMenuPresenter != null)) {
          paramMenuBuilder.addMenuPresenter(this.listMenuPresenter);
        }
      }
    }
    
    void setStyle(Context paramContext)
    {
      Object localObject = new TypedValue();
      Resources.Theme localTheme = paramContext.getResources().newTheme();
      localTheme.setTo(paramContext.getTheme());
      localTheme.resolveAttribute(R.attr.actionBarPopupTheme, (TypedValue)localObject, true);
      if (((TypedValue)localObject).resourceId != 0) {
        localTheme.applyStyle(((TypedValue)localObject).resourceId, true);
      }
      localTheme.resolveAttribute(R.attr.panelMenuListTheme, (TypedValue)localObject, true);
      if (((TypedValue)localObject).resourceId == 0) {
        localTheme.applyStyle(R.style.Theme_AppCompat_CompactMenu, true);
      } else {
        localTheme.applyStyle(((TypedValue)localObject).resourceId, true);
      }
      localObject = new ContextThemeWrapper(paramContext, 0);
      ((Context)localObject).getTheme().setTo(localTheme);
      this.listPresenterContext = ((Context)localObject);
    }
    
    private static class SavedState
      implements Parcelable
    {
      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
      {
        public ActionBarActivityDelegateBase.PanelFeatureState.SavedState createFromParcel(Parcel paramAnonymousParcel)
        {
          return ActionBarActivityDelegateBase.PanelFeatureState.SavedState.readFromParcel(paramAnonymousParcel);
        }
        
        public ActionBarActivityDelegateBase.PanelFeatureState.SavedState[] newArray(int paramAnonymousInt)
        {
          return new ActionBarActivityDelegateBase.PanelFeatureState.SavedState[paramAnonymousInt];
        }
      };
      int featureId;
      boolean isOpen;
      Bundle menuState;
      
      private static SavedState readFromParcel(Parcel paramParcel)
      {
        int i = 1;
        SavedState localSavedState = new SavedState();
        localSavedState.featureId = paramParcel.readInt();
        if (paramParcel.readInt() != i) {
          i = 0;
        }
        localSavedState.isOpen = i;
        if (localSavedState.isOpen) {
          localSavedState.menuState = paramParcel.readBundle();
        }
        return localSavedState;
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeInt(this.featureId);
        int i;
        if (!this.isOpen) {
          i = 0;
        } else {
          i = 1;
        }
        paramParcel.writeInt(i);
        if (this.isOpen) {
          paramParcel.writeBundle(this.menuState);
        }
      }
    }
  }
  
  private final class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      ActionBarActivityDelegateBase.this.checkCloseActionMenu(paramMenuBuilder);
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      WindowCallback localWindowCallback = ActionBarActivityDelegateBase.this.getWindowCallback();
      if (localWindowCallback != null) {
        localWindowCallback.onMenuOpened(8, paramMenuBuilder);
      }
      return true;
    }
  }
  
  private final class PanelMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private PanelMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      MenuBuilder localMenuBuilder = paramMenuBuilder.getRootMenu();
      int i;
      if (localMenuBuilder == paramMenuBuilder) {
        i = 0;
      } else {
        i = 1;
      }
      Object localObject = ActionBarActivityDelegateBase.this;
      if (i != 0) {
        paramMenuBuilder = localMenuBuilder;
      }
      localObject = ((ActionBarActivityDelegateBase)localObject).findMenuPanel(paramMenuBuilder);
      if (localObject != null) {
        if (i == 0)
        {
          ActionBarActivityDelegateBase.this.mActivity.closeOptionsMenu();
          ActionBarActivityDelegateBase.this.closePanel((ActionBarActivityDelegateBase.PanelFeatureState)localObject, paramBoolean);
        }
        else
        {
          ActionBarActivityDelegateBase.this.callOnPanelClosed(((ActionBarActivityDelegateBase.PanelFeatureState)localObject).featureId, (ActionBarActivityDelegateBase.PanelFeatureState)localObject, localMenuBuilder);
          ActionBarActivityDelegateBase.this.closePanel((ActionBarActivityDelegateBase.PanelFeatureState)localObject, true);
        }
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if ((paramMenuBuilder == null) && (ActionBarActivityDelegateBase.this.mHasActionBar))
      {
        WindowCallback localWindowCallback = ActionBarActivityDelegateBase.this.getWindowCallback();
        if ((localWindowCallback != null) && (!ActionBarActivityDelegateBase.this.isDestroyed())) {
          localWindowCallback.onMenuOpened(8, paramMenuBuilder);
        }
      }
      return true;
    }
  }
  
  private class ActionModeCallbackWrapper
    implements ActionMode.Callback
  {
    private ActionMode.Callback mWrapped;
    
    public ActionModeCallbackWrapper(ActionMode.Callback paramCallback)
    {
      this.mWrapped = paramCallback;
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return this.mWrapped.onActionItemClicked(paramActionMode, paramMenuItem);
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return this.mWrapped.onCreateActionMode(paramActionMode, paramMenu);
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      this.mWrapped.onDestroyActionMode(paramActionMode);
      if (ActionBarActivityDelegateBase.this.mActionModePopup != null)
      {
        ActionBarActivityDelegateBase.this.mActivity.getWindow().getDecorView().removeCallbacks(ActionBarActivityDelegateBase.this.mShowActionModePopup);
        ActionBarActivityDelegateBase.this.mActionModePopup.dismiss();
      }
      for (;;)
      {
        if (ActionBarActivityDelegateBase.this.mActionModeView != null) {
          ActionBarActivityDelegateBase.this.mActionModeView.removeAllViews();
        }
        if (ActionBarActivityDelegateBase.this.mActivity != null) {}
        try
        {
          ActionBarActivityDelegateBase.this.mActivity.onSupportActionModeFinished(ActionBarActivityDelegateBase.this.mActionMode);
          ActionBarActivityDelegateBase.this.mActionMode = null;
          return;
          if (ActionBarActivityDelegateBase.this.mActionModeView == null) {
            continue;
          }
          ActionBarActivityDelegateBase.this.mActionModeView.setVisibility(8);
          if (ActionBarActivityDelegateBase.this.mActionModeView.getParent() == null) {
            continue;
          }
          ViewCompat.requestApplyInsets((View)ActionBarActivityDelegateBase.this.mActionModeView.getParent());
        }
        catch (AbstractMethodError localAbstractMethodError)
        {
          for (;;) {}
        }
      }
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return this.mWrapped.onPrepareActionMode(paramActionMode, paramMenu);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\app\ActionBarActivityDelegateBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */