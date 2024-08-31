package ac.asuha.coordinate;

public record Pos(Point point, float yaw, float pitch) {

    public Pos(double x, double y, double z, float yaw, float pitch) {
        this(new Point(x, y, z), yaw, pitch);
    }

    public double x() {
        return point.x();
    }

    public double y() {
        return point.y();
    }

    public double z() {
        return point.z();
    }

}
