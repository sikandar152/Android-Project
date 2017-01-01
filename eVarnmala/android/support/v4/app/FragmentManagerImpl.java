package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FragmentManagerImpl
  extends FragmentManager
  implements LayoutInflater.Factory
{
  static final Interpolator ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
  static final Interpolator ACCELERATE_QUINT;
  static final int ANIM_DUR = 220;
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  static boolean DEBUG = false;
  static final Interpolator DECELERATE_CUBIC;
  static final Interpolator DECELERATE_QUINT;
  static final boolean HONEYCOMB = false;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  ArrayList<Fragment> mActive;
  FragmentActivity mActivity;
  ArrayList<Fragment> mAdded;
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<Integer> mAvailIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
  ArrayList<Fragment> mCreatedMenus;
  int mCurState = 0;
  boolean mDestroyed;
  Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      FragmentManagerImpl.this.execPendingActions();
    }
  };
  boolean mExecutingActions;
  boolean mHavePendingDeferredStart;
  boolean mNeedMenuInvalidate;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<Runnable> mPendingActions;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  Runnable[] mTmpActions;
  
  static
  {
    boolean bool = false;
    DEBUG = false;
    if (Build.VERSION.SDK_INT >= 11) {
      bool = true;
    }
    HONEYCOMB = bool;
    DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
  }
  
  private void checkStateLoss()
  {
    if (!this.mStateSaved)
    {
      if (this.mNoTransactionsBecause == null) {
        return;
      }
      throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
    }
    throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
  }
  
  static Animation makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2)
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    return localAlphaAnimation;
  }
  
  static Animation makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    Object localObject = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    ((ScaleAnimation)localObject).setInterpolator(DECELERATE_QUINT);
    ((ScaleAnimation)localObject).setDuration(220L);
    localAnimationSet.addAnimation((Animation)localObject);
    localObject = new AlphaAnimation(paramFloat3, paramFloat4);
    ((AlphaAnimation)localObject).setInterpolator(DECELERATE_CUBIC);
    ((AlphaAnimation)localObject).setDuration(220L);
    localAnimationSet.addAnimation((Animation)localObject);
    return localAnimationSet;
  }
  
  public static int reverseTransit(int paramInt)
  {
    int i = 0;
    switch (paramInt)
    {
    case 4097: 
      i = 8194;
      break;
    case 4099: 
      i = 4099;
      break;
    case 8194: 
      i = 4097;
    }
    return i;
  }
  
  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (this.mActivity != null) {}
    for (;;)
    {
      try
      {
        this.mActivity.dump("  ", null, localPrintWriter, new String[0]);
        throw paramRuntimeException;
      }
      catch (Exception localException1)
      {
        Log.e("FragmentManager", "Failed dumping state", localException1);
        continue;
      }
      try
      {
        dump("  ", null, localException1, new String[0]);
      }
      catch (Exception localException2)
      {
        Log.e("FragmentManager", "Failed dumping state", localException2);
      }
    }
  }
  
  public static int transitToStyleIndex(int paramInt, boolean paramBoolean)
  {
    int i = -1;
    switch (paramInt)
    {
    case 4097: 
      if (!paramBoolean) {
        i = 2;
      } else {
        i = 1;
      }
      break;
    case 4099: 
      if (!paramBoolean) {
        i = 6;
      } else {
        i = 5;
      }
      break;
    case 8194: 
      if (!paramBoolean) {
        i = 4;
      } else {
        i = 3;
      }
      break;
    }
    return i;
  }
  
  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (this.mBackStack == null) {
      this.mBackStack = new ArrayList();
    }
    this.mBackStack.add(paramBackStackRecord);
    reportBackStackChanged();
  }
  
  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mAdded == null) {
      this.mAdded = new ArrayList();
    }
    if (DEBUG) {
      Log.v("FragmentManager", "add: " + paramFragment);
    }
    makeActive(paramFragment);
    if (!paramFragment.mDetached)
    {
      if (this.mAdded.contains(paramFragment)) {
        break label121;
      }
      this.mAdded.add(paramFragment);
      paramFragment.mAdded = true;
      paramFragment.mRemoving = false;
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      if (paramBoolean) {
        moveToState(paramFragment);
      }
    }
    return;
    label121:
    throw new IllegalStateException("Fragment already added: " + paramFragment);
  }
  
  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null) {
      this.mBackStackChangeListeners = new ArrayList();
    }
    this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }
  
  /* Error */
  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   6: ifnull +13 -> 19
    //   9: aload_0
    //   10: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   13: invokevirtual 315	java/util/ArrayList:size	()I
    //   16: ifgt +87 -> 103
    //   19: aload_0
    //   20: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   23: ifnonnull +14 -> 37
    //   26: aload_0
    //   27: new 253	java/util/ArrayList
    //   30: dup
    //   31: invokespecial 254	java/util/ArrayList:<init>	()V
    //   34: putfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   37: aload_0
    //   38: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   41: invokevirtual 315	java/util/ArrayList:size	()I
    //   44: istore_2
    //   45: getstatic 100	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   48: ifeq +39 -> 87
    //   51: ldc 47
    //   53: new 148	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   60: ldc_w 319
    //   63: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: iload_2
    //   67: invokevirtual 322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   70: ldc_w 324
    //   73: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: aload_1
    //   77: invokevirtual 270	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   80: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   83: invokestatic 273	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   86: pop
    //   87: aload_0
    //   88: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   91: aload_1
    //   92: invokevirtual 258	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   95: pop
    //   96: aload_0
    //   97: monitorexit
    //   98: iload_2
    //   99: istore_2
    //   100: goto +91 -> 191
    //   103: aload_0
    //   104: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   107: bipush -1
    //   109: aload_0
    //   110: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   113: invokevirtual 315	java/util/ArrayList:size	()I
    //   116: iadd
    //   117: invokevirtual 328	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   120: checkcast 330	java/lang/Integer
    //   123: invokevirtual 333	java/lang/Integer:intValue	()I
    //   126: istore_2
    //   127: getstatic 100	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   130: ifeq +39 -> 169
    //   133: ldc 47
    //   135: new 148	java/lang/StringBuilder
    //   138: dup
    //   139: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   142: ldc_w 335
    //   145: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: iload_2
    //   149: invokevirtual 322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   152: ldc_w 337
    //   155: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_1
    //   159: invokevirtual 270	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   162: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   165: invokestatic 273	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   168: pop
    //   169: aload_0
    //   170: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   173: iload_2
    //   174: aload_1
    //   175: invokevirtual 341	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   178: pop
    //   179: aload_0
    //   180: monitorexit
    //   181: iload_2
    //   182: istore_2
    //   183: goto +8 -> 191
    //   186: astore_2
    //   187: aload_0
    //   188: monitorexit
    //   189: aload_2
    //   190: athrow
    //   191: iload_2
    //   192: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	193	0	this	FragmentManagerImpl
    //   0	193	1	paramBackStackRecord	BackStackRecord
    //   44	139	2	i	int
    //   186	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	189	186	finally
  }
  
  public void attachActivity(FragmentActivity paramFragmentActivity, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (this.mActivity == null)
    {
      this.mActivity = paramFragmentActivity;
      this.mContainer = paramFragmentContainer;
      this.mParent = paramFragment;
      return;
    }
    throw new IllegalStateException("Already attached");
  }
  
  public void attachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "attach: " + paramFragment);
    }
    if (paramFragment.mDetached)
    {
      paramFragment.mDetached = false;
      if (!paramFragment.mAdded)
      {
        if (this.mAdded == null) {
          this.mAdded = new ArrayList();
        }
        if (this.mAdded.contains(paramFragment)) {
          break label158;
        }
        if (DEBUG) {
          Log.v("FragmentManager", "add from attach: " + paramFragment);
        }
        this.mAdded.add(paramFragment);
        paramFragment.mAdded = true;
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
          this.mNeedMenuInvalidate = true;
        }
        moveToState(paramFragment, this.mCurState, paramInt1, paramInt2, false);
      }
    }
    return;
    label158:
    throw new IllegalStateException("Fragment already added: " + paramFragment);
  }
  
  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }
  
  public void detachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "detach: " + paramFragment);
    }
    if (!paramFragment.mDetached)
    {
      paramFragment.mDetached = true;
      if (paramFragment.mAdded)
      {
        if (this.mAdded != null)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "remove from detach: " + paramFragment);
          }
          this.mAdded.remove(paramFragment);
        }
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
          this.mNeedMenuInvalidate = true;
        }
        paramFragment.mAdded = false;
        moveToState(paramFragment, 1, paramInt1, paramInt2, false);
      }
    }
  }
  
  public void dispatchActivityCreated()
  {
    this.mStateSaved = false;
    moveToState(2, false);
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        localFragment.performConfigurationChanged(paramConfiguration);
      }
    }
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return 0;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem))) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  public void dispatchCreate()
  {
    this.mStateSaved = false;
    moveToState(1, false);
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool = false;
    ArrayList localArrayList = null;
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size())
      {
        if (this.mCreatedMenus != null) {}
        for (i = 0;; i++)
        {
          if (i >= this.mCreatedMenus.size())
          {
            this.mCreatedMenus = localArrayList;
            return bool;
          }
          localFragment = (Fragment)this.mCreatedMenus.get(i);
          if ((localArrayList == null) || (!localArrayList.contains(localFragment))) {
            localFragment.onDestroyOptionsMenu();
          }
        }
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performCreateOptionsMenu(paramMenu, paramMenuInflater)))
      {
        bool = true;
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localFragment);
      }
    }
  }
  
  public void dispatchDestroy()
  {
    this.mDestroyed = true;
    execPendingActions();
    moveToState(0, false);
    this.mActivity = null;
    this.mContainer = null;
    this.mParent = null;
  }
  
  public void dispatchDestroyView()
  {
    moveToState(1, false);
  }
  
  public void dispatchLowMemory()
  {
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        localFragment.performLowMemory();
      }
    }
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return 0;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem))) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        localFragment.performOptionsMenuClosed(paramMenu);
      }
    }
  }
  
  public void dispatchPause()
  {
    moveToState(4, false);
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool = false;
    if (this.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mAdded.size()) {
        return bool;
      }
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performPrepareOptionsMenu(paramMenu))) {
        bool = true;
      }
    }
  }
  
  public void dispatchReallyStop()
  {
    moveToState(2, false);
  }
  
  public void dispatchResume()
  {
    this.mStateSaved = false;
    moveToState(5, false);
  }
  
  public void dispatchStart()
  {
    this.mStateSaved = false;
    moveToState(4, false);
  }
  
  public void dispatchStop()
  {
    this.mStateSaved = true;
    moveToState(3, false);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    int j;
    if (this.mActive != null)
    {
      j = this.mActive.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Active Fragments in ");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
        paramPrintWriter.println(":");
        for (int k = 0; k < j; k++)
        {
          Fragment localFragment = (Fragment)this.mActive.get(k);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(k);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment);
          if (localFragment != null) {
            localFragment.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
          }
        }
      }
    }
    int n;
    Object localObject2;
    if (this.mAdded != null)
    {
      n = this.mAdded.size();
      if (n > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Added Fragments:");
        for (j = 0; j < n; j++)
        {
          localObject2 = (Fragment)this.mAdded.get(j);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(j);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((Fragment)localObject2).toString());
        }
      }
    }
    if (this.mCreatedMenus != null)
    {
      j = this.mCreatedMenus.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        for (n = 0; n < j; n++)
        {
          localObject2 = (Fragment)this.mCreatedMenus.get(n);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(n);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((Fragment)localObject2).toString());
        }
      }
    }
    if (this.mBackStack != null)
    {
      j = this.mBackStack.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        for (n = 0; n < j; n++)
        {
          localObject2 = (BackStackRecord)this.mBackStack.get(n);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(n);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((BackStackRecord)localObject2).toString());
          ((BackStackRecord)localObject2).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    try
    {
      if (this.mBackStackIndices != null)
      {
        int i = this.mBackStackIndices.size();
        if (i > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          for (j = 0; j < i; j++)
          {
            localObject2 = (BackStackRecord)this.mBackStackIndices.get(j);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(j);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localObject2);
          }
        }
      }
      if ((this.mAvailBackStackIndices != null) && (this.mAvailBackStackIndices.size() > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAvailBackStackIndices: ");
        paramPrintWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
      }
      if (this.mPendingActions != null)
      {
        j = this.mPendingActions.size();
        if (j > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Actions:");
          for (int m = 0; m < j; m++)
          {
            Runnable localRunnable = (Runnable)this.mPendingActions.get(m);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(m);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localRunnable);
          }
        }
      }
      paramPrintWriter.print(paramString);
    }
    finally {}
    paramPrintWriter.println("FragmentManager misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mActivity=");
    paramPrintWriter.println(this.mActivity);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mContainer=");
    paramPrintWriter.println(this.mContainer);
    if (this.mParent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mParent=");
      paramPrintWriter.println(this.mParent);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mCurState=");
    paramPrintWriter.print(this.mCurState);
    paramPrintWriter.print(" mStateSaved=");
    paramPrintWriter.print(this.mStateSaved);
    paramPrintWriter.print(" mDestroyed=");
    paramPrintWriter.println(this.mDestroyed);
    if (this.mNeedMenuInvalidate)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNeedMenuInvalidate=");
      paramPrintWriter.println(this.mNeedMenuInvalidate);
    }
    if (this.mNoTransactionsBecause != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNoTransactionsBecause=");
      paramPrintWriter.println(this.mNoTransactionsBecause);
    }
    if ((this.mAvailIndices != null) && (this.mAvailIndices.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mAvailIndices: ");
      paramPrintWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
    }
  }
  
  public void enqueueAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (!paramBoolean) {
      checkStateLoss();
    }
    try
    {
      if ((this.mDestroyed) || (this.mActivity == null)) {
        throw new IllegalStateException("Activity has been destroyed");
      }
    }
    finally
    {
      throw ((Throwable)localObject);
      if (this.mPendingActions == null) {
        this.mPendingActions = new ArrayList();
      }
      this.mPendingActions.add(paramRunnable);
      if (this.mPendingActions.size() == 1) {
        this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
      }
    }
  }
  
  public boolean execPendingActions()
  {
    if (this.mExecutingActions) {
      throw new IllegalStateException("Recursive entry to executePendingTransactions");
    }
    if (Looper.myLooper() != this.mActivity.mHandler.getLooper()) {
      throw new IllegalStateException("Must be called from main thread of process");
    }
    for (int j = 0;; j = 1) {
      try
      {
        if ((this.mPendingActions == null) || (this.mPendingActions.size() == 0))
        {
          if (!this.mHavePendingDeferredStart) {
            return j;
          }
          boolean bool = false;
          for (int k = 0; k < this.mActive.size(); k++)
          {
            Fragment localFragment = (Fragment)this.mActive.get(k);
            if ((localFragment != null) && (localFragment.mLoaderManager != null)) {
              bool |= localFragment.mLoaderManager.hasRunningLoaders();
            }
          }
        }
        int i = this.mPendingActions.size();
        if ((this.mTmpActions == null) || (this.mTmpActions.length < i)) {
          this.mTmpActions = new Runnable[i];
        }
        this.mPendingActions.toArray(this.mTmpActions);
        this.mPendingActions.clear();
        this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
        this.mExecutingActions = true;
        for (j = 0; j < i; j++)
        {
          this.mTmpActions[j].run();
          this.mTmpActions[j] = null;
        }
        this.mExecutingActions = false;
      }
      finally {}
    }
    if (localObject == 0)
    {
      this.mHavePendingDeferredStart = false;
      startPendingDeferredFragments();
    }
    return j;
  }
  
  public boolean executePendingTransactions()
  {
    return execPendingActions();
  }
  
  public Fragment findFragmentById(int paramInt)
  {
    if (this.mAdded != null) {}
    Fragment localFragment;
    for (int i = -1 + this.mAdded.size();; i--)
    {
      if (i < 0)
      {
        if (this.mActive != null) {}
        for (i = -1 + this.mActive.size();; i--)
        {
          if (i < 0)
          {
            localFragment = null;
            break;
          }
          localFragment = (Fragment)this.mActive.get(i);
          if ((localFragment != null) && (localFragment.mFragmentId == paramInt)) {
            break;
          }
        }
      }
      localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.mFragmentId == paramInt)) {
        break;
      }
    }
    return localFragment;
  }
  
  public Fragment findFragmentByTag(String paramString)
  {
    if ((this.mAdded != null) && (paramString != null)) {}
    Fragment localFragment;
    for (int i = -1 + this.mAdded.size();; i--)
    {
      if (i < 0)
      {
        if ((this.mActive != null) && (paramString != null)) {}
        for (i = -1 + this.mActive.size();; i--)
        {
          if (i < 0)
          {
            localFragment = null;
            break;
          }
          localFragment = (Fragment)this.mActive.get(i);
          if ((localFragment != null) && (paramString.equals(localFragment.mTag))) {
            break;
          }
        }
      }
      localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (paramString.equals(localFragment.mTag))) {
        break;
      }
    }
    return localFragment;
  }
  
  public Fragment findFragmentByWho(String paramString)
  {
    if ((this.mActive != null) && (paramString != null)) {}
    Fragment localFragment;
    for (int i = -1 + this.mActive.size();; i--)
    {
      if (i < 0)
      {
        localFragment = null;
        break;
      }
      localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null)
      {
        localFragment = localFragment.findFragmentByWho(paramString);
        if (localFragment != null) {
          break;
        }
      }
    }
    return localFragment;
  }
  
  public void freeBackStackIndex(int paramInt)
  {
    try
    {
      this.mBackStackIndices.set(paramInt, null);
      if (this.mAvailBackStackIndices == null) {
        this.mAvailBackStackIndices = new ArrayList();
      }
      if (DEBUG) {
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      }
      this.mAvailBackStackIndices.add(Integer.valueOf(paramInt));
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt);
  }
  
  public int getBackStackEntryCount()
  {
    int i;
    if (this.mBackStack == null) {
      i = 0;
    } else {
      i = this.mBackStack.size();
    }
    return i;
  }
  
  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    Fragment localFragment;
    if (i != -1)
    {
      if (i >= this.mActive.size()) {
        throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
      }
      localFragment = (Fragment)this.mActive.get(i);
      if (localFragment == null) {
        throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
      }
    }
    else
    {
      localFragment = null;
    }
    return localFragment;
  }
  
  public List<Fragment> getFragments()
  {
    return this.mActive;
  }
  
  LayoutInflater.Factory getLayoutInflaterFactory()
  {
    return this;
  }
  
  public void hideFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "hide: " + paramFragment);
    }
    if (!paramFragment.mHidden)
    {
      paramFragment.mHidden = true;
      if (paramFragment.mView != null)
      {
        Animation localAnimation = loadAnimation(paramFragment, paramInt1, false, paramInt2);
        if (localAnimation != null) {
          paramFragment.mView.startAnimation(localAnimation);
        }
        paramFragment.mView.setVisibility(8);
      }
      if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(true);
    }
  }
  
  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  Animation loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    Animation localAnimation1 = paramFragment.onCreateAnimation(paramInt1, paramBoolean, paramFragment.mNextAnim);
    Animation localAnimation2;
    if (localAnimation1 == null)
    {
      if (paramFragment.mNextAnim != 0)
      {
        localAnimation1 = AnimationUtils.loadAnimation(this.mActivity, paramFragment.mNextAnim);
        if (localAnimation1 != null) {}
      }
      else
      {
        if (paramInt1 != 0)
        {
          int i = transitToStyleIndex(paramInt1, paramBoolean);
          if (i >= 0) {
            switch (i)
            {
            default: 
              if ((paramInt2 == 0) && (this.mActivity.getWindow() != null)) {
                paramInt2 = this.mActivity.getWindow().getAttributes().windowAnimations;
              }
              if (paramInt2 != 0) {
                localAnimation2 = null;
              } else {
                localAnimation2 = null;
              }
              break;
            case 1: 
              localAnimation2 = makeOpenCloseAnimation(this.mActivity, 1.125F, 1.0F, 0.0F, 1.0F);
              break;
            case 2: 
              localAnimation2 = makeOpenCloseAnimation(this.mActivity, 1.0F, 0.975F, 1.0F, 0.0F);
              break;
            case 3: 
              localAnimation2 = makeOpenCloseAnimation(this.mActivity, 0.975F, 1.0F, 0.0F, 1.0F);
              break;
            case 4: 
              localAnimation2 = makeOpenCloseAnimation(this.mActivity, 1.0F, 1.075F, 1.0F, 0.0F);
              break;
            case 5: 
              localAnimation2 = makeFadeAnimation(this.mActivity, 0.0F, 1.0F);
              break;
            case 6: 
              localAnimation2 = makeFadeAnimation(this.mActivity, 1.0F, 0.0F);
              break;
            }
          }
          localAnimation2 = null;
          break label295;
        }
        localAnimation2 = null;
        break label295;
      }
      localAnimation2 = localAnimation2;
    }
    label295:
    return localAnimation2;
  }
  
  void makeActive(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
    {
      if ((this.mAvailIndices != null) && (this.mAvailIndices.size() > 0))
      {
        paramFragment.setIndex(((Integer)this.mAvailIndices.remove(-1 + this.mAvailIndices.size())).intValue(), this.mParent);
        this.mActive.set(paramFragment.mIndex, paramFragment);
      }
      else
      {
        if (this.mActive == null) {
          this.mActive = new ArrayList();
        }
        paramFragment.setIndex(this.mActive.size(), this.mParent);
        this.mActive.add(paramFragment);
      }
      if (DEBUG) {
        Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
      }
    }
  }
  
  void makeInactive(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0)
    {
      if (DEBUG) {
        Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
      }
      this.mActive.set(paramFragment.mIndex, null);
      if (this.mAvailIndices == null) {
        this.mAvailIndices = new ArrayList();
      }
      this.mAvailIndices.add(Integer.valueOf(paramFragment.mIndex));
      this.mActivity.invalidateSupportFragment(paramFragment.mWho);
      paramFragment.initState();
    }
  }
  
  void moveToState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((this.mActivity != null) || (paramInt1 == 0))
    {
      boolean bool;
      if ((paramBoolean) || (this.mCurState != paramInt1))
      {
        this.mCurState = paramInt1;
        if (this.mActive != null) {
          bool = false;
        }
      }
      for (int i = 0;; i++)
      {
        if (i >= this.mActive.size())
        {
          if (!bool) {
            startPendingDeferredFragments();
          }
          if ((this.mNeedMenuInvalidate) && (this.mActivity != null) && (this.mCurState == 5))
          {
            this.mActivity.supportInvalidateOptionsMenu();
            this.mNeedMenuInvalidate = false;
          }
          return;
        }
        Fragment localFragment = (Fragment)this.mActive.get(i);
        if (localFragment != null)
        {
          moveToState(localFragment, paramInt1, paramInt2, paramInt3, false);
          if (localFragment.mLoaderManager != null) {
            bool |= localFragment.mLoaderManager.hasRunningLoaders();
          }
        }
      }
    }
    throw new IllegalStateException("No activity");
  }
  
  void moveToState(int paramInt, boolean paramBoolean)
  {
    moveToState(paramInt, 0, 0, paramBoolean);
  }
  
  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }
  
  void moveToState(final Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (((!paramFragment.mAdded) || (paramFragment.mDetached)) && (paramInt1 > 1)) {
      paramInt1 = 1;
    }
    if ((paramFragment.mRemoving) && (paramInt1 > paramFragment.mState)) {
      paramInt1 = paramFragment.mState;
    }
    if ((paramFragment.mDeferStart) && (paramFragment.mState < 4) && (paramInt1 > 3)) {
      paramInt1 = 3;
    }
    Object localObject;
    if (paramFragment.mState >= paramInt1)
    {
      if (paramFragment.mState > paramInt1)
      {
        switch (paramFragment.mState)
        {
        default: 
          break;
        case 5: 
          if (paramInt1 < 5)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
            }
            paramFragment.performPause();
            paramFragment.mResumed = false;
          }
        case 4: 
          if (paramInt1 < 4)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
            }
            paramFragment.performStop();
          }
        case 3: 
          if (paramInt1 < 3)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom STOPPED: " + paramFragment);
            }
            paramFragment.performReallyStop();
          }
        case 2: 
          if (paramInt1 < 2)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
            }
            if ((paramFragment.mView != null) && (!this.mActivity.isFinishing()) && (paramFragment.mSavedViewState == null)) {
              saveFragmentViewState(paramFragment);
            }
            paramFragment.performDestroyView();
            if ((paramFragment.mView != null) && (paramFragment.mContainer != null))
            {
              localObject = null;
              if ((this.mCurState > 0) && (!this.mDestroyed)) {
                localObject = loadAnimation(paramFragment, paramInt2, false, paramInt3);
              }
              if (localObject != null)
              {
                paramFragment.mAnimatingAway = paramFragment.mView;
                paramFragment.mStateAfterAnimating = paramInt1;
                ((Animation)localObject).setAnimationListener(new Animation.AnimationListener()
                {
                  public void onAnimationEnd(Animation paramAnonymousAnimation)
                  {
                    if (paramFragment.mAnimatingAway != null)
                    {
                      paramFragment.mAnimatingAway = null;
                      FragmentManagerImpl.this.moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, false);
                    }
                  }
                  
                  public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
                  
                  public void onAnimationStart(Animation paramAnonymousAnimation) {}
                });
                paramFragment.mView.startAnimation((Animation)localObject);
              }
              paramFragment.mContainer.removeView(paramFragment.mView);
            }
            paramFragment.mContainer = null;
            paramFragment.mView = null;
            paramFragment.mInnerView = null;
          }
          break;
        }
        if (paramInt1 < 1)
        {
          if ((this.mDestroyed) && (paramFragment.mAnimatingAway != null))
          {
            localObject = paramFragment.mAnimatingAway;
            paramFragment.mAnimatingAway = null;
            ((View)localObject).clearAnimation();
          }
          if (paramFragment.mAnimatingAway == null)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
            }
            if (!paramFragment.mRetaining) {
              paramFragment.performDestroy();
            }
            paramFragment.mCalled = false;
            paramFragment.onDetach();
            if (paramFragment.mCalled)
            {
              if (!paramBoolean) {
                if (paramFragment.mRetaining)
                {
                  paramFragment.mActivity = null;
                  paramFragment.mParentFragment = null;
                  paramFragment.mFragmentManager = null;
                  paramFragment.mChildFragmentManager = null;
                }
                else
                {
                  makeInactive(paramFragment);
                }
              }
            }
            else {
              throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onDetach()");
            }
          }
          else
          {
            paramFragment.mStateAfterAnimating = paramInt1;
            paramInt1 = 1;
          }
        }
      }
    }
    else
    {
      if ((paramFragment.mFromLayout) && (!paramFragment.mInLayout)) {
        break label1431;
      }
      if (paramFragment.mAnimatingAway != null)
      {
        paramFragment.mAnimatingAway = null;
        moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, true);
      }
      switch (paramFragment.mState)
      {
      case 0: 
        if (DEBUG) {
          Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
        }
        if (paramFragment.mSavedFragmentState != null)
        {
          paramFragment.mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
          paramFragment.mSavedViewState = paramFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
          paramFragment.mTarget = getFragment(paramFragment.mSavedFragmentState, "android:target_state");
          if (paramFragment.mTarget != null) {
            paramFragment.mTargetRequestCode = paramFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
          }
          paramFragment.mUserVisibleHint = paramFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
          if (!paramFragment.mUserVisibleHint)
          {
            paramFragment.mDeferStart = true;
            if (paramInt1 > 3) {
              paramInt1 = 3;
            }
          }
        }
        paramFragment.mActivity = this.mActivity;
        paramFragment.mParentFragment = this.mParent;
        if (this.mParent == null) {
          localObject = this.mActivity.mFragments;
        } else {
          localObject = this.mParent.mChildFragmentManager;
        }
        paramFragment.mFragmentManager = ((FragmentManagerImpl)localObject);
        paramFragment.mCalled = false;
        paramFragment.onAttach(this.mActivity);
        if (!paramFragment.mCalled) {
          break label1432;
        }
        if (paramFragment.mParentFragment == null) {
          this.mActivity.onAttachFragment(paramFragment);
        }
        if (!paramFragment.mRetaining) {
          paramFragment.performCreate(paramFragment.mSavedFragmentState);
        }
        paramFragment.mRetaining = false;
        if (paramFragment.mFromLayout)
        {
          paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
          if (paramFragment.mView == null)
          {
            paramFragment.mInnerView = null;
          }
          else
          {
            paramFragment.mInnerView = paramFragment.mView;
            paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
            if (paramFragment.mHidden) {
              paramFragment.mView.setVisibility(8);
            }
            paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
          }
        }
      case 1: 
        if (paramInt1 > 1)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
          }
          if (!paramFragment.mFromLayout)
          {
            localObject = null;
            if (paramFragment.mContainerId != 0)
            {
              localObject = (ViewGroup)this.mContainer.findViewById(paramFragment.mContainerId);
              if ((localObject == null) && (!paramFragment.mRestored)) {
                throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(paramFragment.mContainerId) + " (" + paramFragment.getResources().getResourceName(paramFragment.mContainerId) + ") for fragment " + paramFragment));
              }
            }
            paramFragment.mContainer = ((ViewGroup)localObject);
            paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), (ViewGroup)localObject, paramFragment.mSavedFragmentState);
            if (paramFragment.mView == null)
            {
              paramFragment.mInnerView = null;
            }
            else
            {
              paramFragment.mInnerView = paramFragment.mView;
              paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
              if (localObject != null)
              {
                Animation localAnimation = loadAnimation(paramFragment, paramInt2, true, paramInt3);
                if (localAnimation != null) {
                  paramFragment.mView.startAnimation(localAnimation);
                }
                ((ViewGroup)localObject).addView(paramFragment.mView);
              }
              if (paramFragment.mHidden) {
                paramFragment.mView.setVisibility(8);
              }
              paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
            }
          }
          paramFragment.performActivityCreated(paramFragment.mSavedFragmentState);
          if (paramFragment.mView != null) {
            paramFragment.restoreViewState(paramFragment.mSavedFragmentState);
          }
          paramFragment.mSavedFragmentState = null;
        }
      case 2: 
      case 3: 
        if (paramInt1 > 3)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
          }
          paramFragment.performStart();
        }
      case 4: 
        if (paramInt1 > 4)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
          }
          paramFragment.mResumed = true;
          paramFragment.performResume();
          paramFragment.mSavedFragmentState = null;
          paramFragment.mSavedViewState = null;
        }
        break;
      }
    }
    paramFragment.mState = paramInt1;
    label1431:
    return;
    label1432:
    throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
  }
  
  public void noteStateNotSaved()
  {
    this.mStateSaved = false;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    Object localObject2 = null;
    String str1;
    View localView;
    if ("fragment".equals(paramString))
    {
      str1 = paramAttributeSet.getAttributeValue(null, "class");
      Object localObject1 = paramContext.obtainStyledAttributes(paramAttributeSet, FragmentTag.Fragment);
      if (str1 == null) {
        str1 = ((TypedArray)localObject1).getString(0);
      }
      int i = ((TypedArray)localObject1).getResourceId(1, -1);
      String str2 = ((TypedArray)localObject1).getString(2);
      ((TypedArray)localObject1).recycle();
      if (Fragment.isSupportFragmentClass(this.mActivity, str1))
      {
        int j;
        if (0 == 0) {
          j = 0;
        } else {
          j = null.getId();
        }
        if ((j == -1) && (i == -1) && (str2 == null)) {
          break label566;
        }
        if (i == -1) {
          localObject1 = null;
        } else {
          localObject1 = findFragmentById(i);
        }
        if ((localObject1 == null) && (str2 != null)) {
          localObject1 = findFragmentByTag(str2);
        }
        if ((localObject1 == null) && (j != -1)) {
          localObject1 = findFragmentById(j);
        }
        if (DEBUG) {
          Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(i) + " fname=" + str1 + " existing=" + localObject1);
        }
        if (localObject1 != null)
        {
          if (!((Fragment)localObject1).mInLayout)
          {
            ((Fragment)localObject1).mInLayout = true;
            if (!((Fragment)localObject1).mRetaining) {
              ((Fragment)localObject1).onInflate(this.mActivity, paramAttributeSet, ((Fragment)localObject1).mSavedFragmentState);
            }
          }
          else
          {
            throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(i) + ", tag " + str2 + ", or parent id 0x" + Integer.toHexString(j) + " with another fragment for " + str1);
          }
        }
        else
        {
          localObject1 = Fragment.instantiate(paramContext, str1);
          ((Fragment)localObject1).mFromLayout = true;
          int k;
          if (i == 0) {
            k = j;
          } else {
            k = i;
          }
          ((Fragment)localObject1).mFragmentId = k;
          ((Fragment)localObject1).mContainerId = j;
          ((Fragment)localObject1).mTag = str2;
          ((Fragment)localObject1).mInLayout = true;
          ((Fragment)localObject1).mFragmentManager = this;
          ((Fragment)localObject1).onInflate(this.mActivity, paramAttributeSet, ((Fragment)localObject1).mSavedFragmentState);
          addFragment((Fragment)localObject1, true);
        }
        if ((this.mCurState >= 1) || (!((Fragment)localObject1).mFromLayout)) {
          moveToState((Fragment)localObject1);
        } else {
          moveToState((Fragment)localObject1, 1, 0, 0, false);
        }
        if (((Fragment)localObject1).mView == null) {
          break label531;
        }
        if (i != 0) {
          ((Fragment)localObject1).mView.setId(i);
        }
        if (((Fragment)localObject1).mView.getTag() == null) {
          ((Fragment)localObject1).mView.setTag(str2);
        }
        localView = ((Fragment)localObject1).mView;
      }
    }
    return localView;
    label531:
    throw new IllegalStateException("Fragment " + str1 + " did not create a view.");
    label566:
    throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str1);
  }
  
  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (paramFragment.mDeferStart) {
      if (!this.mExecutingActions)
      {
        paramFragment.mDeferStart = false;
        moveToState(paramFragment, this.mCurState, 0, 0, false);
      }
      else
      {
        this.mHavePendingDeferredStart = true;
      }
    }
  }
  
  public void popBackStack()
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, -1, 0);
      }
    }, false);
  }
  
  public void popBackStack(final int paramInt1, final int paramInt2)
  {
    if (paramInt1 >= 0)
    {
      enqueueAction(new Runnable()
      {
        public void run()
        {
          FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, paramInt1, paramInt2);
        }
      }, false);
      return;
    }
    throw new IllegalArgumentException("Bad id: " + paramInt1);
  }
  
  public void popBackStack(final String paramString, final int paramInt)
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, paramString, -1, paramInt);
      }
    }, false);
  }
  
  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    executePendingTransactions();
    if (paramInt1 >= 0) {
      return popBackStackState(this.mActivity.mHandler, null, paramInt1, paramInt2);
    }
    throw new IllegalArgumentException("Bad id: " + paramInt1);
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, paramString, -1, paramInt);
  }
  
  boolean popBackStackState(Handler paramHandler, String paramString, int paramInt1, int paramInt2)
  {
    boolean bool1;
    if (this.mBackStack != null)
    {
      SparseArray localSparseArray1;
      if ((paramString != null) || (paramInt1 >= 0) || ((paramInt2 & 0x1) != 0))
      {
        int j = -1;
        if ((paramString != null) || (paramInt1 >= 0)) {}
        for (j = -1 + this.mBackStack.size();; localSparseArray1--)
        {
          Object localObject;
          if (j >= 0)
          {
            localObject = (BackStackRecord)this.mBackStack.get(j);
            if (((paramString == null) || (!paramString.equals(((BackStackRecord)localObject).getName()))) && ((paramInt1 < 0) || (paramInt1 != ((BackStackRecord)localObject).mIndex))) {}
          }
          else
          {
            if (j >= 0)
            {
              if ((paramInt2 & 0x1) != 0) {
                j--;
              }
              for (;;)
              {
                if (j >= 0)
                {
                  localObject = (BackStackRecord)this.mBackStack.get(j);
                  if (((paramString != null) && (paramString.equals(((BackStackRecord)localObject).getName()))) || ((paramInt1 >= 0) && (paramInt1 == ((BackStackRecord)localObject).mIndex))) {}
                }
                else
                {
                  if (j != -1 + this.mBackStack.size())
                  {
                    localObject = new ArrayList();
                    for (int k = -1 + this.mBackStack.size();; k--)
                    {
                      if (k <= j)
                      {
                        k = -1 + ((ArrayList)localObject).size();
                        localSparseArray1 = new SparseArray();
                        SparseArray localSparseArray3 = new SparseArray();
                        for (int m = 0;; m++)
                        {
                          if (m > k)
                          {
                            BackStackRecord.TransitionState localTransitionState = null;
                            for (m = 0;; m++)
                            {
                              if (m > k)
                              {
                                reportBackStackChanged();
                                break;
                              }
                              if (DEBUG) {
                                Log.v("FragmentManager", "Popping back stack state: " + ((ArrayList)localObject).get(m));
                              }
                              BackStackRecord localBackStackRecord2 = (BackStackRecord)((ArrayList)localObject).get(m);
                              boolean bool2;
                              if (m != k) {
                                bool2 = false;
                              } else {
                                bool2 = true;
                              }
                              localTransitionState = localBackStackRecord2.popFromBackStack(bool2, localTransitionState, localSparseArray1, localSparseArray3);
                            }
                          }
                          ((BackStackRecord)((ArrayList)localObject).get(m)).calculateBackFragments(localSparseArray1, localSparseArray3);
                        }
                      }
                      ((ArrayList)localObject).add(this.mBackStack.remove(k));
                    }
                  }
                  i = 0;
                  break label502;
                }
                localSparseArray1--;
              }
            }
            i = 0;
            break label502;
          }
        }
      }
      int i = -1 + this.mBackStack.size();
      if (i >= 0)
      {
        BackStackRecord localBackStackRecord1 = (BackStackRecord)this.mBackStack.remove(i);
        localSparseArray1 = new SparseArray();
        SparseArray localSparseArray2 = new SparseArray();
        localBackStackRecord1.calculateBackFragments(localSparseArray1, localSparseArray2);
        localBackStackRecord1.popFromBackStack(true, null, localSparseArray1, localSparseArray2);
        reportBackStackChanged();
        bool1 = true;
      }
      else
      {
        bool1 = false;
      }
    }
    else
    {
      bool1 = false;
    }
    label502:
    return bool1;
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    paramBundle.putInt(paramString, paramFragment.mIndex);
  }
  
  public void removeFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + paramFragment.mBackStackNesting);
    }
    int i;
    if (paramFragment.isInBackStack()) {
      i = 0;
    } else {
      i = 1;
    }
    if ((!paramFragment.mDetached) || (i != 0))
    {
      if (this.mAdded != null) {
        this.mAdded.remove(paramFragment);
      }
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      paramFragment.mAdded = false;
      paramFragment.mRemoving = true;
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      }
      moveToState(paramFragment, i, paramInt1, paramInt2, false);
    }
  }
  
  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners != null) {
      this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
    }
  }
  
  void reportBackStackChanged()
  {
    if (this.mBackStackChangeListeners != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mBackStackChangeListeners.size()) {
        return;
      }
      ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
    }
  }
  
  void restoreAllState(Parcelable paramParcelable, ArrayList<Fragment> paramArrayList)
  {
    FragmentManagerState localFragmentManagerState;
    if (paramParcelable != null)
    {
      localFragmentManagerState = (FragmentManagerState)paramParcelable;
      if (localFragmentManagerState.mActive != null) {
        if (paramArrayList == null) {}
      }
    }
    Fragment localFragment;
    for (int j = 0;; localFragment++)
    {
      if (j >= paramArrayList.size())
      {
        this.mActive = new ArrayList(localFragmentManagerState.mActive.length);
        if (this.mAvailIndices != null) {
          this.mAvailIndices.clear();
        }
        for (int i = 0;; localObject1++)
        {
          if (i >= localFragmentManagerState.mActive.length)
          {
            if (paramArrayList != null) {}
            for (i = 0;; localObject1++)
            {
              if (i >= paramArrayList.size())
              {
                if (localFragmentManagerState.mAdded == null) {
                  this.mAdded = null;
                } else {
                  this.mAdded = new ArrayList(localFragmentManagerState.mAdded.length);
                }
                for (i = 0;; localObject1++)
                {
                  if (i >= localFragmentManagerState.mAdded.length)
                  {
                    if (localFragmentManagerState.mBackStack == null) {
                      this.mBackStack = null;
                    } else {
                      this.mBackStack = new ArrayList(localFragmentManagerState.mBackStack.length);
                    }
                    for (j = 0;; j++)
                    {
                      if (j >= localFragmentManagerState.mBackStack.length) {
                        return;
                      }
                      localObject1 = localFragmentManagerState.mBackStack[j].instantiate(this);
                      if (DEBUG)
                      {
                        Log.v("FragmentManager", "restoreAllState: back stack #" + j + " (index " + ((BackStackRecord)localObject1).mIndex + "): " + localObject1);
                        ((BackStackRecord)localObject1).dump("  ", new PrintWriter(new LogWriter("FragmentManager")), false);
                      }
                      this.mBackStack.add(localObject1);
                      if (((BackStackRecord)localObject1).mIndex >= 0) {
                        setBackStackIndex(((BackStackRecord)localObject1).mIndex, (BackStackRecord)localObject1);
                      }
                    }
                  }
                  localFragment = (Fragment)this.mActive.get(localFragmentManagerState.mAdded[localObject1]);
                  if (localFragment == null) {
                    throwException(new IllegalStateException("No instantiated fragment for index #" + localFragmentManagerState.mAdded[localObject1]));
                  }
                  localFragment.mAdded = true;
                  if (DEBUG) {
                    Log.v("FragmentManager", "restoreAllState: added #" + localObject1 + ": " + localFragment);
                  }
                  if (this.mAdded.contains(localFragment)) {
                    break;
                  }
                  this.mAdded.add(localFragment);
                }
                throw new IllegalStateException("Already added!");
              }
              localFragment = (Fragment)paramArrayList.get(localObject1);
              if (localFragment.mTargetIndex >= 0) {
                if (localFragment.mTargetIndex >= this.mActive.size())
                {
                  Log.w("FragmentManager", "Re-attaching retained fragment " + localFragment + " target no longer exists: " + localFragment.mTargetIndex);
                  localFragment.mTarget = null;
                }
                else
                {
                  localFragment.mTarget = ((Fragment)this.mActive.get(localFragment.mTargetIndex));
                }
              }
            }
          }
          localObject2 = localFragmentManagerState.mActive[localObject1];
          if (localObject2 == null)
          {
            this.mActive.add(null);
            if (this.mAvailIndices == null) {
              this.mAvailIndices = new ArrayList();
            }
            if (DEBUG) {
              Log.v("FragmentManager", "restoreAllState: avail #" + localObject1);
            }
            this.mAvailIndices.add(Integer.valueOf(localObject1));
          }
          else
          {
            localFragment = ((FragmentState)localObject2).instantiate(this.mActivity, this.mParent);
            if (DEBUG) {
              Log.v("FragmentManager", "restoreAllState: active #" + localObject1 + ": " + localFragment);
            }
            this.mActive.add(localFragment);
            ((FragmentState)localObject2).mInstance = null;
          }
        }
      }
      Object localObject2 = (Fragment)paramArrayList.get(localFragment);
      if (DEBUG) {
        Log.v("FragmentManager", "restoreAllState: re-attaching retained " + localObject2);
      }
      Object localObject1 = localFragmentManagerState.mActive[localObject2.mIndex];
      ((FragmentState)localObject1).mInstance = ((Fragment)localObject2);
      ((Fragment)localObject2).mSavedViewState = null;
      ((Fragment)localObject2).mBackStackNesting = 0;
      ((Fragment)localObject2).mInLayout = false;
      ((Fragment)localObject2).mAdded = false;
      ((Fragment)localObject2).mTarget = null;
      if (((FragmentState)localObject1).mSavedFragmentState != null)
      {
        ((FragmentState)localObject1).mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
        ((Fragment)localObject2).mSavedViewState = ((FragmentState)localObject1).mSavedFragmentState.getSparseParcelableArray("android:view_state");
        ((Fragment)localObject2).mSavedFragmentState = ((FragmentState)localObject1).mSavedFragmentState;
      }
    }
  }
  
  ArrayList<Fragment> retainNonConfig()
  {
    Object localObject = null;
    if (this.mActive != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mActive.size()) {
        return (ArrayList<Fragment>)localObject;
      }
      Fragment localFragment = (Fragment)this.mActive.get(i);
      if ((localFragment != null) && (localFragment.mRetainInstance))
      {
        if (localObject == null) {
          localObject = new ArrayList();
        }
        ((ArrayList)localObject).add(localFragment);
        localFragment.mRetaining = true;
        int j;
        if (localFragment.mTarget == null) {
          j = -1;
        } else {
          j = localFragment.mTarget.mIndex;
        }
        localFragment.mTargetIndex = j;
        if (DEBUG) {
          Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
        }
      }
    }
  }
  
  Parcelable saveAllState()
  {
    Object localObject = null;
    execPendingActions();
    if (HONEYCOMB) {
      this.mStateSaved = true;
    }
    int k;
    FragmentState[] arrayOfFragmentState;
    int m;
    if ((this.mActive != null) && (this.mActive.size() > 0))
    {
      k = this.mActive.size();
      arrayOfFragmentState = new FragmentState[k];
      m = 0;
    }
    BackStackState[] arrayOfBackStackState;
    for (int j = 0;; arrayOfBackStackState++)
    {
      if (j >= k)
      {
        FragmentManagerState localFragmentManagerState;
        if (m != 0)
        {
          int[] arrayOfInt = null;
          arrayOfBackStackState = null;
          if (this.mAdded != null)
          {
            m = this.mAdded.size();
            if (m > 0) {
              arrayOfInt = new int[m];
            }
          }
          for (int i = 0;; localFragmentManagerState++)
          {
            if (i >= m)
            {
              if (this.mBackStack != null)
              {
                i = this.mBackStack.size();
                if (i > 0) {
                  arrayOfBackStackState = new BackStackState[i];
                }
              }
              for (m = 0;; m++)
              {
                if (m >= i)
                {
                  localFragmentManagerState = new FragmentManagerState();
                  localFragmentManagerState.mActive = arrayOfFragmentState;
                  localFragmentManagerState.mAdded = arrayOfInt;
                  localFragmentManagerState.mBackStack = arrayOfBackStackState;
                  break;
                }
                arrayOfBackStackState[m] = new BackStackState(this, (BackStackRecord)this.mBackStack.get(m));
                if (DEBUG) {
                  Log.v("FragmentManager", "saveAllState: adding back stack #" + m + ": " + this.mBackStack.get(m));
                }
              }
            }
            arrayOfInt[localFragmentManagerState] = ((Fragment)this.mAdded.get(localFragmentManagerState)).mIndex;
            if (arrayOfInt[localFragmentManagerState] < 0) {
              throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(localFragmentManagerState) + " has cleared index: " + arrayOfInt[localFragmentManagerState]));
            }
            if (DEBUG) {
              Log.v("FragmentManager", "saveAllState: adding fragment #" + localFragmentManagerState + ": " + this.mAdded.get(localFragmentManagerState));
            }
          }
        }
        if (DEBUG) {
          Log.v("FragmentManager", "saveAllState: no fragments!");
        }
        return localFragmentManagerState;
      }
      Fragment localFragment = (Fragment)this.mActive.get(arrayOfBackStackState);
      if (localFragment != null)
      {
        if (localFragment.mIndex < 0) {
          throwException(new IllegalStateException("Failure saving state: active " + localFragment + " has cleared index: " + localFragment.mIndex));
        }
        m = 1;
        FragmentState localFragmentState = new FragmentState(localFragment);
        arrayOfFragmentState[arrayOfBackStackState] = localFragmentState;
        if ((localFragment.mState <= 0) || (localFragmentState.mSavedFragmentState != null))
        {
          localFragmentState.mSavedFragmentState = localFragment.mSavedFragmentState;
        }
        else
        {
          localFragmentState.mSavedFragmentState = saveFragmentBasicState(localFragment);
          if (localFragment.mTarget != null)
          {
            if (localFragment.mTarget.mIndex < 0) {
              throwException(new IllegalStateException("Failure saving state: " + localFragment + " has target not in fragment manager: " + localFragment.mTarget));
            }
            if (localFragmentState.mSavedFragmentState == null) {
              localFragmentState.mSavedFragmentState = new Bundle();
            }
            putFragment(localFragmentState.mSavedFragmentState, "android:target_state", localFragment.mTarget);
            if (localFragment.mTargetRequestCode != 0) {
              localFragmentState.mSavedFragmentState.putInt("android:target_req_state", localFragment.mTargetRequestCode);
            }
          }
        }
        if (DEBUG) {
          Log.v("FragmentManager", "Saved state of " + localFragment + ": " + localFragmentState.mSavedFragmentState);
        }
      }
    }
  }
  
  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    Bundle localBundle = null;
    if (this.mStateBundle == null) {
      this.mStateBundle = new Bundle();
    }
    paramFragment.performSaveInstanceState(this.mStateBundle);
    if (!this.mStateBundle.isEmpty())
    {
      localBundle = this.mStateBundle;
      this.mStateBundle = null;
    }
    if (paramFragment.mView != null) {
      saveFragmentViewState(paramFragment);
    }
    if (paramFragment.mSavedViewState != null)
    {
      if (localBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
    }
    if (!paramFragment.mUserVisibleHint)
    {
      if (localBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
    }
    return localBundle;
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    Fragment.SavedState localSavedState = null;
    if (paramFragment.mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    if (paramFragment.mState > 0)
    {
      Bundle localBundle = saveFragmentBasicState(paramFragment);
      if (localBundle != null) {
        localSavedState = new Fragment.SavedState(localBundle);
      }
    }
    return localSavedState;
  }
  
  void saveFragmentViewState(Fragment paramFragment)
  {
    if (paramFragment.mInnerView != null)
    {
      if (this.mStateArray != null) {
        this.mStateArray.clear();
      } else {
        this.mStateArray = new SparseArray();
      }
      paramFragment.mInnerView.saveHierarchyState(this.mStateArray);
      if (this.mStateArray.size() > 0)
      {
        paramFragment.mSavedViewState = this.mStateArray;
        this.mStateArray = null;
      }
    }
  }
  
  /* Error */
  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   6: ifnonnull +14 -> 20
    //   9: aload_0
    //   10: new 253	java/util/ArrayList
    //   13: dup
    //   14: invokespecial 254	java/util/ArrayList:<init>	()V
    //   17: putfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   20: aload_0
    //   21: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   24: invokevirtual 315	java/util/ArrayList:size	()I
    //   27: istore_3
    //   28: iload_1
    //   29: iload_3
    //   30: if_icmpge +58 -> 88
    //   33: getstatic 100	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   36: ifeq +39 -> 75
    //   39: ldc 47
    //   41: new 148	java/lang/StringBuilder
    //   44: dup
    //   45: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   48: ldc_w 319
    //   51: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: iload_1
    //   55: invokevirtual 322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   58: ldc_w 324
    //   61: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: aload_2
    //   65: invokevirtual 270	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   68: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokestatic 273	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   74: pop
    //   75: aload_0
    //   76: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   79: iload_1
    //   80: aload_2
    //   81: invokevirtual 341	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   84: pop
    //   85: aload_0
    //   86: monitorexit
    //   87: return
    //   88: iload_3
    //   89: iload_1
    //   90: if_icmpge +80 -> 170
    //   93: aload_0
    //   94: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   97: aconst_null
    //   98: invokevirtual 258	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   101: pop
    //   102: aload_0
    //   103: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   106: ifnonnull +14 -> 120
    //   109: aload_0
    //   110: new 253	java/util/ArrayList
    //   113: dup
    //   114: invokespecial 254	java/util/ArrayList:<init>	()V
    //   117: putfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   120: getstatic 100	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   123: ifeq +29 -> 152
    //   126: ldc 47
    //   128: new 148	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   135: ldc_w 1224
    //   138: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: iload_3
    //   142: invokevirtual 322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   145: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokestatic 273	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: aload_0
    //   153: getfield 311	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   156: iload_3
    //   157: invokestatic 602	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   160: invokevirtual 258	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   163: pop
    //   164: iinc 3 1
    //   167: goto -79 -> 88
    //   170: getstatic 100	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   173: ifeq +39 -> 212
    //   176: ldc 47
    //   178: new 148	java/lang/StringBuilder
    //   181: dup
    //   182: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   185: ldc_w 335
    //   188: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: iload_1
    //   192: invokevirtual 322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   195: ldc_w 337
    //   198: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload_2
    //   202: invokevirtual 270	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: invokestatic 273	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   211: pop
    //   212: aload_0
    //   213: getfield 317	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   216: aload_2
    //   217: invokevirtual 258	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   220: pop
    //   221: goto -136 -> 85
    //   224: astore_3
    //   225: aload_0
    //   226: monitorexit
    //   227: aload_3
    //   228: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	229	0	this	FragmentManagerImpl
    //   0	229	1	paramInt	int
    //   0	229	2	paramBackStackRecord	BackStackRecord
    //   27	138	3	i	int
    //   224	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	227	224	finally
  }
  
  public void showFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "show: " + paramFragment);
    }
    if (paramFragment.mHidden)
    {
      paramFragment.mHidden = false;
      if (paramFragment.mView != null)
      {
        Animation localAnimation = loadAnimation(paramFragment, paramInt1, true, paramInt2);
        if (localAnimation != null) {
          paramFragment.mView.startAnimation(localAnimation);
        }
        paramFragment.mView.setVisibility(0);
      }
      if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(false);
    }
  }
  
  void startPendingDeferredFragments()
  {
    if (this.mActive != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mActive.size()) {
        return;
      }
      Fragment localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null) {
        performPendingDeferredStart(localFragment);
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (this.mParent == null) {
      DebugUtils.buildShortClassTag(this.mActivity, localStringBuilder);
    } else {
      DebugUtils.buildShortClassTag(this.mParent, localStringBuilder);
    }
    localStringBuilder.append("}}");
    return localStringBuilder.toString();
  }
  
  static class FragmentTag
  {
    public static final int[] Fragment;
    public static final int Fragment_id = 1;
    public static final int Fragment_name = 0;
    public static final int Fragment_tag = 2;
    
    static
    {
      int[] arrayOfInt = new int[3];
      arrayOfInt[0] = 16842755;
      arrayOfInt[1] = 16842960;
      arrayOfInt[2] = 16842961;
      Fragment = arrayOfInt;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\FragmentManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */