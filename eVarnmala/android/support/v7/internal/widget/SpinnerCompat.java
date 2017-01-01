package android.support.v7.internal.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.ListPopupWindow.ForwardingListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SpinnerAdapter;

class SpinnerCompat
  extends AbsSpinnerCompat
  implements DialogInterface.OnClickListener
{
  private static final int MAX_ITEMS_MEASURED = 15;
  public static final int MODE_DIALOG = 0;
  public static final int MODE_DROPDOWN = 1;
  private static final int MODE_THEME = -1;
  private static final String TAG = "Spinner";
  private boolean mDisableChildrenWhenDisabled;
  int mDropDownWidth;
  private ListPopupWindow.ForwardingListener mForwardingListener;
  private int mGravity;
  private SpinnerPopup mPopup;
  private DropDownAdapter mTempAdapter;
  private Rect mTempRect = new Rect();
  private final TintManager mTintManager;
  
  SpinnerCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  SpinnerCompat(Context paramContext, int paramInt)
  {
    this(paramContext, null, R.attr.spinnerStyle, paramInt);
  }
  
  SpinnerCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.spinnerStyle);
  }
  
  SpinnerCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, -1);
  }
  
  SpinnerCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.Spinner, paramInt1, 0);
    setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.Spinner_android_background));
    if (paramInt2 == -1) {
      paramInt2 = localTintTypedArray.getInt(R.styleable.Spinner_spinnerMode, 0);
    }
    switch (paramInt2)
    {
    case 0: 
      this.mPopup = new DialogPopup(null);
      break;
    case 1: 
      final DropdownPopup localDropdownPopup = new DropdownPopup(paramContext, paramAttributeSet, paramInt1);
      this.mDropDownWidth = localTintTypedArray.getLayoutDimension(R.styleable.Spinner_android_dropDownWidth, -2);
      localDropdownPopup.setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.Spinner_android_popupBackground));
      this.mPopup = localDropdownPopup;
      this.mForwardingListener = new ListPopupWindow.ForwardingListener(this)
      {
        public ListPopupWindow getPopup()
        {
          return localDropdownPopup;
        }
        
        public boolean onForwardingStarted()
        {
          if (!SpinnerCompat.this.mPopup.isShowing()) {
            SpinnerCompat.this.mPopup.show();
          }
          return true;
        }
      };
    }
    this.mGravity = localTintTypedArray.getInt(R.styleable.Spinner_android_gravity, 17);
    this.mPopup.setPromptText(localTintTypedArray.getString(R.styleable.Spinner_prompt));
    this.mDisableChildrenWhenDisabled = localTintTypedArray.getBoolean(R.styleable.Spinner_disableChildrenWhenDisabled, false);
    localTintTypedArray.recycle();
    if (this.mTempAdapter != null)
    {
      this.mPopup.setAdapter(this.mTempAdapter);
      this.mTempAdapter = null;
    }
    this.mTintManager = localTintTypedArray.getTintManager();
  }
  
  private View makeView(int paramInt, boolean paramBoolean)
  {
    if (!this.mDataChanged)
    {
      localView = this.mRecycler.get(paramInt);
      if (localView != null) {}
    }
    else
    {
      localView = this.mAdapter.getView(paramInt, null, this);
      setUpChild(localView, paramBoolean);
      return localView;
    }
    setUpChild(localView, paramBoolean);
    View localView = localView;
    return localView;
  }
  
  private void setUpChild(View paramView, boolean paramBoolean)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if (localLayoutParams == null) {
      localLayoutParams = generateDefaultLayoutParams();
    }
    if (paramBoolean) {
      addViewInLayout(paramView, 0, localLayoutParams);
    }
    paramView.setSelected(hasFocus());
    if (this.mDisableChildrenWhenDisabled) {
      paramView.setEnabled(isEnabled());
    }
    int j = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
    paramView.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, localLayoutParams.width), j);
    j = this.mSpinnerPadding.top + (getMeasuredHeight() - this.mSpinnerPadding.bottom - this.mSpinnerPadding.top - paramView.getMeasuredHeight()) / 2;
    int i = j + paramView.getMeasuredHeight();
    paramView.layout(0, j, 0 + paramView.getMeasuredWidth(), i);
  }
  
  public int getBaseline()
  {
    int j = -1;
    View localView = null;
    if (getChildCount() <= 0)
    {
      if ((this.mAdapter != null) && (this.mAdapter.getCount() > 0))
      {
        localView = makeView(0, false);
        this.mRecycler.put(0, localView);
      }
    }
    else {
      localView = getChildAt(0);
    }
    if (localView != null)
    {
      int i = localView.getBaseline();
      if (i >= 0) {
        j = i + localView.getTop();
      }
    }
    return j;
  }
  
  public int getDropDownHorizontalOffset()
  {
    return this.mPopup.getHorizontalOffset();
  }
  
  public int getDropDownVerticalOffset()
  {
    return this.mPopup.getVerticalOffset();
  }
  
  public int getDropDownWidth()
  {
    return this.mDropDownWidth;
  }
  
  public Drawable getPopupBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public CharSequence getPrompt()
  {
    return this.mPopup.getHintText();
  }
  
  void layout(int paramInt, boolean paramBoolean)
  {
    int k = this.mSpinnerPadding.left;
    int n = getRight() - getLeft() - this.mSpinnerPadding.left - this.mSpinnerPadding.right;
    if (this.mDataChanged) {
      handleDataChanged();
    }
    if (this.mItemCount != 0)
    {
      if (this.mNextSelectedPosition >= 0) {
        setSelectedPositionInt(this.mNextSelectedPosition);
      }
      recycleAllViews();
      removeAllViewsInLayout();
      this.mFirstPosition = this.mSelectedPosition;
      if (this.mAdapter != null)
      {
        View localView = makeView(this.mSelectedPosition, true);
        int m = localView.getMeasuredWidth();
        int i = k;
        int j = ViewCompat.getLayoutDirection(this);
        switch (0x7 & GravityCompat.getAbsoluteGravity(this.mGravity, j))
        {
        case 1: 
          i = k + n / 2 - m / 2;
          break;
        case 5: 
          i = k + n - m;
        }
        localView.offsetLeftAndRight(i);
      }
      this.mRecycler.clear();
      invalidate();
      checkSelectionChanged();
      this.mDataChanged = false;
      this.mNeedSync = false;
      setNextSelectedPositionInt(this.mSelectedPosition);
    }
    else
    {
      resetList();
    }
  }
  
  int measureContentWidth(SpinnerAdapter paramSpinnerAdapter, Drawable paramDrawable)
  {
    if (paramSpinnerAdapter != null)
    {
      j = 0;
      View localView = null;
      int i = 0;
      int n = View.MeasureSpec.makeMeasureSpec(0, 0);
      int k = View.MeasureSpec.makeMeasureSpec(0, 0);
      int i1 = Math.max(0, getSelectedItemPosition());
      int m = Math.min(paramSpinnerAdapter.getCount(), i1 + 15);
      for (int i2 = Math.max(0, i1 - (15 - (m - i1)));; i2++)
      {
        if (i2 >= m)
        {
          if (paramDrawable == null) {
            break;
          }
          paramDrawable.getPadding(this.mTempRect);
          j += this.mTempRect.left + this.mTempRect.right;
          break;
        }
        i1 = paramSpinnerAdapter.getItemViewType(i2);
        if (i1 != i)
        {
          i = i1;
          localView = null;
        }
        localView = paramSpinnerAdapter.getView(i2, localView, this);
        if (localView.getLayoutParams() == null) {
          localView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        }
        localView.measure(n, k);
        j = Math.max(j, localView.getMeasuredWidth());
      }
    }
    int j = 0;
    return j;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    setSelection(paramInt);
    paramDialogInterface.dismiss();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((this.mPopup != null) && (this.mPopup.isShowing())) {
      this.mPopup.dismiss();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mInLayout = true;
    layout(0, false);
    this.mInLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((this.mPopup != null) && (View.MeasureSpec.getMode(paramInt1) == Integer.MIN_VALUE)) {
      setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), measureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(paramInt1)), getMeasuredHeight());
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    Object localObject = (SavedState)paramParcelable;
    super.onRestoreInstanceState(((SavedState)localObject).getSuperState());
    if (((SavedState)localObject).showDropdown)
    {
      localObject = getViewTreeObserver();
      if (localObject != null) {
        ((ViewTreeObserver)localObject).addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            if (!SpinnerCompat.this.mPopup.isShowing()) {
              SpinnerCompat.this.mPopup.show();
            }
            ViewTreeObserver localViewTreeObserver = SpinnerCompat.this.getViewTreeObserver();
            if (localViewTreeObserver != null) {
              localViewTreeObserver.removeGlobalOnLayoutListener(this);
            }
          }
        });
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    boolean bool;
    if ((this.mPopup == null) || (!this.mPopup.isShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    localSavedState.showDropdown = bool;
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((this.mForwardingListener == null) || (!this.mForwardingListener.onTouch(this, paramMotionEvent))) {
      bool = super.onTouchEvent(paramMotionEvent);
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean performClick()
  {
    boolean bool = super.performClick();
    if (!bool)
    {
      bool = true;
      if (!this.mPopup.isShowing()) {
        this.mPopup.show();
      }
    }
    return bool;
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    super.setAdapter(paramSpinnerAdapter);
    this.mRecycler.clear();
    if ((getContext().getApplicationInfo().targetSdkVersion < 21) || (paramSpinnerAdapter == null) || (paramSpinnerAdapter.getViewTypeCount() == 1))
    {
      if (this.mPopup == null) {
        this.mTempAdapter = new DropDownAdapter(paramSpinnerAdapter);
      } else {
        this.mPopup.setAdapter(new DropDownAdapter(paramSpinnerAdapter));
      }
      return;
    }
    throw new IllegalArgumentException("Spinner adapter view type count must be 1");
  }
  
  public void setDropDownHorizontalOffset(int paramInt)
  {
    this.mPopup.setHorizontalOffset(paramInt);
  }
  
  public void setDropDownVerticalOffset(int paramInt)
  {
    this.mPopup.setVerticalOffset(paramInt);
  }
  
  public void setDropDownWidth(int paramInt)
  {
    if ((this.mPopup instanceof DropdownPopup)) {
      this.mDropDownWidth = paramInt;
    } else {
      Log.e("Spinner", "Cannot set dropdown width for MODE_DIALOG, ignoring");
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    int i;
    if (this.mDisableChildrenWhenDisabled) {
      i = getChildCount();
    }
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      getChildAt(j).setEnabled(paramBoolean);
    }
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      if ((paramInt & 0x7) == 0) {
        paramInt |= 0x800003;
      }
      this.mGravity = paramInt;
      requestLayout();
    }
  }
  
  public void setOnItemClickListener(AdapterViewCompat.OnItemClickListener paramOnItemClickListener)
  {
    throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
  }
  
  void setOnItemClickListenerInt(AdapterViewCompat.OnItemClickListener paramOnItemClickListener)
  {
    super.setOnItemClickListener(paramOnItemClickListener);
  }
  
  public void setPopupBackgroundDrawable(Drawable paramDrawable)
  {
    if ((this.mPopup instanceof DropdownPopup)) {
      ((DropdownPopup)this.mPopup).setBackgroundDrawable(paramDrawable);
    } else {
      Log.e("Spinner", "setPopupBackgroundDrawable: incompatible spinner mode; ignoring...");
    }
  }
  
  public void setPopupBackgroundResource(int paramInt)
  {
    setPopupBackgroundDrawable(this.mTintManager.getDrawable(paramInt));
  }
  
  public void setPrompt(CharSequence paramCharSequence)
  {
    this.mPopup.setPromptText(paramCharSequence);
  }
  
  public void setPromptId(int paramInt)
  {
    setPrompt(getContext().getText(paramInt));
  }
  
  private class DropdownPopup
    extends ListPopupWindow
    implements SpinnerCompat.SpinnerPopup
  {
    private ListAdapter mAdapter;
    private CharSequence mHintText;
    
    public DropdownPopup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
      setAnchorView(SpinnerCompat.this);
      setModal(true);
      setPromptPosition(0);
      setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          SpinnerCompat.this.setSelection(paramAnonymousInt);
          if (SpinnerCompat.this.mOnItemClickListener != null) {
            SpinnerCompat.this.performItemClick(paramAnonymousView, paramAnonymousInt, SpinnerCompat.DropdownPopup.this.mAdapter.getItemId(paramAnonymousInt));
          }
          SpinnerCompat.DropdownPopup.this.dismiss();
        }
      });
    }
    
    void computeContentWidth()
    {
      Object localObject = getBackground();
      int i = 0;
      if (localObject == null)
      {
        localObject = SpinnerCompat.this.mTempRect;
        SpinnerCompat.this.mTempRect.right = 0;
        ((Rect)localObject).left = 0;
      }
      else
      {
        ((Drawable)localObject).getPadding(SpinnerCompat.this.mTempRect);
        if (!ViewUtils.isLayoutRtl(SpinnerCompat.this)) {
          i = -SpinnerCompat.this.mTempRect.left;
        } else {
          i = SpinnerCompat.this.mTempRect.right;
        }
      }
      int k = SpinnerCompat.this.getPaddingLeft();
      int i1 = SpinnerCompat.this.getPaddingRight();
      int n = SpinnerCompat.this.getWidth();
      if (SpinnerCompat.this.mDropDownWidth != -2)
      {
        if (SpinnerCompat.this.mDropDownWidth != -1) {
          setContentWidth(SpinnerCompat.this.mDropDownWidth);
        } else {
          setContentWidth(n - k - i1);
        }
      }
      else
      {
        int j = SpinnerCompat.this.measureContentWidth((SpinnerAdapter)this.mAdapter, getBackground());
        int m = SpinnerCompat.this.getContext().getResources().getDisplayMetrics().widthPixels - SpinnerCompat.this.mTempRect.left - SpinnerCompat.this.mTempRect.right;
        if (j > m) {
          j = m;
        }
        setContentWidth(Math.max(j, n - k - i1));
      }
      if (!ViewUtils.isLayoutRtl(SpinnerCompat.this)) {
        i += k;
      } else {
        i += n - i1 - getWidth();
      }
      setHorizontalOffset(i);
    }
    
    public CharSequence getHintText()
    {
      return this.mHintText;
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      super.setAdapter(paramListAdapter);
      this.mAdapter = paramListAdapter;
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      this.mHintText = paramCharSequence;
    }
    
    public void show(int paramInt1, int paramInt2)
    {
      boolean bool = isShowing();
      computeContentWidth();
      setInputMethodMode(2);
      super.show();
      getListView().setChoiceMode(1);
      setSelection(SpinnerCompat.this.getSelectedItemPosition());
      if (!bool)
      {
        ViewTreeObserver localViewTreeObserver = SpinnerCompat.this.getViewTreeObserver();
        if (localViewTreeObserver != null)
        {
          final ViewTreeObserver.OnGlobalLayoutListener local2 = new ViewTreeObserver.OnGlobalLayoutListener()
          {
            public void onGlobalLayout()
            {
              SpinnerCompat.DropdownPopup.this.computeContentWidth();
              SpinnerCompat.DropdownPopup.this.show();
            }
          };
          localViewTreeObserver.addOnGlobalLayoutListener(local2);
          setOnDismissListener(new PopupWindow.OnDismissListener()
          {
            public void onDismiss()
            {
              ViewTreeObserver localViewTreeObserver = SpinnerCompat.this.getViewTreeObserver();
              if (localViewTreeObserver != null) {
                localViewTreeObserver.removeGlobalOnLayoutListener(local2);
              }
            }
          });
        }
      }
    }
  }
  
  private class DialogPopup
    implements SpinnerCompat.SpinnerPopup, DialogInterface.OnClickListener
  {
    private ListAdapter mListAdapter;
    private AlertDialog mPopup;
    private CharSequence mPrompt;
    
    private DialogPopup() {}
    
    public void dismiss()
    {
      if (this.mPopup != null)
      {
        this.mPopup.dismiss();
        this.mPopup = null;
      }
    }
    
    public Drawable getBackground()
    {
      return null;
    }
    
    public CharSequence getHintText()
    {
      return this.mPrompt;
    }
    
    public int getHorizontalOffset()
    {
      return 0;
    }
    
    public int getVerticalOffset()
    {
      return 0;
    }
    
    public boolean isShowing()
    {
      boolean bool;
      if (this.mPopup == null) {
        bool = false;
      } else {
        bool = this.mPopup.isShowing();
      }
      return bool;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      SpinnerCompat.this.setSelection(paramInt);
      if (SpinnerCompat.this.mOnItemClickListener != null) {
        SpinnerCompat.this.performItemClick(null, paramInt, this.mListAdapter.getItemId(paramInt));
      }
      dismiss();
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      this.mListAdapter = paramListAdapter;
    }
    
    public void setBackgroundDrawable(Drawable paramDrawable)
    {
      Log.e("Spinner", "Cannot set popup background for MODE_DIALOG, ignoring");
    }
    
    public void setHorizontalOffset(int paramInt)
    {
      Log.e("Spinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      this.mPrompt = paramCharSequence;
    }
    
    public void setVerticalOffset(int paramInt)
    {
      Log.e("Spinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
    }
    
    public void show()
    {
      if (this.mListAdapter != null)
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(SpinnerCompat.this.getContext());
        if (this.mPrompt != null) {
          localBuilder.setTitle(this.mPrompt);
        }
        this.mPopup = localBuilder.setSingleChoiceItems(this.mListAdapter, SpinnerCompat.this.getSelectedItemPosition(), this).create();
        this.mPopup.show();
      }
    }
  }
  
  private static abstract interface SpinnerPopup
  {
    public abstract void dismiss();
    
    public abstract Drawable getBackground();
    
    public abstract CharSequence getHintText();
    
    public abstract int getHorizontalOffset();
    
    public abstract int getVerticalOffset();
    
    public abstract boolean isShowing();
    
    public abstract void setAdapter(ListAdapter paramListAdapter);
    
    public abstract void setBackgroundDrawable(Drawable paramDrawable);
    
    public abstract void setHorizontalOffset(int paramInt);
    
    public abstract void setPromptText(CharSequence paramCharSequence);
    
    public abstract void setVerticalOffset(int paramInt);
    
    public abstract void show();
  }
  
  private static class DropDownAdapter
    implements ListAdapter, SpinnerAdapter
  {
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    
    public DropDownAdapter(SpinnerAdapter paramSpinnerAdapter)
    {
      this.mAdapter = paramSpinnerAdapter;
      if ((paramSpinnerAdapter instanceof ListAdapter)) {
        this.mListAdapter = ((ListAdapter)paramSpinnerAdapter);
      }
    }
    
    public boolean areAllItemsEnabled()
    {
      ListAdapter localListAdapter = this.mListAdapter;
      boolean bool;
      if (localListAdapter == null) {
        bool = true;
      } else {
        bool = bool.areAllItemsEnabled();
      }
      return bool;
    }
    
    public int getCount()
    {
      int i;
      if (this.mAdapter != null) {
        i = this.mAdapter.getCount();
      } else {
        i = 0;
      }
      return i;
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      View localView;
      if (this.mAdapter != null) {
        localView = this.mAdapter.getDropDownView(paramInt, paramView, paramViewGroup);
      } else {
        localView = null;
      }
      return localView;
    }
    
    public Object getItem(int paramInt)
    {
      Object localObject;
      if (this.mAdapter != null) {
        localObject = this.mAdapter.getItem(paramInt);
      } else {
        localObject = null;
      }
      return localObject;
    }
    
    public long getItemId(int paramInt)
    {
      long l;
      if (this.mAdapter != null) {
        l = this.mAdapter.getItemId(paramInt);
      } else {
        l = -1L;
      }
      return l;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      boolean bool;
      if ((this.mAdapter == null) || (!this.mAdapter.hasStableIds())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean isEmpty()
    {
      boolean bool;
      if (getCount() != 0) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean isEnabled(int paramInt)
    {
      ListAdapter localListAdapter = this.mListAdapter;
      boolean bool;
      if (localListAdapter == null) {
        bool = true;
      } else {
        bool = bool.isEnabled(paramInt);
      }
      return bool;
    }
    
    public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (this.mAdapter != null) {
        this.mAdapter.registerDataSetObserver(paramDataSetObserver);
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (this.mAdapter != null) {
        this.mAdapter.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  static class SavedState
    extends AbsSpinnerCompat.SavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SpinnerCompat.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SpinnerCompat.SavedState(paramAnonymousParcel, null);
      }
      
      public SpinnerCompat.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SpinnerCompat.SavedState[paramAnonymousInt];
      }
    };
    boolean showDropdown;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      boolean bool;
      if (paramParcel.readByte() == 0) {
        bool = false;
      } else {
        bool = true;
      }
      this.showDropdown = bool;
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      int i;
      if (!this.showDropdown) {
        i = 0;
      } else {
        i = 1;
      }
      paramParcel.writeByte((byte)i);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\SpinnerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */