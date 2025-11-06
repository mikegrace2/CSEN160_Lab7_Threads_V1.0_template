package com.csen160.MultiThreadedFileReader;

public class A_Main {
    public static void main(String[] args) {
        Thread t1 = new FileReaderThread("files/file1.txt");
        Thread t2 = new FileReaderThread("files/file2.txt");
        Thread t3 = new FileReaderThread("files/file3.txt");

        // implement start all three threads

        // implement joining all three threads
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
