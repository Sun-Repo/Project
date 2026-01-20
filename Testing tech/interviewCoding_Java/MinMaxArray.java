// arrays/MinMaxArray.java
import java.util.Arrays;

 public class MinMaxArray {
       public static void main(String[] args) {
        int[] arr = {10, 5, 20, 8};
        int min = Arrays.stream(arr).min().getAsInt();
        int max = Arrays.stream(arr).max().getAsInt();
        System.out.println("Min: " + min + ", Max: " + max);
    }
}