package android.support.v4.media;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class RatingCompat
  implements Parcelable
{
  public static final Parcelable.Creator<RatingCompat> CREATOR = new Parcelable.Creator()
  {
    public RatingCompat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RatingCompat(paramAnonymousParcel.readInt(), paramAnonymousParcel.readFloat(), null);
    }
    
    public RatingCompat[] newArray(int paramAnonymousInt)
    {
      return new RatingCompat[paramAnonymousInt];
    }
  };
  public static final int RATING_3_STARS = 3;
  public static final int RATING_4_STARS = 4;
  public static final int RATING_5_STARS = 5;
  public static final int RATING_HEART = 1;
  public static final int RATING_NONE = 0;
  private static final float RATING_NOT_RATED = -1.0F;
  public static final int RATING_PERCENTAGE = 6;
  public static final int RATING_THUMB_UP_DOWN = 2;
  private static final String TAG = "Rating";
  private Object mRatingObj;
  private final int mRatingStyle;
  private final float mRatingValue;
  
  private RatingCompat(int paramInt, float paramFloat)
  {
    this.mRatingStyle = paramInt;
    this.mRatingValue = paramFloat;
  }
  
  public static RatingCompat fromRating(Object paramObject)
  {
    RatingCompat localRatingCompat = null;
    if ((paramObject != null) && (Build.VERSION.SDK_INT >= 21))
    {
      int i = RatingCompatApi21.getRatingStyle(paramObject);
      if (!RatingCompatApi21.isRated(paramObject)) {
        localRatingCompat = newUnratedRating(i);
      } else {
        switch (i)
        {
        default: 
          break;
        case 1: 
          localRatingCompat = newHeartRating(RatingCompatApi21.hasHeart(paramObject));
          break;
        case 2: 
          localRatingCompat = newThumbRating(RatingCompatApi21.isThumbUp(paramObject));
          break;
        case 3: 
        case 4: 
        case 5: 
          localRatingCompat = newStarRating(i, RatingCompatApi21.getStarRating(paramObject));
          break;
        case 6: 
          localRatingCompat = newPercentageRating(RatingCompatApi21.getPercentRating(paramObject));
        }
      }
      localRatingCompat.mRatingObj = paramObject;
    }
    return localRatingCompat;
  }
  
  public static RatingCompat newHeartRating(boolean paramBoolean)
  {
    float f;
    if (!paramBoolean) {
      f = 0.0F;
    } else {
      f = 1.0F;
    }
    return new RatingCompat(1, f);
  }
  
  public static RatingCompat newPercentageRating(float paramFloat)
  {
    RatingCompat localRatingCompat;
    if ((paramFloat >= 0.0F) && (paramFloat <= 100.0F))
    {
      localRatingCompat = new RatingCompat(6, paramFloat);
    }
    else
    {
      Log.e("Rating", "Invalid percentage-based rating value");
      localRatingCompat = null;
    }
    return localRatingCompat;
  }
  
  public static RatingCompat newStarRating(int paramInt, float paramFloat)
  {
    RatingCompat localRatingCompat = null;
    float f;
    switch (paramInt)
    {
    default: 
      Log.e("Rating", "Invalid rating style (" + paramInt + ") for a star rating");
      break;
    case 3: 
      f = 3.0F;
      break;
    case 4: 
      f = 4.0F;
      break;
    case 5: 
      f = 5.0F;
    }
    if ((paramFloat >= 0.0F) && (paramFloat <= f)) {
      localRatingCompat = new RatingCompat(paramInt, paramFloat);
    } else {
      Log.e("Rating", "Trying to set out of range star-based rating");
    }
    return localRatingCompat;
  }
  
  public static RatingCompat newThumbRating(boolean paramBoolean)
  {
    float f;
    if (!paramBoolean) {
      f = 0.0F;
    } else {
      f = 1.0F;
    }
    return new RatingCompat(2, f);
  }
  
  public static RatingCompat newUnratedRating(int paramInt)
  {
    RatingCompat localRatingCompat;
    switch (paramInt)
    {
    default: 
      localRatingCompat = null;
      break;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
      localRatingCompat = new RatingCompat(paramInt, -1.0F);
    }
    return localRatingCompat;
  }
  
  public int describeContents()
  {
    return this.mRatingStyle;
  }
  
  public float getPercentRating()
  {
    float f;
    if ((this.mRatingStyle == 6) && (isRated())) {
      f = this.mRatingValue;
    } else {
      f = -1.0F;
    }
    return f;
  }
  
  public Object getRating()
  {
    Object localObject;
    if ((this.mRatingObj == null) && (Build.VERSION.SDK_INT >= 21))
    {
      if (!isRated()) {
        this.mRatingObj = RatingCompatApi21.newUnratedRating(this.mRatingStyle);
      }
      switch (this.mRatingStyle)
      {
      case 1: 
        this.mRatingObj = RatingCompatApi21.newHeartRating(hasHeart());
        break;
      case 2: 
        this.mRatingObj = RatingCompatApi21.newThumbRating(isThumbUp());
        break;
      case 3: 
      case 4: 
      case 5: 
        this.mRatingObj = RatingCompatApi21.newStarRating(this.mRatingStyle, getStarRating());
        localObject = this.mRatingObj;
        break;
      case 6: 
        this.mRatingObj = RatingCompatApi21.newPercentageRating(getPercentRating());
      }
      localObject = null;
    }
    else
    {
      localObject = this.mRatingObj;
    }
    return localObject;
  }
  
  public int getRatingStyle()
  {
    return this.mRatingStyle;
  }
  
  public float getStarRating()
  {
    switch (this.mRatingStyle)
    {
    case 3: 
    case 4: 
    case 5: 
      if (isRated()) {
        break;
      }
    default: 
      f = -1.0F;
      break;
    }
    float f = this.mRatingValue;
    return f;
  }
  
  public boolean hasHeart()
  {
    int i = 1;
    boolean bool = false;
    if (this.mRatingStyle == i)
    {
      if (this.mRatingValue != 1.0F) {
        i = 0;
      }
      bool = i;
    }
    return bool;
  }
  
  public boolean isRated()
  {
    boolean bool;
    if (this.mRatingValue < 0.0F) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isThumbUp()
  {
    boolean bool = false;
    if ((this.mRatingStyle == 2) && (this.mRatingValue == 1.0F)) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Rating:style=").append(this.mRatingStyle).append(" rating=");
    String str;
    if (this.mRatingValue >= 0.0F) {
      str = String.valueOf(this.mRatingValue);
    } else {
      str = "unrated";
    }
    return str;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRatingStyle);
    paramParcel.writeFloat(this.mRatingValue);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\media\RatingCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */