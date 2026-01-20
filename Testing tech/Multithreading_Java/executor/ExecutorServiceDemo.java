package executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceDemo {
    public static void main(String[] args) {

        // 1. Create a thread pool of size 3
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 2. Submit 5 tasks to executor
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.submit(() -> {
                System.out.println("Executing Task " + taskId + " on " 
                        + Thread.currentThread().getName());
                try { Thread.sleep(1000); } catch (InterruptedException e) { }
            });
        }

        // 3. Shutdown executor
        executor.shutdown();
    }
}


// Explanation:This Java program demonstrates the use of ExecutorService to manage a pool of threads for executing tasks concurrently.
//  It creates a fixed thread pool of size 3 and submits 5 tasks to it. 
// Thread Management: ExecutorService handles thread creation, reuse, and lifecycle, improving resource management compared to manually creating threads.
// Concurrency: Multiple tasks are executed concurrently by the threads in the pool, showcasing efficient task handling.