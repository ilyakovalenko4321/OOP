package shapes;

public class Circle extends Ellipse {
    public Circle(int x, int y, int radius) {
        super(x, y, radius * 2, radius * 2);
    }

    public int getRadius() { return width / 2; }
}