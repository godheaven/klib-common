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
package cl.kanopus.common.util.format;

import org.junit.jupiter.api.Test;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import static org.junit.jupiter.api.Assertions.*;

class PaddingDecimalFormatTest {

    @Test
    void formatWithPadding() {
        DecimalFormatSymbols s = DecimalFormatSymbols.getInstance();
        DecimalFormat expectedFmt = new DecimalFormat("0.00", s);
        String expected = expectedFmt.format(12.3);

        PaddingDecimalFormat f = new PaddingDecimalFormat("0.00", s);
        String out = f.format(12.3, 6);

        // result must end with the expected formatted number and be at least as long
        assertTrue(out.endsWith(expected));
        assertTrue(out.length() >= expected.length());
    }
}
