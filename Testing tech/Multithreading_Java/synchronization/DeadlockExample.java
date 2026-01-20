package synchronization;

class BankAccount {
    private int balance = 1000;

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }
}

public class DeadlockExample {
    private static final BankAccount accountA = new BankAccount();
    private static final BankAccount accountB = new BankAccount();

    public static void transfer(BankAccount from, BankAccount to, int amount) {
        synchronized (from) {
            System.out.println(Thread.currentThread().getName() + " locked " + from);
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (to) {
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println(Thread.currentThread().getName() + " transferred $" + amount);
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> transfer(accountA, accountB, 100), "Teller-1");
        Thread t2 = new Thread(() -> transfer(accountB, accountA, 200), "Teller-2");

        t1.start();
        t2.start();
    }
}


// Explanation:This Java program demonstrates a classic deadlock scenario where two threads attempt to transfer money between two bank accounts but end up waiting indefinitely for each other to release locks on the accounts.
// Realistic Scenario: Simulates transferring money between two accounts using multiple threads (like multiple bank tellers).

// Deadlock Risk: If Teller-1 locks accountA and Teller-2 locks accountB simultaneously, both threads wait for the other account’s lock—classic deadlock.

// Learning Opportunity: Demonstrates why proper lock ordering or using tryLock() with timeout is essential in banking/finance systems for concurrency safety.