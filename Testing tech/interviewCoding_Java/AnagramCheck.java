// strings/AnagramCheck.java
import java.util.Arrays;

public class AnagramCheck {
    public static void main(String[] args) {
        String s1 = "listen";
        String s2 = "silent";
        boolean isAnagram = Arrays.equals(
                s1.chars().sorted().toArray(),
                s2.chars().sorted().toArray()
        );
        System.out.println("Anagram? " + isAnagram);
    }
}