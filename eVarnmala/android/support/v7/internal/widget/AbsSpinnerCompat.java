package android.support.v7.internal.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.SpinnerAdapter;

abstract class AbsSpinnerCompat
  extends AdapterViewCompat<SpinnerAdapter>
{
  SpinnerAdapter mAdapter;
  private DataSetObserver mDataSetObserver;
  int mHeightMeasureSpec;
  final RecycleBin mRecycler = new RecycleBin();
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  int mSelectionTopPadding = 0;
  final Rect mSpinnerPadding = new Rect();
  private Rect mTouchFrame;
  int mWidthMeasureSpec;
  
  AbsSpinnerCompat(Context paramContext)
  {
    super(paramContext);
    initAbsSpinner();
  }
  
  AbsSpinnerCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  AbsSpinnerCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initAbsSpinner();
  }
  
  private void initAbsSpinner()
  {
    setFocusable(true);
    setWillNotDraw(false);
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.LayoutParams(-1, -2);
  }
  
  public SpinnerAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  int getChildHeight(View paramView)
  {
    return paramView.getMeasuredHeight();
  }
  
  int getChildWidth(View paramView)
  {
    return paramView.getMeasuredWidth();
  }
  
  public int getCount()
  {
    return this.mItemCount;
  }
  
  public View getSelectedView()
  {
    View localView;
    if ((this.mItemCount <= 0) || (this.mSelectedPosition < 0)) {
      localView = null;
    } else {
      localView = getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }
    return localView;
  }
  
  abstract void layout(int paramInt, boolean paramBoolean);
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int i3 = getPaddingLeft();
    int n = getPaddingTop();
    int k = getPaddingRight();
    int j = getPaddingBottom();
    Rect localRect3 = this.mSpinnerPadding;
    if (i3 <= this.mSelectionLeftPadding) {
      i3 = this.mSelectionLeftPadding;
    }
    localRect3.left = i3;
    localRect3 = this.mSpinnerPadding;
    if (n <= this.mSelectionTopPadding) {
      n = this.mSelectionTopPadding;
    }
    localRect3.top = n;
    Rect localRect2 = this.mSpinnerPadding;
    if (k <= this.mSelectionRightPadding) {
      k = this.mSelectionRightPadding;
    }
    localRect2.right = k;
    Rect localRect1 = this.mSpinnerPadding;
    if (j <= this.mSelectionBottomPadding) {
      j = this.mSelectionBottomPadding;
    }
    localRect1.bottom = j;
    if (this.mDataChanged) {
      handleDataChanged();
    }
    int m = 0;
    j = 0;
    int i1 = 1;
    int i2 = getSelectedItemPosition();
    if ((i2 >= 0) && (this.mAdapter != null) && (i2 < this.mAdapter.getCount()))
    {
      View localView = this.mRecycler.get(i2);
      if (localView == null) {
        localView = this.mAdapter.getView(i2, null, this);
      }
      if (localView != null)
      {
        this.mRecycler.put(i2, localView);
        if (localView.getLayoutParams() == null)
        {
          this.mBlockLayoutRequests = true;
          localView.setLayoutParams(generateDefaultLayoutParams());
          this.mBlockLayoutRequests = false;
        }
        measureChild(localView, paramInt1, paramInt2);
        m = getChildHeight(localView) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
        j = getChildWidth(localView) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
        i1 = 0;
      }
    }
    if (i1 != 0)
    {
      m = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
      if (i == 0) {
        j = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
      }
    }
    m = Math.max(m, getSuggestedMinimumHeight());
    i = Math.max(j, getSuggestedMinimumWidth());
    j = ViewCompat.resolveSizeAndState(m, paramInt2, 0);
    setMeasuredDimension(ViewCompat.resolveSizeAndState(i, paramInt1, 0), j);
    this.mHeightMeasureSpec = paramInt2;
    this.mWidthMeasureSpec = paramInt1;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (localSavedState.selectedId >= 0L)
    {
      this.mDataChanged = true;
      this.mNeedSync = true;
      this.mSyncRowId = localSavedState.selectedId;
      this.mSyncPosition = localSavedState.position;
      this.mSyncMode = 0;
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.selectedId = getSelectedItemId();
    if (localSavedState.selectedId < 0L) {
      localSavedState.position = -1;
    } else {
      localSavedState.position = getSelectedItemPosition();
    }
    return localSavedState;
  }
  
  public int pointToPosition(int paramInt1, int paramInt2)
  {
    Rect localRect = this.mTouchFrame;
    if (localRect == null)
    {
      this.mTouchFrame = new Rect();
      localRect = this.mTouchFrame;
    }
    for (int j = -1 + getChildCount();; j--)
    {
      if (j < 0) {
        return -1;
      }
      View localView = getChildAt(j);
      if (localView.getVisibility() == 0)
      {
        localView.getHitRect(i);
        if (i.contains(paramInt1, paramInt2)) {
          break;
        }
      }
    }
    int i = j + this.mFirstPosition;
    return i;
  }
  
  void recycleAllViews()
  {
    int j = getChildCount();
    RecycleBin localRecycleBin = this.mRecycler;
    int i = this.mFirstPosition;
    for (int k = 0;; k++)
    {
      if (k >= j) {
        return;
      }
      View localView = getChildAt(k);
      localRecycleBin.put(i + k, localView);
    }
  }
  
  public void requestLayout()
  {
    if (!this.mBlockLayoutRequests) {
      super.requestLayout();
    }
  }
  
  void resetList()
  {
    this.mDataChanged = false;
    this.mNeedSync = false;
    removeAllViewsInLayout();
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    setSelectedPositionInt(-1);
    setNextSelectedPositionInt(-1);
    invalidate();
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    int i = -1;
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
      resetList();
    }
    this.mAdapter = paramSpinnerAdapter;
    this.mOldSelectedPosition = i;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    if (this.mAdapter == null)
    {
      checkFocus();
      resetList();
      checkSelectionChanged();
    }
    else
    {
      this.mOldItemCount = this.mItemCount;
      this.mItemCount = this.mAdapter.getCount();
      checkFocus();
      this.mDataSetObserver = new AdapterViewCompat.AdapterDataSetObserver(this);
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      if (this.mItemCount > 0) {
        i = 0;
      }
      setSelectedPositionInt(i);
      setNextSelectedPositionInt(i);
      if (this.mItemCount == 0) {
        checkSelectionChanged();
      }
    }
    requestLayout();
  }
  
  public void setSelection(int paramInt)
  {
    setNextSelectedPositionInt(paramInt);
    requestLayout();
    invalidate();
  }
  
  public void setSelection(int paramInt, boolean paramBoolean)
  {
    boolean bool;
    if ((!paramBoolean) || (this.mFirstPosition > paramInt) || (paramInt > -1 + (this.mFirstPosition + getChildCount()))) {
      bool = false;
    } else {
      bool = true;
    }
    setSelectionInt(paramInt, bool);
  }
  
  void setSelectionInt(int paramInt, boolean paramBoolean)
  {
    if (paramInt != this.mOldSelectedPosition)
    {
      this.mBlockLayoutRequests = true;
      int i = paramInt - this.mSelectedPosition;
      setNextSelectedPositionInt(paramInt);
      layout(i, paramBoolean);
      this.mBlockLayoutRequests = false;
    }
  }
  
  class RecycleBin
  {
    private final SparseArray<View> mScrapHeap = new SparseArray();
    
    RecycleBin() {}
    
    void clear()
    {
      SparseArray localSparseArray = this.mScrapHeap;
      int j = localSparseArray.size();
      for (int i = 0;; i++)
      {
        if (i >= j)
        {
          localSparseArray.clear();
          return;
        }
        View localView = (View)localSparseArray.valueAt(i);
        if (localView != null) {
          AbsSpinnerCompat.this.removeDetachedView(localView, true);
        }
      }
    }
    
    View get(int paramInt)
    {
      View localView = (View)this.mScrapHeap.get(paramInt);
      if (localView != null) {
        this.mScrapHeap.delete(paramInt);
      }
      return localView;
    }
    
    public void put(int paramInt, View paramView)
    {
      this.mScrapHeap.put(paramInt, paramView);
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AbsSpinnerCompat.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AbsSpinnerCompat.SavedState(paramAnonymousParcel);
      }
      
      public AbsSpinnerCompat.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AbsSpinnerCompat.SavedState[paramAnonymousInt];
      }
    };
    int position;
    long selectedId;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.selectedId = paramParcel.readLong();
      this.position = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " position=" + this.position + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.selectedId);
      paramParcel.writeInt(this.position);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\AbsSpinnerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */