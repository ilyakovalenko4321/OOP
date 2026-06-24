package factory;

import shapes.Shape;

public interface ShapeFactory {
    Shape createShape(int p1, int p2, int p3, int p4);
    String getShapeName();
}