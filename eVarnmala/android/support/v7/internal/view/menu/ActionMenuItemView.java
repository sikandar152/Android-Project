package android.support.v7.internal.view.menu;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.bool;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.support.v7.internal.widget.CompatTextView;
import android.support.v7.widget.ActionMenuView.ActionMenuChildView;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.ListPopupWindow.ForwardingListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

public class ActionMenuItemView
  extends CompatTextView
  implements MenuView.ItemView, View.OnClickListener, View.OnLongClickListener, ActionMenuView.ActionMenuChildView
{
  private static final int MAX_ICON_SIZE = 32;
  private static final String TAG = "ActionMenuItemView";
  private boolean mAllowTextWithIcon;
  private boolean mExpandedFormat;
  private ListPopupWindow.ForwardingListener mForwardingListener;
  private Drawable mIcon;
  private MenuItemImpl mItemData;
  private MenuBuilder.ItemInvoker mItemInvoker;
  private int mMaxIconSize;
  private int mMinWidth;
  private PopupCallback mPopupCallback;
  private int mSavedPaddingLeft;
  private CharSequence mTitle;
  
  public ActionMenuItemView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ActionMenuItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = paramContext.getResources();
    this.mAllowTextWithIcon = localResources.getBoolean(R.bool.abc_config_allowActionMenuItemTextWithIcon);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActionMenuItemView, paramInt, 0);
    this.mMinWidth = localTypedArray.getDimensionPixelSize(R.styleable.ActionMenuItemView_android_minWidth, 0);
    localTypedArray.recycle();
    this.mMaxIconSize = ((int)(0.5F + 32.0F * localResources.getDisplayMetrics().density));
    setOnClickListener(this);
    setOnLongClickListener(this);
    setTransformationMethod(new AllCapsTransformationMethod(paramContext));
    this.mSavedPaddingLeft = -1;
  }
  
  private void updateTextButtonVisibility()
  {
    int j = 0;
    int i;
    if (TextUtils.isEmpty(this.mTitle)) {
      i = 0;
    } else {
      i = 1;
    }
    if ((this.mIcon == null) || ((this.mItemData.showsTextAsAction()) && ((this.mAllowTextWithIcon) || (this.mExpandedFormat)))) {
      j = 1;
    }
    CharSequence localCharSequence;
    if ((i & j) == 0) {
      localCharSequence = null;
    } else {
      localCharSequence = this.mTitle;
    }
    setText(localCharSequence);
  }
  
  public MenuItemImpl getItemData()
  {
    return this.mItemData;
  }
  
  public boolean hasText()
  {
    boolean bool;
    if (TextUtils.isEmpty(getText())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void initialize(MenuItemImpl paramMenuItemImpl, int paramInt)
  {
    this.mItemData = paramMenuItemImpl;
    setIcon(paramMenuItemImpl.getIcon());
    setTitle(paramMenuItemImpl.getTitleForItemView(this));
    setId(paramMenuItemImpl.getItemId());
    int i;
    if (!paramMenuItemImpl.isVisible()) {
      i = 8;
    } else {
      i = 0;
    }
    setVisibility(i);
    setEnabled(paramMenuItemImpl.isEnabled());
    if ((paramMenuItemImpl.hasSubMenu()) && (this.mForwardingListener == null)) {
      this.mForwardingListener = new ActionMenuItemForwardingListener();
    }
  }
  
  public boolean needsDividerAfter()
  {
    return hasText();
  }
  
  public boolean needsDividerBefore()
  {
    boolean bool;
    if ((!hasText()) || (this.mItemData.getIcon() != null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void onClick(View paramView)
  {
    if (this.mItemInvoker != null) {
      this.mItemInvoker.invokeItem(this.mItemData);
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (Build.VERSION.SDK_INT >= 8) {
      super.onConfigurationChanged(paramConfiguration);
    }
    this.mAllowTextWithIcon = getContext().getResources().getBoolean(R.bool.abc_config_allowActionMenuItemTextWithIcon);
    updateTextButtonVisibility();
  }
  
  public boolean onLongClick(View paramView)
  {
    int i = 0;
    if (!hasText())
    {
      int[] arrayOfInt = new int[2];
      Rect localRect = new Rect();
      getLocationOnScreen(arrayOfInt);
      getWindowVisibleDisplayFrame(localRect);
      Object localObject = getContext();
      int m = getWidth();
      i = getHeight();
      int j = arrayOfInt[1] + i / 2;
      int k = arrayOfInt[0] + m / 2;
      if (ViewCompat.getLayoutDirection(paramView) == 0) {
        k = ((Context)localObject).getResources().getDisplayMetrics().widthPixels - k;
      }
      localObject = Toast.makeText((Context)localObject, this.mItemData.getTitle(), 0);
      if (j >= localRect.height()) {
        ((Toast)localObject).setGravity(81, 0, i);
      } else {
        ((Toast)localObject).setGravity(8388661, k, i);
      }
      ((Toast)localObject).show();
      i = 1;
    }
    return i;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool = hasText();
    if ((bool) && (this.mSavedPaddingLeft >= 0)) {
      super.setPadding(this.mSavedPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
    super.onMeasure(paramInt1, paramInt2);
    int i = View.MeasureSpec.getMode(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt1);
    int j = getMeasuredWidth();
    if (i != Integer.MIN_VALUE) {
      k = this.mMinWidth;
    } else {
      k = Math.min(k, this.mMinWidth);
    }
    if ((i != 1073741824) && (this.mMinWidth > 0) && (j < k)) {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), paramInt2);
    }
    if ((!bool) && (this.mIcon != null)) {
      super.setPadding((getMeasuredWidth() - this.mIcon.getBounds().width()) / 2, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((!this.mItemData.hasSubMenu()) || (this.mForwardingListener == null) || (!this.mForwardingListener.onTouch(this, paramMotionEvent))) {
      bool = super.onTouchEvent(paramMotionEvent);
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean prefersCondensedTitle()
  {
    return true;
  }
  
  public void setCheckable(boolean paramBoolean) {}
  
  public void setChecked(boolean paramBoolean) {}
  
  public void setExpandedFormat(boolean paramBoolean)
  {
    if (this.mExpandedFormat != paramBoolean)
    {
      this.mExpandedFormat = paramBoolean;
      if (this.mItemData != null) {
        this.mItemData.actionFormatChanged();
      }
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    if (paramDrawable != null)
    {
      int j = paramDrawable.getIntrinsicWidth();
      int i = paramDrawable.getIntrinsicHeight();
      float f;
      if (j > this.mMaxIconSize)
      {
        f = this.mMaxIconSize / j;
        j = this.mMaxIconSize;
        i = (int)(f * i);
      }
      if (i > this.mMaxIconSize)
      {
        f = this.mMaxIconSize / i;
        i = this.mMaxIconSize;
        j = (int)(f * j);
      }
      paramDrawable.setBounds(0, 0, j, i);
    }
    setCompoundDrawables(paramDrawable, null, null, null);
    updateTextButtonVisibility();
  }
  
  public void setItemInvoker(MenuBuilder.ItemInvoker paramItemInvoker)
  {
    this.mItemInvoker = paramItemInvoker;
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mSavedPaddingLeft = paramInt1;
    super.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setPopupCallback(PopupCallback paramPopupCallback)
  {
    this.mPopupCallback = paramPopupCallback;
  }
  
  public void setShortcut(boolean paramBoolean, char paramChar) {}
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    setContentDescription(this.mTitle);
    updateTextButtonVisibility();
  }
  
  public boolean showsIcon()
  {
    return true;
  }
  
  public static abstract class PopupCallback
  {
    public abstract ListPopupWindow getPopup();
  }
  
  private class ActionMenuItemForwardingListener
    extends ListPopupWindow.ForwardingListener
  {
    public ActionMenuItemForwardingListener()
    {
      super();
    }
    
    public ListPopupWindow getPopup()
    {
      ListPopupWindow localListPopupWindow;
      if (ActionMenuItemView.this.mPopupCallback == null) {
        localListPopupWindow = null;
      } else {
        localListPopupWindow = ActionMenuItemView.this.mPopupCallback.getPopup();
      }
      return localListPopupWindow;
    }
    
    protected boolean onForwardingStarted()
    {
      boolean bool = false;
      if ((ActionMenuItemView.this.mItemInvoker != null) && (ActionMenuItemView.this.mItemInvoker.invokeItem(ActionMenuItemView.this.mItemData)))
      {
        ListPopupWindow localListPopupWindow = getPopup();
        if ((localListPopupWindow != null) && (localListPopupWindow.isShowing())) {
          bool = true;
        }
      }
      return bool;
    }
    
    protected boolean onForwardingStopped()
    {
      ListPopupWindow localListPopupWindow = getPopup();
      boolean bool;
      if (localListPopupWindow == null)
      {
        bool = false;
      }
      else
      {
        bool.dismiss();
        bool = true;
      }
      return bool;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\ActionMenuItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */