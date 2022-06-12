package edu.yu.da;

import org.junit.Test;


import java.util.*;

public class MaximizePayoutTest {

    @Test
    public void test(){
        List<Long> a = Arrays.asList(4L,7L,8L,4L,3L);
        List<Long> b = Arrays.asList(7L,3L,5L,7L,9L);
        MaximizePayout mp = new MaximizePayout();
        long sort = mp.max(a,b);
        System.out.println(sort);



    }
}