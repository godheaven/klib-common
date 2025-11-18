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

import org.junit.jupiter.api.Test;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.*;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

class XMLUtilsTest {

    @Test
    void removeAndGetElement() {
        String xml = "<root><a>1</a><b>two</b><a>3</a></root>";
        assertEquals("<a>1</a><a>3</a>", String.join("", XMLUtils.getElements("a", xml)));
        assertEquals("<b>two</b>", XMLUtils.getElement("b", xml));
        assertTrue(XMLUtils.hasElement("a", xml));
        assertEquals("1", XMLUtils.getTagValue("a", "<root><a>1</a></root>"));
    }

    @Test
    void parseDocument() throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        Document doc = f.newDocumentBuilder().newDocument();
        doc.appendChild(doc.createElement("root"));
        StringWriter sw = XMLUtils.parse(doc, true);
        assertTrue(sw.toString().contains("<?xml") || sw.toString().contains("root"));
    }
}
