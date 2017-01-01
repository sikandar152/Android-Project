package android.support.v4.app;

import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class BackStackRecord
  extends FragmentTransaction
  implements FragmentManager.BackStackEntry, Runnable
{
  static final int OP_ADD = 1;
  static final int OP_ATTACH = 7;
  static final int OP_DETACH = 6;
  static final int OP_HIDE = 4;
  static final int OP_NULL = 0;
  static final int OP_REMOVE = 3;
  static final int OP_REPLACE = 2;
  static final int OP_SHOW = 5;
  static final String TAG = "FragmentManager";
  boolean mAddToBackStack;
  boolean mAllowAddToBackStack = true;
  int mBreadCrumbShortTitleRes;
  CharSequence mBreadCrumbShortTitleText;
  int mBreadCrumbTitleRes;
  CharSequence mBreadCrumbTitleText;
  boolean mCommitted;
  int mEnterAnim;
  int mExitAnim;
  Op mHead;
  int mIndex = -1;
  final FragmentManagerImpl mManager;
  String mName;
  int mNumOp;
  int mPopEnterAnim;
  int mPopExitAnim;
  ArrayList<String> mSharedElementSourceNames;
  ArrayList<String> mSharedElementTargetNames;
  Op mTail;
  int mTransition;
  int mTransitionStyle;
  
  public BackStackRecord(FragmentManagerImpl paramFragmentManagerImpl)
  {
    this.mManager = paramFragmentManagerImpl;
  }
  
  private TransitionState beginTransition(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, boolean paramBoolean)
  {
    TransitionState localTransitionState = new TransitionState();
    localTransitionState.nonExistentView = new View(this.mManager.mActivity);
    int i = 0;
    for (int j = 0;; j++)
    {
      if (j >= paramSparseArray1.size()) {
        for (j = 0;; j++)
        {
          if (j >= paramSparseArray2.size())
          {
            if (i == 0) {
              localTransitionState = null;
            }
            return localTransitionState;
          }
          int k = paramSparseArray2.keyAt(j);
          if ((paramSparseArray1.get(k) == null) && (configureTransitions(k, localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2))) {
            i = 1;
          }
        }
      }
      if (configureTransitions(paramSparseArray1.keyAt(j), localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2)) {
        i = 1;
      }
    }
  }
  
  private void calculateFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (this.mManager.mContainer.hasView()) {}
    for (Op localOp = this.mHead;; localOp = localOp.next)
    {
      if (localOp == null) {
        return;
      }
      switch (localOp.cmd)
      {
      case 1: 
        setLastIn(paramSparseArray2, localOp.fragment);
        break;
      case 2: 
        Fragment localFragment1 = localOp.fragment;
        if (this.mManager.mAdded != null) {}
        for (int i = 0;; i++)
        {
          if (i >= this.mManager.mAdded.size())
          {
            setLastIn(paramSparseArray2, localFragment1);
            break;
          }
          Fragment localFragment2 = (Fragment)this.mManager.mAdded.get(i);
          if ((localFragment1 == null) || (localFragment2.mContainerId == localFragment1.mContainerId)) {
            if (localFragment2 != localFragment1) {
              setFirstOut(paramSparseArray1, localFragment2);
            } else {
              localFragment1 = null;
            }
          }
        }
      case 3: 
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
      case 4: 
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
      case 5: 
        setLastIn(paramSparseArray2, localOp.fragment);
        break;
      case 6: 
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
      case 7: 
        setLastIn(paramSparseArray2, localOp.fragment);
      }
    }
  }
  
  private void callSharedElementEnd(TransitionState paramTransitionState, Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean, ArrayMap<String, View> paramArrayMap)
  {
    SharedElementCallback localSharedElementCallback;
    if (!paramBoolean) {
      localSharedElementCallback = paramFragment1.mEnterTransitionCallback;
    } else {
      localSharedElementCallback = paramFragment2.mEnterTransitionCallback;
    }
    if (localSharedElementCallback != null) {
      localSharedElementCallback.onSharedElementEnd(new ArrayList(paramArrayMap.keySet()), new ArrayList(paramArrayMap.values()), null);
    }
  }
  
  private static Object captureExitingViews(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap)
  {
    if (paramObject != null) {
      paramObject = FragmentTransitionCompat21.captureExitingViews(paramObject, paramFragment.getView(), paramArrayList, paramArrayMap);
    }
    return paramObject;
  }
  
  private boolean configureTransitions(int paramInt, TransitionState paramTransitionState, boolean paramBoolean, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mManager.mContainer.findViewById(paramInt);
    boolean bool1;
    if (localViewGroup != null)
    {
      final Fragment localFragment = (Fragment)paramSparseArray2.get(paramInt);
      Object localObject4 = (Fragment)paramSparseArray1.get(paramInt);
      Object localObject1 = getEnterTransition(localFragment, paramBoolean);
      Object localObject2 = getSharedElementTransition(localFragment, (Fragment)localObject4, paramBoolean);
      Object localObject5 = getExitTransition((Fragment)localObject4, paramBoolean);
      if ((localObject1 != null) || (localObject2 != null) || (localObject5 != null))
      {
        Object localObject6 = null;
        ArrayList localArrayList1 = new ArrayList();
        if (localObject2 != null)
        {
          localObject6 = remapSharedElements(paramTransitionState, (Fragment)localObject4, paramBoolean);
          if (!((ArrayMap)localObject6).isEmpty()) {
            localArrayList1.addAll(((ArrayMap)localObject6).values());
          } else {
            localArrayList1.add(paramTransitionState.nonExistentView);
          }
          SharedElementCallback localSharedElementCallback;
          if (!paramBoolean) {
            localSharedElementCallback = localFragment.mEnterTransitionCallback;
          } else {
            localSharedElementCallback = ((Fragment)localObject4).mEnterTransitionCallback;
          }
          if (localSharedElementCallback != null)
          {
            localObject7 = new ArrayList(((ArrayMap)localObject6).keySet());
            localArrayList2 = new ArrayList(((ArrayMap)localObject6).values());
            localSharedElementCallback.onSharedElementStart((List)localObject7, localArrayList2, null);
          }
        }
        ArrayList localArrayList2 = new ArrayList();
        localObject5 = captureExitingViews(localObject5, (Fragment)localObject4, localArrayList2, (ArrayMap)localObject6);
        if ((this.mSharedElementTargetNames != null) && (localObject6 != null))
        {
          localObject7 = this.mSharedElementTargetNames.get(0);
          localObject6 = (View)((ArrayMap)localObject6).get(localObject7);
          if (localObject6 != null)
          {
            if (localObject5 != null) {
              FragmentTransitionCompat21.setEpicenter(localObject5, (View)localObject6);
            }
            if (localObject2 != null) {
              FragmentTransitionCompat21.setEpicenter(localObject2, (View)localObject6);
            }
          }
        }
        localObject6 = new FragmentTransitionCompat21.ViewRetriever()
        {
          public View getView()
          {
            return localFragment.getView();
          }
        };
        if (localObject2 != null) {
          prepareSharedElementTransition(paramTransitionState, localViewGroup, localObject2, localFragment, (Fragment)localObject4, paramBoolean, localArrayList1);
        }
        localObject4 = new ArrayList();
        Object localObject7 = new ArrayMap();
        boolean bool2;
        if (!paramBoolean) {
          bool2 = localFragment.getAllowEnterTransitionOverlap();
        } else {
          bool2 = bool2.getAllowReturnTransitionOverlap();
        }
        Object localObject3 = FragmentTransitionCompat21.mergeTransitions(localObject1, localObject5, localObject2, bool2);
        if (localObject3 != null)
        {
          FragmentTransitionCompat21.addTransitionTargets(localObject1, localObject2, localViewGroup, (FragmentTransitionCompat21.ViewRetriever)localObject6, paramTransitionState.nonExistentView, paramTransitionState.enteringEpicenterView, paramTransitionState.nameOverrides, (ArrayList)localObject4, (Map)localObject7, localArrayList1);
          excludeHiddenFragmentsAfterEnter(localViewGroup, paramTransitionState, paramInt, localObject3);
          FragmentTransitionCompat21.excludeTarget(localObject3, paramTransitionState.nonExistentView, true);
          excludeHiddenFragments(paramTransitionState, paramInt, localObject3);
          FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject3);
          FragmentTransitionCompat21.cleanupTransitions(localViewGroup, paramTransitionState.nonExistentView, localObject1, (ArrayList)localObject4, localObject5, localArrayList2, localObject2, localArrayList1, localObject3, paramTransitionState.hiddenFragmentViews, (Map)localObject7);
        }
        if (localObject3 == null) {
          bool1 = false;
        } else {
          bool1 = true;
        }
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
    return bool1;
  }
  
  private void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    paramFragment.mFragmentManager = this.mManager;
    if (paramString != null)
    {
      if ((paramFragment.mTag == null) || (paramString.equals(paramFragment.mTag))) {
        paramFragment.mTag = paramString;
      }
    }
    else
    {
      if (paramInt1 != 0)
      {
        if ((paramFragment.mFragmentId == 0) || (paramFragment.mFragmentId == paramInt1))
        {
          paramFragment.mFragmentId = paramInt1;
          paramFragment.mContainerId = paramInt1;
        }
      }
      else
      {
        Op localOp = new Op();
        localOp.cmd = paramInt2;
        localOp.fragment = paramFragment;
        addOp(localOp);
        return;
      }
      throw new IllegalStateException("Can't change container ID of fragment " + paramFragment + ": was " + paramFragment.mFragmentId + " now " + paramInt1);
    }
    throw new IllegalStateException("Can't change tag of fragment " + paramFragment + ": was " + paramFragment.mTag + " now " + paramString);
  }
  
  private void excludeHiddenFragments(TransitionState paramTransitionState, int paramInt, Object paramObject)
  {
    if (this.mManager.mAdded != null) {}
    for (int i = 0;; i++)
    {
      if (i >= this.mManager.mAdded.size()) {
        return;
      }
      Fragment localFragment = (Fragment)this.mManager.mAdded.get(i);
      if ((localFragment.mView != null) && (localFragment.mContainer != null) && (localFragment.mContainerId == paramInt)) {
        if (!localFragment.mHidden)
        {
          FragmentTransitionCompat21.excludeTarget(paramObject, localFragment.mView, false);
          paramTransitionState.hiddenFragmentViews.remove(localFragment.mView);
        }
        else if (!paramTransitionState.hiddenFragmentViews.contains(localFragment.mView))
        {
          FragmentTransitionCompat21.excludeTarget(paramObject, localFragment.mView, true);
          paramTransitionState.hiddenFragmentViews.add(localFragment.mView);
        }
      }
    }
  }
  
  private void excludeHiddenFragmentsAfterEnter(final View paramView, final TransitionState paramTransitionState, final int paramInt, final Object paramObject)
  {
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        BackStackRecord.this.excludeHiddenFragments(paramTransitionState, paramInt, paramObject);
        return true;
      }
    });
  }
  
  private static Object getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject;
    if (paramFragment != null)
    {
      if (!paramBoolean) {
        localObject = paramFragment.getEnterTransition();
      } else {
        localObject = paramFragment.getReenterTransition();
      }
      localObject = FragmentTransitionCompat21.cloneTransition(localObject);
    }
    else
    {
      localObject = null;
    }
    return localObject;
  }
  
  private static Object getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    Object localObject;
    if (paramFragment != null)
    {
      if (!paramBoolean) {
        localObject = paramFragment.getExitTransition();
      } else {
        localObject = paramFragment.getReturnTransition();
      }
      localObject = FragmentTransitionCompat21.cloneTransition(localObject);
    }
    else
    {
      localObject = null;
    }
    return localObject;
  }
  
  private static Object getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    Object localObject;
    if ((paramFragment1 != null) && (paramFragment2 != null))
    {
      if (!paramBoolean) {
        localObject = paramFragment1.getSharedElementEnterTransition();
      } else {
        localObject = paramFragment2.getSharedElementReturnTransition();
      }
      localObject = FragmentTransitionCompat21.cloneTransition(localObject);
    }
    else
    {
      localObject = null;
    }
    return localObject;
  }
  
  private ArrayMap<String, View> mapEnteringSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    View localView = paramFragment.getView();
    if ((localView != null) && (this.mSharedElementSourceNames != null))
    {
      FragmentTransitionCompat21.findNamedViews(localArrayMap, localView);
      if (!paramBoolean) {
        localArrayMap.retainAll(this.mSharedElementTargetNames);
      } else {
        localArrayMap = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, localArrayMap);
      }
    }
    return localArrayMap;
  }
  
  private ArrayMap<String, View> mapSharedElementsIn(TransitionState paramTransitionState, boolean paramBoolean, Fragment paramFragment)
  {
    ArrayMap localArrayMap = mapEnteringSharedElements(paramTransitionState, paramFragment, paramBoolean);
    if (!paramBoolean)
    {
      if (paramFragment.mEnterTransitionCallback != null) {
        paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      setNameOverrides(paramTransitionState, localArrayMap, true);
    }
    else
    {
      if (paramFragment.mExitTransitionCallback != null) {
        paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      setBackNameOverrides(paramTransitionState, localArrayMap, true);
    }
    return localArrayMap;
  }
  
  private void prepareSharedElementTransition(final TransitionState paramTransitionState, final View paramView, final Object paramObject, final Fragment paramFragment1, final Fragment paramFragment2, final boolean paramBoolean, final ArrayList<View> paramArrayList)
  {
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        if (paramObject != null)
        {
          FragmentTransitionCompat21.removeTargets(paramObject, paramArrayList);
          paramArrayList.clear();
          ArrayMap localArrayMap = BackStackRecord.this.mapSharedElementsIn(paramTransitionState, paramBoolean, paramFragment1);
          if (!localArrayMap.isEmpty()) {
            paramArrayList.addAll(localArrayMap.values());
          } else {
            paramArrayList.add(paramTransitionState.nonExistentView);
          }
          FragmentTransitionCompat21.addTargets(paramObject, paramArrayList);
          BackStackRecord.this.setEpicenterIn(localArrayMap, paramTransitionState);
          BackStackRecord.this.callSharedElementEnd(paramTransitionState, paramFragment1, paramFragment2, paramBoolean, localArrayMap);
        }
        return true;
      }
    });
  }
  
  private static ArrayMap<String, View> remapNames(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayMap<String, View> paramArrayMap)
  {
    ArrayMap localArrayMap;
    int i;
    if (!paramArrayMap.isEmpty())
    {
      localArrayMap = new ArrayMap();
      i = paramArrayList1.size();
    }
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        paramArrayMap = localArrayMap;
        return paramArrayMap;
      }
      View localView = (View)paramArrayMap.get(paramArrayList1.get(j));
      if (localView != null) {
        localArrayMap.put(paramArrayList2.get(j), localView);
      }
    }
  }
  
  private ArrayMap<String, View> remapSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if (this.mSharedElementSourceNames != null)
    {
      FragmentTransitionCompat21.findNamedViews(localArrayMap, paramFragment.getView());
      if (!paramBoolean) {
        localArrayMap = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, localArrayMap);
      } else {
        localArrayMap.retainAll(this.mSharedElementTargetNames);
      }
    }
    if (!paramBoolean)
    {
      if (paramFragment.mExitTransitionCallback != null) {
        paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      setNameOverrides(paramTransitionState, localArrayMap, false);
    }
    else
    {
      if (paramFragment.mEnterTransitionCallback != null) {
        paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      setBackNameOverrides(paramTransitionState, localArrayMap, false);
    }
    return localArrayMap;
  }
  
  private void setBackNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int j;
    if (this.mSharedElementTargetNames != null) {
      j = this.mSharedElementTargetNames.size();
    } else {
      j = 0;
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return;
      }
      String str = (String)this.mSharedElementSourceNames.get(i);
      Object localObject = (View)paramArrayMap.get((String)this.mSharedElementTargetNames.get(i));
      if (localObject != null)
      {
        localObject = FragmentTransitionCompat21.getTransitionName((View)localObject);
        if (!paramBoolean) {
          setNameOverride(paramTransitionState.nameOverrides, (String)localObject, str);
        } else {
          setNameOverride(paramTransitionState.nameOverrides, str, (String)localObject);
        }
      }
    }
  }
  
  private void setEpicenterIn(ArrayMap<String, View> paramArrayMap, TransitionState paramTransitionState)
  {
    if ((this.mSharedElementTargetNames != null) && (!paramArrayMap.isEmpty()))
    {
      View localView = (View)paramArrayMap.get(this.mSharedElementTargetNames.get(0));
      if (localView != null) {
        paramTransitionState.enteringEpicenterView.epicenter = localView;
      }
    }
  }
  
  private static void setFirstOut(SparseArray<Fragment> paramSparseArray, Fragment paramFragment)
  {
    if (paramFragment != null)
    {
      int i = paramFragment.mContainerId;
      if ((i != 0) && (!paramFragment.isHidden()) && (paramFragment.isAdded()) && (paramFragment.getView() != null) && (paramSparseArray.get(i) == null)) {
        paramSparseArray.put(i, paramFragment);
      }
    }
  }
  
  private void setLastIn(SparseArray<Fragment> paramSparseArray, Fragment paramFragment)
  {
    if (paramFragment != null)
    {
      int i = paramFragment.mContainerId;
      if (i != 0) {
        paramSparseArray.put(i, paramFragment);
      }
    }
  }
  
  private static void setNameOverride(ArrayMap<String, String> paramArrayMap, String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null) && (!paramString1.equals(paramString2)))
    {
      for (int i = 0;; i++)
      {
        if (i >= paramArrayMap.size())
        {
          paramArrayMap.put(paramString1, paramString2);
          return;
        }
        if (paramString1.equals(paramArrayMap.valueAt(i))) {
          break;
        }
      }
      paramArrayMap.setValueAt(i, paramString2);
    }
  }
  
  private void setNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int j = paramArrayMap.size();
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return;
      }
      String str1 = (String)paramArrayMap.keyAt(i);
      String str2 = FragmentTransitionCompat21.getTransitionName((View)paramArrayMap.valueAt(i));
      if (!paramBoolean) {
        setNameOverride(paramTransitionState.nameOverrides, str2, str1);
      } else {
        setNameOverride(paramTransitionState.nameOverrides, str1, str2);
      }
    }
  }
  
  private static void setNameOverrides(TransitionState paramTransitionState, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    if (paramArrayList1 != null) {}
    for (int i = 0;; i++)
    {
      if (i >= paramArrayList1.size()) {
        return;
      }
      String str1 = (String)paramArrayList1.get(i);
      String str2 = (String)paramArrayList2.get(i);
      setNameOverride(paramTransitionState.nameOverrides, str1, str2);
    }
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment)
  {
    doAddOp(paramInt, paramFragment, null, 1);
    return this;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment, String paramString)
  {
    doAddOp(paramInt, paramFragment, paramString, 1);
    return this;
  }
  
  public FragmentTransaction add(Fragment paramFragment, String paramString)
  {
    doAddOp(0, paramFragment, paramString, 1);
    return this;
  }
  
  void addOp(Op paramOp)
  {
    if (this.mHead != null)
    {
      paramOp.prev = this.mTail;
      this.mTail.next = paramOp;
      this.mTail = paramOp;
    }
    else
    {
      this.mTail = paramOp;
      this.mHead = paramOp;
    }
    paramOp.enterAnim = this.mEnterAnim;
    paramOp.exitAnim = this.mExitAnim;
    paramOp.popEnterAnim = this.mPopEnterAnim;
    paramOp.popExitAnim = this.mPopExitAnim;
    this.mNumOp = (1 + this.mNumOp);
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      String str = FragmentTransitionCompat21.getTransitionName(paramView);
      if (str != null)
      {
        if (this.mSharedElementSourceNames == null)
        {
          this.mSharedElementSourceNames = new ArrayList();
          this.mSharedElementTargetNames = new ArrayList();
        }
        this.mSharedElementSourceNames.add(str);
        this.mSharedElementTargetNames.add(paramString);
      }
    }
    else
    {
      return this;
    }
    throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
  }
  
  public FragmentTransaction addToBackStack(String paramString)
  {
    if (this.mAllowAddToBackStack)
    {
      this.mAddToBackStack = true;
      this.mName = paramString;
      return this;
    }
    throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
  }
  
  public FragmentTransaction attach(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 7;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  void bumpBackStackNesting(int paramInt)
  {
    Op localOp;
    if (this.mAddToBackStack)
    {
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Bump nesting in " + this + " by " + paramInt);
      }
      localOp = this.mHead;
      if (localOp != null) {}
    }
    else
    {
      return;
    }
    Fragment localFragment;
    if (localOp.fragment != null)
    {
      localFragment = localOp.fragment;
      localFragment.mBackStackNesting = (paramInt + localFragment.mBackStackNesting);
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Bump nesting of " + localOp.fragment + " to " + localOp.fragment.mBackStackNesting);
      }
    }
    if (localOp.removed != null) {}
    for (int i = -1 + localOp.removed.size();; i--)
    {
      if (i < 0)
      {
        localOp = localOp.next;
        break;
      }
      localFragment = (Fragment)localOp.removed.get(i);
      localFragment.mBackStackNesting = (paramInt + localFragment.mBackStackNesting);
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Bump nesting of " + localFragment + " to " + localFragment.mBackStackNesting);
      }
    }
  }
  
  public void calculateBackFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (this.mManager.mContainer.hasView()) {}
    for (Op localOp = this.mHead;; localOp = localOp.next)
    {
      if (localOp == null) {
        return;
      }
      switch (localOp.cmd)
      {
      case 1: 
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
      case 2: 
        if (localOp.removed != null) {}
        for (int i = -1 + localOp.removed.size();; i--)
        {
          if (i < 0)
          {
            setFirstOut(paramSparseArray1, localOp.fragment);
            break;
          }
          setLastIn(paramSparseArray2, (Fragment)localOp.removed.get(i));
        }
      case 3: 
        setLastIn(paramSparseArray2, localOp.fragment);
        break;
      case 4: 
        setLastIn(paramSparseArray2, localOp.fragment);
        break;
      case 5: 
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
      case 6: 
        setLastIn(paramSparseArray2, localOp.fragment);
        break;
      case 7: 
        setFirstOut(paramSparseArray1, localOp.fragment);
      }
    }
  }
  
  public int commit()
  {
    return commitInternal(false);
  }
  
  public int commitAllowingStateLoss()
  {
    return commitInternal(true);
  }
  
  int commitInternal(boolean paramBoolean)
  {
    if (!this.mCommitted)
    {
      if (FragmentManagerImpl.DEBUG)
      {
        Log.v("FragmentManager", "Commit: " + this);
        dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
      }
      this.mCommitted = true;
      if (!this.mAddToBackStack) {
        this.mIndex = -1;
      } else {
        this.mIndex = this.mManager.allocBackStackIndex(this);
      }
      this.mManager.enqueueAction(this, paramBoolean);
      return this.mIndex;
    }
    throw new IllegalStateException("commit already called");
  }
  
  public FragmentTransaction detach(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 6;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction disallowAddToBackStack()
  {
    if (!this.mAddToBackStack)
    {
      this.mAllowAddToBackStack = false;
      return this;
    }
    throw new IllegalStateException("This transaction is already being added to the back stack");
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dump(paramString, paramPrintWriter, true);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(this.mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(this.mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(this.mCommitted);
      if (this.mTransition != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(this.mTransition));
        paramPrintWriter.print(" mTransitionStyle=#");
        paramPrintWriter.println(Integer.toHexString(this.mTransitionStyle));
      }
      if ((this.mEnterAnim != 0) || (this.mExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mExitAnim));
      }
      if ((this.mPopEnterAnim != 0) || (this.mPopExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mPopExitAnim));
      }
      if ((this.mBreadCrumbTitleRes != 0) || (this.mBreadCrumbTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(this.mBreadCrumbTitleText);
      }
      if ((this.mBreadCrumbShortTitleRes != 0) || (this.mBreadCrumbShortTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(this.mBreadCrumbShortTitleText);
      }
    }
    String str1;
    Op localOp;
    int i;
    if (this.mHead != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      str1 = paramString + "    ";
      localOp = this.mHead;
      i = 0;
      if (localOp != null) {}
    }
    else
    {
      return;
    }
    String str2;
    switch (localOp.cmd)
    {
    default: 
      str2 = "cmd=" + localOp.cmd;
      break;
    case 0: 
      str2 = "NULL";
      break;
    case 1: 
      str2 = "ADD";
      break;
    case 2: 
      str2 = "REPLACE";
      break;
    case 3: 
      str2 = "REMOVE";
      break;
    case 4: 
      str2 = "HIDE";
      break;
    case 5: 
      str2 = "SHOW";
      break;
    case 6: 
      str2 = "DETACH";
      break;
    case 7: 
      str2 = "ATTACH";
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Op #");
    paramPrintWriter.print(i);
    paramPrintWriter.print(": ");
    paramPrintWriter.print(str2);
    paramPrintWriter.print(" ");
    paramPrintWriter.println(localOp.fragment);
    if (paramBoolean)
    {
      if ((localOp.enterAnim != 0) || (localOp.exitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("enterAnim=#");
        paramPrintWriter.print(Integer.toHexString(localOp.enterAnim));
        paramPrintWriter.print(" exitAnim=#");
        paramPrintWriter.println(Integer.toHexString(localOp.exitAnim));
      }
      if ((localOp.popEnterAnim != 0) || (localOp.popExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("popEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(localOp.popEnterAnim));
        paramPrintWriter.print(" popExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(localOp.popExitAnim));
      }
    }
    if ((localOp.removed != null) && (localOp.removed.size() > 0)) {}
    for (int j = 0;; j++)
    {
      if (j >= localOp.removed.size())
      {
        localOp = localOp.next;
        i++;
        break;
      }
      paramPrintWriter.print(str1);
      if (localOp.removed.size() != 1)
      {
        if (j == 0) {
          paramPrintWriter.println("Removed:");
        }
        paramPrintWriter.print(str1);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(j);
        paramPrintWriter.print(": ");
      }
      else
      {
        paramPrintWriter.print("Removed: ");
      }
      paramPrintWriter.println(localOp.removed.get(j));
    }
  }
  
  public CharSequence getBreadCrumbShortTitle()
  {
    CharSequence localCharSequence;
    if (this.mBreadCrumbShortTitleRes == 0) {
      localCharSequence = this.mBreadCrumbShortTitleText;
    } else {
      localCharSequence = this.mManager.mActivity.getText(this.mBreadCrumbShortTitleRes);
    }
    return localCharSequence;
  }
  
  public int getBreadCrumbShortTitleRes()
  {
    return this.mBreadCrumbShortTitleRes;
  }
  
  public CharSequence getBreadCrumbTitle()
  {
    CharSequence localCharSequence;
    if (this.mBreadCrumbTitleRes == 0) {
      localCharSequence = this.mBreadCrumbTitleText;
    } else {
      localCharSequence = this.mManager.mActivity.getText(this.mBreadCrumbTitleRes);
    }
    return localCharSequence;
  }
  
  public int getBreadCrumbTitleRes()
  {
    return this.mBreadCrumbTitleRes;
  }
  
  public int getId()
  {
    return this.mIndex;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getTransition()
  {
    return this.mTransition;
  }
  
  public int getTransitionStyle()
  {
    return this.mTransitionStyle;
  }
  
  public FragmentTransaction hide(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 4;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public boolean isAddToBackStackAllowed()
  {
    return this.mAllowAddToBackStack;
  }
  
  public boolean isEmpty()
  {
    boolean bool;
    if (this.mNumOp != 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public TransitionState popFromBackStack(boolean paramBoolean, TransitionState paramTransitionState, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (FragmentManagerImpl.DEBUG)
    {
      Log.v("FragmentManager", "popFromBackStack: " + this);
      dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
    }
    if (paramTransitionState != null)
    {
      if (!paramBoolean)
      {
        ArrayList localArrayList1 = this.mSharedElementTargetNames;
        ArrayList localArrayList2 = this.mSharedElementSourceNames;
        setNameOverrides(paramTransitionState, localArrayList1, localArrayList2);
      }
    }
    else if ((paramSparseArray1.size() != 0) || (paramSparseArray2.size() != 0)) {
      paramTransitionState = beginTransition(paramSparseArray1, paramSparseArray2, true);
    }
    bumpBackStackNesting(-1);
    int i;
    if (paramTransitionState == null) {
      i = this.mTransitionStyle;
    } else {
      i = 0;
    }
    int j;
    if (paramTransitionState == null) {
      j = this.mTransition;
    } else {
      j = 0;
    }
    for (Op localOp = this.mTail;; localOp = localOp.prev)
    {
      if (localOp == null)
      {
        if (paramBoolean)
        {
          this.mManager.moveToState(this.mManager.mCurState, FragmentManagerImpl.reverseTransit(j), i, true);
          paramTransitionState = null;
        }
        if (this.mIndex >= 0)
        {
          this.mManager.freeBackStackIndex(this.mIndex);
          this.mIndex = -1;
        }
        return paramTransitionState;
      }
      int k;
      if (paramTransitionState == null) {
        k = localOp.popEnterAnim;
      } else {
        k = 0;
      }
      int m;
      if (paramTransitionState == null) {
        m = localOp.popExitAnim;
      } else {
        m = 0;
      }
      Fragment localFragment1;
      Fragment localFragment3;
      switch (localOp.cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + localOp.cmd);
      case 1: 
        localFragment1 = localOp.fragment;
        localFragment1.mNextAnim = m;
        this.mManager.removeFragment(localFragment1, FragmentManagerImpl.reverseTransit(j), i);
        break;
      case 2: 
        localFragment3 = localOp.fragment;
        if (localFragment3 != null)
        {
          localFragment3.mNextAnim = m;
          this.mManager.removeFragment(localFragment3, FragmentManagerImpl.reverseTransit(j), i);
        }
        if (localOp.removed != null) {
          m = 0;
        }
        break;
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        while (m < localOp.removed.size())
        {
          localFragment3 = (Fragment)localOp.removed.get(m);
          localFragment3.mNextAnim = localFragment1;
          this.mManager.addFragment(localFragment3, false);
          m++;
          continue;
          Fragment localFragment2 = localOp.fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.addFragment(localFragment2, false);
          break;
          localFragment2 = localOp.fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.showFragment(localFragment2, FragmentManagerImpl.reverseTransit(j), i);
          break;
          localFragment1 = localOp.fragment;
          localFragment1.mNextAnim = localFragment2;
          this.mManager.hideFragment(localFragment1, FragmentManagerImpl.reverseTransit(j), i);
          break;
          localFragment2 = localOp.fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.attachFragment(localFragment2, FragmentManagerImpl.reverseTransit(j), i);
          break;
          localFragment2 = localOp.fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.detachFragment(localFragment2, FragmentManagerImpl.reverseTransit(j), i);
        }
      }
    }
  }
  
  public FragmentTransaction remove(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 3;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment)
  {
    return replace(paramInt, paramFragment, null);
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment, String paramString)
  {
    if (paramInt != 0)
    {
      doAddOp(paramInt, paramFragment, paramString, 2);
      return this;
    }
    throw new IllegalArgumentException("Must use non-zero containerViewId");
  }
  
  public void run()
  {
    if (FragmentManagerImpl.DEBUG) {
      Log.v("FragmentManager", "Run: " + this);
    }
    if ((!this.mAddToBackStack) || (this.mIndex >= 0))
    {
      bumpBackStackNesting(1);
      TransitionState localTransitionState = null;
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject = new SparseArray();
        SparseArray localSparseArray = new SparseArray();
        calculateFragments((SparseArray)localObject, localSparseArray);
        localTransitionState = beginTransition((SparseArray)localObject, localSparseArray, false);
      }
      int j;
      if (localTransitionState == null) {
        j = this.mTransitionStyle;
      } else {
        j = 0;
      }
      int i;
      if (localTransitionState == null) {
        i = this.mTransition;
      } else {
        i = 0;
      }
      for (Object localObject = this.mHead;; localObject = ((Op)localObject).next)
      {
        if (localObject == null)
        {
          this.mManager.moveToState(this.mManager.mCurState, i, j, true);
          if (this.mAddToBackStack) {
            this.mManager.addBackStackState(this);
          }
          return;
        }
        int k;
        if (localTransitionState == null) {
          k = ((Op)localObject).enterAnim;
        } else {
          k = 0;
        }
        int m;
        if (localTransitionState == null) {
          m = ((Op)localObject).exitAnim;
        } else {
          m = 0;
        }
        Fragment localFragment2;
        Fragment localFragment1;
        switch (((Op)localObject).cmd)
        {
        default: 
          throw new IllegalArgumentException("Unknown cmd: " + ((Op)localObject).cmd);
        case 1: 
          localFragment2 = ((Op)localObject).fragment;
          localFragment2.mNextAnim = k;
          this.mManager.addFragment(localFragment2, false);
          break;
        case 2: 
          Fragment localFragment3 = ((Op)localObject).fragment;
          if (this.mManager.mAdded != null) {}
          for (int n = 0;; n++)
          {
            if (n >= this.mManager.mAdded.size())
            {
              if (localFragment3 == null) {
                break;
              }
              localFragment3.mNextAnim = k;
              this.mManager.addFragment(localFragment3, false);
              break;
            }
            Fragment localFragment4 = (Fragment)this.mManager.mAdded.get(n);
            if (FragmentManagerImpl.DEBUG) {
              Log.v("FragmentManager", "OP_REPLACE: adding=" + localFragment3 + " old=" + localFragment4);
            }
            if ((localFragment3 == null) || (localFragment4.mContainerId == localFragment3.mContainerId)) {
              if (localFragment4 != localFragment3)
              {
                if (((Op)localObject).removed == null) {
                  ((Op)localObject).removed = new ArrayList();
                }
                ((Op)localObject).removed.add(localFragment4);
                localFragment4.mNextAnim = localFragment2;
                if (this.mAddToBackStack)
                {
                  localFragment4.mBackStackNesting = (1 + localFragment4.mBackStackNesting);
                  if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Bump nesting of " + localFragment4 + " to " + localFragment4.mBackStackNesting);
                  }
                }
                this.mManager.removeFragment(localFragment4, i, j);
              }
              else
              {
                localFragment3 = null;
                ((Op)localObject).fragment = null;
              }
            }
          }
        case 3: 
          localFragment1 = ((Op)localObject).fragment;
          localFragment1.mNextAnim = localFragment2;
          this.mManager.removeFragment(localFragment1, i, j);
          break;
        case 4: 
          localFragment1 = ((Op)localObject).fragment;
          localFragment1.mNextAnim = localFragment2;
          this.mManager.hideFragment(localFragment1, i, j);
          break;
        case 5: 
          localFragment2 = ((Op)localObject).fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.showFragment(localFragment2, i, j);
          break;
        case 6: 
          localFragment1 = ((Op)localObject).fragment;
          localFragment1.mNextAnim = localFragment2;
          this.mManager.detachFragment(localFragment1, i, j);
          break;
        case 7: 
          localFragment2 = ((Op)localObject).fragment;
          localFragment2.mNextAnim = localFragment1;
          this.mManager.attachFragment(localFragment2, i, j);
        }
      }
    }
    throw new IllegalStateException("addToBackStack() called after commit()");
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(int paramInt)
  {
    this.mBreadCrumbShortTitleRes = paramInt;
    this.mBreadCrumbShortTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(CharSequence paramCharSequence)
  {
    this.mBreadCrumbShortTitleRes = 0;
    this.mBreadCrumbShortTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(int paramInt)
  {
    this.mBreadCrumbTitleRes = paramInt;
    this.mBreadCrumbTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(CharSequence paramCharSequence)
  {
    this.mBreadCrumbTitleRes = 0;
    this.mBreadCrumbTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2)
  {
    return setCustomAnimations(paramInt1, paramInt2, 0, 0);
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mEnterAnim = paramInt1;
    this.mExitAnim = paramInt2;
    this.mPopEnterAnim = paramInt3;
    this.mPopExitAnim = paramInt4;
    return this;
  }
  
  public FragmentTransaction setTransition(int paramInt)
  {
    this.mTransition = paramInt;
    return this;
  }
  
  public FragmentTransaction setTransitionStyle(int paramInt)
  {
    this.mTransitionStyle = paramInt;
    return this;
  }
  
  public FragmentTransaction show(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 5;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("BackStackEntry{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    if (this.mIndex >= 0)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mIndex);
    }
    if (this.mName != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mName);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public class TransitionState
  {
    public FragmentTransitionCompat21.EpicenterView enteringEpicenterView = new FragmentTransitionCompat21.EpicenterView();
    public ArrayList<View> hiddenFragmentViews = new ArrayList();
    public ArrayMap<String, String> nameOverrides = new ArrayMap();
    public View nonExistentView;
    
    public TransitionState() {}
  }
  
  static final class Op
  {
    int cmd;
    int enterAnim;
    int exitAnim;
    Fragment fragment;
    Op next;
    int popEnterAnim;
    int popExitAnim;
    Op prev;
    ArrayList<Fragment> removed;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\BackStackRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */