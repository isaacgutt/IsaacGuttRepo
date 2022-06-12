package edu.yu.da;

import java.util.*;

/** Implements the WaitNoMoreI API.
 *
 * Students MAY NOT change the provided constructor signature!
 *
 * @author Avraham Leff
 */

public class WaitNoMore implements WaitNoMoreI {

  /**
   * No-op constructor
   */
  public WaitNoMore() {
    // no-op, students may change the implementation
  }

  /**
   * Returns the minimum total waiting time required over a legal schedule in
   * which all of the specified jobs are performed and the schedule minimized
   * the total WEIGHTED waiting time.  A legal schedule is one in which jobs
   * execute sequentially, no job beginning execution until the previous job
   * has completed.  The method computes a schedule that minimizes the total
   * WEIGHTED waiting time, and returns (only) the total waiting time.
   * <p>
   * The input parameters only specify a default schedule: determining the
   * minimum total WEIGHTED waiting time may require reordering the jobs.
   *
   * @param durations the ith array element specifies the duration of the ith
   *                  job.  The method's semantics are undefined if any value is less than or
   *                  equal to 0.  Method semantics are undefined if not the same length as the
   *                  weights parameter.  Client maintains ownership of the input parameter.
   * @param weights   the ith array element by specifying the importance (weight) of
   *                  the ith job for the company.  The method's semantics are undefined if any
   *                  value is less than or equal to 0.  Method semantics are undefined if not
   *                  the same length as the durations parameter.  Client maintains ownership of
   *                  the input parameter.
   * @return the minimum total waiting time associated with a legal schedule
   * that minimizes the total WEIGHTED waiting of the input jobs.
   */
  @Override
  public int minTotalWaitingTime(final int[] durations, final int[] weights) {
    List<WeightedJob> list = new ArrayList<>();
    for(int i = 0; i < durations.length; i++){
      list.add(new WeightedJob(durations[i], weights[i]));
    }
    Collections.sort(list);

    int minimumWait = 0;
    int currentWaiting = 0;

    System.out.println(list);
    for(int i = 0; i < durations.length; i++) {
      WeightedJob job = list.get(i);
      //minimumWait += currentWaiting * job.getWeight();
      minimumWait += currentWaiting;
      currentWaiting += job.getDuration();
    }
    return minimumWait;
  }

  /*
  i will make  a weighted job class, loop through both arrays and fill a list of weighted jobs,
  in the class I will create a compare to method where the lowest weights take priority, and if weights are equal
  the highest durations take priority, (priority meaning greatest and I want lowest first), then sort and run through
  list and done.
   */

  private static class WeightedJob implements Comparable<WeightedJob>  {
    int weight;
    int duration;

    public WeightedJob(int duration, int weight){
      this.duration = duration;
      this.weight = weight;

    }
    @Override
    public int compareTo(WeightedJob o) {
      double self = (double)this.duration/this.weight;
      double os = (double)o.duration/o.weight;
      if(os == self) return Integer.compare(this.duration, o.duration);

      return Double.compare((double)this.duration/this.weight, (double)o.duration/o.weight);
      // }
    }

    public int getWeight(){
      return weight;
    }

    public int getDuration(){
      return duration;
    }

    @Override
    public String toString(){
      return "(duration: " +duration + " , weight: " + weight + ", ratio " + (double)this.duration/this.weight + ")";
    }

  }
}