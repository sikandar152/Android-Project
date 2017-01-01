package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.drawable;
import android.util.TypedValue;

public class TintManager
{
  private static final ColorFilterLruCache COLOR_FILTER_CACHE;
  private static final int[] CONTAINERS_WITH_TINT_CHILDREN;
  private static final boolean DEBUG;
  static final PorterDuff.Mode DEFAULT_MODE;
  private static final String TAG = TintManager.class.getSimpleName();
  private static final int[] TINT_COLOR_BACKGROUND_MULTIPLY;
  private static final int[] TINT_COLOR_CONTROL_ACTIVATED;
  private static final int[] TINT_COLOR_CONTROL_NORMAL;
  private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
  private final Context mContext;
  private ColorStateList mDefaultColorStateList;
  private final Resources mResources;
  private ColorStateList mSwitchThumbStateList;
  private ColorStateList mSwitchTrackStateList;
  private final TypedValue mTypedValue;
  
  static
  {
    DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    int[] arrayOfInt = new int[14];
    arrayOfInt[0] = R.drawable.abc_ic_ab_back_mtrl_am_alpha;
    arrayOfInt[1] = R.drawable.abc_ic_go_search_api_mtrl_alpha;
    arrayOfInt[2] = R.drawable.abc_ic_search_api_mtrl_alpha;
    arrayOfInt[3] = R.drawable.abc_ic_commit_search_api_mtrl_alpha;
    arrayOfInt[4] = R.drawable.abc_ic_clear_mtrl_alpha;
    arrayOfInt[5] = R.drawable.abc_ic_menu_share_mtrl_alpha;
    arrayOfInt[6] = R.drawable.abc_ic_menu_copy_mtrl_am_alpha;
    arrayOfInt[7] = R.drawable.abc_ic_menu_cut_mtrl_alpha;
    arrayOfInt[8] = R.drawable.abc_ic_menu_selectall_mtrl_alpha;
    arrayOfInt[9] = R.drawable.abc_ic_menu_paste_mtrl_am_alpha;
    arrayOfInt[10] = R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha;
    arrayOfInt[11] = R.drawable.abc_ic_voice_search_api_mtrl_alpha;
    arrayOfInt[12] = R.drawable.abc_textfield_search_default_mtrl_alpha;
    arrayOfInt[13] = R.drawable.abc_textfield_default_mtrl_alpha;
    TINT_COLOR_CONTROL_NORMAL = arrayOfInt;
    arrayOfInt = new int[3];
    arrayOfInt[0] = R.drawable.abc_textfield_activated_mtrl_alpha;
    arrayOfInt[1] = R.drawable.abc_textfield_search_activated_mtrl_alpha;
    arrayOfInt[2] = R.drawable.abc_cab_background_top_mtrl_alpha;
    TINT_COLOR_CONTROL_ACTIVATED = arrayOfInt;
    arrayOfInt = new int[3];
    arrayOfInt[0] = R.drawable.abc_popup_background_mtrl_mult;
    arrayOfInt[1] = R.drawable.abc_cab_background_internal_bg;
    arrayOfInt[2] = R.drawable.abc_menu_hardkey_panel_mtrl_mult;
    TINT_COLOR_BACKGROUND_MULTIPLY = arrayOfInt;
    arrayOfInt = new int[6];
    arrayOfInt[0] = R.drawable.abc_edit_text_material;
    arrayOfInt[1] = R.drawable.abc_tab_indicator_material;
    arrayOfInt[2] = R.drawable.abc_textfield_search_material;
    arrayOfInt[3] = R.drawable.abc_spinner_mtrl_am_alpha;
    arrayOfInt[4] = R.drawable.abc_btn_check_material;
    arrayOfInt[5] = R.drawable.abc_btn_radio_material;
    TINT_COLOR_CONTROL_STATE_LIST = arrayOfInt;
    arrayOfInt = new int[1];
    arrayOfInt[0] = R.drawable.abc_cab_background_top_material;
    CONTAINERS_WITH_TINT_CHILDREN = arrayOfInt;
  }
  
