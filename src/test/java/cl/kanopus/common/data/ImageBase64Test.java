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
package cl.kanopus.common.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageBase64Test {

    @Test
    void roundtripData() {
        byte[] data = new byte[]{1, 2, 3, 4};
        ImageBase64 ib = new ImageBase64();
        ib.setData(java.util.Base64.getEncoder().encodeToString(data));
        assertNotNull(ib.getData());
        assertArrayEquals(data, java.util.Base64.getDecoder().decode(ib.getData()));
    }

    @Test
    void emptyByDefault() {
        ImageBase64 ib = new ImageBase64();
        assertNull(ib.getData());
    }

}
