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
package cl.kanopus.common.annotation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

class ToUpperCaseAnnotationTest {

    @Test
    void annotationHasConverters() {
        JsonSerialize js = ToUpperCase.class.getAnnotation(JsonSerialize.class);
        JsonDeserialize jd = ToUpperCase.class.getAnnotation(JsonDeserialize.class);
        assertNotNull(js, "@JsonSerialize should be present on ToUpperCase");
        assertNotNull(jd, "@JsonDeserialize should be present on ToUpperCase");
        assertEquals(cl.kanopus.common.annotation.impl.ToUpperCaseConverter.class, js.converter());
        assertEquals(cl.kanopus.common.annotation.impl.ToUpperCaseConverter.class, jd.converter());
    }
}
