import org.junit.jupiter.api.Test;
import com.csen160.MultiThreadedFileReader.A_Main;
import com.csen160.MultiThreadedFileReader.FileReaderThread;
import com.csen160.Bank.AccountHolder;
import com.csen160.Bank.BankAccount;
import com.csen160.ProducerConsumer.Consumer;
import com.csen160.ProducerConsumer.Producer;
import com.csen160.ProducerConsumer.Buffer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AllExercisesTest {
    public static int testResults = 0;

    @Test
    void testExercise1() throws Exception {
        Method mainMethod = A_Main.class.getMethod("main", String[].class);
        assertNotNull(mainMethod, "A_Main.main() method should exist");

        // Run main and check it completes
        long startTime = System.currentTimeMillis();
        Thread mainRunner = new Thread(() -> {
            try {
                A_Main.main(new String[0]);
            } catch (Exception e) {
                throw new RuntimeException("A_Main.main() failed", e);
            }
        });

        mainRunner.start();
        mainRunner.join(10000);
        long executionTime = System.currentTimeMillis() - startTime;

        assertFalse(mainRunner.isAlive(), "A_Main.main() should complete");
        assertTrue(executionTime > 50, "Execution should take time");

        // Track thread creation and start calls
        final AtomicInteger createdCount = new AtomicInteger(0);
        final AtomicInteger startedCount = new AtomicInteger(0);
        final Object lock = new Object();

        class TrackableThread extends FileReaderThread {
            public TrackableThread(String fileName) {
                super(fileName);
                synchronized (lock) {
                    createdCount.incrementAndGet();
                }
            }

            @Override
            public synchronized void start() {
                synchronized (lock) {
                    startedCount.incrementAndGet();
                }
                super.start();
            }
        }

        TrackableThread t1 = new TrackableThread("files/file1.txt");
        TrackableThread t2 = new TrackableThread("files/file2.txt");
        TrackableThread t3 = new TrackableThread("files/file3.txt");

        assertEquals(3, createdCount.get(), "Should create 3 threads");

        t1.start();
        t2.start();
        t3.start();

        assertEquals(3, startedCount.get(), "Should start 3 threads");

        t1.join();
        t2.join();
        t3.join();

        assertFalse(t1.isAlive(), "Thread 1 should complete");
        assertFalse(t2.isAlive(), "Thread 2 should complete");
        assertFalse(t3.isAlive(), "Thread 3 should complete");
    }

    @Test
    void testExercise2() throws Exception {
        // Track all transactions to verify behavior
        final List<String> transactionTypes = new ArrayList<>();
        final List<Integer> transactionAmounts = new ArrayList<>();
        final AtomicInteger depositCount = new AtomicInteger(0);
        final AtomicInteger withdrawCount = new AtomicInteger(0);
        final List<Long> transactionTimestamps = new ArrayList<>();
        final Object lock = new Object();

        class TestableBankAccount extends BankAccount {
            @Override
            public synchronized void deposit(int amount) {
                synchronized (lock) {
                    transactionTypes.add("deposit");
                    transactionAmounts.add(amount);
                    transactionTimestamps.add(System.currentTimeMillis());
                    depositCount.incrementAndGet();
                }
                super.deposit(amount);
            }

            @Override
            public synchronized void withdraw(int amount) {
                synchronized (lock) {
                    transactionTypes.add("withdraw");
                    transactionAmounts.add(amount);
                    transactionTimestamps.add(System.currentTimeMillis());
                    withdrawCount.incrementAndGet();
                }
                super.withdraw(amount);
            }
        }

        TestableBankAccount account = new TestableBankAccount();
        AccountHolder holder = new AccountHolder(account);

        long startTime = System.currentTimeMillis();
        holder.start();
        holder.join(10000);
        long totalTime = System.currentTimeMillis() - startTime;

        assertFalse(holder.isAlive(), "Thread should complete");

        // Should perform exactly 5 transactions
        assertEquals(5, transactionTypes.size(), "Should perform 5 transactions");
        assertEquals(5, transactionAmounts.size(), "Should have 5 transaction amounts");

        // Check amounts are in valid range
        for (int i = 0; i < transactionAmounts.size(); i++) {
            int amount = transactionAmounts.get(i);
            assertTrue(amount >= 1 && amount <= 500,
                    String.format("Amount should be 1-500, got %d", amount));
        }

        // Verify sleep happens between transactions
        assertTrue(totalTime >= 2500, "Should take time for sleeps");

        for (int i = 1; i < transactionTimestamps.size(); i++) {
            long timeBetween = transactionTimestamps.get(i) - transactionTimestamps.get(i - 1);
            assertTrue(timeBetween >= 500,
                    String.format("Sleep should occur between transactions, got %dms", timeBetween));
        }

        int totalDeposits = depositCount.get();
        int totalWithdraws = withdrawCount.get();

        assertTrue(totalDeposits >= 0 && totalDeposits <= 5);
        assertTrue(totalWithdraws >= 0 && totalWithdraws <= 5);
        assertEquals(5, totalDeposits + totalWithdraws);

        for (int i = 0; i < transactionTypes.size(); i++) {
            String type = transactionTypes.get(i);
            assertTrue(type.equals("deposit") || type.equals("withdraw"),
                    String.format("Transaction should be deposit or withdraw, got %s", type));
        }
    }

    @Test
    void testExercise3() throws Exception {
        String testFileName = "files/file1.txt";

        // Calculate expected counts first
        int expectedLineCount = 0;
        int expectedCharCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                expectedLineCount++;
                expectedCharCount += line.length();
            }
        }

        assertTrue(expectedLineCount > 0);
        assertTrue(expectedCharCount > 0);

        // Capture System.out to check println output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream testOut = new PrintStream(outputStream);
        System.setOut(testOut);

        try {
            FileReaderThread fileReader = new FileReaderThread(testFileName);
            assertNotNull(fileReader);
            fileReader.start();
            fileReader.join(10000);
            assertFalse(fileReader.isAlive());
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();

        // Verify output contains expected info
        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertTrue(output.contains(testFileName));
        assertTrue(output.contains("Lines: " + expectedLineCount));
        assertTrue(output.contains("Characters: " + expectedCharCount));

        String expectedPattern = String.format("File: %s - Lines: %d, Characters: %d",
                testFileName, expectedLineCount, expectedCharCount);
        assertTrue(output.contains(expectedPattern));
    }

    @Test
    void testExercise4() throws Exception {
        // Track get() calls and timing
        final AtomicInteger getCallCount = new AtomicInteger(0);
        final List<Long> getCallTimestamps = new ArrayList<>();
        final Object lock = new Object();

        class TestableBuffer extends Buffer {
            @Override
            public synchronized int get() throws InterruptedException {
                synchronized (lock) {
                    getCallCount.incrementAndGet();
                    getCallTimestamps.add(System.currentTimeMillis());
                }
                return super.get();
            }
        }

        TestableBuffer buffer = new TestableBuffer();
        Consumer consumer = new Consumer(buffer);

        // Pre-populate so get() doesn't block
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        consumer.start();
        Thread.sleep(3500);
        consumer.interrupt();
        consumer.join(2000);

        assertTrue(getCallCount.get() >= 3, "Should call get() multiple times");

        // Check sleep timing between calls
        for (int i = 1; i < getCallTimestamps.size(); i++) {
            long timeBetweenCalls = getCallTimestamps.get(i) - getCallTimestamps.get(i - 1);
            assertTrue(timeBetweenCalls >= 800 && timeBetweenCalls <= 1200,
                    String.format("Should sleep ~1000ms between calls, got %dms", timeBetweenCalls));
        }

        assertTrue(getCallCount.get() >= 2, "Should be in a loop");
    }

    @Test
    void testExercise5() throws Exception {
        // Track put() calls and values
        final AtomicInteger putCallCount = new AtomicInteger(0);
        final List<Integer> producedValues = new ArrayList<>();
        final List<Long> putCallTimestamps = new ArrayList<>();
        final Object lock = new Object();

        class TestableBuffer extends Buffer {
            @Override
            public synchronized void put(int value) throws InterruptedException {
                synchronized (lock) {
                    putCallCount.incrementAndGet();
                    producedValues.add(value);
                    putCallTimestamps.add(System.currentTimeMillis());
                }
                super.put(value);
            }
        }

        TestableBuffer buffer = new TestableBuffer();
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        // Start consumer to prevent buffer from filling up
        consumer.start();
        producer.start();
        Thread.sleep(2500);
        producer.interrupt();
        consumer.interrupt();
        producer.join(2000);
        consumer.join(2000);

        assertTrue(putCallCount.get() >= 4, "Should call put() multiple times");

        // Verify random values are in correct range
        for (int i = 0; i < producedValues.size(); i++) {
            int value = producedValues.get(i);
            assertTrue(value >= 0 && value < 100,
                    String.format("Should generate 0-99, got %d", value));
        }

        // Check sleep timing
        for (int i = 1; i < putCallTimestamps.size(); i++) {
            long timeBetweenCalls = putCallTimestamps.get(i) - putCallTimestamps.get(i - 1);
            assertTrue(timeBetweenCalls >= 400 && timeBetweenCalls <= 600,
                    String.format("Should sleep ~500ms between calls, got %dms", timeBetweenCalls));
        }

        assertTrue(putCallCount.get() >= 2, "Should be in a loop");
    }

}