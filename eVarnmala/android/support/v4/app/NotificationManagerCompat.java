package android.support.v4.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NotificationManagerCompat
{
  public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
  public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  private static final Impl IMPL;
  static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
  private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
  private static final int SIDE_CHANNEL_BIND_FLAGS = IMPL.getSideChannelBindFlags();
  private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
  private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
  private static final String TAG = "NotifManCompat";
  private static Set<String> sEnabledNotificationListenerPackages;
  private static String sEnabledNotificationListeners;
  private static final Object sEnabledNotificationListenersLock = new Object();
  private static final Object sLock;
  private static SideChannelManager sSideChannelManager;
  private final Context mContext;
  private final NotificationManager mNotificationManager;
  
  static
  {
    sEnabledNotificationListenerPackages = new HashSet();
    sLock = new Object();
    if (Build.VERSION.SDK_INT < 14)
    {
      if (Build.VERSION.SDK_INT < 5) {
        IMPL = new ImplBase();
      } else {
        IMPL = new ImplEclair();
      }
    }
    else {
      IMPL = new ImplIceCreamSandwich();
    }
  }
  
  private NotificationManagerCompat(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
  }
  
  public static NotificationManagerCompat from(Context paramContext)
  {
    return new NotificationManagerCompat(paramContext);
  }
  
  public static Set<String> getEnabledListenerPackages(Context paramContext)
  {
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "enabled_notification_listeners");
    HashSet localHashSet;
    if ((str != null) && (!str.equals(sEnabledNotificationListeners)))
    {
      String[] arrayOfString = str.split(":");
      localHashSet = new HashSet(arrayOfString.length);
      int j = arrayOfString.length;
      for (int i = 0; i < j; i++)
      {
        ??? = ComponentName.unflattenFromString(arrayOfString[i]);
        if (??? != null) {
          localHashSet.add(((ComponentName)???).getPackageName());
        }
      }
    }
    synchronized (sEnabledNotificationListenersLock)
    {
      sEnabledNotificationListenerPackages = localHashSet;
      sEnabledNotificationListeners = str;
      return sEnabledNotificationListenerPackages;
    }
  }
  
  private void pushSideChannelQueue(Task paramTask)
  {
    synchronized (sLock)
    {
      if (sSideChannelManager == null) {
        sSideChannelManager = new SideChannelManager(this.mContext.getApplicationContext());
      }
      sSideChannelManager.queueTask(paramTask);
      return;
    }
  }
  
  private static boolean useSideChannelForNotification(Notification paramNotification)
  {
    Bundle localBundle = NotificationCompat.getExtras(paramNotification);
    boolean bool;
    if ((localBundle == null) || (!localBundle.getBoolean("android.support.useSideChannel"))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void cancel(int paramInt)
  {
    cancel(null, paramInt);
  }
  
  public void cancel(String paramString, int paramInt)
  {
    IMPL.cancelNotification(this.mNotificationManager, paramString, paramInt);
    if (Build.VERSION.SDK_INT <= 19) {
      pushSideChannelQueue(new CancelTask(this.mContext.getPackageName(), paramInt, paramString));
    }
  }
  
  public void cancelAll()
  {
    this.mNotificationManager.cancelAll();
    if (Build.VERSION.SDK_INT <= 19) {
      pushSideChannelQueue(new CancelTask(this.mContext.getPackageName()));
    }
  }
  
  public void notify(int paramInt, Notification paramNotification)
  {
    notify(null, paramInt, paramNotification);
  }
  
  public void notify(String paramString, int paramInt, Notification paramNotification)
  {
    if (!useSideChannelForNotification(paramNotification))
    {
      IMPL.postNotification(this.mNotificationManager, paramString, paramInt, paramNotification);
    }
    else
    {
      pushSideChannelQueue(new NotifyTask(this.mContext.getPackageName(), paramInt, paramString, paramNotification));
      IMPL.cancelNotification(this.mNotificationManager, paramString, paramInt);
    }
  }
  
  private static class CancelTask
    implements NotificationManagerCompat.Task
  {
    final boolean all;
    final int id;
    final String packageName;
    final String tag;
    
    public CancelTask(String paramString)
    {
      this.packageName = paramString;
      this.id = 0;
      this.tag = null;
      this.all = true;
    }
    
    public CancelTask(String paramString1, int paramInt, String paramString2)
    {
      this.packageName = paramString1;
      this.id = paramInt;
      this.tag = paramString2;
      this.all = false;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      if (!this.all) {
        paramINotificationSideChannel.cancel(this.packageName, this.id, this.tag);
      } else {
        paramINotificationSideChannel.cancelAll(this.packageName);
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("CancelTask[");
      localStringBuilder.append("packageName:").append(this.packageName);
      localStringBuilder.append(", id:").append(this.id);
      localStringBuilder.append(", tag:").append(this.tag);
      localStringBuilder.append(", all:").append(this.all);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class NotifyTask
    implements NotificationManagerCompat.Task
  {
    final int id;
    final Notification notif;
    final String packageName;
    final String tag;
    
    public NotifyTask(String paramString1, int paramInt, String paramString2, Notification paramNotification)
    {
      this.packageName = paramString1;
      this.id = paramInt;
      this.tag = paramString2;
      this.notif = paramNotification;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      paramINotificationSideChannel.notify(this.packageName, this.id, this.tag, this.notif);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("NotifyTask[");
      localStringBuilder.append("packageName:").append(this.packageName);
      localStringBuilder.append(", id:").append(this.id);
      localStringBuilder.append(", tag:").append(this.tag);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static abstract interface Task
  {
    public abstract void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException;
  }
  
  private static class ServiceConnectedEvent
  {
    final ComponentName componentName;
    final IBinder iBinder;
    
    public ServiceConnectedEvent(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.componentName = paramComponentName;
      this.iBinder = paramIBinder;
    }
  }
  
  private static class SideChannelManager
    implements Handler.Callback, ServiceConnection
  {
    private static final String KEY_BINDER = "binder";
    private static final int MSG_QUEUE_TASK = 0;
    private static final int MSG_RETRY_LISTENER_QUEUE = 3;
    private static final int MSG_SERVICE_CONNECTED = 1;
    private static final int MSG_SERVICE_DISCONNECTED = 2;
    private Set<String> mCachedEnabledPackages = new HashSet();
    private final Context mContext;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();
    
    public SideChannelManager(Context paramContext)
    {
      this.mContext = paramContext;
      this.mHandlerThread = new HandlerThread("NotificationManagerCompat");
      this.mHandlerThread.start();
      this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
    }
    
    private boolean ensureServiceBound(ListenerRecord paramListenerRecord)
    {
      boolean bool;
      if (!paramListenerRecord.bound)
      {
        Intent localIntent = new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL").setComponent(paramListenerRecord.componentName);
        paramListenerRecord.bound = this.mContext.bindService(localIntent, this, NotificationManagerCompat.SIDE_CHANNEL_BIND_FLAGS);
        if (!paramListenerRecord.bound)
        {
          Log.w("NotifManCompat", "Unable to bind to listener " + paramListenerRecord.componentName);
          this.mContext.unbindService(this);
        }
        else
        {
          paramListenerRecord.retryCount = 0;
        }
        bool = paramListenerRecord.bound;
      }
      else
      {
        bool = true;
      }
      return bool;
    }
    
    private void ensureServiceUnbound(ListenerRecord paramListenerRecord)
    {
      if (paramListenerRecord.bound)
      {
        this.mContext.unbindService(this);
        paramListenerRecord.bound = false;
      }
      paramListenerRecord.service = null;
    }
    
    private void handleQueueTask(NotificationManagerCompat.Task paramTask)
    {
      updateListenerMap();
      Iterator localIterator = this.mRecordMap.values().iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          return;
        }
        ListenerRecord localListenerRecord = (ListenerRecord)localIterator.next();
        localListenerRecord.taskQueue.add(paramTask);
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleRetryListenerQueue(ComponentName paramComponentName)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (localListenerRecord != null) {
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (localListenerRecord != null)
      {
        localListenerRecord.service = INotificationSideChannel.Stub.asInterface(paramIBinder);
        localListenerRecord.retryCount = 0;
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleServiceDisconnected(ComponentName paramComponentName)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (localListenerRecord != null) {
        ensureServiceUnbound(localListenerRecord);
      }
    }
    
    private void processListenerQueue(ListenerRecord paramListenerRecord)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Processing component " + paramListenerRecord.componentName + ", " + paramListenerRecord.taskQueue.size() + " queued tasks");
      }
      if (paramListenerRecord.taskQueue.isEmpty()) {}
      for (;;)
      {
        return;
        if ((!ensureServiceBound(paramListenerRecord)) || (paramListenerRecord.service == null)) {
          scheduleListenerRetry(paramListenerRecord);
        }
        try
        {
          Object localObject;
          do
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Sending task " + localObject);
            }
            ((NotificationManagerCompat.Task)localObject).send(paramListenerRecord.service);
            paramListenerRecord.taskQueue.remove();
            localObject = (NotificationManagerCompat.Task)paramListenerRecord.taskQueue.peek();
          } while (localObject != null);
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Remote service has died: " + paramListenerRecord.componentName);
            }
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("NotifManCompat", "RemoteException communicating with " + paramListenerRecord.componentName, localRemoteException);
          }
        }
        if (!paramListenerRecord.taskQueue.isEmpty()) {
          scheduleListenerRetry(paramListenerRecord);
        }
      }
    }
    
    private void scheduleListenerRetry(ListenerRecord paramListenerRecord)
    {
      if (!this.mHandler.hasMessages(3, paramListenerRecord.componentName))
      {
        paramListenerRecord.retryCount = (1 + paramListenerRecord.retryCount);
        if (paramListenerRecord.retryCount <= 6)
        {
          int i = 1000 * (1 << -1 + paramListenerRecord.retryCount);
          if (Log.isLoggable("NotifManCompat", 3)) {
            Log.d("NotifManCompat", "Scheduling retry for " + i + " ms");
          }
          Message localMessage = this.mHandler.obtainMessage(3, paramListenerRecord.componentName);
          this.mHandler.sendMessageDelayed(localMessage, i);
        }
        else
        {
          Log.w("NotifManCompat", "Giving up on delivering " + paramListenerRecord.taskQueue.size() + " tasks to " + paramListenerRecord.componentName + " after " + paramListenerRecord.retryCount + " retries");
          paramListenerRecord.taskQueue.clear();
        }
      }
    }
    
    private void updateListenerMap()
    {
      Object localObject1 = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
      Object localObject2;
      HashSet localHashSet;
      Iterator localIterator;
      if (!((Set)localObject1).equals(this.mCachedEnabledPackages))
      {
        this.mCachedEnabledPackages = ((Set)localObject1);
        localObject2 = this.mContext.getPackageManager().queryIntentServices(new Intent().setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 4);
        localHashSet = new HashSet();
        localIterator = ((List)localObject2).iterator();
      }
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          localObject1 = localHashSet.iterator();
          for (;;)
          {
            if (!((Iterator)localObject1).hasNext())
            {
              localObject1 = this.mRecordMap.entrySet().iterator();
              for (;;)
              {
                if (!((Iterator)localObject1).hasNext()) {
                  return;
                }
                localObject2 = (Map.Entry)((Iterator)localObject1).next();
                if (!localHashSet.contains(((Map.Entry)localObject2).getKey()))
                {
                  if (Log.isLoggable("NotifManCompat", 3)) {
                    Log.d("NotifManCompat", "Removing listener record for " + ((Map.Entry)localObject2).getKey());
                  }
                  ensureServiceUnbound((ListenerRecord)((Map.Entry)localObject2).getValue());
                  ((Iterator)localObject1).remove();
                }
              }
            }
            localObject2 = (ComponentName)((Iterator)localObject1).next();
            if (!this.mRecordMap.containsKey(localObject2))
            {
              if (Log.isLoggable("NotifManCompat", 3)) {
                Log.d("NotifManCompat", "Adding listener record for " + localObject2);
              }
              this.mRecordMap.put(localObject2, new ListenerRecord((ComponentName)localObject2));
            }
          }
        }
        ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
        if (((Set)localObject1).contains(localResolveInfo.serviceInfo.packageName))
        {
          localObject2 = new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name);
          if (localResolveInfo.serviceInfo.permission == null) {
            localHashSet.add(localObject2);
          } else {
            Log.w("NotifManCompat", "Permission present on component " + localObject2 + ", not adding listener record.");
          }
        }
      }
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      int i;
      boolean bool;
      switch (paramMessage.what)
      {
      default: 
        i = 0;
        break;
      case 0: 
        handleQueueTask((NotificationManagerCompat.Task)paramMessage.obj);
        i = 1;
        break;
      case 1: 
        NotificationManagerCompat.ServiceConnectedEvent localServiceConnectedEvent = (NotificationManagerCompat.ServiceConnectedEvent)paramMessage.obj;
        handleServiceConnected(localServiceConnectedEvent.componentName, localServiceConnectedEvent.iBinder);
        bool = true;
        break;
      case 2: 
        handleServiceDisconnected((ComponentName)paramMessage.obj);
        bool = true;
        break;
      case 3: 
        handleRetryListenerQueue((ComponentName)paramMessage.obj);
        bool = true;
      }
      return bool;
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Connected to service " + paramComponentName);
      }
      this.mHandler.obtainMessage(1, new NotificationManagerCompat.ServiceConnectedEvent(paramComponentName, paramIBinder)).sendToTarget();
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Disconnected from service " + paramComponentName);
      }
      this.mHandler.obtainMessage(2, paramComponentName).sendToTarget();
    }
    
    public void queueTask(NotificationManagerCompat.Task paramTask)
    {
      this.mHandler.obtainMessage(0, paramTask).sendToTarget();
    }
    
    private static class ListenerRecord
    {
      public boolean bound = false;
      public final ComponentName componentName;
      public int retryCount = 0;
      public INotificationSideChannel service;
      public LinkedList<NotificationManagerCompat.Task> taskQueue = new LinkedList();
      
      public ListenerRecord(ComponentName paramComponentName)
      {
        this.componentName = paramComponentName;
      }
    }
  }
  
  static class ImplIceCreamSandwich
    extends NotificationManagerCompat.ImplEclair
  {
    public int getSideChannelBindFlags()
    {
      return 33;
    }
  }
  
  static class ImplEclair
    extends NotificationManagerCompat.ImplBase
  {
    public void cancelNotification(NotificationManager paramNotificationManager, String paramString, int paramInt)
    {
      NotificationManagerCompatEclair.cancelNotification(paramNotificationManager, paramString, paramInt);
    }
    
    public void postNotification(NotificationManager paramNotificationManager, String paramString, int paramInt, Notification paramNotification)
    {
      NotificationManagerCompatEclair.postNotification(paramNotificationManager, paramString, paramInt, paramNotification);
    }
  }
  
  static class ImplBase
    implements NotificationManagerCompat.Impl
  {
    public void cancelNotification(NotificationManager paramNotificationManager, String paramString, int paramInt)
    {
      paramNotificationManager.cancel(paramInt);
    }
    
    public int getSideChannelBindFlags()
    {
      return 1;
    }
    
    public void postNotification(NotificationManager paramNotificationManager, String paramString, int paramInt, Notification paramNotification)
    {
      paramNotificationManager.notify(paramInt, paramNotification);
    }
  }
  
  static abstract interface Impl
  {
    public abstract void cancelNotification(NotificationManager paramNotificationManager, String paramString, int paramInt);
    
    public abstract int getSideChannelBindFlags();
    
    public abstract void postNotification(NotificationManager paramNotificationManager, String paramString, int paramInt, Notification paramNotification);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\NotificationManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */