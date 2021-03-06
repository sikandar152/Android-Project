package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public abstract interface DecorToolbar
{
  public abstract void animateToVisibility(int paramInt);
  
  public abstract boolean canShowOverflowMenu();
  
  public abstract boolean canSplit();
  
  public abstract void collapseActionView();
  
  public abstract void dismissPopupMenus();
  
  public abstract Context getContext();
  
  public abstract View getCustomView();
  
  public abstract int getDisplayOptions();
  
  public abstract int getDropdownItemCount();
  
  public abstract int getDropdownSelectedPosition();
  
  public abstract int getNavigationMode();
  
  public abstract CharSequence getSubtitle();
  
  public abstract CharSequence getTitle();
  
  public abstract ViewGroup getViewGroup();
  
  public abstract boolean hasEmbeddedTabs();
  
  public abstract boolean hasExpandedActionView();
  
  public abstract boolean hasIcon();
  
  public abstract boolean hasLogo();
  
  public abstract boolean hideOverflowMenu();
  
  public abstract void initIndeterminateProgress();
  
  public abstract void initProgress();
  
  public abstract boolean isOverflowMenuShowPending();
  
  public abstract boolean isOverflowMenuShowing();
  
  public abstract boolean isSplit();
  
  public abstract boolean isTitleTruncated();
  
  public abstract void restoreHierarchyState(SparseArray<Parcelable> paramSparseArray);
  
  public abstract void saveHierarchyState(SparseArray<Parcelable> paramSparseArray);
  
  public abstract void setCollapsible(boolean paramBoolean);
  
  public abstract void setCustomView(View paramView);
  
  public abstract void setDefaultNavigationContentDescription(int paramInt);
  
  public abstract void setDefaultNavigationIcon(Drawable paramDrawable);
  
  public abstract void setDisplayOptions(int paramInt);
  
  public abstract void setDropdownParams(SpinnerAdapter paramSpinnerAdapter, AdapterViewCompat.OnItemSelectedListener paramOnItemSelectedListener);
  
  public abstract void setDropdownSelectedPosition(int paramInt);
  
  public abstract void setEmbeddedTabView(ScrollingTabContainerView paramScrollingTabContainerView);
  
  public abstract void setHomeButtonEnabled(boolean paramBoolean);
  
  public abstract void setIcon(int paramInt);
  
  public abstract void setIcon(Drawable paramDrawable);
  
  public abstract void setLogo(int paramInt);
  
  public abstract void setLogo(Drawable paramDrawable);
  
  public abstract void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback);
  
  public abstract void setMenuPrepared();
  
  public abstract void setNavigationContentDescription(int paramInt);
  
  public abstract void setNavigationContentDescription(CharSequence paramCharSequence);
  
  public abstract void setNavigationIcon(int paramInt);
  
  public abstract void setNavigationIcon(Drawable paramDrawable);
  
  public abstract void setNavigationMode(int paramInt);
  
  public abstract void setSplitToolbar(boolean paramBoolean);
  
  public abstract void setSplitView(ViewGroup paramViewGroup);
  
  public abstract void setSplitWhenNarrow(boolean paramBoolean);
  
  public abstract void setSubtitle(CharSequence paramCharSequence);
  
  public abstract void setTitle(CharSequence paramCharSequence);
  
  public abstract void setWindowCallback(WindowCallback paramWindowCallback);
  
  public abstract void setWindowTitle(CharSequence paramCharSequence);
  
  public abstract boolean showOverflowMenu();
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\DecorToolbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */