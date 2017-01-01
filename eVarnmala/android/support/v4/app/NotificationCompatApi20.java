package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;

class NotificationCompatApi20
{
  public static void addAction(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    RemoteInput[] arrayOfRemoteInput;
    int i;
    if (paramAction.getRemoteInputs() != null)
    {
      arrayOfRemoteInput = RemoteInputCompatApi20.fromCompat(paramAction.getRemoteInputs());
      i = arrayOfRemoteInput.length;
    }
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        if (paramAction.getExtras() != null) {
          localBuilder.addExtras(paramAction.getExtras());
        }
        paramBuilder.addAction(localBuilder.build());
        return;
      }
      localBuilder.addRemoteInput(arrayOfRemoteInput[j]);
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return getActionCompatFromAction(paramNotification.actions[paramInt], paramFactory, paramFactory1);
  }
  
  private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action paramAction, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.toCompat(paramAction.getRemoteInputs(), paramFactory1);
    return paramFactory.build(paramAction.icon, paramAction.title, paramAction.actionIntent, paramAction.getExtras(), arrayOfRemoteInput);
  }
  
  private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent()).addExtras(paramAction.getExtras());
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = paramAction.getRemoteInputs();
    RemoteInput[] arrayOfRemoteInput1;
    int j;
    if (arrayOfRemoteInput != null)
    {
      arrayOfRemoteInput1 = RemoteInputCompatApi20.fromCompat(arrayOfRemoteInput);
      j = arrayOfRemoteInput1.length;
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return localBuilder.build();
      }
      localBuilder.addRemoteInput(arrayOfRemoteInput1[i]);
    }
  }
  
  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    if (paramArrayList != null)
    {
      arrayOfAction = paramFactory.newArray(paramArrayList.size());
      for (int i = 0; i < arrayOfAction.length; i++) {
        arrayOfAction[i] = getActionCompatFromAction((Notification.Action)paramArrayList.get(i), paramFactory, paramFactory1);
      }
    }
    NotificationCompatBase.Action[] arrayOfAction = null;
    return arrayOfAction;
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return paramNotification.getGroup();
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    boolean bool;
    if ((0x100 & paramNotification.flags) == 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    if (paramArrayOfAction != null)
    {
      localArrayList = new ArrayList(paramArrayOfAction.length);
      int i = paramArrayOfAction.length;
      for (int j = 0; j < i; j++) {
        localArrayList.add(getActionFromActionCompat(paramArrayOfAction[j]));
      }
    }
    ArrayList localArrayList = null;
    return localArrayList;
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return paramNotification.getSortKey();
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    boolean bool;
    if ((0x200 & paramNotification.flags) == 0) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2)
    {
      Notification.Builder localBuilder2 = new Notification.Builder(paramContext).setWhen(paramNotification.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      boolean bool1;
      if ((0x2 & paramNotification.flags) == 0) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      Notification.Builder localBuilder1 = localBuilder2.setOngoing(bool1);
      boolean bool3;
      if ((0x8 & paramNotification.flags) == 0) {
        bool3 = false;
      } else {
        bool3 = true;
      }
      Notification.Builder localBuilder3 = localBuilder1.setOnlyAlertOnce(bool3);
      boolean bool2;
      if ((0x10 & paramNotification.flags) == 0) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      localBuilder3 = localBuilder3.setAutoCancel(bool2).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
      if ((0x80 & paramNotification.flags) == 0) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      this.b = localBuilder3.setFullScreenIntent(paramPendingIntent2, bool2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setGroup(paramString1).setGroupSummary(paramBoolean5).setSortKey(paramString2);
      this.mExtras = new Bundle();
      if (paramBundle != null) {
        this.mExtras.putAll(paramBundle);
      }
      if ((paramArrayList != null) && (!paramArrayList.isEmpty())) {
        this.mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(this.b, paramAction);
    }
    
    public Notification build()
    {
      this.b.setExtras(this.mExtras);
      return this.b.build();
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\NotificationCompatApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */