package edu.yu.da;

/** Implements the ShortestCycleBase API.
 *
 */

import java.util.*;

/**
 * This class uses Sedgewick's DijkstraUndirectedSP.java https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DijkstraUndirectedSP.java.html
 * and IndexMinPQ.java https://algs4.cs.princeton.edu/24pq/IndexMinPQ.java.html
 */

public class ShortestCycle extends ShortestCycleBase {

  /**
   * Constructor
   *
   * @param edges List of edges that, in total, represent a weighted undirected
   * graph.  The client maintains ownership of the List: the implementation may
   * not modify this input parameter.  The client guarantees that the List is
   * not null, and doesn't contains any null edges.
   * @param e One of the graph's edges, the "edge of interest" since we want to
   * determine the shortest cycle containing this edge.
   */
  public Edge edge;
  public Graph graph;
  List<Edge> edges = new ArrayList<>();
  //public Cycle cycle;

  public ShortestCycle(final List<Edge> edges, final Edge e) {
    // base class does nothing, but let's do it right
    super(edges, e);
    this.edges = edges;
    graph = new Graph(edges);
    graph.removeEdge(e);
    this.edge = e;
  } // constructor

  /**
   * Finds the shortest cycle in the graph with respect to the specified edge
   * as detailed by the requirements document.
   *
   * @return List of edges representing the shortest cyle containing the "edge
   * of interest".  The List can begin with any edge from the cycle, but must
   * be a sequence that begins and ends at the same vertex and contain the
   * "edge of interest".
   */
  @Override
  public List<Edge> doIt() {
  DijkstraSP sp;
    List<Edge> list = new ArrayList<>();
    sp = new DijkstraSP(graph, edge.v(), edges);
    list = sp.pathTo(edge.w());
    return list;
    //return cycle.getWeight() > cycle2.getWeight()? cycle2.list : cycle.list;
  }


  public class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every
     * other vertex in the edge-weighted graph {@code G}.
     *
     * @param G the edge-weighted digraph
     * @param s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DijkstraSP(Graph G, int s, List<Edge> edges) {
      for (Edge e : edges) {
        if (e.weight() < 0)
          throw new IllegalArgumentException("edge " + e + " has negative weight");
      }

      distTo = new double[G.V()+1];
      edgeTo = new Edge[G.V()+1];

      validateVertex(s);

      for (int v = 1; v <= G.V(); v++)
        distTo[v] = Double.POSITIVE_INFINITY;
      distTo[s] = 0.0;

      // relax vertices in order of distance from s
      pq = new IndexMinPQ<Double>(G.V());
      pq.insert(s, distTo[s]);
      while (!pq.isEmpty()) {
        int v = pq.delMin();
        for (Edge e : G.adj(v))
          relax(e, v);
      }

      // check optimality conditions
      //assert check(G, s);
    }

    private int other(Edge e, int a){
      return a == e.v()? e.w() : e.v();
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
      int w = other(e,v);
      if (distTo[w] > distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
        else pq.insert(w, distTo[w]);
      }
    }


