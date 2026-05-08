import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
