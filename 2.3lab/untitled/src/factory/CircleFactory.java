package factory;

import shapes.Circle;
import shapes.Shape;

public class CircleFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x, int y, int radius, int dummy) {
        return new Circle(x, y, radius);
    }

    @Override
    public String getShapeName() {
        return "Circle";
    }
}