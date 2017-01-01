package android.support.v7.widget;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.WeakHashMap;

class SuggestionsAdapter
  extends ResourceCursorAdapter
  implements View.OnClickListener
{
  private static final boolean DBG = false;
  static final int INVALID_INDEX = -1;
  private static final String LOG_TAG = "SuggestionsAdapter";
  private static final int QUERY_LIMIT = 50;
  static final int REFINE_ALL = 2;
  static final int REFINE_BY_ENTRY = 1;
  static final int REFINE_NONE;
  private boolean mClosed = false;
  private final int mCommitIconResId;
  private int mFlagsCol = -1;
  private int mIconName1Col = -1;
  private int mIconName2Col = -1;
  private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
  private final Context mProviderContext;
  private int mQueryRefinement = 1;
  private final SearchManager mSearchManager = (SearchManager)this.mContext.getSystemService("search");
  private final SearchView mSearchView;
  private final SearchableInfo mSearchable;
  private int mText1Col = -1;
  private int mText2Col = -1;
  private int mText2UrlCol = -1;
  private ColorStateList mUrlColor;
  
  public SuggestionsAdapter(Context paramContext, SearchView paramSearchView, SearchableInfo paramSearchableInfo, WeakHashMap<String, Drawable.ConstantState> paramWeakHashMap)
  {
    super(paramContext, paramSearchView.getSuggestionRowLayout(), null, true);
    this.mSearchView = paramSearchView;
    this.mSearchable = paramSearchableInfo;
    this.mCommitIconResId = paramSearchView.getSuggestionCommitIconResId();
    this.mProviderContext = paramContext;
    this.mOutsideDrawablesCache = paramWeakHashMap;
  }
  
  private Drawable checkIconCache(String paramString)
  {
    Object localObject = (Drawable.ConstantState)this.mOutsideDrawablesCache.get(paramString);
    if (localObject != null) {
      localObject = ((Drawable.ConstantState)localObject).newDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  private CharSequence formatUrl(CharSequence paramCharSequence)
  {
    if (this.mUrlColor == null)
    {
      localObject = new TypedValue();
      this.mContext.getTheme().resolveAttribute(R.attr.textColorSearchUrl, (TypedValue)localObject, true);
      this.mUrlColor = this.mContext.getResources().getColorStateList(((TypedValue)localObject).resourceId);
    }
    Object localObject = new SpannableString(paramCharSequence);
    ((SpannableString)localObject).setSpan(new TextAppearanceSpan(null, 0, 0, this.mUrlColor, null), 0, paramCharSequence.length(), 33);
    return (CharSequence)localObject;
  }
  
  private Drawable getActivityIcon(ComponentName paramComponentName)
  {
    localObject = this.mContext.getPackageManager();
    try
    {
      localActivityInfo = ((PackageManager)localObject).getActivityInfo(paramComponentName, 128);
      int i = localActivityInfo.getIconResource();
      if (i != 0) {
        break label48;
      }
      localObject = null;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        ActivityInfo localActivityInfo;
        Log.w("SuggestionsAdapter", localNameNotFoundException.toString());
        localObject = null;
        continue;
        localObject = ((PackageManager)localObject).getDrawable(paramComponentName.getPackageName(), localNameNotFoundException, localActivityInfo.applicationInfo);
        if (localObject == null)
        {
          Log.w("SuggestionsAdapter", "Invalid icon resource " + localNameNotFoundException + " for " + paramComponentName.flattenToShortString());
          localObject = null;
        }
      }
    }
    return (Drawable)localObject;
  }
  
  private Drawable getActivityIconWithCache(ComponentName paramComponentName)
  {
    Object localObject2 = null;
    Object localObject1 = paramComponentName.flattenToShortString();
    if (!this.mOutsideDrawablesCache.containsKey(localObject1))
    {
      Drawable localDrawable = getActivityIcon(paramComponentName);
      if (localDrawable != null) {
        localObject2 = localDrawable.getConstantState();
      } else {
        localObject2 = null;
      }
      this.mOutsideDrawablesCache.put(localObject1, localObject2);
      localObject2 = localDrawable;
    }
    else
    {
      localObject1 = (Drawable.ConstantState)this.mOutsideDrawablesCache.get(localObject1);
      if (localObject1 != null) {
        localObject2 = ((Drawable.ConstantState)localObject1).newDrawable(this.mProviderContext.getResources());
      }
    }
    return (Drawable)localObject2;
  }
  
  public static String getColumnString(Cursor paramCursor, String paramString)
  {
    return getStringOrNull(paramCursor, paramCursor.getColumnIndex(paramString));
  }
  
  private Drawable getDefaultIcon1(Cursor paramCursor)
  {
    Drawable localDrawable = getActivityIconWithCache(this.mSearchable.getSearchActivity());
    if (localDrawable == null) {
      localDrawable = this.mContext.getPackageManager().getDefaultActivityIcon();
    }
    return localDrawable;
  }
  
  /* Error */
  private Drawable getDrawable(Uri paramUri)
  {
    // Byte code:
    //   0: ldc_w 287
    //   3: aload_1
    //   4: invokevirtual 292	android/net/Uri:getScheme	()Ljava/lang/String;
    //   7: invokevirtual 297	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   10: istore_2
    //   11: iload_2
    //   12: ifeq +87 -> 99
    //   15: aload_0
    //   16: aload_1
    //   17: invokevirtual 300	android/support/v7/widget/SuggestionsAdapter:getDrawableFromResourceUri	(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;
    //   20: astore_2
    //   21: aload_2
    //   22: astore_3
    //   23: aload_3
    //   24: areturn
    //   25: pop
    //   26: new 281	java/io/FileNotFoundException
    //   29: dup
    //   30: new 217	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   37: ldc_w 302
    //   40: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: aload_1
    //   44: invokevirtual 305	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   47: invokevirtual 233	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   50: invokespecial 308	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   53: athrow
    //   54: astore_2
    //   55: ldc 19
    //   57: new 217	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   64: ldc_w 310
    //   67: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   70: aload_1
    //   71: invokevirtual 305	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   74: ldc_w 312
    //   77: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: aload_2
    //   81: invokevirtual 315	java/io/FileNotFoundException:getMessage	()Ljava/lang/String;
    //   84: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: invokevirtual 233	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   90: invokestatic 202	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   93: pop
    //   94: aconst_null
    //   95: astore_3
    //   96: goto -73 -> 23
    //   99: aload_0
    //   100: getfield 101	android/support/v7/widget/SuggestionsAdapter:mProviderContext	Landroid/content/Context;
    //   103: invokevirtual 319	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   106: aload_1
    //   107: invokevirtual 325	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   110: astore_2
    //   111: aload_2
    //   112: ifnonnull +31 -> 143
    //   115: new 281	java/io/FileNotFoundException
    //   118: dup
    //   119: new 217	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   126: ldc_w 327
    //   129: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: aload_1
    //   133: invokevirtual 305	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   136: invokevirtual 233	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: invokespecial 308	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   142: athrow
    //   143: aload_2
    //   144: aconst_null
    //   145: invokestatic 331	android/graphics/drawable/Drawable:createFromStream	(Ljava/io/InputStream;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   148: astore_3
    //   149: aload_3
    //   150: astore_3
    //   151: aload_2
    //   152: invokevirtual 336	java/io/InputStream:close	()V
    //   155: goto -132 -> 23
    //   158: astore_2
    //   159: ldc 19
    //   161: new 217	java/lang/StringBuilder
    //   164: dup
    //   165: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   168: ldc_w 338
    //   171: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: aload_1
    //   175: invokevirtual 305	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   178: invokevirtual 233	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: aload_2
    //   182: invokestatic 342	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   185: pop
    //   186: goto -163 -> 23
    //   189: astore_3
    //   190: aload_2
    //   191: invokevirtual 336	java/io/InputStream:close	()V
    //   194: aload_3
    //   195: athrow
    //   196: astore_2
    //   197: ldc 19
    //   199: new 217	java/lang/StringBuilder
    //   202: dup
    //   203: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   206: ldc_w 338
    //   209: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: aload_1
    //   213: invokevirtual 305	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   216: invokevirtual 233	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   219: aload_2
    //   220: invokestatic 342	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   223: pop
    //   224: goto -30 -> 194
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	227	0	this	SuggestionsAdapter
    //   0	227	1	paramUri	Uri
    //   10	2	2	bool	boolean
    //   20	2	2	localDrawable1	Drawable
    //   54	27	2	localFileNotFoundException	FileNotFoundException
    //   110	42	2	localInputStream	java.io.InputStream
    //   158	33	2	localIOException1	java.io.IOException
    //   196	24	2	localIOException2	java.io.IOException
    //   22	129	3	localDrawable2	Drawable
    //   189	6	3	localObject	Object
    //   25	1	10	localNotFoundException	Resources.NotFoundException
    // Exception table:
    //   from	to	target	type
    //   15	21	25	android/content/res/Resources$NotFoundException
    //   0	11	54	java/io/FileNotFoundException
    //   15	21	54	java/io/FileNotFoundException
    //   26	54	54	java/io/FileNotFoundException
    //   99	143	54	java/io/FileNotFoundException
    //   151	155	54	java/io/FileNotFoundException
    //   159	186	54	java/io/FileNotFoundException
    //   190	194	54	java/io/FileNotFoundException
    //   194	224	54	java/io/FileNotFoundException
    //   151	155	158	java/io/IOException
    //   143	149	189	finally
    //   190	194	196	java/io/IOException
  }
  
  private Drawable getDrawableFromResourceValue(String paramString)
  {
    Drawable localDrawable;
    if ((paramString == null) || (paramString.length() == 0) || ("0".equals(paramString))) {
      localDrawable = null;
    }
    for (;;)
    {
      return localDrawable;
      try
      {
        int i = Integer.parseInt(paramString);
        String str = "android.resource://" + this.mProviderContext.getPackageName() + "/" + i;
        localDrawable = checkIconCache(str);
        if (localDrawable == null)
        {
          localDrawable = ContextCompat.getDrawable(this.mProviderContext, i);
          storeInIconCache(str, localDrawable);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localDrawable = checkIconCache(paramString);
        if (localDrawable == null)
        {
          localDrawable = getDrawable(Uri.parse(paramString));
          storeInIconCache(paramString, localDrawable);
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        Log.w("SuggestionsAdapter", "Icon resource not found: " + paramString);
        localDrawable = null;
      }
    }
  }
  
  private Drawable getIcon1(Cursor paramCursor)
  {
    Drawable localDrawable;
    if (this.mIconName1Col != -1)
    {
      localDrawable = getDrawableFromResourceValue(paramCursor.getString(this.mIconName1Col));
      if (localDrawable == null) {
        localDrawable = getDefaultIcon1(paramCursor);
      }
    }
    else
    {
      localDrawable = null;
    }
    return localDrawable;
  }
  
  private Drawable getIcon2(Cursor paramCursor)
  {
    Drawable localDrawable;
    if (this.mIconName2Col != -1) {
      localDrawable = getDrawableFromResourceValue(paramCursor.getString(this.mIconName2Col));
    } else {
      localDrawable = null;
    }
    return localDrawable;
  }
  
  private static String getStringOrNull(Cursor paramCursor, int paramInt)
  {
    String str = null;
    if (paramInt == -1) {}
    for (;;)
    {
      return str;
      try
      {
        str = paramCursor.getString(paramInt);
        str = str;
      }
      catch (Exception localException)
      {
        Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", localException);
      }
    }
  }
  
  private void setViewDrawable(ImageView paramImageView, Drawable paramDrawable, int paramInt)
  {
    paramImageView.setImageDrawable(paramDrawable);
    if (paramDrawable != null)
    {
      paramImageView.setVisibility(0);
      paramDrawable.setVisible(false, false);
      paramDrawable.setVisible(true, false);
    }
    else
    {
      paramImageView.setVisibility(paramInt);
    }
  }
  
  private void setViewText(TextView paramTextView, CharSequence paramCharSequence)
  {
    paramTextView.setText(paramCharSequence);
    if (!TextUtils.isEmpty(paramCharSequence)) {
      paramTextView.setVisibility(0);
    } else {
      paramTextView.setVisibility(8);
    }
  }
  
  private void storeInIconCache(String paramString, Drawable paramDrawable)
  {
    if (paramDrawable != null) {
      this.mOutsideDrawablesCache.put(paramString, paramDrawable.getConstantState());
    }
  }
  
  private void updateSpinnerState(Cursor paramCursor)
  {
    Bundle localBundle;
    if (paramCursor == null) {
      localBundle = null;
    } else {
      localBundle = paramCursor.getExtras();
    }
    if ((localBundle != null) && (localBundle.getBoolean("in_progress"))) {}
  }
  
  public void bindView(View paramView, Context paramContext, Cursor paramCursor)
  {
    ChildViewCache localChildViewCache = (ChildViewCache)paramView.getTag();
    int i = 0;
    if (this.mFlagsCol != -1) {
      i = paramCursor.getInt(this.mFlagsCol);
    }
    Object localObject;
    if (localChildViewCache.mText1 != null)
    {
      localObject = getStringOrNull(paramCursor, this.mText1Col);
      setViewText(localChildViewCache.mText1, (CharSequence)localObject);
    }
    if (localChildViewCache.mText2 != null)
    {
      localObject = getStringOrNull(paramCursor, this.mText2UrlCol);
      if (localObject == null) {
        localObject = getStringOrNull(paramCursor, this.mText2Col);
      } else {
        localObject = formatUrl((CharSequence)localObject);
      }
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        if (localChildViewCache.mText1 != null)
        {
          localChildViewCache.mText1.setSingleLine(true);
          localChildViewCache.mText1.setMaxLines(1);
        }
      }
      else if (localChildViewCache.mText1 != null)
      {
        localChildViewCache.mText1.setSingleLine(false);
        localChildViewCache.mText1.setMaxLines(2);
      }
      setViewText(localChildViewCache.mText2, (CharSequence)localObject);
    }
    if (localChildViewCache.mIcon1 != null) {
      setViewDrawable(localChildViewCache.mIcon1, getIcon1(paramCursor), 4);
    }
    if (localChildViewCache.mIcon2 != null) {
      setViewDrawable(localChildViewCache.mIcon2, getIcon2(paramCursor), 8);
    }
    if ((this.mQueryRefinement != 2) && ((this.mQueryRefinement != 1) || ((i & 0x1) == 0)))
    {
      localChildViewCache.mIconRefine.setVisibility(8);
    }
    else
    {
      localChildViewCache.mIconRefine.setVisibility(0);
      localChildViewCache.mIconRefine.setTag(localChildViewCache.mText1.getText());
      localChildViewCache.mIconRefine.setOnClickListener(this);
    }
  }
  
  public void changeCursor(Cursor paramCursor)
  {
    if (this.mClosed)
    {
      Log.w("SuggestionsAdapter", "Tried to change cursor after adapter was closed.");
      if (paramCursor != null) {
        paramCursor.close();
      }
    }
    for (;;)
    {
      return;
      try
      {
        super.changeCursor(paramCursor);
        if (paramCursor != null)
        {
          this.mText1Col = paramCursor.getColumnIndex("suggest_text_1");
          this.mText2Col = paramCursor.getColumnIndex("suggest_text_2");
          this.mText2UrlCol = paramCursor.getColumnIndex("suggest_text_2_url");
          this.mIconName1Col = paramCursor.getColumnIndex("suggest_icon_1");
          this.mIconName2Col = paramCursor.getColumnIndex("suggest_icon_2");
          this.mFlagsCol = paramCursor.getColumnIndex("suggest_flags");
        }
      }
      catch (Exception localException)
      {
        Log.e("SuggestionsAdapter", "error changing cursor and caching columns", localException);
      }
    }
  }
  
  public void close()
  {
    changeCursor(null);
    this.mClosed = true;
  }
  
  public CharSequence convertToString(Cursor paramCursor)
  {
    String str;
    if (paramCursor != null)
    {
      str = getColumnString(paramCursor, "suggest_intent_query");
      if (str == null)
      {
        if (this.mSearchable.shouldRewriteQueryFromData())
        {
          str = getColumnString(paramCursor, "suggest_intent_data");
          if (str != null) {}
        }
        else
        {
          if (this.mSearchable.shouldRewriteQueryFromText())
          {
            str = getColumnString(paramCursor, "suggest_text_1");
            if (str != null) {}
          }
          else
          {
            return null;
          }
          return str;
        }
        str = str;
      }
    }
    else
    {
      str = null;
    }
    return str;
  }
  
  Drawable getDrawableFromResourceUri(Uri paramUri)
    throws FileNotFoundException
  {
    String str = paramUri.getAuthority();
    if (TextUtils.isEmpty(str)) {
      throw new FileNotFoundException("No authority: " + paramUri);
    }
    Resources localResources;
    List localList;
    try
    {
      localResources = this.mContext.getPackageManager().getResourcesForApplication(str);
      localList = paramUri.getPathSegments();
      if (localList == null) {
        throw new FileNotFoundException("No path: " + paramUri);
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new FileNotFoundException("No package found for authority: " + paramUri);
    }
    int j = localList.size();
    if (j == 1) {}
    int i;
    for (;;)
    {
      try
      {
        i = Integer.parseInt((String)localList.get(0));
        i = i;
        if (i != 0) {
          break;
        }
        throw new FileNotFoundException("No resource found for: " + paramUri);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new FileNotFoundException("Single path segment is not a resource ID: " + paramUri);
      }
      if (j == 2) {
        i = localResources.getIdentifier((String)localList.get(1), (String)localList.get(0), i);
      } else {
        throw new FileNotFoundException("More than two path segments: " + paramUri);
      }
    }
    return localResources.getDrawable(i);
  }
  
  public int getQueryRefinement()
  {
    return this.mQueryRefinement;
  }
  
  Cursor getSearchManagerSuggestions(SearchableInfo paramSearchableInfo, String paramString, int paramInt)
  {
    Object localObject = null;
    if (paramSearchableInfo != null)
    {
      String str = paramSearchableInfo.getSuggestAuthority();
      if (str != null)
      {
        localObject = new Uri.Builder().scheme("content").authority(str).query("").fragment("");
        str = paramSearchableInfo.getSuggestPath();
        if (str != null) {
          ((Uri.Builder)localObject).appendEncodedPath(str);
        }
        ((Uri.Builder)localObject).appendPath("search_suggest_query");
        str = paramSearchableInfo.getSuggestSelection();
        String[] arrayOfString = null;
        if (str == null)
        {
          ((Uri.Builder)localObject).appendPath(paramString);
        }
        else
        {
          arrayOfString = new String[1];
          arrayOfString[0] = paramString;
        }
        if (paramInt > 0) {
          ((Uri.Builder)localObject).appendQueryParameter("limit", String.valueOf(paramInt));
        }
        localObject = ((Uri.Builder)localObject).build();
        localObject = this.mContext.getContentResolver().query((Uri)localObject, null, str, arrayOfString, null);
      }
    }
    return (Cursor)localObject;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    try
    {
      localView = super.getView(paramInt, paramView, paramViewGroup);
      localView = localView;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", localRuntimeException);
        View localView = newView(this.mContext, this.mCursor, paramViewGroup);
        if (localView != null) {
          ((ChildViewCache)localView.getTag()).mText1.setText(localRuntimeException.toString());
        }
      }
    }
    return localView;
  }
  
  public boolean hasStableIds()
  {
    return false;
  }
  
  public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    View localView = super.newView(paramContext, paramCursor, paramViewGroup);
    localView.setTag(new ChildViewCache(localView));
    ((ImageView)localView.findViewById(R.id.edit_query)).setImageResource(this.mCommitIconResId);
    return localView;
  }
  
  public void notifyDataSetChanged()
  {
    super.notifyDataSetChanged();
    updateSpinnerState(getCursor());
  }
  
  public void notifyDataSetInvalidated()
  {
    super.notifyDataSetInvalidated();
    updateSpinnerState(getCursor());
  }
  
  public void onClick(View paramView)
  {
    Object localObject = paramView.getTag();
    if ((localObject instanceof CharSequence)) {
      this.mSearchView.onQueryRefine((CharSequence)localObject);
    }
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    Object localObject1 = null;
    Object localObject2;
    if (paramCharSequence == null)
    {
      localObject2 = "";
      if ((this.mSearchView.getVisibility() == 0) && (this.mSearchView.getWindowVisibility() == 0)) {
        break label42;
      }
    }
    for (;;)
    {
      return (Cursor)localObject1;
      localObject2 = paramCharSequence.toString();
      break;
      try
      {
        label42:
        localObject2 = getSearchManagerSuggestions(this.mSearchable, (String)localObject2, 50);
        if (localObject2 != null)
        {
          ((Cursor)localObject2).getCount();
          localObject1 = localObject2;
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        Log.w("SuggestionsAdapter", "Search suggestions query threw an exception.", localRuntimeException);
      }
    }
  }
  
  public void setQueryRefinement(int paramInt)
  {
    this.mQueryRefinement = paramInt;
  }
  
  private static final class ChildViewCache
  {
    public final ImageView mIcon1;
    public final ImageView mIcon2;
    public final ImageView mIconRefine;
    public final TextView mText1;
    public final TextView mText2;
    
    public ChildViewCache(View paramView)
    {
      this.mText1 = ((TextView)paramView.findViewById(16908308));
      this.mText2 = ((TextView)paramView.findViewById(16908309));
      this.mIcon1 = ((ImageView)paramView.findViewById(16908295));
      this.mIcon2 = ((ImageView)paramView.findViewById(16908296));
      this.mIconRefine = ((ImageView)paramView.findViewById(R.id.edit_query));
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\SuggestionsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */