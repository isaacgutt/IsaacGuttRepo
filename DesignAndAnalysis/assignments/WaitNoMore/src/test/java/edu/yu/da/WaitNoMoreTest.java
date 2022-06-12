package edu.yu.da;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WaitNoMoreTest{

    @Test
    public void test(){
        //int[] durantions = {3,4,2,5};
        //int[] weights = {1,2,1,1};
        int[] durantions = {2,4};
        int[] weights = {1,1};
        WaitNoMore wnm = new WaitNoMore();
        System.out.println(wnm.minTotalWaitingTime(durantions, weights));

    }


    @Test
    public void test2(){
        int[] durants = {3,4,2,5,6,4,88,4,11,2};
        int[] weights = {1,2,1,1,3,4,2,1, 3,5};
        //6
        //int[] durantions = {2,4};
        //int[] weights = {1,1};
        WaitNoMore wnm = new WaitNoMore();
        System.out.println(wnm.minTotalWaitingTime(durants, weights));

        durants = new int[]{2,4,2,4,6,3,11,4,5,88};
        weights =new int[] {5,4,1,2,3,1, 3,1, 1,2};
        int minimumWait = 0;
        int currentWaiting = 0;
        for(int i = 0; i < durants.length; i++) {
            minimumWait += currentWaiting;
            //minimumWait += currentWaiting;
            currentWaiting += durants[i];
        }
        System.out.println(minimumWait);
    }

    @Test
    public void test3(){
        int[] durants = {2,3,6,1};
        int[] weights = {1,1,1,1};
        //6
        //int[] durantions = {2,4};
        //int[] weights = {1,1};
        WaitNoMore wnm = new WaitNoMore();
        System.out.println(wnm.minTotalWaitingTime(durants, weights));
    }

    @Test
    public void test4(){
        int[] durants = {4,3,1,6};
        int[] weights = {3,2,1,1};
        //6
        //int[] durantions = {2,4};
        //int[] weights = {1,1};
        WaitNoMore wnm = new WaitNoMore();
        System.out.println(wnm.minTotalWaitingTime(durants, weights));
        int minimumWait = 0;
        int currentWaiting = 0;
        for(int i = 0; i < durants.length; i++) {
            minimumWait += currentWaiting * weights[i];
            currentWaiting += durants[i];
        }
        System.out.println(minimumWait);
    }
}
