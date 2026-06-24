package factory;

import shapes.Ellipse;
import shapes.Shape;

public class EllipseFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x, int y, int width, int height) {
        return new Ellipse(x, y, width, height);
    }

    @Override
    public String getShapeName() {
        return "Ellipse";
    }
}