  public TintManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mResources = new TintResources(paramContext.getResources(), this);
    this.mTypedValue = new TypedValue();
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return 0;
      }
      if (paramArrayOfInt[j] == paramInt) {
        break;
      }
    }
    i = 1;
    return i;
  }
  
  private ColorStateList getDefaultColorStateList()
  {
    if (this.mDefaultColorStateList == null)
    {
      int i = getThemeAttrColor(R.attr.colorControlNormal);
      int j = getThemeAttrColor(R.attr.colorControlActivated);
      int[][] arrayOfInt = new int[7][];
      int[] arrayOfInt1 = new int[7];
      int[] arrayOfInt2 = new int[1];
      arrayOfInt2[0] = -16842910;
      arrayOfInt[0] = arrayOfInt2;
      arrayOfInt1[0] = getDisabledThemeAttrColor(R.attr.colorControlNormal);
      int k = 0 + 1;
      int[] arrayOfInt4 = new int[1];
      arrayOfInt4[0] = 16842908;
      arrayOfInt[k] = arrayOfInt4;
      arrayOfInt1[k] = j;
      k += 1;
      arrayOfInt4 = new int[1];
      arrayOfInt4[0] = 16843518;
      arrayOfInt[k] = arrayOfInt4;
      arrayOfInt1[k] = j;
      k += 1;
      arrayOfInt4 = new int[1];
      arrayOfInt4[0] = 16842919;
      arrayOfInt[k] = arrayOfInt4;
      arrayOfInt1[k] = j;
      int n = k + 1;
      int[] arrayOfInt3 = new int[1];
      arrayOfInt3[0] = 16842912;
      arrayOfInt[n] = arrayOfInt3;
      arrayOfInt1[n] = j;
      int m = n + 1;
      int[] arrayOfInt5 = new int[1];
      arrayOfInt5[0] = 16842913;
      arrayOfInt[m] = arrayOfInt5;
      arrayOfInt1[m] = j;
      j = m + 1;
      arrayOfInt[j] = new int[0];
      arrayOfInt1[j] = i;
      (j + 1);
      this.mDefaultColorStateList = new ColorStateList(arrayOfInt, arrayOfInt1);
    }
    return this.mDefaultColorStateList;
  }
  
  public static Drawable getDrawable(Context paramContext, int paramInt)
  {
    Drawable localDrawable;
    if (!isInTintList(paramInt)) {
      localDrawable = ContextCompat.getDrawable(paramContext, paramInt);
    } else {
      localDrawable = new TintManager(paramContext).getDrawable(paramInt);
    }
    return localDrawable;
  }
  
  private ColorStateList getSwitchThumbColorStateList()
  {
    if (this.mSwitchThumbStateList == null)
    {
      int[][] arrayOfInt = new int[3][];
      int[] arrayOfInt1 = new int[3];
      int[] arrayOfInt2 = new int[1];
      arrayOfInt2[0] = -16842910;
      arrayOfInt[0] = arrayOfInt2;
      arrayOfInt1[0] = getDisabledThemeAttrColor(R.attr.colorSwitchThumbNormal);
      int j = 0 + 1;
      arrayOfInt2 = new int[1];
      arrayOfInt2[0] = 16842912;
      arrayOfInt[j] = arrayOfInt2;
      arrayOfInt1[j] = getThemeAttrColor(R.attr.colorControlActivated);
      int i = j + 1;
      arrayOfInt[i] = new int[0];
      arrayOfInt1[i] = getThemeAttrColor(R.attr.colorSwitchThumbNormal);
      (i + 1);
      this.mSwitchThumbStateList = new ColorStateList(arrayOfInt, arrayOfInt1);
    }
    return this.mSwitchThumbStateList;
  }
  
  private ColorStateList getSwitchTrackColorStateList()
  {
    if (this.mSwitchTrackStateList == null)
    {
      int[][] arrayOfInt = new int[3][];
      int[] arrayOfInt1 = new int[3];
      int[] arrayOfInt2 = new int[1];
      arrayOfInt2[0] = -16842910;
      arrayOfInt[0] = arrayOfInt2;
      arrayOfInt1[0] = getThemeAttrColor(16842800, 0.1F);
      int i = 0 + 1;
      int[] arrayOfInt3 = new int[1];
      arrayOfInt3[0] = 16842912;
      arrayOfInt[i] = arrayOfInt3;
      arrayOfInt1[i] = getThemeAttrColor(R.attr.colorControlActivated, 0.3F);
      i += 1;
      arrayOfInt[i] = new int[0];
      arrayOfInt1[i] = getThemeAttrColor(16842800, 0.3F);
      (i + 1);
      this.mSwitchTrackStateList = new ColorStateList(arrayOfInt, arrayOfInt1);
    }
    return this.mSwitchTrackStateList;
  }
  
  private static boolean isInTintList(int paramInt)
  {
    boolean bool;
    if ((!arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, paramInt)) && (!arrayContains(TINT_COLOR_CONTROL_NORMAL, paramInt)) && (!arrayContains(TINT_COLOR_CONTROL_ACTIVATED, paramInt)) && (!arrayContains(TINT_COLOR_CONTROL_STATE_LIST, paramInt)) && (!arrayContains(CONTAINERS_WITH_TINT_CHILDREN, paramInt))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  int getDisabledThemeAttrColor(int paramInt)
  {
    this.mContext.getTheme().resolveAttribute(16842803, this.mTypedValue, true);
    return getThemeAttrColor(paramInt, this.mTypedValue.getFloat());
  }
  
  public Drawable getDrawable(int paramInt)
  {
    Object localObject = ContextCompat.getDrawable(this.mContext, paramInt);
    if (localObject != null) {
      if (!arrayContains(TINT_COLOR_CONTROL_STATE_LIST, paramInt))
      {
        if (paramInt != R.drawable.abc_switch_track_mtrl_alpha)
        {
          if (paramInt != R.drawable.abc_switch_thumb_material)
          {
            if (!arrayContains(CONTAINERS_WITH_TINT_CHILDREN, paramInt)) {
              tintDrawable(paramInt, (Drawable)localObject);
            } else {
              localObject = this.mResources.getDrawable(paramInt);
            }
          }
          else {
            localObject = new TintDrawableWrapper((Drawable)localObject, getSwitchThumbColorStateList(), PorterDuff.Mode.MULTIPLY);
          }
        }
        else {
          localObject = new TintDrawableWrapper((Drawable)localObject, getSwitchTrackColorStateList());
        }
      }
      else {
        localObject = new TintDrawableWrapper((Drawable)localObject, getDefaultColorStateList());
      }
    }
    return (Drawable)localObject;
  }
  
  int getThemeAttrColor(int paramInt)
  {
    if (this.mContext.getTheme().resolveAttribute(paramInt, this.mTypedValue, true))
    {
      if ((this.mTypedValue.type >= 16) && (this.mTypedValue.type <= 31)) {
        break label77;
      }
      if (this.mTypedValue.type == 3) {}
    }
    else
    {
      return 0;
    }
    int i = this.mResources.getColor(this.mTypedValue.resourceId);
    return i;
    label77:
    i = this.mTypedValue.data;
    return i;
  }
  
  int getThemeAttrColor(int paramInt, float paramFloat)
  {
    int i = getThemeAttrColor(paramInt);
    int j = Color.alpha(i);
    return 0xFFFFFF & i | Math.round(paramFloat * j) << 24;
  }
  
  void tintDrawable(int paramInt, Drawable paramDrawable)
  {
    PorterDuff.Mode localMode = null;
    int j = 0;
    int k = 0;
    int i = -1;
    if (!arrayContains(TINT_COLOR_CONTROL_NORMAL, paramInt))
    {
      if (!arrayContains(TINT_COLOR_CONTROL_ACTIVATED, paramInt))
      {
        if (!arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, paramInt))
        {
          if (paramInt == R.drawable.abc_list_divider_mtrl_alpha)
          {
            k = 16842800;
            j = 1;
            i = Math.round(40.8F);
          }
        }
        else
        {
          k = 16842801;
          j = 1;
          localMode = PorterDuff.Mode.MULTIPLY;
        }
      }
      else
      {
        k = R.attr.colorControlActivated;
        j = 1;
      }
    }
    else
    {
      k = R.attr.colorControlNormal;
      j = 1;
    }
    if (j != 0)
    {
      if (localMode == null) {
        localMode = DEFAULT_MODE;
      }
      j = getThemeAttrColor(k);
      PorterDuffColorFilter localPorterDuffColorFilter = COLOR_FILTER_CACHE.get(j, localMode);
      if (localPorterDuffColorFilter == null)
      {
        localPorterDuffColorFilter = new PorterDuffColorFilter(j, localMode);
        COLOR_FILTER_CACHE.put(j, localMode, localPorterDuffColorFilter);
      }
      paramDrawable.setColorFilter(localPorterDuffColorFilter);
      if (i != -1) {
        paramDrawable.setAlpha(i);
      }
    }
  }
  
  private static class ColorFilterLruCache
    extends LruCache<Integer, PorterDuffColorFilter>
  {
    public ColorFilterLruCache(int paramInt)
    {
      super();
    }
    
    private static int generateCacheKey(int paramInt, PorterDuff.Mode paramMode)
    {
      return 31 * (paramInt + 31) + paramMode.hashCode();
    }
    
    PorterDuffColorFilter get(int paramInt, PorterDuff.Mode paramMode)
    {
      return (PorterDuffColorFilter)get(Integer.valueOf(generateCacheKey(paramInt, paramMode)));
    }
    
    PorterDuffColorFilter put(int paramInt, PorterDuff.Mode paramMode, PorterDuffColorFilter paramPorterDuffColorFilter)
    {
      return (PorterDuffColorFilter)put(Integer.valueOf(generateCacheKey(paramInt, paramMode)), paramPorterDuffColorFilter);
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\TintManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */