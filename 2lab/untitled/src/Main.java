import shapes.*;
import factory.*;
import shapes.Rectangle;
import shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    private ShapeList shapeList;
    private DrawingPanel drawingPanel;
    private JComboBox<String> shapeSelector;
    private JLabel statusLabel;
    private JTextField inputField1, inputField2, inputField3, inputField4;
    private Map<String, ShapeFactory> factoryRegistry;
    private int mouseX1, mouseY1;
    private boolean isDrawing;

    public Main() {
        setTitle("Graphic Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        shapeList = new ShapeList();
        initializeFactories();
        initializeUI();

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void initializeFactories() {
        factoryRegistry = new HashMap<>();
        factoryRegistry.put("Line", new LineFactory());
        factoryRegistry.put("Rectangle", new RectangleFactory());
        factoryRegistry.put("Square", new SquareFactory());
        factoryRegistry.put("Ellipse", new EllipseFactory());
        factoryRegistry.put("Circle", new CircleFactory());
        factoryRegistry.put("Triangle", new TriangleFactory());
    }

    private void initializeUI() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Shape:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        String[] shapeNames = factoryRegistry.keySet().toArray(new String[0]);
        shapeSelector = new JComboBox<>(shapeNames);
        controlPanel.add(shapeSelector, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(new JLabel("X:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        inputField1 = new JTextField(4);
        controlPanel.add(inputField1, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        controlPanel.add(new JLabel("Y:"), gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        inputField2 = new JTextField(4);
        controlPanel.add(inputField2, gbc);

        gbc.gridx = 6; gbc.gridy = 0;
        controlPanel.add(new JLabel("W/X2:"), gbc);

        gbc.gridx = 7; gbc.gridy = 0;
        inputField3 = new JTextField(4);
        controlPanel.add(inputField3, gbc);

        gbc.gridx = 8; gbc.gridy = 0;
        controlPanel.add(new JLabel("H/Y2:"), gbc);

        gbc.gridx = 9; gbc.gridy = 0;
        inputField4 = new JTextField(4);
        controlPanel.add(inputField4, gbc);

        JButton addButton = new JButton("Add Shape");
        addButton.addActionListener(e -> addShapeFromDialog());

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> {
            shapeList.clear();
            drawingPanel.repaint();
            updateStatus();
            shapeList.printAll();
        });

        JButton deleteButton = new JButton("Delete Last");
        deleteButton.addActionListener(e -> {
            if (shapeList.getCount() > 0) {
                shapeList.removeShape(shapeList.getCount() - 1);
                drawingPanel.repaint();
                updateStatus();
                shapeList.printAll();
            }
        });

        gbc.gridx = 10; gbc.gridy = 0;
        controlPanel.add(addButton, gbc);

        gbc.gridx = 11; gbc.gridy = 0;
        controlPanel.add(deleteButton, gbc);

        gbc.gridx = 12; gbc.gridy = 0;
        controlPanel.add(clearButton, gbc);

        statusLabel = new JLabel("Shapes: 0");
        gbc.gridx = 13; gbc.gridy = 0;
        controlPanel.add(statusLabel, gbc);

        JPanel instructionPanel = new JPanel();
        instructionPanel.setBackground(Color.LIGHT_GRAY);
        instructionPanel.add(new JLabel("Mouse drawing: Click and drag to draw shapes automatically!"));

        add(controlPanel, BorderLayout.NORTH);
        add(instructionPanel, BorderLayout.SOUTH);
    }

    private void addShapeFromDialog() {
        try {
            String selectedShape = (String) shapeSelector.getSelectedItem();
            ShapeFactory factory = factoryRegistry.get(selectedShape);

            int x = Integer.parseInt(inputField1.getText().isEmpty() ? "100" : inputField1.getText());
            int y = Integer.parseInt(inputField2.getText().isEmpty() ? "100" : inputField2.getText());
            int p3 = Integer.parseInt(inputField3.getText().isEmpty() ? "80" : inputField3.getText());
            int p4 = Integer.parseInt(inputField4.getText().isEmpty() ? "60" : inputField4.getText());

            Shape shape;
            if (selectedShape.equals("Triangle")) {
                TriangleFactory triFactory = (TriangleFactory) factory;
                shape = triFactory.createTriangle(x, y, p3, p4, (x + p3) / 2, y - 60);
            } else {
                shape = factory.createShape(x, y, p3, p4);
            }

            shapeList.addShape(shape);
            drawingPanel.repaint();
            updateStatus();
            shapeList.printAll();

            inputField1.setText("");
            inputField2.setText("");
            inputField3.setText("");
            inputField4.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus() {
        statusLabel.setText("Shapes: " + shapeList.getCount());
    }

    private class DrawingPanel extends JPanel {
        public DrawingPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(900, 600));

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    mouseX1 = e.getX();
                    mouseY1 = e.getY();
                    isDrawing = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDrawing) {
                        int mouseX2 = e.getX();
                        int mouseY2 = e.getY();
                        createShapeFromMouse(mouseX1, mouseY1, mouseX2, mouseY2);
                        isDrawing = false;
                        repaint();
                        updateStatus();
                        shapeList.printAll();
                    }
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        private void createShapeFromMouse(int x1, int y1, int x2, int y2) {
            String selectedShape = (String) shapeSelector.getSelectedItem();
            ShapeFactory factory = factoryRegistry.get(selectedShape);

            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);

            Shape shape = null;
            switch (selectedShape) {
                case "Line":
                    shape = factory.createShape(x1, y1, x2, y2);
                    break;
                case "Rectangle":
                    shape = factory.createShape(x, y, width, height);
                    break;
                case "Square":
                    int side = Math.min(width, height);
                    shape = factory.createShape(x, y, side, 0);
                    break;
                case "Ellipse":
                    shape = factory.createShape(x, y, width, height);
                    break;
                case "Circle":
                    int radius = Math.min(width, height) / 2;
                    int centerX = x + radius;
                    int centerY = y + radius;
                    shape = factory.createShape(centerX - radius, centerY - radius, radius, 0);
                    break;
                case "Triangle":
                    shape = new Triangle(x1, y1, x2, y2, (x1 + x2) / 2, y1 - 60);
                    break;
            }

            if (shape != null) {
                shapeList.addShape(shape);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Shape shape : shapeList.getAllShapes()) {
                if (shape instanceof Line) {
                    Line l = (Line) shape;
                    g.drawLine(l.getX(), l.getY(), l.getX2(), l.getY2());
                } else if (shape instanceof Rectangle) {
                    g.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                } else if (shape instanceof Square) {
                    g.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                } else if (shape instanceof Ellipse) {
                    g.drawOval(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                } else if (shape instanceof Circle) {
                    g.drawOval(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                } else if (shape instanceof Triangle) {
                    Triangle t = (Triangle) shape;
                    int[] xPoints = {t.getX1(), t.getX2(), t.getX3()};
                    int[] yPoints = {t.getY1(), t.getY2(), t.getY3()};
                    g.drawPolygon(xPoints, yPoints, 3);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}