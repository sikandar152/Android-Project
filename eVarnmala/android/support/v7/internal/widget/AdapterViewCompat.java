package android.support.v7.internal.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AdapterViewCompat<T extends Adapter>
  extends ViewGroup
{
  public static final int INVALID_POSITION = -1;
  public static final long INVALID_ROW_ID = Long.MIN_VALUE;
  static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
  static final int ITEM_VIEW_TYPE_IGNORE = -1;
  static final int SYNC_FIRST_POSITION = 1;
  static final int SYNC_MAX_DURATION_MILLIS = 100;
  static final int SYNC_SELECTED_POSITION;
  boolean mBlockLayoutRequests = false;
  boolean mDataChanged;
  private boolean mDesiredFocusableInTouchModeState;
  private boolean mDesiredFocusableState;
  private View mEmptyView;
  @ViewDebug.ExportedProperty(category="scrolling")
  int mFirstPosition = 0;
  boolean mInLayout = false;
  @ViewDebug.ExportedProperty(category="list")
  int mItemCount;
  private int mLayoutHeight;
  boolean mNeedSync = false;
  @ViewDebug.ExportedProperty(category="list")
  int mNextSelectedPosition = -1;
  long mNextSelectedRowId = Long.MIN_VALUE;
  int mOldItemCount;
  int mOldSelectedPosition = -1;
  long mOldSelectedRowId = Long.MIN_VALUE;
  OnItemClickListener mOnItemClickListener;
  OnItemLongClickListener mOnItemLongClickListener;
  OnItemSelectedListener mOnItemSelectedListener;
  @ViewDebug.ExportedProperty(category="list")
  int mSelectedPosition = -1;
  long mSelectedRowId = Long.MIN_VALUE;
  private AdapterViewCompat<T>.SelectionNotifier mSelectionNotifier;
  int mSpecificTop;
  long mSyncHeight;
  int mSyncMode;
  int mSyncPosition;
  long mSyncRowId = Long.MIN_VALUE;
  
  AdapterViewCompat(Context paramContext)
  {
    super(paramContext);
  }
  
  AdapterViewCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  AdapterViewCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void fireOnSelected()
  {
    if (this.mOnItemSelectedListener != null)
    {
      int i = getSelectedItemPosition();
      if (i < 0)
      {
        this.mOnItemSelectedListener.onNothingSelected(this);
      }
      else
      {
        View localView = getSelectedView();
        this.mOnItemSelectedListener.onItemSelected(this, localView, i, getAdapter().getItemId(i));
      }
    }
  }
  
  private void updateEmptyStatus(boolean paramBoolean)
  {
    if (isInFilterMode()) {
      paramBoolean = false;
    }
    if (!paramBoolean)
    {
      if (this.mEmptyView != null) {
        this.mEmptyView.setVisibility(8);
      }
      setVisibility(0);
    }
    else
    {
      if (this.mEmptyView == null)
      {
        setVisibility(0);
      }
      else
      {
        this.mEmptyView.setVisibility(0);
        setVisibility(8);
      }
      if (this.mDataChanged) {
        onLayout(false, getLeft(), getTop(), getRight(), getBottom());
      }
    }
  }
  
  public void addView(View paramView)
  {
    throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
  }
  
  public void addView(View paramView, int paramInt)
  {
    throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
  }
  
  protected boolean canAnimate()
  {
    boolean bool;
    if ((!super.canAnimate()) || (this.mItemCount <= 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  void checkFocus()
  {
    boolean bool1 = false;
    Adapter localAdapter = getAdapter();
    boolean bool2;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    int i;
    if ((bool2) && (!isInFilterMode())) {
      i = 0;
    } else {
      i = 1;
    }
    if ((i == 0) || (!this.mDesiredFocusableInTouchModeState)) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    super.setFocusableInTouchMode(bool2);
    if ((i == 0) || (!this.mDesiredFocusableState)) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    super.setFocusable(bool2);
    if (this.mEmptyView != null)
    {
      if ((localAdapter == null) || (localAdapter.isEmpty())) {
        bool1 = true;
      }
      updateEmptyStatus(bool1);
    }
  }
  
  void checkSelectionChanged()
  {
    if ((this.mSelectedPosition != this.mOldSelectedPosition) || (this.mSelectedRowId != this.mOldSelectedRowId))
    {
      selectionChanged();
      this.mOldSelectedPosition = this.mSelectedPosition;
      this.mOldSelectedRowId = this.mSelectedRowId;
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    View localView = getSelectedView();
    boolean bool;
    if ((localView == null) || (localView.getVisibility() != 0) || (!localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }
  
  int findSyncPosition()
  {
    int i = this.mItemCount;
    int i2;
    if (i != 0)
    {
      long l1 = this.mSyncRowId;
      int j = this.mSyncPosition;
      if (l1 != Long.MIN_VALUE)
      {
        j = Math.max(0, j);
        i2 = Math.min(i - 1, j);
        long l2 = 100L + SystemClock.uptimeMillis();
        int k = i2;
        int i1 = i2;
        int m = 0;
        Adapter localAdapter = getAdapter();
        if (localAdapter != null)
        {
          while (SystemClock.uptimeMillis() <= l2)
          {
            if (localAdapter.getItemId(i2) == l1) {
              break label222;
            }
            int i3;
            if (i1 != i - 1) {
              i3 = 0;
            } else {
              i3 = 1;
            }
            int n;
            if (k != 0) {
              n = 0;
            } else {
              n = 1;
            }
            if ((i3 != 0) && (n != 0)) {
              break;
            }
            if ((n == 0) && ((m == 0) || (i3 != 0)))
            {
              if ((i3 != 0) || ((m == 0) && (n == 0)))
              {
                k--;
                i2 = k;
                m = 1;
              }
            }
            else
            {
              i1++;
              i2 = i1;
              m = 0;
            }
          }
          i2 = -1;
        }
        else
        {
          i2 = -1;
        }
      }
      else
      {
        i2 = -1;
      }
    }
    else
    {
      i2 = -1;
    }
    label222:
    return i2;
  }
  
  public abstract T getAdapter();
  
  @ViewDebug.CapturedViewProperty
  public int getCount()
  {
    return this.mItemCount;
  }
  
  public View getEmptyView()
  {
    return this.mEmptyView;
  }
  
  public int getFirstVisiblePosition()
  {
    return this.mFirstPosition;
  }
  
  public Object getItemAtPosition(int paramInt)
  {
    Object localObject = getAdapter();
    if ((localObject != null) && (paramInt >= 0)) {
      localObject = ((Adapter)localObject).getItem(paramInt);
    } else {
      localObject = null;
    }
    return localObject;
  }
  
  public long getItemIdAtPosition(int paramInt)
  {
    Adapter localAdapter = getAdapter();
    long l;
    if ((localAdapter != null) && (paramInt >= 0)) {
      l = localAdapter.getItemId(paramInt);
    } else {
      l = Long.MIN_VALUE;
    }
    return l;
  }
  
  public int getLastVisiblePosition()
  {
    return -1 + (this.mFirstPosition + getChildCount());
  }
  
  public final OnItemClickListener getOnItemClickListener()
  {
    return this.mOnItemClickListener;
  }
  
  public final OnItemLongClickListener getOnItemLongClickListener()
  {
    return this.mOnItemLongClickListener;
  }
  
  public final OnItemSelectedListener getOnItemSelectedListener()
  {
    return this.mOnItemSelectedListener;
  }
  
  public int getPositionForView(View paramView)
  {
    int i = -1;
    Object localObject = paramView;
    int j;
    try
    {
      for (;;)
      {
        View localView = (View)((View)localObject).getParent();
        j = localView.equals(this);
        if (j != 0) {
          break;
        }
        localObject = localView;
      }
      return i;
    }
    catch (ClassCastException localClassCastException) {}
    label80:
    for (;;)
    {
      int k = getChildCount();
      for (j = 0;; j++)
      {
        if (j >= k) {
          break label80;
        }
        if (getChildAt(j).equals(localObject))
        {
          i = j + this.mFirstPosition;
          break;
        }
      }
    }
  }
  
  public Object getSelectedItem()
  {
    Object localObject = getAdapter();
    int i = getSelectedItemPosition();
    if ((localObject == null) || (((Adapter)localObject).getCount() <= 0) || (i < 0)) {
      localObject = null;
    } else {
      localObject = ((Adapter)localObject).getItem(i);
    }
    return localObject;
  }
  
  @ViewDebug.CapturedViewProperty
  public long getSelectedItemId()
  {
    return this.mNextSelectedRowId;
  }
  
  @ViewDebug.CapturedViewProperty
  public int getSelectedItemPosition()
  {
    return this.mNextSelectedPosition;
  }
  
  public abstract View getSelectedView();
  
  void handleDataChanged()
  {
    int k = this.mItemCount;
    int i = 0;
    if (k > 0)
    {
      int j;
      if (this.mNeedSync)
      {
        this.mNeedSync = false;
        j = findSyncPosition();
        if ((j >= 0) && (lookForSelectablePosition(j, true) == j))
        {
          setNextSelectedPositionInt(j);
          i = 1;
        }
      }
      if (i == 0)
      {
        j = getSelectedItemPosition();
        if (j >= k) {
          j = k - 1;
        }
        if (j < 0) {
          j = 0;
        }
        k = lookForSelectablePosition(j, true);
        if (k < 0) {
          k = lookForSelectablePosition(j, false);
        }
        if (k >= 0)
        {
          setNextSelectedPositionInt(k);
          checkSelectionChanged();
          i = 1;
        }
      }
    }
    if (i == 0)
    {
      this.mSelectedPosition = -1;
      this.mSelectedRowId = Long.MIN_VALUE;
      this.mNextSelectedPosition = -1;
      this.mNextSelectedRowId = Long.MIN_VALUE;
      this.mNeedSync = false;
      checkSelectionChanged();
    }
  }
  
  boolean isInFilterMode()
  {
    return false;
  }
  
  int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    return paramInt;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mSelectionNotifier);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mLayoutHeight = getHeight();
  }
  
  public boolean performItemClick(View paramView, int paramInt, long paramLong)
  {
    boolean bool = false;
    if (this.mOnItemClickListener != null)
    {
      playSoundEffect(0);
      if (paramView != null) {
        paramView.sendAccessibilityEvent(1);
      }
      this.mOnItemClickListener.onItemClick(this, paramView, paramInt, paramLong);
      bool = true;
    }
    return bool;
  }
  
  void rememberSyncState()
  {
    if (getChildCount() > 0)
    {
      this.mNeedSync = true;
      this.mSyncHeight = this.mLayoutHeight;
      View localView;
      if (this.mSelectedPosition < 0)
      {
        localView = getChildAt(0);
        Adapter localAdapter = getAdapter();
        if ((this.mFirstPosition < 0) || (this.mFirstPosition >= localAdapter.getCount())) {
          this.mSyncRowId = -1L;
        } else {
          this.mSyncRowId = localAdapter.getItemId(this.mFirstPosition);
        }
        this.mSyncPosition = this.mFirstPosition;
        if (localView != null) {
          this.mSpecificTop = localView.getTop();
        }
        this.mSyncMode = 1;
      }
      else
      {
        localView = getChildAt(this.mSelectedPosition - this.mFirstPosition);
        this.mSyncRowId = this.mNextSelectedRowId;
        this.mSyncPosition = this.mNextSelectedPosition;
        if (localView != null) {
          this.mSpecificTop = localView.getTop();
        }
        this.mSyncMode = 0;
      }
    }
  }
  
  public void removeAllViews()
  {
    throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
  }
  
  public void removeView(View paramView)
  {
    throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
  }
  
  public void removeViewAt(int paramInt)
  {
    throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
  }
  
  void selectionChanged()
  {
    if (this.mOnItemSelectedListener != null) {
      if ((!this.mInLayout) && (!this.mBlockLayoutRequests))
      {
        fireOnSelected();
      }
      else
      {
        if (this.mSelectionNotifier == null) {
          this.mSelectionNotifier = new SelectionNotifier(null);
        }
        post(this.mSelectionNotifier);
      }
    }
    if ((this.mSelectedPosition != -1) && (isShown()) && (!isInTouchMode())) {
      sendAccessibilityEvent(4);
    }
  }
  
  public abstract void setAdapter(T paramT);
  
  public void setEmptyView(View paramView)
  {
    this.mEmptyView = paramView;
    Adapter localAdapter = getAdapter();
    boolean bool;
    if ((localAdapter != null) && (!localAdapter.isEmpty())) {
      bool = false;
    } else {
      bool = true;
    }
    updateEmptyStatus(bool);
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    boolean bool = true;
    Adapter localAdapter = getAdapter();
    int i;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      i = 0;
    } else {
      i = bool;
    }
    this.mDesiredFocusableState = paramBoolean;
    if (!paramBoolean) {
      this.mDesiredFocusableInTouchModeState = false;
    }
    if ((!paramBoolean) || ((i != 0) && (!isInFilterMode()))) {
      bool = false;
    }
    super.setFocusable(bool);
  }
  
  public void setFocusableInTouchMode(boolean paramBoolean)
  {
    boolean bool = true;
    Adapter localAdapter = getAdapter();
    int i;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      i = 0;
    } else {
      i = bool;
    }
    this.mDesiredFocusableInTouchModeState = paramBoolean;
    if (paramBoolean) {
      this.mDesiredFocusableState = bool;
    }
    if ((!paramBoolean) || ((i != 0) && (!isInFilterMode()))) {
      bool = false;
    }
    super.setFocusableInTouchMode(bool);
  }
  
  void setNextSelectedPositionInt(int paramInt)
  {
    this.mNextSelectedPosition = paramInt;
    this.mNextSelectedRowId = getItemIdAtPosition(paramInt);
    if ((this.mNeedSync) && (this.mSyncMode == 0) && (paramInt >= 0))
    {
      this.mSyncPosition = paramInt;
      this.mSyncRowId = this.mNextSelectedRowId;
    }
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
  }
  
  public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.mOnItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemLongClickListener(OnItemLongClickListener paramOnItemLongClickListener)
  {
    if (!isLongClickable()) {
      setLongClickable(true);
    }
    this.mOnItemLongClickListener = paramOnItemLongClickListener;
  }
  
  public void setOnItemSelectedListener(OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mOnItemSelectedListener = paramOnItemSelectedListener;
  }
  
  void setSelectedPositionInt(int paramInt)
  {
    this.mSelectedPosition = paramInt;
    this.mSelectedRowId = getItemIdAtPosition(paramInt);
  }
  
  public abstract void setSelection(int paramInt);
  
  private class SelectionNotifier
    implements Runnable
  {
    private SelectionNotifier() {}
    
    public void run()
    {
      if (!AdapterViewCompat.this.mDataChanged) {
        AdapterViewCompat.this.fireOnSelected();
      } else if (AdapterViewCompat.this.getAdapter() != null) {
        AdapterViewCompat.this.post(this);
      }
    }
  }
  
  class AdapterDataSetObserver
    extends DataSetObserver
  {
    private Parcelable mInstanceState = null;
    
    AdapterDataSetObserver() {}
    
    public void clearSavedState()
    {
      this.mInstanceState = null;
    }
    
    public void onChanged()
    {
      AdapterViewCompat.this.mDataChanged = true;
      AdapterViewCompat.this.mOldItemCount = AdapterViewCompat.this.mItemCount;
      AdapterViewCompat.this.mItemCount = AdapterViewCompat.this.getAdapter().getCount();
      if ((!AdapterViewCompat.this.getAdapter().hasStableIds()) || (this.mInstanceState == null) || (AdapterViewCompat.this.mOldItemCount != 0) || (AdapterViewCompat.this.mItemCount <= 0))
      {
        AdapterViewCompat.this.rememberSyncState();
      }
      else
      {
        AdapterViewCompat.this.onRestoreInstanceState(this.mInstanceState);
        this.mInstanceState = null;
      }
      AdapterViewCompat.this.checkFocus();
      AdapterViewCompat.this.requestLayout();
    }
    
    public void onInvalidated()
    {
      AdapterViewCompat.this.mDataChanged = true;
      if (AdapterViewCompat.this.getAdapter().hasStableIds()) {
        this.mInstanceState = AdapterViewCompat.this.onSaveInstanceState();
      }
      AdapterViewCompat.this.mOldItemCount = AdapterViewCompat.this.mItemCount;
      AdapterViewCompat.this.mItemCount = 0;
      AdapterViewCompat.this.mSelectedPosition = -1;
      AdapterViewCompat.this.mSelectedRowId = Long.MIN_VALUE;
      AdapterViewCompat.this.mNextSelectedPosition = -1;
      AdapterViewCompat.this.mNextSelectedRowId = Long.MIN_VALUE;
      AdapterViewCompat.this.mNeedSync = false;
      AdapterViewCompat.this.checkFocus();
      AdapterViewCompat.this.requestLayout();
    }
  }
  
  public static class AdapterContextMenuInfo
    implements ContextMenu.ContextMenuInfo
  {
    public long id;
    public int position;
    public View targetView;
    
    public AdapterContextMenuInfo(View paramView, int paramInt, long paramLong)
    {
      this.targetView = paramView;
      this.position = paramInt;
      this.id = paramLong;
    }
  }
  
  public static abstract interface OnItemSelectedListener
  {
    public abstract void onItemSelected(AdapterViewCompat<?> paramAdapterViewCompat, View paramView, int paramInt, long paramLong);
    
    public abstract void onNothingSelected(AdapterViewCompat<?> paramAdapterViewCompat);
  }
  
  public static abstract interface OnItemLongClickListener
  {
    public abstract boolean onItemLongClick(AdapterViewCompat<?> paramAdapterViewCompat, View paramView, int paramInt, long paramLong);
  }
  
  class OnItemClickListenerWrapper
    implements AdapterView.OnItemClickListener
  {
    private final AdapterViewCompat.OnItemClickListener mWrappedListener;
    
    public OnItemClickListenerWrapper(AdapterViewCompat.OnItemClickListener paramOnItemClickListener)
    {
      this.mWrappedListener = paramOnItemClickListener;
    }
    
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      this.mWrappedListener.onItemClick(AdapterViewCompat.this, paramView, paramInt, paramLong);
    }
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(AdapterViewCompat<?> paramAdapterViewCompat, View paramView, int paramInt, long paramLong);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\AdapterViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */