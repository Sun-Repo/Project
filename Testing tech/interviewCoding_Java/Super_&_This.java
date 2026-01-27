// THIS keyword refers to the current instance of the class.
class Product {
    int price;

    Product(int price) {
        this.price = price;
    }
}
// Example of THIS keyword

class ThisExample {
    int value;

    ThisExample(int value) {
        this.value = value; // 'this' differentiates between instance variable and parameter
    }

    void display() {
        System.out.println("Value: " + this.value); // 'this' refers to the current instance variable
    }

    public static void main(String[] args) {
        ThisExample example = new ThisExample(10);
        example.display();
    }
}
// Scenario demonstrating THIS keyword in constructor chaining
class Box {
    int length, width, height;

    Box(int length, int width, int height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    Box(int size) {
        this(size, size, size); // 'this' calls another constructor in the same class
    }

    void displayDimensions() {
        System.out.println("Length: " + length + ", Width: " + width + ", Height: " + height);
    }

    public static void main(String[] args) {
        Box box1 = new Box(10, 20, 30);
        box1.displayDimensions();


        Box box2 = new Box(15);
        box2.displayDimensions();
    }
}

// Output:
// Length: 10, Width: 20, Height: 30
// Length: 15, Width: 15, Height: 15

// =========================================================================================
// SUPER keyword refers to the Access Parent Variable.
class Item {
    int price = 100;
}

class DiscountedItem extends Item {
    int price = 80;

    void showPrice() {
        System.out.println(super.price);
    }
}

// SUPER keyword refers to the Access Parent Variable
    class User {
    User(String name) {
        System.out.println("User: " + name);
    }
}

class Customer extends User {
    Customer() {
        super("Guest");
    }
}


// takee away

// this → current object, same class
// super → parent object, inherited class
// this() → constructor chaining (same class)
// super() → constructor chaining (parent class)
// this.method() → current override
// super.method() → parent implementation


