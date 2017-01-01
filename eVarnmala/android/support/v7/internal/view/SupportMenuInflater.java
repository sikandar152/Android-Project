package android.support.v7.internal.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SupportMenuInflater
  extends MenuInflater
{
  private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
  private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
  private static final String LOG_TAG = "SupportMenuInflater";
  private static final int NO_ID = 0;
  private static final String XML_GROUP = "group";
  private static final String XML_ITEM = "item";
  private static final String XML_MENU = "menu";
  private final Object[] mActionProviderConstructorArguments;
  private final Object[] mActionViewConstructorArguments;
  private Context mContext;
  private Object mRealOwner;
  
  static
  {
    Class[] arrayOfClass = new Class[1];
    arrayOfClass[0] = Context.class;
    ACTION_VIEW_CONSTRUCTOR_SIGNATURE = arrayOfClass;
  }
  
  public SupportMenuInflater(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramContext;
    this.mActionViewConstructorArguments = arrayOfObject;
    this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
  }
  
  private Object findRealOwner(Object paramObject)
  {
    if ((!(paramObject instanceof Activity)) && ((paramObject instanceof ContextWrapper))) {
      paramObject = findRealOwner(((ContextWrapper)paramObject).getBaseContext());
    }
    return paramObject;
  }
  
  private Object getRealOwner()
  {
    if (this.mRealOwner == null) {
      this.mRealOwner = findRealOwner(this.mContext);
    }
    return this.mRealOwner;
  }
  
  private void parseMenu(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Menu paramMenu)
    throws XmlPullParserException, IOException
  {
    MenuState localMenuState = new MenuState(paramMenu);
    int k = paramXmlPullParser.getEventType();
    int i = 0;
    String str1 = null;
    while (k != 2)
    {
      k = paramXmlPullParser.next();
      if (k == 1) {
        break label102;
      }
    }
    String str2 = paramXmlPullParser.getName();
    if (!str2.equals("menu")) {
      throw new RuntimeException("Expecting menu, got " + str2);
    }
    k = paramXmlPullParser.next();
    label102:
    int j = 0;
    for (;;)
    {
      if (j != 0) {
        return;
      }
      String str3;
      switch (k)
      {
      case 1: 
        throw new RuntimeException("Unexpected end of document");
      case 2: 
        if (i == 0)
        {
          str3 = paramXmlPullParser.getName();
          if (!str3.equals("group"))
          {
            if (!str3.equals("item"))
            {
              if (!str3.equals("menu"))
              {
                i = 1;
                str1 = str3;
              }
              else
              {
                parseMenu(paramXmlPullParser, paramAttributeSet, localMenuState.addSubMenuItem());
              }
            }
            else {
              localMenuState.readItem(paramAttributeSet);
            }
          }
          else {
            localMenuState.readGroup(paramAttributeSet);
          }
        }
        break;
      case 3: 
        str3 = paramXmlPullParser.getName();
        if ((i == 0) || (!str3.equals(str1)))
        {
          if (!str3.equals("group"))
          {
            if (!str3.equals("item"))
            {
              if (str3.equals("menu")) {
                j = 1;
              }
            }
            else if (!localMenuState.hasAddedItem()) {
              if ((localMenuState.itemActionProvider == null) || (!localMenuState.itemActionProvider.hasSubMenu())) {
                localMenuState.addItem();
              } else {
                localMenuState.addSubMenuItem();
              }
            }
          }
          else {
            localMenuState.resetGroup();
          }
        }
        else
        {
          i = 0;
          str1 = null;
        }
        break;
      }
      int m = paramXmlPullParser.next();
    }
  }
  
  /* Error */
  public void inflate(int paramInt, Menu paramMenu)
  {
    // Byte code:
    //   0: aload_2
    //   1: instanceof 171
    //   4: ifne +10 -> 14
    //   7: aload_0
    //   8: iload_1
    //   9: aload_2
    //   10: invokespecial 173	android/view/MenuInflater:inflate	(ILandroid/view/Menu;)V
    //   13: return
    //   14: aconst_null
    //   15: astore_3
    //   16: aload_0
    //   17: getfield 53	android/support/v7/internal/view/SupportMenuInflater:mContext	Landroid/content/Context;
    //   20: invokevirtual 177	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   23: iload_1
    //   24: invokevirtual 183	android/content/res/Resources:getLayout	(I)Landroid/content/res/XmlResourceParser;
    //   27: astore_3
    //   28: aload_0
    //   29: aload_3
    //   30: aload_3
    //   31: invokestatic 189	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   34: aload_2
    //   35: invokespecial 141	android/support/v7/internal/view/SupportMenuInflater:parseMenu	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V
    //   38: aload_3
    //   39: ifnull -26 -> 13
    //   42: aload_3
    //   43: invokeinterface 194 1 0
    //   48: goto -35 -> 13
    //   51: astore 4
    //   53: new 196	android/view/InflateException
    //   56: dup
    //   57: ldc -58
    //   59: aload 4
    //   61: invokespecial 201	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   64: athrow
    //   65: astore 4
    //   67: aload_3
    //   68: ifnull +9 -> 77
    //   71: aload_3
    //   72: invokeinterface 194 1 0
    //   77: aload 4
    //   79: athrow
    //   80: astore 4
    //   82: new 196	android/view/InflateException
    //   85: dup
    //   86: ldc -58
    //   88: aload 4
    //   90: invokespecial 201	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	SupportMenuInflater
    //   0	94	1	paramInt	int
    //   0	94	2	paramMenu	Menu
    //   15	57	3	localXmlResourceParser	android.content.res.XmlResourceParser
    //   51	9	4	localXmlPullParserException	XmlPullParserException
    //   65	13	4	localObject	Object
    //   80	9	4	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   16	38	51	org/xmlpull/v1/XmlPullParserException
    //   16	38	65	finally
    //   53	65	65	finally
    //   82	94	65	finally
    //   16	38	80	java/io/IOException
  }
  
  private class MenuState
  {
    private static final int defaultGroupId = 0;
    private static final int defaultItemCategory = 0;
    private static final int defaultItemCheckable = 0;
    private static final boolean defaultItemChecked = false;
    private static final boolean defaultItemEnabled = true;
    private static final int defaultItemId = 0;
    private static final int defaultItemOrder = 0;
    private static final boolean defaultItemVisible = true;
    private int groupCategory;
    private int groupCheckable;
    private boolean groupEnabled;
    private int groupId;
    private int groupOrder;
    private boolean groupVisible;
    private ActionProvider itemActionProvider;
    private String itemActionProviderClassName;
    private String itemActionViewClassName;
    private int itemActionViewLayout;
    private boolean itemAdded;
    private char itemAlphabeticShortcut;
    private int itemCategoryOrder;
    private int itemCheckable;
    private boolean itemChecked;
    private boolean itemEnabled;
    private int itemIconResId;
    private int itemId;
    private String itemListenerMethodName;
    private char itemNumericShortcut;
    private int itemShowAsAction;
    private CharSequence itemTitle;
    private CharSequence itemTitleCondensed;
    private boolean itemVisible;
    private Menu menu;
    
    public MenuState(Menu paramMenu)
    {
      this.menu = paramMenu;
      resetGroup();
    }
    
    private char getShortcut(String paramString)
    {
      char c = '\000';
      if (paramString != null) {
        c = paramString.charAt(0);
      }
      return c;
    }
    
    private <T> T newInstance(String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
    {
      try
      {
        localObject1 = SupportMenuInflater.this.mContext.getClassLoader().loadClass(paramString).getConstructor(paramArrayOfClass).newInstance(paramArrayOfObject);
        localObject1 = localObject1;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Object localObject1;
          Log.w("SupportMenuInflater", "Cannot instantiate class: " + paramString, localException);
          Object localObject2 = null;
        }
      }
      return (T)localObject1;
    }
    
    private void setItem(MenuItem paramMenuItem)
    {
      MenuItem localMenuItem = paramMenuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled);
      boolean bool;
      if (this.itemCheckable < 1) {
        bool = false;
      } else {
        bool = true;
      }
      localMenuItem.setCheckable(bool).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut).setNumericShortcut(this.itemNumericShortcut);
      if (this.itemShowAsAction >= 0) {
        MenuItemCompat.setShowAsAction(paramMenuItem, this.itemShowAsAction);
      }
      if (this.itemListenerMethodName != null)
      {
        if (!SupportMenuInflater.this.mContext.isRestricted()) {
          paramMenuItem.setOnMenuItemClickListener(new SupportMenuInflater.InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
        }
      }
      else
      {
        if ((paramMenuItem instanceof MenuItemImpl)) {
          ((MenuItemImpl)paramMenuItem);
        }
        if (this.itemCheckable >= 2) {
          if (!(paramMenuItem instanceof MenuItemImpl))
          {
            if ((paramMenuItem instanceof MenuItemWrapperICS)) {
              ((MenuItemWrapperICS)paramMenuItem).setExclusiveCheckable(true);
            }
          }
          else {
            ((MenuItemImpl)paramMenuItem).setExclusiveCheckable(true);
          }
        }
        int i = 0;
        if (this.itemActionViewClassName != null)
        {
          MenuItemCompat.setActionView(paramMenuItem, (View)newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments));
          i = 1;
        }
        if (this.itemActionViewLayout > 0) {
          if (i != 0) {
            Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
          } else {
            MenuItemCompat.setActionView(paramMenuItem, this.itemActionViewLayout);
          }
        }
        if (this.itemActionProvider != null) {
          MenuItemCompat.setActionProvider(paramMenuItem, this.itemActionProvider);
        }
        return;
      }
      throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
    }
    
    public void addItem()
    {
      this.itemAdded = true;
      setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
    }
    
    public SubMenu addSubMenuItem()
    {
      this.itemAdded = true;
      SubMenu localSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
      setItem(localSubMenu.getItem());
      return localSubMenu;
    }
    
    public boolean hasAddedItem()
    {
      return this.itemAdded;
    }
    
    public void readGroup(AttributeSet paramAttributeSet)
    {
      TypedArray localTypedArray = SupportMenuInflater.this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuGroup);
      this.groupId = localTypedArray.getResourceId(R.styleable.MenuGroup_android_id, 0);
      this.groupCategory = localTypedArray.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
      this.groupOrder = localTypedArray.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
      this.groupCheckable = localTypedArray.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
      this.groupVisible = localTypedArray.getBoolean(R.styleable.MenuGroup_android_visible, true);
      this.groupEnabled = localTypedArray.getBoolean(R.styleable.MenuGroup_android_enabled, true);
      localTypedArray.recycle();
    }
    
    public void readItem(AttributeSet paramAttributeSet)
    {
      TypedArray localTypedArray = SupportMenuInflater.this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuItem);
      this.itemId = localTypedArray.getResourceId(R.styleable.MenuItem_android_id, 0);
      int j = localTypedArray.getInt(R.styleable.MenuItem_android_menuCategory, this.groupCategory);
      int i = localTypedArray.getInt(R.styleable.MenuItem_android_orderInCategory, this.groupOrder);
      this.itemCategoryOrder = (0xFFFF0000 & j | 0xFFFF & i);
      this.itemTitle = localTypedArray.getText(R.styleable.MenuItem_android_title);
      this.itemTitleCondensed = localTypedArray.getText(R.styleable.MenuItem_android_titleCondensed);
      this.itemIconResId = localTypedArray.getResourceId(R.styleable.MenuItem_android_icon, 0);
      this.itemAlphabeticShortcut = getShortcut(localTypedArray.getString(R.styleable.MenuItem_android_alphabeticShortcut));
      this.itemNumericShortcut = getShortcut(localTypedArray.getString(R.styleable.MenuItem_android_numericShortcut));
      if (!localTypedArray.hasValue(R.styleable.MenuItem_android_checkable))
      {
        this.itemCheckable = this.groupCheckable;
      }
      else
      {
        if (!localTypedArray.getBoolean(R.styleable.MenuItem_android_checkable, false)) {
          i = 0;
        } else {
          i = 1;
        }
        this.itemCheckable = i;
      }
      this.itemChecked = localTypedArray.getBoolean(R.styleable.MenuItem_android_checked, false);
      this.itemVisible = localTypedArray.getBoolean(R.styleable.MenuItem_android_visible, this.groupVisible);
      this.itemEnabled = localTypedArray.getBoolean(R.styleable.MenuItem_android_enabled, this.groupEnabled);
      this.itemShowAsAction = localTypedArray.getInt(R.styleable.MenuItem_showAsAction, -1);
      this.itemListenerMethodName = localTypedArray.getString(R.styleable.MenuItem_android_onClick);
      this.itemActionViewLayout = localTypedArray.getResourceId(R.styleable.MenuItem_actionLayout, 0);
      this.itemActionViewClassName = localTypedArray.getString(R.styleable.MenuItem_actionViewClass);
      this.itemActionProviderClassName = localTypedArray.getString(R.styleable.MenuItem_actionProviderClass);
      if (this.itemActionProviderClassName == null) {
        i = 0;
      } else {
        i = 1;
      }
      if ((i == 0) || (this.itemActionViewLayout != 0) || (this.itemActionViewClassName != null))
      {
        if (i != 0) {
          Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
        }
        this.itemActionProvider = null;
      }
      else
      {
        this.itemActionProvider = ((ActionProvider)newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionProviderConstructorArguments));
      }
      localTypedArray.recycle();
      this.itemAdded = false;
    }
    
    public void resetGroup()
    {
      this.groupId = 0;
      this.groupCategory = 0;
      this.groupOrder = 0;
      this.groupCheckable = 0;
      this.groupVisible = true;
      this.groupEnabled = true;
    }
  }
  
  private static class InflatedOnMenuItemClickListener
    implements MenuItem.OnMenuItemClickListener
  {
    private static final Class<?>[] PARAM_TYPES;
    private Method mMethod;
    private Object mRealOwner;
    
    static
    {
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = MenuItem.class;
      PARAM_TYPES = arrayOfClass;
    }
    
    public InflatedOnMenuItemClickListener(Object paramObject, String paramString)
    {
      this.mRealOwner = paramObject;
      Object localObject = paramObject.getClass();
      try
      {
        this.mMethod = ((Class)localObject).getMethod(paramString, PARAM_TYPES);
        return;
      }
      catch (Exception localException)
      {
        localObject = new InflateException("Couldn't resolve menu item onClick handler " + paramString + " in class " + ((Class)localObject).getName());
        ((InflateException)localObject).initCause(localException);
        throw ((Throwable)localObject);
      }
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      boolean bool = true;
      try
      {
        Method localMethod;
        Object localObject2;
        Object localObject1;
        if (this.mMethod.getReturnType() == Boolean.TYPE)
        {
          localMethod = this.mMethod;
          localObject2 = this.mRealOwner;
          localObject1 = new Object[1];
          localObject1[0] = paramMenuItem;
          bool = ((Boolean)localMethod.invoke(localObject2, (Object[])localObject1)).booleanValue();
        }
        else
        {
          localMethod = this.mMethod;
          localObject1 = this.mRealOwner;
          localObject2 = new Object[1];
          localObject2[0] = paramMenuItem;
          localMethod.invoke(localObject1, (Object[])localObject2);
        }
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      return bool;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\view\SupportMenuInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */