import java.util.*;

public class ArrayListVsLinkedList {

    public static void main(String[] args) {
        int size = 1_000_000;

        // Naive: LinkedList random access
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) linkedList.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < size; i++) linkedList.get(i);
        System.out.println("LinkedList access time: " + (System.nanoTime() - start));

        // Optimized: ArrayList random access
        List<Integer> arrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) arrayList.add(i);

        start = System.nanoTime();
        for (int i = 0; i < size; i++) arrayList.get(i);
        System.out.println("ArrayList access time: " + (System.nanoTime() - start));
    }
}

// ==========================================================
// Explaination:

// for (int i = 0; i < size; i++) arrayList.get(i);
// ArrayList.get(i) Works

// “The optimization is choosing ArrayList over LinkedList for random access.
// LinkedList.get(i) is O(n), so iterating causes O(n²) time, while ArrayList.get(i) is O(1), making the loop O(n).
// Additionally, pre-sizing the ArrayList avoids resizing overhead. This significantly improves performance for read-heavy workloads like test result processing.”
// =================================================================
// Result


// Much faster execution

// Better memory locality

// CPU cache-friendly