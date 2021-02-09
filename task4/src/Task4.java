import java.util.regex.Pattern;

public class Task4 {
    public static void main(String[] args) {
        try {
            compareStrings(args[0], args[1]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No arguments in command line.");
        }
    }

    public static void compareStrings(String s1, String s2) {
        s2 = s2.replace(".", "\\.").replace("*", ".+?");
        if (Pattern.compile(s2).matcher(s1).find()) System.out.println("ОК");
        else System.out.println("КО");
    }
}