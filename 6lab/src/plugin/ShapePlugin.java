package plugin;

import factory.ShapeFactory;
import shapes.Shape;

public interface ShapePlugin {
    // Returns the visible name used in the shape selector.
    String getShapeName();

    // Returns a factory that creates shapes from four numeric parameters.
    ShapeFactory getFactory();

    // Returns the drawing strategy for shapes created by this plugin.
    ShapePainter getPainter();

    // Converts a mouse drag into a shape with the same contract as built-in shapes.
    default Shape createFromMouse(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        return getFactory().createShape(x, y, width, height);
    }
}
