package factory;

import shapes.Shape;
import shapes.Triangle;

public class TriangleFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x1, int y1, int x2, int y2) {
        return new Triangle(x1, y1, x2, y2, (x1 + x2) / 2, y1 - 60);
    }

    public Shape createTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        return new Triangle(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public String getShapeName() {
        return "Triangle";
    }
}