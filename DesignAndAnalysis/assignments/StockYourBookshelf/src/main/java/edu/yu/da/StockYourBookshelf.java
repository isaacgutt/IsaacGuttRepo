package edu.yu.da;

/** Implements the StockYourBookshelfI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;

public class StockYourBookshelf implements StockYourBookshelfI {
    Set<String> sortedSefarim = new TreeSet<>();
    HashMap<String, Integer> niecesChoice;
    boolean maxNotCalled = true;
    boolean noSolution = false;

    /** No-op constructor
     */
    public StockYourBookshelf() {
	// no-op, students may change the implementation
    }

    @Override
    public List<Integer> solution() {
        if(maxNotCalled) throw new IllegalStateException();
        if(noSolution) return Collections.<Integer>emptyList();
        //Collections.sort(sefarimOrder);
        List<Integer> finale = new ArrayList<>();
        for(String s : sortedSefarim){
            finale.add(niecesChoice.get(s));
        }
        return finale;
    }

    @Override
    public int
	maxAmountThatCanBeSpent
	(final int budget, final Map<String, List<Integer>> seforimClassToTypePrices)
    {
        maxNotCalled = false;
        //sefarimOrder.addAll(seforimClassToTypePrices.keySet());
        List<sefersPurchased> previousHits = new ArrayList<>();
        int maxSpent = 0;
        sefersPurchased bestPath = null;
        int count = 0;
        //matrix to calculate potential spending
       int[][] prices = new int[seforimClassToTypePrices.size()][budget];
       //for every sefer
       for(String sefer : seforimClassToTypePrices.keySet()) {
           sortedSefarim.add(sefer);
           List<sefersPurchased> newHits = new ArrayList<>();
           //go through prices of the sefer
           for (int seferCost : seforimClassToTypePrices.get(sefer)) {

               //first row of matrix, no previous info
               if (count == 0) {
                   if(budget-seferCost >= 0) {
                       prices[count][budget - seferCost] = 1;
                       previousHits.add(new sefersPurchased(budget, sefer, seferCost));
                   }
               } else {
                   //Calculate all possible paths from the previous paths
                   for (sefersPurchased path : previousHits) {
                       int oldPrice = path.getBudget();
                       //if this is an affordable path
                       if (oldPrice - seferCost >= 0) {
                           prices[count][oldPrice - seferCost] = 1;
                           sefersPurchased nextRun = null;

                           //clone next path for next run, don't impact this run as there are multiple
                           //routes each single path can take on this run
                           try {
                               nextRun = (sefersPurchased) path.clone();
                               //make the class keep it's map separate from the clone
                               nextRun.setPath();
                           } catch (CloneNotSupportedException e) {
                               e.printStackTrace();
                           }
                           nextRun.addSefer(sefer, seferCost);
                           newHits.add(nextRun);

                           //always check if we found the best path
                           if (budget - nextRun.getBudget() >= maxSpent) {
                               maxSpent = budget - nextRun.getBudget();
                               bestPath = nextRun;
                           }
                       }
                   }
               }
           }
           if (count != 0 && newHits.size() == 0){
               noSolution = true;
               return Integer.MIN_VALUE;
       }
           else if(count != 0)previousHits = newHits;
           count++;
       }

       //this.sefarimOrder = bestPath.getList();
       this.niecesChoice = bestPath.getMap();
       return maxSpent;
    }

    private static class sefersPurchased implements Cloneable{
        HashMap<String, Integer> path = new HashMap<>();
        int budget;

        public sefersPurchased(int budget,String sefer, int price){
            this.budget = budget;
            path.put(sefer, price);
            this.budget -= price;
        }

        public void setPath(){
            path = (HashMap<String, Integer>) path.clone();
        }

        public void addSefer(String sefer, int price) {
            path.put(sefer, price);
            this.budget -= price;
        }


        public HashMap<String, Integer> getMap(){
            return path;
        }

        public int getBudget(){
            return budget;
        }
        public Object clone() throws CloneNotSupportedException{
            return super.clone();
        }

        @Override
        public int hashCode(){
            return Objects.hash(path);
        }

        @Override
        public String toString(){
            return path.toString() + " " + budget;
        }
    }

} // StockYourBookshelf
