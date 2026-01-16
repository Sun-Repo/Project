for (int i = 0; i < n; i++) {
    new Thread(() -> { Thread.sleep(50); }).start();
}
Thread.sleep(5000);
// Problems:

// Creates 100 threads → high memory overhead.

// Thread creation is expensive → more CPU time.

// Threads are unmanaged → hard to control lifecycle.

// Thread.sleep(5000) is arbitrary wait → may waste time if tasks finish earlier.
// ---Optimized

// Thread Pool Reuse: Only 10 threads are created; they are reused for 100 tasks → reduced memory & CPU overhead.

// Managed Execution: ExecutorService handles thread lifecycle → clean shutdown.

// Predictable Parallelism: You can control concurrency with threadPoolSize.

// Faster Execution: Threads start processing immediately, no extra waits.

ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < n; i++) {
    executor.submit(() -> { Thread.sleep(50); });
}
executor.shutdown();
while (!executor.isTerminated()) {}
// --------------------------------------------
// Filtering large collections
List<String> list = new ArrayList<>();
// add 1M elements
List<String> filtered = new ArrayList<>();
for (String s : list) {
    if (s.startsWith("PASS")) {
        filtered.add(s);
    }
}
// --Optimized
// Streams use internal iteration → often faster.

// Cleaner code and potential for parallel streams with .parallelStream().

List<String> filtered = list.stream()
    .filter(s -> s.startsWith("PASS"))
    .toList();
// ---------------------------------------------------------
// Avoid nested loops for lookup
for (Integer a : list1) {
    for (Integer b : list2) {
        if (a.equals(b)) common.add(a);
    }
}
// Optimized
// Reduces O(n²) → O(n).

Critical for comparing test datasets efficiently.
Set<Integer> set = new HashSet<>(list2);
for (Integer a : list1) {
    if (set.contains(a)) common.add(a);
}
// =----------------------------------------------
// String concatenation in loops
String result = "";
for (int i = 0; i < 100_000; i++) {
    result += "test" + i;
}
// --Optimized
// Avoids creating 100,000 intermediate String objects.

// Reduces memory usage and GC pressure.
StringBuilder sb = new StringBuilder(100_000 * 4);
for (int i = 0; i < 100_000; i++) {
    sb.append("test").append(i);
}
String result = sb.toString();
// ==============================================================
// Removing items from list during iteration
for (String s : list) {
    if (s.isEmpty()) list.remove(s);
}
// optimized
for (String s : list) {
    if (s.isEmpty()) list.remove(s);
}
// alternative
list = list.stream().filter(s -> !s.isEmpty()).toList();
// ---------------------------------------------------------------------
// Batch processing / chunking
for (String item : hugeList) {
    process(item);
}
// --Optimized
int batchSize = 1000;
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < hugeList.size(); i += batchSize) {
    int start = i;
    int end = Math.min(i + batchSize, hugeList.size());
    executor.submit(() -> hugeList.subList(start, end).forEach(JavaIteration::process));
}
executor.shutdown();
while (!executor.isTerminated()) {}
// ===================================================================
// highlights>
// Filtering collections: Use HashSet.contains() instead of nested loops → reduces O(n²) → O(n).

// String concatenation: Use StringBuilder instead of + inside loops → avoids creating millions of String objects.

// Streams: Use .stream() or .parallelStream() for filtering, mapping, or summing large collections.

// Batch processing: Divide a huge list into chunks and submit them to an ExecutorService → better memory usage + parallelism.

// Iterator for remove: Use Iterator.remove() instead of modifying list in a for-each loop → avoids ConcurrentModificationException.
// Q: Why 10 threads?

// “I chose 10 based on expected CPU cores and workload. Too few → tasks wait; too many → memory overhead.”

// Q: What if tasks are CPU-intensive?

// “We could use ForkJoinPool or tune thread pool size based on CPU cores. For I/O-heavy tasks, larger pool is okay.”

// Q: How do you measure performance?

// “Using System.nanoTime() or micro-benchmarking libraries like JMH, comparing naive vs optimized execution time.”

// Q: How does this tie to QA?

// “This ensures parallel test execution, faster report processing, and stable pipelines — which directly improves release quality and speed.”