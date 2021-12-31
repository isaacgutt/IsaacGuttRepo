package edu.yu.introtoalgs;

/** Enhances the Queue enqueue() and dequeue() API with a O(1) max()
 * method and O(1) size().  The dequeue() method is O(1), the enqueue
 * is amortized O(1).  The implementation is O(n) in space.
 *
 * @author Avraham Leff
 */

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class MaxQueue {

    //enqueue
    private ArrayDeque<Integer> stack1 = new ArrayDeque<>();
    private ArrayDeque<Integer> max1 = new ArrayDeque<>();
    
    ///dequeue
    private ArrayDeque<Integer> stack2 = new ArrayDeque<>();
    private ArrayDeque<Integer> max2 = new ArrayDeque<>();

    private int size = 0;


  /** No-argument constructor: students may not add any other constructor for
   * this class
   */
  public MaxQueue() {

  }

  /** Insert the element with FIFO semantics
   *
   * @param x the element to be inserted.
   */
  public void enqueue(int x) {
      // your code goes here
      if(stack1.size() == 0) {
          stack1.push(x);
          max1.push(x);
          size++;
          return;
      }
      size++;
      maxStack(x, max1);
      stack1.push(x);
  }

  /** Dequeue an element with FIFO semantics.
   *
   * @return the element that satisfies the FIFO semantics if the queue is not
   * empty.
   * @throws NoSuchElementException if the queue is empty
   */

  public int dequeue() {
      if(stack2.size() == 0 && stack1.size() == 0) throw new NoSuchElementException();

      if(stack2.size() == 0){
          while(stack1.size() != 0){
              int main = stack1.pop();
              max1.pop();
              stack2.push(main);
              maxStack(main, max2);
          }
      }

      size--;
      max2.pop();
      return stack2.pop();
  }

    private void maxStack(int n, ArrayDeque<Integer> stack){
        if(stack.size() == 0){
            stack.push(n);
            return;
        }

        int top = stack.peek();
        stack.push(Math.max(n, top));
    }



  /** Returns the number of elements in the queue
   *
   * @return number of elements in the queue
   */
  public int size() {
      return this.size;
  }


  /** Returns the element with the maximum value
   * 
   * @return the element with the maximum value
   * @throws NoSuchElementException if the queue is empty
   */
  public int max() {
      if(stack2.size() == 0 && stack1.size() == 0) throw new NoSuchElementException();

      if(stack2.size() == 0) return max1.peek();

      if(stack1.size() == 0) return max2.peek();

      return Math.max(max1.peek(), max2.peek());
  }

  
  
} // MaxQueue
