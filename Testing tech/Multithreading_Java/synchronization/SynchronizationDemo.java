package synchronization;

class Counter {
    private int count = 0;

    // synchronized method ensures thread-safe increment
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}

public class SynchronizationDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final Count: " + counter.getCount());
    }
}


// Explanation:This Java program demonstrates the use of synchronization to ensure thread safety when multiple threads access and modify a shared resource. The `Counter` class has a synchronized method `increment()` that safely increments the count variable
// Thread Safety: The increment() method is synchronized, so even with two threads updating the counter, no race condition occurs.

// Consistency: Without synchronized, the final count would likely be less than 2000 due to overlapping increments.

// Thread Coordination: Using join() ensures the main thread waits for both threads to complete before printing the result.