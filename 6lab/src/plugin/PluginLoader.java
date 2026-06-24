package plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginLoader {
    private final File pluginDirectory;

    public PluginLoader(String directoryName) {
        this.pluginDirectory = new File(directoryName);
    }

    // Loads all compiled classes from the plugin folder and keeps only ShapePlugin implementations.
    public List<ShapePlugin> loadShapePlugins() {
        List<ShapePlugin> plugins = new ArrayList<>();
        if (!pluginDirectory.exists() || !pluginDirectory.isDirectory()) {
            return plugins;
        }

        List<File> classFiles = new ArrayList<>();
        collectClassFiles(pluginDirectory, classFiles);
        try {
            URL[] urls = {pluginDirectory.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
            for (File classFile : classFiles) {
                String className = toClassName(classFile);
                Class<?> candidateClass = classLoader.loadClass(className);
                if (ShapePlugin.class.isAssignableFrom(candidateClass)) {
                    plugins.add((ShapePlugin) candidateClass.getDeclaredConstructor().newInstance());
                }
            }
        } catch (Exception ex) {
            System.out.println("Plugin loading error: " + ex.getMessage());
        }
        return plugins;
    }

    // Recursively finds .class files because plugin packages create nested folders.
    private void collectClassFiles(File directory, List<File> result) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                collectClassFiles(file, result);
            } else if (file.getName().endsWith(".class")) {
                result.add(file);
            }
        }
    }

    // Converts a class file path to a Java binary class name.
    private String toClassName(File classFile) {
        String root = pluginDirectory.getAbsolutePath() + File.separator;
        String relativePath = classFile.getAbsolutePath().substring(root.length());
        return relativePath.replace(File.separatorChar, '.').replace(".class", "");
    }
}
