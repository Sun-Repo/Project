
// Covers end-to-end multithreading concepts
//  Uses modern, production-grade APIs (ExecutorService, Callable, Atomic)
// Includes real-world parallel test execution use case
// Clean, readable, and interview-friendly code

// ===================================
// start() method
// ===================================
Thread t = new Thread(() -> System.out.println("Running"));
t.start();
//Starts a new thread and calls run() internally 
// run() never calls new stack


// ===================================
// run() method
// ==================================
public void run() {
    System.out.println("Thread logic");
}
// Contains the logic to be executed in a separate thread
// performs one after another


// ===================================
// join() method
// ===================================
// Makes one thread wait for another to finish
t1.start();
t1.join(); // main waits for t1
// Ensures sequential execution when needed

// ===================================
// sleep() method
// ===================================
Thread.sleep(1000); // Sleep for 1 second
// Pauses the current thread for specified time
// Useful for simulating delays or waiting
// ===================================
// synchronized keyword
// ===================================  

public synchronized void synchronizedMethod() {
    // critical section
}   
// Ensures that only one thread can access the method at a time
// Prevents race conditions on shared resources     
// ===================================

// getName() / setName() methods
// ===================================  
t.setPriority(Thread.MAX_PRIORITY);
String threadName = t.getName();
// Sets or gets thread properties like name and priority    
// Helps in thread identification and scheduling
// ===================================
// yield() method
// ===================================
Thread.yield();
// Suggests the scheduler to give other threads a chance to run
// Useful for improving responsiveness in multithreaded apps
// ===================================
interrupt() method
// ===================================
t.interrupt();
// Signals a thread to stop its current operation   
// Allows for graceful thread termination
// ===================================  
wait() / notify() methods
// ===================================
synchronized(obj) {
    obj.wait(); // Thread waits
    obj.notify(); // Wakes up waiting thread
}       

// Used for inter-thread communication
// Enables threads to coordinate their actions
// ===================================
notify()All() method
// ===================================
synchronized(obj) {
    obj.notifyAll(); // Wakes up all waiting threads
}
// Used for inter-thread communication
// Enables multiple threads to coordinate their actions
// ===================================
synchronized (method / block)   
// ===================================
synchronized(this) {
    // critical section
}   
// Ensures mutual exclusion for the synchronized block
// Prevents race conditions on shared resources     
// ===================================
