package android.support.v4.app;

import android.app.PendingIntent;
import android.os.Bundle;

class NotificationCompatBase
{
  public static abstract class UnreadConversation
  {
    abstract long getLatestTimestamp();
    
    abstract String[] getMessages();
    
    abstract String getParticipant();
    
    abstract String[] getParticipants();
    
    abstract PendingIntent getReadPendingIntent();
    
    abstract RemoteInputCompatBase.RemoteInput getRemoteInput();
    
    abstract PendingIntent getReplyPendingIntent();
    
    public static abstract interface Factory
    {
      public abstract NotificationCompatBase.UnreadConversation build(String[] paramArrayOfString1, RemoteInputCompatBase.RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong);
    }
  }
  
  public static abstract class Action
  {
    protected abstract PendingIntent getActionIntent();
    
    protected abstract Bundle getExtras();
    
    protected abstract int getIcon();
    
    protected abstract RemoteInputCompatBase.RemoteInput[] getRemoteInputs();
    
    protected abstract CharSequence getTitle();
    
    public static abstract interface Factory
    {
      public abstract NotificationCompatBase.Action build(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput);
      
      public abstract NotificationCompatBase.Action[] newArray(int paramInt);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\NotificationCompatBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */