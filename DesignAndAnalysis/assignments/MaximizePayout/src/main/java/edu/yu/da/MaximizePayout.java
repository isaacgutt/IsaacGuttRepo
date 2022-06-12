package edu.yu.da;

/** Implements the MaximizePayoutI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;

public class MaximizePayout implements MaximizePayoutI {

  /** No-op constructor
   */
  public MaximizePayout() {
    // no-op, students may change the implementation
  }

  @Override
  public long max(final List<Long> A, final List<Long> B) {
    if(A.isEmpty() || B.isEmpty() || A == null || B == null || A.size() != B.size()) throw new IllegalArgumentException();
    List<Long> b = new ArrayList<>(B);
    List<Long> a = new ArrayList<>(A);
    a.sort(new greatestFirst());
    b.sort(new greatestFirst());
    long end = 1L;

    for(int i = 0; i < A.size(); i++){
      long pow = (long) Math.pow(a.get(i),b.get(i));
      end *= pow;
    }
    return end;
  }

  class greatestFirst implements Comparator<Long>{

    @Override
    public int compare(Long o1, Long o2) {
      return Long.compare(o2,o1);
    }
  }

} // MaximizePayout
