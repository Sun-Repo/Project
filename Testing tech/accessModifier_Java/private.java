// =========================================
// Accessible Only Within the Same Class
// =========================================

public class Employee {
    private double salary;

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }
}
// ============================================================
// Usage
public class HR {
    public static void main(String[] args) {
        Employee emp = new Employee();
        // emp.salary = 50000; ❌ Not allowed
        emp.setSalary(50000);  // ✅ Allowed
    }
}
// ============================================================
// Best Practice:

// Sensitive data
// Encapsulation
// Prevent unauthorized access