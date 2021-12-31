package edu.yu.introtoalgs;

import edu.yu.introtoalgs.EstimateSecretAlgorithms2;
import org.junit.Test;


public class TimerTest {

    EstimateSecretAlgorithms2 es = new EstimateSecretAlgorithms2();


    @Test
    public void alg1() {
        System.out.println("Algorithm 1:");
        BigOMeasurable measurable = new SecretAlgorithm1();

        double previous= 0;
        for (int n = 250; n < 20000; n += n) {
            double time = es.timeAlg(n, measurable) / 1000;
            
            //n  f(n)   ratio
            System.out.println(n + " " +time + " " +time/previous);

            //To track ratio
            previous= time;
        }
        System.out.println();
    }

    @Test
    public void alg2() {
        System.out.println("Algorithm 2:");
        BigOMeasurable measurable = new SecretAlgorithm2();

        double previous= 0;
        for (int n = 2048000; n <= 262144000; n += n) {
            double time = es.timeAlg(n, measurable);
            
            //n  f(n)   ratio
            System.out.println(n + " " +time + " " +time/previous);

            //To track ratio
            previous= time;

        }
        System.out.println();
    }

    @Test
    public void alg3() {
        System.out.println("Algorithm 3:");
        BigOMeasurable measurable = new SecretAlgorithm3();

        double previous= 0;
        for (int n = 2000; n < 1000000; n += n) {
            double time = es.timeAlg(n, measurable);
            
            //n  f(n)   ratio
            System.out.println(n + " " +time + " " +time/previous);

            //To track ratio
            previous= time;
        }
        System.out.println();
    }

    @Test
    public void alg4() {
        System.out.println("Algorithm 4:");
        BigOMeasurable measurable = new SecretAlgorithm4();

        double previous = 0;
        for (int n = 500; n < 1000000000; n += n) {
            double time = es.timeAlg(n, measurable);
            
            //n  f(n)   ratio
            System.out.println(n + " " +time + " " +time/previous);

            //To track ratio
            previous = time;
        }
        System.out.println();
    }
}