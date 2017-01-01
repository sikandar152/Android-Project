package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

final class BackStackState
  implements Parcelable
{
  public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator()
  {
    public BackStackState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BackStackState(paramAnonymousParcel);
    }
    
    public BackStackState[] newArray(int paramAnonymousInt)
    {
      return new BackStackState[paramAnonymousInt];
    }
  };
  final int mBreadCrumbShortTitleRes;
  final CharSequence mBreadCrumbShortTitleText;
  final int mBreadCrumbTitleRes;
  final CharSequence mBreadCrumbTitleText;
  final int mIndex;
  final String mName;
  final int[] mOps;
  final ArrayList<String> mSharedElementSourceNames;
  final ArrayList<String> mSharedElementTargetNames;
  final int mTransition;
  final int mTransitionStyle;
  
  public BackStackState(Parcel paramParcel)
  {
    this.mOps = paramParcel.createIntArray();
    this.mTransition = paramParcel.readInt();
    this.mTransitionStyle = paramParcel.readInt();
    this.mName = paramParcel.readString();
    this.mIndex = paramParcel.readInt();
    this.mBreadCrumbTitleRes = paramParcel.readInt();
    this.mBreadCrumbTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mBreadCrumbShortTitleRes = paramParcel.readInt();
    this.mBreadCrumbShortTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mSharedElementSourceNames = paramParcel.createStringArrayList();
    this.mSharedElementTargetNames = paramParcel.createStringArrayList();
  }
  
  public BackStackState(FragmentManagerImpl paramFragmentManagerImpl, BackStackRecord paramBackStackRecord)
  {
    int i = 0;
    int[] arrayOfInt2;
    BackStackRecord.Op localOp2;
    for (Object localObject = paramBackStackRecord.mHead;; localOp2 = arrayOfInt2.next)
    {
      BackStackRecord.Op localOp1;
      if (localObject == null)
      {
        this.mOps = new int[i + 7 * paramBackStackRecord.mNumOp];
        if (paramBackStackRecord.mAddToBackStack)
        {
          localOp1 = paramBackStackRecord.mHead;
          int i1 = 0;
          if (localOp1 == null)
          {
            this.mTransition = paramBackStackRecord.mTransition;
            this.mTransitionStyle = paramBackStackRecord.mTransitionStyle;
            this.mName = paramBackStackRecord.mName;
            this.mIndex = paramBackStackRecord.mIndex;
            this.mBreadCrumbTitleRes = paramBackStackRecord.mBreadCrumbTitleRes;
            this.mBreadCrumbTitleText = paramBackStackRecord.mBreadCrumbTitleText;
            this.mBreadCrumbShortTitleRes = paramBackStackRecord.mBreadCrumbShortTitleRes;
            this.mBreadCrumbShortTitleText = paramBackStackRecord.mBreadCrumbShortTitleText;
            this.mSharedElementSourceNames = paramBackStackRecord.mSharedElementSourceNames;
            this.mSharedElementTargetNames = paramBackStackRecord.mSharedElementTargetNames;
            return;
          }
          localObject = this.mOps;
          int m = i1 + 1;
          localObject[i1] = localOp1.cmd;
          int[] arrayOfInt4 = this.mOps;
          int k = m + 1;
          int i3;
          if (localOp1.fragment == null) {
            i3 = -1;
          } else {
            i3 = localOp1.fragment.mIndex;
          }
          arrayOfInt4[m] = i3;
          arrayOfInt4 = this.mOps;
          m = k + 1;
          arrayOfInt4[k] = localOp1.enterAnim;
          arrayOfInt4 = this.mOps;
          k = m + 1;
          arrayOfInt4[m] = localOp1.exitAnim;
          int[] arrayOfInt3 = this.mOps;
          int i2 = k + 1;
          arrayOfInt3[k] = localOp1.popEnterAnim;
          int[] arrayOfInt1 = this.mOps;
          int n = i2 + 1;
          arrayOfInt1[i2] = localOp1.popExitAnim;
          int[] arrayOfInt6;
          if (localOp1.removed == null)
          {
            int[] arrayOfInt5 = this.mOps;
            arrayOfInt2 = n + 1;
            arrayOfInt5[n] = 0;
          }
          else
          {
            arrayOfInt2 = localOp1.removed.size();
            arrayOfInt8 = this.mOps;
            arrayOfInt6 = n + 1;
            arrayOfInt8[n] = arrayOfInt2;
            n = 0;
          }
          int[] arrayOfInt9;
          for (int[] arrayOfInt8 = arrayOfInt6;; arrayOfInt8 = arrayOfInt9)
          {
            if (n >= arrayOfInt2)
            {
              arrayOfInt2 = arrayOfInt8;
              localOp1 = localOp1.next;
              arrayOfInt6 = arrayOfInt2;
              break;
            }
            int[] arrayOfInt7 = this.mOps;
            arrayOfInt9 = arrayOfInt8 + 1;
            arrayOfInt7[arrayOfInt8] = ((Fragment)localOp1.removed.get(n)).mIndex;
            n++;
          }
        }
        throw new IllegalStateException("Not on back stack");
      }
      if (arrayOfInt2.removed != null)
      {
        int j;
        localOp1 += arrayOfInt2.removed.size();
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BackStackRecord instantiate(FragmentManagerImpl paramFragmentManagerImpl)
  {
    BackStackRecord localBackStackRecord = new BackStackRecord(paramFragmentManagerImpl);
    int j = 0;
    int i = 0;
    if (j >= this.mOps.length)
    {
      localBackStackRecord.mTransition = this.mTransition;
      localBackStackRecord.mTransitionStyle = this.mTransitionStyle;
      localBackStackRecord.mName = this.mName;
      localBackStackRecord.mIndex = this.mIndex;
      localBackStackRecord.mAddToBackStack = true;
      localBackStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
      localBackStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
      localBackStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
      localBackStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
      localBackStackRecord.mSharedElementSourceNames = this.mSharedElementSourceNames;
      localBackStackRecord.mSharedElementTargetNames = this.mSharedElementTargetNames;
      localBackStackRecord.bumpBackStackNesting(1);
      return localBackStackRecord;
    }
    BackStackRecord.Op localOp = new BackStackRecord.Op();
    int[] arrayOfInt3 = this.mOps;
    int m = j + 1;
    localOp.cmd = arrayOfInt3[j];
    if (FragmentManagerImpl.DEBUG) {
      Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " op #" + i + " base fragment #" + this.mOps[m]);
    }
    arrayOfInt3 = this.mOps;
    j = m + 1;
    m = arrayOfInt3[m];
    if (m < 0) {
      localOp.fragment = null;
    } else {
      localOp.fragment = ((Fragment)paramFragmentManagerImpl.mActive.get(m));
    }
    int[] arrayOfInt2 = this.mOps;
    int i1 = j + 1;
    localOp.enterAnim = arrayOfInt2[j];
    int[] arrayOfInt1 = this.mOps;
    int n = i1 + 1;
    localOp.exitAnim = arrayOfInt1[i1];
    int[] arrayOfInt4 = this.mOps;
    int k = n + 1;
    localOp.popEnterAnim = arrayOfInt4[n];
    arrayOfInt4 = this.mOps;
    n = k + 1;
    localOp.popExitAnim = arrayOfInt4[k];
    arrayOfInt4 = this.mOps;
    k = n + 1;
    int i2 = arrayOfInt4[n];
    if (i2 > 0)
    {
      localOp.removed = new ArrayList(i2);
      n = 0;
    }
    for (;;)
    {
      if (n >= i2)
      {
        k = k;
        localBackStackRecord.addOp(localOp);
        i++;
        break;
      }
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " set remove fragment #" + this.mOps[k]);
      }
      ArrayList localArrayList = paramFragmentManagerImpl.mActive;
      int[] arrayOfInt5 = this.mOps;
      Fragment localFragment2 = k + 1;
      Fragment localFragment1 = (Fragment)localArrayList.get(arrayOfInt5[k]);
      localOp.removed.add(localFragment1);
      n++;
      localFragment1 = localFragment2;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeIntArray(this.mOps);
    paramParcel.writeInt(this.mTransition);
    paramParcel.writeInt(this.mTransitionStyle);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mIndex);
    paramParcel.writeInt(this.mBreadCrumbTitleRes);
    TextUtils.writeToParcel(this.mBreadCrumbTitleText, paramParcel, 0);
    paramParcel.writeInt(this.mBreadCrumbShortTitleRes);
    TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, paramParcel, 0);
    paramParcel.writeStringList(this.mSharedElementSourceNames);
    paramParcel.writeStringList(this.mSharedElementTargetNames);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\BackStackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */