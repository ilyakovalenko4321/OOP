public class Ellipse extends Shape {
    public Ellipse(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public String getDescription() {
        return "Ellipse(" + x + ", " + y + ", " + width + ", " + height + ")";
    }
}