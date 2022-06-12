package edu.yu.da;
import org.junit.Test;
import edu.yu.da.DataCompression.Solution;
import edu.yu.da.DataCompressionI;

import java.io.IOException;
import java.util.*;
import java.lang.Math;



public class DataCompressionTest {

    //missing ngc west and east
    List<String> original = Arrays.asList("Taylor", "Leonard", "Nelson", "Buckner", "Pittman", "Kelly", "Moore", "Smith", "Ryan", "Paye", "Tannehill", "Lawrence", "Mills", "Burrow", "Jackson", "Trubisky", "Watson", "Tagovailoa", "Jones", "ZWilson", "Allen", "RWilson", "Mahomes", "Herbert", "Carr", "Mariota", "Brady", "Darnold", "Winston", "Rodgers", "Cousins", "Fields", "Goff");
    //List<String> original = Arrays.asList("Taylor", "Leonard", "Nelson", "Buckner", "Pittman", "Kelly", "Moore", "Smith", "Ryan", "Paye");
    //List<String> original = Arrays.asList("AAAAA", "BBBBB", "AAAAA", "BBBBB", "AAAAA", "BBBBB");
    //List<String> original = Arrays.asList("Ryan", "Paye", "Moore", "Buckner", "Pittman", "Nelson", "Taylor", "Leonard", "Smith", "Kelly");
    List<String> change = new ArrayList<>();

    @Test
    public void test() throws IOException {
        //System.out.println(original.size());
        change.addAll(original);
        Collections.shuffle(change);
        Solution a = new Solution(original, change, 1);
        System.out.println(change);
        System.out.println(original);
        //Solution a = new Solution(original, original,1);Solution b = new Solution(original, change, 1);
        System.out.println(a.relativeImprovement());
        //System.out.println(DataCompressionI.bytesCompressed(change));

    }

    @Test
    public void test2() {
        GeneticAlgorithmConfig gac1 = new GeneticAlgorithmConfig(100, 15, 1.2, GeneticAlgorithmConfig.SelectionType.TOURNAMENT, 0.2, 0.7);
        Solution ss = new Solution(original, original, 1);
        System.out.println(ss.relativeImprovement() + "hey");
        DataCompression s = new DataCompression(original);
        Solution a = (Solution) s.solveIt(gac1);
        System.out.println(a.relativeImprovement());
        System.out.println(a.list);
    }

    @Test
    public void test3() {
        Solution s = new Solution(original, original, 1);
        double minimum = 2;
        List<String> bad = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            List<String> change = new ArrayList<>();
            change.addAll(original);
            Collections.shuffle(change);
            s = new Solution(original, change, 1);
            DataCompressionI.bytesCompressed(s.getList());
           System.out.println(s.relativeImprovement());
            if (s.relativeImprovement() < minimum) {
               minimum = s.relativeImprovement();
               bad = s.getList();
            }

        }
        System.out.println(minimum);
        System.out.println(bad);
    }

}