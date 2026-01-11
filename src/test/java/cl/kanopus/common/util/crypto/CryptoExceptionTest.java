/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 * 
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 - 2026 Pablo Díaz Saavedra
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
package cl.kanopus.common.util.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CryptoExceptionTest {

    @Test
    void constructors_work() {
        CryptoException e1 = new CryptoException("msg");
        Assertions.assertEquals("msg", e1.getMessage());

        Throwable cause = new RuntimeException("cause");
        CryptoException e2 = new CryptoException("m2", cause);
        Assertions.assertEquals("m2", e2.getMessage());
        Assertions.assertEquals(cause, e2.getCause());

        CryptoException e3 = new CryptoException(cause);
        Assertions.assertEquals(cause, e3.getCause());
    }
}

