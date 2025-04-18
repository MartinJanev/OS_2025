import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StringConcatenation {
    private StringBuilder result = new StringBuilder();
    private Lock lock = new ReentrantLock();

    public void concatenate(char c) {
        lock.lock();
        try {
            result.append(c);
        } finally {
            lock.unlock();
        }
    }

    public String getResult() {
        return result.toString();
    }


    public static void main(String[] args) {
        StringConcatenation concatenation = new StringConcatenation();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                concatenation.concatenate('A');
            }
        });


        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                concatenation.concatenate('B');
            }
        });

        thread1.start();
        thread2.start();

        try{
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Result: " + concatenation.getResult());
    }
}