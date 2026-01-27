import java.util.stream.Stream;

public class FibonacciLambda {
    public static void main(String[] args) {

        int n = 10;

        Stream.iterate(new int[]{0, 1},
                f -> new int[]{f[1], f[0] + f[1]})
              .limit(n)
              .map(f -> f[0])
              .forEach(System.out::println);
    }
}

// Output:
// 0
// 1
// 1
// 2
// 3
// 5
// 8
// 13
// 21
// 34

// take away:
//  Using Java Streams to generate Fibonacci series in a functional style.
// Uses lambda + mutable state- not functional programming purely
// Simple Lambda Recursion Is Not Used because Java Lambdas Cannot Refer To Themselves Directly.
