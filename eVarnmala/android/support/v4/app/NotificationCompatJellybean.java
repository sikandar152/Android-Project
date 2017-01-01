package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class NotificationCompatJellybean
{
  static final String EXTRA_ACTION_EXTRAS = "android.support.actionExtras";
  static final String EXTRA_GROUP_KEY = "android.support.groupKey";
  static final String EXTRA_GROUP_SUMMARY = "android.support.isGroupSummary";
  static final String EXTRA_LOCAL_ONLY = "android.support.localOnly";
  static final String EXTRA_REMOTE_INPUTS = "android.support.remoteInputs";
  static final String EXTRA_SORT_KEY = "android.support.sortKey";
  static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  private static final String KEY_ACTION_INTENT = "actionIntent";
  private static final String KEY_EXTRAS = "extras";
  private static final String KEY_ICON = "icon";
  private static final String KEY_REMOTE_INPUTS = "remoteInputs";
  private static final String KEY_TITLE = "title";
  public static final String TAG = "NotificationCompat";
  private static Class<?> sActionClass;
  private static Field sActionIconField;
  private static Field sActionIntentField;
  private static Field sActionTitleField;
  private static boolean sActionsAccessFailed;
  private static Field sActionsField;
  private static final Object sActionsLock = new Object();
  private static Field sExtrasField;
  private static boolean sExtrasFieldAccessFailed;
  private static final Object sExtrasLock = new Object();
  
  public static void addBigPictureStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean1, CharSequence paramCharSequence2, Bitmap paramBitmap1, Bitmap paramBitmap2, boolean paramBoolean2)
  {
    Notification.BigPictureStyle localBigPictureStyle = new Notification.BigPictureStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigPicture(paramBitmap1);
    if (paramBoolean2) {
      localBigPictureStyle.bigLargeIcon(paramBitmap2);
    }
    if (paramBoolean1) {
      localBigPictureStyle.setSummaryText(paramCharSequence2);
    }
  }
  
  public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    Notification.BigTextStyle localBigTextStyle = new Notification.BigTextStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigText(paramCharSequence3);
    if (paramBoolean) {
      localBigTextStyle.setSummaryText(paramCharSequence2);
    }
  }
  
  public static void addInboxStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, ArrayList<CharSequence> paramArrayList)
  {
    Notification.InboxStyle localInboxStyle = new Notification.InboxStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1);
    if (paramBoolean) {
      localInboxStyle.setSummaryText(paramCharSequence2);
    }
    Iterator localIterator = paramArrayList.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      localInboxStyle.addLine((CharSequence)localIterator.next());
    }
  }
  
  public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> paramList)
  {
    Object localObject = null;
    int i = 0;
    int j = paramList.size();
    for (;;)
    {
      if (i >= j) {
        return (SparseArray<Bundle>)localObject;
      }
      Bundle localBundle = (Bundle)paramList.get(i);
      if (localBundle != null)
      {
        if (localObject == null) {
          localObject = new SparseArray();
        }
        ((SparseArray)localObject).put(i, localBundle);
      }
      i++;
    }
  }
  
  private static boolean ensureActionReflectionReadyLocked()
  {
    boolean bool2 = false;
    boolean bool1 = true;
    if (sActionsAccessFailed) {}
    for (;;)
    {
      return bool2;
      try
      {
        if (sActionsField == null)
        {
          sActionClass = Class.forName("android.app.Notification$Action");
          sActionIconField = sActionClass.getDeclaredField("icon");
          sActionTitleField = sActionClass.getDeclaredField("title");
          sActionIntentField = sActionClass.getDeclaredField("actionIntent");
          sActionsField = Notification.class.getDeclaredField("actions");
          sActionsField.setAccessible(true);
        }
        if (!sActionsAccessFailed) {
          bool2 = bool1;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification actions", localClassNotFoundException);
          sActionsAccessFailed = bool1;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification actions", localNoSuchFieldException);
          sActionsAccessFailed = bool1;
          continue;
          bool1 = false;
        }
      }
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    synchronized (sActionsLock)
    {
      try
      {
        Object localObject2 = getActionObjectsLocked(paramNotification)[paramInt];
        Bundle localBundle = null;
        Object localObject4 = getExtras(paramNotification);
        if (localObject4 != null)
        {
          localObject4 = ((Bundle)localObject4).getSparseParcelableArray("android.support.actionExtras");
          if (localObject4 != null) {
            localBundle = (Bundle)((SparseArray)localObject4).get(paramInt);
          }
        }
        localObject2 = readAction(paramFactory, paramFactory1, sActionIconField.getInt(localObject2), (CharSequence)sActionTitleField.get(localObject2), (PendingIntent)sActionIntentField.get(localObject2), localBundle);
        localObject2 = localObject2;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localIllegalAccessException);
        sActionsAccessFailed = true;
        Object localObject3 = null;
      }
    }
    return localAction;
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    for (;;)
    {
      synchronized (sActionsLock)
      {
        Object[] arrayOfObject = getActionObjectsLocked(paramNotification);
        if (arrayOfObject != null)
        {
          int i = arrayOfObject.length;
          return i;
        }
      }
      int j = 0;
    }
  }
  
  private static NotificationCompatBase.Action getActionFromBundle(Bundle paramBundle, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return paramFactory.build(paramBundle.getInt("icon"), paramBundle.getCharSequence("title"), (PendingIntent)paramBundle.getParcelable("actionIntent"), paramBundle.getBundle("extras"), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "remoteInputs"), paramFactory1));
  }
  
  private static Object[] getActionObjectsLocked(Notification paramNotification)
  {
    Object localObject3;
    for (;;)
    {
      Object[] arrayOfObject;
      synchronized (sActionsLock)
      {
        if (!ensureActionReflectionReadyLocked())
        {
          arrayOfObject = null;
          return arrayOfObject;
        }
      }
    }
  }
  
  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    if (paramArrayList != null)
    {
      arrayOfAction = paramFactory.newArray(paramArrayList.size());
      for (int i = 0; i < arrayOfAction.length; i++) {
        arrayOfAction[i] = getActionFromBundle((Bundle)paramArrayList.get(i), paramFactory, paramFactory1);
      }
    }
    NotificationCompatBase.Action[] arrayOfAction = null;
    return arrayOfAction;
  }
  
  private static Bundle getBundleForAction(NotificationCompatBase.Action paramAction)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("icon", paramAction.getIcon());
    localBundle.putCharSequence("title", paramAction.getTitle());
    localBundle.putParcelable("actionIntent", paramAction.getActionIntent());
    localBundle.putBundle("extras", paramAction.getExtras());
    localBundle.putParcelableArray("remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    return localBundle;
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    for (;;)
    {
      Object localObject2;
      synchronized (sExtrasLock)
      {
        if (sExtrasFieldAccessFailed)
        {
          localObject2 = null;
          return (Bundle)localObject2;
        }
      }
      try
      {
        if (sExtrasField == null)
        {
          localObject2 = Notification.class.getDeclaredField("extras");
          if (!Bundle.class.isAssignableFrom(((Field)localObject2).getType()))
          {
            Log.e("NotificationCompat", "Notification.extras field is not of type Bundle");
            sExtrasFieldAccessFailed = true;
            localObject2 = null;
            continue;
          }
          ((Field)localObject2).setAccessible(true);
          sExtrasField = (Field)localObject2;
        }
        localObject2 = (Bundle)sExtrasField.get(paramNotification);
        if (localObject2 == null)
        {
          localObject2 = new Bundle();
          sExtrasField.set(paramNotification, localObject2);
        }
        continue;
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.e("NotificationCompat", "Unable to access notification extras", localIllegalAccessException);
        sExtrasFieldAccessFailed = true;
        Object localObject4 = null;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification extras", localNoSuchFieldException);
        }
      }
    }
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.groupKey");
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.localOnly");
  }
  
  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    if (paramArrayOfAction != null)
    {
      localArrayList = new ArrayList(paramArrayOfAction.length);
      int i = paramArrayOfAction.length;
      for (int j = 0; j < i; j++) {
        localArrayList.add(getBundleForAction(paramArrayOfAction[j]));
      }
    }
    ArrayList localArrayList = null;
    return localArrayList;
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.sortKey");
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.isGroupSummary");
  }
  
  public static NotificationCompatBase.Action readAction(NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1, int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = null;
    if (paramBundle != null) {
      arrayOfRemoteInput = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "android.support.remoteInputs"), paramFactory1);
    }
    return paramFactory.build(paramInt, paramCharSequence, paramPendingIntent, paramBundle, arrayOfRemoteInput);
  }
  
  public static Bundle writeActionAndGetExtras(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    paramBuilder.addAction(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    Bundle localBundle = new Bundle(paramAction.getExtras());
    if (paramAction.getRemoteInputs() != null) {
      localBundle.putParcelableArray("android.support.remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    }
    return localBundle;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private List<Bundle> mActionExtrasList = new ArrayList();
    private final Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean3, Bundle paramBundle, String paramString1, boolean paramBoolean4, String paramString2)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(paramNotification.when).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      boolean bool3;
      if ((0x2 & paramNotification.flags) == 0) {
        bool3 = false;
      } else {
        bool3 = true;
      }
      Notification.Builder localBuilder3 = localBuilder1.setOngoing(bool3);
      boolean bool1;
      if ((0x8 & paramNotification.flags) == 0) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      Notification.Builder localBuilder2 = localBuilder3.setOnlyAlertOnce(bool1);
      boolean bool4;
      if ((0x10 & paramNotification.flags) == 0) {
        bool4 = false;
      } else {
        bool4 = true;
      }
      Notification.Builder localBuilder4 = localBuilder2.setAutoCancel(bool4).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
      boolean bool2;
      if ((0x80 & paramNotification.flags) == 0) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      this.b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean2).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1);
      this.mExtras = new Bundle();
      if (paramBundle != null) {
        this.mExtras.putAll(paramBundle);
      }
      if (paramBoolean3) {
        this.mExtras.putBoolean("android.support.localOnly", true);
      }
      if (paramString1 != null)
      {
        this.mExtras.putString("android.support.groupKey", paramString1);
        if (!paramBoolean4) {
          this.mExtras.putBoolean("android.support.useSideChannel", true);
        } else {
          this.mExtras.putBoolean("android.support.isGroupSummary", true);
        }
      }
      if (paramString2 != null) {
        this.mExtras.putString("android.support.sortKey", paramString2);
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, paramAction));
    }
    
    public Notification build()
    {
      Notification localNotification = this.b.build();
      Bundle localBundle1 = NotificationCompatJellybean.getExtras(localNotification);
      Bundle localBundle2 = new Bundle(this.mExtras);
      Object localObject = this.mExtras.keySet().iterator();
      for (;;)
      {
        if (!((Iterator)localObject).hasNext())
        {
          localBundle1.putAll(localBundle2);
          localObject = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
          if (localObject != null) {
            NotificationCompatJellybean.getExtras(localNotification).putSparseParcelableArray("android.support.actionExtras", (SparseArray)localObject);
          }
          return localNotification;
        }
        String str = (String)((Iterator)localObject).next();
        if (localBundle1.containsKey(str)) {
          localBundle2.remove(str);
        }
      }
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\NotificationCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */