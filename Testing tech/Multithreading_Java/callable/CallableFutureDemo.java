package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableFutureDemo {
    public static void main(String[] args) throws Exception {

        // ExecutorService with single thread
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Callable task returns a result
        Callable<Integer> task = () -> {
            System.out.println("Calculating sum in thread: " + Thread.currentThread().getName());
            int sum = 0;
            for (int i = 1; i <= 5; i++) {
                sum += i;
            }
            return sum;
        };

        // Submit task and get Future
        Future<Integer> future = executor.submit(task);

        System.out.println("Waiting for result...");
        Integer result = future.get(); // blocking call
        System.out.println("Result: " + result);

        executor.shutdown();
    }
}


// Explanation:This Java program demonstrates the use of Callable and Future to perform a task that returns a result asynchronously.
// Callable Interface: Unlike Runnable, Callable can return a result and throw checked exceptions.
// Future Interface: Represents the result of an asynchronous computation, allowing retrieval of the result once available.
// Asynchronous Execution: The main thread submits a Callable task to an ExecutorService and waits for the result using Future's get() method, showcasing non-blocking task execution until the result is needed.
// Real-World Use Case: Useful in scenarios where tasks need to return results, such as fetching data from a database or performing computations in the background.