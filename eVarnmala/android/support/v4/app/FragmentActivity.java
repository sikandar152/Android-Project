package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FragmentActivity
  extends Activity
{
  static final String FRAGMENTS_TAG = "android:support:fragments";
  private static final int HONEYCOMB = 11;
  static final int MSG_REALLY_STOPPED = 1;
  static final int MSG_RESUME_PENDING = 2;
  private static final String TAG = "FragmentActivity";
  SimpleArrayMap<String, LoaderManagerImpl> mAllLoaderManagers;
  boolean mCheckedForLoaderManager;
  final FragmentContainer mContainer = new FragmentContainer()
  {
    public View findViewById(int paramAnonymousInt)
    {
      return FragmentActivity.this.findViewById(paramAnonymousInt);
    }
    
    public boolean hasView()
    {
      Window localWindow = FragmentActivity.this.getWindow();
      boolean bool;
      if ((localWindow == null) || (localWindow.peekDecorView() == null)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
  };
  boolean mCreated;
  final FragmentManagerImpl mFragments = new FragmentManagerImpl();
  final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
        break;
      case 1: 
        if (FragmentActivity.this.mStopped) {
          FragmentActivity.this.doReallyStop(false);
        }
        break;
      case 2: 
        FragmentActivity.this.onResumeFragments();
        FragmentActivity.this.mFragments.execPendingActions();
      }
    }
  };
  LoaderManagerImpl mLoaderManager;
  boolean mLoadersStarted;
  boolean mOptionsMenuInvalidated;
  boolean mReallyStopped;
  boolean mResumed;
  boolean mRetaining;
  boolean mStopped;
  
  private void dumpViewHierarchy(String paramString, PrintWriter paramPrintWriter, View paramView)
  {
    paramPrintWriter.print(paramString);
    if (paramView != null)
    {
      paramPrintWriter.println(viewToString(paramView));
      if ((paramView instanceof ViewGroup))
      {
        ViewGroup localViewGroup = (ViewGroup)paramView;
        int i = localViewGroup.getChildCount();
        if (i > 0)
        {
          String str = paramString + "  ";
          for (int j = 0; j < i; j++) {
            dumpViewHierarchy(str, paramPrintWriter, localViewGroup.getChildAt(j));
          }
        }
      }
    }
    else
    {
      paramPrintWriter.println("null");
    }
  }
  
  private static String viewToString(View paramView)
  {
    char c2 = 'F';
    char c1 = '.';
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(paramView.getClass().getName());
    localStringBuilder.append('{');
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(paramView)));
    localStringBuilder.append(' ');
    switch (paramView.getVisibility())
    {
    default: 
      localStringBuilder.append(c1);
    }
    for (;;)
    {
      label108:
      label126:
      label143:
      label161:
      label179:
      label197:
      label215:
      label236:
      label252:
      int i;
      Resources localResources;
      if (paramView.isFocusable())
      {
        char c3 = c2;
        localStringBuilder.append(c3);
        if (!paramView.isEnabled()) {
          break label529;
        }
        c3 = 'E';
        localStringBuilder.append(c3);
        if (!paramView.willNotDraw()) {
          break label535;
        }
        c3 = c1;
        localStringBuilder.append(c3);
        if (!paramView.isHorizontalScrollBarEnabled()) {
          break label542;
        }
        c3 = 'H';
        localStringBuilder.append(c3);
        if (!paramView.isVerticalScrollBarEnabled()) {
          break label548;
        }
        c3 = 'V';
        localStringBuilder.append(c3);
        if (!paramView.isClickable()) {
          break label554;
        }
        c3 = 'C';
        localStringBuilder.append(c3);
        if (!paramView.isLongClickable()) {
          break label560;
        }
        c3 = 'L';
        localStringBuilder.append(c3);
        localStringBuilder.append(' ');
        if (!paramView.isFocused()) {
          break label566;
        }
        localStringBuilder.append(c2);
        if (!paramView.isSelected()) {
          break label571;
        }
        c2 = 'S';
        localStringBuilder.append(c2);
        if (paramView.isPressed()) {
          c1 = 'P';
        }
        localStringBuilder.append(c1);
        localStringBuilder.append(' ');
        localStringBuilder.append(paramView.getLeft());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getTop());
        localStringBuilder.append('-');
        localStringBuilder.append(paramView.getRight());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getBottom());
        i = paramView.getId();
        if (i != -1)
        {
          localStringBuilder.append(" #");
          localStringBuilder.append(Integer.toHexString(i));
          localResources = paramView.getResources();
          if ((i != 0) && (localResources != null)) {
            switch (0xFF000000 & i)
            {
            }
          }
        }
      }
      try
      {
        String str1 = localResources.getResourcePackageName(i);
        for (;;)
        {
          String str2 = localResources.getResourceTypeName(i);
          String str3 = localResources.getResourceEntryName(i);
          localStringBuilder.append(" ");
          localStringBuilder.append(str1);
          localStringBuilder.append(":");
          localStringBuilder.append(str2);
          localStringBuilder.append("/");
          localStringBuilder.append(str3);
          localStringBuilder.append("}");
          return localStringBuilder.toString();
          localStringBuilder.append('V');
          break;
          localStringBuilder.append('I');
          break;
          localStringBuilder.append('G');
          break;
          int j = str1;
          break label108;
          label529:
          int k = str1;
          break label126;
          label535:
          int m = 68;
          break label143;
          label542:
          int n = str1;
          break label161;
          label548:
          int i1 = str1;
          break label179;
          label554:
          int i2 = str1;
          break label197;
          label560:
          int i3 = str1;
          break label215;
          label566:
          str2 = str1;
          break label236;
          label571:
          str2 = str1;
          break label252;
          str1 = "app";
          continue;
          str1 = "android";
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        for (;;) {}
      }
    }
  }
  
  void doReallyStop(boolean paramBoolean)
  {
    if (!this.mReallyStopped)
    {
      this.mReallyStopped = true;
      this.mRetaining = paramBoolean;
      this.mHandler.removeMessages(1);
      onReallyStop();
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT >= 11) {}
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Local FragmentActivity ");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(" State:");
    String str = paramString + "  ";
    paramPrintWriter.print(str);
    paramPrintWriter.print("mCreated=");
    paramPrintWriter.print(this.mCreated);
    paramPrintWriter.print("mResumed=");
    paramPrintWriter.print(this.mResumed);
    paramPrintWriter.print(" mStopped=");
    paramPrintWriter.print(this.mStopped);
    paramPrintWriter.print(" mReallyStopped=");
    paramPrintWriter.println(this.mReallyStopped);
    paramPrintWriter.print(str);
    paramPrintWriter.print("mLoadersStarted=");
    paramPrintWriter.println(this.mLoadersStarted);
    if (this.mLoaderManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Loader Manager ");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
      paramPrintWriter.println(":");
      this.mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    this.mFragments.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("View Hierarchy:");
    dumpViewHierarchy(paramString + "  ", paramPrintWriter, getWindow().getDecorView());
  }
  
  public Object getLastCustomNonConfigurationInstance()
  {
    Object localObject = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((NonConfigurationInstances)localObject).custom;
    }
    return localObject;
  }
  
  LoaderManagerImpl getLoaderManager(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAllLoaderManagers == null) {
      this.mAllLoaderManagers = new SimpleArrayMap();
    }
    LoaderManagerImpl localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
    if (localLoaderManagerImpl != null)
    {
      localLoaderManagerImpl.updateActivity(this);
    }
    else if (paramBoolean2)
    {
      localLoaderManagerImpl = new LoaderManagerImpl(paramString, this, paramBoolean1);
      this.mAllLoaderManagers.put(paramString, localLoaderManagerImpl);
    }
    return localLoaderManagerImpl;
  }
  
  public FragmentManager getSupportFragmentManager()
  {
    return this.mFragments;
  }
  
  public LoaderManager getSupportLoaderManager()
  {
    LoaderManagerImpl localLoaderManagerImpl;
    if (this.mLoaderManager == null)
    {
      this.mCheckedForLoaderManager = true;
      this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
      localLoaderManagerImpl = this.mLoaderManager;
    }
    else
    {
      localLoaderManagerImpl = this.mLoaderManager;
    }
    return localLoaderManagerImpl;
  }
  
  void invalidateSupportFragment(String paramString)
  {
    if (this.mAllLoaderManagers != null)
    {
      LoaderManagerImpl localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
      if ((localLoaderManagerImpl != null) && (!localLoaderManagerImpl.mRetaining))
      {
        localLoaderManagerImpl.doDestroy();
        this.mAllLoaderManagers.remove(paramString);
      }
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mFragments.noteStateNotSaved();
    int i = paramInt1 >> 16;
    if (i == 0)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
    }
    else
    {
      i -= 1;
      if ((this.mFragments.mActive != null) && (i >= 0) && (i < this.mFragments.mActive.size()))
      {
        Fragment localFragment = (Fragment)this.mFragments.mActive.get(i);
        if (localFragment != null) {
          localFragment.onActivityResult(0xFFFF & paramInt1, paramInt2, paramIntent);
        } else {
          Log.w("FragmentActivity", "Activity result no fragment exists for index: 0x" + Integer.toHexString(paramInt1));
        }
      }
      else
      {
        Log.w("FragmentActivity", "Activity result fragment index out of range: 0x" + Integer.toHexString(paramInt1));
      }
    }
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onBackPressed()
  {
    if (!this.mFragments.popBackStackImmediate()) {
      supportFinishAfterTransition();
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mFragments.dispatchConfigurationChanged(paramConfiguration);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    ArrayList localArrayList = null;
    this.mFragments.attachActivity(this, this.mContainer, null);
    if (getLayoutInflater().getFactory() == null) {
      getLayoutInflater().setFactory(this);
    }
    super.onCreate(paramBundle);
    NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localNonConfigurationInstances != null) {
      this.mAllLoaderManagers = localNonConfigurationInstances.loaders;
    }
    if (paramBundle != null)
    {
      Parcelable localParcelable = paramBundle.getParcelable("android:support:fragments");
      FragmentManagerImpl localFragmentManagerImpl = this.mFragments;
      if (localNonConfigurationInstances != null) {
        localArrayList = localNonConfigurationInstances.fragments;
      }
      localFragmentManagerImpl.restoreAllState(localParcelable, localArrayList);
    }
    this.mFragments.dispatchCreate();
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    boolean bool;
    if (paramInt != 0)
    {
      bool = super.onCreatePanelMenu(paramInt, paramMenu);
    }
    else
    {
      bool = super.onCreatePanelMenu(paramInt, paramMenu) | this.mFragments.dispatchCreateOptionsMenu(paramMenu, getMenuInflater());
      if (Build.VERSION.SDK_INT < 11) {
        bool = true;
      }
    }
    return bool;
  }
  
  public View onCreateView(String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet)
  {
    View localView;
    if ("fragment".equals(paramString))
    {
      localView = this.mFragments.onCreateView(paramString, paramContext, paramAttributeSet);
      if (localView == null) {
        localView = super.onCreateView(paramString, paramContext, paramAttributeSet);
      }
    }
    else
    {
      localView = super.onCreateView(paramString, paramContext, paramAttributeSet);
    }
    return localView;
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    doReallyStop(false);
    this.mFragments.dispatchDestroy();
    if (this.mLoaderManager != null) {
      this.mLoaderManager.doDestroy();
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((Build.VERSION.SDK_INT >= 5) || (paramInt != 4) || (paramKeyEvent.getRepeatCount() != 0))
    {
      bool = super.onKeyDown(paramInt, paramKeyEvent);
    }
    else
    {
      onBackPressed();
      bool = true;
    }
    return bool;
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    this.mFragments.dispatchLowMemory();
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    boolean bool;
    if (!super.onMenuItemSelected(paramInt, paramMenuItem)) {
      switch (paramInt)
      {
      default: 
        bool = false;
        break;
      case 0: 
        bool = this.mFragments.dispatchOptionsItemSelected(paramMenuItem);
        break;
      case 6: 
        bool = this.mFragments.dispatchContextItemSelected(paramMenuItem);
        break;
      }
    } else {
      bool = true;
    }
    return bool;
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    this.mFragments.noteStateNotSaved();
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    switch (paramInt)
    {
    case 0: 
      this.mFragments.dispatchOptionsMenuClosed(paramMenu);
    }
    super.onPanelClosed(paramInt, paramMenu);
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mResumed = false;
    if (this.mHandler.hasMessages(2))
    {
      this.mHandler.removeMessages(2);
      onResumeFragments();
    }
    this.mFragments.dispatchPause();
  }
  
  protected void onPostResume()
  {
    super.onPostResume();
    this.mHandler.removeMessages(2);
    onResumeFragments();
    this.mFragments.execPendingActions();
  }
  
  protected boolean onPrepareOptionsPanel(View paramView, Menu paramMenu)
  {
    return super.onPreparePanel(0, paramView, paramMenu);
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    boolean bool;
    if ((paramInt != 0) || (paramMenu == null))
    {
      bool = super.onPreparePanel(paramInt, paramView, paramMenu);
    }
    else
    {
      if (this.mOptionsMenuInvalidated)
      {
        this.mOptionsMenuInvalidated = false;
        paramMenu.clear();
        onCreatePanelMenu(paramInt, paramMenu);
      }
      bool = onPrepareOptionsPanel(paramView, paramMenu) | this.mFragments.dispatchPrepareOptionsMenu(paramMenu);
    }
    return bool;
  }
  
  void onReallyStop()
  {
    if (this.mLoadersStarted)
    {
      this.mLoadersStarted = false;
      if (this.mLoaderManager != null) {
        if (this.mRetaining) {
          this.mLoaderManager.doRetain();
        } else {
          this.mLoaderManager.doStop();
        }
      }
    }
    this.mFragments.dispatchReallyStop();
  }
  
  protected void onResume()
  {
    super.onResume();
    this.mHandler.sendEmptyMessage(2);
    this.mResumed = true;
    this.mFragments.execPendingActions();
  }
  
  protected void onResumeFragments()
  {
    this.mFragments.dispatchResume();
  }
  
  public Object onRetainCustomNonConfigurationInstance()
  {
    return null;
  }
  
  public final Object onRetainNonConfigurationInstance()
  {
    if (this.mStopped) {
      doReallyStop(true);
    }
    Object localObject = onRetainCustomNonConfigurationInstance();
    ArrayList localArrayList = this.mFragments.retainNonConfig();
    int j = 0;
    int i;
    LoaderManagerImpl[] arrayOfLoaderManagerImpl;
    if (this.mAllLoaderManagers != null)
    {
      i = this.mAllLoaderManagers.size();
      arrayOfLoaderManagerImpl = new LoaderManagerImpl[i];
    }
    LoaderManagerImpl localLoaderManagerImpl;
    for (int k = i - 1;; localLoaderManagerImpl--)
    {
      if (k < 0) {
        for (int m = 0;; m++)
        {
          if (m >= i)
          {
            NonConfigurationInstances localNonConfigurationInstances;
            if ((localArrayList != null) || (j != 0) || (localObject != null))
            {
              localNonConfigurationInstances = new NonConfigurationInstances();
              localNonConfigurationInstances.activity = null;
              localNonConfigurationInstances.custom = localObject;
              localNonConfigurationInstances.children = null;
              localNonConfigurationInstances.fragments = localArrayList;
              localNonConfigurationInstances.loaders = this.mAllLoaderManagers;
            }
            else
            {
              localNonConfigurationInstances = null;
            }
            return localNonConfigurationInstances;
          }
          localLoaderManagerImpl = arrayOfLoaderManagerImpl[m];
          if (!localLoaderManagerImpl.mRetaining)
          {
            localLoaderManagerImpl.doDestroy();
            this.mAllLoaderManagers.remove(localLoaderManagerImpl.mWho);
          }
          else
          {
            j = 1;
          }
        }
      }
      arrayOfLoaderManagerImpl[localLoaderManagerImpl] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(localLoaderManagerImpl));
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    Parcelable localParcelable = this.mFragments.saveAllState();
    if (localParcelable != null) {
      paramBundle.putParcelable("android:support:fragments", localParcelable);
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mStopped = false;
    this.mReallyStopped = false;
    this.mHandler.removeMessages(1);
    if (!this.mCreated)
    {
      this.mCreated = true;
      this.mFragments.dispatchActivityCreated();
    }
    this.mFragments.noteStateNotSaved();
    this.mFragments.execPendingActions();
    if (!this.mLoadersStarted)
    {
      this.mLoadersStarted = true;
      if (this.mLoaderManager == null)
      {
        if (!this.mCheckedForLoaderManager)
        {
          this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
          if ((this.mLoaderManager != null) && (!this.mLoaderManager.mStarted)) {
            this.mLoaderManager.doStart();
          }
        }
      }
      else {
        this.mLoaderManager.doStart();
      }
      this.mCheckedForLoaderManager = true;
    }
    this.mFragments.dispatchStart();
    int i;
    LoaderManagerImpl[] arrayOfLoaderManagerImpl;
    if (this.mAllLoaderManagers != null)
    {
      i = this.mAllLoaderManagers.size();
      arrayOfLoaderManagerImpl = new LoaderManagerImpl[i];
    }
    LoaderManagerImpl localLoaderManagerImpl;
    for (int j = i - 1;; localLoaderManagerImpl--)
    {
      if (j < 0) {
        for (int k = 0;; k++)
        {
          if (k >= i) {
            return;
          }
          localLoaderManagerImpl = arrayOfLoaderManagerImpl[k];
          localLoaderManagerImpl.finishRetain();
          localLoaderManagerImpl.doReportStart();
        }
      }
      arrayOfLoaderManagerImpl[localLoaderManagerImpl] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(localLoaderManagerImpl));
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mStopped = true;
    this.mHandler.sendEmptyMessage(1);
    this.mFragments.dispatchStop();
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setEnterSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setExitSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if ((paramInt == -1) || ((0xFFFF0000 & paramInt) == 0))
    {
      super.startActivityForResult(paramIntent, paramInt);
      return;
    }
    throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    if (paramInt != -1)
    {
      if ((0xFFFF0000 & paramInt) == 0) {
        super.startActivityForResult(paramIntent, (1 + paramFragment.mIndex << 16) + (0xFFFF & paramInt));
      } else {
        throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
      }
    }
    else {
      super.startActivityForResult(paramIntent, -1);
    }
  }
  
  public void supportFinishAfterTransition()
  {
    ActivityCompat.finishAfterTransition(this);
  }
  
  public void supportInvalidateOptionsMenu()
  {
    if (Build.VERSION.SDK_INT < 11) {
      this.mOptionsMenuInvalidated = true;
    } else {
      ActivityCompatHoneycomb.invalidateOptionsMenu(this);
    }
  }
  
  public void supportPostponeEnterTransition()
  {
    ActivityCompat.postponeEnterTransition(this);
  }
  
  public void supportStartPostponedEnterTransition()
  {
    ActivityCompat.startPostponedEnterTransition(this);
  }
  
  static final class NonConfigurationInstances
  {
    Object activity;
    SimpleArrayMap<String, Object> children;
    Object custom;
    ArrayList<Fragment> fragments;
    SimpleArrayMap<String, LoaderManagerImpl> loaders;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\FragmentActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */