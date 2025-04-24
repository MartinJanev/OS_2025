import java.util.concurrent.*;
import java.util.*;

public class MaxMin {
    static final int ROWS = 400;
    static final int COLS = 400;
    static int[][] matrix = new int[ROWS][COLS];
    static int[] rowMins = new int[ROWS];
    static int[] rowMaxs = new int[ROWS];

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();

        // Fill the matrix with random values
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                matrix[i][j] = rand.nextInt(10000);
            }
        }

        // Semaphore to limit to 10 concurrent threads
        Semaphore semaphore = new Semaphore(10); // Only 10 threads can run concurrently

        // Create and start threads for each row
        Thread[] threads = new Thread[ROWS];

        for (int i = 0; i < ROWS; i++) {
            final int row = i;
            threads[i] = new Thread(() -> {
                try {
                    // Acquire a permit before proceeding (limit to 10 threads)
                    semaphore.acquire();

                    int localMin = Integer.MAX_VALUE;
                    int localMax = Integer.MIN_VALUE;
                    for (int val : matrix[row]) {
                        if (val < localMin) localMin = val;
                        if (val > localMax) localMax = val;
                    }
                    rowMins[row] = localMin;
                    rowMaxs[row] = localMax;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Release the permit after the task is done
                    semaphore.release();
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < ROWS; i++) {
            threads[i].join();
        }

        // Get global min and max
        int globalMin = Arrays.stream(rowMins).min().getAsInt();
        int globalMax = Arrays.stream(rowMaxs).max().getAsInt();

        System.out.println("Global Min: " + globalMin);
        System.out.println("Global Max: " + globalMax);
    }
}
