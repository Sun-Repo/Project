// hashmaps_sets/FindDuplicates.java
import java.util.*;
public class FindDuplicates {
    public static void main(String[] args){
        int[] arr = {1,2,3,2,4,3};
        Set<Integer> uniques = new HashSet<>();
        Set<Integer> duplicates = new HashSet<>();
        Arrays.stream(arr).forEach(n -> {
            if(!uniques.add(n)) duplicates.add(n);
        });
        System.out.println("Duplicates: " + duplicates);
    }
}
