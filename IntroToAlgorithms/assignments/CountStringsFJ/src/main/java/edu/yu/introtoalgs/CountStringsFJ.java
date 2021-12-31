package edu.yu.introtoalgs;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/** Implements the CountStringsFJ semantics specified in the requirements
 * document.
 *
 * @author Avraham Leff
 */

public class CountStringsFJ {

  int strings = 0;
  String str = "";
  int count = 0;

  /**
   * Constructor.
   *
   * @param arr       the array to process, can't be null or empty
   * @param str       the string to match, can't be null, may be empty
   * @param threshold when the length of arr is less than threshold, processing
   *                  must be sequential; otherwise, processing must use a fork/join, recursive
   *                  divide-and-conquer strategy.  The parameter must be greater than 0.
   *                  <p>
   *                  IMPORTANT: Students must use this constructor, they MAY NOT add another
   *                  constructor.
   */
  public CountStringsFJ(final String[] arr, final String str, final int threshold) {
    if(arr.length ==  0|| str == null || threshold <= 0)
      throw new IllegalArgumentException();


    ForkJoinPool fork = new ForkJoinPool();
    this.str = str;
    int length = arr.length;

    if (length < threshold) {
      noThreadSequential(arr, str);
      return;
    }

    ForkJoinTask<Integer> task = new ForkJoinSum(arr, 0, length, threshold);
    strings = fork.invoke(task);
    fork.shutdown();

  }

  private void noThreadSequential(final String[] arr, final String str) {
    for (String s : arr) {
      if (s.equals(str))
        strings++;
    }
  }

  private int sequential(final String[] arr, final String str, int low, int high){
    count++;
    int amount = 0;
    for(; low < high; low++){
      if (arr[low].equals(str))
        amount++;
    }
    count++;
   // System.out.println(count + "count");
    return amount;
  }


  /**
   * Returns the number of elements in arr that ".equal" the "str" parameter
   *
   * @return Using a strategy dictated by the relative values of threshold and
   * the size of arr, returns the number of times that str appears in arr
   */
  public int doIt() {
    return this.strings;
  }

  public class ForkJoinSum extends RecursiveTask<Integer>{
    String[] array;
    int low;
    int high;
    int threshold;

    ForkJoinSum(String[] array, int low, int high, int threshold) {
      // fixme No error checking !
      this.low = low;
      this.high = high;
      this.array = array;
      this.threshold = threshold;
    }

    @Override
    protected Integer compute() {
      if(high - low <= threshold){
        return sequential(array, str, low, high);
      }
      ForkJoinSum left = new ForkJoinSum(array, low, (high+low)/2, threshold);
      ForkJoinSum right = new ForkJoinSum(array, (high+low)/2, high, threshold);
      //System.out.println("I'm here!!!!! in compute");
      left.fork();
      int rightResult = right.compute();
      int leftResult = left.join();
      return rightResult+leftResult;
    }
  }
}