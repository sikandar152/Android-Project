package android.support.v4.media;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import java.util.Iterator;
import java.util.Set;

public final class MediaMetadataCompat
  implements Parcelable
{
  public static final Parcelable.Creator<MediaMetadataCompat> CREATOR = new Parcelable.Creator()
  {
    public MediaMetadataCompat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaMetadataCompat(paramAnonymousParcel, null);
    }
    
    public MediaMetadataCompat[] newArray(int paramAnonymousInt)
    {
      return new MediaMetadataCompat[paramAnonymousInt];
    }
  };
  private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE = new ArrayMap();
  public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
  public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
  public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
  public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";
  public static final String METADATA_KEY_ART = "android.media.metadata.ART";
  public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
  public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
  public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
  public static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
  public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
  public static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
  public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
  public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
  public static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON";
  public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
  public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
  public static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE";
  public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
  public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
  public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
  public static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
  public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
  public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
  public static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
  public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
  public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
  private static final int METADATA_TYPE_BITMAP = 2;
  private static final int METADATA_TYPE_LONG = 0;
  private static final int METADATA_TYPE_RATING = 3;
  private static final int METADATA_TYPE_TEXT = 1;
  private static final String TAG = "MediaMetadata";
  private final Bundle mBundle;
  private Object mMetadataObj;
  
  static
  {
    METADATA_KEYS_TYPE.put("android.media.metadata.TITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ARTIST", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DURATION", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.AUTHOR", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.WRITER", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.COMPOSER", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.COMPILATION", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DATE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.YEAR", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.GENRE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.TRACK_NUMBER", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.NUM_TRACKS", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISC_NUMBER", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ARTIST", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ART", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.ART_URI", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART_URI", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.USER_RATING", Integer.valueOf(3));
    METADATA_KEYS_TYPE.put("android.media.metadata.RATING", Integer.valueOf(3));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_TITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_SUBTITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_DESCRIPTION", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON_URI", Integer.valueOf(1));
  }
  
  private MediaMetadataCompat(Bundle paramBundle)
  {
    this.mBundle = new Bundle(paramBundle);
  }
  
  private MediaMetadataCompat(Parcel paramParcel)
  {
    this.mBundle = paramParcel.readBundle();
  }
  
  public static MediaMetadataCompat fromMediaMetadata(Object paramObject)
  {
    if ((paramObject != null) && (Build.VERSION.SDK_INT >= 21))
    {
      Builder localBuilder = new Builder();
      localObject = MediaMetadataCompatApi21.keySet(paramObject).iterator();
      for (;;)
      {
        if (!((Iterator)localObject).hasNext())
        {
          localObject = localBuilder.build();
          ((MediaMetadataCompat)localObject).mMetadataObj = paramObject;
          break;
        }
        String str = (String)((Iterator)localObject).next();
        Integer localInteger = (Integer)METADATA_KEYS_TYPE.get(str);
        if (localInteger != null) {
          switch (localInteger.intValue())
          {
          default: 
            break;
          case 0: 
            localBuilder.putLong(str, MediaMetadataCompatApi21.getLong(paramObject, str));
            break;
          case 1: 
            localBuilder.putText(str, MediaMetadataCompatApi21.getText(paramObject, str));
            break;
          case 2: 
            localBuilder.putBitmap(str, MediaMetadataCompatApi21.getBitmap(paramObject, str));
            break;
          case 3: 
            localBuilder.putRating(str, RatingCompat.fromRating(MediaMetadataCompatApi21.getRating(paramObject, str)));
          }
        }
      }
    }
    Object localObject = null;
    return (MediaMetadataCompat)localObject;
  }
  
  public boolean containsKey(String paramString)
  {
    return this.mBundle.containsKey(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bitmap getBitmap(String paramString)
  {
    Bitmap localBitmap = null;
    try
    {
      localBitmap = (Bitmap)this.mBundle.getParcelable(paramString);
      return localBitmap;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.w("MediaMetadata", "Failed to retrieve a key as Bitmap.", localException);
      }
    }
  }
  
  public long getLong(String paramString)
  {
    return this.mBundle.getLong(paramString, 0L);
  }
  
  public Object getMediaMetadata()
  {
    if ((this.mMetadataObj == null) && (Build.VERSION.SDK_INT >= 21))
    {
      Object localObject2 = MediaMetadataCompatApi21.Builder.newInstance();
      Iterator localIterator = keySet().iterator();
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          this.mMetadataObj = MediaMetadataCompatApi21.Builder.build(localObject2);
          localObject1 = this.mMetadataObj;
          break;
        }
        String str = (String)localIterator.next();
        localObject1 = (Integer)METADATA_KEYS_TYPE.get(str);
        if (localObject1 != null) {
          switch (((Integer)localObject1).intValue())
          {
          default: 
            break;
          case 0: 
            MediaMetadataCompatApi21.Builder.putLong(localObject2, str, getLong(str));
            break;
          case 1: 
            MediaMetadataCompatApi21.Builder.putText(localObject2, str, getText(str));
            break;
          case 2: 
            MediaMetadataCompatApi21.Builder.putBitmap(localObject2, str, getBitmap(str));
            break;
          case 3: 
            MediaMetadataCompatApi21.Builder.putRating(localObject2, str, getRating(str).getRating());
          }
        }
      }
    }
    Object localObject1 = this.mMetadataObj;
    return localObject1;
  }
  
  public RatingCompat getRating(String paramString)
  {
    RatingCompat localRatingCompat = null;
    try
    {
      localRatingCompat = (RatingCompat)this.mBundle.getParcelable(paramString);
      return localRatingCompat;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.w("MediaMetadata", "Failed to retrieve a key as Rating.", localException);
      }
    }
  }
  
  public String getString(String paramString)
  {
    Object localObject = this.mBundle.getCharSequence(paramString);
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((CharSequence)localObject).toString();
    }
    return (String)localObject;
  }
  
  public CharSequence getText(String paramString)
  {
    return this.mBundle.getCharSequence(paramString);
  }
  
  public Set<String> keySet()
  {
    return this.mBundle.keySet();
  }
  
  public int size()
  {
    return this.mBundle.size();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeBundle(this.mBundle);
  }
  
  public static final class Builder
  {
    private final Bundle mBundle;
    
    public Builder()
    {
      this.mBundle = new Bundle();
    }
    
    public Builder(MediaMetadataCompat paramMediaMetadataCompat)
    {
      this.mBundle = new Bundle(paramMediaMetadataCompat.mBundle);
    }
    
    public MediaMetadataCompat build()
    {
      return new MediaMetadataCompat(this.mBundle, null);
    }
    
    public Builder putBitmap(String paramString, Bitmap paramBitmap)
    {
      if ((!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(paramString)) || (((Integer)MediaMetadataCompat.METADATA_KEYS_TYPE.get(paramString)).intValue() == 2))
      {
        this.mBundle.putParcelable(paramString, paramBitmap);
        return this;
      }
      throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a Bitmap");
    }
    
    public Builder putLong(String paramString, long paramLong)
    {
      if ((!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(paramString)) || (((Integer)MediaMetadataCompat.METADATA_KEYS_TYPE.get(paramString)).intValue() == 0))
      {
        this.mBundle.putLong(paramString, paramLong);
        return this;
      }
      throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a long");
    }
    
    public Builder putRating(String paramString, RatingCompat paramRatingCompat)
    {
      if ((!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(paramString)) || (((Integer)MediaMetadataCompat.METADATA_KEYS_TYPE.get(paramString)).intValue() == 3))
      {
        this.mBundle.putParcelable(paramString, paramRatingCompat);
        return this;
      }
      throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a Rating");
    }
    
    public Builder putString(String paramString1, String paramString2)
    {
      if ((!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(paramString1)) || (((Integer)MediaMetadataCompat.METADATA_KEYS_TYPE.get(paramString1)).intValue() == 1))
      {
        this.mBundle.putCharSequence(paramString1, paramString2);
        return this;
      }
      throw new IllegalArgumentException("The " + paramString1 + " key cannot be used to put a String");
    }
    
    public Builder putText(String paramString, CharSequence paramCharSequence)
    {
      if ((!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(paramString)) || (((Integer)MediaMetadataCompat.METADATA_KEYS_TYPE.get(paramString)).intValue() == 1))
      {
        this.mBundle.putCharSequence(paramString, paramCharSequence);
        return this;
      }
      throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a CharSequence");
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\media\MediaMetadataCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */