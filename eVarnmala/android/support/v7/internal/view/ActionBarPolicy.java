package android.support.v7.internal.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.bool;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.integer;
import android.support.v7.appcompat.R.styleable;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

public class ActionBarPolicy
{
  private Context mContext;
  
  private ActionBarPolicy(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static ActionBarPolicy get(Context paramContext)
  {
    return new ActionBarPolicy(paramContext);
  }
  
  public boolean enableHomeButtonByDefault()
  {
    boolean bool;
    if (this.mContext.getApplicationInfo().targetSdkVersion >= 14) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public int getEmbeddedMenuWidthLimit()
  {
    return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
  }
  
  public int getMaxActionButtons()
  {
    return this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
  }
  
  public int getStackedTabMaxWidth()
  {
    return this.mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_stacked_tab_max_width);
  }
  
  public int getTabContainerHeight()
  {
    TypedArray localTypedArray = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
    int i = localTypedArray.getLayoutDimension(R.styleable.ActionBar_height, 0);
    Resources localResources = this.mContext.getResources();
    if (!hasEmbeddedTabs()) {
      i = Math.min(i, localResources.getDimensionPixelSize(R.dimen.abc_action_bar_stacked_max_height));
    }
    localTypedArray.recycle();
    return i;
  }
  
  public boolean hasEmbeddedTabs()
  {
    boolean bool;
    if (this.mContext.getApplicationInfo().targetSdkVersion < 16) {
      bool = this.mContext.getResources().getBoolean(R.bool.abc_action_bar_embed_tabs_pre_jb);
    } else {
      bool = this.mContext.getResources().getBoolean(R.bool.abc_action_bar_embed_tabs);
    }
    return bool;
  }
  
  public boolean showsOverflowMenuButton()
  {
    boolean bool = true;
    if ((Build.VERSION.SDK_INT < 19) && (ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mContext)))) {
      bool = false;
    }
    return bool;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\ActionBarPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */