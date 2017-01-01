package android.support.v7.internal.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ActivityChooserModel
  extends DataSetObservable
{
  private static final String ATTRIBUTE_ACTIVITY = "activity";
  private static final String ATTRIBUTE_TIME = "time";
  private static final String ATTRIBUTE_WEIGHT = "weight";
  private static final boolean DEBUG = false;
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  private static final int INVALID_INDEX = -1;
  private static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  private static final String TAG_HISTORICAL_RECORD = "historical-record";
  private static final String TAG_HISTORICAL_RECORDS = "historical-records";
  private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivities = new ArrayList();
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter = new DefaultSorter(null);
  private boolean mCanReadHistoricalData = true;
  private final Context mContext;
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  private boolean mHistoricalRecordsChanged = true;
  private final String mHistoryFileName;
  private int mHistoryMaxSize = 50;
  private final Object mInstanceLock = new Object();
  private Intent mIntent;
  private boolean mReadShareHistoryCalled = false;
  private boolean mReloadActivities = false;
  
  private ActivityChooserModel(Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    if ((TextUtils.isEmpty(paramString)) || (paramString.endsWith(".xml"))) {
      this.mHistoryFileName = paramString;
    } else {
      this.mHistoryFileName = (paramString + ".xml");
    }
  }
  
  private boolean addHisoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
    if (bool)
    {
      this.mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    }
    return bool;
  }
  
  private void ensureConsistentState()
  {
    boolean bool = loadActivitiesIfNeeded() | readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if (bool)
    {
      sortActivitiesIfNeeded();
      notifyChanged();
    }
  }
  
  public static ActivityChooserModel get(Context paramContext, String paramString)
  {
    synchronized (sRegistryLock)
    {
      ActivityChooserModel localActivityChooserModel = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      if (localActivityChooserModel == null)
      {
        localActivityChooserModel = new ActivityChooserModel(paramContext, paramString);
        sDataModelRegistry.put(paramString, localActivityChooserModel);
      }
      return localActivityChooserModel;
    }
  }
  
  private boolean loadActivitiesIfNeeded()
  {
    int i = 0;
    List localList;
    int j;
    if ((this.mReloadActivities) && (this.mIntent != null))
    {
      this.mReloadActivities = false;
      this.mActivities.clear();
      localList = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
      j = localList.size();
    }
    for (i = 0;; i++)
    {
      if (i >= j)
      {
        i = 1;
        return i;
      }
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      this.mActivities.add(new ActivityResolveInfo(localResolveInfo));
    }
  }
  
  private void persistHistoricalDataIfNeeded()
  {
    if (this.mReadShareHistoryCalled)
    {
      if (this.mHistoricalRecordsChanged)
      {
        this.mHistoricalRecordsChanged = false;
        if (!TextUtils.isEmpty(this.mHistoryFileName))
        {
          PersistHistoryAsyncTask localPersistHistoryAsyncTask = new PersistHistoryAsyncTask(null);
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = this.mHistoricalRecords;
          arrayOfObject[1] = this.mHistoryFileName;
          AsyncTaskCompat.executeParallel(localPersistHistoryAsyncTask, arrayOfObject);
        }
      }
      return;
    }
    throw new IllegalStateException("No preceding call to #readHistoricalData");
  }
  
  private void pruneExcessiveHistoricalRecordsIfNeeded()
  {
    int j = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
    if (j > 0) {
      this.mHistoricalRecordsChanged = true;
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return;
      }
      ((HistoricalRecord)this.mHistoricalRecords.remove(0));
    }
  }
  
  private boolean readHistoricalDataIfNeeded()
  {
    boolean bool = true;
    if ((!this.mCanReadHistoricalData) || (!this.mHistoricalRecordsChanged) || (TextUtils.isEmpty(this.mHistoryFileName)))
    {
      bool = false;
    }
    else
    {
      this.mCanReadHistoricalData = false;
      this.mReadShareHistoryCalled = bool;
      readHistoricalDataImpl();
    }
    return bool;
  }
  
  private void readHistoricalDataImpl()
  {
    try
    {
      FileInputStream localFileInputStream = this.mContext.openFileInput(this.mHistoryFileName);
      try
      {
        XmlPullParser localXmlPullParser = Xml.newPullParser();
        localXmlPullParser.setInput(localFileInputStream, null);
        for (int i = 0; (i != 1) && (i != 2); i = localXmlPullParser.next()) {}
        if (!"historical-records".equals(localXmlPullParser.getName())) {
          throw new XmlPullParserException("Share records file does not start with historical-records tag.");
        }
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, localXmlPullParserException);
        if (localFileInputStream != null)
        {
          try
          {
            localFileInputStream.close();
          }
          catch (IOException localIOException2) {}
          localList = this.mHistoricalRecords;
          localList.clear();
          int j;
          do
          {
            j = localXmlPullParserException.next();
            if (j == 1)
            {
              if (localFileInputStream == null) {
                break;
              }
              try
              {
                localFileInputStream.close();
              }
              catch (IOException localIOException3) {}
            }
          } while ((j == 3) || (j == 4));
          if (!"historical-record".equals(localXmlPullParserException.getName())) {
            throw new XmlPullParserException("Share records file not well-formed.");
          }
        }
      }
      catch (IOException localIOException1)
      {
        for (;;)
        {
          List localList;
          Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, localIOException1);
          if (localFileInputStream == null) {
            break;
          }
          try
          {
            localFileInputStream.close();
          }
          catch (IOException localIOException4) {}
          localList.add(new HistoricalRecord(localIOException1.getAttributeValue(null, "activity"), Long.parseLong(localIOException1.getAttributeValue(null, "time")), Float.parseFloat(localIOException1.getAttributeValue(null, "weight"))));
        }
      }
      finally
      {
        if (localFileInputStream != null) {}
        try
        {
          localFileInputStream.close();
          throw ((Throwable)localObject);
        }
        catch (IOException localIOException5)
        {
          for (;;) {}
        }
      }
      return;
    }
    catch (FileNotFoundException localFileNotFoundException) {}
  }
  
  private boolean sortActivitiesIfNeeded()
  {
    boolean bool;
    if ((this.mActivitySorter == null) || (this.mIntent == null) || (this.mActivities.isEmpty()) || (this.mHistoricalRecords.isEmpty()))
    {
      bool = false;
    }
    else
    {
      this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
      bool = true;
    }
    return bool;
  }
  
  public Intent chooseActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      Object localObject2;
      if (this.mIntent == null)
      {
        localObject2 = null;
      }
      else
      {
        ensureConsistentState();
        localObject2 = (ActivityResolveInfo)this.mActivities.get(paramInt);
        ComponentName localComponentName = new ComponentName(((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.packageName, ((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.name);
        localObject2 = new Intent(this.mIntent);
        ((Intent)localObject2).setComponent(localComponentName);
        if (this.mActivityChoserModelPolicy != null)
        {
          Intent localIntent2 = new Intent((Intent)localObject2);
          if (this.mActivityChoserModelPolicy.onChooseActivity(this, localIntent2)) {
            return null;
          }
        }
        addHisoricalRecord(new HistoricalRecord(localComponentName, System.currentTimeMillis(), 1.0F));
      }
    }
    return localIntent1;
  }
  
  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }
  
  public int getActivityCount()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mActivities.size();
      return i;
    }
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    for (;;)
    {
      int j;
      synchronized (this.mInstanceLock)
      {
        ensureConsistentState();
        List localList = this.mActivities;
        int i = localList.size();
        j = 0;
        if (j < i) {
          if (((ActivityResolveInfo)localList.get(j)).resolveInfo != paramResolveInfo) {
            break label74;
          }
        } else {
          j = -1;
        }
      }
      return j;
      label74:
      j++;
    }
  }
  
  public ResolveInfo getDefaultActivity()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo1;
      if (!this.mActivities.isEmpty()) {
        localResolveInfo1 = ((ActivityResolveInfo)this.mActivities.get(0)).resolveInfo;
      } else {
        localResolveInfo1 = null;
      }
    }
    return localResolveInfo2;
  }
  
  public int getHistoryMaxSize()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mHistoryMaxSize;
      return i;
    }
  }
  
  public int getHistorySize()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mHistoricalRecords.size();
      return i;
    }
  }
  
  public Intent getIntent()
  {
    synchronized (this.mInstanceLock)
    {
      Intent localIntent = this.mIntent;
      return localIntent;
    }
  }
  
  public void setActivitySorter(ActivitySorter paramActivitySorter)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mActivitySorter != paramActivitySorter)
      {
        this.mActivitySorter = paramActivitySorter;
        if (sortActivitiesIfNeeded()) {
          notifyChanged();
        }
      }
    }
  }
  
  public void setDefaultActivity(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mInstanceLock)
      {
        ensureConsistentState();
        ActivityResolveInfo localActivityResolveInfo1 = (ActivityResolveInfo)this.mActivities.get(paramInt);
        ActivityResolveInfo localActivityResolveInfo2 = (ActivityResolveInfo)this.mActivities.get(0);
        if (localActivityResolveInfo2 != null)
        {
          f = 5.0F + (localActivityResolveInfo2.weight - localActivityResolveInfo1.weight);
          addHisoricalRecord(new HistoricalRecord(new ComponentName(localActivityResolveInfo1.resolveInfo.activityInfo.packageName, localActivityResolveInfo1.resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
          return;
        }
      }
      float f = 1.0F;
    }
  }
  
  public void setHistoryMaxSize(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mHistoryMaxSize != paramInt)
      {
        this.mHistoryMaxSize = paramInt;
        pruneExcessiveHistoricalRecordsIfNeeded();
        if (sortActivitiesIfNeeded()) {
          notifyChanged();
        }
      }
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent != paramIntent)
      {
        this.mIntent = paramIntent;
        this.mReloadActivities = true;
        ensureConsistentState();
      }
    }
  }
  
  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener)
  {
    synchronized (this.mInstanceLock)
    {
      this.mActivityChoserModelPolicy = paramOnChooseActivityListener;
      return;
    }
  }
  
  private final class PersistHistoryAsyncTask
    extends AsyncTask<Object, Void, Void>
  {
    private PersistHistoryAsyncTask() {}
    
    /* Error */
    public Void doInBackground(Object... paramVarArgs)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast 35	java/util/List
      //   6: astore_3
      //   7: aload_1
      //   8: iconst_1
      //   9: aaload
      //   10: checkcast 37	java/lang/String
      //   13: astore_2
      //   14: aload_0
      //   15: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   18: invokestatic 41	android/support/v7/internal/widget/ActivityChooserModel:access$200	(Landroid/support/v7/internal/widget/ActivityChooserModel;)Landroid/content/Context;
      //   21: aload_2
      //   22: iconst_0
      //   23: invokevirtual 47	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   26: astore_2
      //   27: invokestatic 53	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   30: astore 6
      //   32: aload 6
      //   34: aload_2
      //   35: aconst_null
      //   36: invokeinterface 59 3 0
      //   41: aload 6
      //   43: ldc 61
      //   45: iconst_1
      //   46: invokestatic 67	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   49: invokeinterface 71 3 0
      //   54: aload 6
      //   56: aconst_null
      //   57: ldc 73
      //   59: invokeinterface 77 3 0
      //   64: pop
      //   65: aload_3
      //   66: invokeinterface 81 1 0
      //   71: istore 7
      //   73: iconst_0
      //   74: istore 5
      //   76: iload 5
      //   78: iload 7
      //   80: if_icmpge +130 -> 210
      //   83: aload_3
      //   84: iconst_0
      //   85: invokeinterface 85 2 0
      //   90: checkcast 87	android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord
      //   93: astore 4
      //   95: aload 6
      //   97: aconst_null
      //   98: ldc 89
      //   100: invokeinterface 77 3 0
      //   105: pop
      //   106: aload 6
      //   108: aconst_null
      //   109: ldc 91
      //   111: aload 4
      //   113: getfield 94	android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   116: invokevirtual 100	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   119: invokeinterface 104 4 0
      //   124: pop
      //   125: aload 6
      //   127: aconst_null
      //   128: ldc 106
      //   130: aload 4
      //   132: getfield 109	android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   135: invokestatic 112	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   138: invokeinterface 104 4 0
      //   143: pop
      //   144: aload 6
      //   146: aconst_null
      //   147: ldc 114
      //   149: aload 4
      //   151: getfield 117	android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   154: invokestatic 120	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   157: invokeinterface 104 4 0
      //   162: pop
      //   163: aload 6
      //   165: aconst_null
      //   166: ldc 89
      //   168: invokeinterface 123 3 0
      //   173: pop
      //   174: iinc 5 1
      //   177: goto -101 -> 76
      //   180: astore_3
      //   181: invokestatic 126	android/support/v7/internal/widget/ActivityChooserModel:access$300	()Ljava/lang/String;
      //   184: new 128	java/lang/StringBuilder
      //   187: dup
      //   188: invokespecial 129	java/lang/StringBuilder:<init>	()V
      //   191: ldc -125
      //   193: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   196: aload_2
      //   197: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   200: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   203: aload_3
      //   204: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   207: pop
      //   208: aconst_null
      //   209: areturn
      //   210: aload 6
      //   212: aconst_null
      //   213: ldc 73
      //   215: invokeinterface 123 3 0
      //   220: pop
      //   221: aload 6
      //   223: invokeinterface 147 1 0
      //   228: aload_0
      //   229: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   232: iconst_1
      //   233: invokestatic 151	android/support/v7/internal/widget/ActivityChooserModel:access$502	(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
      //   236: pop
      //   237: aload_2
      //   238: ifnull +7 -> 245
      //   241: aload_2
      //   242: invokevirtual 156	java/io/FileOutputStream:close	()V
      //   245: goto -37 -> 208
      //   248: astore_3
      //   249: invokestatic 126	android/support/v7/internal/widget/ActivityChooserModel:access$300	()Ljava/lang/String;
      //   252: new 128	java/lang/StringBuilder
      //   255: dup
      //   256: invokespecial 129	java/lang/StringBuilder:<init>	()V
      //   259: ldc -125
      //   261: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   264: aload_0
      //   265: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   268: invokestatic 160	android/support/v7/internal/widget/ActivityChooserModel:access$400	(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
      //   271: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   274: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   277: aload_3
      //   278: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   281: pop
      //   282: aload_0
      //   283: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   286: iconst_1
      //   287: invokestatic 151	android/support/v7/internal/widget/ActivityChooserModel:access$502	(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
      //   290: pop
      //   291: aload_2
      //   292: ifnull -47 -> 245
      //   295: aload_2
      //   296: invokevirtual 156	java/io/FileOutputStream:close	()V
      //   299: goto -54 -> 245
      //   302: pop
      //   303: goto -58 -> 245
      //   306: astore_3
      //   307: invokestatic 126	android/support/v7/internal/widget/ActivityChooserModel:access$300	()Ljava/lang/String;
      //   310: new 128	java/lang/StringBuilder
      //   313: dup
      //   314: invokespecial 129	java/lang/StringBuilder:<init>	()V
      //   317: ldc -125
      //   319: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   322: aload_0
      //   323: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   326: invokestatic 160	android/support/v7/internal/widget/ActivityChooserModel:access$400	(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
      //   329: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   332: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   335: aload_3
      //   336: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   339: pop
      //   340: aload_0
      //   341: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   344: iconst_1
      //   345: invokestatic 151	android/support/v7/internal/widget/ActivityChooserModel:access$502	(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
      //   348: pop
      //   349: aload_2
      //   350: ifnull -105 -> 245
      //   353: aload_2
      //   354: invokevirtual 156	java/io/FileOutputStream:close	()V
      //   357: goto -112 -> 245
      //   360: pop
      //   361: goto -116 -> 245
      //   364: astore_3
      //   365: invokestatic 126	android/support/v7/internal/widget/ActivityChooserModel:access$300	()Ljava/lang/String;
      //   368: new 128	java/lang/StringBuilder
      //   371: dup
      //   372: invokespecial 129	java/lang/StringBuilder:<init>	()V
      //   375: ldc -125
      //   377: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   380: aload_0
      //   381: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   384: invokestatic 160	android/support/v7/internal/widget/ActivityChooserModel:access$400	(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
      //   387: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   390: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   393: aload_3
      //   394: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   397: pop
      //   398: aload_0
      //   399: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   402: iconst_1
      //   403: invokestatic 151	android/support/v7/internal/widget/ActivityChooserModel:access$502	(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
      //   406: pop
      //   407: aload_2
      //   408: ifnull -163 -> 245
      //   411: aload_2
      //   412: invokevirtual 156	java/io/FileOutputStream:close	()V
      //   415: goto -170 -> 245
      //   418: pop
      //   419: goto -174 -> 245
      //   422: astore_3
      //   423: aload_0
      //   424: getfield 14	android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/internal/widget/ActivityChooserModel;
      //   427: iconst_1
      //   428: invokestatic 151	android/support/v7/internal/widget/ActivityChooserModel:access$502	(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
      //   431: pop
      //   432: aload_2
      //   433: ifnull +7 -> 440
      //   436: aload_2
      //   437: invokevirtual 156	java/io/FileOutputStream:close	()V
      //   440: aload_3
      //   441: athrow
      //   442: pop
      //   443: goto -198 -> 245
      //   446: pop
      //   447: goto -7 -> 440
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	450	0	this	PersistHistoryAsyncTask
      //   0	450	1	paramVarArgs	Object[]
      //   13	424	2	localObject1	Object
      //   6	78	3	localList	List
      //   180	24	3	localFileNotFoundException	FileNotFoundException
      //   248	30	3	localIllegalArgumentException	IllegalArgumentException
      //   306	30	3	localIllegalStateException	IllegalStateException
      //   364	30	3	localIOException1	IOException
      //   422	19	3	localObject2	Object
      //   93	57	4	localHistoricalRecord	ActivityChooserModel.HistoricalRecord
      //   74	101	5	i	int
      //   30	192	6	localXmlSerializer	org.xmlpull.v1.XmlSerializer
      //   71	10	7	j	int
      //   302	1	13	localIOException2	IOException
      //   360	1	14	localIOException3	IOException
      //   418	1	15	localIOException4	IOException
      //   442	1	16	localIOException5	IOException
      //   446	1	17	localIOException6	IOException
      // Exception table:
      //   from	to	target	type
      //   14	27	180	java/io/FileNotFoundException
      //   32	174	248	java/lang/IllegalArgumentException
      //   210	228	248	java/lang/IllegalArgumentException
      //   295	299	302	java/io/IOException
      //   32	174	306	java/lang/IllegalStateException
      //   210	228	306	java/lang/IllegalStateException
      //   353	357	360	java/io/IOException
      //   32	174	364	java/io/IOException
      //   210	228	364	java/io/IOException
      //   411	415	418	java/io/IOException
      //   32	174	422	finally
      //   210	228	422	finally
      //   249	282	422	finally
      //   307	340	422	finally
      //   365	398	422	finally
      //   241	245	442	java/io/IOException
      //   436	440	446	java/io/IOException
    }
  }
  
  private final class DefaultSorter
    implements ActivityChooserModel.ActivitySorter
  {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    private final Map<String, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();
    
    private DefaultSorter() {}
    
    public void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1)
    {
      Map localMap = this.mPackageNameToActivityMap;
      localMap.clear();
      int i = paramList.size();
      for (int j = 0;; j++)
      {
        if (j >= i)
        {
          j = -1 + paramList1.size();
          float f = 1.0F;
          for (j = j;; j--)
          {
            if (j < 0)
            {
              Collections.sort(paramList);
              return;
            }
            localObject = (ActivityChooserModel.HistoricalRecord)paramList1.get(j);
            ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo = (ActivityChooserModel.ActivityResolveInfo)localMap.get(((ActivityChooserModel.HistoricalRecord)localObject).activity.getPackageName());
            if (localActivityResolveInfo != null)
            {
              localActivityResolveInfo.weight += f * ((ActivityChooserModel.HistoricalRecord)localObject).weight;
              f *= 0.95F;
            }
          }
        }
        Object localObject = (ActivityChooserModel.ActivityResolveInfo)paramList.get(j);
        ((ActivityChooserModel.ActivityResolveInfo)localObject).weight = 0.0F;
        localMap.put(((ActivityChooserModel.ActivityResolveInfo)localObject).resolveInfo.activityInfo.packageName, localObject);
      }
    }
  }
  
  public final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      this.resolveInfo = paramResolveInfo;
    }
    
    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(paramActivityResolveInfo.weight) - Float.floatToIntBits(this.weight);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this != paramObject) {
        if (paramObject != null)
        {
          if (getClass() == paramObject.getClass())
          {
            ActivityResolveInfo localActivityResolveInfo = (ActivityResolveInfo)paramObject;
            if (Float.floatToIntBits(this.weight) != Float.floatToIntBits(localActivityResolveInfo.weight)) {
              bool = false;
            }
          }
          else
          {
            bool = false;
          }
        }
        else {
          bool = false;
        }
      }
      return bool;
    }
    
    public int hashCode()
    {
      return 31 + Float.floatToIntBits(this.weight);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:").append(this.resolveInfo.toString());
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;
    
    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      this.activity = paramComponentName;
      this.time = paramLong;
      this.weight = paramFloat;
    }
    
    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this != paramObject) {
        if (paramObject != null)
        {
          if (getClass() == paramObject.getClass())
          {
            HistoricalRecord localHistoricalRecord = (HistoricalRecord)paramObject;
            if (this.activity != null)
            {
              if (!this.activity.equals(localHistoricalRecord.activity)) {
                return false;
              }
            }
            else {
              if (localHistoricalRecord.activity != null) {
                break label99;
              }
            }
            if (this.time == localHistoricalRecord.time)
            {
              if (Float.floatToIntBits(this.weight) != Float.floatToIntBits(localHistoricalRecord.weight)) {
                bool = false;
              }
            }
            else
            {
              return false;
              label99:
              bool = false;
            }
          }
          else
          {
            bool = false;
          }
        }
        else {
          bool = false;
        }
      }
      return bool;
    }
    
    public int hashCode()
    {
      int i;
      if (this.activity != null) {
        i = this.activity.hashCode();
      } else {
        i = 0;
      }
      return 31 * (31 * (i + 31) + (int)(this.time ^ this.time >>> 32)) + Float.floatToIntBits(this.weight);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:").append(this.activity);
      localStringBuilder.append("; time:").append(this.time);
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }
  
  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }
  
  public static abstract interface ActivityChooserModelClient
  {
    public abstract void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ActivityChooserModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */