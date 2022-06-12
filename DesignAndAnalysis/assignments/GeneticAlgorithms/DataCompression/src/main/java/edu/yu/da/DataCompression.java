package edu.yu.da;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static edu.yu.da.DataCompressionI.SolutionI;

public class DataCompression implements DataCompressionI {

    List<String> original;
    HashSet<List<String>> set = new HashSet<>();
    List<Solution> popu = new ArrayList<>();
    double fitnessSum2 = 0;
    boolean x = true;
    double mutationChance;
    double crossOver;
    int gen = 1;
    int listSize;
    int size;
    double fitnessSum;
    /** Constructor.
     *
     * @param original the list whose elements we want to reorder
     * to reduce the
     * number of bytes when compressing the list.
     */
    public DataCompression(final List<String> original){
        this.original = original;
    }

    /** Returns the best Solution found by a genetic algorithm for the simple
     * equation specified by the requirements document.
     *
     * @param gac contains properties needed by a genetic algorithm
     * @see GeneticAlgorithmConfig
     */
    @Override
    public SolutionI solveIt(GeneticAlgorithmConfig gac) {
        size = gac.getInitialPopulationSize();
        listSize = original.size();
        mutationChance = gac.getMutationProbability();
        crossOver = gac.getCrossoverProbability();
        //fitnessSum = 0;
        Solution begin = new Solution(original, original, 1);

        //fill the first generation
        for(int i = 0; i < size; i++){
            List<String> chrom = new ArrayList<>(original);
            Collections.shuffle(chrom);
            Solution s = new Solution(original, chrom, gen);
            popu.add(s);
            //fitnessSum += s.fitness(); //The total fitness number overall
        }
        Collections.sort(popu);

        //Keep creating new generations until the solution is found
        for(int i = 0; i < gac.getMaxGenerations(); i++) {
            List<Solution> nextGen;

            //Prepare new children, using either tourney or roulette
            nextGen = prepareNextGen(gac.getSelectionType());

            //System.out.println(gen);
            //System.out.println(nextGen.get(0).gen);

            while (nextGen.size() < size){
                nextGen.add(popu.get(0));
                Solution a = popu.remove(0);
                //fitnessSum2 += a.fitness();
            }

            fitnessSum = fitnessSum2;
            fitnessSum2 = 0;
            mutateGeneration(nextGen);
            gen++;
            Collections.sort(nextGen);
            popu = nextGen;
            //System.out.println(popu);
            //System.out.println(popu.get(0).gen);
            for(Solution s : popu){
                set.add(s.list);
            }

            //System.out.println(popu);
            if(popu.get(0).relativeImprovement() >= gac.getThreshold()) {
                //threshold reached!
                return nextGen.get(0);
            }

        }

        //System.out.println(set.size());
        return popu.get(0);
    }

    private List<Solution> prepareNextGen(GeneticAlgorithmConfig.SelectionType type){
        List<Solution> nextGen = new ArrayList<>();
        Solution p1;
        Solution p2;
        int amount = size % 2 == 0? size/2 : (size/2)-1;
        //if(size <= 4)amount = 4;

        if(type == GeneticAlgorithmConfig.SelectionType.ROULETTE){
            for (int l = 0; l < amount; l++){
                p1 = roulette(Math.random() * ((original.size() + 1) * (original.size() / 2)));
                p2 = roulette(Math.random() * ((original.size() + 1) * (original.size() / 2)));
                int nOverflow = 0;
              /*  while(p1.equals(p2)){
                    p2 = roulette(((Math.random() * (fitnessSum)) + 0));
                    nOverflow++;
                    if(nOverflow == size) break;
                }*/
                nextGen.addAll(reproduce(p1, p2, gen));
            }
        }else{
            for (int l = 0; l < amount; l++){
                p1 = tourney(size);
                p2 = tourney(size);
                int nOverflow = 0;
               /* while(p1.equals(p2)){
                    p2 = tourney(size);
                    nOverflow++;
                    if(nOverflow == size) break;
                }*/
                //System.out.println(gen);
                nextGen.addAll(reproduce(p1,p2, gen));
            }
        }
        return nextGen;
    }

    private void mutateGeneration(List<Solution> nextGen){
        int i = 0;
        for(Solution s : nextGen){
            double chance = (Math.random() * (1));
            if(chance <= mutationChance){
                int spot1 = (int) (Math.random() * listSize-1);
                int spot2 = (int) (Math.random() * listSize-1);
                String temp = s.list.get(spot1);
                s.list.set(spot1, s.list.get(spot2));
                s.list.set(spot2, temp);
            }
            i++;
        }
    }

