package edu.yu.da;

import java.util.ArrayList;
import java.util.List;

public class FlowNetwork {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V;
    private int E;
    private List<HelpFromRabbeim.FlowEdge>[] adj;


    public FlowNetwork(int V) {
        if (V < 0) throw new IllegalArgumentException();
        this.V = V;
        this.E = 0;
        adj = new List[V];
        for (int v = 0; v < V; v++)
            adj[v] = new ArrayList<>();
    }



    public int getV() {
        return V;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException();
    }


    public void addEdge(HelpFromRabbeim.FlowEdge e) {
        int v = e.from();
        int w = e.to();
        checkVertex(v);
        checkVertex(w);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    public Iterable<HelpFromRabbeim.FlowEdge> adj(int v) {
        checkVertex(v);
        return adj[v];
    }


}