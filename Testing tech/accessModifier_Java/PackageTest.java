// ===============================
// Package Level Access Modifier
// ===============================

class PackageService {
    void processOrder() {
        System.out.println("Order processed");
    }
}
// ============================================================
// Usage-Not accessible from another package
public class PackageTest {
    public static void main(String[] args) {
        PackageService ps = new PackageService();
        ps.processOrder();  // Allowed (same package)
    }
}
// ============================================================
// Best Practice:
// Internal modules
// Not for public APIs
// Restrict access within package

// ===============================