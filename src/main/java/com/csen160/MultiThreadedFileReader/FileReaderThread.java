package com.csen160.MultiThreadedFileReader;

public class FileReaderThread extends Thread {
    private String fileName;

    public FileReaderThread(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        // Implement me reading a file
        // count lines and characters
        // println() it
        throw new UnsupportedOperationException("Not supported yet.");
    }
}