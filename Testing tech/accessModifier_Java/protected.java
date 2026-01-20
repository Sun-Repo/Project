// ===================================================
// Package + Subclasses
// ===================================================
public class Vehicle {
    protected int speed;

    protected void showSpeed() {
        System.out.println("Speed: " + speed);
    }
}

public class Car extends Vehicle {
    public void setSpeed(int speed) {
        this.speed = speed;
        showSpeed();
    }
}
// ===================================================
// Usage
public class TestVehicle {
    public static void main(String[] args) {
        Car car = new Car();
        car.setSpeed(100);  // Allowed
    }
}
// ===================================================
// Best Practice:
// Inheritance scenarios
// Controlled access to subclasses
// Framework development
// ===================================================
