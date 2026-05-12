import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.management.Notification;
import javax.xml.crypto.KeySelector.Purpose;

public class Multithreading {

    static void inefficientThreads(int n) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            new Thread(() -> sleep(50)).start();
        }
        Thread.sleep(500);
    }

    static void optimizedThreadPool(int n, int threadPoolSize) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < n; i++) {
            executor.submit(() -> sleep(50));
        }
        shutdownAndAwait(executor);
    }
/* ## Code Explanation — Optimized Thread Pool in Java

```java id="t1"
static void optimizedThreadPool(int tasks, int threadCount) 
        throws InterruptedException {

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    for (int i = 0; i < tasks; ++i) {
        executor.submit(() -> sleep(50L));
    }

    shutdownAndAwait(executor);
}
```

---

# Purpose of This Code

This method:

* Creates a fixed-size thread pool
* Submits multiple tasks
* Executes them concurrently
* Waits for completion

Used for:

* Parallel processing
* Background jobs
* API calls
* Batch processing

---

# Step-by-Step Explanation

---

# 1. Method Signature

```java id="t2"
static void optimizedThreadPool(int tasks, int threadCount)
```

## Parameters

| Parameter     | Meaning                   |
| ------------- | ------------------------- |
| `tasks`       | Number of jobs to execute |
| `threadCount` | Number of worker threads  |

Example:

```java id="t3"
optimizedThreadPool(1000, 10);
```

Means:

* 1000 tasks
* 10 threads processing simultaneously

---

# 2. Creating Thread Pool

```java id="t4"
ExecutorService executor =
        Executors.newFixedThreadPool(threadCount);
```

Creates reusable worker threads.

---

# Why Thread Pool?

Without thread pool:

```java id="t5"
new Thread(task).start();
```

Creating thousands of threads is expensive:

* Memory overhead
* CPU context switching
* Slow performance

---

# Thread Pool Benefit

Instead of:

```text id="t6"
1000 tasks = 1000 threads
```

We reuse:

```text id="t7"
1000 tasks = 10 reusable threads
```

Huge optimization.

---

# 3. Submitting Tasks

```java id="t8"
for (int i = 0; i < tasks; ++i) {
    executor.submit(() -> sleep(50L));
}
```

Each task:

* Sleeps for 50 milliseconds

Equivalent to:

```java id="t9"
Runnable task = () -> {
    sleep(50L);
};
```

---

# What Happens Internally?

Suppose:

```java id="t10"
tasks = 100
threadCount = 5
```

Execution:

```text id="t11"
Thread-1 -> Task 1
Thread-2 -> Task 2
Thread-3 -> Task 3
Thread-4 -> Task 4
Thread-5 -> Task 5
```

After completion:

```text id="t12"
Thread-1 -> Task 6
Thread-2 -> Task 7
...
```

Threads are reused.

---

# 4. shutdownAndAwait()

```java id="t13"
shutdownAndAwait(executor);
```

Usually implemented like:

```java id="t14"
static void shutdownAndAwait(ExecutorService executor)
        throws InterruptedException {

    executor.shutdown();

    executor.awaitTermination(1, TimeUnit.MINUTES);
}
```

---

# Why Important?

Without shutdown:

* JVM may not terminate
* Threads remain alive
* Memory leak possible

---

# Real-Time Example — Retail Domain

## Scenario: Order Processing

Suppose:

* 10,000 orders
* Each order requires:

  * Payment validation
  * Inventory check
  * Notification

Using thread pool:

```java id="t15"
ExecutorService executor =
        Executors.newFixedThreadPool(20);

for(Order order : orders) {
    executor.submit(() -> processOrder(order));
}
```

Benefits:

* Faster processing
* Better CPU utilization
* Controlled concurrency

---

# Banking Example

## Fraud Detection

Each transaction:

* Risk scoring
* Rule validation
* AML checks

Thread pool processes multiple transactions simultaneously.

---

# Interview Questions

---

## Q1: Why use `ExecutorService` instead of `Thread`?

Because:

* Thread reuse
* Better performance
* Queue management
* Controlled concurrency

---

## Q2: Difference between `submit()` and `execute()`?

| execute()           | submit()         |
| ------------------- | ---------------- |
| No return value     | Returns Future   |
| Cannot track result | Can track result |
| Simpler             | More flexible    |

---

## Q3: Why fixed thread pool?

```java id="t16"
Executors.newFixedThreadPool(n)
```

Advantages:

* Prevents unlimited thread creation
* Stable resource usage
* Better production safety

---

# Performance Insight

## Sequential Execution

```text id="t17"
1000 tasks × 50ms
= 50 seconds
```

---

## Parallel Execution (10 threads)

```text id="t18"
1000 / 10 = 100 batches

100 × 50ms
≈ 5 seconds
```

Massive speed improvement.

---

# Important Production Consideration

Avoid:

```java id="t19"
Executors.newCachedThreadPool()
```

in high-load systems because:

* Unlimited thread creation
* Can crash server

Preferred:

```java id="t20"
newFixedThreadPool()
```

---

# Advanced Interview Insight

## Thread Pool Internals

`ThreadPoolExecutor` contains:

* Worker threads
* Blocking queue
* Task scheduler
* Rejection policy

Tasks wait in queue when all threads are busy.

---

# Common Rejection Policies

| Policy              | Behavior            |
| ------------------- | ------------------- |
| AbortPolicy         | Throws exception    |
| CallerRunsPolicy    | Caller executes     |
| DiscardPolicy       | Silently discards   |
| DiscardOldestPolicy | Removes oldest task |

---

# One-Line Interview Answer

> `ExecutorService` improves performance by reusing a fixed number of worker threads instead of creating a new thread for every task.

================================================================================ */
    static List<String> filterPassingResults(List<String> list) {
        return list.stream()
                .filter(s -> s.startsWith("PASS"))
                .collect(Collectors.toList());
    }

    static List<Integer> findCommonValues(List<Integer> list1, List<Integer> list2) {
        Set<Integer> set = new HashSet<>(list2);
        List<Integer> common = new ArrayList<>();

        for (Integer value : list1) {
            if (set.contains(value)) {
                common.add(value);
            }
        }
        return common;
    }

    static String buildResultString(int count) {
        StringBuilder sb = new StringBuilder(count * 8);
        for (int i = 0; i < count; i++) {
            sb.append("test").append(i);
        }
        return sb.toString();
    }

    static void removeEmptyValues(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEmpty()) {
                iterator.remove();
            }
        }
    }

    static void processInBatches(List<String> hugeList, int batchSize, int threadPoolSize)
            throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (int i = 0; i < hugeList.size(); i += batchSize) {
            int start = i;
            int end = Math.min(i + batchSize, hugeList.size());
            executor.submit(() -> hugeList.subList(start, end).forEach(Multithreading::process));
        }

        shutdownAndAwait(executor);
    }

    static void process(String item) {
        if (item == null) {
            return;
        }
    }

    static void shutdownAndAwait(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }

    static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int taskCount = 100;

        long start = System.nanoTime();
        optimizedThreadPool(taskCount, 10);
        System.out.println("Optimized thread pool time: " + (System.nanoTime() - start));

        List<String> results = new ArrayList<>(List.of("PASS-login", "FAIL-search", "", "PASS-checkout"));
        System.out.println("Filtered results: " + filterPassingResults(results));

        removeEmptyValues(results);
        System.out.println("Cleaned results: " + results);

        List<Integer> common = findCommonValues(
                List.of(1, 2, 3, 4),
                List.of(3, 4, 5, 6));
        System.out.println("Common values: " + common);

        System.out.println("String length: " + buildResultString(1_000).length());
        processInBatches(results, 2, 2);

        // Notes:
        // Thread pools reuse workers, reducing memory and CPU overhead.
        // HashSet lookup reduces comparisons from O(n^2) to O(n).
        // Iterator.remove avoids ConcurrentModificationException during removal.
        // Thread pool size should be tuned based on CPU cores and workload type.
    }
}
