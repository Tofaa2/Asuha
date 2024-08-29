package ac.asuha.coordinate;

public record Point(double x, double y, double z) {

    public int blockX() {
        return (int) Math.abs(x);
    }

    public int blockY() {
        return (int) Math.abs(y);
    }

    public int blockZ() {
        return (int) Math.abs(z);
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y, z + other.z);
    }

    public Point add(double x, double y, double z) {
        return new Point(x + this.x, y + this.y, z + this.z);
    }

    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y, z - other.z);
    }

    public Point subtract(double x, double y, double z) {
        return new Point(x - this.x, y - this.y, z - this.z);
    }


}
