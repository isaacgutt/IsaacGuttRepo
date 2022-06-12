package edu.yu.da;


import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class StockYourBookshelfTest {

    @Test
    public void test1(){
        int budget = 8;
        Map<String, List<Integer>> sefarim = new HashMap<>();
        sefarim.put("Gemaras", Arrays.asList(3,2,1));
        sefarim.put("Chumashim", Arrays.asList(4,1));
        sefarim.put("Tanya", Arrays.asList(3,2));
        sefarim.put("Halacha", Arrays.asList(2,1));
        StockYourBookshelf syb = new StockYourBookshelf();
        System.out.println( syb.maxAmountThatCanBeSpent(budget, sefarim));
        //8
        System.out.println(syb.solution());
    }

    @Test
    public void test2(){
        int budget = 9;
        Map<String, List<Integer>> sefarim = new HashMap<>();
        sefarim.put("Gemaras", Arrays.asList(6,4,8));
        sefarim.put("Chumashim", Arrays.asList(5,10));
        sefarim.put("Tanya", Arrays.asList(1,5,3,5));
        StockYourBookshelf syb = new StockYourBookshelf();
        System.out.println( syb.maxAmountThatCanBeSpent(budget, sefarim));
        ///interger.min
        System.out.println(syb.solution());
    }

    @Test
    public void test3(){
        int budget = 900;
        Map<String, List<Integer>> sefarim = new HashMap<>();
        sefarim.put("Gemaras", Arrays.asList(30,100,150));
        sefarim.put("Chumashim", Arrays.asList(30,70,150));
        sefarim.put("Tanya", Arrays.asList(40,50,150));
        StockYourBookshelf syb = new StockYourBookshelf();
        System.out.println( syb.maxAmountThatCanBeSpent(budget, sefarim));
        //450
        System.out.println(syb.solution());
    }

    @Test
    public void test4(){
        int budget = 310;
        Map<String, List<Integer>> sefarim = new HashMap<>();
        sefarim.put("Gemaras", Arrays.asList(30,100,150));
        sefarim.put("Chumashim", Arrays.asList(30,70,150));
        sefarim.put("Tanya", Arrays.asList(40,50,150));
        StockYourBookshelf syb = new StockYourBookshelf();
        System.out.println( syb.maxAmountThatCanBeSpent(budget, sefarim));
        //300
        System.out.println(syb.solution());
    }

   
}
