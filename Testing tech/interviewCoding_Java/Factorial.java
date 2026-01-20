// recursion/Factorial.java
import java.util.function.Function;

public class Factorial {
    public static void main(String[] args) {
        Function<Integer, Integer> factorial = new Function<>() {
            @Override
            public Integer apply(Integer n) {
                return n <= 1 ? 1 : n * this.apply(n-1);
            }
        };
        System.out.println(factorial.apply(5));
    }
}