// arrays/ArrayCopy.java
import java.util.Arrays;
public class ArraysCopy {
    public static void main(String[] args) {
        int[] source = {1,2,3,4,5};
        int[] dest = Arrays.copyOf(source, source.length);
        System.out.println(Arrays.toString(dest));
    }
}