package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.RemoteInput.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

class NotificationCompatApi21
{
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_CALL = "call";
  public static final String CATEGORY_EMAIL = "email";
  public static final String CATEGORY_ERROR = "err";
  public static final String CATEGORY_EVENT = "event";
  public static final String CATEGORY_MESSAGE = "msg";
  public static final String CATEGORY_PROGRESS = "progress";
  public static final String CATEGORY_PROMO = "promo";
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";
  private static final String KEY_AUTHOR = "author";
  private static final String KEY_MESSAGES = "messages";
  private static final String KEY_ON_READ = "on_read";
  private static final String KEY_ON_REPLY = "on_reply";
  private static final String KEY_PARTICIPANTS = "participants";
  private static final String KEY_REMOTE_INPUT = "remote_input";
  private static final String KEY_TEXT = "text";
  private static final String KEY_TIMESTAMP = "timestamp";
  
  private static RemoteInput fromCompatRemoteInput(RemoteInputCompatBase.RemoteInput paramRemoteInput)
  {
    return new RemoteInput.Builder(paramRemoteInput.getResultKey()).setLabel(paramRemoteInput.getLabel()).setChoices(paramRemoteInput.getChoices()).setAllowFreeFormInput(paramRemoteInput.getAllowFreeFormInput()).addExtras(paramRemoteInput.getExtras()).build();
  }
  
  static Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
  {
    if (paramUnreadConversation != null)
    {
      localBundle1 = new Bundle();
      String str = null;
      if ((paramUnreadConversation.getParticipants() != null) && (paramUnreadConversation.getParticipants().length > 1)) {
        str = paramUnreadConversation.getParticipants()[0];
      }
      Object localObject = new Parcelable[paramUnreadConversation.getMessages().length];
      for (int i = 0;; i++)
      {
        if (i >= localObject.length)
        {
          localBundle1.putParcelableArray("messages", (Parcelable[])localObject);
          localObject = paramUnreadConversation.getRemoteInput();
          if (localObject != null) {
            localBundle1.putParcelable("remote_input", fromCompatRemoteInput((RemoteInputCompatBase.RemoteInput)localObject));
          }
          localBundle1.putParcelable("on_reply", paramUnreadConversation.getReplyPendingIntent());
          localBundle1.putParcelable("on_read", paramUnreadConversation.getReadPendingIntent());
          localBundle1.putStringArray("participants", paramUnreadConversation.getParticipants());
          localBundle1.putLong("timestamp", paramUnreadConversation.getLatestTimestamp());
          break;
        }
        Bundle localBundle2 = new Bundle();
        localBundle2.putString("text", paramUnreadConversation.getMessages()[i]);
        localBundle2.putString("author", str);
        localObject[i] = localBundle2;
      }
    }
    Bundle localBundle1 = null;
    return localBundle1;
  }
  
  public static String getCategory(Notification paramNotification)
  {
    return paramNotification.category;
  }
  
  static NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    Object localObject1 = null;
    if (paramBundle != null)
    {
      Object localObject2 = paramBundle.getParcelableArray("messages");
      String[] arrayOfString1 = null;
      if (localObject2 != null)
      {
        arrayOfString1 = new String[localObject2.length];
        int i = 1;
        int j = 0;
        while (j < arrayOfString1.length) {
          if ((localObject2[j] instanceof Bundle))
          {
            arrayOfString1[j] = ((Bundle)localObject2[j]).getString("text");
            if (arrayOfString1[j] != null) {
              j++;
            } else {
              i = 0;
            }
          }
          else
          {
            i = 0;
          }
        }
        if (i != 0) {
          arrayOfString1 = arrayOfString1;
        }
      }
      else
      {
        PendingIntent localPendingIntent2 = (PendingIntent)paramBundle.getParcelable("on_read");
        PendingIntent localPendingIntent1 = (PendingIntent)paramBundle.getParcelable("on_reply");
        localObject2 = (RemoteInput)paramBundle.getParcelable("remote_input");
        String[] arrayOfString2 = paramBundle.getStringArray("participants");
        if ((arrayOfString2 != null) && (arrayOfString2.length == 1))
        {
          if (localObject2 != null) {
            localObject1 = toCompatRemoteInput((RemoteInput)localObject2, paramFactory1);
          }
          localObject1 = paramFactory.build(arrayOfString1, (RemoteInputCompatBase.RemoteInput)localObject1, localPendingIntent1, localPendingIntent2, arrayOfString2, paramBundle.getLong("timestamp"));
        }
      }
    }
    return (NotificationCompatBase.UnreadConversation)localObject1;
  }
  
  private static RemoteInputCompatBase.RemoteInput toCompatRemoteInput(RemoteInput paramRemoteInput, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    return paramFactory.build(paramRemoteInput.getResultKey(), paramRemoteInput.getLabel(), paramRemoteInput.getChoices(), paramRemoteInput.getAllowFreeFormInput(), paramRemoteInput.getExtras());
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    
    public Builder(Context paramContext, Notification paramNotification1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, String paramString1, ArrayList<String> paramArrayList, Bundle paramBundle, int paramInt5, int paramInt6, Notification paramNotification2, String paramString2, boolean paramBoolean5, String paramString3)
    {
      Notification.Builder localBuilder2 = new Notification.Builder(paramContext).setWhen(paramNotification1.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification1.icon, paramNotification1.iconLevel).setContent(paramNotification1.contentView).setTicker(paramNotification1.tickerText, paramRemoteViews).setSound(paramNotification1.sound, paramNotification1.audioStreamType).setVibrate(paramNotification1.vibrate).setLights(paramNotification1.ledARGB, paramNotification1.ledOnMS, paramNotification1.ledOffMS);
      boolean bool1;
      if ((0x2 & paramNotification1.flags) == 0) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      Notification.Builder localBuilder1 = localBuilder2.setOngoing(bool1);
      boolean bool3;
      if ((0x8 & paramNotification1.flags) == 0) {
        bool3 = false;
      } else {
        bool3 = true;
      }
      Object localObject = localBuilder1.setOnlyAlertOnce(bool3);
      boolean bool2;
      if ((0x10 & paramNotification1.flags) == 0) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      localObject = ((Notification.Builder)localObject).setAutoCancel(bool2).setDefaults(paramNotification1.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification1.deleteIntent);
      if ((0x80 & paramNotification1.flags) == 0) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      this.b = ((Notification.Builder)localObject).setFullScreenIntent(paramPendingIntent2, bool2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setExtras(paramBundle).setGroup(paramString2).setGroupSummary(paramBoolean5).setSortKey(paramString3).setCategory(paramString1).setColor(paramInt5).setVisibility(paramInt6).setPublicVersion(paramNotification2);
      Iterator localIterator = paramArrayList.iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          return;
        }
        localObject = (String)localIterator.next();
        this.b.addPerson((String)localObject);
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(this.b, paramAction);
    }
    
    public Notification build()
    {
      return this.b.build();
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\NotificationCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */