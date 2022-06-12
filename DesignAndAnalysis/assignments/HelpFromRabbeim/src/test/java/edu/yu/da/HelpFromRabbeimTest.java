package edu.yu.da;


import org.junit.Test;


import java.util.*;

import edu.yu.da.HelpFromRabbeimI.*;
import edu.yu.da.HelpFromRabbeimI.HelpTopics.*;

import static org.junit.Assert.assertEquals;

public class HelpFromRabbeimTest {

    @Test
    public void test() {
        HelpFromRabbeim help = new HelpFromRabbeim();
        //System.out.println(help.createGraph(null, null));
        Rebbe baalshem = new Rebbe(10, Arrays.asList(HelpTopics.BEITZA, HelpTopics.BAVA_KAMMA));
        Rebbe maggid = new Rebbe(20, Collections.singletonList(HelpTopics.BAVA_KAMMA));
        Rebbe alter = new Rebbe(30, Arrays.asList(HelpTopics.BEITZA, HelpTopics.BAVA_KAMMA));
        List<Rebbe> rebbes = Arrays.asList(baalshem, maggid, alter);
        HashMap<HelpTopics, Integer> bochrim = new HashMap<>();
        bochrim.put(HelpTopics.BAVA_KAMMA, 1);
        bochrim.put(HelpTopics.BEITZA, 2);
        System.out.println(help.scheduleIt(rebbes, bochrim));
        //System.out.println(HelpFromRabbeimI.HelpTopics.);
    }

