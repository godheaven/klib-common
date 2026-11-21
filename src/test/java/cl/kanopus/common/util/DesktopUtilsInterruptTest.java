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

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class DesktopUtilsInterruptTest {

    @Test
    void unixCommandExistsPreservesInterruptFlag() throws Exception {
        Method m = DesktopUtils.class.getDeclaredMethod("unixCommandExists", String.class);
        m.setAccessible(true);
        try {
            Thread.currentThread().interrupt();
            try {
                m.invoke(null, "sh");
            } catch (java.lang.reflect.InvocationTargetException e) {
                // "which" may be unavailable in the environment: only the interrupt flag matters here
            }
            assertTrue(Thread.currentThread().isInterrupted(), "interrupt flag must be preserved");
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void appleFileManagerIsNoLongerUsed() {
        for (Method m : DesktopUtils.class.getDeclaredMethods()) {
            assertNotEquals("getAppleFileManagerClass", m.getName());
            assertNotEquals("browseMac", m.getName());
        }
    }
}
