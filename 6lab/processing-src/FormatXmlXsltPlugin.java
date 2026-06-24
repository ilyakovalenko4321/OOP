import processing.XmlProcessingPlugin;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class FormatXmlXsltPlugin implements XmlProcessingPlugin {
    @Override
    public String getName() {
        return "XSLT format XML";
    }

    @Override
    public String beforeSave(String xml) throws Exception {
        return transform(xml);
    }

    @Override
    public String afterLoad(String xml) throws Exception {
        return transform(xml);
    }

    // Applies an identity XSLT and enables indentation for readable XML files.
    private String transform(String xml) throws Exception {
        String xslt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                + "<xsl:output method=\"xml\" indent=\"yes\"/>"
                + "<xsl:strip-space elements=\"*\"/>"
                + "<xsl:template match=\"@*|node()\">"
                + "<xsl:copy><xsl:apply-templates select=\"@*|node()\"/></xsl:copy>"
                + "</xsl:template></xsl:stylesheet>";
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(new StringReader(xslt)));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }
}
