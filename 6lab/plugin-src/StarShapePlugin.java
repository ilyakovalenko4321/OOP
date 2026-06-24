import factory.ShapeFactory;
import plugin.ShapePainter;
import plugin.ShapePlugin;
import shapes.Shape;

import java.awt.Graphics;

public class StarShapePlugin implements ShapePlugin {
    @Override
    public String getShapeName() {
        return "Star";
    }

    @Override
    public ShapeFactory getFactory() {
        return new StarFactory();
    }

    @Override
    public ShapePainter getPainter() {
        return (graphics, shape) -> {
            StarShape star = (StarShape) shape;
            graphics.drawPolygon(star.getXPoints(), star.getYPoints(), star.getPointCount());
        };
    }

    private static class StarFactory implements ShapeFactory {
        // Creates a star inside the rectangle passed by the main program.
        @Override
        public Shape createShape(int x, int y, int width, int height) {
            int size = Math.max(20, Math.min(Math.abs(width), Math.abs(height)));
            return new StarShape(x, y, size, size);
        }

        @Override
        public String getShapeName() {
            return "Star";
        }
    }

    private static class StarShape extends Shape {
        public StarShape(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        // Builds ten alternating outer and inner points for a five-point star.
        public int[] getXPoints() {
            int[] points = new int[getPointCount()];
            fillPoints(points, null);
            return points;
        }

        public int[] getYPoints() {
            int[] points = new int[getPointCount()];
            fillPoints(null, points);
            return points;
        }

        public int getPointCount() {
            return 10;
        }

        @Override
        public String toString() {
            return "Star(" + getX() + ", " + getY() + ", " + getWidth() + ", " + getHeight() + ")";
        }

        // Calculates polygon points lazily so the plugin does not store drawing-only state.
        private void fillPoints(int[] xPoints, int[] yPoints) {
            int centerX = getX() + getWidth() / 2;
            int centerY = getY() + getHeight() / 2;
            int outerRadius = Math.max(10, Math.min(getWidth(), getHeight()) / 2);
            int innerRadius = outerRadius / 2;
            for (int i = 0; i < getPointCount(); i++) {
                double angle = Math.toRadians(-90 + i * 36);
                int radius = i % 2 == 0 ? outerRadius : innerRadius;
                if (xPoints != null) {
                    xPoints[i] = centerX + (int) Math.round(Math.cos(angle) * radius);
                }
                if (yPoints != null) {
                    yPoints[i] = centerY + (int) Math.round(Math.sin(angle) * radius);
                }
            }
        }
    }
}
