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

import java.util.Collection;

public class AssertUtils {

    private AssertUtils() {
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "[Assertion failed] - expected [" + expected + "] must be equals to actual [" + actual + "]");
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!objectsAreEqual(expected, actual)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotEquals(Object expected, Object actual) {
        assertNotEquals(expected, actual, "[Assertion failed] - expected [" + expected + "] must be not equals to actual [" + actual + "]");
    }

    public static void assertNotEquals(Object expected, Object actual, String message) {
        if (objectsAreEqual(expected, actual)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNull(Object actual) {
        assertNull(actual, "[Assertion failed] - this argument is not required; it must be null");
    }

    public static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertTrue(boolean expression) {
        assertTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void assertTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotNull(Object object) {
        assertNotNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertFalse(boolean expression) {
        assertFalse(expression, "[Assertion failed] - this expression must be false");
    }

    public static void assertFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotEmpty(Collection<?> collection) {
        assertNotEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void assertNotEmpty(Collection<?> collection, String message) {
        if ((collection == null || collection.isEmpty())) {
            throw new IllegalArgumentException(message);
        }
    }

    static boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return (obj2 == null);
        }
        return obj1.equals(obj2);
    }

}
