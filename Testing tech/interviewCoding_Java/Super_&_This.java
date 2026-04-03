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


// 🔹 1. this → Refers to Current Class Object
// Points to the current object instance
// Used when class variables and method parameters have the same name

// Example:

// class LoginPage {
//     String username;

//     LoginPage(String username){
//         this.username = username; // refers to current object
//     }
// }

// 👉 Interview Tip:
// “In automation frameworks, I use this to clearly distinguish instance variables in Page Object classes.”

// 🔹 2. super → Refers to Parent Class Object
// Used to access parent class variables and methods
// Important in inheritance-based frameworks

// Example:

// class BasePage {
//     void openBrowser(){
//         System.out.println("Opening browser");
//     }
// }

// class LoginPage extends BasePage {
//     void openBrowser(){
//         super.openBrowser(); // calling parent method
//         System.out.println("Login page setup");
//     }
// }

// 👉 Interview Tip:
// “I use super when extending base test classes to reuse setup/teardown logic.”

// 🔹 3. this() → Constructor Chaining (Same Class)
// Calls another constructor in the same class
// Helps avoid duplicate code

// Example:

// class TestConfig {
//     TestConfig(){
//         this("Chrome"); // calls parameterized constructor
//     }

//     TestConfig(String browser){
//         System.out.println(browser);
//     }
// }

// 👉 Use Case:
// Default configurations in test frameworks

// 🔹 4. super() → Constructor Chaining (Parent Class)
// Calls parent class constructor
// Must be the first statement in constructor

// Example:

// class BaseTest {
//     BaseTest(){
//         System.out.println("Base setup");
//     }
// }

// class LoginTest extends BaseTest {
//     LoginTest(){
//         super(); // calls BaseTest constructor
//         System.out.println("Login test setup");
//     }
// }

// 👉 Real-world QA Use:
// Initializing driver, environment, or reporting setup from base class

// 🔹 5. this.method() → Calls Current Class Method
// Explicitly calls method of same class
// Often used in method chaining

// Example:

// void step1(){
//     this.step2();
// }
// 🔹 6. super.method() → Calls Parent Method
// Used when method is overridden
// Helps extend parent behavior instead of replacing it

// Example:

// @Override
// void setup(){
//     super.setup(); // reuse base setup
//     System.out.println("Child setup");
// }
// Senior-Level Interview Summary (Best Answer)

// You can summarize like this:

// “this refers to the current object and is mainly used for variable resolution and constructor chaining within the same class. super refers to the parent class object and is used to access inherited behavior. In automation frameworks, especially with Page Object Model and Base Test classes, super() helps reuse setup logic, while this() avoids duplication. Using super.method() is critical when extending base functionality without breaking it.”
// Real QA Framework Mapping
// BaseTest → Driver setup (super())
// LoginPage → Uses this for elements
// super.method() → Reuse logging/reporting
// this() → Default browser configs