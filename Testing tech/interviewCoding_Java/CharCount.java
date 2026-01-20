// strings/CharCount.java
import java.util.stream.Collectors;

public class CharCount {
    public static void main(String[] args) {
        String str = "java";
        str.chars()
           .mapToObj(c -> (char)c)
           .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
           .forEach((k,v) -> System.out.println(k + ": " + v));
    }
}