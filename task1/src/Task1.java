import java.math.BigInteger;
import java.util.Arrays;

public class Task1 {
    public static void main(String[] args) {
        try {
            if (args.length == 3) {
                System.out.println(iToBase(args[0], args[1], args[2]));
            } else if (args.length == 2) {
                System.out.println(iToBase(Integer.parseInt(args[0]), args[1]));
            } else throw new MyExceptionTask1("You should enter two or three arguments. You put: " + args.length);
        } catch (MyExceptionTask1 e) {
            System.err.println(e.getMessage());
            if (args.length != 0) Arrays.stream(args).forEach(System.out::println);
        }
    }

    public static int parseBase(String base) {
        if (base.startsWith("0") && !base.contains(" ") && base.length() < 37) {
            char[] code = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
            char[] arr = base.toLowerCase().toCharArray();
            int count = 0;
            for (int i = 0; i < arr.length; i++) {
                if (code[i] == arr[i]) {
                    count++;
                } else throw new MyExceptionTask1("Invalid input of base: " + base);
            }
            return count;
        } else if (base.toLowerCase().contains("котик")) return 9; //Have you heard that cats have 9 lives?
        else throw new MyExceptionTask1("Invalid input of base: " + base);
    }

    public static String iToBase(int i, String base) {
        BigInteger num = new BigInteger(String.valueOf(i));
        int dstBase = parseBase(base);
        return num.toString(dstBase);
    }

    public static String iToBase(String nb, String baseSrc, String baseDst) {
        int srcBase = parseBase(baseSrc);
        int dstBase = parseBase(baseDst);
        try {
            BigInteger num = new BigInteger(nb, srcBase);
            return num.toString(dstBase);
        } catch (NumberFormatException e) {
            throw new MyExceptionTask1("Invalid input of number: " + nb);
        }
    }
}
