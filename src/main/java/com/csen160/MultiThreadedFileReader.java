package com.csen160;

class FileReaderThread extends Thread {
	private String fileName;

	public FileReaderThread(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		// Implement me reading a file
		// count lines and characters
		// println() it
	}
}

public class MultiThreadedFileReader {
	public static void main(String[] args) {
		Thread t1 = new FileReaderThread("file1.txt");
		Thread t2 = new FileReaderThread("file2.txt");
		Thread t3 = new FileReaderThread("file3.txt");

		// implement start all three threads

		// implement joining all three threads
	}
}