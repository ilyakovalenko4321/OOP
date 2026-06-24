package factory;

import shapes.Rectangle;
import shapes.Shape;

public class RectangleFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x, int y, int width, int height) {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String getShapeName() {
        return "Rectangle";
    }
}