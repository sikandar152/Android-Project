package android.support.v4.speech.tts;

import android.content.Context;
import android.os.Build.VERSION;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

class TextToSpeechICS
{
  private static final String TAG = "android.support.v4.speech.tts";
  
  static TextToSpeech construct(Context paramContext, TextToSpeech.OnInitListener paramOnInitListener, String paramString)
  {
    TextToSpeech localTextToSpeech;
    if (Build.VERSION.SDK_INT >= 14)
    {
      localTextToSpeech = new TextToSpeech(paramContext, paramOnInitListener, paramString);
    }
    else if (paramString != null)
    {
      Log.w("android.support.v4.speech.tts", "Can't specify tts engine on this device");
      localTextToSpeech = new TextToSpeech(paramContext, paramOnInitListener);
    }
    else
    {
      localTextToSpeech = new TextToSpeech(paramContext, paramOnInitListener);
    }
    return localTextToSpeech;
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\speech\tts\TextToSpeechICS.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */