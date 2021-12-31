package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    private static final int MAX = 4;
    private Node root; //root of the B-tree
    private Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree
    private PersistenceManager dpm = null;
    private boolean addingToDisk = false;

    public BTreeImpl(){
        this.root = new Node(0);
        this.leftMostExternalNode = this.root;
    }

    @Override
    public Value get(Key key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry entry = this.get(this.root, key, this.height);
        if(entry != null)
        {
            try {
                return (Value)returnValue(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Entry get(Node currentNode, Key key, int height){
        Entry[] entries= currentNode.getEntries();
        //current node is external (i.e. height == 0)
        if(height== 0){
            for(int j= 0; j< currentNode.entryCount; j++){
                if(isEqual(key, entries[j].key)){
                    return entries[j];
                }
            }//didn't find the key
            return null;
        }else{
            for (int j = 0; j < currentNode.entryCount; j++){
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key))
                {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            return null;
        }


    }

    private Object returnValue(Entry entry) throws IOException {
        if(entry.getValue() != null || dpm == null){
            return entry.getValue();
        }
        else{
            Object end = dpm.deserialize(entry.getKey());
            if(end != null){
                entry.val = end;
                dpm.delete(entry.getKey());
            }
            return end;
        }
    }

    private static boolean isEqual(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) == 0;
    }
    private static boolean less(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) < 0;
    }


    @Override
    public Value put(Key k, Value v) {
        if (k == null){
            throw new IllegalArgumentException("argument key to put() is null");
        }
        Entry alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null) {
            Value end = null;
            if(alreadyThere.getValue() == null){
                try {
                    end = (Value) dpm.deserialize(k);
                    if(end == null) dpm.delete(k);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(end == null) end = (Value) alreadyThere.val;
            alreadyThere.val = v;
            return end;
        }
        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null) {
            return null;
        }
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        this.height++;
        return null;
    }

    private Node put(Node currentNode, Key key, Value val, int height) {
        int j;
        Entry newEntry = new Entry(key, val, null);
        if (height == 0) {
            for (j = 0; j < currentNode.entryCount; j++) {
                if (less(key, currentNode.entries[j].key)) {
                    break;
                }
            }
        }
        else {
            for (j = 0; j < currentNode.entryCount; j++) {
                if ((j + 1 == currentNode.entryCount) || less(key, currentNode.entries[j + 1].key)) {
                    Node newNode = this.put(currentNode.entries[j++].child, key, val, height - 1);
                    if (newNode == null) {
                        return null;
                    }
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        return finishPut(currentNode, newEntry, j);
    }
    private Node finishPut(Node currentNode, Entry newEntry, int j){
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX) {
            return null;
        }
        else {
            return this.split(currentNode, height);
        }
    }


    private Node split(Node currentNode, int height)
    {
        Node newNode = new Node(BTreeImpl.MAX / 2);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = BTreeImpl.MAX / 2;
        //copy top half of h into t
        for (int j = 0; j < BTreeImpl.MAX / 2; j++)
        {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        //external node
        if (height == 0)
        {
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }

    @Override
    public void moveToDisk(Key k) throws Exception {
        this.dpm.serialize(k, get(k));
        //addingToDisk = true;
        put(k, null);

    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.dpm = pm;
    }

    private static class Entry
    {
        private Comparable key;
        private Object val;
        private Node child;

        protected Entry(Comparable key, Object val, Node child)
        {
            this.key = key;
            this.val = val;
            this.child = child;
        }
        protected Object getValue()
        {
            return this.val;
        }
        protected Comparable getKey()
        {
            return this.key;
        }
    }
    private static final class Node
    {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children
        private Node next;
        private Node previous;

        // create a node with k entries
        private Node(int k)
        {
            this.entryCount = k;
        }

        private void setNext(Node next)
        {
            this.next = next;
        }
        private Node getNext()
        {
            return this.next;
        }
        private void setPrevious(Node previous)
        {
            this.previous = previous;
        }
        private Node getPrevious()
        {
            return this.previous;
        }

        protected Entry[] getEntries()
        {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }


}

