public class Line extends Shape {
    public Line(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2 - x1, y2 - y1);
    }

    @Override
    public String getDescription() {
        return "Line(" + x + ", " + y + ", " + (x + width) + ", " + (y + height) + ")";
    }
}