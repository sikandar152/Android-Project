package android.support.v4.widget;

import android.view.View;
import android.widget.ListView;

public class ListViewAutoScrollHelper
  extends AutoScrollHelper
{
  private final ListView mTarget;
  
  public ListViewAutoScrollHelper(ListView paramListView)
  {
    super(paramListView);
    this.mTarget = paramListView;
  }
  
  public boolean canTargetScrollHorizontally(int paramInt)
  {
    return false;
  }
  
  public boolean canTargetScrollVertically(int paramInt)
  {
    boolean bool = false;
    ListView localListView = this.mTarget;
    int i = localListView.getCount();
    if (i != 0)
    {
      int j = localListView.getChildCount();
      int k = localListView.getFirstVisiblePosition();
      int m = k + j;
      if (paramInt <= 0 ? (paramInt >= 0) || ((k <= 0) && (localListView.getChildAt(0).getTop() >= 0)) : (m < i) || (localListView.getChildAt(j - 1).getBottom() > localListView.getHeight())) {
        bool = true;
      }
    }
    return bool;
  }
  
  public void scrollTargetBy(int paramInt1, int paramInt2)
  {
    ListView localListView = this.mTarget;
    int i = localListView.getFirstVisiblePosition();
    if (i != -1)
    {
      View localView = localListView.getChildAt(0);
      if (localView != null) {
        localListView.setSelectionFromTop(i, localView.getTop() - paramInt2);
      }
    }
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\widget\ListViewAutoScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */