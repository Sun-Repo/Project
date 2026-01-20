// strings/ReverseString.java
public class ReverseString {
    public static void main(String[] args) {
        String str = "Java";
        String reversed = new StringBuilder(str).reverse().toString();
        System.out.println(reversed);
    }
}