package android.support.v7.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class SearchView
  extends LinearLayoutCompat
  implements CollapsibleActionView
{
  private static final boolean DBG = false;
  static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
  private static final String IME_OPTION_NO_MICROPHONE = "nm";
  private static final boolean IS_AT_LEAST_FROYO = false;
  private static final String LOG_TAG = "SearchView";
  private Bundle mAppSearchData;
  private boolean mClearingFocus;
  private final ImageView mCloseButton;
  private int mCollapsedImeOptions;
  private final View mDropDownAnchor;
  private boolean mExpandedInActionView;
  private boolean mIconified;
  private boolean mIconifiedByDefault;
  private int mMaxWidth;
  private CharSequence mOldQueryText;
  private final View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (paramAnonymousView != SearchView.this.mSearchButton)
      {
        if (paramAnonymousView != SearchView.this.mCloseButton)
        {
          if (paramAnonymousView != SearchView.this.mSubmitButton)
          {
            if (paramAnonymousView != SearchView.this.mVoiceButton)
            {
              if (paramAnonymousView == SearchView.this.mQueryTextView) {
                SearchView.this.forceSuggestionQuery();
              }
            }
            else if (SearchView.IS_AT_LEAST_FROYO) {
              SearchView.this.onVoiceClicked();
            }
          }
          else {
            SearchView.this.onSubmitQuery();
          }
        }
        else {
          SearchView.this.onCloseClicked();
        }
      }
      else {
        SearchView.this.onSearchClicked();
      }
    }
  };
  private OnCloseListener mOnCloseListener;
  private final TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener()
  {
    public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      SearchView.this.onSubmitQuery();
      return true;
    }
  };
  private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      SearchView.this.onItemClicked(paramAnonymousInt, 0, null);
    }
  };
  private final AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      SearchView.this.onItemSelected(paramAnonymousInt);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  private OnQueryTextListener mOnQueryChangeListener;
  private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
  private View.OnClickListener mOnSearchClickListener;
  private OnSuggestionListener mOnSuggestionListener;
  private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache = new WeakHashMap();
  private CharSequence mQueryHint;
  private boolean mQueryRefinement;
  private final SearchAutoComplete mQueryTextView;
  private Runnable mReleaseCursorRunnable = new Runnable()
  {
    public void run()
    {
      if ((SearchView.this.mSuggestionsAdapter != null) && ((SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter))) {
        SearchView.this.mSuggestionsAdapter.changeCursor(null);
      }
    }
  };
  private final ImageView mSearchButton;
  private final View mSearchEditFrame;
  private final ImageView mSearchHintIcon;
  private final int mSearchIconResId;
  private final View mSearchPlate;
  private SearchableInfo mSearchable;
  private Runnable mShowImeRunnable = new Runnable()
  {
    public void run()
    {
      InputMethodManager localInputMethodManager = (InputMethodManager)SearchView.this.getContext().getSystemService("input_method");
      if (localInputMethodManager != null) {
        SearchView.HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(localInputMethodManager, SearchView.this, 0);
      }
    }
  };
  private final View mSubmitArea;
  private final ImageView mSubmitButton;
  private boolean mSubmitButtonEnabled;
  private final int mSuggestionCommitIconResId;
  private final int mSuggestionRowLayout;
  private CursorAdapter mSuggestionsAdapter;
  View.OnKeyListener mTextKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      boolean bool = false;
      if (SearchView.this.mSearchable != null) {
        if ((!SearchView.this.mQueryTextView.isPopupShowing()) || (SearchView.this.mQueryTextView.getListSelection() == -1))
        {
          if ((!SearchView.SearchAutoComplete.access$1700(SearchView.this.mQueryTextView)) && (KeyEventCompat.hasNoModifiers(paramAnonymousKeyEvent)) && (paramAnonymousKeyEvent.getAction() == 1) && (paramAnonymousInt == 66))
          {
            paramAnonymousView.cancelLongPress();
            SearchView.this.launchQuerySearch(0, null, SearchView.this.mQueryTextView.getText().toString());
            bool = true;
          }
        }
        else {
          bool = SearchView.this.onSuggestionsKey(paramAnonymousView, paramAnonymousInt, paramAnonymousKeyEvent);
        }
      }
      return bool;
    }
  };
  private TextWatcher mTextWatcher = new TextWatcher()
  {
    public void afterTextChanged(Editable paramAnonymousEditable) {}
    
    public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    
    public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      SearchView.this.onTextChanged(paramAnonymousCharSequence);
    }
  };
  private final TintManager mTintManager;
  private final Runnable mUpdateDrawableStateRunnable = new Runnable()
  {
    public void run()
    {
      SearchView.this.updateFocusedState();
    }
  };
  private CharSequence mUserQuery;
  private final Intent mVoiceAppSearchIntent;
  private final ImageView mVoiceButton;
  private boolean mVoiceButtonEnabled;
  private final Intent mVoiceWebSearchIntent;
  
  static
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 8) {
      bool = false;
    } else {
      bool = true;
    }
    IS_AT_LEAST_FROYO = bool;
  }
  
  public SearchView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SearchView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.searchViewStyle);
  }
  
  public SearchView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.SearchView, paramInt, 0);
    this.mTintManager = localTintTypedArray.getTintManager();
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(localTintTypedArray.getResourceId(R.styleable.SearchView_layout, 0), this, true);
    this.mQueryTextView = ((SearchAutoComplete)findViewById(R.id.search_src_text));
    this.mQueryTextView.setSearchView(this);
    this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
    this.mSearchPlate = findViewById(R.id.search_plate);
    this.mSubmitArea = findViewById(R.id.submit_area);
    this.mSearchButton = ((ImageView)findViewById(R.id.search_button));
    this.mSubmitButton = ((ImageView)findViewById(R.id.search_go_btn));
    this.mCloseButton = ((ImageView)findViewById(R.id.search_close_btn));
    this.mVoiceButton = ((ImageView)findViewById(R.id.search_voice_btn));
    this.mSearchHintIcon = ((ImageView)findViewById(R.id.search_mag_icon));
    this.mSearchPlate.setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_queryBackground));
    this.mSubmitArea.setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_submitBackground));
    this.mSearchIconResId = localTintTypedArray.getResourceId(R.styleable.SearchView_searchIcon, 0);
    this.mSearchButton.setImageResource(this.mSearchIconResId);
    this.mSubmitButton.setImageDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_goIcon));
    this.mCloseButton.setImageDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_closeIcon));
    this.mVoiceButton.setImageDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_voiceIcon));
    this.mSearchHintIcon.setImageDrawable(localTintTypedArray.getDrawable(R.styleable.SearchView_searchIcon));
    this.mSuggestionRowLayout = localTintTypedArray.getResourceId(R.styleable.SearchView_suggestionRowLayout, 0);
    this.mSuggestionCommitIconResId = localTintTypedArray.getResourceId(R.styleable.SearchView_commitIcon, 0);
    this.mSearchButton.setOnClickListener(this.mOnClickListener);
    this.mCloseButton.setOnClickListener(this.mOnClickListener);
    this.mSubmitButton.setOnClickListener(this.mOnClickListener);
    this.mVoiceButton.setOnClickListener(this.mOnClickListener);
    this.mQueryTextView.setOnClickListener(this.mOnClickListener);
    this.mQueryTextView.addTextChangedListener(this.mTextWatcher);
    this.mQueryTextView.setOnEditorActionListener(this.mOnEditorActionListener);
    this.mQueryTextView.setOnItemClickListener(this.mOnItemClickListener);
    this.mQueryTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
    this.mQueryTextView.setOnKeyListener(this.mTextKeyListener);
    this.mQueryTextView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
          SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, paramAnonymousBoolean);
        }
      }
    });
    setIconifiedByDefault(localTintTypedArray.getBoolean(R.styleable.SearchView_iconifiedByDefault, true));
    int i = localTintTypedArray.getDimensionPixelSize(R.styleable.SearchView_android_maxWidth, -1);
    if (i != -1) {
      setMaxWidth(i);
    }
    CharSequence localCharSequence = localTintTypedArray.getText(R.styleable.SearchView_queryHint);
    if (!TextUtils.isEmpty(localCharSequence)) {
      setQueryHint(localCharSequence);
    }
    int j = localTintTypedArray.getInt(R.styleable.SearchView_android_imeOptions, -1);
    if (j != -1) {
      setImeOptions(j);
    }
    j = localTintTypedArray.getInt(R.styleable.SearchView_android_inputType, -1);
    if (j != -1) {
      setInputType(j);
    }
    setFocusable(localTintTypedArray.getBoolean(R.styleable.SearchView_android_focusable, true));
    localTintTypedArray.recycle();
    this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
    this.mVoiceWebSearchIntent.addFlags(268435456);
    this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
    this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
    this.mVoiceAppSearchIntent.addFlags(268435456);
    this.mDropDownAnchor = findViewById(this.mQueryTextView.getDropDownAnchor());
    if (this.mDropDownAnchor != null) {
      if (Build.VERSION.SDK_INT < 11) {
        addOnLayoutChangeListenerToDropDownAnchorBase();
      } else {
        addOnLayoutChangeListenerToDropDownAnchorSDK11();
      }
    }
    updateViewsVisibility(this.mIconifiedByDefault);
    updateQueryHint();
  }
  
  private void addOnLayoutChangeListenerToDropDownAnchorBase()
  {
    this.mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        SearchView.this.adjustDropDownSizeAndPosition();
      }
    });
  }
  
  @TargetApi(11)
  private void addOnLayoutChangeListenerToDropDownAnchorSDK11()
  {
    this.mDropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        SearchView.this.adjustDropDownSizeAndPosition();
      }
    });
  }
  
  private void adjustDropDownSizeAndPosition()
  {
    if (this.mDropDownAnchor.getWidth() > 1)
    {
      Resources localResources = getContext().getResources();
      int j = this.mSearchPlate.getPaddingLeft();
      Rect localRect = new Rect();
      boolean bool = ViewUtils.isLayoutRtl(this);
      int k;
      if (!this.mIconifiedByDefault) {
        k = 0;
      } else {
        k = k.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width) + k.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left);
      }
      this.mQueryTextView.getDropDownBackground().getPadding(localRect);
      int m;
      if (!bool) {
        m = j - (k + localRect.left);
      } else {
        m = -localRect.left;
      }
      this.mQueryTextView.setDropDownHorizontalOffset(m);
      int i = k + (this.mDropDownAnchor.getWidth() + localRect.left + localRect.right) - j;
      this.mQueryTextView.setDropDownWidth(i);
    }
  }
  
  private Intent createIntent(String paramString1, Uri paramUri, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    Intent localIntent = new Intent(paramString1);
    localIntent.addFlags(268435456);
    if (paramUri != null) {
      localIntent.setData(paramUri);
    }
    localIntent.putExtra("user_query", this.mUserQuery);
    if (paramString3 != null) {
      localIntent.putExtra("query", paramString3);
    }
    if (paramString2 != null) {
      localIntent.putExtra("intent_extra_data_key", paramString2);
    }
    if (this.mAppSearchData != null) {
      localIntent.putExtra("app_data", this.mAppSearchData);
    }
    if (paramInt != 0)
    {
      localIntent.putExtra("action_key", paramInt);
      localIntent.putExtra("action_msg", paramString4);
    }
    if (IS_AT_LEAST_FROYO) {
      localIntent.setComponent(this.mSearchable.getSearchActivity());
    }
    return localIntent;
  }
  
  private Intent createIntentFromSuggestion(Cursor paramCursor, int paramInt, String paramString)
  {
    try
    {
      Object localObject1 = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_action");
      if ((localObject1 == null) && (Build.VERSION.SDK_INT >= 8))
      {
        localObject1 = this.mSearchable.getSuggestIntentAction();
        break label226;
        Object localObject2 = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_data");
        if ((IS_AT_LEAST_FROYO) && (localObject2 == null)) {
          localObject2 = this.mSearchable.getSuggestIntentData();
        }
        if (localObject2 == null) {
          break label239;
        }
        localObject3 = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_data_id");
        if (localObject3 == null) {
          break label239;
        }
        localObject2 = (String)localObject2 + "/" + Uri.encode((String)localObject3);
        break label239;
        for (;;)
        {
          localObject2 = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_query");
          localObject1 = createIntent((String)localObject1, (Uri)localObject3, SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_extra_data"), (String)localObject2, paramInt, paramString);
          break;
          localObject2 = Uri.parse((String)localObject2);
          localObject3 = localObject2;
        }
      }
    }
    catch (RuntimeException localRuntimeException1)
    {
      String str;
      for (;;)
      {
        Object localObject3;
        int i;
        try
        {
          i = paramCursor.getPosition();
          i = i;
        }
        catch (RuntimeException localRuntimeException2)
        {
          i = -1;
          continue;
        }
        Log.w("SearchView", "Search suggestions cursor at row " + i + " returned exception.", localRuntimeException1);
        str = null;
        break;
        label226:
        if (str == null)
        {
          str = "android.intent.action.SEARCH";
          continue;
          label239:
          if (i == null) {
            localObject3 = null;
          }
        }
      }
      return str;
    }
  }
  
  @TargetApi(8)
  private Intent createVoiceAppSearchIntent(Intent paramIntent, SearchableInfo paramSearchableInfo)
  {
    Object localObject = paramSearchableInfo.getSearchActivity();
    Intent localIntent = new Intent("android.intent.action.SEARCH");
    localIntent.setComponent((ComponentName)localObject);
    PendingIntent localPendingIntent = PendingIntent.getActivity(getContext(), 0, localIntent, 1073741824);
    Bundle localBundle = new Bundle();
    if (this.mAppSearchData != null) {
      localBundle.putParcelable("app_data", this.mAppSearchData);
    }
    localIntent = new Intent(paramIntent);
    String str1 = "free_form";
    String str3 = null;
    String str2 = null;
    int i = 1;
    if (Build.VERSION.SDK_INT >= 8)
    {
      Resources localResources = getResources();
      if (paramSearchableInfo.getVoiceLanguageModeId() != 0) {
        str1 = localResources.getString(paramSearchableInfo.getVoiceLanguageModeId());
      }
      if (paramSearchableInfo.getVoicePromptTextId() != 0) {
        str3 = localResources.getString(paramSearchableInfo.getVoicePromptTextId());
      }
      if (paramSearchableInfo.getVoiceLanguageId() != 0) {
        str2 = localResources.getString(paramSearchableInfo.getVoiceLanguageId());
      }
      if (paramSearchableInfo.getVoiceMaxResults() != 0) {
        i = paramSearchableInfo.getVoiceMaxResults();
      }
    }
    localIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", str1);
    localIntent.putExtra("android.speech.extra.PROMPT", str3);
    localIntent.putExtra("android.speech.extra.LANGUAGE", str2);
    localIntent.putExtra("android.speech.extra.MAX_RESULTS", i);
    if (localObject != null) {
      localObject = ((ComponentName)localObject).flattenToShortString();
    } else {
      localObject = null;
    }
    localIntent.putExtra("calling_package", (String)localObject);
    localIntent.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", localPendingIntent);
    localIntent.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", localBundle);
    return localIntent;
  }
  
  @TargetApi(8)
  private Intent createVoiceWebSearchIntent(Intent paramIntent, SearchableInfo paramSearchableInfo)
  {
    Intent localIntent = new Intent(paramIntent);
    Object localObject = paramSearchableInfo.getSearchActivity();
    if (localObject != null) {
      localObject = ((ComponentName)localObject).flattenToShortString();
    } else {
      localObject = null;
    }
    localIntent.putExtra("calling_package", (String)localObject);
    return localIntent;
  }
  
  private void dismissSuggestions()
  {
    this.mQueryTextView.dismissDropDown();
  }
  
  private void forceSuggestionQuery()
  {
    HIDDEN_METHOD_INVOKER.doBeforeTextChanged(this.mQueryTextView);
    HIDDEN_METHOD_INVOKER.doAfterTextChanged(this.mQueryTextView);
  }
  
  private CharSequence getDecoratedHint(CharSequence paramCharSequence)
  {
    if (this.mIconifiedByDefault)
    {
      Drawable localDrawable = this.mTintManager.getDrawable(this.mSearchIconResId);
      int i = (int)(1.25D * this.mQueryTextView.getTextSize());
      localDrawable.setBounds(0, 0, i, i);
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("   ");
      localSpannableStringBuilder.append(paramCharSequence);
      localSpannableStringBuilder.setSpan(new ImageSpan(localDrawable), 1, 2, 33);
      paramCharSequence = localSpannableStringBuilder;
    }
    return paramCharSequence;
  }
  
  private int getPreferredWidth()
  {
    return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
  }
  
  @TargetApi(8)
  private boolean hasVoiceSearch()
  {
    boolean bool = false;
    if ((this.mSearchable != null) && (this.mSearchable.getVoiceSearchEnabled()))
    {
      Intent localIntent = null;
      if (!this.mSearchable.getVoiceSearchLaunchWebSearch())
      {
        if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
          localIntent = this.mVoiceAppSearchIntent;
        }
      }
      else {
        localIntent = this.mVoiceWebSearchIntent;
      }
      if ((localIntent != null) && (getContext().getPackageManager().resolveActivity(localIntent, 65536) != null)) {
        bool = true;
      }
    }
    return bool;
  }
  
  static boolean isLandscapeMode(Context paramContext)
  {
    boolean bool;
    if (paramContext.getResources().getConfiguration().orientation != 2) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean isSubmitAreaEnabled()
  {
    boolean bool;
    if (((!this.mSubmitButtonEnabled) && (!this.mVoiceButtonEnabled)) || (isIconified())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void launchIntent(Intent paramIntent)
  {
    if (paramIntent == null) {}
    for (;;)
    {
      return;
      try
      {
        getContext().startActivity(paramIntent);
      }
      catch (RuntimeException localRuntimeException)
      {
        Log.e("SearchView", "Failed launch activity: " + paramIntent, localRuntimeException);
      }
    }
  }
  
  private void launchQuerySearch(int paramInt, String paramString1, String paramString2)
  {
    Intent localIntent = createIntent("android.intent.action.SEARCH", null, null, paramString2, paramInt, paramString1);
    getContext().startActivity(localIntent);
  }
  
  private boolean launchSuggestion(int paramInt1, int paramInt2, String paramString)
  {
    Cursor localCursor = this.mSuggestionsAdapter.getCursor();
    boolean bool;
    if ((localCursor == null) || (!localCursor.moveToPosition(paramInt1)))
    {
      bool = false;
    }
    else
    {
      launchIntent(createIntentFromSuggestion(bool, paramInt2, paramString));
      bool = true;
    }
    return bool;
  }
  
  private void onCloseClicked()
  {
    if (!TextUtils.isEmpty(this.mQueryTextView.getText()))
    {
      this.mQueryTextView.setText("");
      this.mQueryTextView.requestFocus();
      setImeVisibility(true);
    }
    else if ((this.mIconifiedByDefault) && ((this.mOnCloseListener == null) || (!this.mOnCloseListener.onClose())))
    {
      clearFocus();
      updateViewsVisibility(true);
    }
  }
  
  private boolean onItemClicked(int paramInt1, int paramInt2, String paramString)
  {
    boolean bool = false;
    if ((this.mOnSuggestionListener == null) || (!this.mOnSuggestionListener.onSuggestionClick(paramInt1)))
    {
      launchSuggestion(paramInt1, 0, null);
      setImeVisibility(false);
      dismissSuggestions();
      bool = true;
    }
    return bool;
  }
  
  private boolean onItemSelected(int paramInt)
  {
    boolean bool;
    if ((this.mOnSuggestionListener != null) && (this.mOnSuggestionListener.onSuggestionSelect(paramInt)))
    {
      bool = false;
    }
    else
    {
      rewriteQueryFromSuggestion(paramInt);
      bool = true;
    }
    return bool;
  }
  
  private void onSearchClicked()
  {
    updateViewsVisibility(false);
    this.mQueryTextView.requestFocus();
    setImeVisibility(true);
    if (this.mOnSearchClickListener != null) {
      this.mOnSearchClickListener.onClick(this);
    }
  }
  
  private void onSubmitQuery()
  {
    Editable localEditable = this.mQueryTextView.getText();
    if ((localEditable != null) && (TextUtils.getTrimmedLength(localEditable) > 0) && ((this.mOnQueryChangeListener == null) || (!this.mOnQueryChangeListener.onQueryTextSubmit(localEditable.toString()))))
    {
      if (this.mSearchable != null) {
        launchQuerySearch(0, null, localEditable.toString());
      }
      setImeVisibility(false);
      dismissSuggestions();
    }
  }
  
  private boolean onSuggestionsKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    int i = 0;
    boolean bool;
    if ((this.mSearchable != null) && (this.mSuggestionsAdapter != null) && (paramKeyEvent.getAction() == 0) && (KeyEventCompat.hasNoModifiers(paramKeyEvent))) {
      if ((paramInt != 66) && (paramInt != 84) && (paramInt != 61))
      {
        if ((paramInt != 21) && (paramInt != 22))
        {
          if ((paramInt != 19) || (this.mQueryTextView.getListSelection() != 0)) {}
        }
        else
        {
          if (paramInt != 21) {
            i = this.mQueryTextView.length();
          } else {
            i = 0;
          }
          this.mQueryTextView.setSelection(i);
          this.mQueryTextView.setListSelection(0);
          this.mQueryTextView.clearListSelection();
          HIDDEN_METHOD_INVOKER.ensureImeVisible(this.mQueryTextView, true);
          i = 1;
        }
      }
      else {
        bool = onItemClicked(this.mQueryTextView.getListSelection(), 0, null);
      }
    }
    return bool;
  }
  
  private void onTextChanged(CharSequence paramCharSequence)
  {
    boolean bool1 = true;
    Editable localEditable = this.mQueryTextView.getText();
    this.mUserQuery = localEditable;
    boolean bool2;
    if (TextUtils.isEmpty(localEditable)) {
      bool2 = false;
    } else {
      bool2 = bool1;
    }
    updateSubmitButton(bool2);
    if (bool2) {
      bool1 = false;
    }
    updateVoiceButton(bool1);
    updateCloseButton();
    updateSubmitArea();
    if ((this.mOnQueryChangeListener != null) && (!TextUtils.equals(paramCharSequence, this.mOldQueryText))) {
      this.mOnQueryChangeListener.onQueryTextChange(paramCharSequence.toString());
    }
    this.mOldQueryText = paramCharSequence.toString();
  }
  
  @TargetApi(8)
  private void onVoiceClicked()
  {
    if (this.mSearchable == null) {}
    for (;;)
    {
      return;
      Object localObject = this.mSearchable;
      try
      {
        if (!((SearchableInfo)localObject).getVoiceSearchLaunchWebSearch()) {
          break label54;
        }
        localObject = createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, (SearchableInfo)localObject);
        getContext().startActivity((Intent)localObject);
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        Log.w("SearchView", "Could not find voice search activity");
      }
      continue;
      label54:
      if (((SearchableInfo)localObject).getVoiceSearchLaunchRecognizer())
      {
        localObject = createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, (SearchableInfo)localObject);
        getContext().startActivity((Intent)localObject);
      }
    }
  }
  
  private void postUpdateFocusedState()
  {
    post(this.mUpdateDrawableStateRunnable);
  }
  
  private void rewriteQueryFromSuggestion(int paramInt)
  {
    Editable localEditable = this.mQueryTextView.getText();
    Object localObject = this.mSuggestionsAdapter.getCursor();
    if (localObject != null) {
      if (!((Cursor)localObject).moveToPosition(paramInt))
      {
        setQuery(localEditable);
      }
      else
      {
        localObject = this.mSuggestionsAdapter.convertToString((Cursor)localObject);
        if (localObject == null) {
          setQuery(localEditable);
        } else {
          setQuery((CharSequence)localObject);
        }
      }
    }
  }
  
  private void setImeVisibility(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      removeCallbacks(this.mShowImeRunnable);
      InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService("input_method");
      if (localInputMethodManager != null) {
        localInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
      }
    }
    else
    {
      post(this.mShowImeRunnable);
    }
  }
  
  private void setQuery(CharSequence paramCharSequence)
  {
    this.mQueryTextView.setText(paramCharSequence);
    SearchAutoComplete localSearchAutoComplete = this.mQueryTextView;
    int i;
    if (!TextUtils.isEmpty(paramCharSequence)) {
      i = paramCharSequence.length();
    } else {
      i = 0;
    }
    localSearchAutoComplete.setSelection(i);
  }
  
  private void updateCloseButton()
  {
    int j = 1;
    int k = 0;
    int i;
    if (TextUtils.isEmpty(this.mQueryTextView.getText())) {
      i = 0;
    } else {
      i = j;
    }
    if ((i == 0) && ((!this.mIconifiedByDefault) || (this.mExpandedInActionView))) {
      j = 0;
    }
    ImageView localImageView = this.mCloseButton;
    if (j == 0) {
      k = 8;
    }
    localImageView.setVisibility(k);
    Drawable localDrawable = this.mCloseButton.getDrawable();
    int[] arrayOfInt;
    if (i == 0) {
      arrayOfInt = EMPTY_STATE_SET;
    } else {
      arrayOfInt = ENABLED_STATE_SET;
    }
    localDrawable.setState(arrayOfInt);
  }
  
  private void updateFocusedState()
  {
    boolean bool = this.mQueryTextView.hasFocus();
    Drawable localDrawable = this.mSearchPlate.getBackground();
    if (!bool) {
      localObject = EMPTY_STATE_SET;
    } else {
      localObject = ENABLED_FOCUSED_STATE_SET;
    }
    localDrawable.setState((int[])localObject);
    Object localObject = this.mSubmitArea.getBackground();
    int[] arrayOfInt;
    if (!bool) {
      arrayOfInt = EMPTY_STATE_SET;
    } else {
      arrayOfInt = ENABLED_FOCUSED_STATE_SET;
    }
    ((Drawable)localObject).setState(arrayOfInt);
    invalidate();
  }
  
  private void updateQueryHint()
  {
    if (this.mQueryHint == null)
    {
      if ((!IS_AT_LEAST_FROYO) || (this.mSearchable == null))
      {
        this.mQueryTextView.setHint(getDecoratedHint(""));
      }
      else
      {
        String str = null;
        int i = this.mSearchable.getHintId();
        if (i != 0) {
          str = getContext().getString(i);
        }
        if (str != null) {
          this.mQueryTextView.setHint(getDecoratedHint(str));
        }
      }
    }
    else {
      this.mQueryTextView.setHint(getDecoratedHint(this.mQueryHint));
    }
  }
  
  @TargetApi(8)
  private void updateSearchAutoComplete()
  {
    int i = 1;
    this.mQueryTextView.setThreshold(this.mSearchable.getSuggestThreshold());
    this.mQueryTextView.setImeOptions(this.mSearchable.getImeOptions());
    int j = this.mSearchable.getInputType();
    if ((j & 0xF) == i)
    {
      j &= 0xFFFEFFFF;
      if (this.mSearchable.getSuggestAuthority() != null) {
        j = 0x80000 | j | 0x10000;
      }
    }
    this.mQueryTextView.setInputType(j);
    if (this.mSuggestionsAdapter != null) {
      this.mSuggestionsAdapter.changeCursor(null);
    }
    if (this.mSearchable.getSuggestAuthority() != null)
    {
      this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
      this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
      SuggestionsAdapter localSuggestionsAdapter = (SuggestionsAdapter)this.mSuggestionsAdapter;
      if (this.mQueryRefinement) {
        i = 2;
      }
      localSuggestionsAdapter.setQueryRefinement(i);
    }
  }
  
  private void updateSubmitArea()
  {
    int i = 8;
    if ((isSubmitAreaEnabled()) && ((this.mSubmitButton.getVisibility() == 0) || (this.mVoiceButton.getVisibility() == 0))) {
      i = 0;
    }
    this.mSubmitArea.setVisibility(i);
  }
  
  private void updateSubmitButton(boolean paramBoolean)
  {
    int i = 8;
    if ((this.mSubmitButtonEnabled) && (isSubmitAreaEnabled()) && (hasFocus()) && ((paramBoolean) || (!this.mVoiceButtonEnabled))) {
      i = 0;
    }
    this.mSubmitButton.setVisibility(i);
  }
  
  private void updateViewsVisibility(boolean paramBoolean)
  {
    boolean bool1 = true;
    int i = 8;
    this.mIconified = paramBoolean;
    int j;
    if (!paramBoolean) {
      j = i;
    } else {
      j = 0;
    }
    boolean bool2;
    if (TextUtils.isEmpty(this.mQueryTextView.getText())) {
      bool2 = false;
    } else {
      bool2 = bool1;
    }
    this.mSearchButton.setVisibility(j);
    updateSubmitButton(bool2);
    Object localObject = this.mSearchEditFrame;
    int k;
    if (!paramBoolean) {
      k = 0;
    } else {
      k = i;
    }
    ((View)localObject).setVisibility(k);
    localObject = this.mSearchHintIcon;
    if (!this.mIconifiedByDefault) {
      i = 0;
    }
    ((ImageView)localObject).setVisibility(i);
    updateCloseButton();
    if (bool2) {
      bool1 = false;
    }
    updateVoiceButton(bool1);
    updateSubmitArea();
  }
  
  private void updateVoiceButton(boolean paramBoolean)
  {
    int i = 8;
    if ((this.mVoiceButtonEnabled) && (!isIconified()) && (paramBoolean))
    {
      i = 0;
      this.mSubmitButton.setVisibility(8);
    }
    this.mVoiceButton.setVisibility(i);
  }
  
  public void clearFocus()
  {
    this.mClearingFocus = true;
    setImeVisibility(false);
    super.clearFocus();
    this.mQueryTextView.clearFocus();
    this.mClearingFocus = false;
  }
  
  public int getImeOptions()
  {
    return this.mQueryTextView.getImeOptions();
  }
  
  public int getInputType()
  {
    return this.mQueryTextView.getInputType();
  }
  
  public int getMaxWidth()
  {
    return this.mMaxWidth;
  }
  
  public CharSequence getQuery()
  {
    return this.mQueryTextView.getText();
  }
  
  public CharSequence getQueryHint()
  {
    Object localObject;
    if (this.mQueryHint == null)
    {
      if ((!IS_AT_LEAST_FROYO) || (this.mSearchable == null))
      {
        localObject = null;
      }
      else
      {
        localObject = null;
        int i = this.mSearchable.getHintId();
        if (i != 0) {
          localObject = getContext().getString(i);
        }
      }
    }
    else {
      localObject = this.mQueryHint;
    }
    return (CharSequence)localObject;
  }
  
  int getSuggestionCommitIconResId()
  {
    return this.mSuggestionCommitIconResId;
  }
  
  int getSuggestionRowLayout()
  {
    return this.mSuggestionRowLayout;
  }
  
  public CursorAdapter getSuggestionsAdapter()
  {
    return this.mSuggestionsAdapter;
  }
  
  public boolean isIconfiedByDefault()
  {
    return this.mIconifiedByDefault;
  }
  
  public boolean isIconified()
  {
    return this.mIconified;
  }
  
  public boolean isQueryRefinementEnabled()
  {
    return this.mQueryRefinement;
  }
  
  public boolean isSubmitButtonEnabled()
  {
    return this.mSubmitButtonEnabled;
  }
  
  public void onActionViewCollapsed()
  {
    setQuery("", false);
    clearFocus();
    updateViewsVisibility(true);
    this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions);
    this.mExpandedInActionView = false;
  }
  
  public void onActionViewExpanded()
  {
    if (!this.mExpandedInActionView)
    {
      this.mExpandedInActionView = true;
      this.mCollapsedImeOptions = this.mQueryTextView.getImeOptions();
      this.mQueryTextView.setImeOptions(0x2000000 | this.mCollapsedImeOptions);
      this.mQueryTextView.setText("");
      setIconified(false);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mUpdateDrawableStateRunnable);
    post(this.mReleaseCursorRunnable);
    super.onDetachedFromWindow();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!isIconified())
    {
      int j = View.MeasureSpec.getMode(paramInt1);
      int i = View.MeasureSpec.getSize(paramInt1);
      switch (j)
      {
      case -2147483648: 
        if (this.mMaxWidth <= 0) {
          i = Math.min(getPreferredWidth(), i);
        } else {
          i = Math.min(this.mMaxWidth, i);
        }
        break;
      case 0: 
        if (this.mMaxWidth <= 0) {
          i = getPreferredWidth();
        } else {
          i = this.mMaxWidth;
        }
        break;
      case 1073741824: 
        if (this.mMaxWidth > 0) {
          i = Math.min(this.mMaxWidth, i);
        }
        break;
      }
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), paramInt2);
    }
    else
    {
      super.onMeasure(paramInt1, paramInt2);
    }
  }
  
  void onQueryRefine(CharSequence paramCharSequence)
  {
    setQuery(paramCharSequence);
  }
  
  void onTextFocusChanged()
  {
    updateViewsVisibility(isIconified());
    postUpdateFocusedState();
    if (this.mQueryTextView.hasFocus()) {
      forceSuggestionQuery();
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    postUpdateFocusedState();
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    boolean bool;
    if (!this.mClearingFocus)
    {
      if (isFocusable())
      {
        if (isIconified())
        {
          bool = super.requestFocus(paramInt, paramRect);
        }
        else
        {
          bool = this.mQueryTextView.requestFocus(paramInt, paramRect);
          if (bool) {
            updateViewsVisibility(false);
          }
        }
      }
      else {
        bool = false;
      }
    }
    else {
      bool = false;
    }
    return bool;
  }
  
  public void setAppSearchData(Bundle paramBundle)
  {
    this.mAppSearchData = paramBundle;
  }
  
  public void setIconified(boolean paramBoolean)
  {
    if (!paramBoolean) {
      onSearchClicked();
    } else {
      onCloseClicked();
    }
  }
  
  public void setIconifiedByDefault(boolean paramBoolean)
  {
    if (this.mIconifiedByDefault != paramBoolean)
    {
      this.mIconifiedByDefault = paramBoolean;
      updateViewsVisibility(paramBoolean);
      updateQueryHint();
    }
  }
  
  public void setImeOptions(int paramInt)
  {
    this.mQueryTextView.setImeOptions(paramInt);
  }
  
  public void setInputType(int paramInt)
  {
    this.mQueryTextView.setInputType(paramInt);
  }
  
  public void setMaxWidth(int paramInt)
  {
    this.mMaxWidth = paramInt;
    requestLayout();
  }
  
  public void setOnCloseListener(OnCloseListener paramOnCloseListener)
  {
    this.mOnCloseListener = paramOnCloseListener;
  }
  
  public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener paramOnFocusChangeListener)
  {
    this.mOnQueryTextFocusChangeListener = paramOnFocusChangeListener;
  }
  
  public void setOnQueryTextListener(OnQueryTextListener paramOnQueryTextListener)
  {
    this.mOnQueryChangeListener = paramOnQueryTextListener;
  }
  
  public void setOnSearchClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnSearchClickListener = paramOnClickListener;
  }
  
  public void setOnSuggestionListener(OnSuggestionListener paramOnSuggestionListener)
  {
    this.mOnSuggestionListener = paramOnSuggestionListener;
  }
  
  public void setQuery(CharSequence paramCharSequence, boolean paramBoolean)
  {
    this.mQueryTextView.setText(paramCharSequence);
    if (paramCharSequence != null)
    {
      this.mQueryTextView.setSelection(this.mQueryTextView.length());
      this.mUserQuery = paramCharSequence;
    }
    if ((paramBoolean) && (!TextUtils.isEmpty(paramCharSequence))) {
      onSubmitQuery();
    }
  }
  
  public void setQueryHint(CharSequence paramCharSequence)
  {
    this.mQueryHint = paramCharSequence;
    updateQueryHint();
  }
  
  public void setQueryRefinementEnabled(boolean paramBoolean)
  {
    this.mQueryRefinement = paramBoolean;
    if ((this.mSuggestionsAdapter instanceof SuggestionsAdapter))
    {
      SuggestionsAdapter localSuggestionsAdapter = (SuggestionsAdapter)this.mSuggestionsAdapter;
      int i;
      if (!paramBoolean) {
        i = 1;
      } else {
        i = 2;
      }
      localSuggestionsAdapter.setQueryRefinement(i);
    }
  }
  
  public void setSearchableInfo(SearchableInfo paramSearchableInfo)
  {
    this.mSearchable = paramSearchableInfo;
    if (this.mSearchable != null)
    {
      if (IS_AT_LEAST_FROYO) {
        updateSearchAutoComplete();
      }
      updateQueryHint();
    }
    boolean bool;
    if ((!IS_AT_LEAST_FROYO) || (!hasVoiceSearch())) {
      bool = false;
    } else {
      bool = true;
    }
    this.mVoiceButtonEnabled = bool;
    if (this.mVoiceButtonEnabled) {
      this.mQueryTextView.setPrivateImeOptions("nm");
    }
    updateViewsVisibility(isIconified());
  }
  
  public void setSubmitButtonEnabled(boolean paramBoolean)
  {
    this.mSubmitButtonEnabled = paramBoolean;
    updateViewsVisibility(isIconified());
  }
  
  public void setSuggestionsAdapter(CursorAdapter paramCursorAdapter)
  {
    this.mSuggestionsAdapter = paramCursorAdapter;
    this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
  }
  
  private static class AutoCompleteTextViewReflector
  {
    private Method doAfterTextChanged;
    private Method doBeforeTextChanged;
    private Method ensureImeVisible;
    private Method showSoftInputUnchecked;
    
    AutoCompleteTextViewReflector()
    {
      try
      {
        this.doBeforeTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doBeforeTextChanged", new Class[0]);
        this.doBeforeTextChanged.setAccessible(true);
        try
        {
          this.doAfterTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doAfterTextChanged", new Class[0]);
          this.doAfterTextChanged.setAccessible(true);
          try
          {
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = Boolean.TYPE;
            this.ensureImeVisible = AutoCompleteTextView.class.getMethod("ensureImeVisible", arrayOfClass);
            this.ensureImeVisible.setAccessible(true);
            try
            {
              arrayOfClass = new Class[2];
              arrayOfClass[0] = Integer.TYPE;
              arrayOfClass[1] = ResultReceiver.class;
              this.showSoftInputUnchecked = InputMethodManager.class.getMethod("showSoftInputUnchecked", arrayOfClass);
              this.showSoftInputUnchecked.setAccessible(true);
              return;
            }
            catch (NoSuchMethodException localNoSuchMethodException1)
            {
              for (;;) {}
            }
          }
          catch (NoSuchMethodException localNoSuchMethodException2)
          {
            for (;;) {}
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException3)
        {
          for (;;) {}
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException4)
      {
        for (;;) {}
      }
    }
    
    void doAfterTextChanged(AutoCompleteTextView paramAutoCompleteTextView)
    {
      if (this.doAfterTextChanged != null) {}
      try
      {
        this.doAfterTextChanged.invoke(paramAutoCompleteTextView, new Object[0]);
        return;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    void doBeforeTextChanged(AutoCompleteTextView paramAutoCompleteTextView)
    {
      if (this.doBeforeTextChanged != null) {}
      try
      {
        this.doBeforeTextChanged.invoke(paramAutoCompleteTextView, new Object[0]);
        return;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    void ensureImeVisible(AutoCompleteTextView paramAutoCompleteTextView, boolean paramBoolean)
    {
      if (this.ensureImeVisible != null) {}
      try
      {
        Method localMethod = this.ensureImeVisible;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Boolean.valueOf(paramBoolean);
        localMethod.invoke(paramAutoCompleteTextView, arrayOfObject);
        return;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    void showSoftInputUnchecked(InputMethodManager paramInputMethodManager, View paramView, int paramInt)
    {
      if (this.showSoftInputUnchecked != null) {}
      for (;;)
      {
        try
        {
          Method localMethod = this.showSoftInputUnchecked;
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = Integer.valueOf(paramInt);
          arrayOfObject[1] = null;
          localMethod.invoke(paramInputMethodManager, arrayOfObject);
          return;
        }
        catch (Exception localException) {}
        paramInputMethodManager.showSoftInput(paramView, paramInt);
      }
    }
  }
  
  public static class SearchAutoComplete
    extends AutoCompleteTextView
  {
    private final int[] POPUP_WINDOW_ATTRS;
    private SearchView mSearchView;
    private int mThreshold;
    private final TintManager mTintManager;
    
    public SearchAutoComplete(Context paramContext)
    {
      this(paramContext, null);
    }
    
    public SearchAutoComplete(Context paramContext, AttributeSet paramAttributeSet)
    {
      this(paramContext, paramAttributeSet, 16842859);
    }
    
    public SearchAutoComplete(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
      Object localObject = new int[1];
      localObject[0] = 16843126;
      this.POPUP_WINDOW_ATTRS = ((int[])localObject);
      this.mThreshold = getThreshold();
      localObject = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, this.POPUP_WINDOW_ATTRS, paramInt, 0);
      if (((TintTypedArray)localObject).hasValue(0)) {
        setDropDownBackgroundDrawable(((TintTypedArray)localObject).getDrawable(0));
      }
      ((TintTypedArray)localObject).recycle();
      this.mTintManager = ((TintTypedArray)localObject).getTintManager();
    }
    
    private boolean isEmpty()
    {
      boolean bool;
      if (TextUtils.getTrimmedLength(getText()) != 0) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean enoughToFilter()
    {
      boolean bool;
      if ((this.mThreshold > 0) && (!super.enoughToFilter())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
    {
      super.onFocusChanged(paramBoolean, paramInt, paramRect);
      this.mSearchView.onTextFocusChanged();
    }
    
    public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
    {
      int i = 1;
      if (paramInt == 4)
      {
        if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0)) {
          break label88;
        }
        if (paramKeyEvent.getAction() == i)
        {
          localDispatcherState = getKeyDispatcherState();
          if (localDispatcherState != null) {
            localDispatcherState.handleUpEvent(paramKeyEvent);
          }
          if ((paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled())) {
            break label70;
          }
        }
      }
      boolean bool = super.onKeyPreIme(paramInt, paramKeyEvent);
      return bool;
      label70:
      this.mSearchView.clearFocus();
      this.mSearchView.setImeVisibility(false);
      return bool;
      label88:
      KeyEvent.DispatcherState localDispatcherState = getKeyDispatcherState();
      if (localDispatcherState != null) {
        localDispatcherState.startTracking(paramKeyEvent, this);
      }
      return bool;
    }
    
    public void onWindowFocusChanged(boolean paramBoolean)
    {
      super.onWindowFocusChanged(paramBoolean);
      if ((paramBoolean) && (this.mSearchView.hasFocus()) && (getVisibility() == 0))
      {
        ((InputMethodManager)getContext().getSystemService("input_method")).showSoftInput(this, 0);
        if (SearchView.isLandscapeMode(getContext())) {
          SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this, true);
        }
      }
    }
    
    public void performCompletion() {}
    
    protected void replaceText(CharSequence paramCharSequence) {}
    
    public void setDropDownBackgroundResource(int paramInt)
    {
      setDropDownBackgroundDrawable(this.mTintManager.getDrawable(paramInt));
    }
    
    void setSearchView(SearchView paramSearchView)
    {
      this.mSearchView = paramSearchView;
    }
    
    public void setThreshold(int paramInt)
    {
      super.setThreshold(paramInt);
      this.mThreshold = paramInt;
    }
  }
  
  public static abstract interface OnSuggestionListener
  {
    public abstract boolean onSuggestionClick(int paramInt);
    
    public abstract boolean onSuggestionSelect(int paramInt);
  }
  
  public static abstract interface OnCloseListener
  {
    public abstract boolean onClose();
  }
  
  public static abstract interface OnQueryTextListener
  {
    public abstract boolean onQueryTextChange(String paramString);
    
    public abstract boolean onQueryTextSubmit(String paramString);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\widget\SearchView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */