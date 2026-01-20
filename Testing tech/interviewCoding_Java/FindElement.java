// arrays/FindElement.java
import java.util.Arrays;
public class FindElement {
    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5};
        int target = 3;
        boolean found = Arrays.stream(arr).anyMatch(x -> x == target);
        System.out.println("Found? " + found);
    }
}