    private void swap(List<String> list, int a, int b){
        String temp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, temp);
    }

    public Solution tourney(int size){
        List<Solution> tourney = new ArrayList<>();
        tourney.add(popu.get((int) (Math.random() * size)));
        tourney.add(popu.get((int) (Math.random() * size)));
        tourney.add(popu.get((int) (Math.random() * size)));
        Collections.sort(tourney);
        return tourney.get(0);
    }

    private Solution roulette(double hit){
        int grab = 0;  //the lowest index going up
        int current = original.size();
        for(int i = original.size(); i >= 0; i--){
            if(current >= hit) {
                return popu.get(grab);
            }
            current += i;
            grab++;
        }
        return popu.get(popu.size()-1);
    }

    private List<Solution> reproduce(Solution a, Solution b, int gen) {
        List<Solution> kids = new ArrayList<>();

        //System.out.println(a.list);
        //System.out.println(b.list);
        double chance = (Math.random() * (1));
        List<String> a1 = new ArrayList<>(a.list);
        List<String> b1 = new ArrayList<>(b.list);
        List<String> selections = new ArrayList<>();
        for(int i = 0; i < size/2; i++){
            String s = original.get((int) (Math.random() * listSize));

            selections.add(s);
        }

       while(!selections.isEmpty()){
           String s = selections.remove(0);
           List<Integer> aIndexes = new ArrayList<>();
           List<Integer> bIndexes = new ArrayList<>();

           //Get all the indexes that s is in each list
           for(int i = 0; i < listSize; i++) {
               if(a1.get(i).equals(s)) aIndexes.add(i);
               if(b1.get(i).equals(s)) bIndexes.add(i);
           }

           //pick a randon index of the indexes to swap
           int first = aIndexes.get((int) (Math.random() * aIndexes.size()));
           int second = bIndexes.get((int) (Math.random() * bIndexes.size()));

           /*if(first == second){
               int which = (int) (Math.random() * 2);
               List<String> other = which == 1? a1 : b1;
               for(int i = 0; i < size; i++){
                   String string = other.get(i);
                   if(string.equals(s) && i != second){
                       second = i;
                       break;
                   }
               }
           }*/
          // System.out.println(first + " " + second);
            swap(a1, first, second);
            swap(b1, second, first);
        }


        if (crossOver > chance) {
            if(gen > 1){
                int erifnuewibve = 3;
            }
            //System.out.println(gen);
            Solution x = new Solution(original, a1, gen);
            Solution y = new Solution(original, b1, gen);
            //fitnessSum2 += (x.fitness() + y.fitness());
            kids.add(x);
            kids.add(y);

            return kids;
        } else {
            return Arrays.asList(a, b);
        }
    }


    /** Return the number of bytes when applying compression to the original
     * list.
     *
     * @return number of bytes
     */
    @Override
    public int nCompressedBytesInOriginalList() {
        return DataCompressionI.bytesCompressed(original);
    }

    static class Solution implements SolutionI ,Comparable<Solution>{

        List<String> original;
        List<String> list;
        int gen;

        public Solution(List<String> original, List<String> solution, int gen){
            this.original = original;
            this.list = solution;
            this.gen = gen;
        }


        /** Returns the list associated with this solution: the elements are
         * identical to the original list, but may be ordered differently to
         * require fewer bytes when compressed.
         *
         * @return the solution's List.
         */
        @Override
        public List<String> getList() {
            return original;
        }

        @Override
        public List<String> getOriginalList() {
            return list;
        }

        private Integer compressHere(){
            return DataCompressionI.bytesCompressed(list);
        }


        /** Returns the ratio of the compressed number of bytes associated with the
         * original list (numerator) to the solution's compressed number of bytes
         * (denominator).
         *
         */
        @Override
        public double relativeImprovement() {
            return (double)DataCompressionI.bytesCompressed(original)/DataCompressionI.bytesCompressed(this.list);
        }

        @Override
        public int nGenerations() {
            return gen;
        }



        @Override
        public int compareTo(Solution o) {
            return compressHere().compareTo(o.compressHere());
        }

        @Override
        public String toString(){
            //return list + " " + gen + " " + compressHere();
            return Double.toString(relativeImprovement()) + " " + list;
        }

        @Override
        public boolean equals(Object o){
            if(! (o instanceof Solution))return false;
            Solution a = (Solution) o;
            return a.list.equals(this.list);
        }
    }
}
