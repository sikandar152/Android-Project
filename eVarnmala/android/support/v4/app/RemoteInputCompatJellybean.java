package android.support.v4.app;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;

class RemoteInputCompatJellybean
{
  public static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData";
  private static final String KEY_ALLOW_FREE_FORM_INPUT = "allowFreeFormInput";
  private static final String KEY_CHOICES = "choices";
  private static final String KEY_EXTRAS = "extras";
  private static final String KEY_LABEL = "label";
  private static final String KEY_RESULT_KEY = "resultKey";
  public static final String RESULTS_CLIP_LABEL = "android.remoteinput.results";
  
  static void addResultsToIntent(RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    int i = paramArrayOfRemoteInput.length;
    for (int j = 0;; j++)
    {
      if (j >= i)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("android.remoteinput.resultsData", localBundle);
        paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", localIntent));
        return;
      }
      RemoteInputCompatBase.RemoteInput localRemoteInput = paramArrayOfRemoteInput[j];
      Object localObject = paramBundle.get(localRemoteInput.getResultKey());
      if ((localObject instanceof CharSequence)) {
        localBundle.putCharSequence(localRemoteInput.getResultKey(), (CharSequence)localObject);
      }
    }
  }
  
  static RemoteInputCompatBase.RemoteInput fromBundle(Bundle paramBundle, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    return paramFactory.build(paramBundle.getString("resultKey"), paramBundle.getCharSequence("label"), paramBundle.getCharSequenceArray("choices"), paramBundle.getBoolean("allowFreeFormInput"), paramBundle.getBundle("extras"));
  }
  
  static RemoteInputCompatBase.RemoteInput[] fromBundleArray(Bundle[] paramArrayOfBundle, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    if (paramArrayOfBundle != null)
    {
      arrayOfRemoteInput = paramFactory.newArray(paramArrayOfBundle.length);
      for (int i = 0; i < paramArrayOfBundle.length; i++) {
        arrayOfRemoteInput[i] = fromBundle(paramArrayOfBundle[i], paramFactory);
      }
    }
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = null;
    return arrayOfRemoteInput;
  }
  
  static Bundle getResultsFromIntent(Intent paramIntent)
  {
    Bundle localBundle = null;
    ClipData localClipData = paramIntent.getClipData();
    if (localClipData != null)
    {
      ClipDescription localClipDescription = localClipData.getDescription();
      if ((localClipDescription.hasMimeType("text/vnd.android.intent")) && (localClipDescription.getLabel().equals("android.remoteinput.results"))) {
        localBundle = (Bundle)localClipData.getItemAt(0).getIntent().getExtras().getParcelable("android.remoteinput.resultsData");
      }
    }
    return localBundle;
  }
  
  static Bundle toBundle(RemoteInputCompatBase.RemoteInput paramRemoteInput)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("resultKey", paramRemoteInput.getResultKey());
    localBundle.putCharSequence("label", paramRemoteInput.getLabel());
    localBundle.putCharSequenceArray("choices", paramRemoteInput.getChoices());
    localBundle.putBoolean("allowFreeFormInput", paramRemoteInput.getAllowFreeFormInput());
    localBundle.putBundle("extras", paramRemoteInput.getExtras());
    return localBundle;
  }
  
  static Bundle[] toBundleArray(RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput)
  {
    if (paramArrayOfRemoteInput != null)
    {
      arrayOfBundle = new Bundle[paramArrayOfRemoteInput.length];
      for (int i = 0; i < paramArrayOfRemoteInput.length; i++) {
        arrayOfBundle[i] = toBundle(paramArrayOfRemoteInput[i]);
      }
    }
    Bundle[] arrayOfBundle = null;
    return arrayOfBundle;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\RemoteInputCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */