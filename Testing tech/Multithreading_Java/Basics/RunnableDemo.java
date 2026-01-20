
// Scenario: Simulate processing multiple bank transactions in parallel (implements Runnable).
package basics;

class BankTransaction implements Runnable {
    private String transactionId;
    private int amount;

    public BankTransaction(String transactionId, int amount) {
        this.transactionId = transactionId;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println("Processing transaction " + transactionId + " of amount $" + amount + " by " + Thread.currentThread().getName());
        try {
            Thread.sleep(500); // simulate processing time
        } catch (InterruptedException e) {
            System.out.println("Transaction " + transactionId + " interrupted!");
        }
        System.out.println("Completed transaction " + transactionId + " by " + Thread.currentThread().getName());
    }
}

public class RunnableDemo {
    public static void main(String[] args) {
        Runnable t1 = new BankTransaction("TXN001", 1000);
        Runnable t2 = new BankTransaction("TXN002", 2000);
        Runnable t3 = new BankTransaction("TXN003", 1500);

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}


// Runnable Interface Example:
// Implements Runnable to define a bank transaction task, showing a preferred, flexible way to create threads without extending Thread.

// Parallel Transaction Processing:
// Simulates multiple transactions running in parallel, demonstrating real-world banking operations handled concurrently.

// Simulated Task Execution:
// Uses Thread.sleep() to mimic processing time, illustrating asynchronous execution and giving recruiters a practical example of multithreading in QA/automation.