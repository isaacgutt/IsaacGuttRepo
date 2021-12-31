package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;
//import sun.net.www.content.text.Generic;

import java.util.*;

public class HashTableImpl<Key,Value> implements HashTable<Key, Value> {

    public Entry[] table = new Entry[5];
    /*private Entry<Key,Value>[] table;
    public HashTableImpl(int size){
        this.table= new Entry[size];
    }*/

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    @Override
    public Value get(Key k){
        int place = hashFunction(k);
        int correct = k.hashCode();
        Entry<Key, Value> current;
        try {
            current = this.table[place];
            Entry extra = current.next;
        }catch(NullPointerException e){
            return null;
        }
        while(current.key.hashCode() != correct && current.next != null){
            current = current.next;
        }
        if(current.key.hashCode() == correct){
            return current.value;
        }
        return null;
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     *          To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    @Override
    public Value put(Key k, Value v) {
        int place = hashFunction(k);
        Entry<Key, Value> enter = new Entry<>(k,v);
        if(this.table[place] == null){
            this.table[place] = enter;
            return null;
        }
        Value end = get(k);
        Entry<Key, Value> head = this.table[place];
        if(end == null){
            enter.next = head;
            this.table[place] = enter;
            return end;
        }
        Entry current = head;
        while(current.key != enter.key && current.next != null){
            current = current.next;
        }
        current.value = v;
        return end;
    }

    class Entry<Key, Value>{
        Key key;
        Value value;
        Entry<Key, Value> next;

        private Entry(Key k, Value v){
            if(k == null){throw new IllegalArgumentException();}
            this.key = k;
            this.value = v;
        }

    }
    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }


}
