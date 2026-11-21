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

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class XMLUtilsSecurityTest {

    private static final String XSD = """
            <?xml version="1.0"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
              <xs:element name="root" type="xs:string"/>
            </xs:schema>
            """;

    @TempDir
    Path tmp;

    @Test
    void validateXMLSchemaAcceptsValidDocument() throws Exception {
        File xsd = write("schema.xsd", XSD);
        File xml = write("ok.xml", "<root>hello</root>");
        assertDoesNotThrow(() -> XMLUtils.validateXMLSchema(xsd, xml));
    }

    @Test
    void validateXMLSchemaRejectsExternalEntity() throws Exception {
        File secret = write("secret.txt", "TOP-SECRET");
        File xsd = write("schema.xsd", XSD);
        File xml = write("xxe.xml", "<?xml version=\"1.0\"?>\n<!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secret.toURI() + "\">]>\n<root>&xxe;</root>");
        SAXException ex = assertThrows(SAXException.class, () -> XMLUtils.validateXMLSchema(xsd, xml));
        assertFalse(ex.getMessage().contains("TOP-SECRET"));
    }

    @Test
    void validateXMLSchemaRejectsExternalDtd() throws Exception {
        File dtd = write("ext.dtd", "<!ENTITY x \"y\">");
        File xsd = write("schema.xsd", XSD);
        File xml = write("dtd.xml", "<?xml version=\"1.0\"?>\n<!DOCTYPE root SYSTEM \"" + dtd.toURI() + "\">\n<root>&x;</root>");
        assertThrows(SAXException.class, () -> XMLUtils.validateXMLSchema(xsd, xml));
    }

    @Test
    void secureDocumentBuilderRejectsDoctype() throws Exception {
        File secret = write("secret.txt", "TOP-SECRET");
        DocumentBuilder db = XMLUtils.newSecureDocumentBuilderFactory().newDocumentBuilder();
        String xml = "<?xml version=\"1.0\"?><!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secret.toURI() + "\">]><root>&xxe;</root>";
        assertThrows(SAXException.class, () -> db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));

        Document doc = db.parse(new ByteArrayInputStream("<root><a>1</a></root>".getBytes(StandardCharsets.UTF_8)));
        assertEquals("root", doc.getDocumentElement().getNodeName());
        assertTrue(XMLUtils.parse(doc, false).toString().contains("<a>1</a>"));
    }

    @Test
    void removeElementQuotesTagAndHandlesMultiline() {
        String xml = "<root><a x=\"1\">\nline1\nline2</a><ab>keep</ab><a/><a.b>keep2</a.b><a y='2'/></root>";
        assertEquals("<root><ab>keep</ab><a.b>keep2</a.b></root>", XMLUtils.removeElement("a", xml));
        // regex metacharacters are literal
        assertEquals("<root><ab>keep</ab></root>", XMLUtils.removeElement("a.b", "<root><a.b>x</a.b><ab>keep</ab></root>"));
        assertEquals("<axb>k</axb>", XMLUtils.removeElement("a.b", "<axb>k</axb>"));
        assertEquals("<r>x</r>", XMLUtils.removeElement(".*", "<r>x</r>"));
        assertNull(XMLUtils.removeElement("a", null));
        assertEquals("<r/>", XMLUtils.removeElement(null, "<r/>"));
    }

    @Test
    void getElementDoesNotMatchTagPrefix() {
        String xml = "<root><ab>no</ab><a attr=\"1\">yes</a><a>two</a></root>";
        assertEquals("<a attr=\"1\">yes</a>", XMLUtils.getElement("a", xml));
        List<String> elements = XMLUtils.getElements("a", xml);
        assertEquals(List.of("<a attr=\"1\">yes</a>", "<a>two</a>"), elements);
        assertFalse(XMLUtils.hasElement("a", "<root><ab>no</ab></root>"));
        assertNull(XMLUtils.getElement("a", "<root><ab>no</ab></root>"));
        assertTrue(XMLUtils.getElements("a", null).isEmpty());
        assertEquals("v", XMLUtils.getElementValue("<x>", "</x>", "<x>v</x>"));
    }

    private File write(String name, String content) throws Exception {
        Path p = tmp.resolve(name);
        Files.writeString(p, content, StandardCharsets.UTF_8);
        return p.toFile();
    }
}
