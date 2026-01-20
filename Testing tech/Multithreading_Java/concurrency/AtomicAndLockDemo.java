package concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class AtomicAndLockDemo {

    // Atomic variable for thread-safe account balance
    private static AtomicInteger accountBalance = new AtomicInteger(1000);
    
    // Lock for performing multiple operations atomically
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        Runnable depositTask = () -> {
            for (int i = 0; i < 100; i++) {
                accountBalance.addAndGet(50); // atomic increment
            }
        };

        Runnable withdrawTask = () -> {
            for (int i = 0; i < 100; i++) {
                // Using lock for complex operation
                lock.lock();
                try {
                    if (accountBalance.get() >= 50) {
                        accountBalance.addAndGet(-50);
                    }
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread t1 = new Thread(depositTask, "DepositThread");
        Thread t2 = new Thread(withdrawTask, "WithdrawThread");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final Account Balance: " + accountBalance.get());
    }
}
// Explanation: This Java program demonstrates the use of AtomicInteger for thread-safe operations on a shared variable (account balance) and ReentrantLock for ensuring atomicity in a sequence of operations (withdrawal).

// AtomicInteger for thread-safe operations

// Each deposit directly updates the account balance atomically.

// No risk of race conditions when multiple threads deposit simultaneously.

// Shows lock-free thread safety, important in high-frequency banking transactions.

// 2️⃣ ReentrantLock for conditional withdrawal

// Withdrawals check balance before subtracting, which is a read-modify-write operation.

// Lock ensures consistency, preventing negative balances due to simultaneous withdrawals.

// Demonstrates fine-grained control vs. global synchronized blocks.

// 3️⃣ Realistic banking scenario

// Simulates concurrent deposits and withdrawals in a bank account.

// Useful for QA or Automation engineers to test parallel transactions.

// Recruiters can see understanding of both atomic variables and explicit locks in real-world systems.