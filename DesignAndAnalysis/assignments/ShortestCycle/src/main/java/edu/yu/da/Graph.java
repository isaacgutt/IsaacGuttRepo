package edu.yu.da;

import java.util.*;

import edu.yu.da.ShortestCycleBase.Edge;

/**
 * This class uses Sedgwick's Bag.java https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Bag.java
 *and EdgeWeightedGraph.java from the textbook page 611
 * and the Bag.remove() method I got from StackOverflow https://stackoverflow.com/questions/50204759/bag-remove-method
 * */


public class Graph {
    private int v=0; // number of vertices
    private int E; // number of edges
    private Bag<Edge>[] adj; // adjacency lists
    public Graph(List<Edge> edges)
    {
        for(Edge edge : edges){
            if(edge.w() > v) v = edge.w();
            if(edge.v() > v) v= edge.v();
        }
        //this.V = 0;
        this.E = edges.size();
        adj = (Bag<Edge>[]) new Bag[v+1];
        for (int i = 0; i <= v; i++)
            adj[i] = new Bag<Edge>();

        for(Edge edge : edges){
            addEdge(edge);
        }

    }
    // See Exercise 4.3.9.
    //public int V() { return V; }
    public int E() { return E; }
    public void addEdge(Edge e)
    {
        int v = e.v(), w = e.w();
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }
    public void removeEdge(Edge e){
        adj[e.v()].remove(e);
        adj[e.w()].remove(e);
    }
    public int V(){
        return v;
    }
    public Iterable<Edge> adj(int v)
    { return adj[v]; }
    // See page 609.



    class Bag<Item> implements Iterable<Item> {
        private Node<Item> first;    // beginning of bag
        private int n;               // number of elements in bag

        // helper linked list class
        private class Node<Item> {
            private Item item;
            private Node<Item> next;
        }

        /**
         * Initializes an empty bag.
         */
        public Bag() {
            first = null;
            n = 0;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            Iterator<Item> itr = iterator();
            while (itr.hasNext()) {
                Object element = itr.next();
                s.append(" " + element);
            }
            return s.toString();
        }

        /**
         * Returns true if this bag is empty.
         *
         * @return {@code true} if this bag is empty;
         * {@code false} otherwise
         */
        public boolean isEmpty() {
            return first == null;
        }

        /**
         * Returns the number of items in this bag.
         *
         * @return the number of items in this bag
         */
        public int size() {
            return n;
        }

        public void remove(Item item){
            Node<Item> currentNode = first;
            Bag<Item> b = new Bag<Item>();
            Bag<Item> reverseB = new Bag<Item>();
            Node<Item> previous = new Node<Item>();

            if(item.equals(first.item)){

                first = currentNode.next;
                n--;
                currentNode = currentNode.next;
            }
            previous.item = currentNode.item;
            b.add(previous.item);
            currentNode = currentNode.next;

            while(currentNode != null){
                if(item.equals(currentNode.item))
                {
                    previous.item = previous.item;
                    n--;
                } else{
                    previous.item = currentNode.item;
                    b.add(previous.item);
                }
                currentNode = currentNode.next;
            }

            for(Item i: b)
                reverseB.add(i);
            this.first = reverseB.first;
        }

        /**
         * Adds the item to this bag.
         *
         * @param item the item to add to this bag
         */
        public void add(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldfirst;
            n++;
        }


        /**
         * Returns an iterator that iterates over the items in this bag in arbitrary order.
         *
         * @return an iterator that iterates over the items in this bag in arbitrary order
         */
        public Iterator<Item> iterator() {
            return new LinkedIterator(first);
        }

        // an iterator, doesn't implement remove() since it's optional
        private class LinkedIterator implements Iterator<Item> {
            private Node<Item> current;

            public LinkedIterator(Node<Item> first) {
                current = first;
            }

            public boolean hasNext() {
                return current != null;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        }
    }
}