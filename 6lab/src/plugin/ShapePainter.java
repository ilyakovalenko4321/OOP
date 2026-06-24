package plugin;

import shapes.Shape;

import java.awt.Graphics;

public interface ShapePainter {
    // Draws one shape instance on the Swing drawing surface.
    void paint(Graphics graphics, Shape shape);
}
