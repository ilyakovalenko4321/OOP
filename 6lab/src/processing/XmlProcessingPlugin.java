package processing;

public interface XmlProcessingPlugin {
    // Returns the menu label for this processing plugin.
    String getName();

    // Transforms XML text before it is written to a file.
    String beforeSave(String xml) throws Exception;

    // Transforms XML text after it is read from a file.
    String afterLoad(String xml) throws Exception;
}
