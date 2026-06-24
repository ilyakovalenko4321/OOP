package processing;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ProcessingPluginLoader {
    private final File pluginDirectory;

    public ProcessingPluginLoader(String directoryName) {
        this.pluginDirectory = new File(directoryName);
    }

    // Loads compiled processing plugins from the configured directory.
    public List<XmlProcessingPlugin> loadPlugins() {
        List<XmlProcessingPlugin> plugins = new ArrayList<>();
        if (!pluginDirectory.exists() || !pluginDirectory.isDirectory()) {
            return plugins;
        }

        List<File> classFiles = new ArrayList<>();
        collectClassFiles(pluginDirectory, classFiles);
        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{pluginDirectory.toURI().toURL()},
                    getClass().getClassLoader()
            );
            for (File classFile : classFiles) {
                Class<?> pluginClass = classLoader.loadClass(toClassName(classFile));
                if (XmlProcessingPlugin.class.isAssignableFrom(pluginClass)) {
                    plugins.add((XmlProcessingPlugin) pluginClass.getDeclaredConstructor().newInstance());
                }
            }
        } catch (Exception ex) {
            System.out.println("Processing plugin loading error: " + ex.getMessage());
        }
        return plugins;
    }

    // Recursively collects .class files so package folders are supported.
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

    private String toClassName(File classFile) {
        String root = pluginDirectory.getAbsolutePath() + File.separator;
        String relativePath = classFile.getAbsolutePath().substring(root.length());
        return relativePath.replace(File.separatorChar, '.').replace(".class", "");
    }
}
