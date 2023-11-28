package cl.kanopus.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 * 
 */
public class XMLUtils {

    private XMLUtils(){}
    
    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";

    public static void validateXMLSchema(File xsd, File xml) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
    }

    public static String removeElement(String tag, String xml) {
        return xml.replaceAll("<" + tag + ".*?>.*?</" + tag + ">|<" + tag + "/>", "");
    }

    public static List<String> getElements(String tag, String xml) {
        List<String> values = new ArrayList<>();
        if (xml != null && tag != null) {
            boolean review = true;
            int index = 0;
            while (review) {
                int beginIndex = xml.indexOf("<" + tag, index);
                int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

                if (beginIndex != -1 && endIndex != -1) {
                    values.add(xml.substring(beginIndex, endIndex + tag.length() + 3));
                    index = endIndex;
                } else {
                    review = false;
                }
            }
        }
        return values;
    }

    public static String getElement(String tag, String xml) {
        String value = null;
        if (xml != null && tag != null) {
            int beginIndex = xml.indexOf("<" + tag);
            int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

            if (beginIndex != -1 && endIndex != -1) {
                value = xml.substring(beginIndex, endIndex + tag.length() + 3);
            }
        }
        return value;
    }

    public static boolean hasElement(String tag, String xml) {
        if (xml != null && tag != null) {
            int beginIndex = xml.indexOf("<" + tag);
            int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

            if (beginIndex != -1 && endIndex != -1) {
                return true;
            }
        }
        return false;
    }

    public static String getTagValue(String tag, String xml) {
        String value = null;
        if (xml != null && tag != null) {
            int beginIndex = xml.indexOf("<" + tag + ">");
            int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

            if (beginIndex != -1 && endIndex != -1) {
                value = xml.substring(beginIndex + tag.length() + 2, endIndex);
            }
        }
        return value;
    }

    public static String getElementValue(String start, String end, String xml) {
        String value = null;
        int beginIndex = xml.indexOf(start);
        int endIndex = xml.indexOf(end, beginIndex);
        if (beginIndex != -1 && endIndex != -1) {
            value = xml.substring(beginIndex + start.length(), endIndex);
        }
        return value;
    }

    public static StringWriter parse(Document doc, boolean includeXmlDeclaration) throws TransformerException {
        
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sw = new StringWriter();
        if (includeXmlDeclaration) {
            sw.append(XMLUtils.XML_DECLARATION);
        }

        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw;
    }

}
