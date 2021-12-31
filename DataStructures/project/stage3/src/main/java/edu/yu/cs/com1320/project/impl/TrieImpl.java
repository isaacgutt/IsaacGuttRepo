package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private final int alphabetSize;
    private Node root;

    public TrieImpl(){
        alphabetSize = 36;
    }

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if(key == null){
            throw new IllegalArgumentException("Use the parameters");
        }
        if(key.isEmpty()){
            return;
        }
        key = fixNode(key);
        if (val == null)
        {
            return;
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }
    private Node put(Node node, String key, Value val, int d){
        if (node == null)
        {
            node = new Node<>();
        }
        if (d == key.length())
        {
            node.values.add(val);
            return node;
        }
        char c = key.charAt(d);

        int spot = Character.isDigit(c)? Character.getNumericValue(c) : c - 87;
        node.links[spot] = this.put(node.links[spot], key, val, d+1);
        return node;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if(key == null || comparator == null){
            throw new IllegalArgumentException("Null parameters");
        }
        key = fixNode(key);
        List<Value> list = new ArrayList<>();

        if(key.trim().isEmpty()){
            return list;
        }
        Node node = this.get(this.root, key, 0);
        if (node == null)
        {
            return list;
        }
        try {
            list.addAll(node.values);
        }catch (NullPointerException e){}

        list.sort(comparator);
        return list;
    }

    private Node get(Node x, String key, int d){
        if (x == null)
        {
            return null;
        }

        if (d == key.length())
        {
            return x;
        }
        char c = key.charAt(d);
        int spot = Character.isDigit(c)? Character.getNumericValue(c) : c - 87;
        return this.get(x.links[spot], key, d + 1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if(prefix == null || comparator == null){
            throw new IllegalArgumentException("Null parameters");
        }
        prefix = fixNode(prefix);
        List<Value> valueList = new ArrayList<>();
        if(prefix.trim().isEmpty()){
            return valueList;
        }
        valueList = getAllSorted(prefix, comparator);
        HashSet<Value> spill = new HashSet<>(valueList);
        List<Node> nodeList = new ArrayList<>();
        Node node = this.get(this.root, prefix, 0);
        if (node == null)
        {
            return valueList;
        }
        nodeList = getPrefixNodes(nodeList, node);
        for(Node n : nodeList){
            try {
                spill.addAll(n.values);
            }catch(NullPointerException e){}
        }
        valueList.clear();
        valueList.addAll(spill);
        valueList.sort(comparator);
        return valueList;

    }

    private List<Node> getPrefixNodes(List<Node> list, Node node){
        List<Node> tempList = new ArrayList<>();
        if(hasEntry(node)){
            for(Node link : node.links){
                if(link != null){
                    tempList.add(link);
                    list.add(link);
                }
            }
            for(Node n : tempList){
                list = getPrefixNodes(list, n);
            }
        }
        return list;
    }

    private boolean hasEntry(Node node) {
        for (Node link : node.links) {
            if (link != null) {
                return true;
            }
        }
        return false;
    }
    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException("Null parameters");
        }
        Set<Value> set = new HashSet<>();
        prefix = fixNode(prefix);
        if(prefix.trim().isEmpty()){
            return set;
        }
        set = deleteAll(prefix);
        Node node = get(this.root, prefix,0);
        if (node == null)
        {
            return null;
        }
        List<Node> nodeList = getPrefixNodes(new ArrayList<>(), node);
        for(Node n : nodeList){
            set.addAll(n.values);
        }
        node.values.clear();
        node.links = new Node[alphabetSize];
        return set;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException("Null parameters");
        }
        Set<Value> set = new HashSet<>();
        key = fixNode(key);
        if(key.trim().isEmpty()){
            return set;
        }
        Node node = get(this.root, key,0);
        if (node == null)
        {
            return null;
        }
        set.addAll(node.values);
        node.values.clear();
        return set;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        if(key == null || val == null){
            throw new IllegalArgumentException("Null parameters");
        }
        key = fixNode(key);
        Node node = get(this.root, key,0);
        if (node == null)
        {
            return null;
        }
        boolean check = node.values.remove(val);
        Value end = check? val : null;
        return end;
    }
    private String fixNode(String word){
        return word.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();
    }

    protected class Node<Value>
    {
        protected Set<Value> values;
        protected Node[] links;

        protected Node(){
            values = new HashSet<>();
            links = new Node[alphabetSize];
        }


    }
}
