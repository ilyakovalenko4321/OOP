public class Rectangle extends Shape {
    public Rectangle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public String getDescription() {
        return "Rectangle(" + x + ", " + y + ", " + width + ", " + height + ")";
    }
}