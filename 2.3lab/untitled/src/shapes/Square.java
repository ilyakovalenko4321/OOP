package shapes;

public class Square extends Rectangle {
    public Square(int x, int y, int side) {
        super(x, y, side, side);
    }

    public int getSide() { return width; }
}