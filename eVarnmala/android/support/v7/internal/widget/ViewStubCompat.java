package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.lang.ref.WeakReference;

public final class ViewStubCompat
  extends View
{
  private OnInflateListener mInflateListener;
  private int mInflatedId;
  private WeakReference<View> mInflatedViewRef;
  private LayoutInflater mInflater;
  private int mLayoutResource = 0;
  
  public ViewStubCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ViewStubCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewStubCompat, paramInt, 0);
    this.mInflatedId = localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_inflatedId, -1);
    this.mLayoutResource = localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_layout, 0);
    setId(localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_id, -1));
    localTypedArray.recycle();
    setVisibility(8);
    setWillNotDraw(true);
  }
  
  protected void dispatchDraw(Canvas paramCanvas) {}
  
  public void draw(Canvas paramCanvas) {}
  
  public int getInflatedId()
  {
    return this.mInflatedId;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return this.mInflater;
  }
  
  public int getLayoutResource()
  {
    return this.mLayoutResource;
  }
  
  public View inflate()
  {
    Object localObject1 = getParent();
    if ((localObject1 == null) || (!(localObject1 instanceof ViewGroup))) {
      throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
    }
    if (this.mLayoutResource == 0) {
      throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
    }
    localObject1 = (ViewGroup)localObject1;
    if (this.mInflater == null) {
      localObject2 = LayoutInflater.from(getContext());
    } else {
      localObject2 = this.mInflater;
    }
    View localView = ((LayoutInflater)localObject2).inflate(this.mLayoutResource, (ViewGroup)localObject1, false);
    if (this.mInflatedId != -1) {
      localView.setId(this.mInflatedId);
    }
    int i = ((ViewGroup)localObject1).indexOfChild(this);
    ((ViewGroup)localObject1).removeViewInLayout(this);
    Object localObject2 = getLayoutParams();
    if (localObject2 == null) {
      ((ViewGroup)localObject1).addView(localView, i);
    } else {
      ((ViewGroup)localObject1).addView(localView, i, (ViewGroup.LayoutParams)localObject2);
    }
    this.mInflatedViewRef = new WeakReference(localView);
    if (this.mInflateListener != null) {
      this.mInflateListener.onInflate(this, localView);
    }
    return localView;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(0, 0);
  }
  
  public void setInflatedId(int paramInt)
  {
    this.mInflatedId = paramInt;
  }
  
  public void setLayoutInflater(LayoutInflater paramLayoutInflater)
  {
    this.mInflater = paramLayoutInflater;
  }
  
  public void setLayoutResource(int paramInt)
  {
    this.mLayoutResource = paramInt;
  }
  
  public void setOnInflateListener(OnInflateListener paramOnInflateListener)
  {
    this.mInflateListener = paramOnInflateListener;
  }
  
  public void setVisibility(int paramInt)
  {
    if (this.mInflatedViewRef == null)
    {
      super.setVisibility(paramInt);
      if ((paramInt == 0) || (paramInt == 4)) {
        inflate();
      }
    }
    else
    {
      View localView = (View)this.mInflatedViewRef.get();
      if (localView == null) {
        throw new IllegalStateException("setVisibility called on un-referenced view");
      }
      localView.setVisibility(paramInt);
    }
  }
  
  public static abstract interface OnInflateListener
  {
    public abstract void onInflate(ViewStubCompat paramViewStubCompat, View paramView);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v7\internal\widget\ViewStubCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */