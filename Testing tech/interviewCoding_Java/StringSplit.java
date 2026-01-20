// strings/StringSplit.java
public class StringSplit {
    public static void main(String[] args) {
        String str = "apple,banana,cherry";
        String[] parts = str.split(",");
        for (String part : parts) {
            System.out.println(part);
        }
    }
}