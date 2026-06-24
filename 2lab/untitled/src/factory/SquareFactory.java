package factory;

import shapes.Shape;
import shapes.Square;

public class SquareFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x, int y, int side, int dummy) {
        return new Square(x, y, side);
    }

    @Override
    public String getShapeName() {
        return "Square";
    }
}