import processing.XmlProcessingPlugin;

public class FriendUppercaseToolAdapter implements XmlProcessingPlugin {
    private final FriendUppercaseTool friendTool = new FriendUppercaseTool();

    @Override
    public String getName() {
        return "Adapter: friend uppercase comments";
    }

    @Override
    public String beforeSave(String xml) {
        return friendTool.packForFriendApp(xml);
    }

    @Override
    public String afterLoad(String xml) {
        return friendTool.unpackFromFriendApp(xml);
    }

    private static class FriendUppercaseTool {
        // Simulates a class received from another project with an incompatible API.
        public String packForFriendApp(String sourceXml) {
            if (sourceXml.contains("friendPlugin=\"uppercase-adapter\"")) {
                return sourceXml;
            }
            return sourceXml.replace("<canvas", "<canvas friendPlugin=\"uppercase-adapter\"");
        }

        // The friend's plugin uses different method names, so the adapter maps them to XmlProcessingPlugin.
        public String unpackFromFriendApp(String sourceXml) {
            return sourceXml.replace(" friendPlugin=\"uppercase-adapter\"", "");
        }
    }
}
