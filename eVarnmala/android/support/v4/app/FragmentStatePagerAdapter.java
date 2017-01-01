package android.support.v4.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FragmentStatePagerAdapter
  extends PagerAdapter
{
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentStatePagerAdapter";
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private final FragmentManager mFragmentManager;
  private ArrayList<Fragment> mFragments = new ArrayList();
  private ArrayList<Fragment.SavedState> mSavedState = new ArrayList();
  
  public FragmentStatePagerAdapter(FragmentManager paramFragmentManager)
  {
    this.mFragmentManager = paramFragmentManager;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    Fragment localFragment = (Fragment)paramObject;
    if (this.mCurTransaction == null) {
      this.mCurTransaction = this.mFragmentManager.beginTransaction();
    }
    for (;;)
    {
      if (this.mSavedState.size() > paramInt)
      {
        this.mSavedState.set(paramInt, this.mFragmentManager.saveFragmentInstanceState(localFragment));
        this.mFragments.set(paramInt, null);
        this.mCurTransaction.remove(localFragment);
        return;
      }
      this.mSavedState.add(null);
    }
  }
  
  public void finishUpdate(ViewGroup paramViewGroup)
  {
    if (this.mCurTransaction != null)
    {
      this.mCurTransaction.commitAllowingStateLoss();
      this.mCurTransaction = null;
      this.mFragmentManager.executePendingTransactions();
    }
  }
  
  public abstract Fragment getItem(int paramInt);
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    Object localObject;
    Fragment localFragment;
    if (this.mFragments.size() > paramInt)
    {
      localObject = (Fragment)this.mFragments.get(paramInt);
      if (localObject != null) {}
    }
    else
    {
      if (this.mCurTransaction == null) {
        this.mCurTransaction = this.mFragmentManager.beginTransaction();
      }
      localFragment = getItem(paramInt);
      if (this.mSavedState.size() > paramInt)
      {
        localObject = (Fragment.SavedState)this.mSavedState.get(paramInt);
        if (localObject != null) {
          localFragment.setInitialSavedState((Fragment.SavedState)localObject);
        }
      }
    }
    for (;;)
    {
      if (this.mFragments.size() > paramInt)
      {
        localFragment.setMenuVisibility(false);
        localFragment.setUserVisibleHint(false);
        this.mFragments.set(paramInt, localFragment);
        this.mCurTransaction.add(paramViewGroup.getId(), localFragment);
        localObject = localFragment;
        return localObject;
      }
      this.mFragments.add(null);
    }
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    boolean bool;
    if (((Fragment)paramObject).getView() != paramView) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader)
  {
    Bundle localBundle;
    Object localObject;
    if (paramParcelable != null)
    {
      localBundle = (Bundle)paramParcelable;
      localBundle.setClassLoader(paramClassLoader);
      localObject = localBundle.getParcelableArray("states");
      this.mSavedState.clear();
      this.mFragments.clear();
      if (localObject == null) {}
    }
    Iterator localIterator;
    for (int i = 0;; localIterator++)
    {
      if (i >= localObject.length)
      {
        localIterator = localBundle.keySet().iterator();
        int j;
        for (;;)
        {
          if (!localIterator.hasNext()) {
            return;
          }
          String str = (String)localIterator.next();
          if (str.startsWith("f"))
          {
            j = Integer.parseInt(str.substring(1));
            localObject = this.mFragmentManager.getFragment(localBundle, str);
            if (localObject != null) {
              break;
            }
            Log.w("FragmentStatePagerAdapter", "Bad fragment at key " + str);
          }
        }
        for (;;)
        {
          if (this.mFragments.size() > j)
          {
            ((Fragment)localObject).setMenuVisibility(false);
            this.mFragments.set(j, localObject);
            break;
          }
          this.mFragments.add(null);
        }
      }
      this.mSavedState.add((Fragment.SavedState)localObject[localIterator]);
    }
  }
  
  public Parcelable saveState()
  {
    Bundle localBundle = null;
    if (this.mSavedState.size() > 0)
    {
      localBundle = new Bundle();
      Fragment.SavedState[] arrayOfSavedState = new Fragment.SavedState[this.mSavedState.size()];
      this.mSavedState.toArray(arrayOfSavedState);
      localBundle.putParcelableArray("states", arrayOfSavedState);
    }
    for (int i = 0;; i++)
    {
      if (i >= this.mFragments.size()) {
        return localBundle;
      }
      Fragment localFragment = (Fragment)this.mFragments.get(i);
      if (localFragment != null)
      {
        if (localBundle == null) {
          localBundle = new Bundle();
        }
        String str = "f" + i;
        this.mFragmentManager.putFragment(localBundle, str, localFragment);
      }
    }
  }
  
  public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    Fragment localFragment = (Fragment)paramObject;
    if (localFragment != this.mCurrentPrimaryItem)
    {
      if (this.mCurrentPrimaryItem != null)
      {
        this.mCurrentPrimaryItem.setMenuVisibility(false);
        this.mCurrentPrimaryItem.setUserVisibleHint(false);
      }
      if (localFragment != null)
      {
        localFragment.setMenuVisibility(true);
        localFragment.setUserVisibleHint(true);
      }
      this.mCurrentPrimaryItem = localFragment;
    }
  }
  
  public void startUpdate(ViewGroup paramViewGroup) {}
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\FragmentStatePagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */