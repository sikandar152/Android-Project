package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.support.v7.internal.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import java.lang.reflect.Method;

public class ListPopupWindow
{
  private static final boolean DEBUG = false;
  private static final int EXPAND_LIST_TIMEOUT = 250;
  public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
  public static final int INPUT_METHOD_NEEDED = 1;
  public static final int INPUT_METHOD_NOT_NEEDED = 2;
  public static final int MATCH_PARENT = -1;
  public static final int POSITION_PROMPT_ABOVE = 0;
  public static final int POSITION_PROMPT_BELOW = 1;
  private static final String TAG = "ListPopupWindow";
  public static final int WRAP_CONTENT = -2;
  private static Method sClipToWindowEnabledMethod;
  private ListAdapter mAdapter;
  private Context mContext;
  private boolean mDropDownAlwaysVisible = false;
  private View mDropDownAnchorView;
  private int mDropDownGravity = 0;
  private int mDropDownHeight = -2;
  private int mDropDownHorizontalOffset;
  private DropDownListView mDropDownList;
  private Drawable mDropDownListHighlight;
  private int mDropDownVerticalOffset;
  private boolean mDropDownVerticalOffsetSet;
  private int mDropDownWidth = -2;
  private boolean mForceIgnoreOutsideTouch = false;
  private Handler mHandler = new Handler();
  private final ListSelectorHider mHideSelector = new ListSelectorHider(null);
  private AdapterView.OnItemClickListener mItemClickListener;
  private AdapterView.OnItemSelectedListener mItemSelectedListener;
  private int mLayoutDirection;
  int mListItemExpandMaximum = Integer.MAX_VALUE;
  private boolean mModal;
  private DataSetObserver mObserver;
  private PopupWindow mPopup;
  private int mPromptPosition = 0;
  private View mPromptView;
  private final ResizePopupRunnable mResizePopupRunnable = new ResizePopupRunnable(null);
  private final PopupScrollListener mScrollListener = new PopupScrollListener(null);
  private Runnable mShowDropDownRunnable;
  private Rect mTempRect = new Rect();
  private final PopupTouchInterceptor mTouchInterceptor = new PopupTouchInterceptor(null);
  
