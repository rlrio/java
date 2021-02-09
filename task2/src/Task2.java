import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task2 {
    public static void main(String[] args) {
        try {
           intersection(args[0]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No arguments in command line.");
        }
    }

    public static void intersection(String file) {
        try {
            if (file == null) throw new MyExceptionTask2("No file available");
            String fileLine = getString(file);
            Sphere sphere = getSphere(fileLine);
            Point point1 = getLinePoints(fileLine).get(0);
            Point point2 = getLinePoints(fileLine).get(1);
            Point v = Point.vector(point1, point2);
            Point pSph = Point.vector(point1, sphere.getCenter());
            double a = v.dotProduct(v);
            double b = 2 * pSph.dotProduct(v);
            double c = pSph.dotProduct(pSph) - Math.pow(sphere.getRadius(), 2);
            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) {
                System.out.println("Коллизий не найдено");
            } else if (discriminant == 0) {
                double d = -b / 2;
                Point in = new Point(point1.getX() + d * v.getX(), point1.getY() + d * v.getY(), point1.getZ() + d * v.getZ());
                System.out.println(in);
            } else {
                double d1 = (-b - Math.sqrt(discriminant)) / (2 * a);
                double d2 = (-b + Math.sqrt(discriminant)) / (2 * a);
                Point in1 = new Point(point1.getX() + d1 * v.getX(), point1.getY() + d1 * v.getY(), point1.getZ() + d1 * v.getZ());
                Point in2 = new Point(point1.getX() + d2 * v.getX(), point1.getY() + d2 * v.getY(), point1.getZ() + d2 * v.getZ());
                System.out.println(in1);
                System.out.println(in2);
            }
        } catch (IndexOutOfBoundsException | MyExceptionTask2 e) {
            System.out.println(e.getMessage());
        }
    }
    private static String getString(String file) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new MyExceptionTask2("Cannot read file: " + file);
        }
    }
    private static Sphere getSphere(String s) {
        String[] split = s.split("},");
        String sph = null;
        for (String str: split) {
            if(str.contains("sphere")) {
                sph = str;
            }
        }
        Matcher m = Pattern.compile("\\[.*]").matcher(sph);
        String[] centerCoord;
        double radius = 0;
        Point center = null;
        if (m.find()) {
            centerCoord = m.group().replace("[", "").replace("]", "").split(", ");
            center = new Point(Double.parseDouble(centerCoord[0]),
                    Double.parseDouble(centerCoord[1]),
                    Double.parseDouble(centerCoord[2]));
        }
        Matcher match = Pattern.compile("radius: \\d+\\.?\\d*").matcher(sph);
        if(match.find()) {
            String t = match.group().replace("radius: ", "").trim();
            radius = Double.parseDouble(t);
        }
        return new Sphere(center, radius);
    }
    private static List<Point> getLinePoints(String s) {
        String[] split = s.split("},");
        String l = null;
        for (String str: split) {
            if(str.contains("line")) {
                l = str;
            }
        }
        l = l.replace("line:", "").replace("{", " ").replace("}", "").trim();
        String[] split2 = l.split("], ");
        split2[0].replace("[", "").trim();
        String[] point1Coord = split2[0].replace("[", "").trim().split(", ");
        String[] point2Coord = split2[1].replace("[", "")
                .replace("]", "")
                .trim().split(", ");
        Point point1 = new Point(Double.parseDouble(point1Coord[0]),
                Double.parseDouble(point1Coord[1]),
                Double.parseDouble(point1Coord[2]));
        Point point2 = new Point(Double.parseDouble(point2Coord[0]),
                Double.parseDouble(point2Coord[1]),
                Double.parseDouble(point2Coord[2]));
        List<Point> list = new ArrayList<>();
        list.add(point1);
        list.add(point2);
        return list;

    }
}
