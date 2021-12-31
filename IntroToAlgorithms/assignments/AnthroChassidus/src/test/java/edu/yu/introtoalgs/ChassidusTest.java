package edu.yu.introtoalgs;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.yu.introtoalgs.AnthroChassidus;

import java.util.*;

public class ChassidusTest {

    @Test
    public void test(){
        int[] a = {1,3,1,4,5,7,3,14,27,19,11, 14};
        int[] b = {2,4,3,6,2,8,2, 8, 6,14,0, 27};
        AnthroChassidus chassidus = new AnthroChassidus(30, a ,b);
        assertEquals(11, chassidus.nShareSameChassidus(27));
        assertEquals(11, chassidus.nShareSameChassidus(14));
        assertEquals(2, chassidus.nShareSameChassidus(0));
        assertEquals(19, chassidus.getLowerBoundOnChassidusTypes());
    }

    @Test
    public void test2(){
        int[] a = {1 , 8,37, 6,22,11,37,3 , 1,8, 9, 0,22,60,32,76,22,66};
        int[] b = {88,77, 5,78,11,33, 9,78,24,5,98,49,66,82,56, 2,33, 5};
        AnthroChassidus chassidus = new AnthroChassidus(100, a ,b);
        assertEquals(10, chassidus.nShareSameChassidus(98));
        assertEquals(10, chassidus.nShareSameChassidus(11));
        assertEquals(2, chassidus.nShareSameChassidus(0));
        assertEquals(3, chassidus.nShareSameChassidus(78));

        assertEquals(83, chassidus.getLowerBoundOnChassidusTypes());
    }


    @Test
    public void testTime(){
        double previous = 256;
        int times = 0;
        double combined = 0;
        for(int l = 8192; l < 10000000; l += l) {
            times++;
            int[] a = new int[l];
            int[] b = new int[l];
            int max = l-1;
            int min = 0;
            int x = 0;
            int y = 0;
            boolean again;

            for (int i = 0; i < l; i++) {
                again = true;
                while (again) {
                    x = (int) Math.floor(Math.random() * (max - min + 1) + min);
                    y = (int) Math.floor(Math.random() * (max - min + 1) + min);
                    again = y == x;
                }
                a[i] = x;
                b[i] = y;
            }

            Stopwatch stopwatch = new Stopwatch(false);
            AnthroChassidus chassidus = new AnthroChassidus(l, a, b);
            double time = stopwatch.elapsedTime();
            System.out.println(l + " " + time + " " +time/previous);
            //combined += time == 0 | previous == 0? 0 : time/previous;
            if(time == 0 || previous == 0)
                times--;
            else
                combined += time/previous;

            previous = time;

        }
        System.out.println(combined + " and " + times);
        System.out.println("average ratio is " + (combined / times));
    }


    public class Stopwatch
    {
        private final long start;
        private final boolean nano;
        public Stopwatch(Boolean nano)
        {
            start = nano? System.nanoTime() : System.currentTimeMillis();
            this.nano = nano;
        }
        public double elapsedTime()
        {
            long now = nano? System.nanoTime() : System.currentTimeMillis();
            return (now - start);
        }


    }


}


