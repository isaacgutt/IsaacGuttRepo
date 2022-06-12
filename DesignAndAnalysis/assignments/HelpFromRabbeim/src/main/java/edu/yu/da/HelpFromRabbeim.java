package edu.yu.da;

/** Implements the HelpFromRabbeimI interface.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;

import static edu.yu.da.HelpFromRabbeimI.HelpTopics.*;
import static edu.yu.da.HelpFromRabbeimI.Rebbe;

public class HelpFromRabbeim implements HelpFromRabbeimI {

  //HashMap<HelpTopics, Integer> topicsID = new HashMap<>();
  Map<FlowEdge, RebbeToTopic> redges;

  //to remove rebbes once they're put in map
  HashSet<Integer> ids = new HashSet<>();
  int topicNum = 0;

  /** No-op constructor
   */
  public HelpFromRabbeim() {

    // no-op, students may change the implementation
  }


  /* @param rabbeim List of rabbeim (and their "skill sets") available for that
   * day.  Client maintains ownership of this parameter.
   * @param requestedHelp how many requests per given topic.  Client maintains
   * ownership of this parameter.
   * @return a schedule that satisfies the specified constraints as a map from
   * rebbe (as uniquely specified by an integer) to the topic which he going to
   * be helping with.  If the schedule doesn't require a rebbe to help with
          * that day's requests, map that rebbe's id to null.  If no schedule can be
   * created to meet the constraints, return Collections.emptyMap().
          */
  @Override
  public Map<Integer, HelpTopics>
    scheduleIt(List<Rebbe> rabbeim,
               Map<HelpTopics, Integer> requestedHelp)
  {
    Map<Integer, HelpTopics> solution = new HashMap<>();
      FlowNetwork flowNetwork = createGraph(rabbeim, requestedHelp);
      FordFulkerson ff = new FordFulkerson(flowNetwork, 0, flowNetwork.getV()-1);

      //no solution
      if(ff.value < topicNum){
        return Collections.emptyMap();
      }

    for(FlowEdge edge : redges.keySet()){
      if(edge.flow > 0){
        RebbeToTopic rtt = redges.get(edge);
        solution.put(rtt.RebbeID(), rtt.topic());
        ids.remove(rtt.RebbeID());
      }
    }
    for(int i : ids)solution.put(i, null);
    //System.out.println(flowNetwork);
    return solution;
  }

  public FlowNetwork createGraph(List<Rebbe> rabbeim,
                                 Map<HelpTopics, Integer> requestedHelp){
    this.redges = new HashMap<>();
    Map<HelpTopics, Integer> topicId = new HashMap<>();
    int topicStart = rabbeim.size() + 1;
    FlowNetwork flowNetwork = new FlowNetwork(rabbeim.size() + requestedHelp.size() + 2);


    //create graph
    for(int i = 0; i < rabbeim.size(); i++){
      //will create edges from sink to all rabbeim
      FlowEdge edge = new FlowEdge(0, i+1, 1);
      flowNetwork.addEdge(edge);

      //create edges from rebbe to subject, and in there put a map with edges to object with both rebbe ID and subject.
      for(HelpTopics topic : rabbeim.get(i)._helpTopics){
        if(requestedHelp.containsKey(topic)) {
          if(!topicId.containsKey(topic)){
            topicNum += requestedHelp.get(topic);
            topicId.put(topic, topicStart++);

          }
          FlowEdge flowEdge = new FlowEdge(i + 1, topicId.get(topic), requestedHelp.get(topic));
          flowNetwork.addEdge(flowEdge);
          redges.put(flowEdge, new RebbeToTopic(rabbeim.get(i)._id, topic));

        }
      }
      //Add all rebbeim to the list
      ids.add(rabbeim.get(i)._id);
    }

    //System.out.println(flowNetwork);
    //create the edges from subject to sink
    for(HelpTopics topic : requestedHelp.keySet()){
      FlowEdge edge = new FlowEdge(topicId.get(topic), flowNetwork.getV()-1, requestedHelp.get(topic));
      flowNetwork.addEdge(edge);
    }
    //System.out.println(flowNetwork);
    return flowNetwork;
  }

  class RebbeToTopic{
    int id;
    HelpTopics topic;
    public RebbeToTopic(int id, HelpTopics topic){
      this.id = id;
      this.topic = topic;
    }

    public int RebbeID(){
      return id;
    }

    public HelpTopics topic(){
      return topic;
    }

    @Override
    public String toString(){
      return id + " " + topic;
    }
  }

  public class FlowEdge
  {
    private final int v; // edge source
    private final int w; // edge target
    private final double capacity; // capacity
    private double flow; // flow
    public FlowEdge(int v, int w, double capacity)
    {
      this.v = v;
      this.w = w;
      this.capacity = capacity;
      this.flow = 0.0;
    }
    public int from() { return v; }
    public int to() { return w; }
    public double capacity() { return capacity; }
    public double flow() { return flow; }
    public int other(int vertex){
      if (vertex == v) return w;
      else if (vertex == w) return v;
      //I guess
      else return Integer.MIN_VALUE;
    }
    // same as for Edge
    public double residualCapacityTo(int vertex)
    {
      if (vertex == v) return flow;
      else if (vertex == w) return capacity - flow;
      else throw new RuntimeException("Inconsistent edge");
    }
    public void addResidualFlowTo(int vertex, double delta)
    {
      if (vertex == v) flow -= delta;
      else if (vertex == w) flow += delta;
      else throw new RuntimeException("Inconsistent edge");
    }
    public String toString()
    { return String.format("%d->%d %.2f %.2f", v, w, capacity, flow); }

    public int hashCode(){
      return Objects.hash(v,w);
    }

  }

  public static class FordFulkerson
  {
    private static FlowNetwork gg;
    private boolean[] marked; // Is s->v path in residual graph?
    private FlowEdge[] edgeTo; // last edge on shortest s->v path
    private double value;
    // current value of maxflow
    public FordFulkerson(FlowNetwork G, int s, int t)
    { // Find maxflow in flow network G from s to t.
      gg = G;
      while (hasAugmentingPath(G, s, t))
      { // While there exists an augmenting path, use it.
        // Compute bottleneck capacity.
        double bottle = Double.POSITIVE_INFINITY;
        for (int v = t; v != s; v = edgeTo[v].other(v))
          bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
        // Augment flow.
        for (int v = t; v != s; v = edgeTo[v].other(v)) {
          edgeTo[v].addResidualFlowTo(v, bottle);
        }
        value += bottle;
      }
    }
    public double value() { return value; }
    public boolean inCut(int v) { return marked[v]; }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t)
    {
      marked = new boolean[G.getV()]; // Is path to this vertex known?
      edgeTo = new FlowEdge[G.getV()]; // last edge on path
      Queue<Integer> q = new ArrayDeque<>();
      marked[s] = true;
      // Mark the source
      q.add(s); // and put it on the queue.
      while (!q.isEmpty())
      {
        int v = q.remove();
        for (FlowEdge e : G.adj(v))
        {
          int w = e.other(v);
          if (e.residualCapacityTo(w) > 0 && !marked[w])
          { // For every edge to an unmarked vertex (in residual)
            edgeTo[w] = e; // Save the last edge on a path.
            marked[w] = true; // Mark w because a path is known
            q.add(w); // and add it to the queue.
          }
        }
      }
      return marked[t];
    }

    /*public void stringing()
    {
      FlowNetwork G = gg;
      int s = 0, t = G.getV() - 1;
      FordFulkerson maxflow = new FordFulkerson(G, s, t);
      System.out.println("Max flow from " + s + " to " + t);
      for (int v = 0; v < G.getV(); v++)
        for (FlowEdge e : G.adj(v))
          if ((v == e.from()) && e.flow() > 0)
            System.out.println(" " + e);
      System.out.println("Max flow value = " + maxflow.value());
    }*/
  }

} // HelpFromRabbeim
