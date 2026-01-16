import java.util.*;

public class InefficientVsOptimizedLoop {

    public static void main(String[] args) {
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();

        for (int i = 0; i < 20_000; i++) {
            a.add(i);
            b.add(i);
        }

        // Naive O(n^2)
        long start = System.nanoTime();
        List<Integer> commonNaive = new ArrayList<>();
        for (Integer x : a) {
            for (Integer y : b) {
                if (x.equals(y)) commonNaive.add(x);
            }
        }
        System.out.println("Naive loop time: " + (System.nanoTime() - start));

        // Optimized O(n)
        start = System.nanoTime();
        Set<Integer> set = new HashSet<>(b);
        List<Integer> commonOptimized = new ArrayList<>();
        for (Integer x : a) {
            if (set.contains(x)) commonOptimized.add(x);
        }
        System.out.println("Optimized loop time: " + (System.nanoTime() - start));
    }
}
// ===================================================
// 2--use case: Duplicate test case detection

List<String> testIds = List.of("T1","T2","T1","T3");
List<String> duplicates = new ArrayList<>();

for (int i = 0; i < testIds.size(); i++) {
    for (int j = i + 1; j < testIds.size(); j++) {
        if (testIds.get(i).equals(testIds.get(j))) {
            duplicates.add(testIds.get(i));
        }
    }
}
// optimised

Set<String> seen = new HashSet<>();
Set<String> duplicates = new HashSet<>();

for (String id : testIds) {
    if (!seen.add(id)) {
        duplicates.add(id);
    }
}

// ============================================
// 3.Execution summary generation

int pass = 0, fail = 0;
for (String r : results) {
    if (r.equals("PASS")) pass++;
}
for (String r : results) {
    if (r.equals("FAIL")) fail++;
}
// optimised

int pass = 0, fail = 0;
for (String r : results) {
    if ("PASS".equals(r)) pass++;
    else if ("FAIL".equals(r)) fail++;
}
// =========================================
// 4.Iterating test steps efficiently

for (int i = 0; i < list.size(); i++) {
    process(list.get(i));
}

//optimised

int size = list.size();
for (int i = 0; i < size; i++) {
    process(list.get(i));
}


// ===================================================
// 5.use case: Log analysis
            // Selenium test runs

            // API logs

            // CI/CD execution logs

for (String log : logs) {
    if (log.contains("ERROR") && log.contains("DB")) {
        errors.add(log);
    }
}


// optimised

for (String log : logs) {
    if (log.indexOf("ERROR") != -1 && log.indexOf("DB") != -1) {
        errors.add(log);
    }
}

int errorIndex = log.indexOf("ERROR");
if (errorIndex != -1 && log.indexOf("DB", errorIndex) != -1) {
    errors.add(log);
}

// Means:

// “Capture only database-related errors, not UI or network noise.”
// “indexOf() searches for a substring and returns its position or -1 if not found.
// Using indexOf() != -1 allows efficient existence checks with short-circuiting.
// It’s often preferred in performance-sensitive loops like log processing because it avoids unnecessary overhead and repeated string scans.”