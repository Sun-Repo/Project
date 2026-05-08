import java.io.File;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// The iterative approach eliminates redundant recursive calls and avoids stack usage
// by maintaining only the last two computed values.
public class Iteration {

    // Naive recursive Fibonacci.
    static long fibonacciRecursive(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    // Optimized iterative Fibonacci.
    static long fibonacciIterative(int n) {
        if (n <= 1) {
            return n;
        }

        long prev = 0;
        long curr = 1;
        for (int i = 2; i <= n; i++) {
            long temp = prev + curr;
            prev = curr;
            curr = temp;
        }
        return curr;
    }

    static long factorialRecursive(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    static long factorialIterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // Iterative DFS for log and artifact cleanup calculations.
    static long calculateSize(File root) {
        if (root == null || !root.exists()) {
            return 0;
        }

        long size = 0;
        Stack<File> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            File file = stack.pop();
            if (file.isFile()) {
                size += file.length();
            } else {
                File[] children = file.listFiles();
                if (children != null) {
                    stack.addAll(Arrays.asList(children));
                }
            }
        }
        return size;
    }

    static int binarySearchRecursive(int[] arr, int left, int right, int x) {
        if (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == x) {
                return mid;
            }
            if (arr[mid] > x) {
                return binarySearchRecursive(arr, left, mid - 1, x);
            }
            return binarySearchRecursive(arr, mid + 1, right, x);
        }
        return -1;
    }

    static int binarySearchIterative(int[] arr, int x) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == x) {
                return mid;
            }
            if (arr[mid] < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    // Simple stand-in for Selenium's WebElement so this example compiles without external jars.
    interface Displayable {
        boolean isDisplayed();
    }

    static boolean waitForElement(Displayable element, int timeoutSec) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeoutSec * 1000L;
        while (System.currentTimeMillis() < endTime) {
            if (element.isDisplayed()) {
                return true;
            }
            Thread.sleep(500);
        }
        return false;
    }

    static void runParallelTasks() {
        Runnable test1 = () -> System.out.println("Test 1: " + Thread.currentThread().getId());
        Runnable test2 = () -> System.out.println("Test 2: " + Thread.currentThread().getId());
        Runnable test3 = () -> System.out.println("Test 3: " + Thread.currentThread().getId());

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(test1);
        executor.submit(test2);
        executor.submit(test3);
        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.yield();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int n = 40;

        long start = System.nanoTime();
        long recursiveResult = fibonacciRecursive(n);
        System.out.println("Recursive Fibonacci result: " + recursiveResult);
        System.out.println("Recursive time: " + (System.nanoTime() - start));

        start = System.nanoTime();
        long iterativeResult = fibonacciIterative(n);
        System.out.println("Iterative Fibonacci result: " + iterativeResult);
        System.out.println("Iterative time: " + (System.nanoTime() - start));

        System.out.println("Factorial iterative result: " + factorialIterative(5));
        System.out.println("Binary search result: " + binarySearchIterative(new int[] {1, 3, 5, 7, 9}, 7));
        System.out.println("Wait result: " + waitForElement(() -> true, 1));

        runParallelTasks();
    }
}
