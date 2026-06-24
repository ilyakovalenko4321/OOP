import processing.XmlProcessingPlugin;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class MarkCanvasXsltPlugin implements XmlProcessingPlugin {
    @Override
    public String getName() {
        return "XSLT add lab mark";
    }

    @Override
    public String beforeSave(String xml) throws Exception {
        return transform(xml, "lab5-xslt");
    }

    @Override
    public String afterLoad(String xml) throws Exception {
        return xml;
    }

    // Uses XSLT to copy the document and add an attribute to the root canvas element.
    private String transform(String xml, String mark) throws Exception {
        String xslt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                + "<xsl:output method=\"xml\" indent=\"yes\"/>"
                + "<xsl:template match=\"canvas\">"
                + "<canvas processed=\"" + mark + "\"><xsl:apply-templates select=\"@*[name()!='processed']|node()\"/></canvas>"
                + "</xsl:template>"
                + "<xsl:template match=\"@*|node()\">"
                + "<xsl:copy><xsl:apply-templates select=\"@*|node()\"/></xsl:copy>"
                + "</xsl:template></xsl:stylesheet>";
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(new StringReader(xslt)));
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }
}
