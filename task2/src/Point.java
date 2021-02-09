public class Point {
    private double x, y, z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double dotProduct(Point point) {
        return this.x * point.x + this.y * point.y + this.z + point.z;
    }

    public static Point vector(Point point1, Point point2) {
        return new Point(point2.x - point1.x, point2.y - point1.y, point2.z - point1.z);
    }

    @Override
    public String toString() {
        return "[" + x +
                ", " + y +
                ", " + z +
                "]";
    }
}
