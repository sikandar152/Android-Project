package android.support.v7.internal.view.menu;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public final class MenuWrapperFactory
{
  public static Menu wrapSupportMenu(Context paramContext, SupportMenu paramSupportMenu)
  {
    if (Build.VERSION.SDK_INT < 14) {
      throw new UnsupportedOperationException();
    }
    return new MenuWrapperICS(paramContext, paramSupportMenu);
  }
  
  public static MenuItem wrapSupportMenuItem(Context paramContext, SupportMenuItem paramSupportMenuItem)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT < 16)
    {
      if (Build.VERSION.SDK_INT < 14) {
        throw new UnsupportedOperationException();
      }
      localObject = new MenuItemWrapperICS(paramContext, paramSupportMenuItem);
    }
    else
    {
      localObject = new MenuItemWrapperJB(paramContext, paramSupportMenuItem);
    }
    return (MenuItem)localObject;
  }
  
  public static SubMenu wrapSupportSubMenu(Context paramContext, SupportSubMenu paramSupportSubMenu)
  {
    if (Build.VERSION.SDK_INT < 14) {
      throw new UnsupportedOperationException();
    }
    return new SubMenuWrapperICS(paramContext, paramSupportSubMenu);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\menu\MenuWrapperFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */