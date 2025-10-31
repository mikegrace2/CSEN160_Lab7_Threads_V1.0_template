package com.csen160.ProducerConsumer;

public class A_Main {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        Thread producer = new Producer(buffer);
        Thread consumer = new Consumer(buffer);

        producer.start();
        consumer.start();
    }
}
