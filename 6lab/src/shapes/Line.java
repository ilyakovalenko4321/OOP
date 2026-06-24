package shapes;

public class Line extends Shape {
    public Line(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2 - x1, y2 - y1);
    }

    public int getX2() { return x + width; }
    public int getY2() { return y + height; }
}