    /**
     * Returns true if there is a path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param v the destination vertex
     * @return {@code true} if there is a path between the source vertex
     * {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
      validateVertex(v);
      return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path between the source vertex {@code s} and vertex {@code v}.
     *
     * @param v the destination vertex
     * @return a shortest path between the source vertex {@code s} and vertex {@code v};
     * {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public List<Edge> pathTo(int v) {
      validateVertex(v);
      if (!hasPathTo(v)) return null;
      List<Edge> path = new ArrayList<>();
      path.add(edge);
      int x = v;
      for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
        path.add(e);
        x = other(e,x);
      }
      return path;
    }


    // check optimality conditions:
    // (i) for all edges e = v-w:            distTo[w] <= distTo[v] + e.weight()
    // (ii) for all edge e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(Graph G, int s) {

      // check that edge weights are non-negative
      for (Edge e : edges) {
        if (e.weight() < 0) {
          System.err.println("negative edge weight detected");
          return false;
        }
      }

      // check that distTo[v] and edgeTo[v] are consistent
      if (distTo[s] != 0.0 || edgeTo[s] != null) {
        System.err.println("distTo[s] and edgeTo[s] inconsistent");
        return false;
      }
      for (int v = 0; v < G.V(); v++) {
        if (v == s) continue;
        if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
          System.err.println("distTo[] and edgeTo[] inconsistent");
          return false;
        }
      }

      // check that all edges e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
      for (int v = 0; v < G.V(); v++) {
        for (Edge e : G.adj(v)) {
          int w = other(e,v);
          if (distTo[v] + e.weight() < distTo[w]) {
            System.err.println("edge " + e + " not relaxed");
            return false;
          }
        }
      }

      // check that all edges e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
      for (int w = 0; w < G.V(); w++) {
        if (edgeTo[w] == null) continue;
        Edge e = edgeTo[w];
        if (w != e.v() && w != e.w()) return false;
        int v = other(e,w);
        if (distTo[v] + e.weight() != distTo[w]) {
          System.err.println("edge " + e + " on shortest path not tight");
          return false;
        }
      }
      return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
      int V = distTo.length;
      if (v < 0 || v > V)
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }
  }

  public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int maxN;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys;      // keys[i] = priority of i

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     *
     * @param maxN the keys on this priority queue are index from {@code 0}
     *             {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexMinPQ(int maxN) {
      if (maxN < 0) throw new IllegalArgumentException();
      this.maxN = maxN;
      n = 0;
      keys = (Key[]) new Comparable[maxN + 1];    // make this of length maxN??
      pq = new int[maxN + 1];
      qp = new int[maxN + 1];                   // make this of length maxN??
      for (int i = 0; i <= maxN; i++)
        qp[i] = -1;
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     * {@code false} otherwise
     */
    public boolean isEmpty() {
      return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(int i) {
      validateIndex(i);
      return qp[i] != -1;
    }

    /**
     * Associates key with index {@code i}.
     *
     * @param i   an index
     * @param key the key to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *                                  with index {@code i}
     */
    public void insert(int i, Key key) {
      validateIndex(i);
      if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
      n++;
      qp[i] = n;
      pq[n] = i;
      keys[i] = key;
      swim(n);
    }

    /**
     * Removes a minimum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMin() {
      if (n == 0) throw new NoSuchElementException("Priority queue underflow");
      int min = pq[1];
      exch(1, n--);
      sink(1);
      assert min == pq[n + 1];
      qp[min] = -1;        // delete
      keys[min] = null;    // to help with garbage collection
      pq[n + 1] = -1;        // not needed
      return min;
    }

    /**
     * Change the key associated with index {@code i} to the specified value.
     *
     * @param i   the index of the key to change
     * @param key change the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException   no key is associated with index {@code i}
     */
    public void changeKey(int i, Key key) {
      validateIndex(i);
      if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
      keys[i] = key;
      swim(qp[i]);
      sink(qp[i]);
    }

    /**
     * Change the key associated with index {@code i} to the specified value.
     *
     * @param i   the index of the key to change
     * @param key change the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @deprecated Replaced by {@code changeKey(int, Key)}.
     */
    @Deprecated
    public void change(int i, Key key) {
      changeKey(i, key);
    }

    /**
     * Decrease the key associated with index {@code i} to the specified value.
     *
     * @param i   the index of the key to decrease
     * @param key decrease the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code key >= keyOf(i)}
     * @throws NoSuchElementException   no key is associated with index {@code i}
     */
    public void decreaseKey(int i, Key key) {
      validateIndex(i);
      if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
      if (keys[i].compareTo(key) == 0)
        throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue");
      if (keys[i].compareTo(key) < 0)
        throw new IllegalArgumentException("Calling decreaseKey() with a key strictly greater than the key in the priority queue");
      keys[i] = key;
      swim(qp[i]);
    }


    // throw an IllegalArgumentException if i is an invalid index
    private void validateIndex(int i) {
      if (i < 0) throw new IllegalArgumentException("index is negative: " + i);
      if (i > maxN) throw new IllegalArgumentException("index >= capacity: " + i);
    }

    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private boolean greater(int i, int j) {
      return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
      int swap = pq[i];
      pq[i] = pq[j];
      pq[j] = swap;
      qp[pq[i]] = i;
      qp[pq[j]] = j;
    }


    /***************************************************************************
     * Heap helper functions.
     ***************************************************************************/
    private void swim(int k) {
      while (k > 1 && greater(k / 2, k)) {
        exch(k, k / 2);
        k = k / 2;
      }
    }

    private void sink(int k) {
      while (2 * k <= n) {
        int j = 2 * k;
        if (j < n && greater(j, j + 1)) j++;
        if (!greater(k, j)) break;
        exch(k, j);
        k = j;
      }
    }


    /***************************************************************************
     * Iterators.
     ***************************************************************************/

    /**
     * Returns an iterator that iterates over the keys on the
     * priority queue in ascending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Integer> iterator() {
      return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
      // create a new pq
      private IndexMinPQ<Key> copy;

      // add all elements to copy of heap
      // takes linear time since already in heap order so no keys move
      public HeapIterator() {
        copy = new IndexMinPQ<Key>(pq.length - 1);
        for (int i = 1; i <= n; i++)
          copy.insert(pq[i], keys[pq[i]]);
      }

      public boolean hasNext() {
        return !copy.isEmpty();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public Integer next() {
        if (!hasNext()) throw new NoSuchElementException();
        return copy.delMin();
      }
    }
  }

  /*public class Cycle{

    private List<Edge> list = new ArrayList<>();
    private double weight;

    public Cycle(Edge e){
      add(e);
    }

    public void add(Edge e){
      list.add(e);
      weight += e.weight();
    }
    public void addAll(List<Edge> edges){
      edges.add(0, );
    }

    public List<Edge> list(){
      return list;
    }

    public double getWeight(){
      return weight;
    }


  }
   */


}




