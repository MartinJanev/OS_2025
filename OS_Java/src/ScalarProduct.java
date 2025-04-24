import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ScalarProduct {

    static double dotProduct = 0;
    static Lock lock;

    static final BoundedRandomGenerator random = new BoundedRandomGenerator();

    private static final int ARRAY_LENGTH = 10000000;
    private static final int NUM_THREADS = 10;

    static void init() {
        lock = new ReentrantLock();
    }

    // DO NOT CHANGE
    public static int[] getSubArray(int[] array, int start, int end) {
        return Arrays.copyOfRange(array, start, end);
    }

    public static void main(String[] args) {
        init();

        int[] a = ArrayGenerator.generate(ARRAY_LENGTH);
        int[] b = ArrayGenerator.generate(ARRAY_LENGTH);

        calculateDotProductParallel(a, b); // целата логика се наоѓа тука

        // DO NOT CHANGE
        System.out.println("Your calculated dot product is: " + dotProduct);
        System.out.println("The actual dot product is: " + ArrayGenerator.calculateDotProduct(a, b));
        SynchronizationChecker.checkResult();
    }

    public static void calculateDotProductParallel(int[] a, int[] b) {
        Thread[] threads = new Thread[NUM_THREADS];
        int sizeSub = a.length / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * sizeSub;
            int end = (i == NUM_THREADS - 1) ? a.length : start + sizeSub;
            threads[i] = new CalculateThread(a, b, start, end);
            threads[i].start();
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class CalculateThread extends Thread {
        private int[] a;
        private int[] b;
        int startSearch;
        int endSearch;

        public CalculateThread(int[] a, int[] b, int startSearch, int endSearch) {
            this.a = a;
            this.b = b;
            this.startSearch = startSearch;
            this.endSearch = endSearch;
        }

        public void run() {
            double localDotProduct = 0.0;
            for (int i = startSearch; i < endSearch; i++) {
                localDotProduct += a[i] * b[i];
            }

            synchronized (lock) {
                dotProduct += localDotProduct;
            }
        }
    }

    /******************************************************
     // DO NOT CHANGE THE CODE BELOW TO THE END OF THE FILE
     *******************************************************/

    static class BoundedRandomGenerator {
        static final Random random = new Random();
        static final int RANDOM_BOUND_UPPER = 10;
        static final int RANDOM_BOUND_LOWER = 6;

        public int nextInt() {
            return random.nextInt(RANDOM_BOUND_UPPER - RANDOM_BOUND_LOWER) + RANDOM_BOUND_LOWER;
        }
    }

    static class ArrayGenerator {

        private static double actualDotProduct = 0.0;

        static int[] generate(int length) {
            int[] array = new int[length];

            for (int i = 0; i < length; i++) {
                int num = ScalarProduct.random.nextInt();
                array[i] = num;
            }

            return array;
        }

        public static double calculateDotProduct(int[] a, int[] b) {
            double prod = 0.0;
            for (int i = 0; i < a.length; i++) {
                prod += a[i] * b[i];
            }

            actualDotProduct = prod;
            return prod;
        }
    }

    static class SynchronizationChecker {
        public static void checkResult() {
            if (ArrayGenerator.actualDotProduct != dotProduct) {
                throw new RuntimeException("The calculated result is not equal to the actual dot product!");
            }
        }
    }
}
