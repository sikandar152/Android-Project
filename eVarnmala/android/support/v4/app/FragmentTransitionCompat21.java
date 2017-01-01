package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class FragmentTransitionCompat21
{
  public static void addTargets(Object paramObject, ArrayList<View> paramArrayList)
  {
    Transition localTransition = (Transition)paramObject;
    int i = paramArrayList.size();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      localTransition.addTarget((View)paramArrayList.get(j));
    }
  }
  
  public static void addTransitionTargets(Object paramObject1, Object paramObject2, View paramView1, final ViewRetriever paramViewRetriever, View paramView2, EpicenterView paramEpicenterView, final Map<String, String> paramMap, final ArrayList<View> paramArrayList1, final Map<String, View> paramMap1, ArrayList<View> paramArrayList2)
  {
    if ((paramObject1 != null) || (paramObject2 != null))
    {
      final Transition localTransition = (Transition)paramObject1;
      if (localTransition != null) {
        localTransition.addTarget(paramView2);
      }
      if (paramObject2 != null) {
        addTargets((Transition)paramObject2, paramArrayList2);
      }
      if (paramViewRetriever != null) {
        paramView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            FragmentTransitionCompat21.this.getViewTreeObserver().removeOnPreDrawListener(this);
            View localView = paramViewRetriever.getView();
            Iterator localIterator;
            if (localView != null) {
              if (!paramMap.isEmpty())
              {
                FragmentTransitionCompat21.findNamedViews(paramMap1, localView);
                paramMap1.keySet().retainAll(paramMap.values());
                localIterator = paramMap.entrySet().iterator();
              }
            }
            for (;;)
            {
              if (!localIterator.hasNext())
              {
                if (localTransition != null)
                {
                  FragmentTransitionCompat21.captureTransitioningViews(paramArrayList1, localView);
                  paramArrayList1.removeAll(paramMap1.values());
                  FragmentTransitionCompat21.addTargets(localTransition, paramArrayList1);
                }
                return true;
              }
              Map.Entry localEntry = (Map.Entry)localIterator.next();
              Object localObject = (String)localEntry.getValue();
              localObject = (View)paramMap1.get(localObject);
              if (localObject != null) {
                ((View)localObject).setTransitionName((String)localEntry.getKey());
              }
            }
          }
        });
      }
      setSharedElementEpicenter(localTransition, paramEpicenterView);
    }
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject)
  {
    TransitionManager.beginDelayedTransition(paramViewGroup, (Transition)paramObject);
  }
  
  public static Object captureExitingViews(Object paramObject, View paramView, ArrayList<View> paramArrayList, Map<String, View> paramMap)
  {
    if (paramObject != null)
    {
      captureTransitioningViews(paramArrayList, paramView);
      if (paramMap != null) {
        paramArrayList.removeAll(paramMap.values());
      }
      if (!paramArrayList.isEmpty()) {
        addTargets((Transition)paramObject, paramArrayList);
      } else {
        paramObject = null;
      }
    }
    return paramObject;
  }
  
  private static void captureTransitioningViews(ArrayList<View> paramArrayList, View paramView)
  {
    if (paramView.getVisibility() == 0) {
      if (!(paramView instanceof ViewGroup))
      {
        paramArrayList.add(paramView);
      }
      else
      {
        ViewGroup localViewGroup = (ViewGroup)paramView;
        if (!localViewGroup.isTransitionGroup())
        {
          int j = localViewGroup.getChildCount();
          for (int i = 0; i < j; i++) {
            captureTransitioningViews(paramArrayList, localViewGroup.getChildAt(i));
          }
        }
        paramArrayList.add(localViewGroup);
      }
    }
  }
  
  public static void cleanupTransitions(View paramView1, final View paramView2, Object paramObject1, final ArrayList<View> paramArrayList1, Object paramObject2, final ArrayList<View> paramArrayList2, Object paramObject3, final ArrayList<View> paramArrayList3, Object paramObject4, final ArrayList<View> paramArrayList4, final Map<String, View> paramMap)
  {
    final Transition localTransition3 = (Transition)paramObject1;
    final Transition localTransition2 = (Transition)paramObject2;
    final Transition localTransition1 = (Transition)paramObject3;
    final Transition localTransition4 = (Transition)paramObject4;
    if (localTransition4 != null) {
      paramView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          FragmentTransitionCompat21.this.getViewTreeObserver().removeOnPreDrawListener(this);
          if (localTransition3 != null)
          {
            localTransition3.removeTarget(paramView2);
            FragmentTransitionCompat21.removeTargets(localTransition3, paramArrayList1);
          }
          if (localTransition2 != null) {
            FragmentTransitionCompat21.removeTargets(localTransition2, paramArrayList2);
          }
          if (localTransition1 != null) {
            FragmentTransitionCompat21.removeTargets(localTransition1, paramArrayList3);
          }
          Iterator localIterator = paramMap.entrySet().iterator();
          for (;;)
          {
            int j;
            if (!localIterator.hasNext())
            {
              j = paramArrayList4.size();
              for (int i = 0;; i++)
              {
                if (i >= j)
                {
                  localTransition4.excludeTarget(paramView2, false);
                  return true;
                }
                localTransition4.excludeTarget((View)paramArrayList4.get(i), false);
              }
            }
            Map.Entry localEntry = (Map.Entry)j.next();
            ((View)localEntry.getValue()).setTransitionName((String)localEntry.getKey());
          }
        }
      });
    }
  }
  
  public static Object cloneTransition(Object paramObject)
  {
    if (paramObject != null) {
      paramObject = ((Transition)paramObject).clone();
    }
    return paramObject;
  }
  
  public static void excludeTarget(Object paramObject, View paramView, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeTarget(paramView, paramBoolean);
  }
  
  public static void findNamedViews(Map<String, View> paramMap, View paramView)
  {
    ViewGroup localViewGroup;
    int j;
    if (paramView.getVisibility() == 0)
    {
      String str = paramView.getTransitionName();
      if (str != null) {
        paramMap.put(str, paramView);
      }
      if ((paramView instanceof ViewGroup))
      {
        localViewGroup = (ViewGroup)paramView;
        j = localViewGroup.getChildCount();
      }
    }
    for (int i = 0;; i++)
    {
      if (i >= j) {
        return;
      }
      findNamedViews(paramMap, localViewGroup.getChildAt(i));
    }
  }
  
  private static Rect getBoundsOnScreen(View paramView)
  {
    Rect localRect = new Rect();
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    localRect.set(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + paramView.getWidth(), arrayOfInt[1] + paramView.getHeight());
    return localRect;
  }
  
  public static String getTransitionName(View paramView)
  {
    return paramView.getTransitionName();
  }
  
  public static Object mergeTransitions(Object paramObject1, Object paramObject2, Object paramObject3, boolean paramBoolean)
  {
    boolean bool = true;
    Transition localTransition = (Transition)paramObject1;
    Object localObject2 = (Transition)paramObject2;
    Object localObject1 = (Transition)paramObject3;
    if ((localTransition != null) && (localObject2 != null)) {
      bool = paramBoolean;
    }
    Object localObject3;
    if (!bool)
    {
      localObject3 = null;
      if ((localObject2 == null) || (localTransition == null))
      {
        if (localObject2 == null)
        {
          if (localTransition != null) {
            localObject3 = localTransition;
          }
        }
        else {
          localObject3 = localObject2;
        }
      }
      else {
        localObject3 = new TransitionSet().addTransition((Transition)localObject2).addTransition(localTransition).setOrdering(1);
      }
      if (localObject1 == null)
      {
        localObject1 = localObject3;
      }
      else
      {
        localObject2 = new TransitionSet();
        if (localObject3 != null) {
          ((TransitionSet)localObject2).addTransition((Transition)localObject3);
        }
        ((TransitionSet)localObject2).addTransition((Transition)localObject1);
        localObject1 = localObject2;
      }
    }
    else
    {
      localObject3 = new TransitionSet();
      if (localTransition != null) {
        ((TransitionSet)localObject3).addTransition(localTransition);
      }
      if (localObject2 != null) {
        ((TransitionSet)localObject3).addTransition((Transition)localObject2);
      }
      if (localObject1 != null) {
        ((TransitionSet)localObject3).addTransition((Transition)localObject1);
      }
      localObject1 = localObject3;
    }
    return localObject1;
  }
  
  public static void removeTargets(Object paramObject, ArrayList<View> paramArrayList)
  {
    Transition localTransition = (Transition)paramObject;
    int i = paramArrayList.size();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      localTransition.removeTarget((View)paramArrayList.get(j));
    }
  }
  
  public static void setEpicenter(Object paramObject, View paramView)
  {
    ((Transition)paramObject).setEpicenterCallback(new Transition.EpicenterCallback()
    {
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        return FragmentTransitionCompat21.this;
      }
    });
  }
  
  private static void setSharedElementEpicenter(Transition paramTransition, EpicenterView paramEpicenterView)
  {
    if (paramTransition != null) {
      paramTransition.setEpicenterCallback(new Transition.EpicenterCallback()
      {
        private Rect mEpicenter;
        
        public Rect onGetEpicenter(Transition paramAnonymousTransition)
        {
          if ((this.mEpicenter == null) && (FragmentTransitionCompat21.this.epicenter != null)) {
            this.mEpicenter = FragmentTransitionCompat21.getBoundsOnScreen(FragmentTransitionCompat21.this.epicenter);
          }
          return this.mEpicenter;
        }
      });
    }
  }
  
  public static class EpicenterView
  {
    public View epicenter;
  }
  
  public static abstract interface ViewRetriever
  {
    public abstract View getView();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\app\FragmentTransitionCompat21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */