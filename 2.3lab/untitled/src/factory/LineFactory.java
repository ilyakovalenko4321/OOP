package factory;

import shapes.Line;
import shapes.Shape;

public class LineFactory implements ShapeFactory {
    @Override
    public Shape createShape(int x1, int y1, int x2, int y2) {
        return new Line(x1, y1, x2, y2);
    }

    @Override
    public String getShapeName() {
        return "Line";
    }
}