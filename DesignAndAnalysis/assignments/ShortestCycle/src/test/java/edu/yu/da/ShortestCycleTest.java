package edu.yu.da;

import org.junit.Test;
import edu.yu.da.ShortestCycleBase.Edge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortestCycleTest{

    @Test
    public void test(){
        Edge a = new Edge(1,2,2);
        Edge b = new Edge(2,3,30);
        Edge c = new Edge(1,3,4);
        Edge d = new Edge(2,4,1);
        Edge e = new Edge(3,4,1);
        //Edge f = new Edge(4, 5, 3);
        List<Edge> edges = Arrays.asList(a,b,c,d,e);
        ShortestCycleBase cycle = new ShortestCycle(edges, c);
        System.out.println(cycle.doIt());

    }

    @Test
    public void test1(){
        Edge a = new Edge(1,2,3);
        Edge b = new Edge(1,3,4);
        Edge c = new Edge(2,4,9);
        Edge d = new Edge(2,5,2);
        Edge e = new Edge(3,6,5);
        Edge f = new Edge(3,7,11);
        Edge g = new Edge(3,8,3);
        Edge h = new Edge(8,10,10);
        Edge i = new Edge(10,1,5);
        Edge j = new Edge(5,6,1);
        Edge k = new Edge(4,9,1);
        Edge l = new Edge(9,1,2);
        //Edge f = new Edge(4, 5, 3);
        List<Edge> edges = Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l);
        ShortestCycleBase cycle = new ShortestCycle(edges, b);
        System.out.println(cycle.doIt());

    }
    @Test
    public void test2(){
        Edge a = new Edge(1,2,40);
        Edge b = new Edge(1,3,40);
        Edge c = new Edge(2,4,5);
        Edge d = new Edge(2,5,2);
        Edge e = new Edge(3,6,5);
        Edge f = new Edge(3,7,11);
        Edge g = new Edge(3,8,3);
        Edge h = new Edge(8,10,10);
        Edge i = new Edge(10,1,5);
        Edge j = new Edge(5,6,1);
        Edge k = new Edge(4,9,1);
        Edge l = new Edge(9,1,2);

        //Edge f = new Edge(4, 5, 3);
        List<Edge> edges = Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l);
        ShortestCycleBase cycle = new ShortestCycle(edges, j);
        System.out.println(cycle.doIt());

    }

    @Test
    public void test3(){
        Edge a = new Edge(1,2,5);
        Edge b = new Edge(1,3,5);
        Edge c = new Edge(2,4,5);
        Edge d = new Edge(2,5,2);
        Edge e = new Edge(3,6,20);
        Edge f = new Edge(3,7,11);
        Edge g = new Edge(3,8,3);
        Edge h = new Edge(8,10,10);
        Edge i = new Edge(10,1,5);
        Edge j = new Edge(5,6,1);
        Edge k = new Edge(4,9,1);
        Edge l = new Edge(9,1,20);
        Edge m = new Edge(1,11,1);
        Edge n = new Edge(11,5,1);
        //Edge f = new Edge(4, 5, 3);
        List<Edge> edges = Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l,m,n);
        ShortestCycleBase cycle = new ShortestCycle(edges, a);
        System.out.println(cycle.doIt());

    }

    @Test
    public void test4(){
        Edge a = new Edge(1,2,3);
        Edge b = new Edge(1,3,1);
        Edge c = new Edge(2,3,7);
        Edge d = new Edge(2,4,5);
        Edge e = new Edge(3,4,2);
        Edge f = new Edge(2,5,1);
        Edge g = new Edge(4,5,7);
        //Edge f = new Edge(4, 5, 3);
        List<Edge> edges = Arrays.asList(a,b,c,d,e,f,g);
        ShortestCycleBase cycle = new ShortestCycle(edges, a);
        System.out.println(cycle.doIt());

    }

}