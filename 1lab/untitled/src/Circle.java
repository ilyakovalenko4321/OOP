public class Circle extends Ellipse {
    public Circle(int x, int y, int radius) {
        super(x, y, radius * 2, radius * 2);
    }

    @Override
    public String getDescription() {
        return "Circle(" + x + ", " + y + ", " + (width / 2) + ")";
    }
}