  static
  {
    try
    {
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = Boolean.TYPE;
      sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", arrayOfClass);
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
      }
    }
  }
  
  public ListPopupWindow(Context paramContext)
  {
    this(paramContext, null, R.attr.listPopupWindowStyle);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.listPopupWindowStyle);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPopupWindow, paramInt1, paramInt2);
    this.mDropDownHorizontalOffset = localTypedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
    this.mDropDownVerticalOffset = localTypedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
    if (this.mDropDownVerticalOffset != 0) {
      this.mDropDownVerticalOffsetSet = true;
    }
    localTypedArray.recycle();
    this.mPopup = new AppCompatPopupWindow(paramContext, paramAttributeSet, paramInt1);
    this.mPopup.setInputMethodMode(1);
    this.mLayoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(this.mContext.getResources().getConfiguration().locale);
  }
  
  private int buildDropDown()
  {
    int i = 0;
    int k;
    if (this.mDropDownList != null)
    {
      ((ViewGroup)this.mPopup.getContentView());
      View localView1 = this.mPromptView;
      if (localView1 != null)
      {
        LinearLayout.LayoutParams localLayoutParams1 = (LinearLayout.LayoutParams)localView1.getLayoutParams();
        int j = localView1.getMeasuredHeight() + localLayoutParams1.topMargin + localLayoutParams1.bottomMargin;
      }
    }
    else
    {
      Object localObject2 = this.mContext;
      this.mShowDropDownRunnable = new Runnable()
      {
        public void run()
        {
          View localView = ListPopupWindow.this.getAnchorView();
          if ((localView != null) && (localView.getWindowToken() != null)) {
            ListPopupWindow.this.show();
          }
        }
      };
      boolean bool;
      if (this.mModal) {
        bool = false;
      } else {
        bool = true;
      }
      this.mDropDownList = new DropDownListView((Context)localObject2, bool);
      if (this.mDropDownListHighlight != null) {
        this.mDropDownList.setSelector(this.mDropDownListHighlight);
      }
      this.mDropDownList.setAdapter(this.mAdapter);
      this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
      this.mDropDownList.setFocusable(true);
      this.mDropDownList.setFocusableInTouchMode(true);
      this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
        public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (paramAnonymousInt != -1)
          {
            ListPopupWindow.DropDownListView localDropDownListView = ListPopupWindow.this.mDropDownList;
            if (localDropDownListView != null) {
              ListPopupWindow.DropDownListView.access$502(localDropDownListView, false);
            }
          }
        }
        
        public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
      });
      this.mDropDownList.setOnScrollListener(this.mScrollListener);
      if (this.mItemSelectedListener != null) {
        this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener);
      }
      localObject1 = this.mDropDownList;
      View localView2 = this.mPromptView;
      if (localView2 != null)
      {
        localObject2 = new LinearLayout((Context)localObject2);
        ((LinearLayout)localObject2).setOrientation(1);
        LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(-1, 0, 1.0F);
        switch (this.mPromptPosition)
        {
        default: 
          Log.e("ListPopupWindow", "Invalid hint position " + this.mPromptPosition);
          break;
        case 0: 
          ((LinearLayout)localObject2).addView(localView2);
          ((LinearLayout)localObject2).addView((View)localObject1, localLayoutParams2);
          break;
        case 1: 
          ((LinearLayout)localObject2).addView((View)localObject1, localLayoutParams2);
          ((LinearLayout)localObject2).addView(localView2);
        }
        localView2.measure(View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, Integer.MIN_VALUE), 0);
        localLayoutParams2 = (LinearLayout.LayoutParams)localView2.getLayoutParams();
        k = localView2.getMeasuredHeight() + localLayoutParams2.topMargin + localLayoutParams2.bottomMargin;
        localObject1 = localObject2;
      }
      this.mPopup.setContentView((View)localObject1);
    }
    int m = 0;
    Object localObject1 = this.mPopup.getBackground();
    if (localObject1 == null)
    {
      this.mTempRect.setEmpty();
    }
    else
    {
      ((Drawable)localObject1).getPadding(this.mTempRect);
      m = this.mTempRect.top + this.mTempRect.bottom;
      if (!this.mDropDownVerticalOffsetSet) {
        this.mDropDownVerticalOffset = (-this.mTempRect.top);
      }
    }
    if (this.mPopup.getInputMethodMode() != 2) {}
    int n = this.mPopup.getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset);
    if ((!this.mDropDownAlwaysVisible) && (this.mDropDownHeight != -1))
    {
      int i1;
      switch (this.mDropDownWidth)
      {
      default: 
        i1 = View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
        break;
      case -2: 
        i1 = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
        break;
      case -1: 
        i1 = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
      }
      n = this.mDropDownList.measureHeightOfChildrenCompat(i1, 0, -1, n - k, -1);
      if (n > 0) {
        k += m;
      }
      k = n + k;
    }
    else
    {
      k = n + m;
    }
    return k;
  }
  
  private static boolean isConfirmKey(int paramInt)
  {
    boolean bool;
    if ((paramInt != 66) && (paramInt != 23)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void removePromptView()
  {
    if (this.mPromptView != null)
    {
      ViewParent localViewParent = this.mPromptView.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(this.mPromptView);
      }
    }
  }
  
  private void setPopupClipToScreenEnabled(boolean paramBoolean)
  {
    if (sClipToWindowEnabledMethod != null) {}
    try
    {
      Method localMethod = sClipToWindowEnabledMethod;
      PopupWindow localPopupWindow = this.mPopup;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Boolean.valueOf(paramBoolean);
      localMethod.invoke(localPopupWindow, arrayOfObject);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
      }
    }
  }
  
  public void clearListSelection()
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if (localDropDownListView != null)
    {
      DropDownListView.access$502(localDropDownListView, true);
      localDropDownListView.requestLayout();
    }
  }
  
  public View.OnTouchListener createDragToOpenListener(View paramView)
  {
    new ForwardingListener(paramView)
    {
      public ListPopupWindow getPopup()
      {
        return ListPopupWindow.this;
      }
    };
  }
  
  public void dismiss()
  {
    this.mPopup.dismiss();
    removePromptView();
    this.mPopup.setContentView(null);
    this.mDropDownList = null;
    this.mHandler.removeCallbacks(this.mResizePopupRunnable);
  }
  
  public View getAnchorView()
  {
    return this.mDropDownAnchorView;
  }
  
  public int getAnimationStyle()
  {
    return this.mPopup.getAnimationStyle();
  }
  
  public Drawable getBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public int getHeight()
  {
    return this.mDropDownHeight;
  }
  
  public int getHorizontalOffset()
  {
    return this.mDropDownHorizontalOffset;
  }
  
  public int getInputMethodMode()
  {
    return this.mPopup.getInputMethodMode();
  }
  
  public ListView getListView()
  {
    return this.mDropDownList;
  }
  
  public int getPromptPosition()
  {
    return this.mPromptPosition;
  }
  
  public Object getSelectedItem()
  {
    Object localObject;
    if (isShowing()) {
      localObject = this.mDropDownList.getSelectedItem();
    } else {
      localObject = null;
    }
    return localObject;
  }
  
  public long getSelectedItemId()
  {
    long l;
    if (isShowing()) {
      l = this.mDropDownList.getSelectedItemId();
    } else {
      l = Long.MIN_VALUE;
    }
    return l;
  }
  
  public int getSelectedItemPosition()
  {
    int i;
    if (isShowing()) {
      i = this.mDropDownList.getSelectedItemPosition();
    } else {
      i = -1;
    }
    return i;
  }
  
  public View getSelectedView()
  {
    View localView;
    if (isShowing()) {
      localView = this.mDropDownList.getSelectedView();
    } else {
      localView = null;
    }
    return localView;
  }
  
  public int getSoftInputMode()
  {
    return this.mPopup.getSoftInputMode();
  }
  
  public int getVerticalOffset()
  {
    int i;
    if (this.mDropDownVerticalOffsetSet) {
      i = this.mDropDownVerticalOffset;
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getWidth()
  {
    return this.mDropDownWidth;
  }
  
  public boolean isDropDownAlwaysVisible()
  {
    return this.mDropDownAlwaysVisible;
  }
  
  public boolean isInputMethodNotNeeded()
  {
    boolean bool;
    if (this.mPopup.getInputMethodMode() != 2) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isModal()
  {
    return this.mModal;
  }
  
  public boolean isShowing()
  {
    return this.mPopup.isShowing();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    int i = 1;
    if ((isShowing()) && (paramInt != 62) && ((this.mDropDownList.getSelectedItemPosition() >= 0) || (!isConfirmKey(paramInt))))
    {
      int m = this.mDropDownList.getSelectedItemPosition();
      int k;
      if (this.mPopup.isAboveAnchor()) {
        k = 0;
      } else {
        k = i;
      }
      ListAdapter localListAdapter = this.mAdapter;
      int j = Integer.MAX_VALUE;
      int n = Integer.MIN_VALUE;
      int i1;
      if (localListAdapter != null)
      {
        boolean bool = localListAdapter.areAllItemsEnabled();
        if (!bool) {
          j = this.mDropDownList.lookForSelectablePosition(0, i);
        } else {
          j = 0;
        }
        if (!bool) {
          i1 = this.mDropDownList.lookForSelectablePosition(-1 + localListAdapter.getCount(), false);
        } else {
          i1 = -1 + localListAdapter.getCount();
        }
      }
      if (((k != 0) && (paramInt == 19) && (m <= j)) || ((k == 0) && (paramInt == 20) && (m >= i1))) {
        break label317;
      }
      DropDownListView.access$502(this.mDropDownList, false);
      if (!this.mDropDownList.onKeyDown(paramInt, paramKeyEvent))
      {
        if ((k == 0) || (paramInt != 20))
        {
          if ((k == 0) && (paramInt == 19) && (m == j)) {
            return i;
          }
        }
        else if (m == i1) {
          return i;
        }
      }
      else
      {
        this.mPopup.setInputMethodMode(2);
        this.mDropDownList.requestFocusFromTouch();
        show();
      }
    }
    switch (paramInt)
    {
    default: 
      i = 0;
      break;
      label317:
      clearListSelection();
      this.mPopup.setInputMethodMode(i);
      show();
    }
    return i;
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    int i = 1;
    if ((paramInt == 4) && (isShowing()))
    {
      localObject = this.mDropDownAnchorView;
      if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0)) {
        break label86;
      }
      if (paramKeyEvent.getAction() == i)
      {
        localObject = ((View)localObject).getKeyDispatcherState();
        if (localObject != null) {
          ((KeyEvent.DispatcherState)localObject).handleUpEvent(paramKeyEvent);
        }
        if ((paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled())) {
          break label79;
        }
      }
    }
    return 0;
    label79:
    dismiss();
    return i;
    label86:
    Object localObject = ((View)localObject).getKeyDispatcherState();
    if (localObject != null) {
      ((KeyEvent.DispatcherState)localObject).startTracking(paramKeyEvent, this);
    }
    return i;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((!isShowing()) || (this.mDropDownList.getSelectedItemPosition() < 0))
    {
      bool = false;
    }
    else
    {
      bool = this.mDropDownList.onKeyUp(paramInt, paramKeyEvent);
      if ((bool) && (isConfirmKey(paramInt))) {
        dismiss();
      }
    }
    return bool;
  }
  
  public boolean performItemClick(int paramInt)
  {
    boolean bool;
    if (!isShowing())
    {
      int i = 0;
    }
    else
    {
      if (this.mItemClickListener != null)
      {
        DropDownListView localDropDownListView = this.mDropDownList;
        View localView = localDropDownListView.getChildAt(paramInt - localDropDownListView.getFirstVisiblePosition());
        ListAdapter localListAdapter = localDropDownListView.getAdapter();
        this.mItemClickListener.onItemClick(localDropDownListView, localView, paramInt, localListAdapter.getItemId(paramInt));
      }
      bool = true;
    }
    return bool;
  }
  
  public void postShow()
  {
    this.mHandler.post(this.mShowDropDownRunnable);
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if (this.mObserver != null)
    {
      if (this.mAdapter != null) {
        this.mAdapter.unregisterDataSetObserver(this.mObserver);
      }
    }
    else {
      this.mObserver = new PopupDataSetObserver(null);
    }
    this.mAdapter = paramListAdapter;
    if (this.mAdapter != null) {
      paramListAdapter.registerDataSetObserver(this.mObserver);
    }
    if (this.mDropDownList != null) {
      this.mDropDownList.setAdapter(this.mAdapter);
    }
  }
  
  public void setAnchorView(View paramView)
  {
    this.mDropDownAnchorView = paramView;
  }
  
  public void setAnimationStyle(int paramInt)
  {
    this.mPopup.setAnimationStyle(paramInt);
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mPopup.setBackgroundDrawable(paramDrawable);
  }
  
  public void setContentWidth(int paramInt)
  {
    Drawable localDrawable = this.mPopup.getBackground();
    if (localDrawable == null)
    {
      setWidth(paramInt);
    }
    else
    {
      localDrawable.getPadding(this.mTempRect);
      this.mDropDownWidth = (paramInt + (this.mTempRect.left + this.mTempRect.right));
    }
  }
  
  public void setDropDownAlwaysVisible(boolean paramBoolean)
  {
    this.mDropDownAlwaysVisible = paramBoolean;
  }
  
  public void setDropDownGravity(int paramInt)
  {
    this.mDropDownGravity = paramInt;
  }
  
  public void setForceIgnoreOutsideTouch(boolean paramBoolean)
  {
    this.mForceIgnoreOutsideTouch = paramBoolean;
  }
  
  public void setHeight(int paramInt)
  {
    this.mDropDownHeight = paramInt;
  }
  
  public void setHorizontalOffset(int paramInt)
  {
    this.mDropDownHorizontalOffset = paramInt;
  }
  
  public void setInputMethodMode(int paramInt)
  {
    this.mPopup.setInputMethodMode(paramInt);
  }
  
  void setListItemExpandMax(int paramInt)
  {
    this.mListItemExpandMaximum = paramInt;
  }
  
  public void setListSelector(Drawable paramDrawable)
  {
    this.mDropDownListHighlight = paramDrawable;
  }
  
  public void setModal(boolean paramBoolean)
  {
    this.mModal = paramBoolean;
    this.mPopup.setFocusable(paramBoolean);
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
  {
    this.mPopup.setOnDismissListener(paramOnDismissListener);
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.mItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mItemSelectedListener = paramOnItemSelectedListener;
  }
  
  public void setPromptPosition(int paramInt)
  {
    this.mPromptPosition = paramInt;
  }
  
  public void setPromptView(View paramView)
  {
    boolean bool = isShowing();
    if (bool) {
      removePromptView();
    }
    this.mPromptView = paramView;
    if (bool) {
      show();
    }
  }
  
  public void setSelection(int paramInt)
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if ((isShowing()) && (localDropDownListView != null))
    {
      DropDownListView.access$502(localDropDownListView, false);
      localDropDownListView.setSelection(paramInt);
      if ((Build.VERSION.SDK_INT >= 11) && (localDropDownListView.getChoiceMode() != 0)) {
        localDropDownListView.setItemChecked(paramInt, true);
      }
    }
  }
  
  public void setSoftInputMode(int paramInt)
  {
    this.mPopup.setSoftInputMode(paramInt);
  }
  
  public void setVerticalOffset(int paramInt)
  {
    this.mDropDownVerticalOffset = paramInt;
    this.mDropDownVerticalOffsetSet = true;
  }
  
  public void setWidth(int paramInt)
  {
    this.mDropDownWidth = paramInt;
  }
  
  public void show()
  {
    PopupWindow localPopupWindow1 = 1;
    int j = 0;
    int i = -1;
    int m = buildDropDown();
    int k = 0;
    int n = 0;
    boolean bool = isInputMethodNotNeeded();
    PopupWindow localPopupWindow3;
    if (!this.mPopup.isShowing())
    {
      if (this.mDropDownWidth != i)
      {
        if (this.mDropDownWidth != -2) {
          this.mPopup.setWidth(this.mDropDownWidth);
        } else {
          this.mPopup.setWidth(getAnchorView().getWidth());
        }
      }
      else {
        k = -1;
      }
      if (this.mDropDownHeight != i)
      {
        if (this.mDropDownHeight != -2) {
          this.mPopup.setHeight(this.mDropDownHeight);
        } else {
          this.mPopup.setHeight(m);
        }
      }
      else {
        n = -1;
      }
      this.mPopup.setWindowLayoutMode(k, n);
      setPopupClipToScreenEnabled(localPopupWindow1);
      localPopupWindow3 = this.mPopup;
      if ((this.mForceIgnoreOutsideTouch) || (this.mDropDownAlwaysVisible)) {
        localPopupWindow1 = 0;
      }
      localPopupWindow3.setOutsideTouchable(localPopupWindow1);
      this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
      PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
      this.mDropDownList.setSelection(i);
      if ((!this.mModal) || (this.mDropDownList.isInTouchMode())) {
        clearListSelection();
      }
      if (!this.mModal) {
        this.mHandler.post(this.mHideSelector);
      }
    }
    else
    {
      if (this.mDropDownWidth != i)
      {
        if (this.mDropDownWidth != -2) {
          k = this.mDropDownWidth;
        } else {
          k = getAnchorView().getWidth();
        }
      }
      else {
        k = -1;
      }
      if (this.mDropDownHeight != i)
      {
        if (this.mDropDownHeight != -2) {
          m = this.mDropDownHeight;
        } else {
          m = m;
        }
      }
      else
      {
        if (!bool) {
          m = i;
        } else {
          m = m;
        }
        PopupWindow localPopupWindow4;
        if (!bool)
        {
          localPopupWindow4 = this.mPopup;
          if (this.mDropDownWidth != i) {
            n = 0;
          } else {
            n = i;
          }
          localPopupWindow4.setWindowLayoutMode(n, i);
        }
        else
        {
          localPopupWindow4 = this.mPopup;
          if (this.mDropDownWidth != i) {
            i = 0;
          }
          localPopupWindow4.setWindowLayoutMode(i, 0);
        }
      }
      PopupWindow localPopupWindow2 = this.mPopup;
      if ((!this.mForceIgnoreOutsideTouch) && (!this.mDropDownAlwaysVisible)) {
        localPopupWindow3 = localPopupWindow1;
      }
      localPopupWindow2.setOutsideTouchable(localPopupWindow3);
      this.mPopup.update(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, k, m);
    }
  }
  
  private class PopupScrollListener
    implements AbsListView.OnScrollListener
  {
    private PopupScrollListener() {}
    
    public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt)
    {
      if ((paramInt == 1) && (!ListPopupWindow.this.isInputMethodNotNeeded()) && (ListPopupWindow.this.mPopup.getContentView() != null))
      {
        ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
        ListPopupWindow.this.mResizePopupRunnable.run();
      }
    }
  }
  
  private class PopupTouchInterceptor
    implements View.OnTouchListener
  {
    private PopupTouchInterceptor() {}
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      int k = paramMotionEvent.getAction();
      int j = (int)paramMotionEvent.getX();
      int i = (int)paramMotionEvent.getY();
      if ((k != 0) || (ListPopupWindow.this.mPopup == null) || (!ListPopupWindow.this.mPopup.isShowing()) || (j < 0) || (j >= ListPopupWindow.this.mPopup.getWidth()) || (i < 0) || (i >= ListPopupWindow.this.mPopup.getHeight()))
      {
        if (k == 1) {
          ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
        }
      }
      else {
        ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
      }
      return false;
    }
  }
  
  private class ResizePopupRunnable
    implements Runnable
  {
    private ResizePopupRunnable() {}
    
    public void run()
    {
      if ((ListPopupWindow.this.mDropDownList != null) && (ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount()) && (ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum))
      {
        ListPopupWindow.this.mPopup.setInputMethodMode(2);
        ListPopupWindow.this.show();
      }
    }
  }
  
  private class ListSelectorHider
    implements Runnable
  {
    private ListSelectorHider() {}
    
    public void run()
    {
      ListPopupWindow.this.clearListSelection();
    }
  }
  
  private class PopupDataSetObserver
    extends DataSetObserver
  {
    private PopupDataSetObserver() {}
    
    public void onChanged()
    {
      if (ListPopupWindow.this.isShowing()) {
        ListPopupWindow.this.show();
      }
    }
    
    public void onInvalidated()
    {
      ListPopupWindow.this.dismiss();
    }
  }
  
  private static class DropDownListView
    extends ListViewCompat
  {
    private ViewPropertyAnimatorCompat mClickAnimation;
    private boolean mDrawsInPressedState;
    private boolean mHijackFocus;
    private boolean mListSelectionHidden;
    private ListViewAutoScrollHelper mScrollHelper;
    
    public DropDownListView(Context paramContext, boolean paramBoolean)
    {
      super(null, R.attr.dropDownListViewStyle);
      this.mHijackFocus = paramBoolean;
      setCacheColorHint(0);
    }
    
    private void clearPressedItem()
    {
      this.mDrawsInPressedState = false;
      setPressed(false);
      drawableStateChanged();
      if (this.mClickAnimation != null)
      {
        this.mClickAnimation.cancel();
        this.mClickAnimation = null;
      }
    }
    
    private void clickPressedItem(View paramView, int paramInt)
    {
      performItemClick(paramView, paramInt, getItemIdAtPosition(paramInt));
    }
    
    private void setPressedItem(View paramView, int paramInt, float paramFloat1, float paramFloat2)
    {
      this.mDrawsInPressedState = true;
      setPressed(true);
      layoutChildren();
      setSelection(paramInt);
      positionSelectorLikeTouchCompat(paramInt, paramView, paramFloat1, paramFloat2);
      setSelectorEnabled(false);
      refreshDrawableState();
    }
    
    public boolean hasFocus()
    {
      boolean bool;
      if ((!this.mHijackFocus) && (!super.hasFocus())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean hasWindowFocus()
    {
      boolean bool;
      if ((!this.mHijackFocus) && (!super.hasWindowFocus())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean isFocused()
    {
      boolean bool;
      if ((!this.mHijackFocus) && (!super.isFocused())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean isInTouchMode()
    {
      boolean bool;
      if (((!this.mHijackFocus) || (!this.mListSelectionHidden)) && (!super.isInTouchMode())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean onForwardedEvent(MotionEvent paramMotionEvent, int paramInt)
    {
      boolean bool = true;
      int i = 0;
      int j = MotionEventCompat.getActionMasked(paramMotionEvent);
      switch (j)
      {
      case 1: 
        bool = false;
      case 2: 
        int m = paramMotionEvent.findPointerIndex(paramInt);
        if (m >= 0)
        {
          int k = (int)paramMotionEvent.getX(m);
          int n = (int)paramMotionEvent.getY(m);
          m = pointToPosition(k, n);
          if (m != -1)
          {
            View localView = getChildAt(m - getFirstVisiblePosition());
            setPressedItem(localView, m, k, n);
            bool = true;
            if (j == 1) {
              clickPressedItem(localView, m);
            }
          }
          else
          {
            i = 1;
          }
        }
        else
        {
          bool = false;
        }
        break;
      case 3: 
        bool = false;
      }
      if ((!bool) || (i != 0)) {
        clearPressedItem();
      }
      if (!bool)
      {
        if (this.mScrollHelper != null) {
          this.mScrollHelper.setEnabled(false);
        }
      }
      else
      {
        if (this.mScrollHelper == null) {
          this.mScrollHelper = new ListViewAutoScrollHelper(this);
        }
        this.mScrollHelper.setEnabled(true);
        this.mScrollHelper.onTouch(this, paramMotionEvent);
      }
      return bool;
    }
    
    protected boolean touchModeDrawsInPressedStateCompat()
    {
      boolean bool;
      if ((!this.mDrawsInPressedState) && (!super.touchModeDrawsInPressedStateCompat())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
  }
  
  public static abstract class ForwardingListener
    implements View.OnTouchListener
  {
    private int mActivePointerId;
    private Runnable mDisallowIntercept;
    private boolean mForwarding;
    private final int mLongPressTimeout;
    private final float mScaledTouchSlop;
    private final View mSrc;
    private final int mTapTimeout;
    private final int[] mTmpLocation = new int[2];
    private Runnable mTriggerLongPress;
    private boolean mWasLongPress;
    
    public ForwardingListener(View paramView)
    {
      this.mSrc = paramView;
      this.mScaledTouchSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
      this.mTapTimeout = ViewConfiguration.getTapTimeout();
      this.mLongPressTimeout = ((this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2);
    }
    
    private void clearCallbacks()
    {
      if (this.mTriggerLongPress != null) {
        this.mSrc.removeCallbacks(this.mTriggerLongPress);
      }
      if (this.mDisallowIntercept != null) {
        this.mSrc.removeCallbacks(this.mDisallowIntercept);
      }
    }
    
    private void onLongPress()
    {
      clearCallbacks();
      if ((this.mSrc.isEnabled()) && (onForwardingStarted()))
      {
        this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
        long l = SystemClock.uptimeMillis();
        MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        this.mSrc.onTouchEvent(localMotionEvent);
        localMotionEvent.recycle();
        this.mForwarding = true;
        this.mWasLongPress = true;
      }
    }
    
    private boolean onTouchForwarded(MotionEvent paramMotionEvent)
    {
      int i = 1;
      int j = 0;
      View localView = this.mSrc;
      Object localObject = getPopup();
      int k;
      if ((localObject != null) && (((ListPopupWindow)localObject).isShowing()))
      {
        localObject = ((ListPopupWindow)localObject).mDropDownList;
        if ((localObject != null) && (((ListPopupWindow.DropDownListView)localObject).isShown()))
        {
          MotionEvent localMotionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
          toGlobalMotionEvent(localView, localMotionEvent);
          toLocalMotionEvent((View)localObject, localMotionEvent);
          boolean bool = ((ListPopupWindow.DropDownListView)localObject).onForwardedEvent(localMotionEvent, this.mActivePointerId);
          localMotionEvent.recycle();
          k = MotionEventCompat.getActionMasked(paramMotionEvent);
          if ((k == i) || (k == 3)) {
            k = 0;
          } else {
            k = i;
          }
          if ((!bool) || (k == 0)) {
            i = 0;
          }
          k = i;
        }
      }
      return k;
    }
    
    private boolean onTouchObserved(MotionEvent paramMotionEvent)
    {
      boolean bool = false;
      View localView = this.mSrc;
      if (localView.isEnabled()) {
        switch (MotionEventCompat.getActionMasked(paramMotionEvent))
        {
        default: 
          break;
        case 0: 
          this.mActivePointerId = paramMotionEvent.getPointerId(0);
          this.mWasLongPress = false;
          if (this.mDisallowIntercept == null) {
            this.mDisallowIntercept = new DisallowIntercept(null);
          }
          localView.postDelayed(this.mDisallowIntercept, this.mTapTimeout);
          if (this.mTriggerLongPress == null) {
            this.mTriggerLongPress = new TriggerLongPress(null);
          }
          localView.postDelayed(this.mTriggerLongPress, this.mLongPressTimeout);
          break;
        case 1: 
        case 3: 
          clearCallbacks();
          break;
        case 2: 
          int i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
          if ((i >= 0) && (!pointInView(localView, paramMotionEvent.getX(i), paramMotionEvent.getY(i), this.mScaledTouchSlop)))
          {
            clearCallbacks();
            localView.getParent().requestDisallowInterceptTouchEvent(true);
            bool = true;
          }
          break;
        }
      }
      return bool;
    }
    
    private static boolean pointInView(View paramView, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      boolean bool;
      if ((paramFloat1 < -paramFloat3) || (paramFloat2 < -paramFloat3) || (paramFloat1 >= paramFloat3 + (paramView.getRight() - paramView.getLeft())) || (paramFloat2 >= paramFloat3 + (paramView.getBottom() - paramView.getTop()))) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    private boolean toGlobalMotionEvent(View paramView, MotionEvent paramMotionEvent)
    {
      int[] arrayOfInt = this.mTmpLocation;
      paramView.getLocationOnScreen(arrayOfInt);
      paramMotionEvent.offsetLocation(arrayOfInt[0], arrayOfInt[1]);
      return true;
    }
    
    private boolean toLocalMotionEvent(View paramView, MotionEvent paramMotionEvent)
    {
      int[] arrayOfInt = this.mTmpLocation;
      paramView.getLocationOnScreen(arrayOfInt);
      paramMotionEvent.offsetLocation(-arrayOfInt[0], -arrayOfInt[1]);
      return true;
    }
    
    public abstract ListPopupWindow getPopup();
    
    protected boolean onForwardingStarted()
    {
      ListPopupWindow localListPopupWindow = getPopup();
      if ((localListPopupWindow != null) && (!localListPopupWindow.isShowing())) {
        localListPopupWindow.show();
      }
      return true;
    }
    
    protected boolean onForwardingStopped()
    {
      ListPopupWindow localListPopupWindow = getPopup();
      if ((localListPopupWindow != null) && (localListPopupWindow.isShowing())) {
        localListPopupWindow.dismiss();
      }
      return true;
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      boolean bool3 = false;
      boolean bool1 = this.mForwarding;
      boolean bool2;
      if (!bool1)
      {
        if ((!onTouchObserved(paramMotionEvent)) || (!onForwardingStarted())) {
          bool2 = false;
        } else {
          bool2 = true;
        }
        if (bool2)
        {
          long l = SystemClock.uptimeMillis();
          MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
          this.mSrc.onTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
        }
      }
      else if (!this.mWasLongPress)
      {
        if ((!onTouchForwarded(paramMotionEvent)) && (onForwardingStopped())) {
          bool2 = false;
        } else {
          bool2 = true;
        }
      }
      else
      {
        bool2 = onTouchForwarded(paramMotionEvent);
      }
      this.mForwarding = bool2;
      if ((bool2) || (bool1)) {
        bool3 = true;
      }
      return bool3;
    }
    
    private class TriggerLongPress
      implements Runnable
    {
      private TriggerLongPress() {}
      
      public void run()
      {
        ListPopupWindow.ForwardingListener.this.onLongPress();
      }
    }
    
    private class DisallowIntercept
      implements Runnable
    {
      private DisallowIntercept() {}
      
      public void run()
      {
        ListPopupWindow.ForwardingListener.this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\ListPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */