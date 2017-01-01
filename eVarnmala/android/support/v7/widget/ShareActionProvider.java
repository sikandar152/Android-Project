package android.support.v7.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.Theme;
import android.support.v4.view.ActionProvider;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.string;
import android.support.v7.internal.widget.ActivityChooserModel;
import android.support.v7.internal.widget.ActivityChooserModel.OnChooseActivityListener;
import android.support.v7.internal.widget.ActivityChooserView;
import android.support.v7.internal.widget.TintManager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

public class ShareActionProvider
  extends ActionProvider
{
  private static final int DEFAULT_INITIAL_ACTIVITY_COUNT = 4;
  public static final String DEFAULT_SHARE_HISTORY_FILE_NAME = "share_history.xml";
  private final Context mContext;
  private int mMaxShownActivityCount = 4;
  private ActivityChooserModel.OnChooseActivityListener mOnChooseActivityListener;
  private final ShareMenuItemOnMenuItemClickListener mOnMenuItemClickListener = new ShareMenuItemOnMenuItemClickListener(null);
  private OnShareTargetSelectedListener mOnShareTargetSelectedListener;
  private String mShareHistoryFileName = "share_history.xml";
  
  public ShareActionProvider(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  private void setActivityChooserPolicyIfNeeded()
  {
    if (this.mOnShareTargetSelectedListener != null)
    {
      if (this.mOnChooseActivityListener == null) {
        this.mOnChooseActivityListener = new ShareActivityChooserModelPolicy(null);
      }
      ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName).setOnChooseActivityListener(this.mOnChooseActivityListener);
    }
  }
  
  public boolean hasSubMenu()
  {
    return true;
  }
  
  public View onCreateActionView()
  {
    Object localObject = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
    ActivityChooserView localActivityChooserView = new ActivityChooserView(this.mContext);
    localActivityChooserView.setActivityChooserModel((ActivityChooserModel)localObject);
    localObject = new TypedValue();
    this.mContext.getTheme().resolveAttribute(R.attr.actionModeShareDrawable, (TypedValue)localObject, true);
    localActivityChooserView.setExpandActivityOverflowButtonDrawable(TintManager.getDrawable(this.mContext, ((TypedValue)localObject).resourceId));
    localActivityChooserView.setProvider(this);
    localActivityChooserView.setDefaultActionButtonContentDescription(R.string.abc_shareactionprovider_share_with_application);
    localActivityChooserView.setExpandActivityOverflowButtonContentDescription(R.string.abc_shareactionprovider_share_with);
    return localActivityChooserView;
  }
  
  public void onPrepareSubMenu(SubMenu paramSubMenu)
  {
    paramSubMenu.clear();
    ActivityChooserModel localActivityChooserModel = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
    PackageManager localPackageManager = this.mContext.getPackageManager();
    int i = localActivityChooserModel.getActivityCount();
    int k = Math.min(i, this.mMaxShownActivityCount);
    for (int j = 0;; j++)
    {
      if (j >= k)
      {
        SubMenu localSubMenu;
        if (k < i) {
          localSubMenu = paramSubMenu.addSubMenu(0, k, k, this.mContext.getString(R.string.abc_activity_chooser_view_see_all));
        }
        for (j = 0;; j++)
        {
          if (j >= i) {
            return;
          }
          localResolveInfo = localActivityChooserModel.getActivity(j);
          localSubMenu.add(0, j, j, localResolveInfo.loadLabel(localPackageManager)).setIcon(localResolveInfo.loadIcon(localPackageManager)).setOnMenuItemClickListener(this.mOnMenuItemClickListener);
        }
      }
      ResolveInfo localResolveInfo = localActivityChooserModel.getActivity(j);
      paramSubMenu.add(0, j, j, localResolveInfo.loadLabel(localPackageManager)).setIcon(localResolveInfo.loadIcon(localPackageManager)).setOnMenuItemClickListener(this.mOnMenuItemClickListener);
    }
  }
  
  public void setOnShareTargetSelectedListener(OnShareTargetSelectedListener paramOnShareTargetSelectedListener)
  {
    this.mOnShareTargetSelectedListener = paramOnShareTargetSelectedListener;
    setActivityChooserPolicyIfNeeded();
  }
  
  public void setShareHistoryFileName(String paramString)
  {
    this.mShareHistoryFileName = paramString;
    setActivityChooserPolicyIfNeeded();
  }
  
  public void setShareIntent(Intent paramIntent)
  {
    ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName).setIntent(paramIntent);
  }
  
  private class ShareActivityChooserModelPolicy
    implements ActivityChooserModel.OnChooseActivityListener
  {
    private ShareActivityChooserModelPolicy() {}
    
    public boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent)
    {
      if (ShareActionProvider.this.mOnShareTargetSelectedListener != null) {
        ShareActionProvider.this.mOnShareTargetSelectedListener.onShareTargetSelected(ShareActionProvider.this, paramIntent);
      }
      return false;
    }
  }
  
  private class ShareMenuItemOnMenuItemClickListener
    implements MenuItem.OnMenuItemClickListener
  {
    private ShareMenuItemOnMenuItemClickListener() {}
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      Intent localIntent = ActivityChooserModel.get(ShareActionProvider.this.mContext, ShareActionProvider.this.mShareHistoryFileName).chooseActivity(paramMenuItem.getItemId());
      if (localIntent != null)
      {
        localIntent.addFlags(524288);
        ShareActionProvider.this.mContext.startActivity(localIntent);
      }
      return true;
    }
  }
  
  public static abstract interface OnShareTargetSelectedListener
  {
    public abstract boolean onShareTargetSelected(ShareActionProvider paramShareActionProvider, Intent paramIntent);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\ShareActionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */