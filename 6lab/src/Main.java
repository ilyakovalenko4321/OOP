import factory.*;
import plugin.PluginLoader;
import plugin.ShapePainter;
import plugin.ShapePlugin;
import processing.ProcessingPluginLoader;
import processing.XmlProcessingPlugin;
import shapes.*;
import shapes.Rectangle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main extends JFrame {
    private ShapeList shapeList;
    private DrawingPanel drawingPanel;
    private JComboBox<String> shapeSelector;
    private JLabel statusLabel;
    private JTextField inputField1, inputField2, inputField3, inputField4;
    private Map<String, ShapeFactory> factoryRegistry;
    private Map<String, ShapePainter> painterRegistry;
    private Map<String, ShapePlugin> pluginRegistry;
    private List<XmlProcessingPlugin> activeProcessors;
    private int mouseX1, mouseY1;
    private boolean isDrawing;

    public Main() {
        setTitle("Graphic Editor - Lab 6");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 700);
        setLayout(new BorderLayout());

        shapeList = new ShapeList();
        activeProcessors = new ArrayList<>();
        initializeFactories();
        loadShapePlugins();
        initializeMenu();
        initializeUI();

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Registers built-in shapes and drawing functions.
    private void initializeFactories() {
        factoryRegistry = new LinkedHashMap<>();
        painterRegistry = new LinkedHashMap<>();
        pluginRegistry = new LinkedHashMap<>();
        registerBuiltIn("Line", new LineFactory(), (graphics, shape) -> {
            Line line = (Line) shape;
            graphics.drawLine(line.getX(), line.getY(), line.getX2(), line.getY2());
        });
        registerBuiltIn("Rectangle", new RectangleFactory(), (graphics, shape) ->
                graphics.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
        registerBuiltIn("Square", new SquareFactory(), (graphics, shape) ->
                graphics.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
        registerBuiltIn("Ellipse", new EllipseFactory(), (graphics, shape) ->
                graphics.drawOval(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
        registerBuiltIn("Circle", new CircleFactory(), (graphics, shape) ->
                graphics.drawOval(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
        registerBuiltIn("Triangle", new TriangleFactory(), (graphics, shape) -> {
            Triangle triangle = (Triangle) shape;
            graphics.drawPolygon(
                    new int[]{triangle.getX1(), triangle.getX2(), triangle.getX3()},
                    new int[]{triangle.getY1(), triangle.getY2(), triangle.getY3()},
                    3
            );
        });
    }

    private void registerBuiltIn(String name, ShapeFactory factory, ShapePainter painter) {
        factoryRegistry.put(name, factory);
        painterRegistry.put(name, painter);
    }

    // Adds shape plugins from the plugins folder.
    private void loadShapePlugins() {
        for (ShapePlugin plugin : new PluginLoader("plugins").loadShapePlugins()) {
            factoryRegistry.put(plugin.getShapeName(), plugin.getFactory());
            painterRegistry.put(plugin.getShapeName(), plugin.getPainter());
            pluginRegistry.put(plugin.getShapeName(), plugin);
        }
    }

    // Builds the Settings menu from functional plugins found in the processors folder.
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem saveItem = new JMenuItem("Save XML");
        saveItem.addActionListener(e -> saveToXml());
        JMenuItem loadItem = new JMenuItem("Load XML");
        loadItem.addActionListener(e -> loadFromXml());
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);

        for (XmlProcessingPlugin processor : new ProcessingPluginLoader("processors").loadPlugins()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(processor.getName());
            item.addActionListener(e -> {
                if (item.isSelected()) {
                    activeProcessors.add(processor);
                } else {
                    activeProcessors.remove(processor);
                }
            });
            settingsMenu.add(item);
        }
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    private void initializeUI() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabel(controlPanel, gbc, "Shape:", 0);
        shapeSelector = new JComboBox<>(factoryRegistry.keySet().toArray(new String[0]));
        addControl(controlPanel, gbc, shapeSelector, 1);
        addLabel(controlPanel, gbc, "X:", 2);
        inputField1 = new JTextField(4);
        addControl(controlPanel, gbc, inputField1, 3);
        addLabel(controlPanel, gbc, "Y:", 4);
        inputField2 = new JTextField(4);
        addControl(controlPanel, gbc, inputField2, 5);
        addLabel(controlPanel, gbc, "W/X2:", 6);
        inputField3 = new JTextField(4);
        addControl(controlPanel, gbc, inputField3, 7);
        addLabel(controlPanel, gbc, "H/Y2:", 8);
        inputField4 = new JTextField(4);
        addControl(controlPanel, gbc, inputField4, 9);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addShapeFromDialog());
        addControl(controlPanel, gbc, addButton, 10);
        JButton deleteButton = new JButton("Delete Last");
        deleteButton.addActionListener(e -> deleteLastShape());
        addControl(controlPanel, gbc, deleteButton, 11);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearShapes());
        addControl(controlPanel, gbc, clearButton, 12);
        statusLabel = new JLabel("Shapes: 0");
        addControl(controlPanel, gbc, statusLabel, 13);

        JPanel instructionPanel = new JPanel();
        instructionPanel.setBackground(Color.LIGHT_GRAY);
        instructionPanel.add(new JLabel("Settings contains native processors and adapted friend plugins."));
        add(controlPanel, BorderLayout.NORTH);
        add(instructionPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int x) {
        addControl(panel, gbc, new JLabel(text), x);
    }

    private void addControl(JPanel panel, GridBagConstraints gbc, Component component, int x) {
        gbc.gridx = x;
        gbc.gridy = 0;
        panel.add(component, gbc);
    }

    // Saves shape XML after all enabled processors transform it.
    private void saveToXml() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            String xml = serializeShapes();
            for (XmlProcessingPlugin processor : activeProcessors) {
                xml = processor.beforeSave(xml);
            }
            Files.write(chooser.getSelectedFile().toPath(), xml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Loads shape XML after all enabled processors transform it.
    private void loadFromXml() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            String xml = new String(Files.readAllBytes(chooser.getSelectedFile().toPath()), StandardCharsets.UTF_8);
            for (XmlProcessingPlugin processor : activeProcessors) {
                xml = processor.afterLoad(xml);
            }
            deserializeShapes(xml);
            afterShapeListChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converts the current shape list to XML so functional plugins can process one structure format.
    private String serializeShapes() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("canvas");
        document.appendChild(root);
        for (Shape shape : shapeList.getAllShapes()) {
            Element item = document.createElement("shape");
            item.setAttribute("type", detectShapeName(shape));
            if (shape instanceof Line) {
                Line line = (Line) shape;
                item.setAttribute("x", String.valueOf(line.getX()));
                item.setAttribute("y", String.valueOf(line.getY()));
                item.setAttribute("x2", String.valueOf(line.getX2()));
                item.setAttribute("y2", String.valueOf(line.getY2()));
            } else if (shape instanceof Triangle) {
                Triangle triangle = (Triangle) shape;
                item.setAttribute("x1", String.valueOf(triangle.getX1()));
                item.setAttribute("y1", String.valueOf(triangle.getY1()));
                item.setAttribute("x2", String.valueOf(triangle.getX2()));
                item.setAttribute("y2", String.valueOf(triangle.getY2()));
                item.setAttribute("x3", String.valueOf(triangle.getX3()));
                item.setAttribute("y3", String.valueOf(triangle.getY3()));
            } else {
                item.setAttribute("x", String.valueOf(shape.getX()));
                item.setAttribute("y", String.valueOf(shape.getY()));
                item.setAttribute("width", String.valueOf(shape.getWidth()));
                item.setAttribute("height", String.valueOf(shape.getHeight()));
            }
            root.appendChild(item);
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    // Rebuilds shape objects from XML using registered factories, including plugin factories.
    private void deserializeShapes(String xml) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        NodeList nodes = document.getDocumentElement().getElementsByTagName("shape");
        shapeList.clear();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element item = (Element) nodes.item(i);
            String type = item.getAttribute("type");
            Shape shape;
            if ("Line".equals(type)) {
                shape = new Line(readInt(item, "x"), readInt(item, "y"), readInt(item, "x2"), readInt(item, "y2"));
            } else if ("Triangle".equals(type)) {
                shape = new Triangle(readInt(item, "x1"), readInt(item, "y1"), readInt(item, "x2"),
                        readInt(item, "y2"), readInt(item, "x3"), readInt(item, "y3"));
            } else {
                ShapeFactory factory = factoryRegistry.get(type);
                if (factory == null) {
                    continue;
                }
                shape = factory.createShape(readInt(item, "x"), readInt(item, "y"),
                        readInt(item, "width"), readInt(item, "height"));
            }
            shapeList.addShape(shape);
        }
    }

    private int readInt(Element element, String name) {
        return Integer.parseInt(element.getAttribute(name));
    }

    private String detectShapeName(Shape shape) {
        if (shape instanceof Line) return "Line";
        if (shape instanceof Square) return "Square";
        if (shape instanceof Circle) return "Circle";
        if (shape instanceof Rectangle) return "Rectangle";
        if (shape instanceof Ellipse) return "Ellipse";
        if (shape instanceof Triangle) return "Triangle";
        return shape.toString().split("\\(")[0];
    }

    private void addShapeFromDialog() {
        try {
            String selectedShape = (String) shapeSelector.getSelectedItem();
            ShapeFactory factory = factoryRegistry.get(selectedShape);
            Shape shape = createShape(selectedShape, factory, parseField(inputField1, 100), parseField(inputField2, 100),
                    parseField(inputField3, 80), parseField(inputField4, 60));
            shapeList.addShape(shape);
            clearInputs();
            afterShapeListChanged();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseField(JTextField field, int defaultValue) {
        return Integer.parseInt(field.getText().isEmpty() ? String.valueOf(defaultValue) : field.getText());
    }

    private Shape createShape(String selectedShape, ShapeFactory factory, int x, int y, int p3, int p4) {
        if ("Triangle".equals(selectedShape)) {
            return ((TriangleFactory) factory).createTriangle(x, y, p3, p4, (x + p3) / 2, y - 60);
        }
        return factory.createShape(x, y, p3, p4);
    }

    private void deleteLastShape() {
        if (shapeList.getCount() > 0) {
            shapeList.removeShape(shapeList.getCount() - 1);
            afterShapeListChanged();
        }
    }

    private void clearShapes() {
        shapeList.clear();
        afterShapeListChanged();
    }

    private void afterShapeListChanged() {
        drawingPanel.repaint();
        statusLabel.setText("Shapes: " + shapeList.getCount());
        shapeList.printAll();
    }

    private void clearInputs() {
        inputField1.setText("");
        inputField2.setText("");
        inputField3.setText("");
        inputField4.setText("");
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
                        createShapeFromMouse(mouseX1, mouseY1, e.getX(), e.getY());
                        isDrawing = false;
                        afterShapeListChanged();
                    }
                }
            };
            addMouseListener(mouseAdapter);
        }

        // Creates built-in and plugin shapes from mouse drag coordinates.
        private void createShapeFromMouse(int x1, int y1, int x2, int y2) {
            String selectedShape = (String) shapeSelector.getSelectedItem();
            ShapeFactory factory = factoryRegistry.get(selectedShape);
            ShapePlugin plugin = pluginRegistry.get(selectedShape);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            Shape shape;
            if (plugin != null) shape = plugin.createFromMouse(x1, y1, x2, y2);
            else if ("Line".equals(selectedShape)) shape = factory.createShape(x1, y1, x2, y2);
            else if ("Square".equals(selectedShape)) shape = factory.createShape(x, y, Math.min(width, height), 0);
            else if ("Circle".equals(selectedShape)) shape = factory.createShape(x, y, Math.min(width, height) / 2, 0);
            else if ("Triangle".equals(selectedShape)) shape = new Triangle(x1, y1, x2, y2, (x1 + x2) / 2, y1 - 60);
            else shape = factory.createShape(x, y, width, height);
            shapeList.addShape(shape);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            for (Shape shape : shapeList.getAllShapes()) {
                ShapePainter painter = painterRegistry.get(detectShapeName(shape));
                if (painter != null) {
                    painter.paint(graphics, shape);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
