import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class ProducerController {
    public static int NUM_RUNS = 50;

    static Semaphore accessBuffer;
    static Semaphore canCheck;
    static Lock lock;
    static int numControllers = 0;

    private static void init() {
        accessBuffer = new Semaphore(1);
        canCheck = new Semaphore(1);
        lock = new ReentrantLock();
    }

    public class Buffer {
        static boolean producing = false;


        public static void produce() throws RuntimeException, InterruptedException {
            //Run an exception if the buffer is full
            producing = true;
            if (numControllers != 0) {
                throw new RuntimeException("Cannot produce!");
            }
            sleep(150);
            System.out.println("Producing...");
            producing = false;
        }

        public synchronized void check() {
            numControllers++;
            if (producing) {
                throw new RuntimeException("Cannot do check");
            }

            if (numControllers > 10) {
                throw new RuntimeException("Too many controllers");
            }

            System.out.println("Controlling...");
            numControllers--;
        }
    }

    //tuka nedole kucame nie
    public static class Producer extends Thread {
        private final Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            accessBuffer.acquire();
            buffer.produce();
            accessBuffer.release();
        }

        public void run() {
            for (int i = 0; i < NUM_RUNS; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Consumer extends Thread {
        private final Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            lock.lock();
            if (numControllers == 0) {
                accessBuffer.acquire();
            }
            numControllers++;
            lock.unlock();

            canCheck.acquire();
            buffer.check();

            lock.lock();
            numControllers--;
            canCheck.release();

            if (numControllers == 0) {
                accessBuffer.release();
            }
            lock.unlock();
        }

        public void run() {
            for (int i = 0; i < NUM_RUNS; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void main(String[] args) {
        Buffer buffer = new Buffer();
        Producer p = new Producer(buffer);
        List<Consumer> controllers = new ArrayList<>();
        init();
        for (int i = 0; i < 100; i++) {
            controllers.add(new Consumer(buffer));
        }
        p.start();
        for (int i = 0; i < 100; i++) {
            controllers.get(i).start();
        }

    }
}
