public class Square extends Rectangle {
    public Square(int x, int y, int side) {
        super(x, y, side, side);
    }

    @Override
    public String getDescription() {
        return "Square(" + x + ", " + y + ", " + width + ")";
    }
}