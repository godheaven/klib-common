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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberToLetterConverterTest {

    @Test
    void testPrettyFileSize() {


        Assertions.assertEquals("MIL PESOS", NumberToLetterConverter.convertNumberToLetter((double) 1000));
        Assertions.assertEquals("CUARENTA Y CINCO MIL PESOS", NumberToLetterConverter.convertNumberToLetter((double) 45000));
        Assertions.assertEquals("TRESCIENTOS MIL OCHOCIENTOS NOVENTA Y SIETE PESOS", NumberToLetterConverter.convertNumberToLetter((double) 300897));
        Assertions.assertEquals("UN MILLON QUINIENTOS PESOS", NumberToLetterConverter.convertNumberToLetter((double) 1000500));
        Assertions.assertEquals("CINCUENTA PESOS", NumberToLetterConverter.convertNumberToLetter((double) 50));
    }

}
