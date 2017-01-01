package android.support.v4.speech.tts;

import android.os.Build.VERSION;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;
import java.util.Set;

class TextToSpeechICSMR1
{
  public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";
  public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";
  
  static Set<String> getFeatures(TextToSpeech paramTextToSpeech, Locale paramLocale)
  {
    Set localSet;
    if (Build.VERSION.SDK_INT < 15) {
      localSet = null;
    } else {
      localSet = paramTextToSpeech.getFeatures(paramLocale);
    }
    return localSet;
  }
  
  static void setUtteranceProgressListener(TextToSpeech paramTextToSpeech, UtteranceProgressListenerICSMR1 paramUtteranceProgressListenerICSMR1)
  {
    if (Build.VERSION.SDK_INT < 15) {
      paramTextToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener()
      {
        public void onUtteranceCompleted(String paramAnonymousString)
        {
          TextToSpeechICSMR1.this.onStart(paramAnonymousString);
          TextToSpeechICSMR1.this.onDone(paramAnonymousString);
        }
      });
    } else {
      paramTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
      {
        public void onDone(String paramAnonymousString)
        {
          TextToSpeechICSMR1.this.onDone(paramAnonymousString);
        }
        
        public void onError(String paramAnonymousString)
        {
          TextToSpeechICSMR1.this.onError(paramAnonymousString);
        }
        
        public void onStart(String paramAnonymousString)
        {
          TextToSpeechICSMR1.this.onStart(paramAnonymousString);
        }
      });
    }
  }
  
  static abstract interface UtteranceProgressListenerICSMR1
  {
    public abstract void onDone(String paramString);
    
    public abstract void onError(String paramString);
    
    public abstract void onStart(String paramString);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\speech\tts\TextToSpeechICSMR1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */