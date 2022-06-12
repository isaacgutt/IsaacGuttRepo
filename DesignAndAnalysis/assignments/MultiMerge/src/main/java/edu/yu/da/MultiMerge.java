package edu.yu.da;

import java.util.Arrays;

public class MultiMerge extends MultiMergeBase {

    public int compares = 0;
    public MultiMerge(){
        super();
    }

    /** Does a divide-and-conquer mergesort on the z integer-valued arrays (first
     * dimension is the ith array).
     *
     * @param arrays z (z >= 1) integer valued arrays, each of which is sorted,
     * and of identical size n where n>0 and need not be a power of two.  Results
     * are undefined if the arrays aren't sorted, or if they aren't the same
     * size.
     * @return result array of size "z * n": when the method completes, holds the
     * sorted contents of all input arrays.
     */
    @Override
    public int[] merge(int[][] arrays) {
        return mergeAll(arrays, 0, arrays.length-1);
        //return iterativeMerge(arrays, arrays[0], 1);
    }

    private int[] mergeAll(int[][] arrays, int start, int end){
        if(start == end) return arrays[start];
        int[] a = mergeAll(arrays, start, (start + end)/2);
        int[] b = mergeAll(arrays, (start + end)/2 +1, end);
        //combinedAMerge();
        return mergeTwo(new spotArray(a), new spotArray(b));
    }

    public int[] iterativeMerge(int[][] arrays, int[] current, int spot){
        if(spot == arrays.length-1){
            return mergeTwo(new spotArray(arrays[spot]), new spotArray(current));
        }
        int[] go = mergeTwo(new spotArray(arrays[spot]), new spotArray(current));
        spot++;
        return iterativeMerge(arrays, go, spot);
    }


    private int[] mergeTwo(spotArray a, spotArray b){

        int spot = 0;
        int[] end = new int[a.length() + b.length()];
        while(spot != end.length){

            //if b is done, just go in with a.
            //if b isn't done, make sure a isn't either before going into it
            if(b.done() || (!a.done() && a.spot(false) <= b.spot(false))){
                end[spot] = a.spot(true);
            }
            else{
                end[spot] = b.spot(true);
            }
            spot++;
            compares++;

        }
        //System.out.printf("a: %d, b: %d, compares: %d\n",a.array.length,b.array.length ,compares);
        combinedAMerge();
        //System.out.println(getNCombinedMerges() + " " + compares);
        return end;
    }

    private class spotArray{
        int spot = 0;
        int[] array;

        public boolean done(){
            return spot == length();
        }

        public spotArray(int[] array){
            this.array = array;
        }

        public int spot(boolean grab){
            int end = array[spot];
            if(grab)spot++;
            return end;
        }
        public int length(){
            return array.length;
        }
        @Override
        public String toString(){
             return Arrays.toString(array);
        }

    }
}
