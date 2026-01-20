public class Bank {
    public int interestRate = 5;

    public void displayRate() {
        System.out.println("Interest Rate: " + interestRate);
    }
}
// ============================================================
// Usage
public class Customer {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.displayRate();   // Allowed
    }
}

// ============================================================
Best Practice:
APIs
Utility methods
Features intended for all users

