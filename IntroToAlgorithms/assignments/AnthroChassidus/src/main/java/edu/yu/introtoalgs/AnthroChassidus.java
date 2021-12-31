package edu.yu.introtoalgs;

import java.util.HashMap;

/** Defines and implements the AnthroChassidus API per the requirements
 * documentation.
 *
 * @author Avraham Leff
 */

public class AnthroChassidus {

  /** Constructor.  When the constructor completes, ALL necessary processing
   * for subsequent API calls have been made such that any subsequent call will
   * incur an O(1) cost.
   *
   * @param n the size of the underlying population that we're investigating:
   * need not correspond in any way to the number of people actually
   * interviewed (i.e., the number of elements in the "a" and "b" parameters).
   * Must be greater than 2.
   * @param a interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1
   * @param b interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1.  Pairs of a_i and b_i entries represent the fact
   * that the corresponding people follow the same Chassidus (without
   * specifying what that Chassidus is).
   */

  private WeightedQuickUnion qu;
  private HashMap<Integer, Integer> map = new HashMap<>();
  private int chassidus;

  public AnthroChassidus(final int n, final int[] a, final int[] b) {
    qu = new WeightedQuickUnion(n);
    chassidus = n;
    for(int i = 0; i < a.length; i++){
      if(!qu.connected(a[i], b[i])) {
        qu.union(a[i], b[i]);
        chassidus--;
      }
    }

    for(int i = 0; i < qu.id.length; i++){
      map.put(i, qu.size(i));
    }

  }

  /** Return the tightest value less than or equal to "n" specifying how many
   * types of Chassidus exist in the population: this answer is inferred from
   * the interviewers data supplied to the constructor
   *
   * @return tightest possible lower bound on the number of Chassidus in the
   * underlying population.
   */
  public int getLowerBoundOnChassidusTypes() {
    return chassidus;
  }

  /** Return the number of interviewed people who follow the same Chassidus as
   * this person.
   *
   //* @param id= uniquely identifies the interviewed person
   * @return the number of interviewed people who follow the same Chassidus as
   * this person.
   */
  public int nShareSameChassidus(final int id) {
    return map.get(id);
  }
  
  private class WeightedQuickUnion
  {
    private int[] id; // parent link (site indexed)
    private int[] size; // size of component for roots (site indexed)
    private int count; // number of components

    private WeightedQuickUnion(int N)
    {
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
      size = new int[N];
      for (int i = 0; i < N; i++) size[i] = 1;
    }

    protected int count()
    { return count; }

    protected boolean connected(int p, int q)
    { return find(p) == find(q); }

    protected int find(int p)
    { // Follow links to find a root.
      while (p != id[p]) p = id[p];
      return p;
    }

    protected int size(int n){
      return size[find(n)];
    }

    protected boolean union(int p, int q)
    {
      int i = find(p);
      int j = find(q);
      if (i == j) return false;
      // Make smaller root point to larger one.
      if (size[i] < size[j]) { id[i] = j; size[j] += size[i]; }
      else { id[j] = i; size[i] += size[j]; }
      count--;
      return true;
    }
  }


} // class