    @Test
    public void test1() {
        HelpFromRabbeim help = new HelpFromRabbeim();
        //System.out.println(help.createGraph(null, null));
        Rebbe baalshem = new Rebbe(10, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.SHABBOS, HelpTopics.BAVA_KAMMA));
        Rebbe maggid = new Rebbe(20, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.NACH, HelpTopics.CHUMASH));
        Rebbe alter = new Rebbe(30, Arrays.asList(HelpTopics.BAVA_KAMMA, HelpTopics.NACH, HelpTopics.BROCHOS));
        Rebbe miteler = new Rebbe(40, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.BAVA_KAMMA, HelpTopics.MISHNAYOS));
        Rebbe tzemach = new Rebbe(50, Arrays.asList(HelpTopics.NACH, HelpTopics.SANHEDRIN, HelpTopics.BROCHOS));
        Rebbe maharash = new Rebbe(60, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.SANHEDRIN, HelpTopics.NACH, HelpTopics.BAVA_KAMMA));
        Rebbe rashab = new Rebbe(70, Arrays.asList(HelpTopics.BROCHOS, HelpTopics.SHABBOS));
        Rebbe frideker = new Rebbe(80, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.NACH, HelpTopics.BROCHOS));
        Rebbe heintiker = new Rebbe(90, Arrays.asList(HelpTopics.BEITZA, HelpTopics.BAVA_KAMMA, HelpTopics.NACH, HelpTopics.SHABBOS, HelpTopics.SANHEDRIN, HelpTopics.BROCHOS));
        //Rebbe nonexistent = new Rebbe(100, Collections.emptyList());
        List<Rebbe> rebbes = Arrays.asList(baalshem, maggid, alter, miteler, tzemach, maharash, rashab, frideker, heintiker);
        HashMap<HelpTopics, Integer> bochrim = new HashMap<>();
        bochrim.put(HelpTopics.BAVA_KAMMA, 2);
        bochrim.put(HelpTopics.SANHEDRIN, 1);
        bochrim.put(HelpTopics.NACH, 1);
        bochrim.put(HelpTopics.MISHNAYOS, 1);
        bochrim.put(HelpTopics.BROCHOS, 1);
        bochrim.put(HelpTopics.SHABBOS, 3);
        System.out.println(help.scheduleIt(rebbes, bochrim));
        //System.out.println(HelpFromRabbeimI.HelpTopics.);
    }

    @Test
    public void testBad() {
        HelpFromRabbeim help = new HelpFromRabbeim();
        //System.out.println(help.createGraph(null, null));
        Rebbe baalshem = new Rebbe(10, Arrays.asList(HelpTopics.BEITZA, HelpTopics.BAVA_KAMMA));
        Rebbe maggid = new Rebbe(20, Collections.singletonList(HelpTopics.BAVA_KAMMA));
        Rebbe alter = new Rebbe(30, Arrays.asList(HelpTopics.BAVA_KAMMA));
        List<Rebbe> rebbes = Arrays.asList(baalshem, maggid, alter);
        HashMap<HelpTopics, Integer> bochrim = new HashMap<>();
        bochrim.put(HelpTopics.BAVA_KAMMA, 1);
        bochrim.put(HelpTopics.BEITZA, 2);

        //no solution
        System.out.println(help.scheduleIt(rebbes, bochrim));
        //System.out.println(HelpFromRabbeimI.HelpTopics.);
    }

    @Test
    public void testExtra() {
        HelpFromRabbeim help = new HelpFromRabbeim();
        //System.out.println(help.createGraph(null, null));
        Rebbe baalshem = new Rebbe(10, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.SHABBOS, HelpTopics.BAVA_KAMMA));
        Rebbe maggid = new Rebbe(20, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.NACH, HelpTopics.CHUMASH));
        Rebbe alter = new Rebbe(30, Arrays.asList(HelpTopics.BAVA_KAMMA, HelpTopics.NACH, HelpTopics.BROCHOS));
        Rebbe miteler = new Rebbe(40, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.BAVA_KAMMA, HelpTopics.MISHNAYOS));
        Rebbe tzemach = new Rebbe(50, Arrays.asList(HelpTopics.NACH, HelpTopics.SANHEDRIN, HelpTopics.BROCHOS));
        Rebbe maharash = new Rebbe(60, Arrays.asList(HelpTopics.SHABBOS, HelpTopics.SANHEDRIN, HelpTopics.NACH, HelpTopics.BAVA_KAMMA));
        Rebbe rashab = new Rebbe(70, Arrays.asList(HelpTopics.BROCHOS, HelpTopics.SHABBOS));
        Rebbe frideker = new Rebbe(80, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.NACH, HelpTopics.BROCHOS));
        Rebbe heintiker = new Rebbe(90, Arrays.asList(HelpTopics.BEITZA, HelpTopics.BAVA_KAMMA, HelpTopics.NACH, HelpTopics.SHABBOS, HelpTopics.SANHEDRIN, HelpTopics.BROCHOS));
        Rebbe nonexistent = new Rebbe(100, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.NACH));
        List<Rebbe> rebbes = Arrays.asList(baalshem, maggid, alter, miteler, tzemach, maharash, rashab, frideker, heintiker, nonexistent);
        HashMap<HelpTopics, Integer> bochrim = new HashMap<>();
        bochrim.put(HelpTopics.BAVA_KAMMA, 2);
        bochrim.put(HelpTopics.SANHEDRIN, 1);
        bochrim.put(HelpTopics.NACH, 1);
        bochrim.put(HelpTopics.MISHNAYOS, 1);
        bochrim.put(HelpTopics.BROCHOS, 1);
        bochrim.put(HelpTopics.SHABBOS, 3);

        //one should be null
        System.out.println(help.scheduleIt(rebbes, bochrim));
        //System.out.println(HelpFromRabbeimI.HelpTopics.);
    }

    @Test
    public void testYU() {
        HelpFromRabbeim help = new HelpFromRabbeim();
        //System.out.println(help.createGraph(null, null));
        Rebbe yp = new Rebbe(10, Arrays.asList(HelpTopics.SANHEDRIN, HelpTopics.SHABBOS, HelpTopics.BAVA_KAMMA, HelpTopics.BROCHOS, HelpTopics.BEITZA));
        Rebbe bmp = new Rebbe(20, Arrays.asList(HelpTopics.BAVA_KAMMA, HelpTopics.BROCHOS, HelpTopics.MUSSAR));
        Rebbe ibc = new Rebbe(30, Arrays.asList(HelpTopics.CHUMASH, HelpTopics.NACH, HelpTopics.BROCHOS));
        Rebbe jss = new Rebbe(40, Arrays.asList(HelpTopics.MUSSAR, HelpTopics.CHUMASH, HelpTopics.NACH, HelpTopics.MISHNAYOS));
        Rebbe rabosai = new Rebbe(50, new ArrayList<>());
        List<Rebbe> rebbes = Arrays.asList(yp, bmp, ibc, jss, rabosai);
        HashMap<HelpTopics, Integer> bochrim = new HashMap<>();
        bochrim.put(HelpTopics.MUSSAR, 2);
        bochrim.put(HelpTopics.BROCHOS, 1);
        bochrim.put(HelpTopics.NACH, 1);
        System.out.println(help.scheduleIt(rebbes, bochrim));
    }

    /*/**
     * Returns a string representation of the flow network.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     * followed by the <em>V</em> adjacency lists
     */
    /*public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ":  ");
            for (HelpFromRabbeim.FlowEdge e : adj[v]) {
                if (e.to() != v) s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

     */

}