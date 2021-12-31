package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    private int size;
    private Pile<T> top;


    public StackImpl(){
        size = 0;
        top = null;
    }

    @Override
    public void push(T element){
        Pile<T> pile = new Pile<>(element);
        if(size != 0) {
            pile.next = top;
        }
        top = pile;
        size++;
    }

    @Override
    public T pop() {
        if(size == 0){
            throw new IllegalStateException();
        }
        Pile<T> pile = top;
        top = top.next;
        size--;
        return pile.t;
    }

    @Override
    public T peek() {
        if(top != null) {
            return top.t;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private class Pile<T>{
        T t;
        private Pile next;
         private Pile(T t){
             this.t = t;
         }

    }


}
