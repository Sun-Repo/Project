package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class LoanApplication {
    String applicant;
    double amount;

    LoanApplication(String applicant, double amount) {
        this.applicant = applicant;
        this.amount = amount;
    }
}

public class LoanApprovalCallableDemo {
    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Simulate loan approval task
        Callable<String> loanTask = () -> {
            LoanApplication app = new LoanApplication("Alice", 50000);
            System.out.println("Processing loan for: " + app.applicant);
            Thread.sleep(2000); // simulate verification
            return app.amount <= 100000 ? "Approved" : "Rejected";
        };

        Future<String> approvalFuture = executor.submit(loanTask);

        System.out.println("Checking loan status...");
        String status = approvalFuture.get(); // blocking until result ready
        System.out.println("Loan Status: " + status);

        executor.shutdown();
    }
}

// Explanation:This Java program demonstrates the use of Callable and Future to process a loan application asynchronously.
// Domain Relevance: Shows async processing of bank loans—realistic business scenario.

// Parallel Processing: Can easily scale to multiple loan applications using a thread pool.

// Callable/Future Usage: Returns result to main thread after background processing completes, mimicking real banking approval workflows.