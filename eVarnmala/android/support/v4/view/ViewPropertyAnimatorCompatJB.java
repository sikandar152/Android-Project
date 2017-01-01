package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;

class ViewPropertyAnimatorCompatJB
{
  public static void setListener(final View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
  {
    if (paramViewPropertyAnimatorListener == null) {
      paramView.animate().setListener(null);
    } else {
      paramView.animate().setListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          ViewPropertyAnimatorCompatJB.this.onAnimationCancel(paramView);
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ViewPropertyAnimatorCompatJB.this.onAnimationEnd(paramView);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ViewPropertyAnimatorCompatJB.this.onAnimationStart(paramView);
        }
      });
    }
  }
  
  public static void withEndAction(View paramView, Runnable paramRunnable)
  {
    paramView.animate().withEndAction(paramRunnable);
  }
  
  public static void withLayer(View paramView)
  {
    paramView.animate().withLayer();
  }
  
  public static void withStartAction(View paramView, Runnable paramRunnable)
  {
    paramView.animate().withStartAction(paramRunnable);
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\view\ViewPropertyAnimatorCompatJB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */