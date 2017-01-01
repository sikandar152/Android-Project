package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LocalBroadcastManager
{
  private static final boolean DEBUG = false;
  static final int MSG_EXEC_PENDING_BROADCASTS = 1;
  private static final String TAG = "LocalBroadcastManager";
  private static LocalBroadcastManager mInstance;
  private static final Object mLock = new Object();
  private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap();
  private final Context mAppContext;
  private final Handler mHandler;
  private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList();
  private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap();
  
  private LocalBroadcastManager(Context paramContext)
  {
    this.mAppContext = paramContext;
    this.mHandler = new Handler(paramContext.getMainLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          super.handleMessage(paramAnonymousMessage);
          break;
        case 1: 
          LocalBroadcastManager.this.executePendingBroadcasts();
        }
      }
    };
  }
  
  private void executePendingBroadcasts()
  {
    for (;;)
    {
      int j;
      synchronized (this.mReceivers)
      {
        int i = this.mPendingBroadcasts.size();
        if (i <= 0) {
          return;
        }
        BroadcastRecord[] arrayOfBroadcastRecord = new BroadcastRecord[i];
        this.mPendingBroadcasts.toArray(arrayOfBroadcastRecord);
        this.mPendingBroadcasts.clear();
        j = 0;
        if (j >= arrayOfBroadcastRecord.length) {
          continue;
        }
        BroadcastRecord localBroadcastRecord = arrayOfBroadcastRecord[j];
        int k = 0;
        if (k < localBroadcastRecord.receivers.size())
        {
          ((ReceiverRecord)localBroadcastRecord.receivers.get(k)).receiver.onReceive(this.mAppContext, localBroadcastRecord.intent);
          k++;
        }
      }
      j++;
    }
  }
  
  public static LocalBroadcastManager getInstance(Context paramContext)
  {
    synchronized (mLock)
    {
      if (mInstance == null) {
        mInstance = new LocalBroadcastManager(paramContext.getApplicationContext());
      }
      LocalBroadcastManager localLocalBroadcastManager = mInstance;
      return localLocalBroadcastManager;
    }
  }
  
  public void registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter)
  {
    synchronized (this.mReceivers)
    {
      ReceiverRecord localReceiverRecord = new ReceiverRecord(paramIntentFilter, paramBroadcastReceiver);
      ArrayList localArrayList1 = (ArrayList)this.mReceivers.get(paramBroadcastReceiver);
      if (localArrayList1 == null)
      {
        localArrayList1 = new ArrayList(1);
        this.mReceivers.put(paramBroadcastReceiver, localArrayList1);
      }
      localArrayList1.add(paramIntentFilter);
      for (int i = 0; i < paramIntentFilter.countActions(); i++)
      {
        String str = paramIntentFilter.getAction(i);
        ArrayList localArrayList2 = (ArrayList)this.mActions.get(str);
        if (localArrayList2 == null)
        {
          localArrayList2 = new ArrayList(1);
          this.mActions.put(str, localArrayList2);
        }
        localArrayList2.add(localReceiverRecord);
      }
      return;
    }
  }
  
  public boolean sendBroadcast(Intent paramIntent)
  {
    Object localObject;
    for (;;)
    {
      String str1;
      int m;
      int n;
      int i;
      synchronized (this.mReceivers)
      {
        String str2 = paramIntent.getAction();
        str1 = paramIntent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
        Uri localUri = paramIntent.getData();
        String str3 = paramIntent.getScheme();
        Set localSet = paramIntent.getCategories();
        if ((0x8 & paramIntent.getFlags()) == 0) {
          break label498;
        }
        k = 1;
        if (k != 0) {
          Log.v("LocalBroadcastManager", "Resolving type " + str1 + " scheme " + str3 + " of intent " + paramIntent);
        }
        ArrayList localArrayList3 = (ArrayList)this.mActions.get(paramIntent.getAction());
        if (localArrayList3 != null)
        {
          if (k == 0) {
            continue;
          }
          Log.v("LocalBroadcastManager", "Action list: " + localArrayList3);
          continue;
          if (m >= localArrayList3.size()) {
            break label532;
          }
          ReceiverRecord localReceiverRecord = (ReceiverRecord)localArrayList3.get(m);
          if (k != 0) {
            Log.v("LocalBroadcastManager", "Matching against filter " + localReceiverRecord.filter);
          }
          if (localReceiverRecord.broadcasting)
          {
            if (k != 0) {
              Log.v("LocalBroadcastManager", "  Filter's target already added");
            }
          }
          else
          {
            n = localReceiverRecord.filter.match(str2, str1, str3, localUri, localSet, "LocalBroadcastManager");
            if (n >= 0)
            {
              if (k != 0) {
                Log.v("LocalBroadcastManager", "  Filter matched!  match=0x" + Integer.toHexString(n));
              }
              ArrayList localArrayList1;
              if (localArrayList1 == null) {
                localArrayList1 = new ArrayList();
              }
              localArrayList1.add(localReceiverRecord);
              localReceiverRecord.broadcasting = true;
            }
          }
        }
      }
      m++;
      continue;
      label498:
      int k = 0;
      continue;
      String str4 = "action";
      continue;
      str4 = "category";
      continue;
      str4 = "data";
      continue;
      str4 = "type";
      continue;
      label532:
      if (localObject != null) {
        int j = 0;
      }
    }
    return (boolean)localObject;
  }
  
  public void sendBroadcastSync(Intent paramIntent)
  {
    if (sendBroadcast(paramIntent)) {
      executePendingBroadcasts();
    }
  }
  
  public void unregisterReceiver(BroadcastReceiver paramBroadcastReceiver)
  {
    for (;;)
    {
      int k;
      int i;
      synchronized (this.mReceivers)
      {
        ArrayList localArrayList2 = (ArrayList)this.mReceivers.remove(paramBroadcastReceiver);
        if (localArrayList2 != null) {
          break label163;
        }
        break label162;
        if (j < localArrayList2.size())
        {
          IntentFilter localIntentFilter = (IntentFilter)localArrayList2.get(j);
          k = 0;
          if (k >= localIntentFilter.countActions()) {
            break label181;
          }
          String str = localIntentFilter.getAction(k);
          ArrayList localArrayList1 = (ArrayList)this.mActions.get(str);
          if (localArrayList1 == null) {
            break label175;
          }
          i = 0;
          if (i < localArrayList1.size())
          {
            if (((ReceiverRecord)localArrayList1.get(i)).receiver != paramBroadcastReceiver) {
              break label169;
            }
            localArrayList1.remove(i);
            i--;
            break label169;
          }
          if (localArrayList1.size() > 0) {
            break label175;
          }
          this.mActions.remove(str);
          break label175;
        }
      }
      label162:
      return;
      label163:
      int j = 0;
      continue;
      label169:
      i++;
      continue;
      label175:
      k++;
      continue;
      label181:
      j++;
    }
  }
  
  private static class BroadcastRecord
  {
    final Intent intent;
    final ArrayList<LocalBroadcastManager.ReceiverRecord> receivers;
    
    BroadcastRecord(Intent paramIntent, ArrayList<LocalBroadcastManager.ReceiverRecord> paramArrayList)
    {
      this.intent = paramIntent;
      this.receivers = paramArrayList;
    }
  }
  
  private static class ReceiverRecord
  {
    boolean broadcasting;
    final IntentFilter filter;
    final BroadcastReceiver receiver;
    
    ReceiverRecord(IntentFilter paramIntentFilter, BroadcastReceiver paramBroadcastReceiver)
    {
      this.filter = paramIntentFilter;
      this.receiver = paramBroadcastReceiver;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Receiver{");
      localStringBuilder.append(this.receiver);
      localStringBuilder.append(" filter=");
      localStringBuilder.append(this.filter);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\content\LocalBroadcastManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */