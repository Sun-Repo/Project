package executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BankTransaction implements Runnable {
    private String account;
    private double amount;

    BankTransaction(String account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println("Processing $" + amount + " for account " 
                           + account + " on " + Thread.currentThread().getName());
        try { Thread.sleep(500); } catch (InterruptedException e) { }
    }
}

public class BankTransactionExecutor {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Simulate transactions for multiple accounts
        executor.submit(new BankTransaction("ACC123", 1500));
        executor.submit(new BankTransaction("ACC456", 2500));
        executor.submit(new BankTransaction("ACC789", 500));
        executor.submit(new BankTransaction("ACC123", -200)); // withdrawal
        executor.submit(new BankTransaction("ACC456", 1000));

        executor.shutdown();
    }
}


// Explanation
// Parallel Transaction Processing – Multiple account operations run concurrently, reducing wait times and improving system throughput.

// Thread Safety Consideration – In real systems, each BankTransaction would synchronize updates to the same account to avoid race conditions.

// Real-World Use Case – Mimics backend banking systems handling multiple deposits/withdrawals simultaneously, similar to high-performance financial applications.