package android.support.v7.internal.app;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBar.OnMenuVisibilityListener;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.view.ActionBarPolicy;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.internal.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.internal.widget.ActionBarContainer;
import android.support.v7.internal.widget.ActionBarContextView;
import android.support.v7.internal.widget.ActionBarOverlayLayout;
import android.support.v7.internal.widget.ActionBarOverlayLayout.ActionBarVisibilityCallback;
import android.support.v7.internal.widget.DecorToolbar;
import android.support.v7.internal.widget.ScrollingTabContainerView;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.SpinnerAdapter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WindowDecorActionBar
  extends ActionBar
  implements ActionBarOverlayLayout.ActionBarVisibilityCallback
{
  private static final boolean ALLOW_SHOW_HIDE_ANIMATIONS = false;
  private static final int CONTEXT_DISPLAY_NORMAL = 0;
  private static final int CONTEXT_DISPLAY_SPLIT = 1;
  private static final int INVALID_POSITION = -1;
  private static final String TAG = "WindowDecorActionBar";
  ActionModeImpl mActionMode;
  private FragmentActivity mActivity;
  private ActionBarContainer mContainerView;
  private boolean mContentAnimations = true;
  private View mContentView;
  private Context mContext;
  private int mContextDisplayMode;
  private ActionBarContextView mContextView;
  private int mCurWindowVisibility = 0;
  private ViewPropertyAnimatorCompatSet mCurrentShowAnim;
  private DecorToolbar mDecorToolbar;
  ActionMode mDeferredDestroyActionMode;
  ActionMode.Callback mDeferredModeDestroyCallback;
  private Dialog mDialog;
  private boolean mDisplayHomeAsUpSet;
  private boolean mHasEmbeddedTabs;
  private boolean mHiddenByApp;
  private boolean mHiddenBySystem;
  final ViewPropertyAnimatorListener mHideListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationEnd(View paramAnonymousView)
    {
      if ((WindowDecorActionBar.this.mContentAnimations) && (WindowDecorActionBar.this.mContentView != null))
      {
        ViewCompat.setTranslationY(WindowDecorActionBar.this.mContentView, 0.0F);
        ViewCompat.setTranslationY(WindowDecorActionBar.this.mContainerView, 0.0F);
      }
      if ((WindowDecorActionBar.this.mSplitView != null) && (WindowDecorActionBar.this.mContextDisplayMode == 1)) {
        WindowDecorActionBar.this.mSplitView.setVisibility(8);
      }
      WindowDecorActionBar.this.mContainerView.setVisibility(8);
      WindowDecorActionBar.this.mContainerView.setTransitioning(false);
      WindowDecorActionBar.access$502(WindowDecorActionBar.this, null);
      WindowDecorActionBar.this.completeDeferredDestroyActionMode();
      if (WindowDecorActionBar.this.mOverlayLayout != null) {
        ViewCompat.requestApplyInsets(WindowDecorActionBar.this.mOverlayLayout);
      }
    }
  };
  boolean mHideOnContentScroll;
  private boolean mLastMenuVisibility;
  private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList();
  private boolean mNowShowing = true;
  private ActionBarOverlayLayout mOverlayLayout;
  private int mSavedTabPosition = -1;
  private TabImpl mSelectedTab;
  private boolean mShowHideAnimationEnabled;
  final ViewPropertyAnimatorListener mShowListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationEnd(View paramAnonymousView)
    {
      WindowDecorActionBar.access$502(WindowDecorActionBar.this, null);
      WindowDecorActionBar.this.mContainerView.requestLayout();
    }
  };
  private boolean mShowingForMode;
  private ActionBarContainer mSplitView;
  private ScrollingTabContainerView mTabScrollView;
  private ArrayList<TabImpl> mTabs = new ArrayList();
  private Context mThemedContext;
  private TintManager mTintManager;
  final ViewPropertyAnimatorUpdateListener mUpdateListener = new ViewPropertyAnimatorUpdateListener()
  {
    public void onAnimationUpdate(View paramAnonymousView)
    {
      ((View)WindowDecorActionBar.this.mContainerView.getParent()).invalidate();
    }
  };
  
  static
  {
    boolean bool2 = true;
    boolean bool1;
    if (WindowDecorActionBar.class.desiredAssertionStatus()) {
      bool1 = false;
    } else {
      bool1 = bool2;
    }
    $assertionsDisabled = bool1;
    if (Build.VERSION.SDK_INT < 14) {
      bool2 = false;
    }
    ALLOW_SHOW_HIDE_ANIMATIONS = bool2;
  }
  
  public WindowDecorActionBar(Dialog paramDialog)
  {
    this.mDialog = paramDialog;
    init(paramDialog.getWindow().getDecorView());
  }
  
  public WindowDecorActionBar(ActionBarActivity paramActionBarActivity, boolean paramBoolean)
  {
    this.mActivity = paramActionBarActivity;
    View localView = paramActionBarActivity.getWindow().getDecorView();
    init(localView);
    if (!paramBoolean) {
      this.mContentView = localView.findViewById(16908290);
    }
  }
  
  public WindowDecorActionBar(View paramView)
  {
    if (($assertionsDisabled) || (paramView.isInEditMode()))
    {
      init(paramView);
      return;
    }
    throw new AssertionError();
  }
  
  private static boolean checkShowingFlags(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = true;
    if ((!paramBoolean3) && ((paramBoolean1) || (paramBoolean2))) {
      bool = false;
    }
    return bool;
  }
  
  private void cleanupTabs()
  {
    if (this.mSelectedTab != null) {
      selectTab(null);
    }
    this.mTabs.clear();
    if (this.mTabScrollView != null) {
      this.mTabScrollView.removeAllTabs();
    }
    this.mSavedTabPosition = -1;
  }
  
  private void configureTab(ActionBar.Tab paramTab, int paramInt)
  {
    TabImpl localTabImpl = (TabImpl)paramTab;
    if (localTabImpl.getCallback() != null)
    {
      localTabImpl.setPosition(paramInt);
      this.mTabs.add(paramInt, localTabImpl);
      int j = this.mTabs.size();
      for (int i = paramInt + 1;; i++)
      {
        if (i >= j) {
          return;
        }
        ((TabImpl)this.mTabs.get(i)).setPosition(i);
      }
    }
    throw new IllegalStateException("Action Bar Tab must have a Callback");
  }
  
  private void ensureTabsExist()
  {
    if (this.mTabScrollView == null)
    {
      ScrollingTabContainerView localScrollingTabContainerView = new ScrollingTabContainerView(this.mContext);
      if (!this.mHasEmbeddedTabs)
      {
        if (getNavigationMode() != 2)
        {
          localScrollingTabContainerView.setVisibility(8);
        }
        else
        {
          localScrollingTabContainerView.setVisibility(0);
          if (this.mOverlayLayout != null) {
            ViewCompat.requestApplyInsets(this.mOverlayLayout);
          }
        }
        this.mContainerView.setTabContainer(localScrollingTabContainerView);
      }
      else
      {
        localScrollingTabContainerView.setVisibility(0);
        this.mDecorToolbar.setEmbeddedTabView(localScrollingTabContainerView);
      }
      this.mTabScrollView = localScrollingTabContainerView;
    }
  }
  
  private DecorToolbar getDecorToolbar(View paramView)
  {
    DecorToolbar localDecorToolbar;
    if (!(paramView instanceof DecorToolbar))
    {
      if (!(paramView instanceof Toolbar)) {
        throw new IllegalStateException("Can't make a decor toolbar out of " + paramView.getClass().getSimpleName());
      }
      localDecorToolbar = ((Toolbar)paramView).getWrapper();
    }
    else
    {
      localDecorToolbar = (DecorToolbar)paramView;
    }
    return localDecorToolbar;
  }
  
  private void hideForActionMode()
  {
    if (this.mShowingForMode)
    {
      this.mShowingForMode = false;
      if (this.mOverlayLayout != null) {
        this.mOverlayLayout.setShowingForActionMode(false);
      }
      updateVisibility(false);
    }
  }
  
  private void init(View paramView)
  {
    this.mOverlayLayout = ((ActionBarOverlayLayout)paramView.findViewById(R.id.decor_content_parent));
    if (this.mOverlayLayout != null) {
      this.mOverlayLayout.setActionBarVisibilityCallback(this);
    }
    this.mDecorToolbar = getDecorToolbar(paramView.findViewById(R.id.action_bar));
    this.mContextView = ((ActionBarContextView)paramView.findViewById(R.id.action_context_bar));
    this.mContainerView = ((ActionBarContainer)paramView.findViewById(R.id.action_bar_container));
    this.mSplitView = ((ActionBarContainer)paramView.findViewById(R.id.split_action_bar));
    if ((this.mDecorToolbar != null) && (this.mContextView != null) && (this.mContainerView != null))
    {
      this.mContext = this.mDecorToolbar.getContext();
      int i;
      if (!this.mDecorToolbar.isSplit()) {
        i = 0;
      } else {
        i = 1;
      }
      this.mContextDisplayMode = i;
      boolean bool;
      if ((0x4 & this.mDecorToolbar.getDisplayOptions()) == 0) {
        bool = false;
      } else {
        bool = true;
      }
      if (bool) {
        this.mDisplayHomeAsUpSet = true;
      }
      Object localObject = ActionBarPolicy.get(this.mContext);
      if ((!((ActionBarPolicy)localObject).enableHomeButtonByDefault()) && (!bool)) {
        bool = false;
      } else {
        bool = true;
      }
      setHomeButtonEnabled(bool);
      setHasEmbeddedTabs(((ActionBarPolicy)localObject).hasEmbeddedTabs());
      localObject = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
      if (((TypedArray)localObject).getBoolean(R.styleable.ActionBar_hideOnContentScroll, false)) {
        setHideOnContentScrollEnabled(true);
      }
      int j = ((TypedArray)localObject).getDimensionPixelSize(R.styleable.ActionBar_elevation, 0);
      if (j != 0) {
        setElevation(j);
      }
      ((TypedArray)localObject).recycle();
      return;
    }
    throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with a compatible window decor layout");
  }
  
  private void setHasEmbeddedTabs(boolean paramBoolean)
  {
    boolean bool1 = true;
    this.mHasEmbeddedTabs = paramBoolean;
    if (this.mHasEmbeddedTabs)
    {
      this.mContainerView.setTabContainer(null);
      this.mDecorToolbar.setEmbeddedTabView(this.mTabScrollView);
    }
    else
    {
      this.mDecorToolbar.setEmbeddedTabView(null);
      this.mContainerView.setTabContainer(this.mTabScrollView);
    }
    int i;
    if (getNavigationMode() != 2) {
      i = 0;
    } else {
      i = bool1;
    }
    if (this.mTabScrollView != null) {
      if (i == 0)
      {
        this.mTabScrollView.setVisibility(8);
      }
      else
      {
        this.mTabScrollView.setVisibility(0);
        if (this.mOverlayLayout != null) {
          ViewCompat.requestApplyInsets(this.mOverlayLayout);
        }
      }
    }
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    boolean bool2;
    if ((this.mHasEmbeddedTabs) || (i == 0)) {
      bool2 = false;
    } else {
      bool2 = bool1;
    }
    localDecorToolbar.setCollapsible(bool2);
    ActionBarOverlayLayout localActionBarOverlayLayout = this.mOverlayLayout;
    if ((this.mHasEmbeddedTabs) || (i == 0)) {
      bool1 = false;
    }
    localActionBarOverlayLayout.setHasNonEmbeddedTabs(bool1);
  }
  
  private void showForActionMode()
  {
    if (!this.mShowingForMode)
    {
      this.mShowingForMode = true;
      if (this.mOverlayLayout != null) {
        this.mOverlayLayout.setShowingForActionMode(true);
      }
      updateVisibility(false);
    }
  }
  
  private void updateVisibility(boolean paramBoolean)
  {
    if (!checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode))
    {
      if (this.mNowShowing)
      {
        this.mNowShowing = false;
        doHide(paramBoolean);
      }
    }
    else if (!this.mNowShowing)
    {
      this.mNowShowing = true;
      doShow(paramBoolean);
    }
  }
  
  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.add(paramOnMenuVisibilityListener);
  }
  
  public void addTab(ActionBar.Tab paramTab)
  {
    addTab(paramTab, this.mTabs.isEmpty());
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt)
  {
    addTab(paramTab, paramInt, this.mTabs.isEmpty());
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    ensureTabsExist();
    this.mTabScrollView.addTab(paramTab, paramInt, paramBoolean);
    configureTab(paramTab, paramInt);
    if (paramBoolean) {
      selectTab(paramTab);
    }
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    ensureTabsExist();
    this.mTabScrollView.addTab(paramTab, paramBoolean);
    configureTab(paramTab, this.mTabs.size());
    if (paramBoolean) {
      selectTab(paramTab);
    }
  }
  
  public void animateToMode(boolean paramBoolean)
  {
    int i = 0;
    if (!paramBoolean) {
      hideForActionMode();
    } else {
      showForActionMode();
    }
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    int j;
    if (!paramBoolean) {
      j = 0;
    } else {
      j = 8;
    }
    localDecorToolbar.animateToVisibility(j);
    ActionBarContextView localActionBarContextView = this.mContextView;
    if (!paramBoolean) {
      i = 8;
    }
    localActionBarContextView.animateToVisibility(i);
  }
  
  public boolean collapseActionView()
  {
    boolean bool;
    if ((this.mDecorToolbar == null) || (!this.mDecorToolbar.hasExpandedActionView()))
    {
      bool = false;
    }
    else
    {
      this.mDecorToolbar.collapseActionView();
      bool = true;
    }
    return bool;
  }
  
  void completeDeferredDestroyActionMode()
  {
    if (this.mDeferredModeDestroyCallback != null)
    {
      this.mDeferredModeDestroyCallback.onDestroyActionMode(this.mDeferredDestroyActionMode);
      this.mDeferredDestroyActionMode = null;
      this.mDeferredModeDestroyCallback = null;
    }
  }
  
  public void dispatchMenuVisibilityChanged(boolean paramBoolean)
  {
    int i;
    if (paramBoolean != this.mLastMenuVisibility)
    {
      this.mLastMenuVisibility = paramBoolean;
      i = this.mMenuVisibilityListeners.size();
    }
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      ((ActionBar.OnMenuVisibilityListener)this.mMenuVisibilityListeners.get(j)).onMenuVisibilityChanged(paramBoolean);
    }
  }
  
  public void doHide(boolean paramBoolean)
  {
    if (this.mCurrentShowAnim != null) {
      this.mCurrentShowAnim.cancel();
    }
    if ((this.mCurWindowVisibility != 0) || (!ALLOW_SHOW_HIDE_ANIMATIONS) || ((!this.mShowHideAnimationEnabled) && (!paramBoolean)))
    {
      this.mHideListener.onAnimationEnd(null);
    }
    else
    {
      ViewCompat.setAlpha(this.mContainerView, 1.0F);
      this.mContainerView.setTransitioning(true);
      ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
      float f = -this.mContainerView.getHeight();
      if (paramBoolean)
      {
        localObject = new int[2];
        localObject[0] = 0;
        localObject[1] = 0;
        this.mContainerView.getLocationInWindow((int[])localObject);
        f -= localObject[1];
      }
      Object localObject = ViewCompat.animate(this.mContainerView).translationY(f);
      ((ViewPropertyAnimatorCompat)localObject).setUpdateListener(this.mUpdateListener);
      localViewPropertyAnimatorCompatSet.play((ViewPropertyAnimatorCompat)localObject);
      if ((this.mContentAnimations) && (this.mContentView != null)) {
        localViewPropertyAnimatorCompatSet.play(ViewCompat.animate(this.mContentView).translationY(f));
      }
      if ((this.mSplitView != null) && (this.mSplitView.getVisibility() == 0))
      {
        ViewCompat.setAlpha(this.mSplitView, 1.0F);
        localViewPropertyAnimatorCompatSet.play(ViewCompat.animate(this.mSplitView).translationY(this.mSplitView.getHeight()));
      }
      localViewPropertyAnimatorCompatSet.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17432581));
      localViewPropertyAnimatorCompatSet.setDuration(250L);
      localViewPropertyAnimatorCompatSet.setListener(this.mHideListener);
      this.mCurrentShowAnim = localViewPropertyAnimatorCompatSet;
      localViewPropertyAnimatorCompatSet.start();
    }
  }
  
  public void doShow(boolean paramBoolean)
  {
    if (this.mCurrentShowAnim != null) {
      this.mCurrentShowAnim.cancel();
    }
    this.mContainerView.setVisibility(0);
    if ((this.mCurWindowVisibility != 0) || (!ALLOW_SHOW_HIDE_ANIMATIONS) || ((!this.mShowHideAnimationEnabled) && (!paramBoolean)))
    {
      ViewCompat.setAlpha(this.mContainerView, 1.0F);
      ViewCompat.setTranslationY(this.mContainerView, 0.0F);
      if ((this.mContentAnimations) && (this.mContentView != null)) {
        ViewCompat.setTranslationY(this.mContentView, 0.0F);
      }
      if ((this.mSplitView != null) && (this.mContextDisplayMode == 1))
      {
        ViewCompat.setAlpha(this.mSplitView, 1.0F);
        ViewCompat.setTranslationY(this.mSplitView, 0.0F);
        this.mSplitView.setVisibility(0);
      }
      this.mShowListener.onAnimationEnd(null);
    }
    else
    {
      ViewCompat.setTranslationY(this.mContainerView, 0.0F);
      float f = -this.mContainerView.getHeight();
      if (paramBoolean)
      {
        localObject = new int[2];
        localObject[0] = 0;
        localObject[1] = 0;
        this.mContainerView.getLocationInWindow((int[])localObject);
        f -= localObject[1];
      }
      ViewCompat.setTranslationY(this.mContainerView, f);
      Object localObject = new ViewPropertyAnimatorCompatSet();
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(this.mContainerView).translationY(0.0F);
      localViewPropertyAnimatorCompat.setUpdateListener(this.mUpdateListener);
      ((ViewPropertyAnimatorCompatSet)localObject).play(localViewPropertyAnimatorCompat);
      if ((this.mContentAnimations) && (this.mContentView != null))
      {
        ViewCompat.setTranslationY(this.mContentView, f);
        ((ViewPropertyAnimatorCompatSet)localObject).play(ViewCompat.animate(this.mContentView).translationY(0.0F));
      }
      if ((this.mSplitView != null) && (this.mContextDisplayMode == 1))
      {
        ViewCompat.setTranslationY(this.mSplitView, this.mSplitView.getHeight());
        this.mSplitView.setVisibility(0);
        ((ViewPropertyAnimatorCompatSet)localObject).play(ViewCompat.animate(this.mSplitView).translationY(0.0F));
      }
      ((ViewPropertyAnimatorCompatSet)localObject).setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17432582));
      ((ViewPropertyAnimatorCompatSet)localObject).setDuration(250L);
      ((ViewPropertyAnimatorCompatSet)localObject).setListener(this.mShowListener);
      this.mCurrentShowAnim = ((ViewPropertyAnimatorCompatSet)localObject);
      ((ViewPropertyAnimatorCompatSet)localObject).start();
    }
    if (this.mOverlayLayout != null) {
      ViewCompat.requestApplyInsets(this.mOverlayLayout);
    }
  }
  
  public void enableContentAnimations(boolean paramBoolean)
  {
    this.mContentAnimations = paramBoolean;
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
    return ViewCompat.getElevation(this.mContainerView);
  }
  
  public int getHeight()
  {
    return this.mContainerView.getHeight();
  }
  
  public int getHideOffset()
  {
    return this.mOverlayLayout.getActionBarHideOffset();
  }
  
  public int getNavigationItemCount()
  {
    int i;
    switch (this.mDecorToolbar.getNavigationMode())
    {
    default: 
      i = 0;
      break;
    case 1: 
      i = this.mDecorToolbar.getDropdownItemCount();
      break;
    case 2: 
      i = this.mTabs.size();
    }
    return i;
  }
  
  public int getNavigationMode()
  {
    return this.mDecorToolbar.getNavigationMode();
  }
  
  public int getSelectedNavigationIndex()
  {
    int i = -1;
    switch (this.mDecorToolbar.getNavigationMode())
    {
    case 1: 
      i = this.mDecorToolbar.getDropdownSelectedPosition();
      break;
    case 2: 
      if (this.mSelectedTab != null) {
        i = this.mSelectedTab.getPosition();
      }
      break;
    }
    return i;
  }
  
  public ActionBar.Tab getSelectedTab()
  {
    return this.mSelectedTab;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mDecorToolbar.getSubtitle();
  }
  
  public ActionBar.Tab getTabAt(int paramInt)
  {
    return (ActionBar.Tab)this.mTabs.get(paramInt);
  }
  
  public int getTabCount()
  {
    return this.mTabs.size();
  }
  
  public Context getThemedContext()
  {
    if (this.mThemedContext == null)
    {
      TypedValue localTypedValue = new TypedValue();
      this.mContext.getTheme().resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
      int i = localTypedValue.resourceId;
      if (i == 0) {
        this.mThemedContext = this.mContext;
      } else {
        this.mThemedContext = new ContextThemeWrapper(this.mContext, i);
      }
    }
    return this.mThemedContext;
  }
  
  TintManager getTintManager()
  {
    if (this.mTintManager == null) {
      this.mTintManager = new TintManager(this.mContext);
    }
    return this.mTintManager;
  }
  
  public CharSequence getTitle()
  {
    return this.mDecorToolbar.getTitle();
  }
  
  public boolean hasIcon()
  {
    return this.mDecorToolbar.hasIcon();
  }
  
  public boolean hasLogo()
  {
    return this.mDecorToolbar.hasLogo();
  }
  
  public void hide()
  {
    if (!this.mHiddenByApp)
    {
      this.mHiddenByApp = true;
      updateVisibility(false);
    }
  }
  
  public void hideForSystem()
  {
    if (!this.mHiddenBySystem)
    {
      this.mHiddenBySystem = true;
      updateVisibility(true);
    }
  }
  
  public boolean isHideOnContentScrollEnabled()
  {
    return this.mOverlayLayout.isHideOnContentScrollEnabled();
  }
  
  public boolean isShowing()
  {
    int i = getHeight();
    if ((!this.mNowShowing) || ((i != 0) && (getHideOffset() >= i))) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  public boolean isTitleTruncated()
  {
    boolean bool;
    if ((this.mDecorToolbar == null) || (!this.mDecorToolbar.isTitleTruncated())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public ActionBar.Tab newTab()
  {
    return new TabImpl();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
  }
  
  public void onContentScrollStarted()
  {
    if (this.mCurrentShowAnim != null)
    {
      this.mCurrentShowAnim.cancel();
      this.mCurrentShowAnim = null;
    }
  }
  
  public void onContentScrollStopped() {}
  
  public void onWindowVisibilityChanged(int paramInt)
  {
    this.mCurWindowVisibility = paramInt;
  }
  
  public void removeAllTabs()
  {
    cleanupTabs();
  }
  
  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.remove(paramOnMenuVisibilityListener);
  }
  
  public void removeTab(ActionBar.Tab paramTab)
  {
    removeTabAt(paramTab.getPosition());
  }
  
  public void removeTabAt(int paramInt)
  {
    int i;
    int k;
    if (this.mTabScrollView != null)
    {
      if (this.mSelectedTab == null) {
        i = this.mSavedTabPosition;
      } else {
        i = this.mSelectedTab.getPosition();
      }
      this.mTabScrollView.removeTabAt(paramInt);
      TabImpl localTabImpl2 = (TabImpl)this.mTabs.remove(paramInt);
      if (localTabImpl2 != null) {
        localTabImpl2.setPosition(-1);
      }
      k = this.mTabs.size();
    }
    for (int j = paramInt;; j++)
    {
      if (j >= k)
      {
        if (i == paramInt)
        {
          TabImpl localTabImpl1;
          if (!this.mTabs.isEmpty()) {
            localTabImpl1 = (TabImpl)this.mTabs.get(Math.max(0, paramInt - 1));
          } else {
            localTabImpl1 = null;
          }
          selectTab(localTabImpl1);
        }
        return;
      }
      ((TabImpl)this.mTabs.get(j)).setPosition(j);
    }
  }
  
  public void selectTab(ActionBar.Tab paramTab)
  {
    int i = -1;
    if (getNavigationMode() == 2)
    {
      FragmentTransaction localFragmentTransaction;
      if (!this.mDecorToolbar.getViewGroup().isInEditMode()) {
        localFragmentTransaction = this.mActivity.getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
      } else {
        localFragmentTransaction = null;
      }
      if (this.mSelectedTab != paramTab)
      {
        ScrollingTabContainerView localScrollingTabContainerView = this.mTabScrollView;
        if (paramTab != null) {
          i = paramTab.getPosition();
        }
        localScrollingTabContainerView.setTabSelected(i);
        if (this.mSelectedTab != null) {
          this.mSelectedTab.getCallback().onTabUnselected(this.mSelectedTab, localFragmentTransaction);
        }
        this.mSelectedTab = ((TabImpl)paramTab);
        if (this.mSelectedTab != null) {
          this.mSelectedTab.getCallback().onTabSelected(this.mSelectedTab, localFragmentTransaction);
        }
      }
      else if (this.mSelectedTab != null)
      {
        this.mSelectedTab.getCallback().onTabReselected(this.mSelectedTab, localFragmentTransaction);
        this.mTabScrollView.animateToTab(paramTab.getPosition());
      }
      if ((localFragmentTransaction != null) && (!localFragmentTransaction.isEmpty())) {
        localFragmentTransaction.commit();
      }
    }
    else
    {
      if (paramTab != null) {
        i = paramTab.getPosition();
      }
      this.mSavedTabPosition = i;
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mContainerView.setPrimaryBackground(paramDrawable);
  }
  
  public void setCustomView(int paramInt)
  {
    setCustomView(LayoutInflater.from(getThemedContext()).inflate(paramInt, this.mDecorToolbar.getViewGroup(), false));
  }
  
  public void setCustomView(View paramView)
  {
    this.mDecorToolbar.setCustomView(paramView);
  }
  
  public void setCustomView(View paramView, ActionBar.LayoutParams paramLayoutParams)
  {
    paramView.setLayoutParams(paramLayoutParams);
    this.mDecorToolbar.setCustomView(paramView);
  }
  
  public void setDefaultDisplayHomeAsUpEnabled(boolean paramBoolean)
  {
    if (!this.mDisplayHomeAsUpSet) {
      setDisplayHomeAsUpEnabled(paramBoolean);
    }
  }
  
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
    if ((paramInt & 0x4) != 0) {
      this.mDisplayHomeAsUpSet = true;
    }
    this.mDecorToolbar.setDisplayOptions(paramInt);
  }
  
  public void setDisplayOptions(int paramInt1, int paramInt2)
  {
    int i = this.mDecorToolbar.getDisplayOptions();
    if ((paramInt2 & 0x4) != 0) {
      this.mDisplayHomeAsUpSet = true;
    }
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
    ViewCompat.setElevation(this.mContainerView, paramFloat);
    if (this.mSplitView != null) {
      ViewCompat.setElevation(this.mSplitView, paramFloat);
    }
  }
  
  public void setHideOffset(int paramInt)
  {
    if ((paramInt == 0) || (this.mOverlayLayout.isInOverlayMode()))
    {
      this.mOverlayLayout.setActionBarHideOffset(paramInt);
      return;
    }
    throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to set a non-zero hide offset");
  }
  
  public void setHideOnContentScrollEnabled(boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mOverlayLayout.isInOverlayMode()))
    {
      this.mHideOnContentScroll = paramBoolean;
      this.mOverlayLayout.setHideOnContentScrollEnabled(paramBoolean);
      return;
    }
    throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
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
    this.mDecorToolbar.setNavigationIcon(paramInt);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    this.mDecorToolbar.setNavigationIcon(paramDrawable);
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean)
  {
    this.mDecorToolbar.setHomeButtonEnabled(paramBoolean);
  }
  
  public void setIcon(int paramInt)
  {
    this.mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mDecorToolbar.setIcon(paramDrawable);
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
    int i = 1;
    int j = this.mDecorToolbar.getNavigationMode();
    switch (j)
    {
    case 2: 
      this.mSavedTabPosition = getSelectedNavigationIndex();
      selectTab(null);
      this.mTabScrollView.setVisibility(8);
    }
    if ((j != paramInt) && (!this.mHasEmbeddedTabs) && (this.mOverlayLayout != null)) {
      ViewCompat.requestApplyInsets(this.mOverlayLayout);
    }
    this.mDecorToolbar.setNavigationMode(paramInt);
    switch (paramInt)
    {
    case 2: 
      ensureTabsExist();
      this.mTabScrollView.setVisibility(0);
      if (this.mSavedTabPosition != -1)
      {
        setSelectedNavigationItem(this.mSavedTabPosition);
        this.mSavedTabPosition = -1;
      }
      break;
    }
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    if ((paramInt != 2) || (this.mHasEmbeddedTabs)) {
      j = 0;
    } else {
      j = i;
    }
    localDecorToolbar.setCollapsible(j);
    ActionBarOverlayLayout localActionBarOverlayLayout = this.mOverlayLayout;
    if ((paramInt != 2) || (this.mHasEmbeddedTabs)) {
      i = 0;
    }
    localActionBarOverlayLayout.setHasNonEmbeddedTabs(i);
  }
  
  public void setSelectedNavigationItem(int paramInt)
  {
    switch (this.mDecorToolbar.getNavigationMode())
    {
    default: 
      throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
    case 1: 
      this.mDecorToolbar.setDropdownSelectedPosition(paramInt);
      break;
    case 2: 
      selectTab((ActionBar.Tab)this.mTabs.get(paramInt));
    }
  }
  
  public void setShowHideAnimationEnabled(boolean paramBoolean)
  {
    this.mShowHideAnimationEnabled = paramBoolean;
    if ((!paramBoolean) && (this.mCurrentShowAnim != null)) {
      this.mCurrentShowAnim.cancel();
    }
  }
  
  public void setSplitBackgroundDrawable(Drawable paramDrawable)
  {
    if (this.mSplitView != null) {
      this.mSplitView.setSplitBackground(paramDrawable);
    }
  }
  
  public void setStackedBackgroundDrawable(Drawable paramDrawable)
  {
    this.mContainerView.setStackedBackground(paramDrawable);
  }
  
  public void setSubtitle(int paramInt)
  {
    setSubtitle(this.mContext.getString(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setSubtitle(paramCharSequence);
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(this.mContext.getString(paramInt));
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
    if (this.mHiddenByApp)
    {
      this.mHiddenByApp = false;
      updateVisibility(false);
    }
  }
  
  public void showForSystem()
  {
    if (this.mHiddenBySystem)
    {
      this.mHiddenBySystem = false;
      updateVisibility(true);
    }
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
    this.mOverlayLayout.setHideOnContentScrollEnabled(false);
    this.mContextView.killMode();
    ActionModeImpl localActionModeImpl = new ActionModeImpl(paramCallback);
    if (!localActionModeImpl.dispatchOnCreate())
    {
      localActionModeImpl = null;
    }
    else
    {
      localActionModeImpl.invalidate();
      this.mContextView.initForMode(localActionModeImpl);
      animateToMode(true);
      if ((this.mSplitView != null) && (this.mContextDisplayMode == 1) && (this.mSplitView.getVisibility() != 0))
      {
        this.mSplitView.setVisibility(0);
        if (this.mOverlayLayout != null) {
          ViewCompat.requestApplyInsets(this.mOverlayLayout);
        }
      }
      this.mContextView.sendAccessibilityEvent(32);
      this.mActionMode = localActionModeImpl;
    }
    return localActionModeImpl;
  }
  
  public class TabImpl
    extends ActionBar.Tab
  {
    private ActionBar.TabListener mCallback;
    private CharSequence mContentDesc;
    private View mCustomView;
    private Drawable mIcon;
    private int mPosition = -1;
    private Object mTag;
    private CharSequence mText;
    
    public TabImpl() {}
    
    public ActionBar.TabListener getCallback()
    {
      return this.mCallback;
    }
    
    public CharSequence getContentDescription()
    {
      return this.mContentDesc;
    }
    
    public View getCustomView()
    {
      return this.mCustomView;
    }
    
    public Drawable getIcon()
    {
      return this.mIcon;
    }
    
    public int getPosition()
    {
      return this.mPosition;
    }
    
    public Object getTag()
    {
      return this.mTag;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public void select()
    {
      WindowDecorActionBar.this.selectTab(this);
    }
    
    public ActionBar.Tab setContentDescription(int paramInt)
    {
      return setContentDescription(WindowDecorActionBar.this.mContext.getResources().getText(paramInt));
    }
    
    public ActionBar.Tab setContentDescription(CharSequence paramCharSequence)
    {
      this.mContentDesc = paramCharSequence;
      if (this.mPosition >= 0) {
        WindowDecorActionBar.this.mTabScrollView.updateTab(this.mPosition);
      }
      return this;
    }
    
    public ActionBar.Tab setCustomView(int paramInt)
    {
      return setCustomView(LayoutInflater.from(WindowDecorActionBar.this.getThemedContext()).inflate(paramInt, null));
    }
    
    public ActionBar.Tab setCustomView(View paramView)
    {
      this.mCustomView = paramView;
      if (this.mPosition >= 0) {
        WindowDecorActionBar.this.mTabScrollView.updateTab(this.mPosition);
      }
      return this;
    }
    
    public ActionBar.Tab setIcon(int paramInt)
    {
      return setIcon(WindowDecorActionBar.this.getTintManager().getDrawable(paramInt));
    }
    
    public ActionBar.Tab setIcon(Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
      if (this.mPosition >= 0) {
        WindowDecorActionBar.this.mTabScrollView.updateTab(this.mPosition);
      }
      return this;
    }
    
    public void setPosition(int paramInt)
    {
      this.mPosition = paramInt;
    }
    
    public ActionBar.Tab setTabListener(ActionBar.TabListener paramTabListener)
    {
      this.mCallback = paramTabListener;
      return this;
    }
    
    public ActionBar.Tab setTag(Object paramObject)
    {
      this.mTag = paramObject;
      return this;
    }
    
    public ActionBar.Tab setText(int paramInt)
    {
      return setText(WindowDecorActionBar.this.mContext.getResources().getText(paramInt));
    }
    
    public ActionBar.Tab setText(CharSequence paramCharSequence)
    {
      this.mText = paramCharSequence;
      if (this.mPosition >= 0) {
        WindowDecorActionBar.this.mTabScrollView.updateTab(this.mPosition);
      }
      return this;
    }
  }
  
  public class ActionModeImpl
    extends ActionMode
    implements MenuBuilder.Callback
  {
    private ActionMode.Callback mCallback;
    private WeakReference<View> mCustomView;
    private MenuBuilder mMenu;
    
    public ActionModeImpl(ActionMode.Callback paramCallback)
    {
      this.mCallback = paramCallback;
      this.mMenu = new MenuBuilder(WindowDecorActionBar.this.getThemedContext()).setDefaultShowAsAction(1);
      this.mMenu.setCallback(this);
    }
    
    public boolean dispatchOnCreate()
    {
      this.mMenu.stopDispatchingItemsChanged();
      try
      {
        boolean bool = this.mCallback.onCreateActionMode(this, this.mMenu);
        return bool;
      }
      finally
      {
        this.mMenu.startDispatchingItemsChanged();
      }
    }
    
    public void finish()
    {
      if (WindowDecorActionBar.this.mActionMode == this)
      {
        if (WindowDecorActionBar.checkShowingFlags(WindowDecorActionBar.this.mHiddenByApp, WindowDecorActionBar.this.mHiddenBySystem, false))
        {
          this.mCallback.onDestroyActionMode(this);
        }
        else
        {
          WindowDecorActionBar.this.mDeferredDestroyActionMode = this;
          WindowDecorActionBar.this.mDeferredModeDestroyCallback = this.mCallback;
        }
        this.mCallback = null;
        WindowDecorActionBar.this.animateToMode(false);
        WindowDecorActionBar.this.mContextView.closeMode();
        WindowDecorActionBar.this.mDecorToolbar.getViewGroup().sendAccessibilityEvent(32);
        WindowDecorActionBar.this.mOverlayLayout.setHideOnContentScrollEnabled(WindowDecorActionBar.this.mHideOnContentScroll);
        WindowDecorActionBar.this.mActionMode = null;
      }
    }
    
    public View getCustomView()
    {
      View localView;
      if (this.mCustomView == null) {
        localView = null;
      } else {
        localView = (View)this.mCustomView.get();
      }
      return localView;
    }
    
    public Menu getMenu()
    {
      return this.mMenu;
    }
    
    public MenuInflater getMenuInflater()
    {
      return new SupportMenuInflater(WindowDecorActionBar.this.getThemedContext());
    }
    
    public CharSequence getSubtitle()
    {
      return WindowDecorActionBar.this.mContextView.getSubtitle();
    }
    
    public CharSequence getTitle()
    {
      return WindowDecorActionBar.this.mContextView.getTitle();
    }
    
    public void invalidate()
    {
      this.mMenu.stopDispatchingItemsChanged();
      try
      {
        this.mCallback.onPrepareActionMode(this, this.mMenu);
        return;
      }
      finally
      {
        this.mMenu.startDispatchingItemsChanged();
      }
    }
    
    public boolean isTitleOptional()
    {
      return WindowDecorActionBar.this.mContextView.isTitleOptional();
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onCloseSubMenu(SubMenuBuilder paramSubMenuBuilder) {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      boolean bool;
      if (this.mCallback == null) {
        bool = false;
      } else {
        bool = this.mCallback.onActionItemClicked(this, paramMenuItem);
      }
      return bool;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (this.mCallback != null)
      {
        invalidate();
        WindowDecorActionBar.this.mContextView.showOverflowMenu();
      }
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      boolean bool = true;
      if (this.mCallback != null)
      {
        if (paramSubMenuBuilder.hasVisibleItems()) {
          new MenuPopupHelper(WindowDecorActionBar.this.getThemedContext(), paramSubMenuBuilder).show();
        }
      }
      else {
        bool = false;
      }
      return bool;
    }
    
    public void setCustomView(View paramView)
    {
      WindowDecorActionBar.this.mContextView.setCustomView(paramView);
      this.mCustomView = new WeakReference(paramView);
    }
    
    public void setSubtitle(int paramInt)
    {
      setSubtitle(WindowDecorActionBar.this.mContext.getResources().getString(paramInt));
    }
    
    public void setSubtitle(CharSequence paramCharSequence)
    {
      WindowDecorActionBar.this.mContextView.setSubtitle(paramCharSequence);
    }
    
    public void setTitle(int paramInt)
    {
      setTitle(WindowDecorActionBar.this.mContext.getResources().getString(paramInt));
    }
    
    public void setTitle(CharSequence paramCharSequence)
    {
      WindowDecorActionBar.this.mContextView.setTitle(paramCharSequence);
    }
    
    public void setTitleOptionalHint(boolean paramBoolean)
    {
      super.setTitleOptionalHint(paramBoolean);
      WindowDecorActionBar.this.mContextView.setTitleOptional(paramBoolean);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\app\WindowDecorActionBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */