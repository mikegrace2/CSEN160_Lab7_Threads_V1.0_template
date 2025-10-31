package com.csen160.ProducerConsumer;

import java.util.LinkedList;

public class Buffer {
    private LinkedList<Integer> list = new LinkedList<>();
    private final int CAPACITY = 5;

    public synchronized void put(int value) throws InterruptedException {
        while (list.size() == CAPACITY) {
            wait();
        }
        list.add(value);
        System.out.println("Produced: " + value);
        notifyAll();
    }

    public synchronized int get() throws InterruptedException {
        while (list.isEmpty()) {
            wait();
        }
        int value = list.removeFirst();
        System.out.println("Consumed: " + value);
        notifyAll();
        return value;
    }
}