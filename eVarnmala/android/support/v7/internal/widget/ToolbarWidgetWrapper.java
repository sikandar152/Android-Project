package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.drawable;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.string;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.menu.ActionMenuItem;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SpinnerAdapter;

public class ToolbarWidgetWrapper
  implements DecorToolbar
{
  private static final int AFFECTS_LOGO_MASK = 3;
  private static final String TAG = "ToolbarWidgetWrapper";
  private ActionMenuPresenter mActionMenuPresenter;
  private View mCustomView;
  private int mDefaultNavigationContentDescription = 0;
  private Drawable mDefaultNavigationIcon;
  private int mDisplayOpts;
  private CharSequence mHomeDescription;
  private Drawable mIcon;
  private Drawable mLogo;
  private boolean mMenuPrepared;
  private Drawable mNavIcon;
  private int mNavigationMode = 0;
  private SpinnerCompat mSpinner;
  private CharSequence mSubtitle;
  private View mTabView;
  private final TintManager mTintManager;
  private CharSequence mTitle;
  private boolean mTitleSet;
  private Toolbar mToolbar;
  private WindowCallback mWindowCallback;
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean)
  {
    this(paramToolbar, paramBoolean, R.string.abc_action_bar_up_description, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
  }
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mToolbar = paramToolbar;
    this.mTitle = paramToolbar.getTitle();
    this.mSubtitle = paramToolbar.getSubtitle();
    boolean bool;
    if (this.mTitle == null) {
      bool = false;
    } else {
      bool = true;
    }
    this.mTitleSet = bool;
    if (!paramBoolean)
    {
      this.mDisplayOpts = detectDisplayOptions();
      this.mTintManager = new TintManager(paramToolbar.getContext());
    }
    else
    {
      localObject1 = TintTypedArray.obtainStyledAttributes(paramToolbar.getContext(), null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
      Object localObject2 = ((TintTypedArray)localObject1).getText(R.styleable.ActionBar_title);
      if (!TextUtils.isEmpty((CharSequence)localObject2)) {
        setTitle((CharSequence)localObject2);
      }
      localObject2 = ((TintTypedArray)localObject1).getText(R.styleable.ActionBar_subtitle);
      if (!TextUtils.isEmpty((CharSequence)localObject2)) {
        setSubtitle((CharSequence)localObject2);
      }
      localObject2 = ((TintTypedArray)localObject1).getDrawable(R.styleable.ActionBar_logo);
      if (localObject2 != null) {
        setLogo((Drawable)localObject2);
      }
      localObject2 = ((TintTypedArray)localObject1).getDrawable(R.styleable.ActionBar_icon);
      if (localObject2 != null) {
        setIcon((Drawable)localObject2);
      }
      localObject2 = ((TintTypedArray)localObject1).getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
      if (localObject2 != null) {
        setNavigationIcon((Drawable)localObject2);
      }
      setDisplayOptions(((TintTypedArray)localObject1).getInt(R.styleable.ActionBar_displayOptions, 0));
      int i = ((TintTypedArray)localObject1).getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
      if (i != 0)
      {
        setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(i, this.mToolbar, false));
        setDisplayOptions(0x10 | this.mDisplayOpts);
      }
      i = ((TintTypedArray)localObject1).getLayoutDimension(R.styleable.ActionBar_height, 0);
      if (i > 0)
      {
        ViewGroup.LayoutParams localLayoutParams = this.mToolbar.getLayoutParams();
        localLayoutParams.height = i;
        this.mToolbar.setLayoutParams(localLayoutParams);
      }
      int j = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.ActionBar_contentInsetStart, -1);
      i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.ActionBar_contentInsetEnd, -1);
      if ((j >= 0) || (i >= 0)) {
        this.mToolbar.setContentInsetsRelative(Math.max(j, 0), Math.max(i, 0));
      }
      i = ((TintTypedArray)localObject1).getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
      if (i != 0) {
        this.mToolbar.setTitleTextAppearance(this.mToolbar.getContext(), i);
      }
      i = ((TintTypedArray)localObject1).getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
      if (i != 0) {
        this.mToolbar.setSubtitleTextAppearance(this.mToolbar.getContext(), i);
      }
      i = ((TintTypedArray)localObject1).getResourceId(R.styleable.ActionBar_popupTheme, 0);
      if (i != 0) {
        this.mToolbar.setPopupTheme(i);
      }
      ((TintTypedArray)localObject1).recycle();
      this.mTintManager = ((TintTypedArray)localObject1).getTintManager();
    }
    setDefaultNavigationContentDescription(paramInt1);
    this.mHomeDescription = this.mToolbar.getNavigationContentDescription();
    setDefaultNavigationIcon(this.mTintManager.getDrawable(paramInt2));
    Object localObject1 = this.mToolbar;
    View.OnClickListener local1 = new View.OnClickListener()
    {
      final ActionMenuItem mNavItem = new ActionMenuItem(ToolbarWidgetWrapper.this.mToolbar.getContext(), 0, 16908332, 0, 0, ToolbarWidgetWrapper.this.mTitle);
      
      public void onClick(View paramAnonymousView)
      {
        if ((ToolbarWidgetWrapper.this.mWindowCallback != null) && (ToolbarWidgetWrapper.this.mMenuPrepared)) {
          ToolbarWidgetWrapper.this.mWindowCallback.onMenuItemSelected(0, this.mNavItem);
        }
      }
    };
    ((Toolbar)localObject1).setNavigationOnClickListener(local1);
  }
  
  private int detectDisplayOptions()
  {
    int i = 11;
    if (this.mToolbar.getNavigationIcon() != null) {
      i |= 0x4;
    }
    return i;
  }
  
  private void ensureSpinner()
  {
    if (this.mSpinner == null)
    {
      this.mSpinner = new SpinnerCompat(getContext(), null, R.attr.actionDropDownStyle);
      Toolbar.LayoutParams localLayoutParams = new Toolbar.LayoutParams(-2, -2, 8388627);
      this.mSpinner.setLayoutParams(localLayoutParams);
    }
  }
  
  private void setTitleInt(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    if ((0x8 & this.mDisplayOpts) != 0) {
      this.mToolbar.setTitle(paramCharSequence);
    }
  }
  
  private void updateHomeAccessibility()
  {
    if ((0x4 & this.mDisplayOpts) != 0) {
      if (!TextUtils.isEmpty(this.mHomeDescription)) {
        this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
      } else {
        this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
      }
    }
  }
  
  private void updateNavigationIcon()
  {
    if ((0x4 & this.mDisplayOpts) != 0)
    {
      Toolbar localToolbar = this.mToolbar;
      Drawable localDrawable;
      if (this.mNavIcon == null) {
        localDrawable = this.mDefaultNavigationIcon;
      } else {
        localDrawable = this.mNavIcon;
      }
      localToolbar.setNavigationIcon(localDrawable);
    }
  }
  
  private void updateToolbarLogo()
  {
    Drawable localDrawable = null;
    if ((0x2 & this.mDisplayOpts) != 0) {
      if ((0x1 & this.mDisplayOpts) == 0) {
        localDrawable = this.mIcon;
      } else if (this.mLogo == null) {
        localDrawable = this.mIcon;
      } else {
        localDrawable = this.mLogo;
      }
    }
    this.mToolbar.setLogo(localDrawable);
  }
  
  public void animateToVisibility(int paramInt)
  {
    if (paramInt != 8)
    {
      if (paramInt == 0) {
        ViewCompat.animate(this.mToolbar).alpha(1.0F).setListener(new ViewPropertyAnimatorListenerAdapter()
        {
          public void onAnimationStart(View paramAnonymousView)
          {
            ToolbarWidgetWrapper.this.mToolbar.setVisibility(0);
          }
        });
      }
    }
    else {
      ViewCompat.animate(this.mToolbar).alpha(0.0F).setListener(new ViewPropertyAnimatorListenerAdapter()
      {
        private boolean mCanceled = false;
        
        public void onAnimationCancel(View paramAnonymousView)
        {
          this.mCanceled = true;
        }
        
        public void onAnimationEnd(View paramAnonymousView)
        {
          if (!this.mCanceled) {
            ToolbarWidgetWrapper.this.mToolbar.setVisibility(8);
          }
        }
      });
    }
  }
  
  public boolean canShowOverflowMenu()
  {
    return this.mToolbar.canShowOverflowMenu();
  }
  
  public boolean canSplit()
  {
    return false;
  }
  
  public void collapseActionView()
  {
    this.mToolbar.collapseActionView();
  }
  
  public void dismissPopupMenus()
  {
    this.mToolbar.dismissPopupMenus();
  }
  
  public Context getContext()
  {
    return this.mToolbar.getContext();
  }
  
  public View getCustomView()
  {
    return this.mCustomView;
  }
  
  public int getDisplayOptions()
  {
    return this.mDisplayOpts;
  }
  
  public int getDropdownItemCount()
  {
    int i;
    if (this.mSpinner == null) {
      i = 0;
    } else {
      i = this.mSpinner.getCount();
    }
    return i;
  }
  
  public int getDropdownSelectedPosition()
  {
    int i;
    if (this.mSpinner == null) {
      i = 0;
    } else {
      i = this.mSpinner.getSelectedItemPosition();
    }
    return i;
  }
  
  public int getNavigationMode()
  {
    return this.mNavigationMode;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mToolbar.getSubtitle();
  }
  
  public CharSequence getTitle()
  {
    return this.mToolbar.getTitle();
  }
  
  public ViewGroup getViewGroup()
  {
    return this.mToolbar;
  }
  
  public boolean hasEmbeddedTabs()
  {
    boolean bool;
    if (this.mTabView == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasExpandedActionView()
  {
    return this.mToolbar.hasExpandedActionView();
  }
  
  public boolean hasIcon()
  {
    boolean bool;
    if (this.mIcon == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasLogo()
  {
    boolean bool;
    if (this.mLogo == null) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hideOverflowMenu()
  {
    return this.mToolbar.hideOverflowMenu();
  }
  
  public void initIndeterminateProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public void initProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return this.mToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing()
  {
    return this.mToolbar.isOverflowMenuShowing();
  }
  
  public boolean isSplit()
  {
    return false;
  }
  
  public boolean isTitleTruncated()
  {
    return this.mToolbar.isTitleTruncated();
  }
  
  public void restoreHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    this.mToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    this.mToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    this.mToolbar.setCollapsible(paramBoolean);
  }
  
  public void setCustomView(View paramView)
  {
    if ((this.mCustomView != null) && ((0x10 & this.mDisplayOpts) != 0)) {
      this.mToolbar.removeView(this.mCustomView);
    }
    this.mCustomView = paramView;
    if ((paramView != null) && ((0x10 & this.mDisplayOpts) != 0)) {
      this.mToolbar.addView(this.mCustomView);
    }
  }
  
  public void setDefaultNavigationContentDescription(int paramInt)
  {
    if (paramInt != this.mDefaultNavigationContentDescription)
    {
      this.mDefaultNavigationContentDescription = paramInt;
      if (TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription())) {
        setNavigationContentDescription(this.mDefaultNavigationContentDescription);
      }
    }
  }
  
  public void setDefaultNavigationIcon(Drawable paramDrawable)
  {
    if (this.mDefaultNavigationIcon != paramDrawable)
    {
      this.mDefaultNavigationIcon = paramDrawable;
      updateNavigationIcon();
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    int i = paramInt ^ this.mDisplayOpts;
    this.mDisplayOpts = paramInt;
    if (i != 0)
    {
      if ((i & 0x4) != 0) {
        if ((paramInt & 0x4) == 0)
        {
          this.mToolbar.setNavigationIcon(null);
        }
        else
        {
          updateNavigationIcon();
          updateHomeAccessibility();
        }
      }
      if ((i & 0x3) != 0) {
        updateToolbarLogo();
      }
      if ((i & 0x8) != 0) {
        if ((paramInt & 0x8) == 0)
        {
          this.mToolbar.setTitle(null);
          this.mToolbar.setSubtitle(null);
        }
        else
        {
          this.mToolbar.setTitle(this.mTitle);
          this.mToolbar.setSubtitle(this.mSubtitle);
        }
      }
      if (((i & 0x10) != 0) && (this.mCustomView != null)) {
        if ((paramInt & 0x10) == 0) {
          this.mToolbar.removeView(this.mCustomView);
        } else {
          this.mToolbar.addView(this.mCustomView);
        }
      }
    }
  }
  
  public void setDropdownParams(SpinnerAdapter paramSpinnerAdapter, AdapterViewCompat.OnItemSelectedListener paramOnItemSelectedListener)
  {
    ensureSpinner();
    this.mSpinner.setAdapter(paramSpinnerAdapter);
    this.mSpinner.setOnItemSelectedListener(paramOnItemSelectedListener);
  }
  
  public void setDropdownSelectedPosition(int paramInt)
  {
    if (this.mSpinner != null)
    {
      this.mSpinner.setSelection(paramInt);
      return;
    }
    throw new IllegalStateException("Can't set dropdown selected position without an adapter");
  }
  
  public void setEmbeddedTabView(ScrollingTabContainerView paramScrollingTabContainerView)
  {
    if ((this.mTabView != null) && (this.mTabView.getParent() == this.mToolbar)) {
      this.mToolbar.removeView(this.mTabView);
    }
    this.mTabView = paramScrollingTabContainerView;
    if ((paramScrollingTabContainerView != null) && (this.mNavigationMode == 2))
    {
      this.mToolbar.addView(this.mTabView, 0);
      Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)this.mTabView.getLayoutParams();
      localLayoutParams.width = -2;
      localLayoutParams.height = -2;
      localLayoutParams.gravity = 8388691;
      paramScrollingTabContainerView.setAllowCollapse(true);
    }
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt)
  {
    Drawable localDrawable;
    if (paramInt == 0) {
      localDrawable = null;
    } else {
      localDrawable = this.mTintManager.getDrawable(paramInt);
    }
    setIcon(localDrawable);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setLogo(int paramInt)
  {
    Drawable localDrawable;
    if (paramInt == 0) {
      localDrawable = null;
    } else {
      localDrawable = this.mTintManager.getDrawable(paramInt);
    }
    setLogo(localDrawable);
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    this.mLogo = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback)
  {
    if (this.mActionMenuPresenter == null)
    {
      this.mActionMenuPresenter = new ActionMenuPresenter(this.mToolbar.getContext());
      this.mActionMenuPresenter.setId(R.id.action_menu_presenter);
    }
    this.mActionMenuPresenter.setCallback(paramCallback);
    this.mToolbar.setMenu((MenuBuilder)paramMenu, this.mActionMenuPresenter);
  }
  
  public void setMenuPrepared()
  {
    this.mMenuPrepared = true;
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    String str;
    if (paramInt != 0) {
      str = getContext().getString(paramInt);
    } else {
      str = null;
    }
    setNavigationContentDescription(str);
  }
  
  public void setNavigationContentDescription(CharSequence paramCharSequence)
  {
    this.mHomeDescription = paramCharSequence;
    updateHomeAccessibility();
  }
  
  public void setNavigationIcon(int paramInt)
  {
    Drawable localDrawable;
    if (paramInt == 0) {
      localDrawable = null;
    } else {
      localDrawable = this.mTintManager.getDrawable(paramInt);
    }
    setNavigationIcon(localDrawable);
  }
  
  public void setNavigationIcon(Drawable paramDrawable)
  {
    this.mNavIcon = paramDrawable;
    updateNavigationIcon();
  }
  
  public void setNavigationMode(int paramInt)
  {
    int i = this.mNavigationMode;
    if (paramInt != i)
    {
      switch (i)
      {
      case 1: 
        if ((this.mSpinner != null) && (this.mSpinner.getParent() == this.mToolbar)) {
          this.mToolbar.removeView(this.mSpinner);
        }
        break;
      case 2: 
        if ((this.mTabView != null) && (this.mTabView.getParent() == this.mToolbar)) {
          this.mToolbar.removeView(this.mTabView);
        }
        break;
      }
      this.mNavigationMode = paramInt;
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Invalid navigation mode " + paramInt);
      case 1: 
        ensureSpinner();
        this.mToolbar.addView(this.mSpinner, 0);
        break;
      case 2: 
        if (this.mTabView != null)
        {
          this.mToolbar.addView(this.mTabView, 0);
          Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)this.mTabView.getLayoutParams();
          localLayoutParams.width = -2;
          localLayoutParams.height = -2;
          localLayoutParams.gravity = 8388691;
        }
        break;
      }
    }
  }
  
  public void setSplitToolbar(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return;
    }
    throw new UnsupportedOperationException("Cannot split an android.widget.Toolbar");
  }
  
  public void setSplitView(ViewGroup paramViewGroup) {}
  
  public void setSplitWhenNarrow(boolean paramBoolean) {}
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mSubtitle = paramCharSequence;
    if ((0x8 & this.mDisplayOpts) != 0) {
      this.mToolbar.setSubtitle(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitleSet = true;
    setTitleInt(paramCharSequence);
  }
  
  public void setWindowCallback(WindowCallback paramWindowCallback)
  {
    this.mWindowCallback = paramWindowCallback;
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    if (!this.mTitleSet) {
      setTitleInt(paramCharSequence);
    }
  }
  
  public boolean showOverflowMenu()
  {
    return this.mToolbar.showOverflowMenu();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ToolbarWidgetWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */