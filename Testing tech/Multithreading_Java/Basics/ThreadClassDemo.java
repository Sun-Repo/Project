



// Simulate bank account balance update using multiple threads (extends Thread).package basics;

class BankAccount {
    private int balance;

    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(int amount) {
        balance += amount;
        System.out.println(Thread.currentThread().getName() + " deposited " + amount + ", Balance: " + balance);
    }

    public void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ", Balance: " + balance);
        } else {
            System.out.println(Thread.currentThread().getName() + " tried to withdraw " + amount + " but insufficient funds! Balance: " + balance);
        }
    }

    public int getBalance() {
        return balance;
    }
}

class DepositThread extends Thread {
    private BankAccount account;
    private int amount;

    public DepositThread(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        account.deposit(amount);
    }
}

class WithdrawThread extends Thread {
    private BankAccount account;
    private int amount;

    public WithdrawThread(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        account.withdraw(amount);
    }
}

public class ThreadClassDemo {
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount(1000);

        Thread t1 = new DepositThread(account, 500);
        Thread t2 = new WithdrawThread(account, 200);
        Thread t3 = new WithdrawThread(account, 800);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Final Balance: " + account.getBalance());
    }
}


// Explanation of methods used in multithreading:
// Thread Inheritance Example:
// Uses classes that extend Thread (DepositThread and WithdrawThread) to simulate bank account operations, showing how multiple threads can run concurrently.

// Shared Resource Management:
// Threads operate on a single BankAccount object, demonstrating race conditions and the importance of synchronization in real-world banking scenarios.

// Thread Coordination:
// Uses start() to run threads and join() to ensure the main thread waits for all transactions to complete before printing the final balance.