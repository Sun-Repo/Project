// “The iterative approach eliminates redundant recursive calls and avoids stack usage by maintaining only the last two computed values, reducing both time and space complexity.”

public class FibonacciOptimization {

    // Naive recursive
    static long recursive(int n) {
        if (n <= 1) return n;
        return recursive(n - 1) + recursive(n - 2);
    }

    // Optimized iterative
    static long iterative(int n) {
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long temp = prev + curr;
            prev = curr;
            curr = temp;
        }
        return curr;
    }

    public static void main(String[] args) {
        int n = 40;

        long start = System.nanoTime();
        recursive(n);
        System.out.println("Recursive time: " + (System.nanoTime() - start));

        start = System.nanoTime();
        iterative(n);
        System.out.println("Iterative time: " + (System.nanoTime() - start));
    }
}
// -----------Test data generation
// Recursive
static long factorialRecursive(int n) {
    if (n == 1) return 1;
    return n * factorialRecursive(n - 1);
}

// Iterative (Optimized)
static long factorialIterative(int n) {
    long result = 1;
    for (int i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}
// --------Log & artifact cleanup
// Iterative using stack (DFS)
static long calculateSize(File root) {
    long size = 0;
    Stack<File> stack = new Stack<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        File file = stack.pop();
        if (file.isFile()) size += file.length();
        else stack.addAll(Arrays.asList(file.listFiles()));
    }
    return size;
}

// ------------Use Case: Searching test data efficiently
// Recursive
static int binarySearchRecursive(int[] arr, int l, int r, int x) {
    if (l <= r) {
        int mid = (l + r) / 2;
        if (arr[mid] == x) return mid;
        if (arr[mid] > x) return binarySearchRecursive(arr, l, mid - 1, x);
        return binarySearchRecursive(arr, mid + 1, r, x);
    }
    return -1;
}

// Iterative (Optimized)
static int binarySearchIterative(int[] arr, int x) {
    int l = 0, r = arr.length - 1;
    while (l <= r) {
        int mid = (l + r) / 2;
        if (arr[mid] == x) return mid;
        if (arr[mid] < x) l = mid + 1;
        else r = mid - 1;
    }
    return -1;
}
// -------------Use Case: Polling for AJAX content.
// Advantage: Avoids recursive waits, safe for long-running CI pipelines.
import org.openqa.selenium.WebElement;
import java.time.Duration;

public class WaitUtils {

    // Iterative wait for element
    public static boolean waitForElement(WebElement element, int timeoutSec) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeoutSec * 1000;
        while(System.currentTimeMillis() < endTime) {
            if(element.isDisplayed()) return true;
            Thread.sleep(500);
        }
        return false;
    }
}
// Data-Driven Iterative Login Test
// 

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            {"admin", "admin123"},
            {"user", "user123"}
        };
    }

    @Test(dataProvider = "loginData")
    public void iterativeLoginTest(String username, String password) throws InterruptedException {
        driver.get("https://example.com/login");
        WebElement userInput = driver.findElement(By.id("username"));
        WebElement passInput = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login"));

        // Iterative login input
        for (int i = 0; i < 3; i++) {
            userInput.clear();
            passInput.clear();
            userInput.sendKeys(username);
            passInput.sendKeys(password);
            loginBtn.click();
            if(driver.getCurrentUrl().contains("dashboard")) break;
        }

        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }
}
//Iterative Parallel Execution
// Safe thread management for parallel TestNG executions

import org.testng.annotations.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelExecutionTest {

    @Test
    public void runParallelTests() throws InterruptedException {
        Runnable test1 = () -> System.out.println("Test 1: " + Thread.currentThread().getId());
        Runnable test2 = () -> System.out.println("Test 2: " + Thread.currentThread().getId());
        Runnable test3 = () -> System.out.println("Test 3: " + Thread.currentThread().getId());

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(test1);
        executor.submit(test2);
        executor.submit(test3);
        executor.shutdown();
        while(!executor.isTerminated()) {}
    }
}

