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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertUtilsTest {

    @Test
    void assertNotNullAndEquals() {
        AssertUtils.assertNotNull("x");
        AssertUtils.assertEquals("a", "a");
    }

    @Test
    void assertNotNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.assertNotNull(null));
    }

    @Test
    void assertNotEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.assertNotEmpty(Collections.emptyList()));
    }

    @Test
    void objectsAreEqualAndNotEqual() {
        Assertions.assertTrue(AssertUtils.objectsAreEqual("a", "a"));
        Assertions.assertFalse(AssertUtils.objectsAreEqual("a", "b"));
    }

    @Test
    void assertNotEqualsThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AssertUtils.assertNotEquals("a", "a"));
    }
}
