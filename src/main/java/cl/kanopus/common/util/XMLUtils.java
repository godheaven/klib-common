/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class XMLUtils {

    private XMLUtils() {
    }

    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";

    /**
     * Validates an XML file against an XSD schema.
     * <p>
     * The validation is hardened against XXE: secure processing is enabled, the XML document cannot load external DTDs/entities or schemas, and the (trusted) XSD may only include/import other schemas from the local file system or the classpath.
     *
     * @param xsd
     *            schema file (trusted)
     * @param xml
     *            XML file to validate (untrusted)
     * @throws SAXException
     *             if the document is invalid or tries to resolve an external resource
     * @throws IOException
     *             if a file cannot be read
     */
    public static void validateXMLSchema(File xsd, File xml) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        // the XSD is trusted, allow local xs:include/xs:import but never remote ones
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file,jar:file");
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        validator.validate(new StreamSource(xml));
    }

    /**
     * Creates a {@link DocumentBuilderFactory} hardened against XXE (no DOCTYPE, no external entities/DTDs/schemas, secure processing).
     *
     * @return a namespace-aware, secure {@link DocumentBuilderFactory}
     * @throws ParserConfigurationException
     *             if the underlying implementation does not support the secure features
     */
    public static DocumentBuilderFactory newSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return dbf;
    }

    /**
     * Removes every occurrence of the element {@code tag} (with its content, or self-closed) from the given XML text. Elements whose name only starts with {@code tag} (e.g. {@code <tagX>}) are not removed.
     *
     * @param tag
     *            element name (taken literally, not as a regex)
     * @param xml
     *            XML text
     * @return the XML text without the element
     */
    public static String removeElement(String tag, String xml) {
        if (xml == null || tag == null || tag.isEmpty()) {
            return xml;
        }
        String q = Pattern.quote(tag);
        Pattern pattern = Pattern.compile("<" + q + "(?:\\s[^>]*)?/>|<" + q + "(?:\\s[^>]*)?(?<!/)>.*?</" + q + "\\s*>", Pattern.DOTALL);
        return pattern.matcher(xml).replaceAll("");
    }

    /**
     * Finds the start of the opening tag {@code <tag} followed by {@code >}, {@code /} or whitespace, starting at {@code from}.
     */
    private static int indexOfStartTag(String xml, String tag, int from) {
        String open = "<" + tag;
        int index = from;
        while ((index = xml.indexOf(open, index)) != -1) {
            int next = index + open.length();
            if (next < xml.length()) {
                char c = xml.charAt(next);
                if (c == '>' || c == '/' || Character.isWhitespace(c)) {
                    return index;
                }
            }
            index = next;
        }
        return -1;
    }

    public static List<String> getElements(String tag, String xml) {
        List<String> values = new ArrayList<>();
        if (xml != null && tag != null) {
            boolean review = true;
            int index = 0;
            while (review) {
                int beginIndex = indexOfStartTag(xml, tag, index);
                int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

                if (beginIndex != -1 && endIndex != -1) {
                    values.add(xml.substring(beginIndex, endIndex + tag.length() + 3));
                    index = endIndex + tag.length() + 3;
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
            int beginIndex = indexOfStartTag(xml, tag, 0);
            int endIndex = xml.indexOf("</" + tag + ">", beginIndex);

            if (beginIndex != -1 && endIndex != -1) {
                value = xml.substring(beginIndex, endIndex + tag.length() + 3);
            }
        }
        return value;
    }

    public static boolean hasElement(String tag, String xml) {
        if (xml != null && tag != null) {
            int beginIndex = indexOfStartTag(xml, tag, 0);
            return beginIndex != -1 && xml.indexOf("</" + tag + ">", beginIndex) != -1;
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

        Transformer t = newSecureTransformerFactory().newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sw = new StringWriter();
        if (includeXmlDeclaration) {
            sw.append(XMLUtils.XML_DECLARATION);
        }

        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw;
    }

    private static TransformerFactory newSecureTransformerFactory() throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (javax.xml.transform.TransformerConfigurationException ex) {
            throw new TransformerException("Unable to enable secure processing", ex);
        }
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return factory;
    }
}
