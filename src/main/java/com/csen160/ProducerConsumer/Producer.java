package com.csen160.ProducerConsumer;

public class Producer extends Thread {
    private Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // implement a loop that produces a random values
        // to put on the buffer. Then sleep for half a second.
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
