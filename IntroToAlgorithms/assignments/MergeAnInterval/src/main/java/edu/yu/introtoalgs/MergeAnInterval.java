package edu.yu.introtoalgs;

//import com.sun.source.tree.Tree;

import java.util.*;

/** Implements the "Add an Interval To a Set of Intervals" semantics defined in
 * the requirements document.
 * 
 * @author Avraham Leff 
 */

public class MergeAnInterval {

  /** An immutable class, holds a left and right integer-valued pair that
   * defines a closed interval
   *
   * IMPORTANT: students may not modify the semantics of the "left", "right"
   * instance variables, nor may they use any other constructor signature.
   * Students may (are encouraged to) add any other methods that they choose,
   * bearing in mind that my tests will ONLY DIRECTLY INVOKE the constructor
   * and the "merge" method.
   */
  public static class Interval implements Comparable<Interval>{
    public final int left;
    public final int right;

    /** Constructor
     * 
     * @param// left the left endpoint of the interval, may be negative
     * @param// right the right endpoint of the interval, may be negative
     * @throws IllegalArgumentException if left is >= right
     */
    public Interval(int l, int r) {
      this.left = l;
      this.right = r;
    }

    @Override
    public int compareTo(Interval o) {
	if(o.equals(this)) return 0;

	if(o.left == this.left)
	  return this.right < o.right? -1 : 1;

	return this.left < o.left? -1 : 1;

    }

    @Override
    public boolean equals(Object o){
      if(!(o instanceof Interval))return false;
      Interval i = (Interval) o;
      return i.left == this.left && i.right == this.right;
    }



  } // Interval class

  /** Merges the new interval into an existing set of disjoint intervals.
   *
   * @param intervals an set of disjoint intervals (may be empty)
   * @param newInterval the interval to be added
   * @return a new set of disjoint intervals containing the original intervals
   * and the new interval, merging the new interval if necessary into existing
   * interval(s), to preseve the "disjointedness" property.
   * @throws IllegalArgumentException if either parameter is null
   */
  public static Set<Interval> merge(final Set<Interval> intervals, Interval newInterval)
  {
    List<Interval> list = new ArrayList<>(intervals);
    Collections.sort(list);

    Interval in;
    int addRight = -1;
    int addLeft = -1;
    int i;

    for(i = 0; i < list.size(); i++){
      in = list.get(i);
      //if right of the interval is greater than the left of the entry, then that's where the entry should go
      if(in.right >= newInterval.left) {
        //if the right of the entry is >= the left of the current interval, it should
        //go there, if not then the entry doesn't overlap, goes between two intervals
        if(newInterval.right >= in.left) {
          list.remove(i);
          addLeft = Integer.min(newInterval.left, in.left);
          addRight = Integer.max(newInterval.right, in.right);
        }else{
          //just put in the new interval and set addRight to return
          list.add(newInterval);
          addRight = -2;
        }
        break;
      }
    }
    if(addRight == -2)return new TreeSet<Interval>(list);
    if(addLeft == -1) return intervals;

    for(; i < list.size(); i++){
      in = list.get(i);
      //if the right of the entry is bigger than the current intervals left,
      //then the new interval connects with this one.
      if(in.left <= addRight) {
        addRight = Integer.max(addRight, in.right);
        list.remove(i);
        i--;
      }
      else
        break;
    }

    list.add(new Interval(addLeft, addRight));
    //Collections.sort(list);
    return new TreeSet<Interval>(list);
  }

}
