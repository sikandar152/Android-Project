package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.MenuPresenter.Callback;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.internal.widget.DecorToolbar;
import android.support.v7.internal.widget.RtlSpacingHelper;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ToolbarWidgetWrapper;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.CollapsibleActionView;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Toolbar
  extends ViewGroup
{
  private static final String TAG = "Toolbar";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private int mButtonGravity;
  private ImageButton mCollapseButtonView;
  private CharSequence mCollapseDescription;
  private Drawable mCollapseIcon;
  private boolean mCollapsible;
  private final RtlSpacingHelper mContentInsets = new RtlSpacingHelper();
  private boolean mEatingTouch;
  View mExpandedActionView;
  private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
  private int mGravity = 8388627;
  private ImageView mLogoView;
  private int mMaxButtonHeight;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private ActionMenuView mMenuView;
  private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener;
  private int mMinHeight;
  private ImageButton mNavButtonView;
  private OnMenuItemClickListener mOnMenuItemClickListener;
  private ActionMenuPresenter mOuterActionMenuPresenter;
  private Context mPopupContext;
  private int mPopupTheme;
  private final Runnable mShowOverflowMenuRunnable;
  private CharSequence mSubtitleText;
  private int mSubtitleTextAppearance;
  private int mSubtitleTextColor;
  private TextView mSubtitleTextView;
  private final int[] mTempMargins = new int[2];
  private final ArrayList<View> mTempViews = new ArrayList();
  private final TintManager mTintManager;
  private int mTitleMarginBottom;
  private int mTitleMarginEnd;
  private int mTitleMarginStart;
  private int mTitleMarginTop;
  private CharSequence mTitleText;
  private int mTitleTextAppearance;
  private int mTitleTextColor;
  private TextView mTitleTextView;
  private ToolbarWidgetWrapper mWrapper;
  
  public Toolbar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Toolbar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.toolbarStyle);
  }
  
  public Toolbar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(themifyContext(paramContext, paramAttributeSet, paramInt), paramAttributeSet, paramInt);
    Object localObject1 = new ActionMenuView.OnMenuItemClickListener()
    {
      public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
      {
        boolean bool;
        if (Toolbar.this.mOnMenuItemClickListener == null) {
          bool = false;
        } else {
          bool = Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(paramAnonymousMenuItem);
        }
        return bool;
      }
    };
    this.mMenuViewItemClickListener = ((ActionMenuView.OnMenuItemClickListener)localObject1);
    localObject1 = new Runnable()
    {
      public void run()
      {
        Toolbar.this.showOverflowMenu();
      }
    };
    this.mShowOverflowMenuRunnable = ((Runnable)localObject1);
    localObject1 = TintTypedArray.obtainStyledAttributes(getContext(), paramAttributeSet, R.styleable.Toolbar, paramInt, 0);
    this.mTitleTextAppearance = ((TintTypedArray)localObject1).getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
    this.mSubtitleTextAppearance = ((TintTypedArray)localObject1).getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
    this.mGravity = ((TintTypedArray)localObject1).getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
    this.mButtonGravity = 48;
    int i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, 0);
    this.mTitleMarginBottom = i;
    this.mTitleMarginTop = i;
    this.mTitleMarginEnd = i;
    this.mTitleMarginStart = i;
    i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
    if (i >= 0) {
      this.mTitleMarginStart = i;
    }
    i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
    if (i >= 0) {
      this.mTitleMarginEnd = i;
    }
    i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
    if (i >= 0) {
      this.mTitleMarginTop = i;
    }
    i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
    if (i >= 0) {
      this.mTitleMarginBottom = i;
    }
    this.mMaxButtonHeight = ((TintTypedArray)localObject1).getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
    int k = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, Integer.MIN_VALUE);
    i = ((TintTypedArray)localObject1).getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, Integer.MIN_VALUE);
    int m = ((TintTypedArray)localObject1).getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
    int j = ((TintTypedArray)localObject1).getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);
    this.mContentInsets.setAbsolute(m, j);
    if ((k != Integer.MIN_VALUE) || (i != Integer.MIN_VALUE)) {
      this.mContentInsets.setRelative(k, i);
    }
    this.mCollapseIcon = ((TintTypedArray)localObject1).getDrawable(R.styleable.Toolbar_collapseIcon);
    this.mCollapseDescription = ((TintTypedArray)localObject1).getText(R.styleable.Toolbar_collapseContentDescription);
    Object localObject2 = ((TintTypedArray)localObject1).getText(R.styleable.Toolbar_title);
    if (!TextUtils.isEmpty((CharSequence)localObject2)) {
      setTitle((CharSequence)localObject2);
    }
    localObject2 = ((TintTypedArray)localObject1).getText(R.styleable.Toolbar_subtitle);
    if (!TextUtils.isEmpty((CharSequence)localObject2)) {
      setSubtitle((CharSequence)localObject2);
    }
    this.mPopupContext = getContext();
    setPopupTheme(((TintTypedArray)localObject1).getResourceId(R.styleable.Toolbar_popupTheme, 0));
    localObject2 = ((TintTypedArray)localObject1).getDrawable(R.styleable.Toolbar_navigationIcon);
    if (localObject2 != null) {
      setNavigationIcon((Drawable)localObject2);
    }
    localObject2 = ((TintTypedArray)localObject1).getText(R.styleable.Toolbar_navigationContentDescription);
    if (!TextUtils.isEmpty((CharSequence)localObject2)) {
      setNavigationContentDescription((CharSequence)localObject2);
    }
    this.mMinHeight = ((TintTypedArray)localObject1).getDimensionPixelSize(R.styleable.Toolbar_android_minHeight, 0);
    ((TintTypedArray)localObject1).recycle();
    this.mTintManager = ((TintTypedArray)localObject1).getTintManager();
  }
  
  private void addCustomViewsWithGravity(List<View> paramList, int paramInt)
  {
    int k = 1;
    if (ViewCompat.getLayoutDirection(this) != k) {
      k = 0;
    }
    int j = getChildCount();
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    paramList.clear();
    if (k == 0) {
      for (k = 0; k < j; k++)
      {
        View localView2 = getChildAt(k);
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if ((localLayoutParams2.mViewType == 0) && (shouldLayout(localView2)) && (getChildHorizontalGravity(localLayoutParams2.gravity) == i)) {
          paramList.add(localView2);
        }
      }
    }
    for (int m = j - 1;; m--)
    {
      if (m < 0) {
        return;
      }
      View localView1 = getChildAt(m);
      LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if ((localLayoutParams1.mViewType == 0) && (shouldLayout(localView1)) && (getChildHorizontalGravity(localLayoutParams1.gravity) == i)) {
        paramList.add(localView1);
      }
    }
  }
  
  private void addSystemView(View paramView)
  {
    Object localObject = paramView.getLayoutParams();
    if (localObject != null)
    {
      if (checkLayoutParams((ViewGroup.LayoutParams)localObject)) {
        localObject = (LayoutParams)localObject;
      } else {
        localObject = generateLayoutParams((ViewGroup.LayoutParams)localObject);
      }
    }
    else {
      localObject = generateDefaultLayoutParams();
    }
    ((LayoutParams)localObject).mViewType = 1;
    addView(paramView, (ViewGroup.LayoutParams)localObject);
  }
  
  private void ensureCollapseButtonView()
  {
    if (this.mCollapseButtonView == null)
    {
      this.mCollapseButtonView = new ImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
      this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800003 | 0x70 & this.mButtonGravity);
      localLayoutParams.mViewType = 2;
      this.mCollapseButtonView.setLayoutParams(localLayoutParams);
      this.mCollapseButtonView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Toolbar.this.collapseActionView();
        }
      });
    }
  }
  
  private void ensureLogoView()
  {
    if (this.mLogoView == null) {
      this.mLogoView = new ImageView(getContext());
    }
  }
  
  private void ensureMenu()
  {
    ensureMenuView();
    if (this.mMenuView.peekMenu() == null)
    {
      MenuBuilder localMenuBuilder = (MenuBuilder)this.mMenuView.getMenu();
      if (this.mExpandedMenuPresenter == null) {
        this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(null);
      }
      this.mMenuView.setExpandedActionViewsExclusive(true);
      localMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    }
  }
  
  private void ensureMenuView()
  {
    if (this.mMenuView == null)
    {
      this.mMenuView = new ActionMenuView(getContext());
      this.mMenuView.setPopupTheme(this.mPopupTheme);
      this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
      this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800005 | 0x70 & this.mButtonGravity);
      this.mMenuView.setLayoutParams(localLayoutParams);
      addSystemView(this.mMenuView);
    }
  }
  
  private void ensureNavButtonView()
  {
    if (this.mNavButtonView == null)
    {
      this.mNavButtonView = new ImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800003 | 0x70 & this.mButtonGravity);
      this.mNavButtonView.setLayoutParams(localLayoutParams);
    }
  }
  
  private int getChildHorizontalGravity(int paramInt)
  {
    int i = ViewCompat.getLayoutDirection(this);
    int j = 0x7 & GravityCompat.getAbsoluteGravity(paramInt, i);
    switch (j)
    {
    case 2: 
    case 4: 
    default: 
      if (i != 1) {
        i = 3;
      } else {
        i = 5;
      }
      j = i;
    }
    return j;
  }
  
  private int getChildTop(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int k = paramView.getMeasuredHeight();
    int j;
    if (paramInt <= 0) {
      j = 0;
    } else {
      j = (k - paramInt) / 2;
    }
    int i;
    switch (getChildVerticalGravity(localLayoutParams.gravity))
    {
    default: 
      int m = getPaddingTop();
      int n = getPaddingBottom();
      int i1 = getHeight();
      j = (i1 - m - n - k) / 2;
      if (j >= localLayoutParams.topMargin)
      {
        k = i1 - n - k - j - m;
        if (k < localLayoutParams.bottomMargin) {
          j = Math.max(0, j - (localLayoutParams.bottomMargin - k));
        }
      }
      else
      {
        j = localLayoutParams.topMargin;
      }
      i = m + j;
      break;
    case 48: 
      i = getPaddingTop() - j;
      break;
    case 80: 
      i = getHeight() - getPaddingBottom() - k - i.bottomMargin - j;
    }
    return i;
  }
  
  private int getChildVerticalGravity(int paramInt)
  {
    int i = paramInt & 0x70;
    switch (i)
    {
    default: 
      i = 0x70 & this.mGravity;
    }
    return i;
  }
  
  private int getHorizontalMargins(View paramView)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return MarginLayoutParamsCompat.getMarginStart(localMarginLayoutParams) + MarginLayoutParamsCompat.getMarginEnd(localMarginLayoutParams);
  }
  
  private MenuInflater getMenuInflater()
  {
    return new SupportMenuInflater(getContext());
  }
  
  private int getMinimumHeightCompat()
  {
    int i;
    if (Build.VERSION.SDK_INT < 16) {
      i = this.mMinHeight;
    } else {
      i = ViewCompat.getMinimumHeight(this);
    }
    return i;
  }
  
  private int getVerticalMargins(View paramView)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin;
  }
  
  private int getViewListMeasuredWidth(List<View> paramList, int[] paramArrayOfInt)
  {
    int m = paramArrayOfInt[0];
    int i1 = paramArrayOfInt[1];
    int i = 0;
    int j = paramList.size();
    for (int k = 0;; k++)
    {
      if (k >= j) {
        return i;
      }
      View localView = (View)paramList.get(k);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      m = localLayoutParams.leftMargin - m;
      i1 = localLayoutParams.rightMargin - i1;
      int i2 = Math.max(0, m);
      int n = Math.max(0, i1);
      m = Math.max(0, -m);
      i1 = Math.max(0, -i1);
      i += n + (i2 + localView.getMeasuredWidth());
    }
  }
  
  private static boolean isCustomView(View paramView)
  {
    boolean bool;
    if (((LayoutParams)paramView.getLayoutParams()).mViewType != 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private int layoutChildLeft(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int j = localLayoutParams.leftMargin - paramArrayOfInt[0];
    int i = paramInt1 + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -j);
    j = getChildTop(paramView, paramInt2);
    int k = paramView.getMeasuredWidth();
    paramView.layout(i, j, i + k, j + paramView.getMeasuredHeight());
    return i + (k + localLayoutParams.rightMargin);
  }
  
  private int layoutChildRight(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int j = localLayoutParams.rightMargin - paramArrayOfInt[1];
    int i = paramInt1 - Math.max(0, j);
    paramArrayOfInt[1] = Math.max(0, -j);
    j = getChildTop(paramView, paramInt2);
    int k = paramView.getMeasuredWidth();
    paramView.layout(i - k, j, i, j + paramView.getMeasuredHeight());
    return i - (k + localLayoutParams.leftMargin);
  }
  
  private int measureChildCollapseMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = localMarginLayoutParams.leftMargin - paramArrayOfInt[0];
    int j = localMarginLayoutParams.rightMargin - paramArrayOfInt[1];
    int k = Math.max(0, i) + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramArrayOfInt[1] = Math.max(0, -j);
    paramView.measure(getChildMeasureSpec(paramInt1, paramInt2 + (k + (getPaddingLeft() + getPaddingRight())), localMarginLayoutParams.width), getChildMeasureSpec(paramInt3, paramInt4 + (getPaddingTop() + getPaddingBottom() + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin), localMarginLayoutParams.height));
    return k + paramView.getMeasuredWidth();
  }
  
  private void measureChildConstrained(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, paramInt2 + (getPaddingLeft() + getPaddingRight() + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin), localMarginLayoutParams.width);
    int j = getChildMeasureSpec(paramInt3, paramInt4 + (getPaddingTop() + getPaddingBottom() + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin), localMarginLayoutParams.height);
    int k = View.MeasureSpec.getMode(j);
    if ((k != 1073741824) && (paramInt5 >= 0))
    {
      if (k == 0) {
        j = paramInt5;
      } else {
        j = Math.min(View.MeasureSpec.getSize(j), paramInt5);
      }
      j = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
    }
    paramView.measure(i, j);
  }
  
  private void postShowOverflowMenu()
  {
    removeCallbacks(this.mShowOverflowMenuRunnable);
    post(this.mShowOverflowMenuRunnable);
  }
  
  private void setChildVisibilityForExpandedActionView(boolean paramBoolean)
  {
    int i = getChildCount();
    for (int k = 0;; k++)
    {
      if (k >= i) {
        return;
      }
      View localView = getChildAt(k);
      if ((((LayoutParams)localView.getLayoutParams()).mViewType != 2) && (localView != this.mMenuView))
      {
        int j;
        if (!paramBoolean) {
          j = 0;
        } else {
          j = 8;
        }
        localView.setVisibility(j);
      }
    }
  }
  
  private boolean shouldCollapse()
  {
    boolean bool = false;
    int j;
    if (this.mCollapsible) {
      j = getChildCount();
    }
    for (int i = 0;; i++)
    {
      if (i >= j)
      {
        bool = true;
      }
      else
      {
        View localView = getChildAt(i);
        if ((!shouldLayout(localView)) || (localView.getMeasuredWidth() <= 0) || (localView.getMeasuredHeight() <= 0)) {
          continue;
        }
      }
      return bool;
    }
  }
  
  private boolean shouldLayout(View paramView)
  {
    boolean bool;
    if ((paramView == null) || (paramView.getParent() != this) || (paramView.getVisibility() == 8)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private static Context themifyContext(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Toolbar, paramInt, 0);
    int i = localTypedArray.getResourceId(R.styleable.Toolbar_theme, 0);
    if (i != 0) {
      paramContext = new ContextThemeWrapper(paramContext, i);
    }
    localTypedArray.recycle();
    return paramContext;
  }
  
  private void updateChildVisibilityForExpandedActionView(View paramView)
  {
    if ((((LayoutParams)paramView.getLayoutParams()).mViewType != 2) && (paramView != this.mMenuView))
    {
      int i;
      if (this.mExpandedActionView == null) {
        i = 0;
      } else {
        i = 8;
      }
      paramView.setVisibility(i);
    }
  }
  
  public boolean canShowOverflowMenu()
  {
    boolean bool;
    if ((getVisibility() != 0) || (this.mMenuView == null) || (!this.mMenuView.isOverflowReserved())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((!super.checkLayoutParams(paramLayoutParams)) || (!(paramLayoutParams instanceof LayoutParams))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void collapseActionView()
  {
    MenuItemImpl localMenuItemImpl;
    if (this.mExpandedMenuPresenter != null) {
      localMenuItemImpl = this.mExpandedMenuPresenter.mCurrentExpandedItem;
    } else {
      localMenuItemImpl = null;
    }
    if (localMenuItemImpl != null) {
      localMenuItemImpl.collapseActionView();
    }
  }
  
  public void dismissPopupMenus()
  {
    if (this.mMenuView != null) {
      this.mMenuView.dismissPopupMenus();
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    LayoutParams localLayoutParams;
    if (!(paramLayoutParams instanceof LayoutParams))
    {
      if (!(paramLayoutParams instanceof ActionBar.LayoutParams))
      {
        if (!(paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
          localLayoutParams = new LayoutParams(paramLayoutParams);
        } else {
          localLayoutParams = new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
        }
      }
      else {
        localLayoutParams = new LayoutParams((ActionBar.LayoutParams)paramLayoutParams);
      }
    }
    else {
      localLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return localLayoutParams;
  }
  
  public int getContentInsetEnd()
  {
    return this.mContentInsets.getEnd();
  }
  
  public int getContentInsetLeft()
  {
    return this.mContentInsets.getLeft();
  }
  
  public int getContentInsetRight()
  {
    return this.mContentInsets.getRight();
  }
  
  public int getContentInsetStart()
  {
    return this.mContentInsets.getStart();
  }
  
  public Drawable getLogo()
  {
    Drawable localDrawable;
    if (this.mLogoView == null) {
      localDrawable = null;
    } else {
      localDrawable = this.mLogoView.getDrawable();
    }
    return localDrawable;
  }
  
  public CharSequence getLogoDescription()
  {
    CharSequence localCharSequence;
    if (this.mLogoView == null) {
      localCharSequence = null;
    } else {
      localCharSequence = this.mLogoView.getContentDescription();
    }
    return localCharSequence;
  }
  
  public Menu getMenu()
  {
    ensureMenu();
    return this.mMenuView.getMenu();
  }
  
  @Nullable
  public CharSequence getNavigationContentDescription()
  {
    CharSequence localCharSequence;
    if (this.mNavButtonView == null) {
      localCharSequence = null;
    } else {
      localCharSequence = this.mNavButtonView.getContentDescription();
    }
    return localCharSequence;
  }
  
  @Nullable
  public Drawable getNavigationIcon()
  {
    Drawable localDrawable;
    if (this.mNavButtonView == null) {
      localDrawable = null;
    } else {
      localDrawable = this.mNavButtonView.getDrawable();
    }
    return localDrawable;
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mSubtitleText;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitleText;
  }
  
  public DecorToolbar getWrapper()
  {
    if (this.mWrapper == null) {
      this.mWrapper = new ToolbarWidgetWrapper(this, true);
    }
    return this.mWrapper;
  }
  
  public boolean hasExpandedActionView()
  {
    boolean bool;
    if ((this.mExpandedMenuPresenter == null) || (this.mExpandedMenuPresenter.mCurrentExpandedItem == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hideOverflowMenu()
  {
    boolean bool;
    if ((this.mMenuView == null) || (!this.mMenuView.hideOverflowMenu())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void inflateMenu(int paramInt)
  {
    getMenuInflater().inflate(paramInt, getMenu());
  }
  
  public boolean isOverflowMenuShowPending()
  {
    boolean bool;
    if ((this.mMenuView == null) || (!this.mMenuView.isOverflowMenuShowPending())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    boolean bool;
    if ((this.mMenuView == null) || (!this.mMenuView.isOverflowMenuShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isTitleTruncated()
  {
    boolean bool = false;
    if (this.mTitleTextView != null)
    {
      Layout localLayout = this.mTitleTextView.getLayout();
      if (localLayout != null)
      {
        int j = localLayout.getLineCount();
        int i = 0;
        while (i < j) {
          if (localLayout.getEllipsisCount(i) <= 0) {
            i++;
          } else {
            bool = true;
          }
        }
      }
    }
    return bool;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mShowOverflowMenuRunnable);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i6;
    if (ViewCompat.getLayoutDirection(this) != 1) {
      i6 = 0;
    } else {
      i6 = 1;
    }
    int j = getWidth();
    int i9 = getHeight();
    int m = getPaddingLeft();
    int n = getPaddingRight();
    int i7 = getPaddingTop();
    int i8 = getPaddingBottom();
    int i2 = m;
    int i1 = j - n;
    int[] arrayOfInt = this.mTempMargins;
    arrayOfInt[1] = 0;
    arrayOfInt[0] = 0;
    int i = getMinimumHeightCompat();
    if (shouldLayout(this.mNavButtonView)) {
      if (i6 == 0) {
        i2 = layoutChildLeft(this.mNavButtonView, i2, arrayOfInt, i);
      } else {
        i1 = layoutChildRight(this.mNavButtonView, i1, arrayOfInt, i);
      }
    }
    if (shouldLayout(this.mCollapseButtonView)) {
      if (i6 == 0) {
        i2 = layoutChildLeft(this.mCollapseButtonView, i2, arrayOfInt, i);
      } else {
        i1 = layoutChildRight(this.mCollapseButtonView, i1, arrayOfInt, i);
      }
    }
    if (shouldLayout(this.mMenuView)) {
      if (i6 == 0) {
        i1 = layoutChildRight(this.mMenuView, i1, arrayOfInt, i);
      } else {
        i2 = layoutChildLeft(this.mMenuView, i2, arrayOfInt, i);
      }
    }
    arrayOfInt[0] = Math.max(0, getContentInsetLeft() - i2);
    arrayOfInt[1] = Math.max(0, getContentInsetRight() - (j - n - i1));
    int k = getContentInsetLeft();
    k = Math.max(i2, k);
    i2 = j - n - getContentInsetRight();
    i1 = Math.min(i1, i2);
    if (shouldLayout(this.mExpandedActionView)) {
      if (i6 == 0) {
        k = layoutChildLeft(this.mExpandedActionView, k, arrayOfInt, i);
      } else {
        i1 = layoutChildRight(this.mExpandedActionView, i1, arrayOfInt, i);
      }
    }
    if (shouldLayout(this.mLogoView)) {
      if (i6 == 0) {
        k = layoutChildLeft(this.mLogoView, k, arrayOfInt, i);
      } else {
        i1 = layoutChildRight(this.mLogoView, i1, arrayOfInt, i);
      }
    }
    boolean bool2 = shouldLayout(this.mTitleTextView);
    boolean bool1 = shouldLayout(this.mSubtitleTextView);
    int i10 = 0;
    Object localObject1;
    if (bool2)
    {
      localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
      i10 = 0 + (((LayoutParams)localObject1).topMargin + this.mTitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin);
    }
    if (bool1)
    {
      localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
      i10 += ((LayoutParams)localObject1).topMargin + this.mSubtitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin;
    }
    if ((bool2) || (bool1))
    {
      if (!bool2) {
        localObject2 = this.mSubtitleTextView;
      } else {
        localObject2 = this.mTitleTextView;
      }
      if (!bool1) {
        localObject1 = this.mTitleTextView;
      } else {
        localObject1 = this.mSubtitleTextView;
      }
      Object localObject2 = (LayoutParams)((View)localObject2).getLayoutParams();
      LayoutParams localLayoutParams4 = (LayoutParams)((View)localObject1).getLayoutParams();
      if (((!bool2) || (this.mTitleTextView.getMeasuredWidth() <= 0)) && ((!bool1) || (this.mSubtitleTextView.getMeasuredWidth() <= 0))) {
        i3 = 0;
      } else {
        i3 = 1;
      }
      switch (0x70 & this.mGravity)
      {
      default: 
        int i11 = (i9 - i7 - i8 - i10) / 2;
        int i12 = ((LayoutParams)localObject2).topMargin + this.mTitleMarginTop;
        if (i11 >= i12)
        {
          i8 = i9 - i8 - i10 - i11 - i7;
          if (i8 < ((LayoutParams)localObject2).bottomMargin + this.mTitleMarginBottom) {
            i11 = Math.max(0, i11 - (localLayoutParams4.bottomMargin + this.mTitleMarginBottom - i8));
          }
        }
        else
        {
          i11 = ((LayoutParams)localObject2).topMargin + this.mTitleMarginTop;
        }
        i7 += i11;
        break;
      case 48: 
        i7 = getPaddingTop() + ((LayoutParams)localObject2).topMargin + this.mTitleMarginTop;
        break;
      case 80: 
        i7 = i9 - i8 - localLayoutParams4.bottomMargin - this.mTitleMarginBottom - i10;
      }
      LayoutParams localLayoutParams1;
      int i5;
      if (i6 == 0)
      {
        if (i3 == 0) {
          i6 = 0;
        } else {
          i6 = this.mTitleMarginStart;
        }
        i6 -= arrayOfInt[0];
        k += Math.max(0, i6);
        arrayOfInt[0] = Math.max(0, -i6);
        i6 = k;
        i8 = k;
        if (bool2)
        {
          LayoutParams localLayoutParams2 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          i10 = i6 + this.mTitleTextView.getMeasuredWidth();
          i9 = i7 + this.mTitleTextView.getMeasuredHeight();
          this.mTitleTextView.layout(i6, i7, i10, i9);
          i6 = i10 + this.mTitleMarginEnd;
          i7 = i9 + localLayoutParams2.bottomMargin;
        }
        if (bool1)
        {
          localLayoutParams1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          i7 += localLayoutParams1.topMargin;
          i5 = i8 + this.mSubtitleTextView.getMeasuredWidth();
          i9 = i7 + this.mSubtitleTextView.getMeasuredHeight();
          this.mSubtitleTextView.layout(i8, i7, i5, i9);
          i8 = i5 + this.mTitleMarginEnd;
          (i9 + localLayoutParams1.bottomMargin);
        }
        if (i3 != 0) {
          k = Math.max(i6, i8);
        }
      }
      else
      {
        if (i3 == 0) {
          i6 = 0;
        } else {
          i6 = this.mTitleMarginStart;
        }
        i6 -= arrayOfInt[1];
        i1 -= Math.max(0, i6);
        arrayOfInt[1] = Math.max(0, -i6);
        i6 = i1;
        i8 = i1;
        if (i5 != 0)
        {
          LayoutParams localLayoutParams3 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          i5 = i6 - this.mTitleTextView.getMeasuredWidth();
          i9 = i7 + this.mTitleTextView.getMeasuredHeight();
          this.mTitleTextView.layout(i5, i7, i6, i9);
          i6 = i5 - this.mTitleMarginEnd;
          i7 = i9 + localLayoutParams3.bottomMargin;
        }
        if (localLayoutParams1 != 0)
        {
          localLayoutParams1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          i7 += localLayoutParams1.topMargin;
          i5 = i8 - this.mSubtitleTextView.getMeasuredWidth();
          i9 = i7 + this.mSubtitleTextView.getMeasuredHeight();
          this.mSubtitleTextView.layout(i5, i7, i8, i9);
          i8 -= this.mTitleMarginEnd;
          (i9 + localLayoutParams1.bottomMargin);
        }
        if (i3 != 0) {
          i1 = Math.min(i6, i8);
        }
      }
    }
    addCustomViewsWithGravity(this.mTempViews, 3);
    int i3 = this.mTempViews.size();
    for (int i4 = 0;; i4++)
    {
      if (i4 >= i3)
      {
        addCustomViewsWithGravity(this.mTempViews, 5);
        i3 = this.mTempViews.size();
        for (i4 = 0;; i4++)
        {
          if (i4 >= i3)
          {
            addCustomViewsWithGravity(this.mTempViews, 1);
            i3 = getViewListMeasuredWidth(this.mTempViews, arrayOfInt);
            j = m + (j - m - n) / 2 - i3 / 2;
            m = j + i3;
            if (j >= k)
            {
              if (m > i1) {
                j -= m - i1;
              }
            }
            else {
              j = k;
            }
            k = this.mTempViews.size();
            for (m = 0;; m++)
            {
              if (m >= k)
              {
                this.mTempViews.clear();
                return;
              }
              j = layoutChildLeft((View)this.mTempViews.get(m), j, arrayOfInt, i);
            }
          }
          i1 = layoutChildRight((View)this.mTempViews.get(i4), i1, arrayOfInt, i);
        }
      }
      k = layoutChildLeft((View)this.mTempViews.get(i4), k, arrayOfInt, i);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = 0;
    int i = 0;
    int[] arrayOfInt = this.mTempMargins;
    if (!ViewUtils.isLayoutRtl(this))
    {
      i2 = 0;
      n = 1;
    }
    else
    {
      i2 = 1;
      n = 0;
    }
    int i1 = 0;
    if (shouldLayout(this.mNavButtonView))
    {
      measureChildConstrained(this.mNavButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      i1 = this.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(this.mNavButtonView);
      j = Math.max(0, this.mNavButtonView.getMeasuredHeight() + getVerticalMargins(this.mNavButtonView));
      i = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(this.mNavButtonView));
    }
    if (shouldLayout(this.mCollapseButtonView))
    {
      measureChildConstrained(this.mCollapseButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      i1 = this.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(this.mCollapseButtonView);
      m = this.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(this.mCollapseButtonView);
      j = Math.max(j, m);
      m = ViewCompat.getMeasuredState(this.mCollapseButtonView);
      i = ViewUtils.combineMeasuredStates(i, m);
    }
    int i4 = getContentInsetStart();
    int m = 0 + Math.max(i4, i1);
    arrayOfInt[i2] = Math.max(0, i4 - i1);
    i1 = 0;
    if (shouldLayout(this.mMenuView))
    {
      measureChildConstrained(this.mMenuView, paramInt1, m, paramInt2, 0, this.mMaxButtonHeight);
      i1 = this.mMenuView.getMeasuredWidth() + getHorizontalMargins(this.mMenuView);
      i2 = this.mMenuView.getMeasuredHeight() + getVerticalMargins(this.mMenuView);
      j = Math.max(j, i2);
      i2 = ViewCompat.getMeasuredState(this.mMenuView);
      i = ViewUtils.combineMeasuredStates(i, i2);
    }
    int i2 = getContentInsetEnd();
    m += Math.max(i2, i1);
    arrayOfInt[n] = Math.max(0, i2 - i1);
    if (shouldLayout(this.mExpandedActionView))
    {
      m += measureChildCollapseMargins(this.mExpandedActionView, paramInt1, m, paramInt2, 0, arrayOfInt);
      n = this.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(this.mExpandedActionView);
      j = Math.max(j, n);
      n = ViewCompat.getMeasuredState(this.mExpandedActionView);
      i = ViewUtils.combineMeasuredStates(i, n);
    }
    if (shouldLayout(this.mLogoView))
    {
      m += measureChildCollapseMargins(this.mLogoView, paramInt1, m, paramInt2, 0, arrayOfInt);
      n = this.mLogoView.getMeasuredHeight() + getVerticalMargins(this.mLogoView);
      j = Math.max(j, n);
      n = ViewCompat.getMeasuredState(this.mLogoView);
      i = ViewUtils.combineMeasuredStates(i, n);
    }
    i1 = getChildCount();
    for (int n = 0;; n++)
    {
      int k;
      if (n >= i1)
      {
        i1 = 0;
        n = 0;
        i4 = this.mTitleMarginTop + this.mTitleMarginBottom;
        int i5 = this.mTitleMarginStart + this.mTitleMarginEnd;
        if (shouldLayout(this.mTitleTextView))
        {
          measureChildCollapseMargins(this.mTitleTextView, paramInt1, m + i5, paramInt2, i4, arrayOfInt);
          i1 = this.mTitleTextView.getMeasuredWidth() + getHorizontalMargins(this.mTitleTextView);
          n = this.mTitleTextView.getMeasuredHeight() + getVerticalMargins(this.mTitleTextView);
          i2 = ViewCompat.getMeasuredState(this.mTitleTextView);
          i = ViewUtils.combineMeasuredStates(i, i2);
        }
        if (shouldLayout(this.mSubtitleTextView))
        {
          k = measureChildCollapseMargins(this.mSubtitleTextView, paramInt1, m + i5, paramInt2, n + i4, arrayOfInt);
          i1 = Math.max(i1, k);
          n += this.mSubtitleTextView.getMeasuredHeight() + getVerticalMargins(this.mSubtitleTextView);
          k = ViewCompat.getMeasuredState(this.mSubtitleTextView);
          i = ViewUtils.combineMeasuredStates(i, k);
        }
        k = m + i1;
        m = Math.max(j, n);
        j = k + (getPaddingLeft() + getPaddingRight());
        k = m + (getPaddingTop() + getPaddingBottom());
        j = ViewCompat.resolveSizeAndState(Math.max(j, getSuggestedMinimumWidth()), paramInt1, 0xFF000000 & i);
        i = ViewCompat.resolveSizeAndState(Math.max(k, getSuggestedMinimumHeight()), paramInt2, i << 16);
        if (shouldCollapse()) {
          i = 0;
        }
        setMeasuredDimension(j, i);
        return;
      }
      View localView = getChildAt(n);
      if ((((LayoutParams)localView.getLayoutParams()).mViewType == 0) && (shouldLayout(localView)))
      {
        m += measureChildCollapseMargins(localView, paramInt1, m, paramInt2, 0, k);
        i4 = localView.getMeasuredHeight() + getVerticalMargins(localView);
        j = Math.max(j, i4);
        int i3 = ViewCompat.getMeasuredState(localView);
        i = ViewUtils.combineMeasuredStates(i, i3);
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    Object localObject;
    if (this.mMenuView == null) {
      localObject = null;
    } else {
      localObject = this.mMenuView.peekMenu();
    }
    if ((localSavedState.expandedMenuItemId != 0) && (this.mExpandedMenuPresenter != null) && (localObject != null))
    {
      localObject = ((Menu)localObject).findItem(localSavedState.expandedMenuItemId);
      if (localObject != null) {
        MenuItemCompat.expandActionView((MenuItem)localObject);
      }
    }
    if (localSavedState.isOverflowOpen) {
      postShowOverflowMenu();
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    int i = 1;
    if (Build.VERSION.SDK_INT >= 17) {
      super.onRtlPropertiesChanged(paramInt);
    }
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    if (paramInt != i) {
      i = 0;
    }
    localRtlSpacingHelper.setDirection(i);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if ((this.mExpandedMenuPresenter != null) && (this.mExpandedMenuPresenter.mCurrentExpandedItem != null)) {
      localSavedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
    }
    localSavedState.isOverflowOpen = isOverflowMenuShowing();
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      this.mEatingTouch = false;
    }
    if (!this.mEatingTouch)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if ((i == 0) && (!bool)) {
        this.mEatingTouch = true;
      }
    }
    if ((i == 1) || (i == 3)) {
      this.mEatingTouch = false;
    }
    return true;
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    this.mCollapsible = paramBoolean;
    requestLayout();
  }
  
  public void setContentInsetsAbsolute(int paramInt1, int paramInt2)
  {
    this.mContentInsets.setAbsolute(paramInt1, paramInt2);
  }
  
  public void setContentInsetsRelative(int paramInt1, int paramInt2)
  {
    this.mContentInsets.setRelative(paramInt1, paramInt2);
  }
  
  public void setLogo(int paramInt)
  {
    setLogo(this.mTintManager.getDrawable(paramInt));
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    if (paramDrawable == null)
    {
      if ((this.mLogoView != null) && (this.mLogoView.getParent() != null)) {
        removeView(this.mLogoView);
      }
    }
    else
    {
      ensureLogoView();
      if (this.mLogoView.getParent() == null)
      {
        addSystemView(this.mLogoView);
        updateChildVisibilityForExpandedActionView(this.mLogoView);
      }
    }
    if (this.mLogoView != null) {
      this.mLogoView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setLogoDescription(int paramInt)
  {
    setLogoDescription(getContext().getText(paramInt));
  }
  
  public void setLogoDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureLogoView();
    }
    if (this.mLogoView != null) {
      this.mLogoView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setMenu(MenuBuilder paramMenuBuilder, ActionMenuPresenter paramActionMenuPresenter)
  {
    if ((paramMenuBuilder != null) || (this.mMenuView != null))
    {
      ensureMenuView();
      MenuBuilder localMenuBuilder = this.mMenuView.peekMenu();
      if (localMenuBuilder != paramMenuBuilder)
      {
        if (localMenuBuilder != null)
        {
          localMenuBuilder.removeMenuPresenter(this.mOuterActionMenuPresenter);
          localMenuBuilder.removeMenuPresenter(this.mExpandedMenuPresenter);
        }
        if (this.mExpandedMenuPresenter == null) {
          this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(null);
        }
        paramActionMenuPresenter.setExpandedActionViewsExclusive(true);
        if (paramMenuBuilder == null)
        {
          paramActionMenuPresenter.initForMenu(this.mPopupContext, null);
          this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
          paramActionMenuPresenter.updateMenuView(true);
          this.mExpandedMenuPresenter.updateMenuView(true);
        }
        else
        {
          paramMenuBuilder.addMenuPresenter(paramActionMenuPresenter, this.mPopupContext);
          paramMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
        }
        this.mMenuView.setPopupTheme(this.mPopupTheme);
        this.mMenuView.setPresenter(paramActionMenuPresenter);
        this.mOuterActionMenuPresenter = paramActionMenuPresenter;
      }
    }
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
  }
  
  public void setMinimumHeight(int paramInt)
  {
    this.mMinHeight = paramInt;
    super.setMinimumHeight(paramInt);
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    CharSequence localCharSequence;
    if (paramInt == 0) {
      localCharSequence = null;
    } else {
      localCharSequence = getContext().getText(paramInt);
    }
    setNavigationContentDescription(localCharSequence);
  }
  
  public void setNavigationContentDescription(@Nullable CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureNavButtonView();
    }
    if (this.mNavButtonView != null) {
      this.mNavButtonView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setNavigationIcon(int paramInt)
  {
    setNavigationIcon(this.mTintManager.getDrawable(paramInt));
  }
  
  public void setNavigationIcon(@Nullable Drawable paramDrawable)
  {
    if (paramDrawable == null)
    {
      if ((this.mNavButtonView != null) && (this.mNavButtonView.getParent() != null)) {
        removeView(this.mNavButtonView);
      }
    }
    else
    {
      ensureNavButtonView();
      if (this.mNavButtonView.getParent() == null)
      {
        addSystemView(this.mNavButtonView);
        updateChildVisibilityForExpandedActionView(this.mNavButtonView);
      }
    }
    if (this.mNavButtonView != null) {
      this.mNavButtonView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setNavigationOnClickListener(View.OnClickListener paramOnClickListener)
  {
    ensureNavButtonView();
    this.mNavButtonView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setPopupTheme(int paramInt)
  {
    if (this.mPopupTheme != paramInt)
    {
      this.mPopupTheme = paramInt;
      if (paramInt != 0) {
        this.mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
      } else {
        this.mPopupContext = getContext();
      }
    }
  }
  
  public void setSubtitle(int paramInt)
  {
    setSubtitle(getContext().getText(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (TextUtils.isEmpty(paramCharSequence))
    {
      if ((this.mSubtitleTextView != null) && (this.mSubtitleTextView.getParent() != null)) {
        removeView(this.mSubtitleTextView);
      }
    }
    else
    {
      if (this.mSubtitleTextView == null)
      {
        Context localContext = getContext();
        this.mSubtitleTextView = new TextView(localContext);
        this.mSubtitleTextView.setSingleLine();
        this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mSubtitleTextAppearance != 0) {
          this.mSubtitleTextView.setTextAppearance(localContext, this.mSubtitleTextAppearance);
        }
        if (this.mSubtitleTextColor != 0) {
          this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor);
        }
      }
      if (this.mSubtitleTextView.getParent() == null)
      {
        addSystemView(this.mSubtitleTextView);
        updateChildVisibilityForExpandedActionView(this.mSubtitleTextView);
      }
    }
    if (this.mSubtitleTextView != null) {
      this.mSubtitleTextView.setText(paramCharSequence);
    }
    this.mSubtitleText = paramCharSequence;
  }
  
  public void setSubtitleTextAppearance(Context paramContext, int paramInt)
  {
    this.mSubtitleTextAppearance = paramInt;
    if (this.mSubtitleTextView != null) {
      this.mSubtitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setSubtitleTextColor(int paramInt)
  {
    this.mSubtitleTextColor = paramInt;
    if (this.mSubtitleTextView != null) {
      this.mSubtitleTextView.setTextColor(paramInt);
    }
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(getContext().getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (TextUtils.isEmpty(paramCharSequence))
    {
      if ((this.mTitleTextView != null) && (this.mTitleTextView.getParent() != null)) {
        removeView(this.mTitleTextView);
      }
    }
    else
    {
      if (this.mTitleTextView == null)
      {
        Context localContext = getContext();
        this.mTitleTextView = new TextView(localContext);
        this.mTitleTextView.setSingleLine();
        this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mTitleTextAppearance != 0) {
          this.mTitleTextView.setTextAppearance(localContext, this.mTitleTextAppearance);
        }
        if (this.mTitleTextColor != 0) {
          this.mTitleTextView.setTextColor(this.mTitleTextColor);
        }
      }
      if (this.mTitleTextView.getParent() == null)
      {
        addSystemView(this.mTitleTextView);
        updateChildVisibilityForExpandedActionView(this.mTitleTextView);
      }
    }
    if (this.mTitleTextView != null) {
      this.mTitleTextView.setText(paramCharSequence);
    }
    this.mTitleText = paramCharSequence;
  }
  
  public void setTitleTextAppearance(Context paramContext, int paramInt)
  {
    this.mTitleTextAppearance = paramInt;
    if (this.mTitleTextView != null) {
      this.mTitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setTitleTextColor(int paramInt)
  {
    this.mTitleTextColor = paramInt;
    if (this.mTitleTextView != null) {
      this.mTitleTextView.setTextColor(paramInt);
    }
  }
  
  public boolean showOverflowMenu()
  {
    boolean bool;
    if ((this.mMenuView == null) || (!this.mMenuView.showOverflowMenu())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private class ExpandedActionViewMenuPresenter
    implements MenuPresenter
  {
    MenuItemImpl mCurrentExpandedItem;
    MenuBuilder mMenu;
    
    private ExpandedActionViewMenuPresenter() {}
    
    public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewCollapsed();
      }
      Toolbar.this.removeView(Toolbar.this.mExpandedActionView);
      Toolbar.this.removeView(Toolbar.this.mCollapseButtonView);
      Toolbar.this.mExpandedActionView = null;
      Toolbar.this.setChildVisibilityForExpandedActionView(false);
      this.mCurrentExpandedItem = null;
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(false);
      return true;
    }
    
    public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      Toolbar.this.ensureCollapseButtonView();
      if (Toolbar.this.mCollapseButtonView.getParent() != Toolbar.this) {
        Toolbar.this.addView(Toolbar.this.mCollapseButtonView);
      }
      Toolbar.this.mExpandedActionView = paramMenuItemImpl.getActionView();
      this.mCurrentExpandedItem = paramMenuItemImpl;
      if (Toolbar.this.mExpandedActionView.getParent() != Toolbar.this)
      {
        Toolbar.LayoutParams localLayoutParams = Toolbar.this.generateDefaultLayoutParams();
        localLayoutParams.gravity = (0x800003 | 0x70 & Toolbar.this.mButtonGravity);
        localLayoutParams.mViewType = 2;
        Toolbar.this.mExpandedActionView.setLayoutParams(localLayoutParams);
        Toolbar.this.addView(Toolbar.this.mExpandedActionView);
      }
      Toolbar.this.setChildVisibilityForExpandedActionView(true);
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(true);
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewExpanded();
      }
      return true;
    }
    
    public boolean flagActionItems()
    {
      return false;
    }
    
    public int getId()
    {
      return 0;
    }
    
    public MenuView getMenuView(ViewGroup paramViewGroup)
    {
      return null;
    }
    
    public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
    {
      if ((this.mMenu != null) && (this.mCurrentExpandedItem != null)) {
        this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
      }
      this.mMenu = paramMenuBuilder;
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      return false;
    }
    
    public void setCallback(MenuPresenter.Callback paramCallback) {}
    
    public void updateMenuView(boolean paramBoolean)
    {
      if (this.mCurrentExpandedItem != null)
      {
        int k = 0;
        if (this.mMenu != null)
        {
          int j = this.mMenu.size();
          int i = 0;
          while (i < j) {
            if (this.mMenu.getItem(i) != this.mCurrentExpandedItem) {
              i++;
            } else {
              k = 1;
            }
          }
        }
        if (k == 0) {
          collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
        }
      }
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public Toolbar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Toolbar.SavedState(paramAnonymousParcel);
      }
      
      public Toolbar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Toolbar.SavedState[paramAnonymousInt];
      }
    };
    public int expandedMenuItemId;
    public boolean isOverflowOpen;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.expandedMenuItemId = paramParcel.readInt();
      boolean bool;
      if (paramParcel.readInt() == 0) {
        bool = false;
      } else {
        bool = true;
      }
      this.isOverflowOpen = bool;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.expandedMenuItemId);
      int i;
      if (!this.isOverflowOpen) {
        i = 0;
      } else {
        i = 1;
      }
      paramParcel.writeInt(i);
    }
  }
  
  public static class LayoutParams
    extends ActionBar.LayoutParams
  {
    static final int CUSTOM = 0;
    static final int EXPANDED = 2;
    static final int SYSTEM = 1;
    int mViewType = 0;
    
    public LayoutParams(int paramInt)
    {
      this(-2, -1, paramInt);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.gravity = 8388627;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      this.gravity = paramInt3;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ActionBar.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.mViewType = paramLayoutParams.mViewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
      copyMarginsFromCompat(paramMarginLayoutParams);
    }
    
    void copyMarginsFromCompat(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      this.leftMargin = paramMarginLayoutParams.leftMargin;
      this.topMargin = paramMarginLayoutParams.topMargin;
      this.rightMargin = paramMarginLayoutParams.rightMargin;
      this.bottomMargin = paramMarginLayoutParams.bottomMargin;
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\Toolbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */