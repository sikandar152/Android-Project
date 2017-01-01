package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.appcompat.R.attr;
import android.support.v7.internal.view.ActionBarPolicy;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingTabContainerView
  extends HorizontalScrollView
  implements AdapterViewCompat.OnItemClickListener
{
  private static final int FADE_DURATION = 200;
  private static final String TAG = "ScrollingTabContainerView";
  private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
  private boolean mAllowCollapse;
  private int mContentHeight;
  int mMaxTabWidth;
  private int mSelectedTabIndex;
  int mStackedTabMaxWidth;
  private TabClickListener mTabClickListener;
  private LinearLayoutCompat mTabLayout;
  Runnable mTabSelector;
  private SpinnerCompat mTabSpinner;
  protected final VisibilityAnimListener mVisAnimListener = new VisibilityAnimListener();
  protected ViewPropertyAnimatorCompat mVisibilityAnim;
  
  public ScrollingTabContainerView(Context paramContext)
  {
    super(paramContext);
    setHorizontalScrollBarEnabled(false);
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(paramContext);
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    this.mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
    this.mTabLayout = createTabLayout();
    addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -1));
  }
  
  private SpinnerCompat createSpinner()
  {
    SpinnerCompat localSpinnerCompat = new SpinnerCompat(getContext(), null, R.attr.actionDropDownStyle);
    localSpinnerCompat.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    localSpinnerCompat.setOnItemClickListenerInt(this);
    return localSpinnerCompat;
  }
  
  private LinearLayoutCompat createTabLayout()
  {
    LinearLayoutCompat localLinearLayoutCompat = new LinearLayoutCompat(getContext(), null, R.attr.actionBarTabBarStyle);
    localLinearLayoutCompat.setMeasureWithLargestChildEnabled(true);
    localLinearLayoutCompat.setGravity(17);
    localLinearLayoutCompat.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    return localLinearLayoutCompat;
  }
  
  private TabView createTabView(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = new TabView(getContext(), paramTab, paramBoolean);
    if (!paramBoolean)
    {
      localTabView.setFocusable(true);
      if (this.mTabClickListener == null) {
        this.mTabClickListener = new TabClickListener(null);
      }
      localTabView.setOnClickListener(this.mTabClickListener);
    }
    else
    {
      localTabView.setBackgroundDrawable(null);
      localTabView.setLayoutParams(new AbsListView.LayoutParams(-1, this.mContentHeight));
    }
    return localTabView;
  }
  
  private boolean isCollapsed()
  {
    boolean bool;
    if ((this.mTabSpinner == null) || (this.mTabSpinner.getParent() != this)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void performCollapse()
  {
    if (!isCollapsed())
    {
      if (this.mTabSpinner == null) {
        this.mTabSpinner = createSpinner();
      }
      removeView(this.mTabLayout);
      addView(this.mTabSpinner, new ViewGroup.LayoutParams(-2, -1));
      if (this.mTabSpinner.getAdapter() == null) {
        this.mTabSpinner.setAdapter(new TabAdapter(null));
      }
      if (this.mTabSelector != null)
      {
        removeCallbacks(this.mTabSelector);
        this.mTabSelector = null;
      }
      this.mTabSpinner.setSelection(this.mSelectedTabIndex);
    }
  }
  
  private boolean performExpand()
  {
    if (isCollapsed())
    {
      removeView(this.mTabSpinner);
      addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -1));
      setTabSelected(this.mTabSpinner.getSelectedItemPosition());
    }
    return false;
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    this.mTabLayout.addView(localTabView, paramInt, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (this.mTabSpinner != null) {
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (paramBoolean) {
      localTabView.setSelected(true);
    }
    if (this.mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    this.mTabLayout.addView(localTabView, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (this.mTabSpinner != null) {
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (paramBoolean) {
      localTabView.setSelected(true);
    }
    if (this.mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void animateToTab(int paramInt)
  {
    final View localView = this.mTabLayout.getChildAt(paramInt);
    if (this.mTabSelector != null) {
      removeCallbacks(this.mTabSelector);
    }
    this.mTabSelector = new Runnable()
    {
      public void run()
      {
        int i = localView.getLeft() - (ScrollingTabContainerView.this.getWidth() - localView.getWidth()) / 2;
        ScrollingTabContainerView.this.smoothScrollTo(i, 0);
        ScrollingTabContainerView.this.mTabSelector = null;
      }
    };
    post(this.mTabSelector);
  }
  
  public void animateToVisibility(int paramInt)
  {
    if (this.mVisibilityAnim != null) {
      this.mVisibilityAnim.cancel();
    }
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat;
    if (paramInt != 0)
    {
      localViewPropertyAnimatorCompat = ViewCompat.animate(this).alpha(0.0F);
      localViewPropertyAnimatorCompat.setDuration(200L);
      localViewPropertyAnimatorCompat.setInterpolator(sAlphaInterpolator);
      localViewPropertyAnimatorCompat.setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat, paramInt));
      localViewPropertyAnimatorCompat.start();
    }
    else
    {
      if (getVisibility() != 0) {
        ViewCompat.setAlpha(this, 0.0F);
      }
      localViewPropertyAnimatorCompat = ViewCompat.animate(this).alpha(1.0F);
      localViewPropertyAnimatorCompat.setDuration(200L);
      localViewPropertyAnimatorCompat.setInterpolator(sAlphaInterpolator);
      localViewPropertyAnimatorCompat.setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat, paramInt));
      localViewPropertyAnimatorCompat.start();
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mTabSelector != null) {
      post(this.mTabSelector);
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (Build.VERSION.SDK_INT >= 8) {
      super.onConfigurationChanged(paramConfiguration);
    }
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(getContext());
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    this.mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mTabSelector != null) {
      removeCallbacks(this.mTabSelector);
    }
  }
  
  public void onItemClick(AdapterViewCompat<?> paramAdapterViewCompat, View paramView, int paramInt, long paramLong)
  {
    ((TabView)paramView).getTab().select();
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt1);
    boolean bool;
    if (j != 1073741824) {
      bool = false;
    } else {
      bool = true;
    }
    setFillViewport(bool);
    int i = this.mTabLayout.getChildCount();
    if ((i <= 1) || ((j != 1073741824) && (j != Integer.MIN_VALUE)))
    {
      this.mMaxTabWidth = -1;
    }
    else
    {
      if (i <= 2) {
        this.mMaxTabWidth = (View.MeasureSpec.getSize(paramInt1) / 2);
      } else {
        this.mMaxTabWidth = ((int)(0.4F * View.MeasureSpec.getSize(paramInt1)));
      }
      this.mMaxTabWidth = Math.min(this.mMaxTabWidth, this.mStackedTabMaxWidth);
    }
    j = View.MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824);
    if ((bool) || (!this.mAllowCollapse)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i == 0)
    {
      performExpand();
    }
    else
    {
      this.mTabLayout.measure(0, j);
      if (this.mTabLayout.getMeasuredWidth() <= View.MeasureSpec.getSize(paramInt1)) {
        performExpand();
      } else {
        performCollapse();
      }
    }
    i = getMeasuredWidth();
    super.onMeasure(paramInt1, j);
    j = getMeasuredWidth();
    if ((bool) && (i != j)) {
      setTabSelected(this.mSelectedTabIndex);
    }
  }
  
  public void removeAllTabs()
  {
    this.mTabLayout.removeAllViews();
    if (this.mTabSpinner != null) {
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (this.mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void removeTabAt(int paramInt)
  {
    this.mTabLayout.removeViewAt(paramInt);
    if (this.mTabSpinner != null) {
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (this.mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void setAllowCollapse(boolean paramBoolean)
  {
    this.mAllowCollapse = paramBoolean;
  }
  
  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = paramInt;
    requestLayout();
  }
  
  public void setTabSelected(int paramInt)
  {
    this.mSelectedTabIndex = paramInt;
    int i = this.mTabLayout.getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        if ((this.mTabSpinner != null) && (paramInt >= 0)) {
          this.mTabSpinner.setSelection(paramInt);
        }
        return;
      }
      View localView = this.mTabLayout.getChildAt(j);
      boolean bool;
      if (j != paramInt) {
        bool = false;
      } else {
        bool = true;
      }
      localView.setSelected(bool);
      if (bool) {
        animateToTab(paramInt);
      }
    }
  }
  
  public void updateTab(int paramInt)
  {
    ((TabView)this.mTabLayout.getChildAt(paramInt)).update();
    if (this.mTabSpinner != null) {
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (this.mAllowCollapse) {
      requestLayout();
    }
  }
  
  protected class VisibilityAnimListener
    implements ViewPropertyAnimatorListener
  {
    private boolean mCanceled = false;
    private int mFinalVisibility;
    
    protected VisibilityAnimListener() {}
    
    public void onAnimationCancel(View paramView)
    {
      this.mCanceled = true;
    }
    
    public void onAnimationEnd(View paramView)
    {
      if (!this.mCanceled)
      {
        ScrollingTabContainerView.this.mVisibilityAnim = null;
        ScrollingTabContainerView.this.setVisibility(this.mFinalVisibility);
      }
    }
    
    public void onAnimationStart(View paramView)
    {
      ScrollingTabContainerView.this.setVisibility(0);
      this.mCanceled = false;
    }
    
    public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, int paramInt)
    {
      this.mFinalVisibility = paramInt;
      ScrollingTabContainerView.this.mVisibilityAnim = paramViewPropertyAnimatorCompat;
      return this;
    }
  }
  
  private class TabClickListener
    implements View.OnClickListener
  {
    private TabClickListener() {}
    
    public void onClick(View paramView)
    {
      ((ScrollingTabContainerView.TabView)paramView).getTab().select();
      int j = ScrollingTabContainerView.this.mTabLayout.getChildCount();
      for (int i = 0;; i++)
      {
        if (i >= j) {
          return;
        }
        View localView = ScrollingTabContainerView.this.mTabLayout.getChildAt(i);
        boolean bool;
        if (localView != paramView) {
          bool = false;
        } else {
          bool = true;
        }
        localView.setSelected(bool);
      }
    }
  }
  
  private class TabAdapter
    extends BaseAdapter
  {
    private TabAdapter() {}
    
    public int getCount()
    {
      return ScrollingTabContainerView.this.mTabLayout.getChildCount();
    }
    
    public Object getItem(int paramInt)
    {
      return ((ScrollingTabContainerView.TabView)ScrollingTabContainerView.this.mTabLayout.getChildAt(paramInt)).getTab();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView != null) {
        ((ScrollingTabContainerView.TabView)paramView).bindTab((ActionBar.Tab)getItem(paramInt));
      } else {
        paramView = ScrollingTabContainerView.this.createTabView((ActionBar.Tab)getItem(paramInt), true);
      }
      return paramView;
    }
  }
  
  private class TabView
    extends LinearLayoutCompat
    implements View.OnLongClickListener
  {
    private final int[] BG_ATTRS;
    private View mCustomView;
    private ImageView mIconView;
    private ActionBar.Tab mTab;
    private TextView mTextView;
    
    public TabView(Context paramContext, ActionBar.Tab paramTab, boolean paramBoolean)
    {
      super(null, R.attr.actionBarTabStyle);
      Object localObject = new int[1];
      localObject[0] = 16842964;
      this.BG_ATTRS = ((int[])localObject);
      this.mTab = paramTab;
      localObject = TintTypedArray.obtainStyledAttributes(paramContext, null, this.BG_ATTRS, R.attr.actionBarTabStyle, 0);
      if (((TintTypedArray)localObject).hasValue(0)) {
        setBackgroundDrawable(((TintTypedArray)localObject).getDrawable(0));
      }
      ((TintTypedArray)localObject).recycle();
      if (paramBoolean) {
        setGravity(8388627);
      }
      update();
    }
    
    public void bindTab(ActionBar.Tab paramTab)
    {
      this.mTab = paramTab;
      update();
    }
    
    public ActionBar.Tab getTab()
    {
      return this.mTab;
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ActionBar.Tab.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      if (Build.VERSION.SDK_INT >= 14) {
        paramAccessibilityNodeInfo.setClassName(ActionBar.Tab.class.getName());
      }
    }
    
    public boolean onLongClick(View paramView)
    {
      int[] arrayOfInt = new int[2];
      getLocationOnScreen(arrayOfInt);
      Object localObject = getContext();
      int j = getWidth();
      int k = getHeight();
      int i = ((Context)localObject).getResources().getDisplayMetrics().widthPixels;
      localObject = Toast.makeText((Context)localObject, this.mTab.getContentDescription(), 0);
      ((Toast)localObject).setGravity(49, arrayOfInt[0] + j / 2 - i / 2, k);
      ((Toast)localObject).show();
      return true;
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if ((ScrollingTabContainerView.this.mMaxTabWidth > 0) && (getMeasuredWidth() > ScrollingTabContainerView.this.mMaxTabWidth)) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(ScrollingTabContainerView.this.mMaxTabWidth, 1073741824), paramInt2);
      }
    }
    
    public void setSelected(boolean paramBoolean)
    {
      int i;
      if (isSelected() == paramBoolean) {
        i = 0;
      } else {
        i = 1;
      }
      super.setSelected(paramBoolean);
      if ((i != 0) && (paramBoolean)) {
        sendAccessibilityEvent(4);
      }
    }
    
    public void update()
    {
      Object localObject1 = this.mTab;
      Object localObject2 = ((ActionBar.Tab)localObject1).getCustomView();
      if (localObject2 == null)
      {
        if (this.mCustomView != null)
        {
          removeView(this.mCustomView);
          this.mCustomView = null;
        }
        Object localObject3 = ((ActionBar.Tab)localObject1).getIcon();
        localObject2 = ((ActionBar.Tab)localObject1).getText();
        Object localObject4;
        if (localObject3 == null)
        {
          if (this.mIconView != null)
          {
            this.mIconView.setVisibility(8);
            this.mIconView.setImageDrawable(null);
          }
        }
        else
        {
          if (this.mIconView == null)
          {
            ImageView localImageView = new ImageView(getContext());
            localObject4 = new LinearLayoutCompat.LayoutParams(-2, -2);
            ((LinearLayoutCompat.LayoutParams)localObject4).gravity = 16;
            localImageView.setLayoutParams((ViewGroup.LayoutParams)localObject4);
            addView(localImageView, 0);
            this.mIconView = localImageView;
          }
          this.mIconView.setImageDrawable((Drawable)localObject3);
          this.mIconView.setVisibility(0);
        }
        int i;
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          i = 0;
        } else {
          i = 1;
        }
        if (i == 0)
        {
          if (this.mTextView != null)
          {
            this.mTextView.setVisibility(8);
            this.mTextView.setText(null);
          }
        }
        else
        {
          if (this.mTextView == null)
          {
            localObject4 = new CompatTextView(getContext(), null, R.attr.actionBarTabTextStyle);
            ((TextView)localObject4).setEllipsize(TextUtils.TruncateAt.END);
            localObject3 = new LinearLayoutCompat.LayoutParams(-2, -2);
            ((LinearLayoutCompat.LayoutParams)localObject3).gravity = 16;
            ((TextView)localObject4).setLayoutParams((ViewGroup.LayoutParams)localObject3);
            addView((View)localObject4);
            this.mTextView = ((TextView)localObject4);
          }
          this.mTextView.setText((CharSequence)localObject2);
          this.mTextView.setVisibility(0);
        }
        if (this.mIconView != null) {
          this.mIconView.setContentDescription(((ActionBar.Tab)localObject1).getContentDescription());
        }
        if ((i != 0) || (TextUtils.isEmpty(((ActionBar.Tab)localObject1).getContentDescription())))
        {
          setOnLongClickListener(null);
          setLongClickable(false);
        }
        else
        {
          setOnLongClickListener(this);
        }
      }
      else
      {
        localObject1 = ((View)localObject2).getParent();
        if (localObject1 != this)
        {
          if (localObject1 != null) {
            ((ViewGroup)localObject1).removeView((View)localObject2);
          }
          addView((View)localObject2);
        }
        this.mCustomView = ((View)localObject2);
        if (this.mTextView != null) {
          this.mTextView.setVisibility(8);
        }
        if (this.mIconView != null)
        {
          this.mIconView.setVisibility(8);
          this.mIconView.setImageDrawable(null);
        }
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ScrollingTabContainerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */