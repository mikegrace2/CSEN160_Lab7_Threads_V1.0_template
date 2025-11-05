package com.csen160.ProducerConsumer;

public class Consumer extends Thread {
    private Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // Implement a loop that consumes the next value
        // from the buffer and then sleeps for one second
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
